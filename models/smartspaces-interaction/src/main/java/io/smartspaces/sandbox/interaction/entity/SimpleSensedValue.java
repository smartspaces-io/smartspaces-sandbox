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
public class SimpleSensedValue<T> implements SensedValue<T> {

  /**
   * The name of the value.
   */
  private String valueName;

  /**
   * The type of the value.
   */
  private String valueType;

  /**
   * The value of the sensor.
   */
  private T value;

  /**
   * The timestamp of the value.
   */
  private long timestamp;

  /**
   * Construct a new value.
   * 
   * @param valueName
   *          the name of the value
   * @param valueType
   *          the type of the value
   * @param timestamp
   *          the timestamp of the value
   * @param value
   *          the value
   */
  public SimpleSensedValue(String valueName, String valueType, T value, long timestamp) {
    this.valueName = valueName;
    this.valueType = valueType;
    this.value = value;
    this.timestamp = timestamp;
  }

  @Override
  public String getValueName() {
    return valueName;
  }

  @Override
  public String getValueType() {
    return valueType;
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public long getLastUpdate() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "SimpleSensedValue [valueName=" + valueName + ", valueType=" + valueType + ", value="
        + value + ", timestamp=" + timestamp + "]";
  }
}
