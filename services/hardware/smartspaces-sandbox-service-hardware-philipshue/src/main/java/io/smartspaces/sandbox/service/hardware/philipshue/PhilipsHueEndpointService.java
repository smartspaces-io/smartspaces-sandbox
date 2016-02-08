/*
 * Copyright (C) 2015 Keith M. Hughes.
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

package io.smartspaces.sandbox.service.hardware.philipshue;

import io.smartspaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * Service for control of Philips Hue lights.
 * 
 * @author Keith M. Hughes
 */
public interface PhilipsHueEndpointService extends SupportedService {

  /**
   * The name for the service.
   */
  String SERVICE_NAME = "light.philips.hue";

  /**
   * Create a new endpoint.
   * 
   * @param host
   *          the network address of the Philips Hue light
   * @param hueUser
   *          the user for controlling the light
   * @param log
   *          the logger to use
   * 
   * @return the new endpoint
   */
  PhilipsHueEndpoint newEndpoint(String host, String hueUser, Log log);
}
