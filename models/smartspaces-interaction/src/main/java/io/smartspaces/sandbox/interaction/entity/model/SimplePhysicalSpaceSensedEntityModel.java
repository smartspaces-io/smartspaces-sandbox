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

import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;
import io.smartspaces.service.speech.synthesis.SpeechSynthesisPlayer;

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
   * Not staying! Just until vent listeners in.
   */
  private SpeechSynthesisPlayer speechPlayer;

  /**
   * Construct a new sensed entity model.
   * 
   * @param entityDescription
   *          the description of the entity
   * @param models
   *          the collection of models this entity is in
   */
  public SimplePhysicalSpaceSensedEntityModel(
      PhysicalSpaceSensedEntityDescription entityDescription, SensedEntityModelCollection models,
      SpeechSynthesisPlayer speechPlayer) {
    super(entityDescription, models);

    this.speechPlayer = speechPlayer;
  }

  @Override
  public PhysicalSpaceSensedEntityModel occupantEntered(PersonSensedEntityModel person) {

    occupants.add(person);

    person.setPhysicalSpaceLocation(this);

    speechPlayer.speak(
        String.format("%s has entered %s", person.getSensedEntityDescription().getDisplayName(),
            getSensedEntityDescription().getDisplayName()),
        false);

    return this;
  }

  @Override
  public PhysicalSpaceSensedEntityModel occupantExited(PersonSensedEntityModel person) {
    occupants.remove(person);

    person.setPhysicalSpaceLocation(null);

    speechPlayer.speak(
        String.format("%s has exited %s", person.getSensedEntityDescription().getDisplayName(),
            getSensedEntityDescription().getDisplayName()),
        false);

    return this;
  }
}
