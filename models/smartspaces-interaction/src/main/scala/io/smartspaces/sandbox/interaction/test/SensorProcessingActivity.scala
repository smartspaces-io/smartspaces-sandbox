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

import org.reactivestreams.Subscriber

import io.smartspaces.activity.behavior.web.StandardActivityWebServer
import io.smartspaces.activity.impl.BaseActivity
import io.smartspaces.event.observable.EventPublisherSubject
import io.smartspaces.logging.ExtendedLog
import io.smartspaces.resource.managed.ManagedResources
import io.smartspaces.sandbox.interaction.behavior.speech.SequentialSpeechSpeaker
import io.smartspaces.sandbox.interaction.entity.model.reactive.ObserverSpeechSpeaker
import io.smartspaces.sandbox.interaction.entity.model.reactive.ObserverWebSocketNotifier
import io.smartspaces.sandbox.sensor.entity.YamlSensorDescriptionImporter
import io.smartspaces.sandbox.sensor.entity.model.event.PhysicalLocationOccupancyEvent
import io.smartspaces.sandbox.sensor.entity.model.event.SensorOfflineEvent
import io.smartspaces.sandbox.sensor.integrator.SensorIntegrator
import io.smartspaces.sandbox.sensor.integrator.StandardSensorIntegrator
import io.smartspaces.sandbox.sensor.value.converter.PhysicalLocationWebConverter
import io.smartspaces.service.event.observable.EventObservableService
import io.smartspaces.service.speech.synthesis.SpeechSynthesisService
import io.smartspaces.tasks.ManagedTasks
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.smartspaces.scope.ManagedScope
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription

/**
 * An activity to merge sensors across the entire space.
 *
 * @author Keith M. Hughes
 */
class SensorProcessingActivity() extends BaseActivity with StandardActivityWebServer {

  private var sensorIntegrator: SensorIntegrator = null

  override def onActivityStartup(): Unit = {

    val spaceEnvironment = getSpaceEnvironment
    val managedTasks: ManagedTasks = getManagedTasks
    val managedResources: ManagedResources = getManagedResources
    val managedScope: ManagedScope = getActivityManagedScope

    val log = getSpaceEnvironment.getLog

    val speechSynthesisService = spaceEnvironment.getServiceRegistry().
      getRequiredService(SpeechSynthesisService.SERVICE_NAME).asInstanceOf[SpeechSynthesisService]
    val speechPlayer = speechSynthesisService.newPlayer(getLog())
    managedResources.addResource(speechPlayer)
    speechPlayer.speak("Hello world", false)

    sensorIntegrator = new StandardSensorIntegrator(getSpaceEnvironment, managedScope, getSpaceEnvironment.getLog)
    sensorIntegrator.descriptionImporter = new YamlSensorDescriptionImporter(getClass().getResourceAsStream("testdescription.yaml"), log)
    sensorIntegrator.startup()
    addManagedResource(sensorIntegrator)

    val mqttHost = getConfiguration.getRequiredPropertyString("smartspaces.comm.mqtt.broker.host")
    val mqttPort = getConfiguration.getRequiredPropertyInteger("smartspaces.comm.mqtt.broker.port")
    val mqttSensorInput = sensorIntegrator.addMqttSensorInput(new MqttBrokerDescription(mqttHost, mqttPort, false), "/home/sensor/agregator2")
    mqttSensorInput.addMqttSubscription("/home/sensor", 2)

    val eventObservableService = spaceEnvironment.getServiceRegistry.
      getRequiredService(EventObservableService.SERVICE_NAME).asInstanceOf[EventObservableService]

    setUpObservables(log)

    //    managedTasks.scheduleAtFixedRate(new Runnable {
    //      override def run {
    //        log.info("Giving sensor report")
    //        val converter = new CelciusToFahrenheitMeasurementValueConverter
    //        sensorIntegrator.queryProcessor.getAllValuesForMeasurementType(StandardSensorData.MEASUREMENT_TYPE_TEMPERATURE).foreach { x =>
    //          val temp: Double = converter.convert(x.value.asInstanceOf[Double])
    //          log.formatInfo("The temperature in %s is %s", x.sensor.sensedEntityModel.get.sensedEntityDescription.displayName, temp.toString())
    //        }
    //      }
    //    }, 10000l, 10000l, TimeUnit.MILLISECONDS)
  }

  private def setUpObservables(log: ExtendedLog): Unit = {

    val eventObservableService = getSpaceEnvironment.getServiceRegistry().
      getRequiredService(EventObservableService.SERVICE_NAME).asInstanceOf[EventObservableService]
    val physicalLocationOccupancyEventObservable: EventPublisherSubject[PhysicalLocationOccupancyEvent] =
      eventObservableService.getObservable(PhysicalLocationOccupancyEvent.EVENT_TYPE)
    if (physicalLocationOccupancyEventObservable != null) {
      val speechSpeaker = new SequentialSpeechSpeaker(getSpaceEnvironment, log)
      getManagedResources.addResource(speechSpeaker)
      physicalLocationOccupancyEventObservable.subscribe(new ObserverSpeechSpeaker(speechSpeaker))
      physicalLocationOccupancyEventObservable.subscribe(new ObserverWebSocketNotifier(this))
    }
    val sensorOfflineEventObservable: EventPublisherSubject[SensorOfflineEvent] =
      eventObservableService.getObservable(SensorOfflineEvent.EVENT_TYPE)
    if (sensorOfflineEventObservable != null) {
      sensorOfflineEventObservable.subscribe(new Observer[SensorOfflineEvent]() {
        override def onComplete(): Unit = {}
        override def onError(e: Throwable): Unit = {}
        override def onNext(event: SensorOfflineEvent): Unit = {
          log.formatWarn("Sensor offline %s", event.sensorModel.sensorEntityDescription)
        }

        override def onSubscribe(d: Disposable): Unit = {
          // Nothing to do
        }
      })
    }
  }

  override def onNewWebSocketConnection(connectionId: String): Unit = {
    getLog.warn("Got web socket connection " + connectionId);
  }

  override def onWebSocketReceive(connectionId: String, data: Object) = {
    val msg = new StandardDynamicObjectNavigator(data)
    if (msg.getString("type") == "request.data") {
      val physicalLocations = sensorIntegrator.queryProcessor.getAllPhysicalLocations(new PhysicalLocationWebConverter)
      sendWebSocketJson(connectionId, physicalLocations.toMap())
    }
  }
}
