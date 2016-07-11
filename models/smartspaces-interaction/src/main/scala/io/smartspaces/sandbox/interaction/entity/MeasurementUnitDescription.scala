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
 * The description of a measurement unit.
 *
 * @author Keith M. Hughes
 */
trait MeasurementUnitDescription extends Displayable {

  /**
   * Get the ID of the measurement unit.
   *
   * @return the measurement unit ID
   */
  def getId(): String

  /**
   * Get the measurement type of the unit.
   *
   * @return the measurement type of the unit
   */
  def getMeasurementType(): MeasurementTypeDescription
}