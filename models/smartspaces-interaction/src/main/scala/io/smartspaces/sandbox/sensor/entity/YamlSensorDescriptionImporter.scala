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

package io.smartspaces.sandbox.sensor.entity

import java.io.InputStream
import java.util.Map

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.mapAsScalaMap
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import io.smartspaces.logging.ExtendedLog
import io.smartspaces.util.data.dynamic.DynamicObject
import io.smartspaces.util.data.dynamic.DynamicObject.ArrayDynamicObjectEntry
import io.smartspaces.util.data.dynamic.DynamicObject.ObjectDynamicObjectEntry
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator
import io.smartspaces.util.data.json.StandardYamlMapper
import io.smartspaces.util.data.json.YamlMapper

/**
 * A YAML-based sensor description importer.
 *
 * @author Keith M. Hughes
 */
class YamlSensorDescriptionImporter(descriptionStream: InputStream, log: ExtendedLog) extends SensorDescriptionImporter {

  /**
   * The field in all entity descriptions for the entity ID.
   */
  val ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID = "externalId"

  /**
   * The field in all entity descriptions for the entity name.
   */
  val ENTITY_DESCRIPTION_FIELD_NAME = "name"

  /**
   * The field in all entity descriptions for the entity description.
   */
  val ENTITY_DESCRIPTION_FIELD_DESCRIPTION = "description"

  /**
   * The section header for the measurement types section of the file.
   */
  val SECTION_HEADER_MEASUREMENT_TYPES = "measurementTypes"

  /**
   * The measurement type section field for the value type for the measurement.
   */
  val SECTION_FIELD_MEASUREMENT_TYPES_VALUE_TYPE = "valueType"

  /**
   * The measurement type section field for the default unit for the
   * measurement.
   */
  val SECTION_FIELD_MEASUREMENT_TYPES_DEFAULT_UNIT = "defaultUnit"

  /**
   * The measurement type section field for the aliases for the measurement.
   */
  val SECTION_FIELD_MEASUREMENT_TYPES_ALIASES = "aliases"

  /**
   * The section header for the measurement units in the measurement type
   * entries.
   */
  val SECTION_HEADER_MEASUREMENT_TYPES_MEASUREMENT_UNITS =
    "measurementUnits"

  /**
   * The section header for the sensor details entries.
   */
  val SECTION_HEADER_SENSOR_DETAILS = "sensorDetails"

  /**
   * The sensor details section field for the channels for the sensor detail.
   */
  val SECTION_FIELD_SENSOR_DETAILS_CHANNELS = "channels"

  /**
   * The section field for the measurement type of a sensor channel.
   */
  val SECTION_FIELD_SENSOR_DETAILS_CHANNELS_TYPE = "type"

  /**
   * The section field for the measurement unit of a sensor channel.
   */
  val SECTION_FIELD_SENSOR_DETAILS_CHANNELS_UNIT = "unit"

  /**
   * The section header for the people section of the file.
   */
  val SECTION_HEADER_PEOPLE = "people"

  /**
   * The section header for the sensor section of the file.
   */
  val SECTION_HEADER_SENSORS = "sensors"

  /**
   * The section field for the measurement unit of a sensor channel.
   */
  val SECTION_FIELD_SENSORS_SENSOR_DETAIL = "sensorDetail"

  /**
   * The section field for the update time limit for a sensor.
   */
  val SECTION_FIELD_SENSORS_SENSOR_UPDATE_TIME_LIMIT = "sensorUpdateTimeLimit"

  /**
   * The section field for the heartbeat time limit for a sensor.
   */
  val SECTION_FIELD_SENSORS_SENSOR_HEARTBEAT_UPDATE_TIME_LIMIT = "sensorHeartbeatUpdateTimeLimit"

  /**
   * The section field for whether a sensor is to be considered active or not.
   */
  val SECTION_FIELD_SENSORS_ACTIVE = "active"

