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
 * A Bluetooth adapter.
 * 
 * @author Keith M. Hughes
 */
public interface BluetoothAdapter {

	void startup() throws Exception;

	void shutdown() throws Exception;

	/**
	 * Tell the adapter to start scanning for devices.
	 * 
	 * @throws Exception
	 */
	void startScanning() throws Exception;

	/**
	 * Tell the adapter to stop trying to discover devices.
	 * 
	 * @throws Exception
	 */
	void stopScanning() throws Exception;

	/**
	 * Is the adapter scanning?
	 * 
	 * @return {@code true} if scanning, {@code false} otherwise
	 */
	boolean isScanning();

	/**
	 * Set whether the adapter is powered or not.
	 * 
	 * @param powered
	 *            {@code true} to power up, {@code false} to power down
	 */
	void setPowered(boolean powered);

	/**
	 * Is the adapter powered?
	 * 
	 * @return {@code true} if powered up, {@code false} otherwise
	 */
	boolean isPowered();

	/**
	 * Get a device under the current adapter.
	 * 
	 * @param address
	 *            the Bluetooth address for the device.
	 * 
	 * @return the device object
	 */
	BluetoothDevice newDevice(String address);

}