/**
 * 
 */
package io.smartspaces.service.comm.bluetooth.internal.bluez5;

/**
 * A collection of constants for working with the BlueZ DBus API.
 * 
 * @author Keith M. Hughes
 */
public class BluezConstants {
	
	/**
	 * The well-known DBus bus name for BlueZ
	 */
	public static final String BLUEZ_DBUS_BUSNAME = "org.bluez";
	
	/**
	 * The Bluez DBus interface for Bluetooth adapters.
	 */
	public static final String BLUEZ_ADAPTER_INTERFACE = "org.bluez.Adapter1";

	/**
	 * The Bluez DBus interface for Bluetooth adapters.
	 */
	public static final String BLUEZ_DEVICE_INTERFACE = "org.bluez.Device1";
}
