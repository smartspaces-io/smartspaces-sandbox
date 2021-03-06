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

package io.smartspaces.sandbox.sensor.entity.model.updater

import io.smartspaces.sandbox.sensor.entity.model.PersonSensedEntityModel
import io.smartspaces.sandbox.sensor.entity.model.PhysicalSpaceSensedEntityModel

/**
 * A model updater for a change from one location to another.
 *
 * @author Keith M. Hughes
 */
trait LocationChangeModelUpdater {
  /**
   * Update the location of the person.
   *
   * @param newLocation
   *             the new location
   * @param person
   *             the person
   * @param timestamp
   *             the timestamp of when the location change took place
   */
  def updateLocation(newLocation: PhysicalSpaceSensedEntityModel, person: PersonSensedEntityModel, timestamp: Long): Unit
}