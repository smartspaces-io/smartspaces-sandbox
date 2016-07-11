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

import scala.collection.mutable.ArrayBuffer

/**
 * A simple implementation of the measurement type description.
 *
 * @author Keith M. Hughes
 */
case class SimpleMeasurementTypeDescription(private val id: String, private val displayName: String, private val displayDescription: String, private val aliases: Set[String]) extends MeasurementTypeDescription {

  /**
   * The measurement units for this type.
   */
  private val measurementUnits: ArrayBuffer[MeasurementUnitDescription] = new ArrayBuffer
  
  /**
   * The default unit for the measurement type.
   */
  private var defaultUnit: MeasurementUnitDescription = null
 
  override def getId(): String = {
    id
  }

  override def getDisplayName(): String = {
    displayName
  }

  override def getDisplayDescription(): String = {
    displayDescription
  }

  override def getDefaultUnit(): MeasurementUnitDescription = {
    defaultUnit
  }
  
  override def setDefaultUnit(defaultUnit: MeasurementUnitDescription): Unit = {
    this.defaultUnit = defaultUnit
  }

  override def getAliases(): Set[String] = {
    aliases
  }
  
  override def addMeasurementUnit(measurementUnit: MeasurementUnitDescription): Unit = {
    measurementUnits += measurementUnit
  }
  
  override def getAllMeasurementUnits(): List[MeasurementUnitDescription] = {
    measurementUnits.toList
  }
  
  override def getMeasurementUnit(id: String): Option[MeasurementUnitDescription] = {
    measurementUnits.find(_.getId == id)
  }
}