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

import java.util.Map;

import io.smartspaces.event.trigger.SimpleHysteresisThresholdValueTrigger;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;

/**
 * Support for BLE proximity markers.
 * 
 * @author Keith M. Hughes
 */
public class StandardBleProximitySupport {

  /**
   * The configuration field that gives the map between sensors and the
   * configuration values for that sensor.
   * 
   * <p>
   * The key for this map will be the sensor ID.
   */
  public static final String CONFiGURATION_FIELD_SENSORS = "sensors";

  /**
   * The configuration field for a given sensor that gives the RSSI value that
   * should trigger when the markable entity enters the space.
   */
  public static final String CONFIGURATION_FIELD_RSSI_TRIGGER = "rssi.trigger";

  /**
   * The configuration field for a given sensor that gives the RSSI value offset
   * from the trigger that should trigger when the markable entity exits the
   * space.
   */
  public static final String CONFIGURATION_FIELD_RSSI_SPREAD = "rssi.spread";

  /**
   * Configure the trigger.
   * 
   * @param userTrigger
   *          the trigger to be configured
   * @param configData
   *          the configuration data
   * @param sensor
   *          the sensor that detected the BLE
   * @param processorContext
   *          the processor context
   */
  public void configureTrigger(SimpleHysteresisThresholdValueTrigger userTrigger,
      Map<String, Object> configData, SensorEntityDescription sensor,
      SensorValueProcessorContext processorContext) {
    DynamicObject config = new StandardDynamicObjectNavigator(configData);

    config.down(CONFiGURATION_FIELD_SENSORS);

    String sensorId = sensor.getId();
    if (config.downChecked(sensorId)) {
      userTrigger.setThresholdsWithOffset(config.getDouble(CONFIGURATION_FIELD_RSSI_TRIGGER),
          config.getDouble(CONFIGURATION_FIELD_RSSI_SPREAD));
    } else {
      processorContext.getLog().formatWarn("No BLE configuration data for sensor with ID %s",
          sensorId);
    }
  }

}
