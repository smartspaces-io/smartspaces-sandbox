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

import interactivespaces.master.api.messages.MasterApiMessages;
import interactivespaces.sandbox.service.interactivespaces.master.InteractiveSpacesMasterApiMessageHandler;
import interactivespaces.sandbox.service.interactivespaces.master.InteractiveSpacesMasterClient;
import interactivespaces.service.web.WebSocketHandler;
import interactivespaces.service.web.client.WebSocketClient;
import interactivespaces.service.web.client.WebSocketClientService;
import interactivespaces.util.data.json.JsonBuilder;
import interactivespaces.util.data.json.StandardJsonBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of the Interactive Spaces Master API Client.
 *
 * @author Keith M. Hughes
 */
public class NativeInteractiveSpacesMasterClient implements InteractiveSpacesMasterClient {

  /**
   * Web socket URI prefix for the master API.
   */
  public static final String MASTERAPI_WEBSOCKET_URI_PREFIX = "/masterapi/websocket";

  /**
   * The web socket client for communicating with the master.
   */
  private final WebSocketClient webSocketClient;

  /**
   * Log for the endpoint.
   */
  private final Log log;

  /**
   * The map of request IDs to their requests.
   */
  private final Map<String, Request> requestIdToRequest = Maps.newConcurrentMap();

  /**
   * The generator for request IDs.
   */
  private final AtomicLong requestIdGenerator = new AtomicLong();

  /**
   * The URL the client is pointing to.
   */
  private String masterAddress;

  /**
   * The list of callbacks for event messages from the master.
   */
  private List<InteractiveSpacesMasterApiMessageHandler> eventHandlers = Lists.newCopyOnWriteArrayList();

  /**
   * Construct a new endpoint.
   *
   * @param webSocketClientService
   *          the web socket client service
   * @param masterApiHost
   *          host for the Master API
   * @param masterApiPort
   *          port for the Master API
   * @param log
   *          the logger
   */
  public NativeInteractiveSpacesMasterClient(WebSocketClientService webSocketClientService, String masterApiHost,
      int masterApiPort, Log log) {
    masterAddress = "ws://" + masterApiHost + ":" + masterApiPort + MASTERAPI_WEBSOCKET_URI_PREFIX;
    this.webSocketClient = webSocketClientService.newWebSocketClient(masterAddress, new WebSocketHandler() {

      @SuppressWarnings("unchecked")
      @Override
      public void onReceive(Object data) {
        handleMasterMessage((Map<String, Object>) data);
      }

      @Override
      public void onConnect() {
        handleMasterConnect();
      }

      @Override
      public void onClose() {
        handleMasterClose();
      }
    }, log);
    this.log = log;

  }

  @Override
  public void startup() {
    log.info("Starting up Interactive Spaces Master API Client");
    webSocketClient.startup();
  }

  @Override
  public void shutdown() {
    log.info("Shutting down Interactive Spaces Master API Client");
    webSocketClient.shutdown();
  }

  @Override
  public String getMasterAddress() {
    return masterAddress;
  }

  @Override
  public void getAllLiveActivities(InteractiveSpacesMasterApiMessageHandler callback) {
    getAllLiveActivities(null, callback);
  }

