/*
 * Copyright (C) 2015 Keith M. Hughes
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

import io.smartspaces.SimpleSmartSpacesException;
import io.smartspaces.evaluation.ExecutionContext;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpoint;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueLight;
import io.smartspaces.service.action.Action;
import io.smartspaces.service.action.ActionSource;
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;

/**
 * An action source for Philips Hue lights.
 * 
 * @author Keith M. Hughes
 */
public class StandardPhilipsHueActionSource implements ActionSource {

  public static final String ACTION_NAME_LIGHT_STATE_SET = "light.state.set";

  /**
   * The light action argument for the light ID.
   */
  public static final String LIGHT_ARGUMENT_LIGHT_ID = "lightId";

  /**
   * The light action argument for changing the light state.
   */
  public static final String LIGHT_ARGUMENT_NEW_STATE = "newState";

  /**
   * The light action argument for changing the on state.
   */
  public static final String LIGHT_ARGUMENT_STATE_ON = "on";

  /**
   * The endpoint for Philips Hue lights.
   */
  private PhilipsHueEndpoint philipsHueEndpoint;

  /**
   * The space environment for the source.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  public StandardPhilipsHueActionSource(PhilipsHueEndpoint philipsHueEndpoint,
      SmartSpacesEnvironment spaceEnvironment) {
    this.philipsHueEndpoint = philipsHueEndpoint;
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public Action getAction(String actionName) {
    if (ACTION_NAME_LIGHT_STATE_SET.equals(actionName)) {
      return new PhilipsHueLightChangeAction();
    }

    throw new SimpleSmartSpacesException(String.format(
        "No action with name %s from action source %s", actionName, getClass().getName()));
  }

  /**
   * The action for changing a collection of Philips Hue lights to the same
   * state.
   * 
   * @author Keith M. Hughes
   */
  public class PhilipsHueLightChangeAction implements Action {

    @Override
    public void perform(ExecutionContext context) {
      StandardDynamicObjectNavigator actionArguments = new StandardDynamicObjectNavigator(context.);

      String lightId = actionArguments.getRequiredString(LIGHT_ARGUMENT_LIGHT_ID);
      PhilipsHueLight light = philipsHueEndpoint.getLightByName(lightId);
      if (light != null) {
        actionArguments.down(LIGHT_ARGUMENT_NEW_STATE);
        Boolean on = actionArguments.getBoolean(LIGHT_ARGUMENT_STATE_ON);
        if (on != null) {
          light.setOn(on);
        }

        philipsHueEndpoint.updateLightState(light);
      } else {
        spaceEnvironment.getLog().warn(
            String.format("Light state change, could not find light with name %s from endpoint %s",
                lightId, philipsHueEndpoint));
      }
    }
  }
}
