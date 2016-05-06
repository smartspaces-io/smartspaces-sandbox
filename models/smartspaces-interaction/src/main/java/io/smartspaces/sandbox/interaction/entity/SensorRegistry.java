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

import java.util.Collection;

/**
 * A registry of known sensors and entities that are being sensed.
 * 
 * @author Keith M. Hughes
 */
public interface SensorRegistry {

  /**
   * Register a sensor with the registry.
   * 
   * @param sensor
   *          the sensor to add
   * 
   * @return this registry
   */
  SensorRegistry registerSensor(SensorEntityDescription sensor);

  /**
   * Get the sensor description associated with a given ID.
   * 
   * @param id
   *          the sensor ID
   * 
   * @return the description, or {@code null} if no such sensor
   */
  SensorEntityDescription getSensor(String id);

  /**
   * Register a marker with the registry.
   * 
   * @param marker
   *          the marker to add
   * 
   * @return this registry
   */
  SensorRegistry registerMarker(MarkerEntityDescription marker);

  /**
   * Get the marker description associated with a given ID.
   * 
   * @param id
   *          the marker ID
   * 
   * @return the description, or {@code null} if no such marker
   */
  MarkerEntityDescription getMarker(String id);

  /**
   * Get the markable entity description associated with a given ID.
   * 
   * @param id
   *          the markable entity ID
   * 
   * @return the description, or {@code null} if no such markable entity
   */
  MarkableEntityDescription getMarkableEntity(String id);

  /**
   * Get the markable entity description associated with a given marker ID.
   * 
   * <p>
   * The {@code markerId} is the value of
   * {@link MarkerEntityDescription.getMarkerId()}.
   * 
   * @param markerId
   *          the marker ID
   * 
   * @return the description, or {@code null} if no such markable entity
   */
  MarkableEntityDescription getMarkableEntityByMarkerId(String markerId);

  /**
   * Register a sensed entity with the registry.
   * 
   * @param sensedEntity
   *          the sensed entity to add
   * 
   * @return this registry
   */
  SensorRegistry registerSensedEntity(SensedEntityDescription sensor);

  /**
   * Get the sensed entity description associated with a given ID.
   * 
   * @param id
   *          the sensed entity ID
   * 
   * @return the description, or {@code null} if no such sensed entity
   */
  SensedEntityDescription getSensedEntity(String id);

  /**
   * Get all the sensed entities in the registry.
   * 
   * @return a collection of the entities
   */
  Collection<SensedEntityDescription> getAllSensedEntities();

  /**
   * Associate a marker with its marked entity.
   * 
   * @param markerId
   *          the ID of the marker
   * @param markedEntityId
   *          the ID of the marked entity
   * 
   * @returns this registry
   */
  SensorRegistry associateMarkerWithMarkedEntity(String markerId, String markedEntityId);

  /**
   * Associate a marker with its marked entity.
   * 
   * @param marker
   *          the marker
   * @param markableEntity
   *          the markable entity
   * 
   * @returns this registry
   */
  SensorRegistry associateMarkerWithMarkedEntity(MarkerEntityDescription marker,
      MarkableEntityDescription markableEntity);

  /**
   * Get the associations between markers and their marked entities.
   * 
   * @return the associations as an unmodifiable list
   */
  Collection<MarkerMarkedEntityAssociation> getMarkerMarkedEntityAssociations();

  /**
   * Associate a sensor with its sensed entity.
   * 
   * @param sensorId
   *          the ID of the sensor
   * @param sensedEntityId
   *          the ID of the sensed entity
   * 
   * @returns this registry
   */
  SensorRegistry associateSensorWithSensedEntity(String sensorId, String sensedEntityId);

  /**
   * Get the associations between sensors and their sensed entities.
   * 
   * @return the associations as an unmodifiable list
   */
  Collection<SimpleSensorSensedEntityAssociation> getSensorSensedEntityAssociations();
}