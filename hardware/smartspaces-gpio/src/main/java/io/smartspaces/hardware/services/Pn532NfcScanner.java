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

package io.smartspaces.hardware.services;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.smartspaces.hardware.gpio.GpioService;
import io.smartspaces.hardware.gpio.Pi4jGpioService;
import io.smartspaces.hardware.gpio.device.Pn532Device;
import io.smartspaces.hardware.gpio.device.SpiPn532Device;

/**
 * An NFC scanner that uses a local SPI-based PN532 board.
 * 
 * @author Keith M. Hughes
 */
public class Pn532NfcScanner implements NfcScanner {
	private GpioService gpioService;
	private Pn532Device pn532;

	private String lastGoodUuid;

	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> tagScanFuture;
	
	private Subject<String> observable = PublishSubject.create();

	@Override
	public void startup() {
		executorService = new ScheduledThreadPoolExecutor(10);

		gpioService = new Pi4jGpioService();
		gpioService.startup();

		Pin sclkPin = RaspiPin.GPIO_27;
		Pin mosiPin = RaspiPin.GPIO_04;
		Pin misoPin = RaspiPin.GPIO_17;
		Pin csPin = RaspiPin.GPIO_22;

		pn532 = new SpiPn532Device(gpioService.getSoftwareSpi(sclkPin, mosiPin, misoPin, csPin));
		pn532.startup();

		pn532.useSamConfiguration();

		tagScanFuture = executorService.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				scanForTag();
			}
		}, 0, 2000, TimeUnit.MILLISECONDS);

	}

	@Override
	public void shutdown() {
		tagScanFuture.cancel(true);

		pn532.shutdown();

		gpioService.shutdown();
	}

	@Override
	public Observable<String> getObservable() {
		return observable;
	}
	private void scanForTag() {
		byte[] uuidComponents = pn532.readPassiveTarget();

		if (uuidComponents != null) {
			String uuid = toHexString(uuidComponents);

			if (lastGoodUuid != null) {
				if (!lastGoodUuid.equals(uuid)) {
					processUuid(uuid);
				}
			} else {
				processUuid(uuid);
			}
		} else {
			// No scan this time so clear things out.
			lastGoodUuid = null;
		}
	}

	/**
	 * Process a new UUID.
	 * 
	 * @param uuid
	 *            the UUID to process
	 */
	private void processUuid(String uuid) {
		lastGoodUuid = uuid;

		observable.onNext(uuid);
	}

	/**
	 * Get the hex string for the bytes.
	 *
	 * <p>
	 * The bytes are assumed to be most significant byte first.
	 *
	 * @param ba
	 *            the array of bytes
	 *
	 * @return hex string for the bytes
	 */
	private String toHexString(byte[] ba) {
		if (ba.length == 7) {
			return String.format("%02x%02x%02x%02x%02x%02x%02x", ba[0], ba[1], ba[2], ba[3], ba[4], ba[5], ba[6]);
		} else {
			return String.format("%02x%02x%02x%02x", ba[0], ba[1], ba[2], ba[3]);
		}
	}

}
