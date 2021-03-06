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

import org.freedesktop.dbus.DBusInterface;

/**
 * The Bluez interface for the Bluetooth network server.
 * 
 * @author Keith M. Hughes
 *
 */
public interface NetworkServer1 extends DBusInterface
{

  void Register(String uuid, String bridge);
  void Unregister(String uuid);

}
