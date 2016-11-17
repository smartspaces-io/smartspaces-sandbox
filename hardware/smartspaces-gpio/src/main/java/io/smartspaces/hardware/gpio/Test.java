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
import com.pi4j.io.gpio.RaspiPin;

/**
 * Test driver for the PN532.
 * 
 * @author Keith M. Hughes
 */
public class Test {
	public static void main(String[] args) {
		Pin sclkPin = RaspiPin.GPIO_27;
		Pin mosiPin = RaspiPin.GPIO_04;
		Pin misoPin = RaspiPin.GPIO_17;
		Pin ssPin = RaspiPin.GPIO_22;

		GpioController gpio = GpioFactory.getInstance();
		Spi spi = new BitBangSpi(gpio, sclkPin, mosiPin, misoPin, ssPin);
		StandardPn532 pn532 = new StandardPn532(spi);
		pn532.begin();

		pn532.SAM_configuration();

		while (true) {
			byte[] uuid = pn532.read_passive_target();

			if (uuid != null) {
				for (byte uuidComponent : uuid) {
					System.out.println(Integer.toHexString(uuidComponent));
				}
			}
		}
	}

}
