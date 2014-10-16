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

package interactivespaces.sandbox.service.control.remotecontrol;

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * A service for obtaining Remote Control communication endpoints.
 *
 * @author Keith M. Hughes
 */
public interface RemoteControlCommunicationEndpointService extends SupportedService {

  /**
   * The name of the service.
   */
  String SERVICE_NAME = "control.remotecontrol";

  /**
   * Construct a new Remote Control communication endpoint.
   *
   * @param host
   *          the host which is reading the remote endpoint
   * @param port
   *          the port on the remote host
   * @param log
   *          the logger to use
   *
   * @return a Remote Control communication endpoint
   */
  RemoteControlCommunicationEndpoint newNetworkRemoteControlEndpoint(String host, int port, Log log);
}
