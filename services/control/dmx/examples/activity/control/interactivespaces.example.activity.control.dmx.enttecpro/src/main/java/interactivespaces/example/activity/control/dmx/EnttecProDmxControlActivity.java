/*
 * Copyright (C) 2013 Google Inc.
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

package interactivespaces.example.activity.control.dmx;

import interactivespaces.activity.impl.BaseActivity;
import interactivespaces.sandbox.service.control.dmx.DmxControlEndpoint;
import interactivespaces.sandbox.service.control.dmx.DmxControlService;
import interactivespaces.sandbox.service.control.dmx.internal.enttecpro.EnttecProDmxControlService;

/**
 * An Interactive Spaces Java-based activity that communicates with an Arduino sketch that controls a DMX system.
 *
 * <p>
 * Commands sent to the arduino will specify the DMX channel and the value to set that channel at.
 *
 * @author Keith M. Hughes
 */
public class EnttecProDmxControlActivity extends BaseActivity {

  /**
   * The name of the config property for obtaining the serial port.
   */
  public static final String CONFIGURATION_PROPERTY_HARDWARE_SERIAL_PORT = "space.hardware.serial.port";

  /**
   * Maximum length of a message from the serial connection.
   */
  public static final int MESSAGE_LENGTH = 4;

  /**
   * The source for DMX control endpoints.
   */
  private DmxControlService dmxControlService;

  /**
   * The communication endpoint for DMX control.
   */
  private DmxControlEndpoint dmxEndpoint;

  /**
   * The DMX channel to write to.
   *
   * TODO(keith): Make a config parameter.
   */
  private int dmxChannel = 1;

  /**
   * The color to set the DMX light to for activation. This is in RGB.
   */
  private int[] activateColor = new int[] { 128, 0, 0 };

  /**
   * The color to set the DMX light to for deactivation. This is in RGB.
   */
  private int[] deactivateColor = new int[] { 0, 128, 0 };

  @Override
  public void onActivitySetup() {
    EnttecProDmxControlService controlService = new EnttecProDmxControlService();
    getSpaceEnvironment().getServiceRegistry().registerService(controlService);

    dmxControlService = getSpaceEnvironment().getServiceRegistry().getRequiredService(DmxControlService.SERVICE_NAME);

    String portName = getConfiguration().getRequiredPropertyString(CONFIGURATION_PROPERTY_HARDWARE_SERIAL_PORT);

    dmxEndpoint = dmxControlService.newSerialDmxControlEndpoint(portName, getLog());
    addManagedResource(dmxEndpoint);
  }

  @Override
  public void onActivityActivate() {
    dmxEndpoint.writeDmxData(dmxChannel, activateColor);
  }

  @Override
  public void onActivityDeactivate() {
    dmxEndpoint.writeDmxData(dmxChannel, deactivateColor);
  }
}
