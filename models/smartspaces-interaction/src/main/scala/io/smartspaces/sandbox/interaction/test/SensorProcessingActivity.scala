/*
 * Copyright (C) 2016 Keith M. Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.smartspaces.sandbox.interaction.test;

import java.io.File
import java.util.concurrent.CountDownLatch

import io.smartspaces.event.observable.EventObservable
import io.smartspaces.logging.ExtendedLog
import io.smartspaces.sandbox.interaction.behavior.speech.SequentialSpeechSpeaker
import io.smartspaces.sandbox.interaction.entity.InMemorySensorRegistry
import io.smartspaces.sandbox.interaction.entity.SensorDescriptionImporter
import io.smartspaces.sandbox.interaction.entity.SensorRegistry
import io.smartspaces.sandbox.interaction.entity.YamlSensorDescriptionImporter
import io.smartspaces.sandbox.interaction.entity.model.PhysicalLocationOccupancyEvent
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel
import io.smartspaces.sandbox.interaction.entity.model.SensorEntityModel
import io.smartspaces.sandbox.interaction.entity.model.StandardCompleteSensedEntityModel
import io.smartspaces.sandbox.interaction.entity.model.reactive.SubscriberSpeechSpeaker
import io.smartspaces.sandbox.interaction.processing.sensor.MqttSensorInputAggregator
import io.smartspaces.sandbox.interaction.processing.sensor.SensedEntitySensorHandler
import io.smartspaces.sandbox.interaction.processing.sensor.SensedEntitySensorListener
import io.smartspaces.sandbox.interaction.processing.sensor.SensorProcessor
import io.smartspaces.sandbox.interaction.processing.sensor.SimpleMarkerSensorValueProcessor
import io.smartspaces.sandbox.interaction.processing.sensor.StandardBleProximitySensorValueProcessor
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorHandler
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorInput
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntityModelProcessor
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntitySensorHandler
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensorProcessor
import io.smartspaces.sandbox.interaction.processing.sensor.StandardUnknownSensedEntityHandler
import io.smartspaces.service.event.observable.EventObservableService
import io.smartspaces.service.speech.synthesis.SpeechSynthesisService
import io.smartspaces.system.StandaloneSmartSpacesEnvironment
import io.smartspaces.util.SmartSpacesUtilities
import io.smartspaces.util.data.dynamic.DynamicObject
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription
import io.smartspaces.sandbox.interaction.processing.sensor.ContinuousValueSensorValueProcessor

/**
 * An activity to merge sensors across the entire space.
 *
 * @author Keith M. Hughes
 */
