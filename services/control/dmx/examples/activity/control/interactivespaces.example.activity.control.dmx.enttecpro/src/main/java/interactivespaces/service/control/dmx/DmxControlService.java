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

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * A service for allowing control of DMX devices.
 *
 * @author Keith M. Hughes
 */
public interface DmxControlService extends SupportedService {

  /**
   * The name for the service.
   */
  String SERVICE_NAME = "control.dmx";

  /**
   * Get a new DMX control endpoint.
   *
   * @param portName
   *          the port for the endpoint
   * @param log
   *          the logger to use
   *
   * @return the DMX control endpoint
   */
  DmxControlEndpoint newDmxControlEndpoint(String portName, Log log);
}