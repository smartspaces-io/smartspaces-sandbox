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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

/**
 * A GPIO service using Pi4J.
 * 
 * @author Keith M. Hughes
 */
public class Pi4jGpioService implements GpioService {

	/**
	 * The GPIO controller.
	 */
	private GpioController gpio;

	@Override
	public void startup() {
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

		gpio = GpioFactory.getInstance();
	}

	@Override
	public void shutdown() {
		gpio.shutdown();
	}

	@Override
	public Spi getSoftwareSpi(Pin sclkPin, Pin mosiPin, Pin misoPin, Pin csPin) {
		return new BitBangSpi(gpio, sclkPin, mosiPin, misoPin, csPin);
	}
}
