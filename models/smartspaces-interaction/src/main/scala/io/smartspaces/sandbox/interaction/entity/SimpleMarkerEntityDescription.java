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
 * The standard marker entity description.
 * 
 * @author Keith M. Hughes
 */
public class SimpleMarkerEntityDescription extends SimpleEntityDescription
    implements MarkerEntityDescription {

  /**
   * The ID the marker will use for identification.
   */
  private String markerId;

  /**
   * Construct a new description.
   * 
   * @param id
   *          the ID of the entity
   * @param displayName
   *          the human readable display name of the entity
   * @param description
   *          the human readable description of the entity
   * @param markerId
   *          the ID the marker will use for identification.
   */
  public SimpleMarkerEntityDescription(String id, String displayName, String description,
      String markerId) {
    super(id, displayName, description);

    this.markerId = markerId;
  }

  @Override
  public String getMarkerId() {
    return markerId;
  }

  @Override
  public String toString() {
    return "SimpleMarkerEntityDescription [getId()=" + getId() + ", getDescription()="
        + getDescription() + ", markerId=" + markerId + "]";
  }
}
