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
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * An SPI driver that performs software based SPI over GPIO pins.
 * 
 * @author Keith M. Hughes
 */
public class BitBangSpi implements Spi {
	private GpioController gpio;
	private Pin sclkPin;
	private GpioPinDigitalOutput sclkOutput;
	private Pin mosiPin;
	private GpioPinDigitalOutput mosiOutput;
	private Pin misoPin;
	private GpioPinDigitalInput misoInput;
	private Pin ssPin;
	private GpioPinDigitalOutput ssOutput;
	private boolean clockBase;
	private boolean readLeading;

	private byte mask;

	private BitOperator readShift;

	private BitOperator writeShift;

	public BitBangSpi(Pin sclkPin, Pin mosiPin, Pin misoPin, Pin ssPin) {
		this.sclkPin = sclkPin;
		this.mosiPin = mosiPin;
		this.misoPin = misoPin;
		this.ssPin = ssPin;

		// Assume mode 0.
		setMode(0);

		// Assume most significant bit first order.
		setBitOrder(ByteOrder.MSBFIRST);
	}

	@Override
	public void startup() {
		// Initialize bit bang (or software) based SPI. Must provide a BaseGPIO
		// class, the SPI clock, and optionally MOSI, MISO, and SS (slave
		// select)
		// pin numbers. If MOSI is set to None then writes will be disabled and
		// fail
		// with an error, likewise for MISO reads will be disabled. If SS is set
		// to
		// None then SS will not be asserted high/low by the library when
		// transfering data.

		// Set pins as outputs/inputs.
		sclkOutput = gpio.provisionDigitalOutputPin(sclkPin, "SCLK Pin", PinState.LOW);
		mosiOutput = gpio.provisionDigitalOutputPin(mosiPin, "MOSI Pin", PinState.LOW);
		misoInput = gpio.provisionDigitalInputPin(misoPin, "MISO Pin");

		// Assert SS high to start with device communication off.
		ssOutput = gpio.provisionDigitalOutputPin(ssPin, "SS Pin", PinState.HIGH);
	}

	@Override
	public void setClockHz(int hz) {
		// This is ignored in software SPI so ignore it.
	}

	@Override
	public void setMode(int mode) {
		if (mode < 0 || mode > 3) {
			throw new RuntimeException("Mode must be a value 0, 1, 2, or 3.");
		}

		if ((mode & 0x02) != 0) {
			// Clock is normally high in mode 2 and 3.
			clockBase = true;
		} else {
			// Clock is normally low in mode 0 and 1.
			clockBase = false;
		}

		if ((mode & 0x01) != 0) {
			// Read on trailing edge in mode 1 and 3.
			readLeading = false;
		} else {
			// Read on leading edge in mode 0 and 2.
			readLeading = true;
		}

		// Put clock into its base state.
		sclkOutput.setState(clockBase);
	}

	@Override
	public void setBitOrder(ByteOrder order) {
		// Set self._mask to the bitmask which points at the appropriate bit to
		// read or write, and appropriate left/right shift operator function for
		// reading/writing.
		if (order == ByteOrder.MSBFIRST) {
			mask = (byte) 0x80;
			writeShift = new MsbBitOperator();
			readShift = new LsbBitOperator();
		} else if (order == ByteOrder.LSBFIRST) {
			mask = (byte) 0x01;
			writeShift = new LsbBitOperator();
			readShift = new MsbBitOperator();
		} else {
			throw new RuntimeException("Order must be MSBFIRST or LSBFIRST.");
		}
	}

	@Override
	public void close() {
		// This is ignored in software SPI so ignore it.
	}

	@Override
	public void write(byte[] data) {
		write(data, true, true);
	}

	@Override
	public void write(byte[] data, boolean assert_ss, boolean deassert_ss) {
		if (assert_ss) {
			ssOutput.setState(false);
		}
		for (byte dataItem : data) {
			for (int i = 0; i < 8; i++) {
				// Write bit to MOSI.
				mosiOutput.setState(writeShift.getBit(dataItem, i));

				// Flip clock off base.
				sclkOutput.setState(!clockBase);

				// Return clock to base.
				sclkOutput.setState(clockBase);
			}
		}
		if (deassert_ss) {
			ssOutput.setState(true);
		}
	}

	@Override
	public byte[] read(int length) {
		return read(length, true, true);
	}

	@Override
	public byte[] read(int length, boolean assert_ss, boolean deassert_ss) {

		if (assert_ss) {
			ssOutput.setState(false);
		}

		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < 8; j++) {
				// Flip clock off base.
				sclkOutput.setState(!clockBase);
				// Handle read on leading edge of clock.
				if (readLeading) {
					if (misoInput.isHigh()) {
						// Set bit to 1 at appropriate location.
						result[i] |= readShift.getByteShift(mask, j);
					} else {
						// Set bit to 0 at appropriate location.
						result[i] &= ~readShift.getByteShift(mask, j);
					}
				}
				// Return clock to base.
				sclkOutput.setState(clockBase);
				// Handle read on trailing edge of clock.
				if (!readLeading) {
					if (misoInput.isHigh()) {
						// Set bit to 1 at appropriate location.
						result[i] |= readShift.getByteShift(mask, j);
					} else {
						// Set bit to 0 at appropriate location.
						result[i] &= ~readShift.getByteShift(mask, j);
					}
				}
			}
		}
		if (deassert_ss) {
			ssOutput.setState(true);
		}

		return result;
	}

	@Override
	public byte[] transfer(byte[] data, boolean assert_ss, boolean deassert_ss) {
		if (assert_ss) {
			ssOutput.setState(false);
		}

		byte[] result = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			byte dataItem = data[i];
			for (int j = 0; j < 8; j++) {
				// Write bit to MOSI.
				mosiOutput.setState(writeShift.getBit(dataItem, i));

				// Flip clock off base.
				sclkOutput.setState(!clockBase);

				// Handle read on leading edge of clock.
				if (readLeading) {
					if (misoInput.isHigh()) {
						// Set bit to 1 at appropriate location.
						result[i] |= readShift.getByteShift(mask, j);
					} else {
						// Set bit to 0 at appropriate location.
						result[i] &= ~readShift.getByteShift(mask, j);
					}
				}

				// Return clock to base.
				sclkOutput.setState(clockBase);

				// Handle read on trailing edge of clock.
				if (!readLeading) {
					if (misoInput.isHigh()) {
						// Set bit to 1 at appropriate location.
						result[i] |= readShift.getByteShift(mask, j);
					} else {
						// Set bit to 0 at appropriate location.
						result[i] &= ~readShift.getByteShift(mask, j);
					}
				}
			}
		}

		if (deassert_ss) {
			ssOutput.setState(true);
		}

		return result;
	}
}