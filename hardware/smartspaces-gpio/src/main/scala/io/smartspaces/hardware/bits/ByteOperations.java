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
 * Various operations useful for bytes.
 * 
 * @author Keith M. Hughes
 */
public class ByteOperations {

  /**
   * Add add two values as unsigned 8-bit values.
   * 
   * @param a
   *          the first byte
   * @param b
   *          the second byte
   * 
   * @return the final result
   */
  public static byte unsignedAdd(byte a, byte b) {
    return (byte) (((int) a & 0xFF) + ((int) b & 0xFF));
  }

  /**
   * Calculate the checksum of the contents of the entire array.
   * 
   * @param data
   *          the data to calculate the checksum of
   * @param checksumStart
   *          the value to start the calculations from
   * 
   * @return the checksum
   */
  public static byte calculateChecksum(byte[] data, byte checksumStart) {
    return calculateChecksum(data, 0, data.length, checksumStart);
  }

  /**
   * Calculate the checksum of the contents of an array.
   * 
   * @param data
   *          the data to calculate the checksum of
   * @param start
   *          the byte position to start calculating from
   * @param length
   *          the number of bytes to include in the sum
   * @param checksumStart
   *          the value to start the calculations from
   * 
   * @return the checksum
   */
  public static byte calculateChecksum(byte[] data, int start, int length, byte checksumStart) {
    int checksum = checksumStart & 0xff;
    for (int i = start; i < start + length; i++) {
      checksum += (int) data[i] & 0xff;
    }

    return (byte) checksum;
  }

}
