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
 * A simple implementation of an entity description.
 * 
 * @author Keith M. Hughes
 */
public class SimpleEntityDescription implements EntityDescription {

  /**
   * The ID of the entity.
   */
  private String id;

  /**
   * The human readable display name of the entity.
   */
  private String displayName;

  /**
   * The human readable description of the entity.
   */
  private String description;

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
  public SimpleEntityDescription(String id, String displayName, String description) {
    this.id = id;
    this.displayName = displayName;
    this.description = description;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "SimpleEntityDescription [id=" + id + ", displayName=" + displayName + ", description="
        + description + "]";
  }
}
