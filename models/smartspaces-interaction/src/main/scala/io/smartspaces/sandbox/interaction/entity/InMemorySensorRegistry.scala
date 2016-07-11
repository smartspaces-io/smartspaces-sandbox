/*
 * Copyright (C) 2016 Keith M. Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not
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

package io.smartspaces.sandbox.interaction.entity

import scala.collection.mutable._

/**
 * A sensor registry totally contained in memory.
 *
 * @author Keith M. Hughes
 */
class InMemorySensorRegistry extends SensorRegistry {

  /**
   * A map of IDs to their measurement types.
   */
  private val idToMeasurementType: Map[String, MeasurementTypeDescription] = new HashMap

  /**
   * A map of IDs to measurement units.
   */
  private val idToMeasurementUnit: Map[String, MeasurementUnitDescription] = new HashMap

  /**
   * A map of sensor IDs to their description.
   */
  private val idToSensor: Map[String, SensorEntityDescription] = new HashMap

  /**
   * A map of marker IDs to their description.
   */
  private val idToMarker: Map[String, MarkerEntityDescription] = new HashMap

  /**
   * A map of markable IDs to their description.
   */
  private val idToMarkable: Map[String, MarkableEntityDescription] = new HashMap

  /**
   * A map of marker IDs to their description.
   */
  private val markerIdToMarker: Map[String, MarkerEntityDescription] = new HashMap

  /**
   * A map of marker IDs to their description.
   */
  private val markerIdToMarkable: Map[String, MarkableEntityDescription] = new HashMap

  /**
   * A map of sensed entities IDs to their description
   */
  private val idToSensed: Map[String, SensedEntityDescription] = new HashMap

  /**
   * The associations between sensors and what entity is being sensed by them.
   */
  private val sensorSensedEntityAssociations: ListBuffer[SimpleSensorSensedEntityAssociation] =
    new ListBuffer

  /**
   * The associations between markers and what entity is being marked by them.
   */
  private val markerMarkedEntityAssociations: ListBuffer[MarkerMarkedEntityAssociation] = new ListBuffer

  /**
   * The entity configurations.
   */
  private val configurations: Map[String, Map[String, AnyRef]] = new HashMap

  override def registerMeasurementType(measurementType: MeasurementTypeDescription): SensorRegistry = {
    idToMeasurementType.put(measurementType.getId(), measurementType)

    measurementType.getAllMeasurementUnits().foreach((unit) =>
      idToMeasurementUnit.put(unit.getId(), unit))

    this
  }

  override def getMeasurementType(id: String): Option[MeasurementTypeDescription] = {
    idToMeasurementType.get(id)
  }

  override def getMeasurementUnit(id: String): Option[MeasurementUnitDescription] = {
    idToMeasurementUnit.get(id)
  }

  override def registerSensor(sensor: SensorEntityDescription): SensorRegistry = {
    idToSensor.put(sensor.getId(), sensor)

    this
  }

  override def getSensor(id: String): Option[SensorEntityDescription] = {
    idToSensor.get(id)
  }

  override def registerMarker(marker: MarkerEntityDescription): SensorRegistry = {
    idToMarker.put(marker.getId(), marker)
    markerIdToMarker.put(marker.getMarkerId(), marker)

    this
  }

  override def getMarker(id: String): Option[MarkerEntityDescription] = {
    idToMarker.get(id)
  }

  override def getMarkableEntity(id: String): Option[MarkableEntityDescription] = {
    idToMarkable.get(id)
  }

  override def registerSensedEntity(sensedEntity: SensedEntityDescription): SensorRegistry = {
    idToSensed.put(sensedEntity.getId(), sensedEntity)

    if (sensedEntity.isInstanceOf[MarkableEntityDescription]) {
      idToMarkable.put(sensedEntity.getId(), sensedEntity.asInstanceOf[MarkableEntityDescription])
    }

    this
  }

  override def getSensedEntity(id: String): Option[SensedEntityDescription] = {
    idToSensed.get(id)
  }

  override def getAllSensedEntities(): scala.collection.immutable.List[SensedEntityDescription] = {
    idToSensed.values.toList
  }

  override def associateSensorWithSensedEntity(sensorId: String, sensedEntityId: String): SensorRegistry = {
    // TODO(keith) Decide what to do if neither exists
    val sensor = idToSensor.get(sensorId)
    val sensedEntity = idToSensed.get(sensedEntityId)

    sensorSensedEntityAssociations +=
      new SimpleSensorSensedEntityAssociation(sensor.get, sensedEntity.get)

    this
  }

  override def getSensorSensedEntityAssociations(): scala.collection.immutable.List[SimpleSensorSensedEntityAssociation] = {
    sensorSensedEntityAssociations.toList
  }

  override def associateMarkerWithMarkedEntity(markerId: String, markedEntityId: String): SensorRegistry = {
    // TODO(keith) Decide what to do if neither exists
    val marker = idToMarker.get(markerId)
    val markedEntity = idToMarkable.get(markedEntityId)

    associateMarkerWithMarkedEntity(marker.get, markedEntity.get)
  }

  override def associateMarkerWithMarkedEntity(marker: MarkerEntityDescription,
    markableEntity: MarkableEntityDescription): SensorRegistry = {
    markerMarkedEntityAssociations +=
      new SimpleMarkerMarkedEntityAssociation(marker, markableEntity)

    markerIdToMarkable.put(marker.getMarkerId(), markableEntity)

    this
  }

  override def getMarkerMarkedEntityAssociations(): List[MarkerMarkedEntityAssociation] = {
    markerMarkedEntityAssociations.to
  }

  override def getMarkerEntityByMarkerId(markerId: String): Option[MarkerEntityDescription] = {
    markerIdToMarker.get(markerId)
  }

  override def getMarkableEntityByMarkerId(markerId: String): Option[MarkableEntityDescription] = {
    markerIdToMarkable.get(markerId)
  }

  override def addConfigurationData(entityId: String,
    configurationData: scala.collection.immutable.Map[String, AnyRef]): SensorRegistry = {
    val map: Map[String, AnyRef] = getConfigurationMap(entityId)

    map ++= configurationData

    this
  }

  override def getConfigurationData(entityId: String): scala.collection.immutable.Map[String, AnyRef] = {
    val map = getConfigurationMap(entityId)

    map.toMap
  }

  /**
   * Get the configuration map for the given entity ID.
   *
   * [p]
   * Create a map if there isn't one yet.
   *
   * @param entityId
   *          the ID of the entity
   *
   * @return the map for the entity
   */
  private def getConfigurationMap(entityId: String): Map[String, AnyRef] = {
    val map = configurations.get(entityId)
    if (map.isEmpty) {
      val newMap: Map[String, AnyRef] = new HashMap

      configurations.put(entityId, newMap)

      newMap
    } else {
      map.get
    }
  }
}
