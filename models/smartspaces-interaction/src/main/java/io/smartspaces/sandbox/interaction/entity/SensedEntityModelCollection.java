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

package io.smartspaces.sandbox.interaction.entity;

import java.util.Collection;

/**
 * A collection of sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public interface SensedEntityModelCollection {
  /**
   * Create sensed entity models from their descriptions.
   * 
   * @param entities
   *          the entity descriptions
   * 
   * @return this collection
   */
  SensedEntityModelCollection
      createModelsFromDescriptions(Collection<SensedEntityDescription> entities);

  /**
   * Get the model for a given entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model, or {@code null} if none
   */
  SensedEntityModel getSensedEntityModel(String id);

  /**
   * Get all models in the collection.
   * 
   * @return the models
   */
  Collection<SensedEntityModel> getAllSensedEntityModels();
}