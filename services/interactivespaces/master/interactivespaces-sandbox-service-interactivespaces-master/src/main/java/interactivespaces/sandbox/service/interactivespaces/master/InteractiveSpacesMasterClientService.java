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

package interactivespaces.sandbox.service.interactivespaces.master;

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * A service for obtaining clients to work with the Interactive Spaces Master API.
 *
 * @author Keith M. Hughes
 */
public interface InteractiveSpacesMasterClientService extends SupportedService {

  /**
   * The name of the service.
   */
  String SERVICE_NAME = "interactivespaces.master.client";

  /**
   * Create a new Interactive Spaces Master client.
   *
   * <p>
   * This connects to the web socket Master API.
   *
   * @param masterApiHost
   *        the hostname for the machine running the Interactive Spaces Master API
   * @param masterApiPort
   *        the port on the machine running the Interactive Spaces Master API
   * @param log
   *          the logger for this connection
   *
   * @return the new client
   */
  InteractiveSpacesMasterClient newMasterClient(String masterApiHost, int masterApiPort, Log log);
}
