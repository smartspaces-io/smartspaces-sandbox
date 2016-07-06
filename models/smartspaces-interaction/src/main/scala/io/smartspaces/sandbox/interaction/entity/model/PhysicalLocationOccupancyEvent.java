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

import java.util.Set;

/**
 * An event that signals occupancy changes in a physical location.
 * 
 * @author Keith M. Hughes
 */
public class PhysicalLocationOccupancyEvent {

  /**
   * The name of the event.
   */
  public static final String EVENT_NAME = "location.occupancy";

  /**
   * The physical space that was entered or exited.
   */
  private PhysicalSpaceSensedEntityModel physicalSpace;

  /**
   * The people who have entered the room.
   */
  private Set<PersonSensedEntityModel> entered;

  /**
   * The people who have exited the room.
   */
  private Set<PersonSensedEntityModel> exited;

  public PhysicalLocationOccupancyEvent(PhysicalSpaceSensedEntityModel physicalSpace,
      Set<PersonSensedEntityModel> entered, Set<PersonSensedEntityModel> exited) {
    this.physicalSpace = physicalSpace;
    this.entered = entered;
    this.exited = exited;
  }

  /**
   * Get the physical space.
   * 
   * @return the physical space.
   */
  public PhysicalSpaceSensedEntityModel getPhysicalSpace() {
    return physicalSpace;
  }

  /**
   * Get the people who have entered in this event.
   * 
   * @return the people who have entered in this event.
   */
  public Set<PersonSensedEntityModel> getEntered() {
    return entered;
  }

  /**
   * Get the people who have exited in this event.
   * 
   * @return the people who have exited in this event.
   */
  public Set<PersonSensedEntityModel> getExited() {
    return exited;
  }
}
