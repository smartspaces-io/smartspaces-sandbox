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

import io.smartspaces.hardware.bits.BitOrderOperation;
import io.smartspaces.hardware.bits.BitOrder;
import io.smartspaces.hardware.bits.LsbBitOrderOperation;
import io.smartspaces.hardware.bits.MsbBitOrderOperation;

/**
 * An SPI driver that performs software based SPI over GPIO pins.
 * 
 * @author Keith M. Hughes
 */
public class BitBangSpi implements Spi {

	/**
	 * The GPIO controller.
	 */
	private GpioController gpio;

	/**
	 * The pin to use for the system clock.
	 */
	private Pin sclkPin;

	/**
	 * The digital output to use for the system clock.
	 */
	private GpioPinDigitalOutput sclkOutput;

	/**
	 * The pin to use for MOSI.
	 */
	private Pin mosiPin;

	/**
	 * The digital output to use for MOSI.
	 */
	private GpioPinDigitalOutput mosiOutput;

	/**
	 * The pin to use for MISO.
	 */
	private Pin misoPin;

	/**
	 * The digital output to use for MISO.
	 */
	private GpioPinDigitalInput misoInput;

	/**
	 * The pin to use for chip select.
	 */
	private Pin ssPin;

	/**
	 * The digital output to use for chip select.
	 */
	private GpioPinDigitalOutput ssOutput;

	/**
	 * {@code true} if the clock base is high, {@code false} if the clock base
	 * is low.
	 */
	private boolean clockBase;

	/**
	 * {@code true} if a bit read should be on the leading edge of a clock
	 * shift.
	 */
	private boolean readLeading;

	/**
	 * A mask giving the bit position of the "first" bit, dpending on bit
	 * ordering.
	 */
	private byte mask;

	/**
	 * The shift to use when reading.
	 */
	private BitOrderOperation readShift;

	/**
	 * The shift to use when writing.
	 */
	private BitOrderOperation writeShift;

	public BitBangSpi(GpioController gpio, Pin sclkPin, Pin mosiPin, Pin misoPin, Pin ssPin) {
		this.gpio = gpio;
		this.sclkPin = sclkPin;
		this.mosiPin = mosiPin;
		this.misoPin = misoPin;
		this.ssPin = ssPin;

		// Assume mode 0.
		setMode(0);

		// Assume most significant bit first order.
		setBitOrder(BitOrder.MSBFIRST);
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
		// transferring data.

		// Set pins as outputs/inputs.
		sclkOutput = gpio.provisionDigitalOutputPin(sclkPin, "SCLK Pin", PinState.LOW);
		mosiOutput = gpio.provisionDigitalOutputPin(mosiPin, "MOSI Pin", PinState.LOW);
		misoInput = gpio.provisionDigitalInputPin(misoPin, "MISO Pin");

		// Assert SS high to start with device communication off.
		ssOutput = gpio.provisionDigitalOutputPin(ssPin, "SS Pin", PinState.HIGH);

		// Put clock into its base state.
		sclkOutput.setState(clockBase);
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
	}

	@Override
	public void setBitOrder(BitOrder order) {
		// Set self._mask to the bitmask which points at the appropriate bit to
		// read or write, and appropriate left/right shift operator function for
		// reading/writing.
		if (order == BitOrder.MSBFIRST) {
			mask = (byte) 0x80;
			writeShift = new MsbBitOrderOperation();
			readShift = new LsbBitOrderOperation();
		} else if (order == BitOrder.LSBFIRST) {
			mask = (byte) 0x01;
			writeShift = new LsbBitOrderOperation();
			readShift = new MsbBitOrderOperation();
		} else {
			throw new RuntimeException("Order must be MSBFIRST or LSBFIRST.");
		}
	}

	@Override
	public void shutdown() {
		// This is ignored in software SPI so ignore it.
	}

	@Override
	public void write(byte[] data) {
		write(data, true, true);
	}

	@Override
	public void write(byte[] data, boolean selectChip, boolean deselectChip) {
		if (selectChip) {
			selectChip();
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
		if (deselectChip) {
			deselectChip();
		}
	}

	@Override
	public void selectChip() {
		ssOutput.setState(false);
	}

	@Override
	public void deselectChip() {
		ssOutput.setState(true);
	}

	@Override
	public byte[] read(int length) {
		return read(length, true, true);
	}

	@Override
	public byte[] read(int length, boolean selectChip, boolean deselectChip) {

		if (selectChip) {
			selectChip();
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
		if (deselectChip) {
			ssOutput.setState(true);
		}

		return result;
	}

	@Override
	public byte[] transfer(byte[] data, boolean selectChip, boolean deselectChip) {
		if (selectChip) {
			selectChip();
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

		if (deselectChip) {
			deselectChip();
		}

		return result;
	}
}