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

package interactivespaces.service.control.dmx;

import interactivespaces.util.resource.ManagedResource;

/**
 * An endpoint for controlling a DMX device.
 *
 * @author Keith M. Hughes
 */
public interface DmxControlEndpoint extends ManagedResource {

  /**
   * Set the value of a given channel.
   *
   * <p>
   * This method does not change the channel for {@link #setValue(int)}.
   *
   * @param channel
   *          the channel to set
   * @param value
   *          the value for the channel
   */
  void setValue(int channel, int value);

  /**
   * Send the supplied value to the most recently set channel.
   *
   * @param value
   *          the value to send
   */
  void setValue(int value);

  /**
   * Set the supplied channel to use for {@link #setValue(int)}.
   *
   * @param newChannel
   *          the new channel
   */
  void setChannel(int newChannel);

  /**
   * Get the current DMX channel the endpoint is addressing.
   *
   * @return the current DMX channel
   */
  int getCurrentChannel();
}