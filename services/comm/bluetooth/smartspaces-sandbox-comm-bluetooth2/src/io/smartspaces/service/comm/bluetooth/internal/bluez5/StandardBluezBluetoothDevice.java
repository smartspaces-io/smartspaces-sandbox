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

import org.bluez.Device1;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;

import io.smartspaces.service.comm.bluetooth.BluetoothDevice;
import io.smartspaces.support.dbus.Properties;

/**
 * A Bluetooth device accessed through Bluez.
 * 
 * @author Keith M. Hughes
 */
public class StandardBluezBluetoothDevice implements BluetoothDevice {

	private static final String BLUEZ_PROPERTY_DEVICE_ADDRESS = "Address";

	private static final String BLUEZ_PROPERTY_DEVICE_NAME = "Name";

	private static final String BLUEZ_PROPERTY_DEVICE_RSSI = "RSSI";

	private static final String BLUEZ_PROPERTY_DEVICE_CONNECTED = "Connected";

	/**
	 * The Bluez remote object for the bluetooth device.
	 */
	private Device1 bluezDevice;

	/**
	 * The DBus properties interface for the bluetooth device.
	 */
	private Properties bluezDeviceProperties;

	/**
	 * The DBus object path for the device.
	 */
	private String deviceObjectPath;

	/**
	 * The bluez provider.
	 */
	private StandardBluezBluetoothProvider bluezProvider;

	/**
	 * The signal handler for changing properties.
	 */
	private DBusSigHandler<Properties.PropertiesChanged> propertiesChangedSignalHandler;

	/**
	 * Construct a new bluetooth device.
	 * 
	 * @param bluezProvider
	 *            the bluez provider
	 * @param deviceObjectPath
	 *            the DBus object path to the device
	 */
	public StandardBluezBluetoothDevice(StandardBluezBluetoothProvider bluezProvider, String deviceObjectPath) {
		this.bluezProvider = bluezProvider;
		this.deviceObjectPath = deviceObjectPath;
	}

	@Override
	public void startup() throws Exception {
		DBusConnection dbusConnection = bluezProvider.getDbusConnection();

		bluezDevice = (Device1) dbusConnection.getRemoteObject(BluezConstants.BLUEZ_DBUS_BUSNAME, deviceObjectPath,
				Device1.class);

		bluezDeviceProperties = (Properties) dbusConnection.getRemoteObject(BluezConstants.BLUEZ_DBUS_BUSNAME,
				deviceObjectPath, Properties.class);

		propertiesChangedSignalHandler = new DBusSigHandler<Properties.PropertiesChanged>() {
			@Override
			public void handle(Properties.PropertiesChanged propertiesChanged) {
				handlePropertiesChangedDbusSignal(propertiesChanged);
			}
		};

		dbusConnection.addSigHandler(Properties.PropertiesChanged.class, bluezProvider.getBluezDbusBusName(),
				bluezDeviceProperties, propertiesChangedSignalHandler);
	}

	@Override
	public void shutdown() throws Exception {
		bluezProvider.getDbusConnection().removeSigHandler(Properties.PropertiesChanged.class,
				propertiesChangedSignalHandler);
	}

	void getProperties() {
		System.out.println(bluezDeviceProperties.GetAll("org.bluez.Device1"));
	}

	@Override
	public void connect() {
		bluezDevice.Connect();
	}

	@Override
	public void disconnect() {
		bluezDevice.Disconnect();
	}

	@Override
	public String getId() {
		Variant<String> value = bluezDeviceProperties.Get(BluezConstants.BLUEZ_DEVICE_INTERFACE,
				BLUEZ_PROPERTY_DEVICE_ADDRESS);

		return value.getValue();
	}

	@Override
	public String getName() {
		Variant<String> value = bluezDeviceProperties.Get(BluezConstants.BLUEZ_DEVICE_INTERFACE,
				BLUEZ_PROPERTY_DEVICE_NAME);

		return value.getValue();
	}

	@Override
	public int getRssi() {
		Variant<Short> value = bluezDeviceProperties.Get(BluezConstants.BLUEZ_DEVICE_INTERFACE,
				BLUEZ_PROPERTY_DEVICE_RSSI);

		return value.getValue();
	}

	@Override
	public boolean isConnected() {
		Variant<Boolean> value = bluezDeviceProperties.Get(BluezConstants.BLUEZ_DEVICE_INTERFACE,
				BLUEZ_PROPERTY_DEVICE_CONNECTED);

		return value.getValue();
	}


	/**
	 * Handle a DBus signal for properties changes on the device.
	 * 
	 * @param propertiesChanged
	 *            the DBus Properties Changed signal 
	 */
	private void handlePropertiesChangedDbusSignal(Properties.PropertiesChanged propertiesChanged) {
		System.out.println("Properties changed for Bluetooth device " + propertiesChanged.getPropertiesChanged());
	}
}
