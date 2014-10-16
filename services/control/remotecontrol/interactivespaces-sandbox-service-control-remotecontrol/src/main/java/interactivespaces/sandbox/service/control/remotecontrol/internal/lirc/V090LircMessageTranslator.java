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

package interactivespaces.sandbox.service.control.remotecontrol.internal.lirc;

import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.sandbox.service.control.remotecontrol.RemoteControlKeyPressEvent;

/**
 * A message translator for version 0.9.0+ of the LIRC protocol.
 *
 * @author Keith M. Hughes
 */
public class V090LircMessageTranslator implements LircMessageTranslator {

  /**
   * The number of components in a LIRC message.
   */
  private static final int LIRC_PROTOCOL_MESSAGE_LENGTH = 4;

  /**
   * Position of the key count in a LIRC message.
   */
  private static final int LIRC_PROTOCOL_POSITION_COUNT = 1;

  /**
   * Position of the key code in a LIRC message.
   */
  private static final int LIRC_PROTOCOL_POSITION_KEY_CODE = 2;

  /**
   * Position of the remote ID in a LIRC message.
   */
  private static final int LIRC_PROTOCOL_POSITION_REMOTE_ID = 3;

  /**
   * The delimiter for a LIRC event over a network connection.
   */
  private static final byte LIRC_EVENT_DELIMITER = (byte) 0x0a;

  @Override
  public byte[][] getMessageDelimiters() {
    return new byte[][] { new byte[] { LIRC_EVENT_DELIMITER } };
  }

  @Override
  public RemoteControlKeyPressEvent parseEvent(String eventString) {
    String[] components = eventString.split(" ");
    if (components.length != LIRC_PROTOCOL_MESSAGE_LENGTH) {
      throw new SimpleInteractiveSpacesException(String.format(
          "LIRC Remote Control event does not have enough components: %s", eventString));
    }

    try {
      return new RemoteControlKeyPressEvent(new String(components[LIRC_PROTOCOL_POSITION_REMOTE_ID]), new String(
          components[LIRC_PROTOCOL_POSITION_KEY_CODE]), Integer.parseInt(components[LIRC_PROTOCOL_POSITION_COUNT], 16));
    } catch (NumberFormatException e) {
      throw new SimpleInteractiveSpacesException(String.format(
          "LIRC Remote Control event does not have a number in its second component: %s", e));
    }
  }
}
