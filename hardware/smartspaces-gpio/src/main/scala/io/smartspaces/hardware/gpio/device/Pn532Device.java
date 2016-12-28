/**
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

package io.smartspaces.hardware.gpio.device;

import io.smartspaces.resource.managed.ManagedResource;

/**
 * An interface to a PN532 NFC/RFID device.
 * 
 * @author Keith M. Hughes
 */
public interface Pn532Device extends ManagedResource {

  /**
   * Get the firmware version of the PN532.
   * 
   * @return a tuple with the IC, Ver, Rev, and Support values, in that order
   */
  byte[] getFirmwareVersion();

  /**
   * Configure the PN532 to read MiFare cards.
   */
  void useSamConfiguration();

  /**
   * Wait for a card to be scanned and get its UUID.
   * 
   * <p>
   * Expects ISO14443A cards and a timeout of 1 second.
   * 
   * @return the UUID of a card, if one scanned, or {@code null} is no card is
   *         read before the timeout
   */
  byte[] readPassiveTarget();

  /**
   * Wait for a card to be scanned and get its UUID.
   * 
   * @param cardBaud
   *          the baud for the desired card
   * @param timeout
   *          the timeout to wait for a card, in milliseconds
   * 
   * @return the UUID of a card, if one scanned, or {@code null} is no card is
   *         read before the timeout
   */
  byte[] readPassiveTarget(byte cardBaud, long timeout);

}