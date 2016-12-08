/*
 * Copyright (C) 2016 Keith M. Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not
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

package io.smartspaces.sandbox.interaction.test

import io.smartspaces.activity.Activity
import io.smartspaces.activity.configuration.ActivityConfiguration
import io.smartspaces.comm.network.zeroconf.StandardZeroconfService
import io.smartspaces.comm.network.zeroconf.ZeroconfService
import io.smartspaces.liveactivity.runtime.development.lightweight.StandardLightweightActivityRuntime
import io.smartspaces.sandbox.sensor.entity.InMemorySensorRegistry
import io.smartspaces.sandbox.sensor.entity.YamlSensorDescriptionImporter
import io.smartspaces.sandbox.sensor.test.StandardTestClient
import io.smartspaces.service.comm.pubsub.mqtt.paho.PahoMqttCommunicationEndpointService
import io.smartspaces.service.event.observable.StandardEventObservableService
import io.smartspaces.service.speech.synthesis.internal.freetts.FreeTtsSpeechSynthesisService
import io.smartspaces.service.web.server.internal.netty.NettyWebServerService
import io.smartspaces.system.StandaloneSmartSpacesEnvironment
import io.smartspaces.time.provider.LocalTimeProvider
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription

object TestDriver {

  var activity: Activity = null

  var testClient: StandardTestClient = null
  
  var spaceEnvironment: StandaloneSmartSpacesEnvironment = null;

  def main(args: Array[String]): Unit = {
createSpaceEnvironment()

// justYaml()
    //runEverythingWithmDns()
    runEverythingLocal()
  }

  def justYaml(): Unit = {
    val sensorRegistry = new InMemorySensorRegistry()
    val descriptionImporter = new YamlSensorDescriptionImporter(getClass().getResourceAsStream("testdescription.yaml"), spaceEnvironment.getLog)

    descriptionImporter.importDescriptions(sensorRegistry)
  }

  def runEverythingLocal(): Unit = {
    runActivity("127.0.0.1", 1883)

  }

  def runEverythingWithmDns(): Unit = {
    val mdnsService: ZeroconfService = spaceEnvironment.getServiceRegistry.getRequiredService(ZeroconfService.SERVICE_NAME)
    val mqttServiceName = "_mqtt._tcp.local."
    mdnsService.addSimpleDiscovery(mqttServiceName, (hostName, hostPort) => {
      println(hostName)
      println(hostPort)
      runActivity(hostName, hostPort)
    })
  }

  def createSpaceEnvironment(): StandaloneSmartSpacesEnvironment = {
    spaceEnvironment =
      StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment()
    spaceEnvironment.setTimeProvider(new LocalTimeProvider())

    val pahoMqttCommunicationEndpointService =
      new PahoMqttCommunicationEndpointService()
    spaceEnvironment.addManagedResource(pahoMqttCommunicationEndpointService)
    spaceEnvironment.getServiceRegistry().registerService(pahoMqttCommunicationEndpointService)
    val freeTtsSpeechSynthesisService =
      new FreeTtsSpeechSynthesisService()
    spaceEnvironment.addManagedResource(freeTtsSpeechSynthesisService)
    spaceEnvironment.getServiceRegistry().registerService(freeTtsSpeechSynthesisService)

    val observableService = new StandardEventObservableService()
    spaceEnvironment.addManagedResource(observableService)
    spaceEnvironment.getServiceRegistry().registerService(observableService)

    val webServerService = new NettyWebServerService()
    webServerService.setSpaceEnvironment(spaceEnvironment)
    spaceEnvironment.addManagedResource(webServerService)
    spaceEnvironment.getServiceRegistry().registerService(webServerService)

    val zeroconfService = new StandardZeroconfService()
    zeroconfService.setSpaceEnvironment(spaceEnvironment)
    spaceEnvironment.addManagedResource(zeroconfService)
    spaceEnvironment.getServiceRegistry().registerService(zeroconfService)

    return spaceEnvironment
  }

  def runActivity(hostname: String, port: Int): Unit = {
    spaceEnvironment.getSystemConfiguration().setProperty("smartspaces.comm.mqtt.broker.host",
      hostname)
    spaceEnvironment.getSystemConfiguration().setProperty("smartspaces.comm.mqtt.broker.port",
      Integer.toString(port))

    var mqttBrokerDescription = new MqttBrokerDescription(hostname, port, false)
    testClient = new StandardTestClient(spaceEnvironment, mqttBrokerDescription, "/home/sensor")
    spaceEnvironment.addManagedResource(testClient)

    new Thread(new Runnable() {

      override def run(): Unit = {
        try {
          activity = new SensorProcessingActivity()

          val runtime =
            new StandardLightweightActivityRuntime(spaceEnvironment)
          runtime.startup()
          runtime.injectActivity(activity)

          val conf = activity.getConfiguration()
          conf.setProperty("space.activity.webapp.web.server.port", "8083")

          // conf.setProperty("space.activity.webapp.content.location",
          // "webapp")
          // conf.setProperty("space.activity.webapp.url.initial",
          // "index.html")
          // conf.setProperty("space.activity.webapp.url.query_string", "foo")
          conf.setProperty(ActivityConfiguration.CONFIGURATION_PROPERTY_ACTIVITY_NAME,
            activity.getName())

          activity.startup()
        } catch {
          // TODO Auto-generated catch block
          case e: Exception => e.printStackTrace()
        }
      }
    }).start()
  }
  
  def shutdown(): Unit = {
    activity.shutdown()
    spaceEnvironment.shutdown
  }
}
