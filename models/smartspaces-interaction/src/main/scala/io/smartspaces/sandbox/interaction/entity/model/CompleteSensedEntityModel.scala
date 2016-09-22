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

import io.smartspaces.sandbox.interaction.entity.SensorRegistry;

/**
 * A collection of sensed entity models.
 * 
 * @author Keith M. Hughes
 */
trait CompleteSensedEntityModel {

  /**
   * Prepare the collection.
   * 
   * <p>
   * This will include building sensing models.
   */
  def prepare(): Unit

  /**
   * Get the sensor entity model for a given entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model
   */
  def getSensorEntityModel(id: String): Option[SensorEntityModel]

  /**
   * Get all sensor entity models in the collection.
   * 
   * @return the models
   */
  def getAllSensorEntityModels(): List[SensorEntityModel]

  /**
   * Get the sensed entity model for a given entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model
   */
  def getSensedEntityModel(id: String): Option[SensedEntityModel]

  /**
   * Get all sensed entity models in the collection.
   * 
   * @return the models
   */
  def getAllSensedEntityModels(): List[SensedEntityModel]

  /**
   * Get the model for a given physical space entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model
   */
  def getPhysicalSpaceSensedEntityModel(id: String): Option[PhysicalSpaceSensedEntityModel]

  /**
   * Get all physical space models in the collection.
   * 
   * @return the models
   */
   def getAllPhysicalSpaceSensedEntityModels(): List[PhysicalSpaceSensedEntityModel]

  /**
   * Get the model for a given person entity ID.
   * 
   * @param id
   *          the ID of the entity
   * 
   * @return the model
   */
  def getPersonSensedEntityModel(id: String ): Option[PersonSensedEntityModel]

  /**
   * Get all person models in the collection.
   * 
   * @return the models
   */
   def getAllPersonSensedEntityModels(): List[PersonSensedEntityModel]

  /**
   * Get the model for a given marked entity ID.
   * 
   * @param markerId
   *          the marker ID associated with the entity
   * 
   * @return the model
   */
   def getMarkedSensedEntityModel(markerId: String ): Option[PersonSensedEntityModel]

  /**
   * The sensor registry for the collection.
   */
  val sensorRegistry: SensorRegistry 
  
  /**
   * Perform an operations within a read transaction.
   * 
   * <p>
   * Multiple readers can run at the same time.
   *
   * @param transaction
   *          the code to run inside the transaction
   */
  def doReadTransaction(transaction: () => Unit): Unit
  
  /**
   * Perform an operations within a read transaction.
   * 
   * <p>
   * Only one writer can run at a time.
   *
   * @param transaction
   *          the code to run inside the transaction
   */
  def doWriteTransaction(transaction: () => Unit): Unit

}