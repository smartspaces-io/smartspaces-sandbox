/*
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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

import io.smartspaces.hardware.gpio.device.Pn532Device;
import io.smartspaces.hardware.gpio.device.SpiPn532Device;

/**
 * Test driver for the PN532.
 * 
 * @author Keith M. Hughes
 */
public class Test {
	public static void main(String[] args) {
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

		Pin sclkPin = RaspiPin.GPIO_27;
		Pin mosiPin = RaspiPin.GPIO_04;
		Pin misoPin = RaspiPin.GPIO_17;
		Pin csPin = RaspiPin.GPIO_22;

		GpioService gpioService = new Pi4jGpioService();
		gpioService.startup();

		Pn532Device pn532 = new SpiPn532Device(gpioService.getSoftwareSpi(sclkPin, mosiPin, misoPin, csPin));
		pn532.startup();

		pn532.useSamConfiguration();

		while (true) {
			byte[] uuid = pn532.readPassiveTarget();

			if (uuid != null) {
				System.out.println("UUID is");
				for (byte uuidComponent : uuid) {
					System.out.println(Integer.toHexString(uuidComponent));
				}
			}
		}
	}

}
