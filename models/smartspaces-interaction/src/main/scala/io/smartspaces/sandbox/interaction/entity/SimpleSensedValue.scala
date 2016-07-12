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
 * A support class for sensed values.
 *
 * @param <T>
 *          the type of the value
 *
 * @author Keith M. HUghes
 */
class SimpleSensedValue[T <: Any](sensor: SensorEntityDescription, valueName: String, valueType: String,
    value: T, timestamp: Long) extends SensedValue[T] {

  override def getSensor(): SensorEntityDescription = {
    sensor
  }

  override def getName(): String = {
    return valueName;
  }

  override def getType(): String = {
    valueType
  }

  override def getValue(): T = {
    value
  }

  override def getTimestamp(): Long = {
    timestamp;
  }

  override def toString() = {
    "SimpleSensedValue [sensor=" + sensor + ", valueName=" + valueName + ", valueType=" +
      valueType + ", value=" + value + ", timestamp=" + timestamp + "]"
  }
}