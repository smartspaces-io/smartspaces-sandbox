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

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.EntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleEntityDescription;
import io.smartspaces.sandbox.interaction.processing.sensor.MqttSensorInputAggregator;
import io.smartspaces.sandbox.interaction.processing.sensor.PhysicalBasedSensorListener;
import io.smartspaces.sandbox.interaction.processing.sensor.SensorProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorInput;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardPhysicalBasedSensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensorProcessor;
import io.smartspaces.service.comm.pubsub.mqtt.paho.PahoMqttCommunicationEndpointService;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.time.LocalTimeProvider;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();
    spaceEnvironment.setTimeProvider(new LocalTimeProvider());

    spaceEnvironment.getServiceRegistry()
        .registerService(new PahoMqttCommunicationEndpointService());

    final ExtendedLog log = spaceEnvironment.getExtendedLog();

    EntityDescription livingRoomEsp8266 = new SimpleEntityDescription("/sensornode/nodemcu13895542",
        "ESP8266-based temperature/humidity sensor");

    EntityDescription livingRoomProximity = new SimpleEntityDescription(
        "/home/livingroom/proximity", "Raspberry Pi BLE proximity sensor");

    EntityDescription livingRoom =
        new SimpleEntityDescription("/home/livingroom", "The living room on the first floor");

    SensorProcessor sensorProcessor = new StandardSensorProcessor(log);

    File sampleFile = new File("/var/tmp/sensordata.json");
    boolean liveData = false;
    boolean samplePlayback = true;
    
    StandardFilePersistenceSensorInput persistedSensorInput = null;
    if (liveData) {
      sensorProcessor.addSensorInput(
          new MqttSensorInputAggregator(new MqttBrokerDescription("tcp://192.168.188.109:1883"),
              "/home/sensor/agregator", "/home/sensor", spaceEnvironment, log));
    } else {
      if (samplePlayback) {
        persistedSensorInput = new StandardFilePersistenceSensorInput(sampleFile);
        sensorProcessor.addSensorInput(persistedSensorInput);
      } else {
        StandardFilePersistenceSensorHandler persistenceHandler =
            new StandardFilePersistenceSensorHandler(sampleFile);
        sensorProcessor.addSensorHandler(persistenceHandler);
      }
    }

    StandardPhysicalBasedSensorHandler sensorHandler = new StandardPhysicalBasedSensorHandler(log);
    sensorHandler.addSensorDescription(livingRoomEsp8266, livingRoom);
    sensorHandler.addSensorDescription(livingRoomProximity, livingRoom);
    sensorHandler.addPhysicalBasedSensorListener(new PhysicalBasedSensorListener() {

      @Override
      public void handleSensorData(long timestamp, EntityDescription sensor,
          EntityDescription physicalLocation, DynamicObject data) {
        log.formatInfo("Got data at %d from sensor %s in location %s: %s", timestamp, sensor,
            physicalLocation, data.asMap());

      }
    });
    sensorProcessor.addSensorHandler(sensorHandler);

    spaceEnvironment.addManagedResource(sensorProcessor);

    if (!liveData) {
      if (samplePlayback) {
        final CountDownLatch latch = new CountDownLatch(1);
        final StandardFilePersistenceSensorInput playableSensorInput = persistedSensorInput;
        spaceEnvironment.getExecutorService().submit(new Runnable() {

          @Override
          public void run() {
            playableSensorInput.play();
            latch.countDown();
          }
        });
        
        latch.await();
      } else {
        // Recording
        SmartSpacesUtilities.delay(10000);
      }

      spaceEnvironment.shutdown();
    }
  }
}
