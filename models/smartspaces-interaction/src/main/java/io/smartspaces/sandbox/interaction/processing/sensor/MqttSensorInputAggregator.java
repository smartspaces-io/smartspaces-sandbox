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

package io.smartspaces.sandbox.interaction.processing.sensor;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.service.comm.pubsub.mqtt.MqttCommunicationEndpoint;
import io.smartspaces.service.comm.pubsub.mqtt.MqttCommunicationEndpointService;
import io.smartspaces.service.comm.pubsub.mqtt.MqttSubscriberListener;
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.messaging.mqtt.MqttBrokerDescription;

/**
 * A sensor input aggregator for sensor message over MQTT.
 * 
 * @author Keith M. Hughes
 */
public class MqttSensorInputAggregator implements SensorInput {

  /**
   * The description of the MQTT broker to attach to.
   */
  private MqttBrokerDescription mqttBrokerDescription;
  /**
   * The client ID to use for this MQTT client.
   */
  private String mqttClientId;

  /**
   * The topic name for the sensor data.
   */
  private String mqttSensorTopicName;

  /**
   * The space environment being run under.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  /**
   * The logger.
   */
  private ExtendedLog log;

  /**
   * The sensor processor the sensor input is running under.
   */
  private SensorProcessor sensorProcessor;

  /**
   * The MQTT client endpoint.
   */
  private MqttCommunicationEndpoint mqttEndpoint;

  /**
   * The codec for translating messages.
   */
  private DynamicObjectByteArrayCodec codec = new DynamicObjectByteArrayCodec();

  /**
   * Construct a new aggregator.
   * 
   * @param mqttBrokerDescription
   *          the description of the MQTT broker to connect to
   * @param mqttClientId
   *          the client ID for this handler
   * @param mqttSensorTopicName
   *          the topic name the sensor data is coming in on
   * @param spaceEnvironment
   *          the space environment to run under
   * @param log
   *          the logger to use
   */
  public MqttSensorInputAggregator(MqttBrokerDescription mqttBrokerDescription, String mqttClientId,
      String mqttSensorTopicName, SmartSpacesEnvironment spaceEnvironment, ExtendedLog log) {
    this.mqttBrokerDescription = mqttBrokerDescription;
    this.mqttClientId = mqttClientId;
    this.mqttSensorTopicName = mqttSensorTopicName;
    this.spaceEnvironment = spaceEnvironment;
    this.log = log;
  }

  @Override
  public void startup() {
    MqttCommunicationEndpointService service = spaceEnvironment.getServiceRegistry()
        .getRequiredService(MqttCommunicationEndpointService.SERVICE_NAME);
    mqttEndpoint = service.newMqttCommunicationEndpoint(mqttBrokerDescription, mqttClientId, log);
    mqttEndpoint.startup();

    SmartSpacesUtilities.delay(2000);

    mqttEndpoint.addSubscriberListener(new MqttSubscriberListener() {
      @Override
      public void handleMessage(MqttCommunicationEndpoint endpoint, String topicName,
          byte[] payload) {
        handleSensorMessage(topicName, payload);
      }
    });
    mqttEndpoint.subscribe(mqttSensorTopicName);
  }

  @Override
  public void shutdown() {
    if (mqttEndpoint != null) {
      mqttEndpoint.shutdown();
    }
  }

  @Override
  public void setSensorProcessor(SensorProcessor sensorProcessor) {
    this.sensorProcessor = sensorProcessor;
  }

  /**
   * Handle an incoming sensor message.
   * 
   * @param topicName
   *          the name of the topic the message cam in on
   * @param payload
   *          the message payload
   */
  private void handleSensorMessage(String topicName, byte[] payload) {
    DynamicObject message = codec.decode(payload);
    log.info(String.format("Got message on topic %s: %s", topicName, message.asMap()));

    sensorProcessor.processSensorData(message);
  }
}
