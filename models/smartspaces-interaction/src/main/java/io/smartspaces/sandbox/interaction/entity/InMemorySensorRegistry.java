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
public class InMemorySensorRegistry {

  /**
   * A map of sensor IDs to their description
   */
  private Map<String, SensorEntityDescription> idToSensor = new HashMap<>();

  /**
   * A map of sensed entities IDs to their description
   */
  private Map<String, SensedEntityDescription> idToSensed = new HashMap<>();

  /**
   * The associations between sensors and what entity is being sensed by them.
   */
  private List<SimpleSensorSensedEntityAssociation> associations = new ArrayList<>();

  /**
   * The associations as an unmodifiable list.
   */
  private List<SimpleSensorSensedEntityAssociation> associationsImmutable =
      Collections.unmodifiableList(associations);

  /**
   * Register a sensor with the registry.
   * 
   * @param sensor
   *          the sensor to add
   * 
   * @return this registry
   */
  public InMemorySensorRegistry registerSensor(SensorEntityDescription sensor) {
    idToSensor.put(sensor.getId(), sensor);

    return this;
  }

  /**
   * Get the sensor description associated with a given ID.
   * 
   * @param id
   *          the sensor ID
   * 
   * @return the description, or {@code null} if no such sensor
   */
  public SensorEntityDescription getSensor(String id) {
    return idToSensor.get(id);
  }

  /**
   * Register a sensed entity with the registry.
   * 
   * @param sensedEntity
   *          the sensed entity to add
   * 
   * @return this registry
   */
  public InMemorySensorRegistry registerSensedEntity(SensedEntityDescription sensor) {
    idToSensed.put(sensor.getId(), sensor);

    return this;
  }

  /**
   * Get the sensed entity description associated with a given ID.
   * 
   * @param id
   *          the sensed entity ID
   * 
   * @return the description, or {@code null} if no such sensed entity
   */
  public SensedEntityDescription getSensedEntity(String id) {
    return idToSensed.get(id);
  }

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
  public InMemorySensorRegistry associateSensorWithSensedEntity(String sensorId,
      String sensedEntityId) {
    // TODO(keith) Decide what to do if neither exists
    SensorEntityDescription sensor = idToSensor.get(sensorId);
    SensedEntityDescription sensedEntity = idToSensed.get(sensedEntityId);

    associations.add(new SimpleSensorSensedEntityAssociation(sensor, sensedEntity));
    return this;
  }

  /**
   * Get the associations between sensors and their sensed entities.
   * 
   * @return the associations as an unmodifiable list
   */
  public Collection<SimpleSensorSensedEntityAssociation> getAssociations() {
    return associationsImmutable;
  }
}
