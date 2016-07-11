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

package io.smartspaces.sandbox.interaction.entity

/**
 * A description of a measurement type, such as temperature or humidity.
 *
 * @author Keith M. Hughes
 */
trait MeasurementTypeDescription extends Displayable {

  /**
   * Get the ID of the measurement type.
   *
   * @return the ID
   */
  def getId(): String

  /**
   * Get the default unit for the measurement type.
   *
   * @return the default unit
   */
  def getDefaultUnit(): MeasurementUnitDescription

  /**
   * Set the default unit for the measurement type.
   *
   * @param defaultUnit
   * 					the default unit
   */
  def setDefaultUnit(defaultUnit: MeasurementUnitDescription): Unit

  /**
   * Get the aliases for the measurement type.
   *
   * @return the aliases
   */
  def getAliases(): Set[String]

  /**
   * Add in a new measurement unit to the measurement type.
   *
   * @param measurementUnit
   *      the measurement unit to add
   */
  def addMeasurementUnit(measurementUnit: MeasurementUnitDescription): Unit
  
  /**
   * Add in a new measurement unit to the measurement type.
   *
   * @return all of the measurement units for this type
   */
  def getAllMeasurementUnits(): List[MeasurementUnitDescription]

}