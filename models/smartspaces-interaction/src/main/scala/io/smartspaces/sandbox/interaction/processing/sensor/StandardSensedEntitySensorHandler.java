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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.EntityMapper;
import io.smartspaces.sandbox.interaction.entity.MemoryEntityMapper;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.util.data.dynamic.DynamicObject;

/**
 * The standard implementation of a sensed entity sensor handler.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntitySensorHandler implements SensedEntitySensorHandler {

  /**
   * The mapping from sensor to sensed entity.
   */
  private EntityMapper sensorToSensedEntity = new MemoryEntityMapper();

  /**
   * The sensors being handled, keyed by their ID.
   */
  private Map<String, SensorEntityDescription> sensors = new HashMap<>();

  /**
   * The entities being sensed, keyed by their ID.
   */
  private Map<String, SensedEntityDescription> sensedEntities = new HashMap<>();

  /**
   * The log for this handler.
   */
  private ExtendedLog log;

  /**
   * The sensor processor the sensor input is running under.
   */
  private SensorProcessor sensorProcessor;

  /**
   * The listeners for physical based sensor events.
   */
  private List<SensedEntitySensorListener> sensedEntitySensorListeners =
      new CopyOnWriteArrayList<>();

  /**
   * The handler for unknown sensor and sensed entity IDs.
   */
  private UnknownSensedEntityHandler unknownSensedEntityHandler;

  /**
   * Construct a new handler.
   * 
   * @param unknownSensedEntityHandler
   *          the unknown sensed entity handler
   * @param log
   *          the log to use
   */
  public StandardSensedEntitySensorHandler(UnknownSensedEntityHandler unknownSensedEntityHandler,
      ExtendedLog log) {
    this.unknownSensedEntityHandler = unknownSensedEntityHandler;
    this.log = log;
  }

  @Override
  public void startup() {
    // Nothing to do.
  }

  @Override
  public void shutdown() {
    // Nothing to do.
  }

  @Override
  public void setSensorProcessor(SensorProcessor sensorProcessor) {
    this.sensorProcessor = sensorProcessor;
  }

  @Override
  public SensorProcessor getSensorProcessor() {
    return sensorProcessor;
  }

  @Override
  public SensedEntitySensorHandler
      addSensedEntitySensorListener(SensedEntitySensorListener listener) {
    sensedEntitySensorListeners.add(listener);

    return this;
  }

  @Override
  public SensedEntitySensorHandler associateSensorWithEntity(SensorEntityDescription sensor,
      SensedEntityDescription sensedEntity) {
    sensors.put(sensor.getId(), sensor);
    sensedEntities.put(sensedEntity.getId(), sensedEntity);
    sensorToSensedEntity.put(sensor.getId(), sensedEntity.getId());

    return this;
  }

  @Override
  public void handleSensorData(long timestamp, DynamicObject data) {
    String sensorId = data.getString(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_SENSOR);

    if (sensorId == null) {
      log.warn("Got data from unknown sensor, the sensor ID is missing");
      return;
    }

    SensorEntityDescription sensor = sensors.get(sensorId);
    if (sensor == null) {
      log.formatWarn("Got data from unregistered sensor %s, the data is %s", sensorId,
          data.asMap());
      unknownSensedEntityHandler.handleUnknownSensor(sensorId);
      
      return;
    }

    String sensedEntityId = sensorToSensedEntity.get(sensorId);
    if (sensedEntityId == null) {
      log.formatWarn("Got data from sensor %s with no registered sensed entity: %s", sensorId,
          data.asMap());
      return;
    }

    // No need to confirm sensed entity, we would not have a sensed entity ID
    // unless there was an entity registered.
    SensedEntityDescription sensedEntity = sensedEntities.get(sensedEntityId);

    log.formatDebug("Got data from sensor %s for sensed entity %s: %s", sensor, sensedEntity,
        data.asMap());

    for (SensedEntitySensorListener listener : sensedEntitySensorListeners) {
      try {
        listener.handleSensorData(this, timestamp, sensor, sensedEntity, data);
      } catch (Throwable e) {
        log.formatError(e, "Error during listener processing of physical based sensor data");
      }
    }
  }
}
