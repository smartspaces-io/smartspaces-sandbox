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
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

/**
 * A Bluez Bluetooth adapter.
 * 
 * @author Keith M. Hughes
 */
public interface Adapter1 extends DBusInterface {

	void StartDiscovery();

	void SetDiscoveryFilter(Map<String, Variant> properties);

	void StopDiscovery();

	/**
	 * Remove the device with the given DBus path.
	 * 
	 * @param devicePath
	 *            the path for the device.
	 */
	void RemoveDevice(Path devicePath);
}
