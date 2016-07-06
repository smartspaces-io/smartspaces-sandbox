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

/**
 * The standard person entity description.
 * 
 * @author Keith M. Hughes
 */
public class SimplePersonSensedEntityDescription extends SimpleEntityDescription
    implements PersonSensedEntityDescription {

  /**
   * Construct a new description.
   * 
   * @param id
   *          the ID of the entity
   * @param displayName
   *          the human readable display name of the entity
   * @param description
   *          the human readable description of the entity
   */
  public SimplePersonSensedEntityDescription(String id, String displayName, String description) {
    super(id, displayName, description);
  }

  @Override
  public String toString() {
    return "SimplePersonSensedEntityDescription [getId()=" + getId() + ", getDisplayName()="
        + getDisplayName() + ", getDescription()=" + getDescription() + "]";
  }
}