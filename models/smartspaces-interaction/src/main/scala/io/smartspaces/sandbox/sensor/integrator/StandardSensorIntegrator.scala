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

package io.smartspaces.sandbox.sensor.integrator

import io.smartspaces.configuration.Configuration
import io.smartspaces.logging.ExtendedLog
import io.smartspaces.resource.managed.IdempotentManagedResource
import io.smartspaces.resource.managed.ManagedResources
import io.smartspaces.sandbox.sensor.entity.InMemorySensorRegistry
import io.smartspaces.sandbox.sensor.entity.SensorDescriptionImporter
import io.smartspaces.sandbox.sensor.entity.SensorRegistry
import io.smartspaces.sandbox.sensor.entity.model.CompleteSensedEntityModel
import io.smartspaces.sandbox.sensor.entity.model.SensedEntityModel
import io.smartspaces.sandbox.sensor.entity.model.SensorEntityModel
import io.smartspaces.sandbox.sensor.entity.model.StandardCompleteSensedEntityModel
import io.smartspaces.sandbox.sensor.entity.model.query.SensedEntityModelQueryProcessor
import io.smartspaces.sandbox.sensor.entity.model.query.StandardSensedEntityModelQueryProcessor
import io.smartspaces.sandbox.sensor.processing.ContinuousValueSensorValueProcessor
import io.smartspaces.sandbox.sensor.processing.MqttSensorInputAggregator
import io.smartspaces.sandbox.sensor.processing.SensedEntitySensorHandler
import io.smartspaces.sandbox.sensor.processing.SensedEntitySensorListener
import io.smartspaces.sandbox.sensor.processing.SensorProcessor
import io.smartspaces.sandbox.sensor.processing.SimpleMarkerSensorValueProcessor
import io.smartspaces.sandbox.sensor.processing.StandardBleProximitySensorValueProcessor
import io.smartspaces.sandbox.sensor.processing.StandardFilePersistenceSensorHandler
import io.smartspaces.sandbox.sensor.processing.StandardFilePersistenceSensorInput
import io.smartspaces.sandbox.sensor.processing.StandardSensedEntityModelProcessor
import io.smartspaces.sandbox.sensor.processing.StandardSensedEntitySensorHandler
import io.smartspaces.sandbox.sensor.processing.StandardSensorProcessor
import io.smartspaces.sandbox.sensor.processing.StandardUnknownSensedEntityHandler
import io.smartspaces.service.event.observable.EventObservableService
import io.smartspaces.service.speech.synthesis.SpeechSynthesisService
import io.smartspaces.system.SmartSpacesEnvironment
import io.smartspaces.tasks.ManagedTasks
import io.smartspaces.util.data.dynamic.DynamicObject
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription

import java.io.File
import io.smartspaces.time.TimeFrequency

/**
 * The sensor integration layer.
 *
 * @author Keith M. Hughes
 */
class StandardSensorIntegrator(private val spaceEnvironment: SmartSpacesEnvironment, private val configuration: Configuration, private val managedTasks: ManagedTasks,
    private val managedResources: ManagedResources, private val log: ExtendedLog) extends SensorIntegrator with IdempotentManagedResource {

  /**
   * The sensor registry for the integrator.
   */
  private var sensorRegistry: SensorRegistry = null

  /**
   * The complete set of models of sensors and sensed entities.
   */
  private var completeSensedEntityModel: CompleteSensedEntityModel = null

  /**
   * The processor for queries against the models.
   */
  private var _queryProcessor: SensedEntityModelQueryProcessor = null

  /**
   * The description importer
   */
  var descriptionImporter: SensorDescriptionImporter = null

  def queryProcessor: SensedEntityModelQueryProcessor = _queryProcessor

  override def onStartup(): Unit = {
    val speechSynthesisService = spaceEnvironment.getServiceRegistry().
      getRequiredService(SpeechSynthesisService.SERVICE_NAME).asInstanceOf[SpeechSynthesisService]
    val speechPlayer = speechSynthesisService.newPlayer(log)
    managedResources.addResource(speechPlayer)
    speechPlayer.speak("Hello world", false)

    val eventObservableService = spaceEnvironment.getServiceRegistry().
      getRequiredService(EventObservableService.SERVICE_NAME).asInstanceOf[EventObservableService]

    sensorRegistry = new InMemorySensorRegistry()

    descriptionImporter.importDescriptions(sensorRegistry)

    completeSensedEntityModel =
      new StandardCompleteSensedEntityModel(sensorRegistry, eventObservableService, log, spaceEnvironment)
    completeSensedEntityModel.prepare()

    _queryProcessor = new StandardSensedEntityModelQueryProcessor(completeSensedEntityModel)

    val sensorProcessor: SensorProcessor = new StandardSensorProcessor(log)

    val sampleFile = new File("/var/tmp/sensordata.json")
    val liveData = true
    val sampleRecord = false

    var persistedSensorInput: StandardFilePersistenceSensorInput = null

    val mqttHost = configuration.getRequiredPropertyString("smartspaces.comm.mqtt.broker.host")
    val mqttPort = configuration.getRequiredPropertyInteger("smartspaces.comm.mqtt.broker.port")
    if (liveData) {
      val mqttBrokerDescription = new MqttBrokerDescription(mqttHost, mqttPort, false)
      log.formatInfo("MQTT Broker URL %s", mqttBrokerDescription.brokerAddress)
      sensorProcessor
        .addSensorInput(new MqttSensorInputAggregator(mqttBrokerDescription,
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
      new StandardSensedEntitySensorHandler(completeSensedEntityModel, unknownSensedEntityHandler, log)
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
      new StandardSensedEntityModelProcessor(completeSensedEntityModel, log)
    modelProcessor.addSensorValueProcessor(new StandardBleProximitySensorValueProcessor())
    modelProcessor.addSensorValueProcessor(new SimpleMarkerSensorValueProcessor())
    sensorRegistry.getAllMeasurementTypes().filter(_.valueType == "double").foreach {
      measurementType => modelProcessor.addSensorValueProcessor(new ContinuousValueSensorValueProcessor(measurementType))
    }

    sensorHandler.addSensedEntitySensorListener(modelProcessor)

    sensorProcessor.addSensorHandler(sensorHandler)

    managedResources.addResource(sensorProcessor)
    
    val sensorCheckupTask = managedTasks.scheduleAtFixedRate(new Runnable() {
      override def run(): Unit = {
        completeSensedEntityModel.checkModels()
      }
    }, TimeFrequency.timesPerHour(60.0), false)
    
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
}