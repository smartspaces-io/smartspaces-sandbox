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
import io.smartspaces.sandbox.event.observable.EventObservable;
import io.smartspaces.sandbox.interaction.behavior.speech.SequentialSpeechSpeaker;
import io.smartspaces.sandbox.interaction.entity.InMemorySensorRegistry;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorDescriptionImporter;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorRegistry;
import io.smartspaces.sandbox.interaction.entity.SimpleMarkerEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimplePersonSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimplePhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorSensedEntityAssociation;
import io.smartspaces.sandbox.interaction.entity.YamlSensorDescriptionImporter;
import io.smartspaces.sandbox.interaction.entity.model.PhysicalLocationOccupancyEvent;
import io.smartspaces.sandbox.interaction.entity.model.StandardSensedEntityModelCollection;
import io.smartspaces.sandbox.interaction.entity.model.reactive.SubscriberSpeechSpeaker;
import io.smartspaces.sandbox.interaction.processing.sensor.MqttSensorInputAggregator;
import io.smartspaces.sandbox.interaction.processing.sensor.SensedEntitySensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.SensedEntitySensorListener;
import io.smartspaces.sandbox.interaction.processing.sensor.SensorProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardBleProximitySensorValueProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardFilePersistenceSensorInput;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntityModelProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensedEntitySensorHandler;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardSensorProcessor;
import io.smartspaces.sandbox.interaction.processing.sensor.StandardUnknownSensedEntityHandler;
import io.smartspaces.service.event.observable.EventObservableService;
import io.smartspaces.service.speech.synthesis.SpeechSynthesisPlayer;
import io.smartspaces.service.speech.synthesis.SpeechSynthesisService;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription;

/**
 * An activity to merge sensors across the entire space.
 * 
 * @author Keith M. Hughes
 */
public class SensorProcessingActivity {

  private StandaloneSmartSpacesEnvironment spaceEnvironment;

  private String mqttHost;

  private int mqttPort;

  public SensorProcessingActivity(String mqttHost, int mqttPort,
      StandaloneSmartSpacesEnvironment spaceEnvironment) {
    this.spaceEnvironment = spaceEnvironment;
    this.mqttHost = mqttHost;
    this.mqttPort = mqttPort;
  }

