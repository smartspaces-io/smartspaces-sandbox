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

/**
 * Start up the SPI endpoint.
 * 
 * @author Keith M. Hughes
 */
public interface Spi {

	void startup();

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
	 * MSBFIRST for most-significant first, or LSBFIRST for least-signifcant
	 * first.
	 *
	 * @param order
	 */
	void setBitOrder(ByteOrder order);

	/**
	 * Close the SPI connection.
	 */
	void close();

	void write(byte[] data);

	/**
	 * Half-duplex SPI write. If assert_ss is True, the SS line will be asserted
	 * low, the specified bytes will be clocked out the MOSI line, and if
	 * deassert_ss is True the SS line be put back high.
	 *
	 * 
	 * @param data
	 * @param assert_ss
	 * @param deassert_ss
	 */
	void write(byte[] data, boolean assert_ss, boolean deassert_ss);

	byte[] read(int length);

	/**
	 * Half-duplex SPI read. If assert_ss is true, the SS line will be asserted
	 * low, the specified length of bytes will be clocked in the MISO line, and
	 * if deassert_ss is true the SS line will be put back high. Bytes which are
	 * read will be returned as a bytearray object.
	 *
	 * @param length
	 * @param assert_ss
	 * @return
	 */
	byte[] read(int length, boolean assert_ss, boolean deassert_ss);

	/**
	 * Full-duplex SPI read and write. If assert_ss is true, the SS line will be
	 * asserted low, the specified bytes will be clocked out the MOSI line while
	 * bytes will also be read from the MISO line, and if deassert_ss is true
	 * the SS line will be put back high. Bytes which are read will be returned
	 * as a bytearray object.
	 *
	 * @param data
	 * @param assert_ss
	 * @param deassert_ss
	 * @return
	 */
	byte[] transfer(byte[] data, boolean assert_ss, boolean deassert_ss);

}