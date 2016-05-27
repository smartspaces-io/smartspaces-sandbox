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

import java.util.Collection;

/**
 * A handler for sensed entities where the entity ID is not recognized.
 * 
 * @author Keith M. Hughes
 */
public interface UnknownSensedEntityHandler {

  /**
   * An unknown sensor ID was received.
   * 
   * @param sensorId
   *          the ID of the unknown sensor
   */
  void handleUnknownSensor(String sensorId);

  /**
   * Remove an unknown sensor ID from the handler.
   * 
   * <p>
   * This will usually be because it has finally been added to the known
   * sensors.
   * 
   * @param sensorId
   *          the known sensor
   */
  void removeUnknownSensorId(String sensorId);

  /**
   * Get all of the unknown sensor IDs.
   * 
   * @return all of the unknown sensor IDs
   */
  Collection<String> getAllUnknownSensorIds();
}
