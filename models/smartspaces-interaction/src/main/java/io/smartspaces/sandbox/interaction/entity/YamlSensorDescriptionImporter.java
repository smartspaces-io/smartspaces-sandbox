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

package io.smartspaces.sandbox.interaction.entity;

import io.smartspaces.SmartSpacesException;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ArrayDynamicObjectEntry;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.Map;

/**
 * A YAML-based sensor description importer.
 * 
 * @author Keith M. Hughes
 */
public class YamlSensorDescriptionImporter implements SensorDescriptionImporter {

  @Override
  public SensorDescriptionImporter importDescriptions(SensorRegistry sensorRegistry,
      InputStream descriptionStream) {
    Map<String, Object> configuration = readConfiguration(descriptionStream);

    DynamicObject data = new StandardDynamicObjectNavigator(configuration);

    getSensors(sensorRegistry, data);
    getPeople(sensorRegistry, data);
    getMarkers(sensorRegistry, data);
    getPhysicalLocations(sensorRegistry, data);

    getSensorSensedEntityAssociations(sensorRegistry, data);
    getMarkerAssociations(sensorRegistry, data);

    return this;
  }

  private void getPeople(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down("people");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject personData = entry.down();

      sensorRegistry.registerSensedEntity(
          new SimplePersonSensedEntityDescription(personData.getRequiredString("id"),
              personData.getRequiredString("name"), personData.getRequiredString("description")));
    }
    data.up();
  }

  private void getSensors(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down("sensors");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject sensorData = entry.down();

      sensorRegistry
          .registerSensor(new SimpleSensorEntityDescription(sensorData.getRequiredString("id"),
              sensorData.getRequiredString("name"), sensorData.getRequiredString("description")));
    }
    data.up();
  }

  private void getMarkers(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down("markers");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject markerData = entry.down();

      sensorRegistry.registerMarker(new SimpleMarkerEntityDescription(
          markerData.getRequiredString("id"), markerData.getRequiredString("name"),
          markerData.getRequiredString("description"), markerData.getRequiredString("markerId")));
    }
    data.up();
  }

  private void getPhysicalLocations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down("physicalLocations");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject markerData = entry.down();

      sensorRegistry.registerSensedEntity(
          new SimplePhysicalSpaceSensedEntityDescription(markerData.getRequiredString("id"),
              markerData.getRequiredString("name"), markerData.getRequiredString("description")));
    }

    data.up();
  }

  private void getSensorSensedEntityAssociations(SensorRegistry sensorRegistry,
      DynamicObject data) {
    data.down("sensorAssociations");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject markerData = entry.down();

      sensorRegistry.associateSensorWithSensedEntity(markerData.getRequiredString("sensor"),
          markerData.getRequiredString("sensed"));
    }
    data.up();
  }

  private void getMarkerAssociations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down("markerAssociations");

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject markerData = entry.down();

      sensorRegistry.associateMarkerWithMarkedEntity(markerData.getRequiredString("marker"),
          markerData.getRequiredString("marked"));
    }

    data.up();
  }

  /**
   * Read the configuration stream.
   * 
   * @param inputStream
   *          the stream for the configuration
   * 
   * @return the configuration
   */
  private Map<String, Object> readConfiguration(InputStream inputStream) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> configuration = mapper.readValue(inputStream, Map.class);
      return configuration;
    } catch (Exception e) {
      throw new SmartSpacesException("Could not parse sensor description input stream", e);
    }
  }
}
