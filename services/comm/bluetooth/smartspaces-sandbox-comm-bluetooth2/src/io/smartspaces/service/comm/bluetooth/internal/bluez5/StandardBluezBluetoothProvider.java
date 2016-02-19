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

package io.smartspaces.service.comm.bluetooth.internal.bluez5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

import io.smartspaces.service.comm.bluetooth.BluetoothAdapter;
import io.smartspaces.service.comm.bluetooth.BluetoothDevice;
import io.smartspaces.service.comm.bluetooth.BluetoothProvider;
import io.smartspaces.support.dbus.ObjectManager;

/**
 * The provider for acess to the BlueZ bluetooth services in DBus.
 * 
 * @author Keith M. Hughes
 */
public class StandardBluezBluetoothProvider implements BluetoothProvider {
	public static void main(String[] args) throws Exception {
		StandardBluezBluetoothProvider bluezProvider = new StandardBluezBluetoothProvider();

		bluezProvider.startup();

		bluezProvider.printManagedObjects();

		BluetoothAdapter adapter = bluezProvider.newAdapter("/org/bluez/hci0");
		adapter.startup();

		Thread.sleep(10000);

		System.out.println(bluezProvider.listAdapters());

		System.out.println(bluezProvider.listDevices());

		adapter.startScanning();

		System.out.println("Now scanning");

		Thread.sleep(5000);
		
		BluetoothDevice device = adapter.newDevice("B0:B4:48:BD:D0:83");
		device.startup();
		
		Thread.sleep(5000);

		System.out.println(bluezProvider.listAdapters());

		System.out.println(bluezProvider.listDevices());

		Thread.sleep(100000);

		adapter.shutdown();

		bluezProvider.shutdown();
	}

	/**
	 * The DBus connection used to talk to the Bluez service.
	 */
	private DBusConnection dbusConnection = null;

	/**
	 * The DBus ObjectManager for the root Bluez object.
	 */
	private ObjectManager bluezObjectManager;

	/**
	 * The DBus signal handler for the ObjectManager's InterfacesAdded signal.
	 */
	private DBusSigHandler<ObjectManager.InterfacesAdded> interfacesAddedSignalHandler;

	/**
	 * The DBus signal handler for the ObjectManager's InterfacesRemoved signal.
	 */
	private DBusSigHandler<ObjectManager.InterfacesRemoved> interfacesRemovedSignalHandler;

	/**
	 * The unique name of the DBus bus for Bluez.
	 */
	private String bluezDbusBusName;

	@Override
	public void startup() throws Exception {
		dbusConnection = DBusConnection.getConnection(DBusConnection.SYSTEM);

		DBus dbus = dbusConnection.getRemoteObject("org.freedesktop.DBus", "/or/freedesktop/DBus", DBus.class);

		bluezDbusBusName = dbus.GetNameOwner("org.bluez");

		bluezObjectManager = (ObjectManager) dbusConnection.getRemoteObject("org.bluez", "/", ObjectManager.class);

		interfacesAddedSignalHandler = new DBusSigHandler<ObjectManager.InterfacesAdded>() {
			@Override
			public void handle(ObjectManager.InterfacesAdded signal) {
				System.out.println("Interfaces added");
			}
		};

		interfacesRemovedSignalHandler = new DBusSigHandler<ObjectManager.InterfacesRemoved>() {

			@Override
			public void handle(ObjectManager.InterfacesRemoved signal) {
				System.out.println("Interfaces removed");
			}
		};

		dbusConnection.addSigHandler(ObjectManager.InterfacesAdded.class, bluezDbusBusName, bluezObjectManager,
				interfacesAddedSignalHandler);
		dbusConnection.addSigHandler(ObjectManager.InterfacesRemoved.class, bluezDbusBusName, bluezObjectManager,
				interfacesRemovedSignalHandler);
	}

	@Override
	public void shutdown() throws Exception {
		dbusConnection.removeSigHandler(ObjectManager.InterfacesAdded.class, interfacesAddedSignalHandler);
		dbusConnection.removeSigHandler(ObjectManager.InterfacesRemoved.class, interfacesRemovedSignalHandler);

		dbusConnection.disconnect();
	}

	@Override
	public List<String> listAdapters() throws Exception {
		return getObjectsByInterface(BluezConstants.BLUEZ_ADAPTER_INTERFACE);
	}

	@Override
	public List<String> listDevices() throws Exception {
		return getObjectsByInterface(BluezConstants.BLUEZ_DEVICE_INTERFACE);
	}

	@Override
	public BluetoothAdapter newAdapter(String adapterObjectPath) {
		return new StandardBluezBluetoothAdapter(this, adapterObjectPath);
	}

	/**
	 * Get a list of all BlueZ objects that implement a given DBus interface.
	 * 
	 * @param objectInterface
	 *            the name of the interface to scan for
	 * 
	 * @return a list of all objects implementing the specified interface
	 */
	private List<String> getObjectsByInterface(String objectInterface) {
		List<String> results = new ArrayList<>();

		for (Entry<Path, Map<String, Map<String, Variant>>> objectByPath : bluezObjectManager.GetManagedObjects()
				.entrySet()) {
			if (objectByPath.getValue().containsKey(objectInterface)) {
				results.add(objectByPath.getKey().toString());
			}
		}
		return results;
	}

	/**
	 * Get the dbus connection.
	 * 
	 * @return the dbus connection
	 */
	DBusConnection getDbusConnection() {
		return dbusConnection;
	}

	/**
	 * Get the unique DBus bus name for the BlueZ service.
	 * 
	 * @return the BlueZ DBus bus name
	 */
	String getBluezDbusBusName() {
		return bluezDbusBusName;
	}

	private void printManagedObjects() {
		System.out.println(bluezObjectManager.GetManagedObjects());
	}
}
