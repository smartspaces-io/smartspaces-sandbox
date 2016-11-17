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

import io.smartspaces.hardware.bits.BitOrder;

/**
 * An SPI endpoint for communicating with SPI hardware.
 * 
 * @author Keith M. Hughes
 */
public interface Spi {

	/**
	 * Start up the SPI connection.
	 */
	void startup();

	/**
	 * Close the SPI connection.
	 */
	void shutdown();

	/**
	 * Set the speed of the SPI clock.
	 * 
	 * @param hz
	 *            the speed in hertz
	 */
	void setClockHz(int hz);

	/**
	 * Set SPI mode which controls clock polarity and phase. Should be a numeric
	 * value 0, 1, 2, or 3. See wikipedia page for details on meaning:
	 * http://en.wikipedia.org/wiki/Serial_Peripheral_Interface_Bus
	 *
	 * @param mode
	 */
	void setMode(int mode);

	/**
	 * Set order of bits to be read/written over serial lines. Should be either
	 * MSBFIRST for most-significant first, or LSBFIRST for least-significant
	 * first.
	 *
	 * @param order
	 */
	void setBitOrder(BitOrder order);

	/**
	 * Write out the data in half-duplex mode.
	 * 
	 * <p>
	 * The chip will be selected and then deselected in the write.
	 * 
	 * @param data
	 *            the data to write
	 */
	void write(byte[] data);

	/**
	 * Write data on SPI in half-duplex mode.
	 * 
	 * @param data
	 *            the data to write
	 * @param selectChip
	 *            {@code true} if the method should select the chip before
	 *            starting the write
	 * @param deselectChip
	 *            {@code true} if the method should deselect the chip after the
	 *            write completes
	 */
	void write(byte[] data, boolean selectChip, boolean deselectChip);

	/**
	 * Read data in half-duplex mode.
	 * 
	 * <p>
	 * The chip will be selected and then deselected in the read.
	 * 
	 * @param length
	 *            the number of bytes to read
	 * 
	 * @return the data read
	 */
	byte[] read(int length);

	/**
	 * Read data from SPI in half-duplex mode.
	 * 
	 * @param length
	 *            the number of bytes to read
	 * @param selectChip
	 *            {@code true} if the method should select the chip before
	 *            starting the read
	 * @param deselectChip
	 *            {@code true} if the method should deselect the chip after the
	 *            read completes
	 * 
	 * @return the data read
	 */
	byte[] read(int length, boolean selectChip, boolean deselectChip);

	/**
	 * Transfer data in full-duplex mode.
	 *
	 * @param data
	 *            the data to write
	 * @param selectChip
	 *            {@code true} if the method should select the chip before
	 *            starting the transfer
	 * @param deselectChip
	 *            {@code true} if the method should deselect the chip after the
	 *            transfer completes
	 * 
	 * @return the data read
	 */
	byte[] transfer(byte[] data, boolean selectChip, boolean deselectChip);

	/**
	 * Select the chip.
	 */
	void selectChip();

	/**
	 * Deselect the chip.
	 */
	void deselectChip();
}