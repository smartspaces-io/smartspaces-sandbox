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

import io.smartspaces.activity.configuration.ActivityConfiguration
import io.smartspaces.liveactivity.runtime.development.lightweight.StandardLightweightActivityRuntime
import io.smartspaces.sandbox.sensor.entity.InMemorySensorRegistry
import io.smartspaces.sandbox.sensor.entity.YamlSensorDescriptionImporter
import io.smartspaces.service.comm.pubsub.mqtt.paho.PahoMqttCommunicationEndpointService
import io.smartspaces.service.event.observable.StandardEventObservableService
import io.smartspaces.service.speech.synthesis.internal.freetts.FreeTtsSpeechSynthesisService
import io.smartspaces.service.web.server.internal.netty.NettyWebServerService
import io.smartspaces.system.StandaloneSmartSpacesEnvironment
import io.smartspaces.time.provider.LocalTimeProvider

import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

import scala.collection.JavaConversions.enumerationAsScalaIterator

import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

object TestDriver {

  def main(args: Array[String]): Unit = {
    // justYaml()
    runEverythingWithmDns()
    //runEverythingLocal()
  }

  def justYaml(): Unit = {
    val sensorRegistry = new InMemorySensorRegistry()
    val spaceEnvironment = createSpaceEnvironment()
    val descriptionImporter = new YamlSensorDescriptionImporter(getClass().getResourceAsStream("testdescription.yaml"), spaceEnvironment.getExtendedLog)

    descriptionImporter.importDescriptions(sensorRegistry)
  }

  def runEverythingLocal(): Unit = {
    runActivity(createSpaceEnvironment(), "127.0.0.1", 1883)

  }

  def runEverythingWithmDns(): Unit = {
    val spaceEnvironment = createSpaceEnvironment()

    val ipAddress = getIpAddress()
    if (ipAddress == null) {
      return
    }

    val jmdns = JmDNS.create(InetAddress.getByName(ipAddress))
    val mqttServiceName = "_mqtt._tcp.local."
    jmdns.addServiceListener(mqttServiceName, new ServiceListener() {
      override def serviceAdded(event: ServiceEvent): Unit = {
        val serviceName = event.getName() + "." + event.getType()
        System.out.println("Service added   : " + serviceName)
        val services = jmdns.list(mqttServiceName)
        // jmdns.printServices()

        if (services.length > 0) {
          val hostname = services(0).getHostAddresses()(0)
          val port = services(0).getPort
          runActivity(spaceEnvironment, hostname, port)

          try {
            jmdns.close()
          } catch {
            // TODO Auto-generated catch block
            case e: IOException => e.printStackTrace()
          }
        }
      }

      override def serviceRemoved(event: ServiceEvent): Unit = {
        System.out.println("Service removed : " + event.getName() + "." + event.getType())
      }

      override def serviceResolved(event: ServiceEvent): Unit = {
        System.out.println("Service resolved: " + event.getInfo())
      }
    })
  }

  def createSpaceEnvironment(): StandaloneSmartSpacesEnvironment = {
    val spaceEnvironment =
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

    return spaceEnvironment
  }

  def runActivity(spaceEnvironment: StandaloneSmartSpacesEnvironment,
    hostname: String, port: Integer): Unit = {
    spaceEnvironment.getSystemConfiguration().setProperty("smartspaces.comm.mqtt.broker.host",
      hostname)
    spaceEnvironment.getSystemConfiguration().setProperty("smartspaces.comm.mqtt.broker.port",
      Integer.toString(port))
    new Thread(new Runnable() {

      override def run(): Unit = {
        try {
          val activity = new SensorProcessingActivity()

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

  def getIpAddress(): String = {
    val interfacesToIgnore = Set("docker0", "wlan0")
    try {
      val foo =
        NetworkInterface.getNetworkInterfaces().foreach { (intf) =>
          System.out.println("Checking " + intf.getName())
          if (!intf.isLoopback() && !interfacesToIgnore.contains(intf.getName())) {
            intf.getInetAddresses().foreach { (address) =>
              if (address.isInstanceOf[Inet4Address]) {
                return address.getHostAddress()
              }
            }
          }
        }
    } catch {
      case e: SocketException => System.out.println(" (error retrieving network interface list)")
    }

    return null
  }
}
