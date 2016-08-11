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

package io.smartspaces.sandbox.interaction.entity.model;

import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.model.SensedValue;

import java.util.Collection;

/**
 * A model of the sensor state of an entity.
 *
 * @author Keith M. Hughes
 */
trait SensedEntityModel {

  /**
   * Get the sensed entity model collection this model is in.
   *
   * @return the sensed entity model collection
   */
  def getAllModels(): CompleteSensedEntityModel

  /**
   * Get the entity description for the entity being modeled.
   *
   * @return the entity description
   */
  def getSensedEntityDescription(): SensedEntityDescription

  /**
   * Get the value of a sensed property.
   *
   * @param valueName
   *          the name of the value
   *
   * @return the sensed value with the specified name
   */
  def getSensedValue(valueName: String): Option[SensedValue[Any]]

  /**
   * Get all sensed values for this entity.
   *
   * @return all sensed values
   */
  def getAllSensedValues(): List[SensedValue[Any]]

  /**
   * Update a sensed value.
   *
   * @param value
   *          the value being updated
   */
  def updateSensedValue[T <: Any](value: SensedValue[T]): Unit

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
