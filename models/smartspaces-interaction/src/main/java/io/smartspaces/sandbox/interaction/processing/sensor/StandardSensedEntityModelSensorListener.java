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

import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedValue;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensedValue;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModelCollection;
import io.smartspaces.sandbox.interaction.entity.sensor.StandardSensorData;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry;

/**
 * A sensor listener that will update sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelSensorListener implements SensedEntitySensorListener {

  /**
   * The sensed entity models to be updated.
   */
  private SensedEntityModelCollection sensedEntityModelCollection;

  /**
   * The map of sensor types to sensor processors.
   */
  private Map<String, StandardBleProximitySensorValueProcessor> sensorValuesProcessors =
      new HashMap<>();

  /**
   * Construct a new listener.
   * 
   * @param sensedEntityModelCollection
   *          the sensed entity models to be updated.
   */
  public StandardSensedEntityModelSensorListener(
      SensedEntityModelCollection sensedEntityModelCollection) {
    this.sensedEntityModelCollection = sensedEntityModelCollection;

    StandardBleProximitySensorValueProcessor standardBleProximitySensorValueProcessor = new StandardBleProximitySensorValueProcessor();
    sensorValuesProcessors.put(StandardSensorData.SENSOR_TYPE_PROXIMITY_BLE,
        standardBleProximitySensorValueProcessor);
  }

  @Override
  public void handleSensorData(SensedEntitySensorHandler handler, long timestamp,
      SensorEntityDescription sensor, SensedEntityDescription sensedEntity, DynamicObject data) {
    SensedEntityModel model =
        sensedEntityModelCollection.getSensedEntityModel(sensedEntity.getId());
    if (model == null) {
      handler.getSensorProcessor().getLog().formatWarn("Have no sensed entity model for entity %s",
          sensedEntity);
    }

    handler.getSensorProcessor().getLog().formatInfo("Updating model for entity %s", sensedEntity);

    // Go into the data fields.
    data.down("data");

    // Go through every property in the data set, find its type, and then create
    // appropriate values
    for (ObjectDynamicObjectEntry entry : data.getObjectEntries()) {
      String sensedValueName = entry.getProperty();

      entry.down();

      String sensedType = data.getRequiredString("type");
      if (StandardSensorData.DOUBLE_VALUED_SENSOR_TYPES.contains(sensedType)) {
        SensedValue<Double> value = new SimpleSensedValue<Double>(sensor, sensedValueName,
            sensedType, data.getDouble("value"), timestamp);
        handler.getSensorProcessor().getLog().info(value);

        model.updateSensedValue(value);
      } else {
        StandardBleProximitySensorValueProcessor sensorValueProcessor =
            sensorValuesProcessors.get(sensedType);
        if (sensorValueProcessor != null) {
          sensorValueProcessor.processData(timestamp, sensor, sensedEntity,
              sensedEntityModelCollection, data);
        } else {
          handler.getSensorProcessor().getLog().formatWarn("Got unknown sensor type %s",
              sensedType);
        }
      }
    }
  }
}
