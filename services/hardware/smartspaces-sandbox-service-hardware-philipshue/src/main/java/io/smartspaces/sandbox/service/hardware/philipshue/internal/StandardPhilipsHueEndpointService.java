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

package io.smartspaces.sandbox.service.hardware.philipshue.internal;

import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpoint;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpointService;
import io.smartspaces.service.BaseSupportedService;
import io.smartspaces.service.web.HttpClientRestWebClient;

import org.apache.commons.logging.Log;

/**
 * The standard service for working with Philips Hue lights.
 * 
 * @author Keith M. Hughes
 */
public class StandardPhilipsHueEndpointService extends BaseSupportedService implements
    PhilipsHueEndpointService {

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public PhilipsHueEndpoint newEndpoint(String host, String hueUser, Log log) {
    HttpClientRestWebClient client = new HttpClientRestWebClient();

    return new StandardPhilipsHueEndpoint(host, hueUser, client, log, getSpaceEnvironment());
  }
}
