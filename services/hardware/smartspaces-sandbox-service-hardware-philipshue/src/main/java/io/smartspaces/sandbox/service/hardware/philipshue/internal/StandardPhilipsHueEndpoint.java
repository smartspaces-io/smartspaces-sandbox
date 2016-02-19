/*
 * Copyright (C) 2014 Keith M. Hughes
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

import io.smartspaces.sandbox.service.action.ActionService;
import io.smartspaces.sandbox.service.action.ActionSource;
import io.smartspaces.sandbox.service.action.internal.StandardActionService;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpoint;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpointService;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueLight;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueRestMessages;
import io.smartspaces.service.web.HttpClientRestWebClient;
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry;
import io.smartspaces.util.data.dynamic.DynamicObjectBuilder;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectBuilder;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;
import io.smartspaces.util.data.json.JsonMapper;
import io.smartspaces.util.data.json.StandardJsonMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.Map;

/**
 * Standard endpoint for controlling Philips Hue Lights.
 * 
 * @author Keith M. Hughes
 */
public class StandardPhilipsHueEndpoint implements PhilipsHueEndpoint {

  static public void main(String[] args) {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();

    spaceEnvironment.getServiceRegistry().registerService(new StandardPhilipsHueEndpointService());

    PhilipsHueEndpointService service =
        spaceEnvironment.getServiceRegistry().getRequiredService(
            PhilipsHueEndpointService.SERVICE_NAME);
    PhilipsHueEndpoint endpoint =
        service.newEndpoint("192.168.199.10", "newdeveloper", spaceEnvironment.getLog());
    spaceEnvironment.addManagedResource(endpoint);

    endpoint.scanForLights();

    ActionService actionService = new StandardActionService();
    String sourceName = "light.philipshue";
    actionService.registerActionSource(sourceName, endpoint.newActionSource());

    String lightId = "Hue Lamp 2";

    Map<String, Object> callArgsOn = newCallArgsLightState(lightId, true);
    Map<String, Object> callArgsOff = newCallArgsLightState(lightId, false);

    actionService.performAction(sourceName,
        StandardPhilipsHueActionSource.ACTION_NAME_LIGHT_STATE_SET, callArgsOn);

    SmartSpacesUtilities.delay(5000);

    actionService.performAction(sourceName,
        StandardPhilipsHueActionSource.ACTION_NAME_LIGHT_STATE_SET, callArgsOff);

    // endpoint.updateLightState(endpoint.getLightByName("Hue Lamp 2").setOn(true));
    // System.out.println(client.performGet("http://192.168.188.123/api/newdeveloper/lights/2"));
    spaceEnvironment.shutdown();
  }

  private static Map<String, Object> newCallArgsLightState(String lightId, boolean lightOnState) {
    Map<String, Object> callArgs = Maps.newHashMap();
    callArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_LIGHT_ID, lightId);

