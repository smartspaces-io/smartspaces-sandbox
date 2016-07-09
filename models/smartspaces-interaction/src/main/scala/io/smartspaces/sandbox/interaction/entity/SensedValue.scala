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
 * The value of a sensor.
 * 
 * @param <T>
 *          the type of the value
 * 
 * @author Keith M. Hughes
 */
trait SensedValue[+T <: Any] {

  /**
   * Get the description of the sensor that gave the value.
   * 
   * @return the sensor description
   */
   def getSensor(): SensorEntityDescription

  /**
   * Get the name of the sensed value.
   * 
   * @return the name
   */
  def getName(): String 

  /**
   * Get the type of the sensed value.
   * 
   * @return the type
   */
  def getType(): String 

  /**
   * Get the timestamp of when the value was last updated.
   * 
   * @return the timestamp in milliseconds since the epoch
   */
  def getTimestamp(): Long

  /**
   * Get the value of the sensor.
   * 
   * @return the value of the sensor
   */
  def getValue(): T
}
