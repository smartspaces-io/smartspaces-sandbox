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

import io.smartspaces.sandbox.interaction.entity.SensorRegistry;

import java.util.Collection;

/**
 * A collection of sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public interface SensedEntityModelCollection {

  /**
   * Prepare the collection.
   * 
   * <p>
   * This will include building sensing models.
   */
  void prepare();

  /**
   * Get the model for a given entity ID.
   * 
   * @param id
   *          the ID of the entity
   * @param <T>
   *          the complete type of the model
   * 
   * @return the model, or {@code null} if none
   */
  <T extends SensedEntityModel> T getSensedEntityModel(String id);

  /**
   * Get all models in the collection.
   * 
   * @return the models
   */
  Collection<SensedEntityModel> getAllSensedEntityModels();

  /**
   * Get the model for a given physical space entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model, or {@code null} if none
   */
  PhysicalSpaceSensedEntityModel getPhysicalSpaceSensedEntityModel(String id);

  /**
   * Get all physical space models in the collection.
   * 
   * @return the models
   */
  Collection<PhysicalSpaceSensedEntityModel> getAllPhysicalSpaceSensedEntityModels();

  /**
   * Get the model for a given person entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model, or {@code null} if none
   */
  PersonSensedEntityModel getPersonSensedEntityModel(String id);

  /**
   * Get all person models in the collection.
   * 
   * @return the models
   */
  Collection<PersonSensedEntityModel> getAllPersonSensedEntityModels();

  /**
   * Get the model for a given marked entity ID.
   * 
   * @param markerId
   *          the marker ID associated with the entity
   * 
   * @return the model, or {@code null} if none
   */
  PersonSensedEntityModel getMarkedSensedEntityModel(String markerId);

  /**
   * Get the sensor registry for the collection.
   * 
   * @return the sensor registry
   */
  SensorRegistry getSensorRegistry();
}