class SensorProcessingActivity(mqttHost: String, mqttPort: Int,
    spaceEnvironment: StandaloneSmartSpacesEnvironment) {

  def run(): Unit = {

    val speechSynthesisService = spaceEnvironment.getServiceRegistry().
      getRequiredService(SpeechSynthesisService.SERVICE_NAME).asInstanceOf[SpeechSynthesisService]
    val speechPlayer = speechSynthesisService.newPlayer(spaceEnvironment.getLog())
    spaceEnvironment.addManagedResource(speechPlayer)
    speechPlayer.speak("Hello world", false)

    val eventObservableService = spaceEnvironment.getServiceRegistry().
      getRequiredService(EventObservableService.SERVICE_NAME).asInstanceOf[EventObservableService]

    val sensorRegistry: SensorRegistry = new InMemorySensorRegistry();

    importDescriptions(sensorRegistry);

    val log = spaceEnvironment.getExtendedLog()
    val sensedEntityModelCollection =
      new StandardCompleteSensedEntityModel(sensorRegistry, eventObservableService, log);
    sensedEntityModelCollection.prepare()

    val sensorProcessor: SensorProcessor = new StandardSensorProcessor(log)

    val sampleFile = new File("/var/tmp/sensordata.json")
    val liveData = true
    val sampleRecord = true

    var persistedSensorInput: StandardFilePersistenceSensorInput = null;
    if (liveData) {
      val mqttUrl = "tcp://" + mqttHost + ":" + mqttPort
      log.formatInfo("MQTT Broker URL %s", mqttUrl)
      sensorProcessor
        .addSensorInput(new MqttSensorInputAggregator(new MqttBrokerDescription(mqttUrl),
          "/home/sensor/agregator2", "/home/sensor", spaceEnvironment, log))

      if (sampleRecord) {
        val persistenceHandler = new StandardFilePersistenceSensorHandler(sampleFile);
        sensorProcessor.addSensorHandler(persistenceHandler);
      }
    } else {
      persistedSensorInput = new StandardFilePersistenceSensorInput(sampleFile);
      sensorProcessor.addSensorInput(persistedSensorInput);
    }

    val unknownSensedEntityHandler = new StandardUnknownSensedEntityHandler();

    val sensorHandler =
      new StandardSensedEntitySensorHandler(sensedEntityModelCollection, unknownSensedEntityHandler, log)
    sensorRegistry.getSensorSensedEntityAssociations.foreach((association) =>
      sensorHandler.associateSensorWithEntity(association.sensor, association.sensedEntity))

    sensorHandler.addSensedEntitySensorListener(new SensedEntitySensorListener() {

      override def handleSensorData(handler: SensedEntitySensorHandler, timestamp: Long,
        sensor: SensorEntityModel, sensedEntity: SensedEntityModel,
        data: DynamicObject): Unit = {
        log.formatInfo("Got data at %s from sensor %s for entity %s: %s", timestamp.toString, sensor,
          sensedEntity, data.asMap())

      }
    });

    val modelProcessor =
      new StandardSensedEntityModelProcessor(sensedEntityModelCollection, log)
    modelProcessor.addSensorValueProcessor(new StandardBleProximitySensorValueProcessor())
    modelProcessor.addSensorValueProcessor(new SimpleMarkerSensorValueProcessor())
    sensorRegistry.getAllMeasurementTypes().filter(_.valueType == "double").foreach {
      measurementType => modelProcessor.addSensorValueProcessor(new ContinuousValueSensorValueProcessor(measurementType))
    }

    sensorHandler.addSensedEntitySensorListener(modelProcessor)

    sensorProcessor.addSensorHandler(sensorHandler)

    spaceEnvironment.addManagedResource(sensorProcessor)

    setUpObservables(eventObservableService, log)

    if (liveData) {
      if (sampleRecord) {
        // Recording
        SmartSpacesUtilities.delay(1000L * 60 * 2 * 10)

        spaceEnvironment.shutdown()
      }
    } else {
      // Playing back
      val latch = new CountDownLatch(1);
      val playableSensorInput = persistedSensorInput;
      spaceEnvironment.getExecutorService().submit(new Runnable() {

        override def run(): Unit = {
          playableSensorInput.play()
          latch.countDown()
        }
      })

      latch.await()

      spaceEnvironment.shutdown()
    }
  }

  private def setUpObservables(eventObservableService: EventObservableService,
    log: ExtendedLog): Unit = {
    val eventObservable: EventObservable[PhysicalLocationOccupancyEvent] =
      eventObservableService.getObservable(PhysicalLocationOccupancyEvent.EVENT_NAME);
    if (eventObservable != null) {
      val speechSpeaker = new SequentialSpeechSpeaker(spaceEnvironment, log)
      spaceEnvironment.addManagedResource(speechSpeaker)
      eventObservable.subscribe(new SubscriberSpeechSpeaker(speechSpeaker))
    }
  }

  /**
   * @param sensorRegistry
   */
  private def importDescriptions(sensorRegistry: SensorRegistry): Unit = {
    val descriptionImporter: SensorDescriptionImporter = new YamlSensorDescriptionImporter()

    descriptionImporter.importDescriptions(sensorRegistry,
      getClass().getResourceAsStream("testdescription.yaml"))
  }
}
