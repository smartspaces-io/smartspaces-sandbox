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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import io.smartspaces.sandbox.interaction.entity.InMemorySensorRegistry;
import io.smartspaces.sandbox.interaction.entity.SensorDescriptionImporter;
import io.smartspaces.sandbox.interaction.entity.SensorRegistry;
import io.smartspaces.sandbox.interaction.entity.YamlSensorDescriptionImporter;
import io.smartspaces.service.comm.pubsub.mqtt.paho.PahoMqttCommunicationEndpointService;
import io.smartspaces.service.event.observable.StandardEventObservableService;
import io.smartspaces.service.speech.synthesis.internal.freetts.FreeTtsSpeechSynthesisService;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.time.LocalTimeProvider;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    justYaml();
  }
  
  private static void justYaml() {
    SensorRegistry sensorRegistry = new InMemorySensorRegistry();
    SensorDescriptionImporter descriptionImporter = new YamlSensorDescriptionImporter();

    descriptionImporter.importDescriptions(sensorRegistry,
      TestDriver.class.getResourceAsStream("testdescription.yaml"));

  }
  
  private static void runEverything() throws Exception {
    final StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();
    spaceEnvironment.setTimeProvider(new LocalTimeProvider());

    PahoMqttCommunicationEndpointService pahoMqttCommunicationEndpointService =
        new PahoMqttCommunicationEndpointService();
    spaceEnvironment.addManagedResource(pahoMqttCommunicationEndpointService);
    spaceEnvironment.getServiceRegistry().registerService(pahoMqttCommunicationEndpointService);
    FreeTtsSpeechSynthesisService freeTtsSpeechSynthesisService =
        new FreeTtsSpeechSynthesisService();
    spaceEnvironment.addManagedResource(freeTtsSpeechSynthesisService);
    spaceEnvironment.getServiceRegistry().registerService(freeTtsSpeechSynthesisService);

    StandardEventObservableService observableService = new StandardEventObservableService();
    spaceEnvironment.addManagedResource(observableService);
    spaceEnvironment.getServiceRegistry().registerService(observableService);

    String ipAddress = getIpAddress();
    if (ipAddress == null) {
      return;
    }

    final JmDNS jmdns = JmDNS.create(InetAddress.getByName(ipAddress));
    final String mqttServiceName = "_mqtt._tcp.local.";
    jmdns.addServiceListener(mqttServiceName, new ServiceListener() {
      @Override
      public void serviceAdded(ServiceEvent event) {
        String serviceName = event.getName() + "." + event.getType();
        System.out.println("Service added   : " + serviceName);
        ServiceInfo[] services = jmdns.list(mqttServiceName);
        // jmdns.printServices();

        if (services.length > 0) {
          String hostname = services[0].getHostAddresses()[0];
          int port = services[0].getPort();

          final SensorProcessingActivity activity =
              new SensorProcessingActivity(hostname, port, spaceEnvironment);
          new Thread(new Runnable() {

            @Override
            public void run() {
              try {
                activity.run();
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }).start();
          ;

          try {
            jmdns.close();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }

      @Override
      public void serviceRemoved(ServiceEvent event) {
        System.out.println("Service removed : " + event.getName() + "." + event.getType());
      }

      @Override
      public void serviceResolved(ServiceEvent event) {
        System.out.println("Service resolved: " + event.getInfo());
      }
    });

    // SensorProcessingActivity activity = new
    // SensorProcessingActivity(spaceEnvironment);
    // activity.run();
   
  }

  private static String getIpAddress() {
    Set<String> interfacesToIgnore = new HashSet<>();
    interfacesToIgnore.add("docker0");
    interfacesToIgnore.add("wlan0");
    try {
      for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
        System.out.println("Checking " + intf.getName());
        if (!intf.isLoopback() && !interfacesToIgnore.contains(intf.getName())) {
          for (InetAddress address : Collections.list(intf.getInetAddresses())) {
            if (address instanceof Inet4Address) {
              return address.getHostAddress();
            }
          }
        }
      }
    } catch (SocketException e) {
      System.out.println(" (error retrieving network interface list)");
    }

    return null;

  }
}
