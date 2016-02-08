/*
 * Copyright (C) 2014 Keith M. Hughes.
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

import io.smartspaces.sandbox.service.action.ActionSource;
import io.smartspaces.util.resource.ManagedResource;

import java.util.List;

/**
 * An endpoint for controlling Philips Hue lights.
 * 
 * @author Keith M. Hughes
 */
public interface PhilipsHueEndpoint extends ManagedResource {

  /**
   * Get the network host of the Hue Hub.
   * 
   * @return the network host of the Hue Hub
   */
  String getHost();

  /**
   * Get the API user on the Hue Hub.
   * 
   * @return the API user on the Hue Hub
   */
  String getHueUser();

  /**
   * Scan for all lights on the hub.
   */
  void scanForLights();

  /**
   * Rename a light.
   * 
   * @param light
   *          the light to be renamed
   * @param newName
   *          the new name for the light
   */
  void renameLight(PhilipsHueLight light, String newName);

  /**
   * Get a list of all light names known by the endpoint.
   * 
   * @return all light names, sorted by their name
   */
  List<String> getLightNames();

  /**
   * Get a light by its name.
   * 
   * @param name
   *          the name of the light
   * 
   * @return the light, or {@code null} if no light with that name
   */
  PhilipsHueLight getLightByName(String name);

  /**
   * Update the remote light to match the local light data.
   * 
   * @param light
   *          the local light data
   */
  void updateLightState(PhilipsHueLight light);

  /**
   * Create a new action source that works with this endpoint.
   * 
   * @return the new action source
   */
  ActionSource newActionSource();
}