  /**
   * The section field default value for whether a sensor is to be considered active or not.
   */
  val SECTION_FIELD_DEFAULT_VALUE_SENSORS_ACTIVE = true

  /**
   * The section header for the physical location section of the file.hannels
   */
  val SECTION_HEADER_PHYSICAL_LOCATIONS = "physicalLocations"

  /**
   * The section header for the marker section of the file.
   */
  val SECTION_HEADER_MARKERS = "markers"

  /**
   * The field in a marker entity description for the marker ID.
   */
  val ENTITY_DESCRIPTION_FIELD_MARKER_ID = "markerId"

  /**
   * The section header for the marker association of the file.
   */
  val SECTION_HEADER_MARkER_ASSOCIATIONS = "markerAssociations"

  /**
   * The field in a marker association for the marker ID.
   */
  val ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKER = "marker"

  /**
   * The field in a marker association for the marked item ID.
   */
  val ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKED = "marked"

  /**
   * The section header for the sensor association section of the file.
   */
  val SECTION_HEADER_SENSOR_ASSOCIATIONS = "sensorAssociations"

  /**
   * The field in a sensor association for the sensor ID.
   */
  val ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSOR = "sensor"

  /**
   * The field in a sensor association for the sensed item ID.
   */
  val ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSED = "sensed"

  /**
   * The section header for the configuration section of the file.
   */
  val SECTION_HEADER_CONFIGURATIONS = "configurations"

  /**
   * The ID to be given to entities.
   *
   * TODO(keith): This should be created in the registry.
   */
  private var id: Integer = 0

  private val MAPPER: YamlMapper = StandardYamlMapper.INSTANCE

  override def importDescriptions(sensorRegistry: SensorRegistry): SensorDescriptionImporter = {
    val configuration: Map[String, Object] = MAPPER.parseObject(descriptionStream)

    val data: DynamicObject = new StandardDynamicObjectNavigator(configuration)

    getMeasurementTypes(sensorRegistry, data)
    getSensorDetails(sensorRegistry, data)
    getSensors(sensorRegistry, data)
    getPeople(sensorRegistry, data)
    getMarkers(sensorRegistry, data)
    getPhysicalLocations(sensorRegistry, data)

    getSensorSensedEntityAssociations(sensorRegistry, data)
    getMarkerAssociations(sensorRegistry, data)
    getEntityConfigurations(sensorRegistry, data)

    return this
  }

