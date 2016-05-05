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
import java.util.HashMap;
import java.util.Map;

/**
 * A very simple model of a sensed entity.
 * 
 * @author Keith M. Hughes
 */
public class SimpleSensedEntityModel implements SensedEntityModel {

  /**
   * The description of the entity that is being sensed.
   */
  private SensedEntityDescription entityDescription;

  /**
   * The model collection this model is in.
   */
  private SensedEntityModelCollection models;

  /**
   * The values being sensed keyed by the value name.
   */
  private Map<String, SensedValue<?>> sensedValues = new HashMap<>();

  /**
   * Construct a new sensed entity model.
   * 
   * @param entityDescription
   *          the description of the entity
   * @param models
   *          the collection of models this entity is in
   */
  public SimpleSensedEntityModel(SensedEntityDescription entityDescription,
      SensedEntityModelCollection models) {
    this.entityDescription = entityDescription;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends SensedEntityDescription> T getSensedEntityDescription() {
    return (T) entityDescription;
  }

  @Override
  public SensedEntityModelCollection getAllModels() {
    return models;
  }

  @Override
  public SensedValue<?> getSensedValue(String valueName) {
    // TODO(keith): Needs some sort of concurrency block
    return sensedValues.get(valueName);
  }

  @Override
  public Collection<SensedValue<?>> getAllSensedValues() {
    return sensedValues.values();
  }

  @Override
  public void updateSensedValue(SensedValue<?> value) {
    // TODO(keith): Needs some sort of concurrency block
    sensedValues.put(value.getName(), value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends SensedEntityModel, U> U
      doTransaction(SensedEntityModelTransaction<T, U> transaction) {
    return transaction.perform((T) this);
  }
}