  public void run() throws Exception {

    SpeechSynthesisService speechSynthesisService = spaceEnvironment.getServiceRegistry()
        .getRequiredService(SpeechSynthesisService.SERVICE_NAME);
    SpeechSynthesisPlayer speechPlayer =
        speechSynthesisService.newPlayer(spaceEnvironment.getLog());
    spaceEnvironment.addManagedResource(speechPlayer);
    speechPlayer.speak("Hello world", false);

    EventObservableService eventObservableService = spaceEnvironment.getServiceRegistry()
        .getRequiredService(EventObservableService.SERVICE_NAME);

    SensorRegistry sensorRegistry = new InMemorySensorRegistry();

    importDescriptions(sensorRegistry);

    final ExtendedLog log = spaceEnvironment.getExtendedLog();
    SensorProcessor sensorProcessor = new StandardSensorProcessor(log);

    File sampleFile = new File("/var/tmp/sensordata.json");
    boolean liveData = true;
    boolean sampleRecord = true;

    StandardFilePersistenceSensorInput persistedSensorInput = null;
    if (liveData) {
      String mqttUrl = "tcp://" + mqttHost + ":" + mqttPort;
      log.formatInfo("MQTT Broker URL %s", mqttUrl);
      sensorProcessor
          .addSensorInput(new MqttSensorInputAggregator(new MqttBrokerDescription(mqttUrl),
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

    StandardUnknownSensedEntityHandler unknownSensedEntityHandler =
        new StandardUnknownSensedEntityHandler();

    StandardSensedEntitySensorHandler sensorHandler =
        new StandardSensedEntitySensorHandler(unknownSensedEntityHandler, log);
    for (SimpleSensorSensedEntityAssociation association : sensorRegistry
        .getSensorSensedEntityAssociations()) {
      sensorHandler.associateSensorWithEntity(association.getSensor(),
          association.getSensedEntity());
    }

    sensorHandler.addSensedEntitySensorListener(new SensedEntitySensorListener() {

      @Override
      public void handleSensorData(SensedEntitySensorHandler handler, long timestamp,
          SensorEntityDescription sensor, SensedEntityDescription sensedEntity,
          DynamicObject data) {
        log.formatInfo("Got data at %d from sensor %s for entity %s: %s", timestamp, sensor,
            sensedEntity, data.asMap());

      }
    });

    final StandardSensedEntityModelCollection sensedEntityModelCollection =
        new StandardSensedEntityModelCollection(sensorRegistry, eventObservableService, log);
    sensedEntityModelCollection.prepare();

    StandardSensedEntityModelProcessor modelProcessor =
        new StandardSensedEntityModelProcessor(sensedEntityModelCollection, log);
    modelProcessor.addSensorValueProcessor(new StandardBleProximitySensorValueProcessor());

    sensorHandler.addSensedEntitySensorListener(modelProcessor);

    sensorProcessor.addSensorHandler(sensorHandler);

    spaceEnvironment.addManagedResource(sensorProcessor);

    setUpObservables(eventObservableService, log);

    if (liveData) {
      if (sampleRecord) {
        // Recording
        SmartSpacesUtilities.delay(1000L * 60 * 2 * 10);

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

  private void setUpObservables(EventObservableService eventObservableService,
      final ExtendedLog log) {
    EventObservable<PhysicalLocationOccupancyEvent> eventObservable =
        eventObservableService.getObservable(PhysicalLocationOccupancyEvent.EVENT_NAME);
    if (eventObservable != null) {
      SequentialSpeechSpeaker speechSpeaker = new SequentialSpeechSpeaker(spaceEnvironment, log);
      spaceEnvironment.addManagedResource(speechSpeaker);
      eventObservable.subscribe(new SubscriberSpeechSpeaker(speechSpeaker));
    }
  }

  /**
   * @param sensorRegistry
   */
  private void importDescriptions(SensorRegistry sensorRegistry) {
    SensorDescriptionImporter descriptionImporter = new YamlSensorDescriptionImporter();

    descriptionImporter.importDescriptions(sensorRegistry,
        getClass().getResourceAsStream("testdescription.yaml"));
  }

  /**
   * @param sensorRegistry
   */
  private void ximportDescriptions(SensorRegistry sensorRegistry) {
    sensorRegistry.registerMarker(new SimpleMarkerEntityDescription("/marker/ble/fc0d12fe7e5c",
        "BLE Beacon", "An Estimote BLE Beacon", "ble:fc0d12fe7e5c"));
    sensorRegistry.registerSensedEntity(new SimplePersonSensedEntityDescription(
        "/person/keith.hughes", "Keith Hughes", "Keith Hughes"));
    sensorRegistry.associateMarkerWithMarkedEntity("/marker/ble/fc0d12fe7e5c",
        "/person/keith.hughes");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/home/livingroom/proximity",
        "Proximity Sensor 1", "Raspberry Pi BLE proximity sensor"));
    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107700",
        "Sensor 9107700", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/livingroom", "Living Room", "The living room on the first floor"));
    sensorRegistry.associateSensorWithSensedEntity("/home/livingroom/proximity",
        "/home/livingroom");
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107700",
        "/home/livingroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107716",
        "Sensor 9107716", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/masterbedroom", "Master Bedroom", "The master bedroom, on the second floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107716",
        "/home/masterbedroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107709",
        "Sensor 9107709", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/garagestudio", "Garage Studio", "The studio in the garage"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107709",
        "/home/garagestudio");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054868",
        "Sensor 9054868", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/basement", "Basement", "The main room in the basement"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054868", "/home/basement");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054793",
        "Sensor 9054793", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/kitchen", "Kitchen", "The kitchen on the first floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054793", "/home/kitchen");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9107713",
        "Sensor 9107713", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
        "/home/musicroom", "Music Room", "The music room on the second floor"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9107713", "/home/musicroom");

    sensorRegistry.registerSensor(new SimpleSensorEntityDescription("/sensornode/nodemcu9054677",
        "Sensor 9054677", "ESP8266-based temperature/humidity sensor"));
    sensorRegistry.registerSensedEntity(
        new SimplePhysicalSpaceSensedEntityDescription("/home/attic", "Attic", "The attic"));
    sensorRegistry.associateSensorWithSensedEntity("/sensornode/nodemcu9054677", "/home/attic");
  }
}
