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

package io.smartspaces.sandbox.service.control.dmx.internal.genericserial;

import io.smartspaces.sandbox.service.control.dmx.DmxControlEndpoint;
import io.smartspaces.sandbox.service.control.dmx.DmxData;
import io.smartspaces.service.comm.serial.SerialCommunicationEndpoint;

import org.apache.commons.logging.Log;

/**
 * A DMX control endpoint which sends a generic protocol over a serial
 * connection.
 *
 * @author Keith M. Hughes
 */
public class GenericSerialDmxControlEndpoint implements DmxControlEndpoint {

  /**
   * The designator for the channel in the generic protocol.
   */
  public static final char GENERIC_PROTOCOL_CHANNEL_IDENTIFIER = 'c';

  /**
   * The designator for the a fixture value in the generic protocol.
   */
  public static final char GENERIC_PROTOCOL_VALUE_IDENTIFIER = 'w';

  /**
   * The communication endpoint for speaking with the DMX controller.
   */
  private SerialCommunicationEndpoint commEndpoint;

  /**
   * Log for the endpoint.
   */
  private final Log log;

  /**
   * Construct a new endpoint.
   *
   * @param commEndpoint
   *          the serial communication endpoint
   * @param log
   *          the logger
   */
  public GenericSerialDmxControlEndpoint(SerialCommunicationEndpoint commEndpoint, Log log) {
    this.commEndpoint = commEndpoint;
    this.log = log;
  }

  @Override
  public void startup() {
    log.info("Starting up DMX serial connection");
    commEndpoint.startup();
  }

  @Override
  public void shutdown() {
    log.info("Shutting down DMX serial connection");

    if (commEndpoint != null) {
      commEndpoint.shutdown();
      commEndpoint = null;
    }
  }

  @Override
  public void writeDmxData(int channel, int... data) {
    StringBuilder builder =
        new StringBuilder().append(channel).append(GENERIC_PROTOCOL_CHANNEL_IDENTIFIER);

    for (int value : data) {
      builder.append(value).append(GENERIC_PROTOCOL_VALUE_IDENTIFIER);
    }

    sendCommand(builder);
  }

  @Override
  public void writeDmxData(DmxData data) {
    data.writeDmxData(this);
  }

  /**
   * end the command over the serial connection.
   *
   * @param commandBuilder
   *          the command builder
   */
  private void sendCommand(StringBuilder commandBuilder) {
    commEndpoint.write(commandBuilder.toString().getBytes());
    commEndpoint.flush();
  }
}
