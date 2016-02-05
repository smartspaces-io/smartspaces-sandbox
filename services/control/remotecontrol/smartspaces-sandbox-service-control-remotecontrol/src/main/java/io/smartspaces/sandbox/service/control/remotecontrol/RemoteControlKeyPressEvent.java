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

package io.smartspaces.sandbox.service.control.remotecontrol;

/**
 * A key press event that has happened from an
 * {@link RemoteControlCommunicationEndpoint}.
 *
 * @author Keith M. Hughes
 */
public class RemoteControlKeyPressEvent {

  /**
   * The current key press count.
   *
   * <p>
   * This count is starts at 0 every time a new key is pressed and increments by
   * 1 each sample for as long as the key remains pressed.
   */
  private final int currentKeyPressCount;

  /**
   * The designator for the key which has been pressed.
   */
  private final String keyCode;

  /**
   * The identifier for Remote Control which generated the event.
   *
   * <p>
   * Can be {@code null}.
   */
  private final String remoteId;

  /**
   * Construct an event.
   *
   * @param remoteId
   *          the ID for the Remote Control which created the event, can be
   *          {@code null}
   * @param keyCode
   *          the key
   * @param count
   *          the count of the number of times the key has been pressed
   *
   */
  public RemoteControlKeyPressEvent(String remoteId, String keyCode, int count) {
    this.remoteId = remoteId;
    this.keyCode = keyCode;
    this.currentKeyPressCount = count;
  }

  /**
   * Get the ID of the Remote Control which sent the key event.
   *
   * @return the ID of the Remote Control, can be {@code null}
   */
  public String getRemoteId() {
    return remoteId;
  }

  /**
   * Get the key code of the key which was pressed.
   *
   * @return the key code of the key which was pressed
   */
  public String getKeyCode() {
    return keyCode;
  }

  /**
   * Get the current key press count.
   *
   * <p>
   * This count is starts at 0 every time a new key is pressed and increments by
   * 1 each sample for as long as the key remains pressed.
   *
   * @return the current key press count
   */
  public int getCurrentKeyPressCount() {
    return currentKeyPressCount;
  }

  @Override
  public String toString() {
    return "RemoteControlKeyPressEvent [keyCode=" + keyCode + ", currentKeyPressCount="
        + currentKeyPressCount + ", remote=" + remoteId + "]";
  }
}