    Map<String, Object> stateArgs = Maps.newHashMap();
    callArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_NEW_STATE, stateArgs);

    stateArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_STATE_ON, lightOnState);
    return callArgs;
  }

  /**
   * The host for the Hue Hub.
   */
  private String host;

  /**
   * The user for the Hue Hub.
   */
  private String hueUser;

  /**
   * The REST client for communicating with the Hue hub.
   */
  private HttpClientRestWebClient client;

  /**
   * The base URL for the REST commands.
   */
  private String baseRestUrl;

  /**
   * A map from unique IDs to the light with that ID.
   */
  private Map<String, PhilipsHueLight> uniqueIdToLights = Maps.newHashMap();

  /**
   * A map from the names of lights to the light.
   */
  private Map<String, PhilipsHueLight> nameToLights = Maps.newTreeMap();

  /**
   * The JSON mapper for the rest calls.
   */
  private JsonMapper jsonMapper = StandardJsonMapper.INSTANCE;

  /**
   * The logger to use.
   */
  private Log log;

  /**
   * The space environment to use.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  /**
   * Construct a new endpoint.
   * 
   * @param host
   *          the host for the Hue Hub
   * @param hueUser
   *          the user for the Hue Hub
   * @param client
   *          the REST client
   * @param log
   *          the logger
   * @param spaceEnvironment
   *          the space environment
   */
  public StandardPhilipsHueEndpoint(String host, String hueUser, HttpClientRestWebClient client,
      Log log, SmartSpacesEnvironment spaceEnvironment) {
    this.host = host;
    this.hueUser = hueUser;
    this.client = client;
    this.log = log;
    this.spaceEnvironment = spaceEnvironment;

    baseRestUrl = "http://" + host + "/api/" + hueUser;
  }

  @Override
  public void startup() {
    client.startup();
  }

  @Override
  public void shutdown() {
    client.shutdown();
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public String getHueUser() {
    return hueUser;
  }

  @Override
  public void scanForLights() {
    String response = client.performGet(baseRestUrl + "/lights");
    Map<String, Object> responseObject = jsonMapper.parseObject(response);
    DynamicObject nav = new StandardDynamicObjectNavigator(responseObject);

    for (ObjectDynamicObjectEntry lightData : nav.getObjectEntries()) {
      String controlId = lightData.getProperty();

      DynamicObject specificLightData = lightData.down();

      String uniqueId =
          specificLightData
              .getString(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_UNIQUEID);
      String lightName =
          specificLightData.getString(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_NAME);

      PhilipsHueLight light = uniqueIdToLights.get(uniqueId);
      if (light == null) {
        light = new PhilipsHueLight(uniqueId);
        uniqueIdToLights.put(uniqueId, light);
        log.info(String.format("New Philips hue light found with unique ID %s and name %s",
            uniqueId, lightName));
      }

      light.setControlId(controlId);
      light.setName(lightName);

      extractLightStateData(
          specificLightData.down(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE), light);

      nameToLights.put(light.getName(), light);
    }
  }

  @Override
  public void renameLight(PhilipsHueLight light, String newName) {
    DynamicObjectBuilder builder = new StandardDynamicObjectBuilder();
    builder.setProperty(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_NAME, newName);

    System.out.println(client.performPut(baseRestUrl + "/lights/" + light.getControlId(),
        builder.toJson()));

    // TODO(keith): Only change if all was successful.

    nameToLights.remove(light.getName());
    light.setName(newName);
  }

  @Override
  public List<String> getLightNames() {
    return Lists.newArrayList(nameToLights.keySet());
  }

  @Override
  public PhilipsHueLight getLightByName(String name) {
    return nameToLights.get(name);
  }

  @Override
  public void updateLightState(PhilipsHueLight light) {
    DynamicObjectBuilder builder = new StandardDynamicObjectBuilder();
    builder.setProperty(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_ON, light.isOn());
    builder.setProperty(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_SATURATION,
        light.getSaturation());
    builder.setProperty(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_BRIGHTNESS,
        light.getBrightness());
    builder.setProperty(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_HUE, light.getHue());

    System.out.println(client.performPut(
        baseRestUrl + "/lights/" + light.getControlId() + "/state", builder.toJson()));
  }

  @Override
  public ActionSource newActionSource() {
    return new StandardPhilipsHueActionSource(this, spaceEnvironment);
  }

  /**
   * Extract the state data from the dynamic object for a light.
   * 
   * @param stateData
   *          a dynamic object positioned at the state data for the light
   * @param light
   *          the light
   */
  private void extractLightStateData(DynamicObject stateData, PhilipsHueLight light) {
    light.setOn(stateData.getBoolean(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_ON));
    light.setBrightness(stateData
        .getInteger(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_BRIGHTNESS));
    light.setSaturation(stateData
        .getInteger(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_SATURATION));
    light.setHue(stateData.getInteger(PhilipsHueRestMessages.PHILIPS_HUE_FIELD_LIGHT_STATE_HUE));
  }
}
