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

package io.smartspaces.sandbox.interaction.entity.model.updater;

import io.smartspaces.sandbox.interaction.entity.model.PersonSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.PhysicalSpaceSensedEntityModel;

/**
 * A model updater for interactions between a person and a physical space.
 * 
 * @author Keith M. Hughes
 */
public class PersonPhysicalSpaceModelUpdater {

  /**
   * The physical space that is detecting the marker.
   */
  private PhysicalSpaceSensedEntityModel physicalSpace;

  /**
   * The person associated with the marker.
   */
  private PersonSensedEntityModel person;

  /**
   * Construct a new model updater.
   * 
   * @param physicalSpace
   *          the physical space
   * @param person
   *          the person
   */
  public PersonPhysicalSpaceModelUpdater(PhysicalSpaceSensedEntityModel physicalSpace,
      PersonSensedEntityModel person) {
    this.physicalSpace = physicalSpace;
    this.person = person;
  }

  /**
   * Have the person enter the space.
   */
  public void enterSpace() {
    physicalSpace.occupantEntered(person);
  }

  /**
   * Have the person exit the space.
   */
  public void exitSpace() {
    physicalSpace.occupantExited(person);
  }

  /**
   * Get the physical space.
   * 
   * @return the physical space
   */
  public PhysicalSpaceSensedEntityModel getPhysicalSpace() {
    return physicalSpace;
  }

  /**
   * Get the person.
   * 
   * @return the person
   */
  public PersonSensedEntityModel getPerson() {
    return person;
  }
}
