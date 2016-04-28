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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A sensor registry totally contained in memory.
 * 
 * @author Keith M. Hughes
 */
public class InMemorySensorRegistry implements SensorRegistry {

  /**
   * A map of sensor IDs to their description
   */
  private Map<String, SensorEntityDescription> idToSensor = new HashMap<>();

  /**
   * A map of marker IDs to their description.
   */
  private Map<String, MarkerEntityDescription> idToMarker = new HashMap<>();

  /**
   * A map of markable IDs to their description.
   */
  private Map<String, MarkableEntityDescription> idToMarkable = new HashMap<>();

  /**
   * A map of marker IDs to their description.
   */
  private Map<String, MarkableEntityDescription> markerIdToMarkable = new HashMap<>();

  /**
   * A map of sensed entities IDs to their description
   */
  private Map<String, SensedEntityDescription> idToSensed = new HashMap<>();

  /**
   * The associations between sensors and what entity is being sensed by them.
   */
  private List<SimpleSensorSensedEntityAssociation> sensorSensedEntityAssociations =
      new ArrayList<>();

  /**
   * The associations as an unmodifiable list.
   */
  private List<SimpleSensorSensedEntityAssociation> sensorSensedEntityAssociationsImmutable =
      Collections.unmodifiableList(sensorSensedEntityAssociations);

  /**
   * The associations between markers and what entity is being marked by them.
   */
  private List<SimpleMarkerMarkedEntityAssociation> markerMarkedEntityAssociations =
      new ArrayList<>();

  /**
   * The associations as an unmodifiable list.
   */
  private List<SimpleMarkerMarkedEntityAssociation> markerMarkedEntityAssociationsImmutable =
      Collections.unmodifiableList(markerMarkedEntityAssociations);

  @Override
  public SensorRegistry registerSensor(SensorEntityDescription sensor) {
    idToSensor.put(sensor.getId(), sensor);

    return this;
  }

  @Override
  public SensorEntityDescription getSensor(String id) {
    return idToSensor.get(id);
  }

  @Override
  public SensorRegistry registerMarker(MarkerEntityDescription marker) {
    idToMarker.put(marker.getId(), marker);

    return this;
  }

  @Override
  public MarkerEntityDescription getMarker(String id) {
    return idToMarker.get(id);
  }

  @Override
  public MarkableEntityDescription getMarkableEntity(String id) {
    return idToMarkable.get(id);
  }

  @Override
  public SensorRegistry registerSensedEntity(SensedEntityDescription sensedEntity) {
    idToSensed.put(sensedEntity.getId(), sensedEntity);

    if (sensedEntity instanceof MarkableEntityDescription) {
      idToMarkable.put(sensedEntity.getId(), (MarkableEntityDescription) sensedEntity);
    }

    return this;
  }

  @Override
  public SensedEntityDescription getSensedEntity(String id) {
    return idToSensed.get(id);
  }

  @Override
  public Collection<SensedEntityDescription> getAllSensedEntities() {
    return idToSensed.values();
  }

  @Override
  public SensorRegistry associateSensorWithSensedEntity(String sensorId, String sensedEntityId) {
    // TODO(keith) Decide what to do if neither exists
    SensorEntityDescription sensor = idToSensor.get(sensorId);
    SensedEntityDescription sensedEntity = idToSensed.get(sensedEntityId);

    sensorSensedEntityAssociations
        .add(new SimpleSensorSensedEntityAssociation(sensor, sensedEntity));
    return this;
  }

  @Override
  public Collection<SimpleSensorSensedEntityAssociation> getSensorSensedEntityAssociations() {
    return sensorSensedEntityAssociationsImmutable;
  }

  @Override
  public SensorRegistry associateMarkerWithMarkedEntity(String markerId, String markedEntityId) {
    // TODO(keith) Decide what to do if neither exists
    MarkerEntityDescription marker = idToMarker.get(markerId);
    MarkableEntityDescription markedEntity = idToMarkable.get(markedEntityId);

    return associateMarkerWithMarkedEntity(marker, markedEntity);
  }

  @Override
  public SensorRegistry associateMarkerWithMarkedEntity(MarkerEntityDescription marker,
      MarkableEntityDescription markableEntity) {
    markerMarkedEntityAssociations
        .add(new SimpleMarkerMarkedEntityAssociation(marker, markableEntity));

    markerIdToMarkable.put(marker.getMarkerId(), markableEntity);

    return this;
  }

  @Override
  public Collection<SimpleMarkerMarkedEntityAssociation> getMarkerMarkedEntityAssociations() {
    return markerMarkedEntityAssociationsImmutable;
  }

  @Override
  public MarkableEntityDescription getMarkableEntityByMarkerId(String markerId) {
    return markerIdToMarkable.get(markerId);
  }
}
