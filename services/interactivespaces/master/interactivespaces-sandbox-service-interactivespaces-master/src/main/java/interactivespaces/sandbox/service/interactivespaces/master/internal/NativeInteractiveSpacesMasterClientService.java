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

package interactivespaces.sandbox.service.interactivespaces.master.internal;

import interactivespaces.sandbox.service.interactivespaces.master.InteractiveSpacesMasterClient;
import interactivespaces.sandbox.service.interactivespaces.master.InteractiveSpacesMasterClientService;
import interactivespaces.service.BaseSupportedService;
import interactivespaces.service.web.client.WebSocketClientService;

import org.apache.commons.logging.Log;

/**
 * An implementation of the service for obtaining Interactive Spaces Master API clients.
 *
 * @author Keith M. Hughes
 */
public class NativeInteractiveSpacesMasterClientService extends BaseSupportedService implements
    InteractiveSpacesMasterClientService {

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public InteractiveSpacesMasterClient newMasterClient(String masterApiHost, int masterApiPort, Log log) {
    WebSocketClientService clientService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(WebSocketClientService.SERVICE_NAME);

    return new NativeInteractiveSpacesMasterClient(clientService, masterApiHost, masterApiPort, log);
  }
}
