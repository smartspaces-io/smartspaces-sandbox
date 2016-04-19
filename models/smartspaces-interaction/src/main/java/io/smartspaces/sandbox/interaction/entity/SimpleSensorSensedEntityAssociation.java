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

/**
 * An association between a sensor and the entity it senses.
 * 
 * @author Keith M. Hughes
 */
public class SimpleSensorSensedEntityAssociation {

  /**
   * The sensor.
   */
  private SensorEntityDescription sensor;

  /**
   * The sensed entity.
   */
  private SensedEntityDescription sensedEntity;

  /**
   * Construct a new association.
   * 
   * @param sensor
   *          the sensor
   * @param sensedEntity
   *          the associated sensed entity
   */
  public SimpleSensorSensedEntityAssociation(SensorEntityDescription sensor,
      SensedEntityDescription sensedEntity) {
    this.sensor = sensor;
    this.sensedEntity = sensedEntity;
  }

  /**
   * Get the sensor.
   * 
   * @return the sensor
   */
  public SensorEntityDescription getSensor() {
    return sensor;
  }

  /**
   * Get the sensed entity.
   * 
   * @return the sensed entity
   */
  public SensedEntityDescription getSensedEntity() {
    return sensedEntity;
  }

  @Override
  public String toString() {
    return "SimpleSensorSensedEntityAssociation [sensor=" + sensor + ", sensedEntity="
        + sensedEntity + "]";
  }
}
