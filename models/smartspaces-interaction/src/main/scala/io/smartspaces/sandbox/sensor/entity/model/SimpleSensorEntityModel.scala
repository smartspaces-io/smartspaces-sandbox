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
class SimpleSensorEntityModel(val sensorEntityDescription: SensorEntityDescription, val allModels: CompleteSensedEntityModel, val modelCreationTime: Long) extends SensorEntityModel {

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
   *
   * <p>
   * Assume that it is offlinen until told otherwise.
   */
  var online: Boolean = false

  /**
   * The time of the last update.
   */
  private var lastUpdate: Option[Long] = None

  override def getSensedValue(valueTypeId: String): Option[SensedValue[Any]] = {
    // TODO(keith): Needs some sort of concurrency block
    sensedValues.get(valueTypeId)
  }

  override def getAllSensedValues(): scala.collection.immutable.List[SensedValue[Any]] = {
    sensedValues.values.toList
  }

  override def updateSensedValue[T <: Any](value: SensedValue[T], timestamp: Long): Unit = {
    // TODO(keith): Needs some sort of concurrency block
    lastUpdate = Option(timestamp)

    // The online status is definitely true if an update is coming in.
    online = true

    sensedValues.put(value.valueType.externalId, value)
  }

  override def getLastUpdate(): Option[Long] = {
    lastUpdate
  }

  /**
   * Set he last update time.
   *
   * <p>
   * This is for testing.
   */
  private[model] def setLastUpdateTime(time: Long): Unit = {
    lastUpdate = Option(time)
  }

  override def checkIfOfflineTransition(currentTime: Long): Unit = {
    // Only check if the model thinks it is online and there was an update time,
    // otherwise we want the initial 
    if (online && lastUpdate.isDefined) {
      val sensorUpdateTimeLimit = sensorEntityDescription.sensorUpdateTimeLimit
      if (sensorUpdateTimeLimit.isDefined) {
        online = currentTime - lastUpdate.get <= sensorUpdateTimeLimit.get
      } else {
        // If this sensor requires a heartbeat, the heartbeat time can be checked.
      }

      // We knew online was true, so if now offline, then transitioned.
      if (!online) {
        allModels.broadcastSensorOfflineEvent(new SensorOfflineEvent(this, currentTime))
      }
    }
  }
}