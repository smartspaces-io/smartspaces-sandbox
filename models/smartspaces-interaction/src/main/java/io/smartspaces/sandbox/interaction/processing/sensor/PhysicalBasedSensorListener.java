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

import io.smartspaces.sandbox.interaction.entity.EntityDescription;
import io.smartspaces.util.data.dynamic.DynamicObject;

/**
 * A listener for physical based sensor events.
 * 
 * @author Keith M. Hughes
 */
public interface PhysicalBasedSensorListener {

  /**
   * Handle sensor data that has come in.
   * 
   * @param timestamp
   *          the time the sensor event came in
   * @param sensor
   *          the sensor the data came in on
   * @param physicalLocation
   *          the physical location the sensor gives data for
   * @param data
   *          the sensor data
   */
  void handleSensorData(long timestamp, EntityDescription sensor,
      EntityDescription physicalLocation, DynamicObject data);
}
