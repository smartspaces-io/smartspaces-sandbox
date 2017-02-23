/**
 * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.hardware.gpio;

import io.smartspaces.SmartSpacesException;
import io.smartspaces.service.BaseSupportedService;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.wiringpi.GpioUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * A GPIO service using Pi4J.
 * 
 * @author Keith M. Hughes
 */
public class Pi4jGpioService extends BaseSupportedService implements GpioService {

  private static final String SYSTEM_VALUE_NAME_SMARTSPACES_GPIO_GPIO = "system.smartspaces.gpio.gpio";
  private static Map<String, Pin> PIN_MAPPINGS;

  static {
    PIN_MAPPINGS = new HashMap<>();

    PIN_MAPPINGS.put("GPIO_00", RaspiPin.GPIO_00);
    PIN_MAPPINGS.put("GPIO_01", RaspiPin.GPIO_01);
    PIN_MAPPINGS.put("GPIO_02", RaspiPin.GPIO_02);
    PIN_MAPPINGS.put("GPIO_03", RaspiPin.GPIO_03);
    PIN_MAPPINGS.put("GPIO_04", RaspiPin.GPIO_04);
    PIN_MAPPINGS.put("GPIO_05", RaspiPin.GPIO_05);
    PIN_MAPPINGS.put("GPIO_06", RaspiPin.GPIO_06);
    PIN_MAPPINGS.put("GPIO_07", RaspiPin.GPIO_07);
    PIN_MAPPINGS.put("GPIO_08", RaspiPin.GPIO_08);
    PIN_MAPPINGS.put("GPIO_09", RaspiPin.GPIO_09);
    PIN_MAPPINGS.put("GPIO_10", RaspiPin.GPIO_10);
    PIN_MAPPINGS.put("GPIO_11", RaspiPin.GPIO_11);
    PIN_MAPPINGS.put("GPIO_12", RaspiPin.GPIO_12);
    PIN_MAPPINGS.put("GPIO_13", RaspiPin.GPIO_13);
    PIN_MAPPINGS.put("GPIO_14", RaspiPin.GPIO_14);
    PIN_MAPPINGS.put("GPIO_15", RaspiPin.GPIO_15);
    PIN_MAPPINGS.put("GPIO_16", RaspiPin.GPIO_16);
    PIN_MAPPINGS.put("GPIO_17", RaspiPin.GPIO_17);
    PIN_MAPPINGS.put("GPIO_18", RaspiPin.GPIO_18);
    PIN_MAPPINGS.put("GPIO_19", RaspiPin.GPIO_19);
    PIN_MAPPINGS.put("GPIO_20", RaspiPin.GPIO_20);
    PIN_MAPPINGS.put("GPIO_21", RaspiPin.GPIO_21);
    PIN_MAPPINGS.put("GPIO_22", RaspiPin.GPIO_22);
    PIN_MAPPINGS.put("GPIO_23", RaspiPin.GPIO_23);
    PIN_MAPPINGS.put("GPIO_24", RaspiPin.GPIO_24);
    PIN_MAPPINGS.put("GPIO_25", RaspiPin.GPIO_25);
    PIN_MAPPINGS.put("GPIO_26", RaspiPin.GPIO_26);
    PIN_MAPPINGS.put("GPIO_27", RaspiPin.GPIO_27);
    PIN_MAPPINGS.put("GPIO_28", RaspiPin.GPIO_28);
    PIN_MAPPINGS.put("GPIO_29", RaspiPin.GPIO_29);
    PIN_MAPPINGS.put("GPIO_30", RaspiPin.GPIO_30);
    PIN_MAPPINGS.put("GPIO_31", RaspiPin.GPIO_31);
  }

  /**
   * The GPIO controller.
   */
  private GpioController gpio;

  @Override
  public String getName() {
    return GpioService.SERVICE_NAME;
  }

  @Override
  public void startup() {
    // Pi4J cannot be initialized more than once for a given run of a JVM process,
    // and if you try the JVM process is shut down by Pi4J.
    // This makes it impossible to call GpioFactory.getInstance() more than once,
    // which makes it hard to update the GPIO service in a live running JVM and have
    // the service restart. So store the GpioController into the SmartSpaces value storage
    // so it can be picked up later if we need it when this service updates and restarts.
    gpio = getSpaceEnvironment().getValue(SYSTEM_VALUE_NAME_SMARTSPACES_GPIO_GPIO);
    if (gpio == null) {
      // Enable non sudo access to GPIO.
      GpioUtil.enableNonPrivilegedAccess();

      // Use the Broadcom pin numberings.
      GpioFactory.setDefaultProvider(
          new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

      gpio = GpioFactory.getInstance();
    }
  }

  @Override
  public void shutdown() {
    gpio.shutdown();
  }

  @Override
  public Spi getSoftwareSpi(String sclkPinName, String mosiPinName, String misoPinName, String csPinName) {
    if (csPinName == null || sclkPinName == null || mosiPinName == null || misoPinName == null) {
      throw new SmartSpacesException("A pin name has not been set for the software SPI pin definitions");
    }

    return new BitBangSpi(gpio, getPin(sclkPinName), getPin(mosiPinName), getPin(misoPinName), getPin(csPinName));
  }
  
  /**
   * Get a pin by its name.
   * 
   * @param pinName
   *       the name of the pin
   *       
   * @return the pin associated with the name
   * 
   * @throws SmartSpacesException
   *       no pin with the given name
   */
  private Pin getPin(String pinName) {
    Pin pin = PIN_MAPPINGS.get(pinName);
    if (pin != null) {
      return pin;
    } else {
      throw SmartSpacesException.newFormattedException("No GPIO pin by the name %s", pinName);
    }
  }
}
