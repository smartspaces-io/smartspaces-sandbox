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
import io.smartspaces.sandbox.interaction.entity.SensedValue;

import java.util.Collection;

/**
 * A model of the sensor state of an entity.
 * 
 * @author Keith M. Hughes
 */
public interface SensedEntityModel {
  
  /**
   * Get the sensed entity model collection this model is in.
   * 
   * @return the sensed entity model collection
   */
  CompleteSensedEntityModel getAllModels();

  /**
   * Get the entity description for the entity being modeled.
   * 
   * @return the entity description
   */
  <T extends SensedEntityDescription> T getSensedEntityDescription();

  /**
   * Get the value of a sensed property.
   * 
   * @param valueName
   *          the name of the value
   * 
   * @return the sensed value with the specified name or {@link null} if none
   */
  SensedValue<?> getSensedValue(String valueName);

  /**
   * Get all sensed values for this entity.
   * 
   * @return the sensed value with the specified name or {@link null} if none
   */
  Collection<SensedValue<?>> getAllSensedValues();

  /**
   * Update a sensed value.
   * 
   * @param value
   *          the value being updated
   */
  void updateSensedValue(SensedValue<?> value);

  /**
   * Perform a set of operations within a transaction.
   * 
   * @param transaction
   *          the code to run inside the transaction
   * 
   * @return the value returned
   */
  <T extends SensedEntityModel, U> U doTransaction(SensedEntityModelTransaction<T, U> transaction);
}