  /**
   * Get all the measurement type data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getMeasurementTypes(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_MEASUREMENT_TYPES)

    data.getArrayEntries().foreach((measurementTypeEntry: ArrayDynamicObjectEntry) => {
      val measurementTypeData: DynamicObject = measurementTypeEntry.down()

      val valueType =
        measurementTypeData.getRequiredString(SECTION_FIELD_MEASUREMENT_TYPES_VALUE_TYPE)
      val measurementType = new SimpleMeasurementTypeDescription(getNextId(),
        measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        measurementTypeData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION), valueType,
        null)

      if (!"id".equals(valueType)) {
        val defaultUnitId =
          measurementTypeData.getRequiredString(SECTION_FIELD_MEASUREMENT_TYPES_DEFAULT_UNIT)

        measurementTypeData.down(SECTION_HEADER_MEASUREMENT_TYPES_MEASUREMENT_UNITS)
        data.getArrayEntries().foreach((measurementUnitEntry: ArrayDynamicObjectEntry) => {
          val measurementUnitData = measurementUnitEntry.down()

          val measurementUnit =
            new SimpleMeasurementUnitDescription(measurementType, getNextId(),
              measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
              measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
              measurementUnitData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION))

          measurementType.addMeasurementUnit(measurementUnit)

          measurementUnitData.up()
        })

        val measurementUnit = measurementType.getMeasurementUnit(defaultUnitId)
        if (measurementUnit.isDefined) {
          measurementType.defaultUnit = measurementUnit.get
        } else {
          // Need an error message
        }
      }
      measurementTypeData.up()

      sensorRegistry.registerMeasurementType(measurementType)
    })
    data.up()
  }

  /**
   * Get all the sensor details data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getSensorDetails(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_SENSOR_DETAILS)

    data.getArrayEntries().foreach((sensorDetailEntry) => {
      val sensorDetailData = sensorDetailEntry.down()

      var sensorUpdateTimeLimit: Option[Long] = None
      val sensorUpdateTimeLimitValue: java.lang.Long = sensorDetailData.getLong(SECTION_FIELD_SENSORS_SENSOR_UPDATE_TIME_LIMIT)
      if (sensorUpdateTimeLimitValue != null) {
        sensorUpdateTimeLimit = Option(sensorUpdateTimeLimitValue)
      }

      var sensorHeartbeatUpdateTimeLimit: Option[Long] = None
      val sensorHeartbeatUpdateTimeLimitValue: java.lang.Long = sensorDetailData.getLong(SECTION_FIELD_SENSORS_SENSOR_HEARTBEAT_UPDATE_TIME_LIMIT)
      if (sensorHeartbeatUpdateTimeLimitValue != null) {
        sensorHeartbeatUpdateTimeLimit = Option(sensorHeartbeatUpdateTimeLimitValue)
      }

      val sensorDetail = new SimpleSensorDetail(getNextId(),
        sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        sensorDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION),
        sensorUpdateTimeLimit, sensorHeartbeatUpdateTimeLimit)

      sensorDetailData.down(SECTION_FIELD_SENSOR_DETAILS_CHANNELS)
      data.getArrayEntries().foreach((channelDetailEntry: ArrayDynamicObjectEntry) => breakable {
        val channelDetailData = channelDetailEntry.down()

        val measurementTypeId =
          channelDetailData.getRequiredString(SECTION_FIELD_SENSOR_DETAILS_CHANNELS_TYPE)
        val measurementType = sensorRegistry.getMeasurementTypeByExternalId(measurementTypeId)
        if (measurementType.isEmpty) {
          // TODO(keith): Some sort of error message
          break
        }

        var measurementUnit: MeasurementUnitDescription = null
        val measurementUnitId =
          channelDetailData.getString(SECTION_FIELD_SENSOR_DETAILS_CHANNELS_UNIT)
        if (measurementUnitId != null) {
          var measurementUnitOption = sensorRegistry.getMeasurementUnitByExternalId(measurementUnitId)
          if (measurementUnitOption.isEmpty) {
            // TODO(keith): Some sort of error message

            break
          } else {
            measurementUnit = measurementUnitOption.get
          }
        } else {
          // The default unit is used if none was specified
          measurementUnit = measurementType.get.defaultUnit
        }

        val channelDetail = new SimpleSensorChannelDetail(sensorDetail,
          channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
          channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
          channelDetailData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION),
          measurementType.get, measurementUnit)

        sensorDetail.addSensorChannelDetail(channelDetail)

        channelDetailData.up()
      })

      sensorRegistry.registerSensorDetail(sensorDetail)
    })
    data.up()
  }

  /**
   * Get all the people data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getPeople(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_PEOPLE)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => {
      val itemData = entry.down()

      sensorRegistry.registerSensedEntity(new SimplePersonSensedEntityDescription(getNextId(),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION)))
    })
    data.up()
  }

  /**
   * Get all the sensor data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getSensors(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_SENSORS)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => breakable {
      val itemData = entry.down()

      var sensorDetail: Option[SensorDetail] = None
      var sensorDetailId = itemData.getString(SECTION_FIELD_SENSORS_SENSOR_DETAIL)
      if (sensorDetailId != null) {
        sensorDetail = sensorRegistry.getSensorDetailByExternalId(sensorDetailId)
        if (sensorDetail.isEmpty) {
          // TODO(keith): Some sort of error.
          break
        }
      }

      var sensorUpdateTimeLimit: Option[Long] = None
      val updateTimeLimitValue = itemData.getLong(SECTION_FIELD_SENSORS_SENSOR_UPDATE_TIME_LIMIT)
      if (updateTimeLimitValue != null) {
        sensorUpdateTimeLimit = Option(updateTimeLimitValue)
      } else {
        if (sensorDetail.isDefined) {
          sensorUpdateTimeLimit = sensorDetail.get.sensorUpdateTimeLimit
        } else {
          sensorUpdateTimeLimit = None
        }
      }

      var sensorHeartbeatUpdateTimeLimit: Option[Long] = None
      val updateHeartbeatTimeLimitValue = itemData.getLong(SECTION_FIELD_SENSORS_SENSOR_UPDATE_TIME_LIMIT)
      if (updateHeartbeatTimeLimitValue != null) {
        sensorHeartbeatUpdateTimeLimit = Option(updateHeartbeatTimeLimitValue)
      } else {
        if (sensorDetail.isDefined) {
          sensorHeartbeatUpdateTimeLimit = sensorDetail.get.sensorHeartbeatUpdateTimeLimit
        } else {
          sensorHeartbeatUpdateTimeLimit = None
        }
      }
      val entity = new SimpleSensorEntityDescription(getNextId(),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION), sensorDetail, sensorUpdateTimeLimit, sensorHeartbeatUpdateTimeLimit)

      entity.active = itemData.getBoolean(SECTION_FIELD_SENSORS_ACTIVE, SECTION_FIELD_DEFAULT_VALUE_SENSORS_ACTIVE)

      sensorRegistry.registerSensor(entity)
    })
    data.up()
  }

  /**
   * Get all the marker data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getMarkers(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_MARKERS)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => {
      val itemData = entry.down()

      sensorRegistry.registerMarker(new SimpleMarkerEntityDescription(getNextId(),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ID)))
    })
    data.up()
  }

  /**
   * Get all the physical location data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getPhysicalLocations(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_PHYSICAL_LOCATIONS)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => {
      val itemData = entry.down()

      sensorRegistry.registerSensedEntity(new SimplePhysicalSpaceSensedEntityDescription(getNextId(),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_EXTERNAL_ID),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_NAME),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_DESCRIPTION)))
    })

    data.up()
  }

  /**
   * Get all the sensor/sensed entity association data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getSensorSensedEntityAssociations(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_SENSOR_ASSOCIATIONS)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => {
      val itemData = entry.down()

      sensorRegistry.associateSensorWithSensedEntity(
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSOR),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_SENSOR_ASSOCIATION_SENSED))
    })
    data.up()
  }

  /**
   * Get all the marker/marked entity association data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getMarkerAssociations(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_MARkER_ASSOCIATIONS)

    data.getArrayEntries().foreach((entry: ArrayDynamicObjectEntry) => {
      val itemData = entry.down()

      sensorRegistry.associateMarkerWithMarkedEntity(
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKER),
        itemData.getRequiredString(ENTITY_DESCRIPTION_FIELD_MARKER_ASSOCIATION_MARKED))
    })

    data.up()
  }

  /**
   * Get all the entity configuration data.
   *
   * @param sensorRegistry
   *          the sensor registry to store the data in
   * @param data
   *          the data read from the input stream
   */
  def getEntityConfigurations(sensorRegistry: SensorRegistry, data: DynamicObject): Unit = {
    data.down(SECTION_HEADER_CONFIGURATIONS)

    data.getObjectEntries().foreach((entry: ObjectDynamicObjectEntry) => {
      val entityId = entry.getProperty()

      val configurationData = entry.getValue().down(entityId)
      if (configurationData.isObject) {
        sensorRegistry.addConfigurationData(entityId, configurationData.asMap().toMap)
      }
    })

    data.up()
  }

  /**
   * Get the next "database" ID.
   *
   * @return the next ID.
   */
  private def getNextId(): String = {
    id = id + 1
    return Integer.toString(id)
  }
}
