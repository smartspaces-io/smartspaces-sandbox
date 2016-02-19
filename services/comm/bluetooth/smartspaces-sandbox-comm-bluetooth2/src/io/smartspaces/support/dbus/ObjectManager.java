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

package io.smartspaces.support.dbus;

import java.util.List;
import java.util.Map;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 * The BlueZ object for the DBus ObjectManager. The ObjectManager tracks what items are being
 * added to a place in a DBus object hierarchy on a given DBus bus.
 * 
 * @author Keith M. Hughes
 */
@DBusInterfaceName("org.freedesktop.DBus.ObjectManager")
public interface ObjectManager extends DBusInterface {

	/**
	 * Get all objects managed by where the object manager is in the DBus
	 * hierarchy.
	 * 
	 * @return a map, keyed by object paths, value a map of interfaces to
	 *         properties for that interface.
	 */
	Map<Path, Map<String, Map<String, Variant>>> GetManagedObjects();

	/**
	 * The DBus signal for objects being added to an object hierarchy and the
	 * interfaces those objects support.
	 * 
	 * @author Keith M. Hughes
	 */
	public static class InterfacesAdded extends DBusSignal {

		/**
		 * The DBus object path for the object whose interfaces are being added.
		 */
		private final Path objectPath;

		/**
		 * A map of the interfaces added, keyed by interface name. and values of
		 * the current values of properties for the interfaces.
		 */
		private final Map<String, Map<String, Variant>> interfacesAdded;

		/**
		 * Construct a new signal object for InterfacesAdded.
		 * 
		 * @param path
		 *            the path of the object that sent the signal
		 * @param objectPath
		 *            the DBus path to the object that has been added
		 * @param interfacesAdded
		 *            a map of all interfaces added for the new object to their
		 *            properties
		 * 
		 * @throws DBusException
		 *             a DBus error happened
		 */
		public InterfacesAdded(String path, Path objectPath, Map<String, Map<String, Variant>> interfacesAdded)
				throws DBusException {
			super(path, objectPath, interfacesAdded);
			this.objectPath = objectPath;
			this.interfacesAdded = interfacesAdded;
		}

		/**
		 * Get the DBus object path for the object whose interfaces are being
		 * added.
		 * 
		 * @return the path
		 */
		public Path getObjectPath() {
			return objectPath;
		}

		/**
		 * Get the map of the interfaces added, keyed by interface name. and
		 * values of the current values of properties for the interfaces.
		 * 
		 * @return the interface map
		 */
		public Map<String, Map<String, Variant>> getInterfacesAdded() {
			return interfacesAdded;
		}
	}

	/**
	 * The DBus signal for objects being removed from an object hierarchy and
	 * the interfaces those objects support.
	 * 
	 * @author Keith M. Hughes
	 */
	public static class InterfacesRemoved extends DBusSignal {

		/**
		 * The DBus object path for the object whose interfaces are being added.
		 */
		private final Path objectPath;

		/**
		 * The names of the interfaces that were removed.
		 */
		private List<String> interfacesRemoved;

		/**
		 * Construct a new signal object for InterfacesRemoved.
		 * 
		 * @param path
		 *            the path of the object that sent the signal
		 * @param objectPath
		 *            the DBus path to the object that has been removed
		 * @param interfacesRemoved
		 *            a list of ll interface names being removed
		 * 
		 * @throws DBusException
		 *             a DBus error happened
		 */
		public InterfacesRemoved(String path, Path objectPath, List<String> interfacesRemoved) throws DBusException {
			super(path, objectPath, interfacesRemoved);

			this.objectPath = objectPath;
			this.interfacesRemoved = interfacesRemoved;
		}

		/**
		 * Get the DBus object path for the object whose interfaces are being
		 * removed.
		 * 
		 * @return the object path
		 */
		public Path getObjectPath() {
			return objectPath;
		}

		/**
		 * Get the list of names of interfaces being removed.
		 * 
		 * @return the names of interfaces being removed
		 */
		public List<String> getInterfacesRemoved() {
			return interfacesRemoved;
		}
	}

}
