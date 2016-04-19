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

import java.io.File;
import java.util.concurrent.CountDownLatch;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.InMemorySensorRegistry;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimplePhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorSensedEntityAssociation;
import io.smartspaces.sandbox.interaction.processing.sensor.MqttSensorInputAggregator;
import io.smartspaces.sandbox.interaction.processing.sensor.SensedEntitySensorListener;
import io.smartspaces.sandbox.interaction.processing.sensor.SensorProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorInput;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntityModelSensorListener;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntitySensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensorProcessor;
import io.smartspaces.service.comm.pubsub.mqtt.paho.PahoMqttCommunicationEndpointService;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.time.LocalTimeProvider;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();
    spaceEnvironment.setTimeProvider(new LocalTimeProvider());

    spaceEnvironment.getServiceRegistry()
        .registerService(new PahoMqttCommunicationEndpointService());

    final ExtendedLog log = spaceEnvironment.getExtendedLog();

    InMemorySensorRegistry sensorRegistry = new InMemorySensorRegistry();

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/home/livingroom/proximity",
        "Raspberry Pi BLE proximity sensor"));
    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107700",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/livingroom", "The living room on the first floor"));
    sensorRegistry.associateSensorWithSensedEntity("/home/livingroom/proximity",
        "/home/livingroom");
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107700",
        "/home/livingroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107716",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/masterbedroom", "The master bedroom, on the second floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107716",
        "/home/masterbedroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107709",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/garagestudio", "The studio in the garage"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107709",
        "/home/garagestudio");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054868",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/basement", "The main room in the basement"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054868", "/home/basement");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054793",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/kitchen", "The kitchen on the first floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054793", "/home/kitchen");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107713",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/musicroom", "The music room on the second floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107713", "/home/musicroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054677",
        "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(
        new SimplePhysicalSpaceSensedEntityDescription("/home/attic", "The attic"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054677", "/home/attic");

    SensorProcessor sensorProcessor = new StandardSensorProcessor(log);

    File sampleFile = new File("/var/tmp/sensordata.json");
    boolean liveData = true;
    boolean sampleRecord = false;

    StandardFilePersistenceSensorInput persistedSensorInput = null;
    if (liveData) {
      sensorProcessor.addSensorInput(
          new MqttSensorInputAggregator(new MqttBrokerDescription("tcp://192.168.188.145:1883"),
              "/home/sensor/agregator2", "/home/sensor", spaceEnvironment, log));

      if (sampleRecord) {
        StandardFilePersistenceSensorHandler persistenceHandler =
            new StandardFilePersistenceSensorHandler(sampleFile);
        sensorProcessor.addSensorHandler(persistenceHandler);
      }
    } else {
      persistedSensorInput = new StandardFilePersistenceSensorInput(sampleFile);
      sensorProcessor.addSensorInput(persistedSensorInput);
    }

    StandardSensedEntitySensorHandler sensorHandler = new StandardSensedEntitySensorHandler(log);
    for (SimpleSensorSensedEntityAssociation association : sensorRegistry.getAssociations()) {
      sensorHandler.associateSensorWithEntity(association.getSensor(),
          association.getSensedEntity());
    }

    sensorHandler.addSensedEntitySensorListener(new SensedEntitySensorListener() {

      @Override
      public void handleSensorData(long timestamp, SensorEntityDescription sensor,
          SensedEntityDescription sensedEntity, DynamicObject data) {
        log.formatInfo("Got data at %d from sensor %s for entity %s: %s", timestamp, sensor,
            sensedEntity, data.asMap());

      }
    });

    StandardSensedEntityModelSensorListener modelUpdater =
        new StandardSensedEntityModelSensorListener();
    sensorHandler.addSensedEntitySensorListener(modelUpdater);

    sensorProcessor.addSensorHandler(sensorHandler);

    spaceEnvironment.addManagedResource(sensorProcessor);

    if (liveData) {
      if (sampleRecord) {
        // Recording
        SmartSpacesUtilities.delay(20000);

        spaceEnvironment.shutdown();
      }
    } else {
      // Playing back
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

      spaceEnvironment.shutdown();
    }
  }
}
