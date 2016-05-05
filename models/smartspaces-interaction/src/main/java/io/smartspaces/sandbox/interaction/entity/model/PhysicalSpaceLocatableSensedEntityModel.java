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

/**
 * A sensed entity model that can be located in a physical space.
 * 
 * @author Keith M. hughes
 */
public interface PhysicalSpaceLocatableSensedEntityModel extends SensedEntityModel {

  /**
   * Set the physical location of the entity.
   * 
   * @param location
   *          the new location of the entity
   */
  void setPhysicalSpaceLocation(PhysicalSpaceSensedEntityModel location);

  /**
   * Get the physical location of the entity.
   * 
   * @returns the location of the entity
   */
  PhysicalSpaceSensedEntityModel getPhysicalSpaceLocation();
}
