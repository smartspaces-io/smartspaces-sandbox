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

import io.smartspaces.util.resource.ManagedResource;

/**
 * An endpoint for controlling a DMX device.
 *
 * <p>
 * DMX endpoints are stateful. This means that each write will only modify what
 * is specified in the write data and will leave all other channels at their
 * previous value.
 *
 * @author Keith M. Hughes
 */
public interface DmxControlEndpoint extends ManagedResource {

  /**
   * The minimum value for a DMX channel.
   */
  int DMX_CHANNEL_MINIMUM = 1;

  /**
   * The maximum value for a DMX channel.
   */
  int DMX_CHANNEL_MAXIMUM = 512;

  /**
   * The minimum value for a DMX channel's value.
   */
  int DMX_VALUE_MINIMUM = 0;

  /**
   * The maximum value for a DMX channel's value.
   */
  int DMX_VALUE_MAXIMUM = 255;

  /**
   * Write DMX data onto the channel.
   *
   * @param channel
   *          the DMX channel to write on
   * @param data
   *          the data to write
   */
  void writeDmxData(int channel, int... data);

  /**
   * Write DMX data to the endpoint.
   *
   * @param data
   *          the data
   */
  void writeDmxData(DmxData data);
}
