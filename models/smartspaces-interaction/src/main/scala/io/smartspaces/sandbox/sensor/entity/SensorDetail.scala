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

package io.smartspaces.sandbox.sensor.entity

/**
 * Details about a sensor.
 * 
 * @author Keith M. Hughes
 */
trait SensorDetail extends Displayable {
  
  /**
   * The ID of the sensor detail.
   */
  val id: String
  
  /**
   * The external ID of the sensor detail.
   */
  val externalId: String
    
  /**
   * The time limit on when a sensor update should happen, in milliseconds
   */
  val sensorUpdateTimeLimit: Option[Long]
    
  /**
   * The time limit on when a sensor heartbeat update should happen, in milliseconds
   */
  val sensorHeartbeatUpdateTimeLimit: Option[Long]

  /**
   * Add in a new channel detail to the sensor detail.
   *
   * @param sensorChannelDetail
   *      the channel detail to add
   */
  def addSensorChannelDetail(sensorChannelDetail: SensorChannelDetail): Unit
  
  /**
   * Get all channel details for this sensor detail.
   *
   * @return all channel details for this sensor detail
   */
  def getAllSensorChannelDetails(): List[SensorChannelDetail]

  /**
   * Get a sensor channel detail of this sensor detail.
   * 
   * <p>
   * The channel must be a channel of this sensor detail to be found. Channel names are local
   * to the detail they are contained in.
   * 
   * @param id
   *     the ID of the channel detail
   *
   * @return the measurement unit
   */
  def getSensorChannelDetail(id: String): Option[SensorChannelDetail]
}