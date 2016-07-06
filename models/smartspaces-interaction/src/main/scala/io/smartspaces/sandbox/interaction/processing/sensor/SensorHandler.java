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

import io.smartspaces.resource.managed.ManagedResource;
import io.smartspaces.util.data.dynamic.DynamicObject;

/**
 * A handler for sensor data in a sensor processor.
 * 
 * @author Keith M. Hughes
 */
public interface SensorHandler extends ManagedResource {

  /**
   * Handle sensor data that has come in.
   * 
   * @param timestamp
   *          the time the sensor event came in
   * @param data
   *          the sensor data
   */
  void handleSensorData(long timestamp, DynamicObject data);

  /**
   * Set the sensor processor the handler is running under.
   * 
   * @param sensorProcessor
   *          the sensor processor
   */
  void setSensorProcessor(SensorProcessor sensorProcessor);

  /**
   * Get the sensor processor the handler is running under.
   * 
   * @return the sensor processor
   */
  SensorProcessor getSensorProcessor();
}