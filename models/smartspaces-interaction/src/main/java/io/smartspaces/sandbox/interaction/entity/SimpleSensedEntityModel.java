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
   * The values being sensed keyed by the value name.
   */
  private Map<String, SensedValue<?>> sensedValues = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public <T extends SensedEntityDescription> T getSensedEntityDescription() {
    return (T) entityDescription;
  }

  @Override
  public SensedValue<?> getSensedValue(String valueName) {
    // TODO(keith): Needs some sort of concurrency block
    return sensedValues.get(valueName);
  }

  @Override
  public void updateSensedValue(SensedValue<?> value) {
    // TODO(keith): Needs some sort of concurrency block
    sensedValues.put(value.getValueName(), value);
  }
}