  @Override
  public void getAllLiveActivities(String filter, InteractiveSpacesMasterApiMessageHandler callback) {
    sendApiCall(newFilteredSearchCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_ALL, filter, callback));
  }

  @Override
  public void getLiveActivityFullView(String typedId, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_VIEW_FULL, typedId, callback);
  }

  @Override
  public void getLiveActivityConfiguration(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_CONFIGURATION_GET, id, callback);
  }

  @Override
  public void setLiveActivityConfiguration(String id, Map<String, String> newConfiguration,
      InteractiveSpacesMasterApiMessageHandler callback) {
    JsonBuilder call =
        newEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_CONFIGURATION_SET, id, callback);
    call.newObject(MasterApiMessages.MASTER_API_PARAMETER_NAME_ENTITY_CONFIG);
    for (Entry<String, String> entry : newConfiguration.entrySet()) {
      call.put(entry.getKey(), entry.getValue());
    }
    sendApiCall(call);
  }

  @Override
  public void startupLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_STARTUP, id, callback);
  }

  @Override
  public void activateLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_ACTIVATE, id, callback);
  }

  @Override
  public void deactivateLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_DEACTIVATE, id, callback);
  }

  @Override
  public void shutdownLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_SHUTDOWN, id, callback);
  }

  @Override
  public void getLiveActivityGroupFullView(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_VIEW_FULL, id, callback);
  }

  @Override
  public void getAllLiveActivityGroups(InteractiveSpacesMasterApiMessageHandler callback) {
    getAllLiveActivityGroups(null, callback);
  }

  @Override
  public void getAllLiveActivityGroups(String filter, InteractiveSpacesMasterApiMessageHandler callback) {
    JsonBuilder builder =
        newFilteredSearchCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_ALL, filter, callback);

    sendApiCall(builder);
  }

  @Override
  public void startupLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_STARTUP, id, callback);
  }

  @Override
  public void activateLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_ACTIVATE, id, callback);
  }

  @Override
  public void deactivateLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_DEACTIVATE, id, callback);
  }

  @Override
  public void shutdownLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_LIVE_ACTIVITY_GROUP_SHUTDOWN, id, callback);
  }

  @Override
  public void getAllSpaces(InteractiveSpacesMasterApiMessageHandler callback) {
    getAllSpaces(null, callback);
  }

  @Override
  public void getSpaceFullView(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_VIEW_FULL, id, callback);
  }

  @Override
  public void getAllSpaces(String filter, InteractiveSpacesMasterApiMessageHandler callback) {
    JsonBuilder builder = newFilteredSearchCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_ALL, filter, callback);

    sendApiCall(builder);
  }

  @Override
  public void startupSpace(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_STARTUP, id, callback);
  }

  @Override
  public void activateSpace(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_ACTIVATE, id, callback);
  }

  @Override
  public void deactivateSpace(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_DEACTIVATE, id, callback);
  }

  @Override
  public void shutdownSpace(String id, InteractiveSpacesMasterApiMessageHandler callback) {
    sendEntityCall(MasterApiMessages.MASTER_API_COMMAND_SPACE_SHUTDOWN, id, callback);
  }

  @Override
  public void addEventHandler(InteractiveSpacesMasterApiMessageHandler handler) {
    eventHandlers.add(handler);
  }

  @Override
  public void removeEventHandler(InteractiveSpacesMasterApiMessageHandler handler) {
    eventHandlers.remove(handler);
  }

  /**
   * Send a method call to an entity on the master.
   *
   * @param method
   *          the method to be done
   * @param entityId
   *          the ID of the entity that the method should be done on
   * @param callback
   *          the callback to be run
   */
  private void sendEntityCall(String method, String entityId, InteractiveSpacesMasterApiMessageHandler callback) {
    sendApiCall(newEntityCall(method, entityId, callback));
  }

  /**
   * Create a search command message.
   *
   * @param searchCommand
   *          the search command
   * @param filter
   *          the filter for the search, can be {@code null}
   * @param callback
   *          the callback handler for the message
   *
   * @return a message builder in the data section of the message
   */
  private JsonBuilder newFilteredSearchCall(String searchCommand, String filter,
      InteractiveSpacesMasterApiMessageHandler callback) {
    JsonBuilder builder = newReponseCall(searchCommand, callback);

    if (filter != null) {
      builder.put(MasterApiMessages.MASTER_API_PARAMETER_NAME_FILTER, filter);
    }

    return builder;
  }

  /**
   * Create a new entity call.
   *
   * @param command
   *          the command for the entity
   * @param entityId
   *          the ID for the entity
   * @param callback
   *          the callback to be executed when the method response comes back
   *
   * @return the request message in the data section
   */
  private JsonBuilder
      newEntityCall(String command, String entityId, InteractiveSpacesMasterApiMessageHandler callback) {
    JsonBuilder builder = newReponseCall(command, callback);
    builder.put(MasterApiMessages.MASTER_API_PARAMETER_NAME_ENTITY_ID, entityId);

    return builder;
  }

  /**
   * Create a new call that requires a response.
   *
   * @param command
   *          the command to be run
   * @param callback
   *          the callback to be executed when the method response comes back
   *
   * @return the builder in the data section
   */
  private JsonBuilder newReponseCall(String command, InteractiveSpacesMasterApiMessageHandler callback) {
    Request request = newRequest(callback);

    JsonBuilder builder = new StandardJsonBuilder();
    builder.put(MasterApiMessages.MASTER_API_MESSAGE_ENVELOPE_TYPE, command);
    builder.put(MasterApiMessages.MASTER_API_MESSAGE_ENVELOPE_REQUEST_ID, request.getRequestId());
    builder.newObject(MasterApiMessages.MASTER_API_MESSAGE_ENVELOPE_DATA);

    return builder;
  }

  /**
   * Create a new request.
   *
   * @param callback
   *          the callback for the message response
   *
   * @return the request
   */
  private Request newRequest(InteractiveSpacesMasterApiMessageHandler callback) {
    String requestId = newRequestId();
    Request request = new Request(requestId, callback);
    requestIdToRequest.put(requestId, request);
    return request;
  }

  /**
   * Send a call to the API.
   *
   * @param builder
   *          the builder containing the message to send
   */
  private void sendApiCall(JsonBuilder builder) {
    webSocketClient.writeDataAsJson(builder.build());
  }

  /**
   * Handle the connection response from the Master.
   */
  private void handleMasterConnect() {
    log.info("Master API Client is connected to the Interactive Spaces master");
  }

  /**
   * handle a connection close from the Master.
   */
  private void handleMasterClose() {
    log.info("Master API Client is no longer connected to the Interactive Spaces master");
  }

  /**
   * Handle a response from the master.
   *
   * @param message
   *          the response
   */
  private void handleMasterMessage(Map<String, Object> message) {
    String requestId = (String) message.get(MasterApiMessages.MASTER_API_MESSAGE_ENVELOPE_REQUEST_ID);
    if (requestId != null) {
      Request request = requestIdToRequest.remove(requestId);
      if (request != null) {
        request.responseReceived(message);
      } else {
        log.warn(String.format("Received response with unknown request ID %s", requestId));
      }
    } else {
      handleEventMessage(message);
    }
  }

  /**
   * Handle an event message.
   *
   * @param message
   *          the message to handle
   */
  private void handleEventMessage(Map<String, Object> message) {
    for (InteractiveSpacesMasterApiMessageHandler handler : eventHandlers) {
      try {
        handler.onMasterApiMessage(this, message);
      } catch (Throwable e) {
        log.error("Error handling Master API message", e);
      }
    }
  }

  /**
   * Generate a new request ID.
   *
   * @return a new request ID
   */
  private String newRequestId() {
    return Long.toHexString(requestIdGenerator.getAndIncrement());
  }

  @Override
  public String toString() {
    return "NativeInteractiveSpacesMasterClient [masterAddress=" + getMasterAddress() + "]";
  }

  /**
   * A request which has gone to the master.
   *
   * @author Keith M. Hughes
   */
  private class Request {

    /**
     * The ID of the request which went to the master.
     */
    private String requestId;

    /**
     * The callback to execute when the call comes back.
     */
    private InteractiveSpacesMasterApiMessageHandler callback;

    /**
     * Construct a new request.
     *
     * @param requestId
     *          the ID of the request which went to the master
     * @param callback
     *          the callback to execute when the call comes back
     */
    public Request(String requestId, InteractiveSpacesMasterApiMessageHandler callback) {
      this.requestId = requestId;
      this.callback = callback;
    }

    /**
     * Get the request ID for the call.
     *
     * @return the request ID for the call
     */
    public String getRequestId() {
      return requestId;
    }

    /**
     * Handle the response received.
     *
     * @param response
     *          the response
     */
    public void responseReceived(Map<String, Object> response) {
      if (callback != null) {
        callback.onMasterApiMessage(NativeInteractiveSpacesMasterClient.this, response);
      }
    }
  }
}
