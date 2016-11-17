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

package io.smartspaces.hardware.bits;

/**
 * A bit shift operation depending on the bit ordering.
 * 
 * <p>
 * This operation will be determined by whether operations should be most
 * significant bit first, or least significant bit first.
 * 
 * @author Keith M. Hughes
 */
public interface BitOrderOperation {

	/**
	 * Get the bit from a value.
	 * 
	 * @param value
	 *            the value to get the bit from
	 * @param bit
	 *            the number of the bit
	 * 
	 * @return {@code true} if the bit was {@code 1}
	 */
	boolean getBit(byte value, int bit);

	/**
	 * Shift the byte by the number of bits specified.
	 * 
	 * @param value
	 *            the value to be shifted
	 * @param bits
	 *            the number of bits to shift
	 * 
	 * @return the new byte after shifting
	 */
	byte getByteShift(byte value, int bits);
}
