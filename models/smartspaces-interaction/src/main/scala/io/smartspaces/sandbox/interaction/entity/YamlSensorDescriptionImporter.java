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

import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.smartspaces.SmartSpacesException;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.DynamicObject.ArrayDynamicObjectEntry;
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;
import scala.Option;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;

/**
 * A YAML-based sensor description importer.
 * 
 * @author Keith M. Hughes
 */
public class YamlSensorDescriptionImporter implements SensorDescriptionImporter {

  /**
   * The field in all entity descriptions for the entity ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_ID = "id";

  /**
   * The field in all entity descriptions for the entity name.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_NAME = "name";

  /**
   * The field in all entity descriptions for the entity description.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_DESCRIPTION = "description";

  /**
   * The section header for the measurement types section of the file.
   */
  public static final String SECTION_HEADER_MEASUREMENT_TYPES = "measurementTypes";

  /**
   * The measurement type section field for the value type for the measurement.
   */
  public static final String SECTION_FIELD_MEASUREMENT_TYPES_VALUE_TYPE = "valueType";

  /**
   * The measurement type section field for the default unit for the
   * measurement.
   */
  public static final String SECTION_FIELD_MEASUREMENT_TYPES_DEFAULT_UNIT = "defaultUnit";

  /**
   * The measurement type section field for the aliases for the measurement.
   */
  public static final String SECTION_FIELD_MEASUREMENT_TYPES_ALIASES = "aliases";

  /**
   * The section header for the measurement units in the measurement type
   * entries.
   */
  public static final String SECTION_HEADER_MEASUREMENT_TYPES_MEASUREMENT_UNITS =
      "measurementUnits";

  /**
   * The section header for the sensor details entries.
   */
  public static final String SECTION_HEADER_SENSOR_DETAILS = "sensorDetails";

  /**
   * The sensor details section field for the channels for the sensor detail.
   */
  public static final String SECTION_FIELD_SENSOR_DETAILS_CHANNELS = "channels";

  /**
   * The section field for the measurement type of a sensor channel.
   */
  public static final String SECTION_FIELD_SENSOR_DETAILS_CHANNELS_TYPE = "type";

  /**
   * The section field for the measurement unit of a sensor channel.
   */
  public static final String SECTION_FIELD_SENSOR_DETAILS_CHANNELS_UNIT = "unit";

  /**
   * The section header for the people section of the file.
   */
  public static final String SECTION_HEADER_PEOPLE = "people";

  /**
   * The section header for the sensor section of the file.
   */
  public static final String SECTION_HEADER_SENSORS = "sensors";

  /**
   * The section field for the measurement unit of a sensor channel.
   */
  public static final String SECTION_FIELD_SENSORS_SENSOR_DETAIL = "sensorDetail";

  /**
   * The section header for the physical location section of the file.hannels
   */
  public static final String SECTION_HEADER_PHYSICAL_LOCATIONS = "physicalLocations";

  /**
   * The section header for the marker section of the file.
   */
  public static final String SECTION_HEADER_MARKERS = "markers";

  /**
   * The field in a marker entity description for the marker ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_MARKER_ID = "markerId";

  /**
   * The section header for the marker association of the file.
   */
  public static final String SECTION_HEADER_MARkER_ASSOCIATIONS = "markerAssociations";

  /**
   * The field in a marker association for the marker ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKER = "marker";

  /**
   * The field in a marker association for the marked item ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKED = "marked";

  /**
   * The section header for the sensor association section of the file.
   */
  public static final String SECTION_HEADER_SENSOR_ASSOCIATIONS = "sensorAssociations";

  /**
   * The field in a sensor association for the sensor ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSOR = "sensor";

  /**
   * The field in a sensor association for the sensed item ID.
   */
  public static final String ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSED = "sensed";

  /**
   * The section header for the configuration section of the file.
   */
  public static final String SECTION_HEADER_CONFIGURATIONS = "configurations";

  @Override
  public SensorDescriptionImporter importDescriptions(SensorRegistry sensorRegistry,
      InputStream descriptionStream) {
    Map<String, ? extends Object> configuration = readConfiguration(descriptionStream);

    DynamicObject data = new StandardDynamicObjectNavigator(configuration);

    getMeasurementTypes(sensorRegistry, data);
    getSensorDetails(sensorRegistry, data);
    getSensors(sensorRegistry, data);
    getPeople(sensorRegistry, data);
    getMarkers(sensorRegistry, data);
    getPhysicalLocations(sensorRegistry, data);

    getSensorSensedEntityAssociations(sensorRegistry, data);
    getMarkerAssociations(sensorRegistry, data);
    getEntityConfigurations(sensorRegistry, data);

    return this;
  }

