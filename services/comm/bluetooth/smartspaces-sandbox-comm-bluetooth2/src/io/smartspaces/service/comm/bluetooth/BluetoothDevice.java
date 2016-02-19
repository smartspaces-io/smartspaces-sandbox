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

package io.smartspaces.service.comm.bluetooth;

/**
 * A Bluetooth device.
 * 
 * @author Keith M. Hughes
 */
public interface BluetoothDevice {

	void startup() throws Exception;

	void shutdown() throws Exception;

	/**
	 * Connect to the device.
	 */
	void connect();

	/**
	 * Disconnect to the device.
	 */
	void disconnect();

	/**
	 * Get the unique ID for the device.
	 * 
	 * @return the unique ID
	 */
	String getId();

	/**
	 * Get the name of the device.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Get the RSSI of the device.
	 * 
	 * @return the RSSI
	 */
	int getRssi();

	/**
	 * Is the device connected?
	 * 
	 * @return {@code true} if the device is connected
	 */
	boolean isConnected();
}