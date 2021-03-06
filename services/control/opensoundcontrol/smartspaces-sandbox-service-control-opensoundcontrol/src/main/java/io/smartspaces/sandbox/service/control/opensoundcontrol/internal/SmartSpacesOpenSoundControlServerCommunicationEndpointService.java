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

package io.smartspaces.sandbox.service.control.opensoundcontrol.internal;

import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlServerCommunicationEndpoint;
import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlServerCommunicationEndpointService;
import io.smartspaces.service.BaseSupportedService;
import io.smartspaces.service.comm.network.server.UdpServerNetworkCommunicationEndpointService;

import org.apache.commons.logging.Log;

/**
 * A Smart Spaces implementation of an Open Sound Control Server Endpoint
 * service.
 *
 * @author Keith M. Hughes
 */
public class SmartSpacesOpenSoundControlServerCommunicationEndpointService extends
    BaseSupportedService implements OpenSoundControlServerCommunicationEndpointService {

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public OpenSoundControlServerCommunicationEndpoint newUdpEndpoint(int localPort, Log log) {
    UdpServerNetworkCommunicationEndpointService serverService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(
            UdpServerNetworkCommunicationEndpointService.SERVICE_NAME);

    return new SmartSpacesOpenSoundControlServerCommunicationEndpoint(
        serverService.newServer(localPort, log), log);
  }
}
