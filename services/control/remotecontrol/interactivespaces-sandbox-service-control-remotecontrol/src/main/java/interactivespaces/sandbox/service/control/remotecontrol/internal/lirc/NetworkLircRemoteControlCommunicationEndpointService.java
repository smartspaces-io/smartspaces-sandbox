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

import interactivespaces.InteractiveSpacesException;
import interactivespaces.sandbox.service.control.remotecontrol.RemoteControlCommunicationEndpoint;
import interactivespaces.sandbox.service.control.remotecontrol.RemoteControlCommunicationEndpointService;
import interactivespaces.service.BaseSupportedService;
import interactivespaces.service.comm.network.client.TcpClientNetworkCommunicationEndpointService;

import org.apache.commons.logging.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A service for creating {@link RemoteControlCommunicationEndpoint} instances which use the network for communication.
 *
 * @author Keith M. Hughes
 */
public class NetworkLircRemoteControlCommunicationEndpointService extends BaseSupportedService implements
    RemoteControlCommunicationEndpointService {

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public RemoteControlCommunicationEndpoint newNetworkRemoteControlEndpoint(String host, int port, Log log) {
    TcpClientNetworkCommunicationEndpointService networkService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(
            TcpClientNetworkCommunicationEndpointService.SERVICE_NAME);

    InetAddress hostAddress;
    try {
      hostAddress = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      throw new InteractiveSpacesException(String.format(
          "Could not create network address for %s for LIRC IR communication endpoint", host), e);
    }

    return new NetworkLircRemoteControlCommunicationEndpoint(hostAddress, port, networkService, log);
  }
}
