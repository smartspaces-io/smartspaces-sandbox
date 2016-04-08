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
import java.util.Map;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.EntityDescription;
import io.smartspaces.sandbox.interaction.entity.EntityMapper;
import io.smartspaces.sandbox.interaction.entity.MemoryEntityMapper;
import io.smartspaces.util.data.dynamic.DynamicObject;

/**
 * The standard implementation of a physical based sensor handler.
 * 
 * @author Keith M. Hughes
 */
public class StandardPhysicalBasedSensorHandler implements PhysicalBasedSensorHandler {

  /**
   * The entity mapper to use.
   */
  private EntityMapper sensorToLocation = new MemoryEntityMapper();

  /**
   * The sensors being handled, keyed by their ID.
   */
  private Map<String, EntityDescription> sensors = new HashMap<>();

  /**
   * The physical locations being handled, keyed by their ID.
   */
  private Map<String, EntityDescription> physicalLocations = new HashMap<>();

  /**
   * The log for this handler.
   */
  private ExtendedLog log;

  /**
   * The sensor processor the sensor input is running under.
   */
  private SensorProcessor sensorProcessor;

  @Override
  public void setSensorProcessor(SensorProcessor sensorProcessor) {
    this.sensorProcessor = sensorProcessor;
  }

  /**
   * Construct a new handler.
   * 
   * @param log
   *          the log to use
   */
  public StandardPhysicalBasedSensorHandler(ExtendedLog log) {
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
  public PhysicalBasedSensorHandler addSensorDescription(EntityDescription sensor,
      EntityDescription physicalLocation) {
    sensors.put(sensor.getId(), sensor);
    physicalLocations.put(physicalLocation.getId(), physicalLocation);
    sensorToLocation.put(sensor.getId(), physicalLocation.getId());

    return this;
  }

  @Override
  public void handleSensorData(DynamicObject data) {
    String sensorId = data.getString("sensor");

    if (sensorId == null) {
      log.warn("Got data from unknown sensor, the sensor ID is missing");
      return;
    }

    EntityDescription sensor = sensors.get(sensorId);
    if (sensor == null) {
      log.formatWarn("Got data from unregistered sensor %s, the data is %s", sensorId,
          data.asMap());
      return;
    }

    String locationId = sensorToLocation.get(sensorId);
    if (locationId == null) {
      log.formatWarn("Got data from sensor %s with no registered location: %s", sensorId,
          data.asMap());
      return;
    }

    // No need to confirm location, we would not have a locationID unless there
    // was a physical
    // location registered.
    EntityDescription location = physicalLocations.get(locationId);

    log.formatInfo("Got data from sensor %s in location %s: %s", sensor, location, data.asMap());
  }
}
