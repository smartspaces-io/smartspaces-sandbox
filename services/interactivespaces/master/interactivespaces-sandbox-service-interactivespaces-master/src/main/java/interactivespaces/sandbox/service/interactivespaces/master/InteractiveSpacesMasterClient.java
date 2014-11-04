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

import interactivespaces.util.resource.ManagedResource;

/**
 * A client for interactive with the Interactive Spaces Master API.
 *
 * <p>
 * Filters use OGNL. The OGNL expressions must return true or false. For details on OGNL see
 * <a href="http://commons.apache.org/proper/commons-ognl/language-guide.html">the OGNL reference</a>.
 *
 * @author Keith M. Hughes
 */
public interface InteractiveSpacesMasterClient extends ManagedResource {

  /**
   * Get the address of the master this is a client for.
   *
   * @return the address of the master
   */
  String getMasterAddress();

  /**
   * Get all live activities.
   *
   * @param callback
   *          the callback to run after the response
   */
  void getAllLiveActivities(InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Get all live activities that match the specified filter.
   *
   * <p>
   * A {@code null} filter returns all live activities.
   *
   * @param filter
   *          the filter for matching, can be {@code null}
   * @param callback
   *          the callback to run after the response
   */
  void getAllLiveActivities(String filter, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Start up a live activity.
   *
   * @param id
   *          the ID of the live activity to start up
   * @param callback
   *          the callback for the call
   */
  void startupLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Activate a live activity.
   *
   * @param id
   *          the ID of the live activity to activate
   * @param callback
   *          the callback for the call
   */
  void activateLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Deactivate a live activity.
   *
   * @param id
   *          the ID of the live activity to deactivate
   * @param callback
   *          the callback for the call
   */
  void deactivateLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Shutdown a live activity.
   *
   * @param id
   *          the ID of the live activity to shutdown
   * @param callback
   *          the callback for the call
   */
  void shutdownLiveActivity(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Get all live activity groups.
   *
   * @param callback
   *          the callback to run after the response
   */
  void getAllLiveActivityGroups(InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Get all live activity groups that match the specified filter.
   *
   * <p>
   * A {@code null} filter returns all live activity groups.
   *
   * @param filter
   *          the filter for matching, can be {@code null}
   * @param callback
   *          the callback to run after the response
   */
  void getAllLiveActivityGroups(String filter, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Start up a live activity group.
   *
   * @param id
   *          the ID of the live activity group to start up
   * @param callback
   *          the callback for the call
   */
  void startupLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Activate a live activity group.
   *
   * @param id
   *          the ID of the live activity group to activate
   * @param callback
   *          the callback for the call
   */
  void activateLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Deactivate a live activity group.
   *
   * @param id
   *          the ID of the live activity group to deactivate
   * @param callback
   *          the callback for the call
   */
  void deactivateLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Shutdown a live activity group.
   *
   * @param id
   *          the ID of the live activity group to shutdown
   * @param callback
   *          the callback for the call
   */
  void shutdownLiveActivityGroup(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Get all spaces.
   *
   * @param callback
   *          the callback to run after the response
   */
  void getAllSpaces(InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Get all spaces that match the specified filter.
   *
   * <p>
   * A {@code null} filter returns all spaces.
   *
   * @param filter
   *          the filter for matching, can be {@code null}
   * @param callback
   *          the callback to run after the response
   */
  void getAllSpaces(String filter, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Start up a space.
   *
   * @param id
   *          the ID of the space to start up
   * @param callback
   *          the callback for the call
   */
  void startupSpace(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Activate a space.
   *
   * @param id
   *          the ID of the space to activate
   * @param callback
   *          the callback for the call
   */
  void activateSpace(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Deactivate a space.
   *
   * @param id
   *          the ID of the space to deactivate
   * @param callback
   *          the callback for the call
   */
  void deactivateSpace(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Shutdown a space.
   *
   * @param id
   *          the ID of the space to shutdown
   * @param callback
   *          the callback for the call
   */
  void shutdownSpace(String id, InteractiveSpacesMasterApiMessageHandler callback);

  /**
   * Add an event handler.
   *
   * <p>
   * Event handlers are handlers that are not in response to a call initiated in this service. They include events like
   * a live activity starting up or shutting down.
   *
   * @param handler
   *          the handler to be called
   */
  void addEventHandler(InteractiveSpacesMasterApiMessageHandler handler);

  /**
   * Remove an event handler.
   *
   * <p>
   * Does nothing if the handler was never added.
   *
   * @param handler
   *          the handler to be removed
   */
  void removeEventHandler(InteractiveSpacesMasterApiMessageHandler handler);
}
