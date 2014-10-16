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

import interactivespaces.sandbox.service.control.remotecontrol.BaseRemoteControlCommunicationEndpoint;
import interactivespaces.sandbox.service.control.remotecontrol.RemoteControlCommunicationEndpoint;
import interactivespaces.service.comm.network.client.TcpClientNetworkCommunicationEndpoint;
import interactivespaces.service.comm.network.client.TcpClientNetworkCommunicationEndpointListener;
import interactivespaces.service.comm.network.client.TcpClientNetworkCommunicationEndpointService;

import org.apache.commons.logging.Log;
import org.jboss.netty.util.CharsetUtil;

import java.net.InetAddress;

/**
 * An {@link RemoteControlCommunicationEndpoint} which uses the LIRC network connection.
 *
 * @author Keith M. Hughes
 */
public class NetworkLircRemoteControlCommunicationEndpoint extends BaseRemoteControlCommunicationEndpoint {

  /**
   * The service for obtaining a TCP client.
   */
  private TcpClientNetworkCommunicationEndpointService networkService;

  /**
   * The network host which is running the LIRC server.
   */
  private InetAddress lircHost;

  /**
   * The port on the network host which is running the LIRC server.
   */
  private int lircPort;

  /**
   * The network communication endpoint to the LIRC server.
   */
  private TcpClientNetworkCommunicationEndpoint<String> networkEndpoint;

  /**
   * The translator for messages from LIRC.
   */
  private LircMessageTranslator messageTranslater = new V090LircMessageTranslator();

  /**
   * Construct an endpoint.
   *
   * @param lircHost
   *          the network host which is running the LIRC server
   * @param lircPort
   *          the port on the network host for the LIRC server
   * @param networkService
   *          the networking service for getting the connection to LIRC
   * @param log
   *          the logger to use
   */
  public NetworkLircRemoteControlCommunicationEndpoint(InetAddress lircHost, int lircPort,
      TcpClientNetworkCommunicationEndpointService networkService, Log log) {
    super(log);

    this.lircHost = lircHost;
    this.lircPort = lircPort;
    this.networkService = networkService;
  }

  @Override
  public void startup() {
    networkEndpoint =
        networkService.newStringClient(messageTranslater.getMessageDelimiters(), CharsetUtil.ISO_8859_1, lircHost,
            lircPort, getLog());

    networkEndpoint.addListener(new TcpClientNetworkCommunicationEndpointListener<String>() {
      @Override
      public void onTcpResponse(TcpClientNetworkCommunicationEndpoint<String> endpoint, String response) {
        handleKeypressNotification(response);
      }
    });
    networkEndpoint.startup();
  }

  @Override
  public void shutdown() {
    if (networkEndpoint != null) {
      networkEndpoint.shutdown();
      networkEndpoint = null;
    }
  }

  /**
   * Handle a keypress notification from LIRC.
   *
   * @param notification
   *          the keypress string from LIRC
   */
  private void handleKeypressNotification(String notification) {
    notifyKeyPress(messageTranslater.parseEvent(notification));
  }
}
