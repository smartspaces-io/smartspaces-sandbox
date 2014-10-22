/*
 * Copyright (C) 2014 Google Inc.
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

package interactivespaces.sandbox.service.control.dmx;

/**
 * Data for a DMX device.
 *
 * <p>
 * The data array is in the order in which the data will be written to ascending channels of the DMX
 * stream.
 *
 * <ul>
 * <li>{@code data[0] to channel</li>
 * <li>{@code data[1] to channel + 1</li>
 * <li>...</li>
 * </ul>
 *
 * @author Keith M. Hughes
 */
public interface DmxFixtureData extends DmxData {

  /**
   * Get the channel for the data.
   *
   * @return the channel for the data
   */
  int getChannel();

  /**
   * Get the number of data items needed for the device.
   *
   * @return the number of data items needed for the device
   */
  int getLength();

  /**
   * Get the data for the DMX channel.
   *
   * @return the data for the DMX channel
   */
  int[] getData();

  /**
   * Set the data for the DMX channel.
   *
   * <p>
   * The new data must be at least as long as the data needed for the device. Extra data will be ignored.
   *
   * @param newData
   *          the new data for the DMX channel
   */
  void setData(int... newData);
}
