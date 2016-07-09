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
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedValue;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensedValue;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.CompleteSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.sensor.StandardSensorData;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry;

import scala.collection.mutable._
import scala.collection.JavaConversions._;

/**
 * A sensor processor that will update sensed entity models.
 * 
 * @author Keith M. Hughes
 */
class StandardSensedEntityModelProcessor(private val completeSensedEntityModel: CompleteSensedEntityModel,
      private val log: ExtendedLog)
    extends SensedEntityModelProcessor with SensedEntitySensorListener {

  /**
   * The map of sensor types to sensor processors.
   */
  private val  sensorValuesProcessors: Map[String, SensorValueProcessor] = new HashMap

  /**
   * The context for sensor value processors.
   */
  private val processorContext = new SensorValueProcessorContext(completeSensedEntityModel, log)

  override def  addSensorValueProcessor(processor: SensorValueProcessor ): SensedEntityModelProcessor = {
    sensorValuesProcessors.put(processor.getSensorValueType(), processor)

    this
  }

  override def handleSensorData(handler: SensedEntitySensorHandler , timestamp: Long,
       sensor: SensorEntityDescription, sensedEntity: SensedEntityDescription , data: DynamicObject ): Unit = {
    val sensedEntityModel =
        completeSensedEntityModel.getSensedEntityModel(sensedEntity.getId())
    if (sensedEntityModel.isEmpty) {
      log.formatWarn("Have no sensed entity model for entity %s", sensedEntity)
      return
    }

    log.formatInfo("Updating model for entity %s", sensedEntity)

    // Go into the data fields.
    data.down(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA)

    // Go through every property in the data set, find its type, and then create
    // appropriate values
    data.getObjectEntries().foreach((entry) => {
      val sensedValueName = entry.getProperty()

      entry.down()

      val sensedType =
          data.getRequiredString(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_TYPE)
      if (StandardSensorData.DOUBLE_VALUED_SENSOR_TYPES.contains(sensedType)) {
        val value =
            new SimpleSensedValue[Double](sensor, sensedValueName, sensedType,
                data.getDouble(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_VALUE), timestamp)
                
        handler.getSensorProcessor().getLog().info(value)

        sensedEntityModel.get.updateSensedValue(value)
      } else {
        val sensorValueProcessor = sensorValuesProcessors.get(sensedType);
        if (sensorValueProcessor.isDefined) {
          sensorValueProcessor.get.processData(timestamp, sensor, sensedEntityModel.get, processorContext,
              data);
        } else {
          log.formatWarn("Got unknown sensor type %s", sensedType);
        }
      }
    })
  }
}
