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
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelCollection implements SensedEntityModelCollection {

  /**
   * The sensor registry for this collection.
   */
  private SensorRegistry sensorRegistry;

  /**
   * Map of entity IDs to their models.
   */
  private Map<String, SensedEntityModel> idToModels = new HashMap<>();

  /**
   * Construct a new collection.
   * 
   * @param sensorRegistry
   *          the sensor registry
   */
  public StandardSensedEntityModelCollection(SensorRegistry sensorRegistry) {
    this.sensorRegistry = sensorRegistry;
  }

  @Override
  public void prepare() {
    createModelsFromDescriptions(sensorRegistry.getAllSensedEntities());
  }

  @Override
  public SensorRegistry getSensorRegistry() {
    return sensorRegistry;
  }

  @Override
  public SensedEntityModelCollection
      createModelsFromDescriptions(Collection<SensedEntityDescription> entities) {
    for (SensedEntityDescription entityDescription : entities) {
      idToModels.put(entityDescription.getId(), new SimpleSensedEntityModel(entityDescription));
    }

    return this;
  }

  @Override
  public SensedEntityModel getSensedEntityModel(String id) {
    return idToModels.get(id);
  }

  @Override
  public Collection<SensedEntityModel> getAllSensedEntityModels() {
    return idToModels.values();
  }
}
