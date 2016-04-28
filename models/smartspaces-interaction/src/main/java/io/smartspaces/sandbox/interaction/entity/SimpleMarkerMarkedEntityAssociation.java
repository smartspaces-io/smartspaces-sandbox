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
 * An association between a marker and the entity it marks.
 * 
 * @author Keith M. Hughes
 */
public class SimpleMarkerMarkedEntityAssociation {

  /**
   * The marker.
   */
  private MarkerEntityDescription marker;

  /**
   * The sensed entity.
   */
  private MarkableEntityDescription markedEntity;

  /**
   * Construct a new association.
   * 
   * @param marker
   *          the marker
   * @param markedEntity
   *          the associated marked entity
   */
  public SimpleMarkerMarkedEntityAssociation(MarkerEntityDescription marker,
      MarkableEntityDescription markedEntity) {
    this.marker = marker;
    this.markedEntity = markedEntity;
  }

  /**
   * Get the marker.
   * 
   * @return the marker
   */
  public MarkerEntityDescription getMarker() {
    return marker;
  }

  /**
   * Get the sensed entity.
   * 
   * @return the sensed entity
   */
  public MarkableEntityDescription getMarkedEntity() {
    return markedEntity;
  }

}
