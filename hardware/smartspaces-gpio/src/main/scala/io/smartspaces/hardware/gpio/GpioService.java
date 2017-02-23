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

import io.smartspaces.service.SupportedService;

/**
 * A service for getting GPIO resources.
 * 
 * @author Keith M. Hughes
 */
public interface GpioService extends SupportedService {

  /**
   * Name of the service.
   */
  public static final String SERVICE_NAME = "rpi.gpio";

  /**
   * Get a software SPI instance.
   * 
   * @param sclkPinName
   *          the system clock pin
   * @param mosiPinName
   *          the MOSI pin
   * @param misoPinName
   *          the MISO pin
   * @param csPinName
   *          the chip select pin
   * 
   * @return the SPI implementation
   */
  Spi getSoftwareSpi(String sclkPinName, String mosiPinName, String misoPinName, String ssPinName);
}
