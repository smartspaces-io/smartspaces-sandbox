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
import io.smartspaces.sandbox.interaction.entity.EntityMapper;
import io.smartspaces.sandbox.interaction.entity.MemoryEntityMapper;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.util.data.dynamic.DynamicObject;

import scala.collection.mutable._

/**
 * The standard implementation of a sensed entity sensor handler.
 *
 * @author Keith M. Hughes
 */
class StandardSensedEntitySensorHandler(private val unknownSensedEntityHandler: UnknownSensedEntityHandler,
    private val log: ExtendedLog) extends SensedEntitySensorHandler {

  /**
   * The mapping from sensor to sensed entity.
   */
  private val sensorToSensedEntity: EntityMapper = new MemoryEntityMapper

  /**
   * The sensors being handled, keyed by their ID.
   */
  private val sensors: Map[String, SensorEntityDescription] = new HashMap

  /**
   * The entities being sensed, keyed by their ID.
   */
  private val sensedEntities: Map[String, SensedEntityDescription] = new HashMap

  /**
   * The sensor processor the sensor input is running under.
   */
  private var sensorProcessor: SensorProcessor = null

  /**
   * The listeners for physical based sensor events.
   */
  private val sensedEntitySensorListeners: ArrayBuffer[SensedEntitySensorListener] =
    new ArrayBuffer

  override def startup(): Unit = {
    // Nothing to do.
  }

  override def shutdown(): Unit = {
    // Nothing to do.
  }

  override def setSensorProcessor(sensorProcessor: SensorProcessor): Unit = {
    this.sensorProcessor = sensorProcessor;
  }

  override def getSensorProcessor(): SensorProcessor = {
    sensorProcessor
  }

  override def addSensedEntitySensorListener(listener: SensedEntitySensorListener): SensedEntitySensorHandler = {
    sensedEntitySensorListeners += listener

    this
  }

  override def associateSensorWithEntity(sensor: SensorEntityDescription,
    sensedEntity: SensedEntityDescription): SensedEntitySensorHandler = {
    sensors.put(sensor.getId(), sensor)
    sensedEntities.put(sensedEntity.getId(), sensedEntity)
    sensorToSensedEntity.put(sensor.getId(), sensedEntity.getId())

    this
  }

  override def handleSensorData(timestamp: Long, data: DynamicObject): Unit = {
    val sensorId = data.getString(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_SENSOR);

    if (sensorId == null) {
      log.warn("Got data from unknown sensor, the sensor ID is missing")
      return
    }

    val sensor = sensors.get(sensorId);
    if (sensor.isEmpty) {
      log.formatWarn("Got data from unregistered sensor %s, the data is %s", sensorId,
        data.asMap())
      unknownSensedEntityHandler.handleUnknownSensor(sensorId)

      return
    }

    val sensedEntityId = sensorToSensedEntity.get(sensorId)
    if (sensedEntityId.isEmpty) {
      log.formatWarn("Got data from sensor %s with no registered sensed entity: %s", sensorId,
        data.asMap())
      return
    }

    // No need to confirm sensed entity, we would not have a sensed entity ID
    // unless there was an entity registered.
    val sensedEntity = sensedEntities.get(sensedEntityId.get)

    log.formatDebug("Got data from sensor %s for sensed entity %s: %s", sensor, sensedEntity,
      data.asMap());

    sensedEntitySensorListeners.foreach((listener) => {
      try {
        listener.handleSensorData(this, timestamp, sensor.get, sensedEntity.get, data);
      } catch {
        case e: Throwable =>
          log.formatError(e, "Error during listener processing of physical based sensor data");
      }
    })
  }
}
