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

package interactivespaces.service.control.dmx.internal;

import interactivespaces.service.comm.serial.SerialCommunicationEndpoint;
import interactivespaces.service.control.dmx.DmxControlEndpoint;
import interactivespaces.util.concurrency.CancellableLoop;

import org.apache.commons.logging.Log;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Keith M. Hughes
 */
public class SerialDmxControlEndpoint implements DmxControlEndpoint {

  /**
   * The communication endpoint for speaking with the XBee.
   */
  private SerialCommunicationEndpoint commEndpoint;

  /**
   * The executor service for running the reader loop.
   */
  private final ScheduledExecutorService executorService;

  /**
   * Log for the endpoint.
   */
  private final Log log;

  /**
   * The current DMX channel that will be written to.
   *
   */
  private int currentChannel;

  /**
   * Loop for reading info from the XBee
   */
  private CancellableLoop readerLoop;

  public SerialDmxControlEndpoint(SerialCommunicationEndpoint commEndpoint, ScheduledExecutorService executorService,
      Log log) {
    this.commEndpoint = commEndpoint;
    this.executorService = executorService;
    this.log = log;
  }

  @Override
  public void startup() {
    log.info("Starting up XBee connection");
    commEndpoint.startup();

    readerLoop = new CancellableLoop() {
      @Override
      protected void loop() throws InterruptedException {
        // readFrame();
      }

      @Override
      protected void handleException(Exception e) {
        log.error("Error while reading DMX control frame", e);
      }

      @Override
      protected void cleanup() {
        log.info("DMX control serial connection read loop shut down");
      }
    };

    executorService.submit(readerLoop);
  }

  @Override
  public void shutdown() {
    log.info("Shutting down XBee connection");

    if (readerLoop != null) {
      readerLoop.cancel();

      readerLoop = null;
    }

    if (commEndpoint != null) {
      commEndpoint.shutdown();
      commEndpoint = null;
    }
  }

  /* (non-Javadoc)
   * @see interactivespaces.service.control.dmx.internal.DmxControlEndpoint#setValue(int, int)
   */
  @Override
  public void setValue(int channel, int value) {
    StringBuilder builder = new StringBuilder().append(channel).append('c').append(value).append('w');
    sendCommand(builder);

    this.currentChannel = channel;
  }

  /* (non-Javadoc)
   * @see interactivespaces.service.control.dmx.internal.DmxControlEndpoint#setValue(int)
   */
  @Override
  public void setValue(int value) {
    StringBuilder builder = new StringBuilder().append(value).append('w');
    sendCommand(builder);
  }

  /* (non-Javadoc)
   * @see interactivespaces.service.control.dmx.internal.DmxControlEndpoint#setChannel(int)
   */
  @Override
  public void setChannel(int newChannel) {
    StringBuilder builder = new StringBuilder().append(newChannel).append('c');
    sendCommand(builder);

    this.currentChannel = newChannel;
  }

  /* (non-Javadoc)
   * @see interactivespaces.service.control.dmx.internal.DmxControlEndpoint#getCurrentChannel()
   */
  @Override
  public int getCurrentChannel() {
    return currentChannel;
  }

  private void sendCommand(StringBuilder builder) {
    commEndpoint.write(builder.toString().getBytes());
    commEndpoint.flush();
  }
}
