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
import interactivespaces.service.control.dmx.DmxControlEndpoint;
import interactivespaces.service.control.dmx.DmxControlService;
import interactivespaces.service.control.dmx.internal.SerialDmxControlService;
import interactivespaces.util.concurrency.CancellableLoop;
import interactivespaces.util.resource.ManagedResourceWithTask;

/**
 * An Interactive Spaces Java-based activity which communicates with an Arduino
 * sketch which controls a DMX system.
 *
 * <p>
 * Commands sent to the arduino will specify the DMX channel and the value to
 * set that channel at.
 *
 * @author Keith M. Hughes
 */
public class SerialBasedDmxControlActivity extends BaseActivity {

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

  @Override
  public void onActivitySetup() {
    SerialDmxControlService controlService = new SerialDmxControlService();
    getSpaceEnvironment().getServiceRegistry().registerService(controlService);

    dmxControlService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(DmxControlService.SERVICE_NAME);

    String portName = getConfiguration().getRequiredPropertyString(CONFIGURATION_PROPERTY_HARDWARE_SERIAL_PORT);

    dmxEndpoint = dmxControlService.newDmxControlEndpoint(portName, getLog());

    addManagedResource(new ManagedResourceWithTask(dmxEndpoint, new SerialReadTask(), getSpaceEnvironment()));
  }

  @Override
  public void onActivityActivate() {
    dmxEndpoint.setValue(6, 128);
    dmxEndpoint.setValue(7, 0);
  }

  @Override
  public void onActivityDeactivate() {
    dmxEndpoint.setValue(6, 0);
    dmxEndpoint.setValue(7, 128);
  }

  /**
   * Attempt to read the serial data from the arduino.
   *
   * @param serialMessageBuffer
   *          the buffer for reading serial data into
   */
  private void readStream(byte[] serialMessageBuffer) {
//    if (arduinoEndpoint.available() >= MESSAGE_LENGTH) {
//      arduinoEndpoint.read(serialMessageBuffer);
//
//      if (isActivated()) {
//        // Nothing right now
//      }
//    }
  }

  /**
   * The task for doing reads from the serial connection.
   *
   * @author Keith M. Hughes
   */
  private class SerialReadTask extends CancellableLoop {
    /**
     * Buffer for serial messages.
     */
    private final byte[] serialMessageBuffer = new byte[MESSAGE_LENGTH];

    @Override
    protected void loop() throws InterruptedException {
      readStream(serialMessageBuffer);
    }

    @Override
    protected void handleException(Exception e) {
      getLog().error("Exception while reading serial port", e);
    }
  }
}
