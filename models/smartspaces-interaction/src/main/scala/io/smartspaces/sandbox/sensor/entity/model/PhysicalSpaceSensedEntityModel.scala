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

package io.smartspaces.sandbox.sensor.entity.model

/**
 * A sensed entity model for a physical space.
 *
 * @author keith M. Hughes
 */
trait PhysicalSpaceSensedEntityModel extends SensedEntityModel {

  /**
   * An occupant has entered the physical space.
   *
   * <p>
   * This method will update the person's location.
   *
   * @param person
   *          the person who entered
   * @param timestamp
   * 				  the time the occupant exited the space
   *
   * @return this model
   */
  def occupantEntered(person: PersonSensedEntityModel, timestamp: Long): PhysicalSpaceSensedEntityModel

  /**
   * An occupant has exited the physical space.
   *
   * <p>
   * This method will update the person's location.
   *
   * @param person
   *          the person who exited
   * @param timestamp
   * 				  the time the occupant exited the space
   *
   * @return this model
   */
  def occupantExited(person: PersonSensedEntityModel, timestamp: Long): PhysicalSpaceSensedEntityModel

  /**
   * Get the current occupants of the space.
   *
   * @return the collection of current occupants
   */
  def getOccupants(): Set[PersonSensedEntityModel]
}
