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

package io.smartspaces.sandbox.interaction.entity.sensor;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * A collection of constants helpful for sensors.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensorData {

  /**
   * The name of a temperature sensor value.
   */
  public static final String SENSOR_VALUE_NAME_TEMPERATURE = "temperature";

  /**
   * The name of a humidity sensor value.
   */
  public static final String SENSOR_VALUE_NAME_HUMIDITY = "humidity";

  /**
   * The sensor values that are double-valued.
   */
  public static final Set<String> DOUBLE_VALUED_SENSOR_VALUES =
      ImmutableSet.of(SENSOR_VALUE_NAME_TEMPERATURE, SENSOR_VALUE_NAME_HUMIDITY);
}
