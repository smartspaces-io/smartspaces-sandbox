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

package io.smartspaces.sandbox.interaction.entity.model

import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription

/**
 * The model of a sensor.
 * 
 * @author Keith M. Hughes
 */
trait SensorEntityModel {
  
  /**
   * The sensor entity description for the model.
   */
  val sensorEntityDescription: SensorEntityDescription
  
  /**
   * Get the sensed entity model collection this model is in.
   */
  val allModels: CompleteSensedEntityModel

  /**
   * Get the last update for the model.
   *
   * @return the last time
   */
  def getLastUpdate(): Long

  /**
   * Set the update time for the model.
   *
   * @param updateTime
   * 		the time of the last update
   */
  def setUpdateTime(updateTime: Long): Unit
}