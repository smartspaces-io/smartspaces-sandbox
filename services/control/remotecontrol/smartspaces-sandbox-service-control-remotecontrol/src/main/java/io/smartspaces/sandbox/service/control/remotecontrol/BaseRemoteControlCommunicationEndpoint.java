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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;

/**
 * A support class for implementing {@link RemoteControlCommunicationEndpoint}
 * instances.
 *
 * @author Keith M. Hughes
 */
public abstract class BaseRemoteControlCommunicationEndpoint implements
    RemoteControlCommunicationEndpoint {

  /**
   * The listeners for the endpoint.
   */
  private List<RemoteControlCommunicationEndpointEndpointListener> listeners =
      new CopyOnWriteArrayList<RemoteControlCommunicationEndpointEndpointListener>();

  /**
   * The logger to use.
   */
  private Log log;

  /**
   * Construct the base endpoint.
   *
   * @param log
   *          the logger to use
   */
  public BaseRemoteControlCommunicationEndpoint(Log log) {
    this.log = log;
  }

  @Override
  public void addListener(RemoteControlCommunicationEndpointEndpointListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(RemoteControlCommunicationEndpointEndpointListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notify all listeners that a keypress event has happened.
   *
   * @param event
   *          the keypress event
   */
  protected void notifyKeyPress(RemoteControlKeyPressEvent event) {
    for (RemoteControlCommunicationEndpointEndpointListener listener : listeners) {
      try {
        listener.onRemoteControlKeyPressEvent(this, event);
      } catch (Exception e) {
        log.error("Error while processing remote control keypress event handler", e);
      }
    }
  }

  /**
   * Get the logger.
   *
   * @return the logger
   */
  protected Log getLog() {
    return log;
  }
}
