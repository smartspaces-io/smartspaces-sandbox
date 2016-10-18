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

import io.smartspaces.activity.behavior.web.StandardActivityWebServer
import io.smartspaces.activity.impl.BaseActivity
import io.smartspaces.event.observable.EventPublisherSubject
import io.smartspaces.event.observable.EventPublisherSubject
import io.smartspaces.logging.ExtendedLog
import io.smartspaces.sandbox.interaction.behavior.speech.SequentialSpeechSpeaker
import io.smartspaces.sandbox.interaction.entity.SensorDescriptionImporter
import io.smartspaces.sandbox.interaction.entity.SensorRegistry
import io.smartspaces.sandbox.interaction.entity.YamlSensorDescriptionImporter
import io.smartspaces.sandbox.interaction.entity.model.PhysicalLocationOccupancyEvent
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel
import io.smartspaces.sandbox.interaction.entity.model.SensorEntityModel
import io.smartspaces.sandbox.interaction.entity.model.StandardCompleteSensedEntityModel
import io.smartspaces.sandbox.interaction.entity.model.query.StandardSensedEntityModelQueryProcessor
import io.smartspaces.sandbox.interaction.entity.model.reactive.ObserverSpeechSpeaker
import io.smartspaces.sandbox.interaction.entity.model.reactive.ObserverWebSocketNotifier
import io.smartspaces.sandbox.interaction.processing.sensor.ContinuousValueSensorValueProcessor
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
import io.smartspaces.util.data.dynamic.DynamicObject
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription

import java.io.File
import java.util.concurrent.TimeUnit
import io.smartspaces.sandbox.interaction.entity.InMemorySensorRegistry
import io.smartspaces.sandbox.interaction.entity.model.CompleteSensedEntityModel
import io.smartspaces.sandbox.interaction.entity.model.query.SensedEntityModelQueryProcessor
import io.smartspaces.sandbox.interaction.entity.model.updater.value.converter.CelciusToFahrenheitMeasurementValueConverter
import io.smartspaces.sandbox.interaction.entity.model.updater.value.converter.PhysicalLocationWebConverter
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator

/**
 * An activity to merge sensors across the entire space.
 *
 * @author Keith M. Hughes
 */
class SensorProcessingActivity() extends BaseActivity with StandardActivityWebServer {

  private var sensorRegistry: SensorRegistry = null

  private var sensedEntityModelCollection: CompleteSensedEntityModel = null

  private var queryProcessor: SensedEntityModelQueryProcessor = null

  override def onActivityStartup(): Unit = {

    val spaceEnvironment = getSpaceEnvironment

    val speechSynthesisService = spaceEnvironment.getServiceRegistry().
      getRequiredService(SpeechSynthesisService.SERVICE_NAME).asInstanceOf[SpeechSynthesisService]
    val speechPlayer = speechSynthesisService.newPlayer(getLog())
    addManagedResource(speechPlayer)
    speechPlayer.speak("Hello world", false)

    val eventObservableService = spaceEnvironment.getServiceRegistry().
      getRequiredService(EventObservableService.SERVICE_NAME).asInstanceOf[EventObservableService]

    sensorRegistry = new InMemorySensorRegistry()

    importDescriptions(sensorRegistry);

    val log = getSpaceEnvironment.getExtendedLog

    sensedEntityModelCollection =
      new StandardCompleteSensedEntityModel(sensorRegistry, eventObservableService, log)
    sensedEntityModelCollection.prepare()

    queryProcessor = new StandardSensedEntityModelQueryProcessor(sensedEntityModelCollection)

    val sensorProcessor: SensorProcessor = new StandardSensorProcessor(log)

    val sampleFile = new File("/var/tmp/sensordata.json")
    val liveData = true
    val sampleRecord = false

    var persistedSensorInput: StandardFilePersistenceSensorInput = null

    val mqttHost = getConfiguration.getRequiredPropertyString("smartspaces.comm.mqtt.broker.host")
    val mqttPort = getConfiguration.getRequiredPropertyInteger("smartspaces.comm.mqtt.broker.port")
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

    addManagedResource(sensorProcessor)

    setUpObservables(eventObservableService, log)

    getManagedTasks.scheduleAtFixedRate(new Runnable {
      override def run {
        val converter = new CelciusToFahrenheitMeasurementValueConverter
        queryProcessor.getAllValuesForMeasurementType("/sensor/measurement/temperature").foreach { x =>
          val temp: Double = converter.convert(x.value.asInstanceOf[Double])
          log.formatInfo("The temperature in %s is %s", x.sensor.sensedEntityModel.get.sensedEntityDescription.displayName, temp.toString())
        }
      }
    }, 10000l, 10000l, TimeUnit.MILLISECONDS)

    //    if (liveData) {
    //      if (sampleRecord) {
    //        // Recording
    //        SmartSpacesUtilities.delay(1000L * 60 * 2 * 10)
    //        //spaceEnvironment.shutdown()
    //
    //      }
    //    } else {
    //      // Playing back
    //      val latch = new CountDownLatch(1);
    //      val playableSensorInput = persistedSensorInput;
    //      spaceEnvironment.getExecutorService().submit(new Runnable() {
    //
    //        override def run(): Unit = {
    //          playableSensorInput.play()
    //          latch.countDown()
    //        }
    //      })
    //
    //      latch.await()
    //
    //      //spaceEnvironment.shutdown()
    //    }
  }

  override def onNewWebSocketConnection(connectionId: String): Unit = {
    getLog.warn("Got web socket connection " + connectionId);
  }

  override def onWebSocketReceive(connectionId: String, data: Object) = {
    val msg = new StandardDynamicObjectNavigator(data)
    if (msg.getString("type") == "request.data") {
      val physicalLocations = queryProcessor.getAllPhysicalLocations(new PhysicalLocationWebConverter)
      sendWebSocketJson(connectionId, physicalLocations.toMap())
    }
  }

  private def setUpObservables(eventObservableService: EventObservableService,
    log: ExtendedLog): Unit = {
    val spaceEnvironment = getSpaceEnvironment
    val eventObservable: EventPublisherSubject[PhysicalLocationOccupancyEvent] =
      eventObservableService.getObservable(PhysicalLocationOccupancyEvent.EVENT_NAME)
    if (eventObservable != null) {
      val speechSpeaker = new SequentialSpeechSpeaker(spaceEnvironment, log)
      addManagedResource(speechSpeaker)
      eventObservable.subscribe(new ObserverSpeechSpeaker(speechSpeaker))
      eventObservable.subscribe(new ObserverWebSocketNotifier(this))
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
