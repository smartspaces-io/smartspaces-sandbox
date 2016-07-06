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
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedValue;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensedValue;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.CompleteSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.sensor.StandardSensorData;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry;

/**
 * A sensor processor that will update sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelProcessor
    implements SensedEntityModelProcessor, SensedEntitySensorListener {

  /**
   * The sensed entity models to be updated.
   */
  private CompleteSensedEntityModel completeSensedEntityModel;

  /**
   * The map of sensor types to sensor processors.
   */
  private Map<String, SensorValueProcessor> sensorValuesProcessors = new HashMap<>();

  /**
   * The context for sensor value processors.
   */
  private SensorValueProcessorContext processorContext;

  /**
   * The logger to use.
   */
  private ExtendedLog log;

  /**
   * Construct a new listener.
   * 
   * @param completeSensedEntityModel
   *          the sensed entity models to be updated
   * @param log
   *          the logger to use
   */
  public StandardSensedEntityModelProcessor(CompleteSensedEntityModel completeSensedEntityModel,
      ExtendedLog log) {
    this.completeSensedEntityModel = completeSensedEntityModel;
    this.log = log;

    processorContext = new SensorValueProcessorContext(completeSensedEntityModel, log);
  }

  @Override
  public SensedEntityModelProcessor addSensorValueProcessor(SensorValueProcessor processor) {
    sensorValuesProcessors.put(processor.getSensorValueType(), processor);

    return this;
  }

  @Override
  public void handleSensorData(SensedEntitySensorHandler handler, long timestamp,
      SensorEntityDescription sensor, SensedEntityDescription sensedEntity, DynamicObject data) {
    SensedEntityModel sensedEntityModel =
        completeSensedEntityModel.getSensedEntityModel(sensedEntity.getId());
    if (sensedEntityModel == null) {
      log.formatWarn("Have no sensed entity model for entity %s", sensedEntity);
      return;
    }

    log.formatInfo("Updating model for entity %s", sensedEntity);

    // Go into the data fields.
    data.down(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA);

    // Go through every property in the data set, find its type, and then create
    // appropriate values
    for (ObjectDynamicObjectEntry entry : data.getObjectEntries()) {
      String sensedValueName = entry.getProperty();

      entry.down();

      String sensedType =
          data.getRequiredString(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_TYPE);
      if (StandardSensorData.DOUBLE_VALUED_SENSOR_TYPES.contains(sensedType)) {
        SensedValue<Double> value =
            new SimpleSensedValue<Double>(sensor, sensedValueName, sensedType,
                data.getDouble(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_VALUE), timestamp);
        handler.getSensorProcessor().getLog().info(value);

        sensedEntityModel.updateSensedValue(value);
      } else {
        SensorValueProcessor sensorValueProcessor = sensorValuesProcessors.get(sensedType);
        if (sensorValueProcessor != null) {
          sensorValueProcessor.processData(timestamp, sensor, sensedEntityModel, processorContext,
              data);
        } else {
          log.formatWarn("Got unknown sensor type %s", sensedType);
        }
      }
    }
  }
}
