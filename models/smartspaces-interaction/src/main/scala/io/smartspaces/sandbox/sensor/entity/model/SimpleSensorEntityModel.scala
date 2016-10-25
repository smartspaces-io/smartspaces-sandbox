/*
O * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.sandbox.sensor.entity.model

import io.smartspaces.sandbox.sensor.entity.SensorEntityDescription

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/**
 * The model of a sensor.
 * 
 * @author Keith M. Hughes
 */
class SimpleSensorEntityModel(val sensorEntityDescription: SensorEntityDescription, val allModels: CompleteSensedEntityModel) extends SensorEntityModel {
 
  /**
   * The values being sensed keyed by the value name.
   */
  private val sensedValues: Map[String, SensedValue[Any]] = new HashMap
  
  /**
   * The model that is being sensed by this sensor.
   */
  var sensedEntityModel: Option[SensedEntityModel] = None
 
  /**
   * Is the sensor online?
   */
  var online: Boolean = false
 
  /**
   * The time of the last update.
   */
  private var lastUpdate: Long = 0

  override def getSensedValue(valueTypeId: String): Option[SensedValue[Any]] = {
    // TODO(keith): Needs some sort of concurrency block
    sensedValues.get(valueTypeId)
  }

  override def getAllSensedValues(): scala.collection.immutable.List[SensedValue[Any]] = {
    sensedValues.values.toList
  }

  override def updateSensedValue[T <: Any](value: SensedValue[T], timestamp: Long): Unit = {
    // TODO(keith): Needs some sort of concurrency block
    lastUpdate = timestamp
    online = true
    sensedValues.put(value.valueType.externalId, value);
  }
  
  override def getLastUpdate(): Long = {
    lastUpdate
  }
}