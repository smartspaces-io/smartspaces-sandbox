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

import io.smartspaces.sandbox.interaction.entity.MarkerMarkedEntityAssociation;
import io.smartspaces.sandbox.interaction.entity.PersonSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorRegistry;
import io.smartspaces.service.speech.synthesis.SpeechSynthesisPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of sensed entity models.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelCollection implements SensedEntityModelCollection {

  /**
   * The sensor registry for this collection.
   */
  private SensorRegistry sensorRegistry;

  /**
   * Map of entity IDs to their models.
   */
  private Map<String, SensedEntityModel> idToModels = new HashMap<>();

  /**
   * Map of physical entity IDs to their models.
   */
  private Map<String, PhysicalSpaceSensedEntityModel> idToPhysicalSpaceModels = new HashMap<>();

  /**
   * Map of person entity IDs to their models.
   */
  private Map<String, PersonSensedEntityModel> idToPersonModels = new HashMap<>();

  /**
   * Map of marker IDs to their models.
   */
  private Map<String, PersonSensedEntityModel> markerIdToPersonModels = new HashMap<>();

  /**
   * Not staying! Just here until event listeners are in.
   */
  private SpeechSynthesisPlayer speechPlayer;

  /**
   * Construct a new collection.
   * 
   * @param sensorRegistry
   *          the sensor registry
   */
  public StandardSensedEntityModelCollection(SensorRegistry sensorRegistry,
      SpeechSynthesisPlayer speechPlayer) {
    this.sensorRegistry = sensorRegistry;
    this.speechPlayer = speechPlayer;
  }

  @Override
  public SensorRegistry getSensorRegistry() {
    return sensorRegistry;
  }

  @Override
  public void prepare() {
    createModelsFromDescriptions();
  }

  /**
   * Create all models from the descriptions in the registry.
   */
  private void createModelsFromDescriptions() {
    for (SensedEntityDescription entityDescription : sensorRegistry.getAllSensedEntities()) {
      addNewSensedEntity(entityDescription);
    }

    for (MarkerMarkedEntityAssociation association : sensorRegistry
        .getMarkerMarkedEntityAssociations()) {
      markerIdToPersonModels.put(association.getMarker().getMarkerId(),
          idToPersonModels.get(association.getMarkedEntity().getId()));
    }

  }

  /**
   * Add in a new sensed entity into the collection.
   * 
   * @param entityDescription
   *          the new description
   */
  private void addNewSensedEntity(SensedEntityDescription entityDescription) {
    String id = entityDescription.getId();

    SensedEntityModel model = null;
    if (entityDescription instanceof PhysicalSpaceSensedEntityDescription) {
      model = new SimplePhysicalSpaceSensedEntityModel(
          (PhysicalSpaceSensedEntityDescription) entityDescription, this, speechPlayer);
      idToPhysicalSpaceModels.put(id, (PhysicalSpaceSensedEntityModel) model);
    } else if (entityDescription instanceof PersonSensedEntityDescription) {
      model = new SimplePersonSensedEntityModel((PersonSensedEntityDescription) entityDescription,
          this);
      idToPersonModels.put(id, (PersonSensedEntityModel) model);
    } else {
      model = new SimpleSensedEntityModel(entityDescription, this);
    }

    idToModels.put(id, model);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends SensedEntityModel> T getSensedEntityModel(String id) {
    return (T) idToModels.get(id);
  }

  @Override
  public Collection<SensedEntityModel> getAllSensedEntityModels() {
    return idToModels.values();
  }

  @Override
  public PhysicalSpaceSensedEntityModel getPhysicalSpaceSensedEntityModel(String id) {
    return idToPhysicalSpaceModels.get(id);
  }

  @Override
  public Collection<PhysicalSpaceSensedEntityModel> getAllPhysicalSpaceSensedEntityModels() {
    return idToPhysicalSpaceModels.values();
  }

  @Override
  public PersonSensedEntityModel getPersonSensedEntityModel(String id) {
    return idToPersonModels.get(id);
  }

  @Override
  public Collection<PersonSensedEntityModel> getAllPersonSensedEntityModels() {
    return idToPersonModels.values();
  }

  @Override
  public PersonSensedEntityModel getMarkedSensedEntityModel(String markerId) {
    return markerIdToPersonModels.get(markerId);
  }
}