  /**
   * Get all the measurement type data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getMeasurementTypes(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_MEASUREMENT_TYPES);

    for (ArrayDynamicObjectEntry measurementTypeEntry : data.getArrayEntries()) {
      DynamicObject measurementTypeData = measurementTypeEntry.down();

      String valueType =
          measurementTypeData.getRequiredString(SECTION_FIELD_MEASUREMENT_TYPES_VALUE_TYPE);
      MeasurementTypeDescription measurementType = new SimpleMeasurementTypeDescription(
          measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
          measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
          measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION), valueType,
          null);

      if (!"id".equals(valueType)) {
        String defaultUnitId =
            measurementTypeData.getRequiredString(SECTION_FIELD_MEASUREMENT_TYPES_DEFAULT_UNIT);

        measurementTypeData.down(SECTION_HEADER_MEASUREMENT_TYPES_MEASUREMENT_UNITS);
        for (ArrayDynamicObjectEntry measurementUnitEntry : data.getArrayEntries()) {
          DynamicObject measurementUnitData = measurementUnitEntry.down();

          MeasurementUnitDescription measurementUnit = new SimpleMeasurementUnitDescription(
              measurementType, measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
              measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
              measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION));

          measurementType.addMeasurementUnit(measurementUnit);

          measurementUnitData.up();
        }

        Option<MeasurementUnitDescription> measurementUnit =
            measurementType.getMeasurementUnit(defaultUnitId);
        if (measurementUnit.isDefined()) {
          measurementType.defaultUnit_$eq(measurementUnit.get());
        } else {
          // Need an error message
        }
      }
      measurementTypeData.up();

      sensorRegistry.registerMeasurementType(measurementType);
    }
    data.up();
  }

  /**
   * Get all the sensor details data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getSensorDetails(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_SENSOR_DETAILS);

    for (ArrayDynamicObjectEntry sensorDetailEntry : data.getArrayEntries()) {
      DynamicObject sensorDetailData = sensorDetailEntry.down();

      SensorDetail sensorDetail =
          new SimpleSensorDetail(sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
              sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
              sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION));

      sensorDetailData.down(SECTION_FIELD_SENSOR_DETAILS_CHANNELS);
      for (ArrayDynamicObjectEntry channelDetailEntry : data.getArrayEntries()) {
        DynamicObject channelDetailData = channelDetailEntry.down();

        String measurementTypeId =
            channelDetailData.getRequiredString(SECTION_FIELD_SENSOR_DETAILS_CHANNELS_TYPE);
        Option<MeasurementTypeDescription> measurementType =
            sensorRegistry.getMeasurementType(measurementTypeId);
        if (measurementType.isEmpty()) {
          // TODO(keith): Some sort of error message
          continue;
        }

        MeasurementUnitDescription measurementUnit = null;
        String measurementUnitId =
            channelDetailData.getString(SECTION_FIELD_SENSOR_DETAILS_CHANNELS_UNIT);
        if (measurementUnitId != null) {
          Option<MeasurementUnitDescription> measurementUnitOption =
              sensorRegistry.getMeasurementUnit(measurementUnitId);
          if (measurementUnitOption.isEmpty()) {
            // TODO(keith): Some sort of error message

            continue;
          } else {
            measurementUnit = measurementUnitOption.get();
          }
        } else {
          // The default unit is used if none was specified
          measurementUnit = measurementType.get().defaultUnit();
        }

        SensorChannelDetail channelDetail = new SimpleSensorChannelDetail(sensorDetail,
            channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
            channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
            channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION),
            measurementType.get(), measurementUnit);

        sensorDetail.addSensorChannelDetail(channelDetail);

        channelDetailData.up();
      }

      sensorRegistry.registerSensorDetail(sensorDetail);
    }
    data.up();
  }

  /**
   * Get all the people data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getPeople(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_PEOPLE);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      sensorRegistry.registerSensedEntity(new SimplePersonSensedEntityDescription(
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION)));
    }
    data.up();
  }

  /**
   * Get all the sensor data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getSensors(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_SENSORS);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      Option<SensorDetail> sensorDetail = Option.apply(null);
      String sensorDetailId = itemData.getString(SECTION_FIELD_SENSORS_SENSOR_DETAIL);
      if (sensorDetailId != null) {
        sensorDetail = sensorRegistry.getSensorDetail(sensorDetailId);
        if (sensorDetail.isEmpty()) {
          // TODO(keith): Some sort of error.
          continue;
        }
      }

      sensorRegistry.registerSensor(
          new SimpleSensorEntityDescription(itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
              itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
              itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION), sensorDetail));
    }
    data.up();
  }

  /**
   * Get all the marker data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getMarkers(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_MARKERS);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      sensorRegistry.registerMarker(
          new SimpleMarkerEntityDescription(itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
              itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
              itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION),
              itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ID)));
    }
    data.up();
  }

  /**
   * Get all the physical location data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getPhysicalLocations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_PHYSICAL_LOCATIONS);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_ID),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION)));
    }

    data.up();
  }

  /**
   * Get all the sensor/sensed entity association data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getSensorSensedEntityAssociations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_SENSOR_ASSOCIATIONS);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      sensorRegistry.associateSensorWithSensedEntity(
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSOR),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSED));
    }
    data.up();
  }

  /**
   * Get all the marker/marked entity association data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getMarkerAssociations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_MARkER_ASSOCIATIONS);

    for (ArrayDynamicObjectEntry entry : data.getArrayEntries()) {
      DynamicObject itemData = entry.down();

      sensorRegistry.associateMarkerWithMarkedEntity(
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKER),
          itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKED));
    }

    data.up();
  }

  /**
   * Get all the entity configuration data.
   * 
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  public void getEntityConfigurations(SensorRegistry sensorRegistry, DynamicObject data) {
    data.down(SECTION_HEADER_CONFIGURATIONS);

    for (ObjectDynamicObjectEntry entry : data.getObjectEntries()) {
      String entityId = entry.getProperty();

      DynamicObject configurationData = entry.getValue().down(entityId);
      if (configurationData.isObject()) {
        sensorRegistry.addConfigurationData(entityId,
            JavaConverters.mapAsScalaMapConverter(configurationData.asMap()).asScala()
                .toMap(Predef.<Tuple2<String, Object>>conforms()));
      }
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
  private Map<String, ? extends Object> readConfiguration(InputStream inputStream) {
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
