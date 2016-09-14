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

import io.smartspaces.event.observable.EventObservable;
import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;

import scala.collection.mutable._

/**
 * A model of a physical space.
 *
 * @author Keith M. Hughes
 */
class SimplePhysicalSpaceSensedEntityModel(
  entityDescription: PhysicalSpaceSensedEntityDescription, models: CompleteSensedEntityModel,
  private val occupancyObservable: EventObservable[PhysicalLocationOccupancyEvent]) extends SimpleSensedEntityModel(entityDescription, models)
    with PhysicalSpaceSensedEntityModel {

  /**
   * The occupants in the space.
   */
  private val occupants: Set[PersonSensedEntityModel] = new HashSet

  override def occupantEntered(person: PersonSensedEntityModel): PhysicalSpaceSensedEntityModel = {

    val hasBeenAdded = occupants.add(person)

    if (hasBeenAdded) {
      person.physicalSpaceLocation = this

      val entered = scala.collection.immutable.HashSet(person)

      occupancyObservable.emitEvent(new PhysicalLocationOccupancyEvent(this, entered, null))
    }

    this
  }

  override def occupantExited(person: PersonSensedEntityModel): PhysicalSpaceSensedEntityModel = {
    val wasHere = occupants.remove(person)

    if (wasHere) {
      person.physicalSpaceLocation = null

      val exited = scala.collection.immutable.HashSet(person)

      occupancyObservable.emitEvent(new PhysicalLocationOccupancyEvent(this, null, exited))
    }

    this
  }

  override def getOccupants(): scala.collection.immutable.Set[PersonSensedEntityModel] = {
    occupants.toSet
  }
}
