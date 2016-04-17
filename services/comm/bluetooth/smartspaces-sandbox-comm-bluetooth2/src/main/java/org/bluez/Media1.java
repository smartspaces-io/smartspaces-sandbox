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

/**
 * A Bluetooth Media device.
 * 
 * @author Keith M. Hughes
 */

public interface Media1 extends DBusInterface {

	void RegisterEndpoint(DBusInterface endpoint, Map<String, Variant> properties);

	void UnregisterEndpoint(DBusInterface endpoint);

	void RegisterPlayer(DBusInterface player, Map<String, Variant> properties);

	void UnregisterPlayer(DBusInterface player);
}
