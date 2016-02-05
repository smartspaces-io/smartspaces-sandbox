/*
 * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.sandbox.service.control.dmx;

import io.smartspaces.SimpleSmartSpacesException;

/**
 * A base class for implementing {@link DmxFixtureData} classes.
 *
 * @author Keith M. Hughes
 */
public class BasicDmxFixtureData implements DmxFixtureData {

  /**
   * The DMX channel where the device resides.
   */
  private final int channel;

  /**
   * The data for the device.
   */
  private final int[] data;

  /**
   * Construct the device DMX data.
   *
   * <p>
   * The data values will all be {2code 0}.
   *
   * @param channel
   *          the DMX channel where the device resides
   * @param length
   *          the length of the DMX channel data
   */
  public BasicDmxFixtureData(int channel, int length) {
    this.channel = channel;
    this.data = new int[length];
  }

  /**
   * Construct the device DMX data.
   *
   * <p>
   * The instance uses the supplied array for its data.
   *
   * @param channel
   *          the DMX channel where the device resides
   * @param data
   *          the initial DMX channel data
   */
  public BasicDmxFixtureData(int channel, int... data) {
    this.channel = channel;
    this.data = data;
  }

  @Override
  public int getChannel() {
    return channel;
  }

  @Override
  public int getLength() {
    return data.length;
  }

  @Override
  public int[] getData() {
    return data;
  }

  @Override
  public void setData(int... newData) {
    if (newData.length < data.length) {
      throw new SimpleSmartSpacesException(String.format(
          "Not enough DMX data in supplied array. Need %d and there is only %d", data.length,
          newData.length));
    }

    System.arraycopy(newData, 0, data, 0, data.length);
  }

  @Override
  public void writeDmxData(DmxControlEndpoint endpoint) {
    endpoint.writeDmxData(channel, data);
  }
}
