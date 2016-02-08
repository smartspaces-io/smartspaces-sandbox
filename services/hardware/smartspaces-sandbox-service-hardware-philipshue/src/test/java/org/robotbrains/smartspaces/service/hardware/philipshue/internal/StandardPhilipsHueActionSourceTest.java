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

package org.robotbrains.smartspaces.service.hardware.philipshue.internal;

import io.smartspaces.sandbox.service.action.Action;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueEndpoint;
import io.smartspaces.sandbox.service.hardware.philipshue.PhilipsHueLight;
import io.smartspaces.sandbox.service.hardware.philipshue.internal.StandardPhilipsHueActionSource;
import io.smartspaces.system.SmartSpacesEnvironment;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

/**
 * Tests for the Philips Hue action source.
 * 
 * @author Keith M. Hughes
 */
public class StandardPhilipsHueActionSourceTest {
  private StandardPhilipsHueActionSource actionSource;
  private SmartSpacesEnvironment spaceEnvironment;
  private PhilipsHueEndpoint endpoint;

  @Before
  public void setup() {
    spaceEnvironment = Mockito.mock(SmartSpacesEnvironment.class);
    endpoint = Mockito.mock(PhilipsHueEndpoint.class);

    actionSource = new StandardPhilipsHueActionSource(endpoint, spaceEnvironment);
  }

  @Test
  public void testTurnLightOn() {
    PhilipsHueLight light1 = Mockito.mock(PhilipsHueLight.class);

    String lightName1 = "light1";
    Mockito.when(endpoint.getLightByName(lightName1)).thenReturn(light1);

    PhilipsHueLight light2 = Mockito.mock(PhilipsHueLight.class);

    String lightName2 = "light2";
    Mockito.when(endpoint.getLightByName(lightName2)).thenReturn(light2);

    Action action =
        actionSource.getAction(StandardPhilipsHueActionSource.ACTION_NAME_LIGHT_STATE_SET);

    Map<String, Object> callArgs = Maps.newHashMap();
    callArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_LIGHT_ID, lightName1);

    Map<String, Object> stateArgs = Maps.newHashMap();
    callArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_NEW_STATE, stateArgs);

    stateArgs.put(StandardPhilipsHueActionSource.LIGHT_ARGUMENT_STATE_ON, true);

    action.perform(callArgs);

    Mockito.verify(light1, Mockito.times(1)).setOn(true);
    Mockito.verify(light2, Mockito.never()).setOn(Mockito.anyBoolean());
  }
}
