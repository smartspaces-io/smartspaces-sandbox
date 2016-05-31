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

import io.smartspaces.sandbox.event.observable.EventObservable;
import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * A model of a physical space.
 * 
 * @author Keith M. Hughes
 */
public class SimplePhysicalSpaceSensedEntityModel extends SimpleSensedEntityModel
    implements PhysicalSpaceSensedEntityModel {

  /**
   * The occupants in the space.
   */
  private Set<PersonSensedEntityModel> occupants = new HashSet<>();

  /**
   * The observable for occupancy events.
   */
  private EventObservable<PhysicalLocationOccupancyEvent> occupancyObservable;

  /**
   * Construct a new sensed entity model.
   * 
   * @param entityDescription
   *          the description of the entity
   * @param models
   *          the collection of models this entity is in
   * @param occupancyObservable
   *          the observable for occupancy events
   */
  public SimplePhysicalSpaceSensedEntityModel(
      PhysicalSpaceSensedEntityDescription entityDescription, SensedEntityModelCollection models,
      EventObservable<PhysicalLocationOccupancyEvent> occupancyObservable) {
    super(entityDescription, models);

    this.occupancyObservable = occupancyObservable;
  }

  @Override
  public PhysicalSpaceSensedEntityModel occupantEntered(PersonSensedEntityModel person) {

    boolean hasBeenAdded = occupants.add(person);

    if (hasBeenAdded) {
      person.setPhysicalSpaceLocation(this);

      Set<PersonSensedEntityModel> entered = ImmutableSet.of(person);

      occupancyObservable.emitEvent(new PhysicalLocationOccupancyEvent(this, entered, null));
    }

    return this;
  }

  @Override
  public PhysicalSpaceSensedEntityModel occupantExited(PersonSensedEntityModel person) {
    System.out.println("Called exit");
    boolean wasHere = occupants.remove(person);
 
    if (wasHere) {
      person.setPhysicalSpaceLocation(null);

      Set<PersonSensedEntityModel> exited = ImmutableSet.of(person);

      System.out.println("Emitting event " + exited);
      occupancyObservable.emitEvent(new PhysicalLocationOccupancyEvent(this, null, exited));
    }

    return this;
  }

  @Override
  public Set<PersonSensedEntityModel> getOccupants() {
    return Sets.newHashSet(occupants);
  }
}
