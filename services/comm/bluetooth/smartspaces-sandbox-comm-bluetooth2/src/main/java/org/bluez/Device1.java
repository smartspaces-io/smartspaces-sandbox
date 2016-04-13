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

package org.bluez;

import java.util.Map;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Variant;

public interface Device1 extends DBusInterface {

	/**
	 * Connect to the device.
	 */
	void Connect();

	/**
	 * Disconnect from the device.
	 */
	void Disconnect();

	/**
	 * Connect to a specific profile on the device.
	 * 
	 * @param uuid
	 *            the UUID of the profile
	 */
	void ConnectProfile(String uuid);

	/**
	 * Disconnect from a specific profile on the device.
	 * 
	 * @param uuid
	 *            the UUID of the profile
	 */
	void DisconnectProfile(String uuid);

	/**
	 * Pair with the device.
	 */
	void Pair();
}