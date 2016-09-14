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

package io.smartspaces.sandbox.interaction.entity.model;

import scala.collection.mutable._

import io.smartspaces.event.observable.EventObservable;
import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.MarkerMarkedEntityAssociation;
import io.smartspaces.sandbox.interaction.entity.PersonSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorRegistry;
import io.smartspaces.service.event.observable.EventObservableService;
import io.smartspaces.service.event.observable.ObservableCreator;
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * A collection of sensed entity models.
 *
 * @author Keith M. Hughes
 */
class StandardCompleteSensedEntityModel(val sensorRegistry: SensorRegistry,
    private val eventObservableService: EventObservableService, private val log: ExtendedLog) extends CompleteSensedEntityModel {

  /**
   * Map of entity IDs to their models.
   */
  private val idToModels: Map[String, SensedEntityModel] = new HashMap

  /**
   * Map of physical entity IDs to their models.
   */
  private val idToPhysicalSpaceModels: Map[String, PhysicalSpaceSensedEntityModel] = new HashMap

  /**
   * Map of person entity IDs to their models.
   */
  private val idToPersonModels: Map[String, PersonSensedEntityModel] = new HashMap

  /**
   * Map of marker IDs to their models.
   */
  private val markerIdToPersonModels: Map[String, PersonSensedEntityModel] = new HashMap

  /**
   * The creator for physical occupancy observables.
   */
  private val physicalLocationOccupancyEventCreator: ObservableCreator[EventObservable[PhysicalLocationOccupancyEvent]] =
    new ObservableCreator[EventObservable[PhysicalLocationOccupancyEvent]]() {
      override def newObservable(): EventObservable[PhysicalLocationOccupancyEvent] = {
        new EventObservable[PhysicalLocationOccupancyEvent](log)
      }
    }

  /**
   * The readwrite lock for the
   */
  private val readWriteLock = new ReentrantReadWriteLock

  override def prepare(): Unit = {
    createModelsFromDescriptions();
  }

  /**
   * Create all models from the descriptions in the registry.
   */
  private def createModelsFromDescriptions(): Unit = {
    sensorRegistry.getAllSensedEntities().foreach(addNewSensedEntity(_))

    sensorRegistry
      .getMarkerMarkedEntityAssociations().foreach((association) =>
        markerIdToPersonModels.put(association.getMarkerEntityDescription().getMarkerId(),
          idToPersonModels.get(association.getMarkableEntityDescription().getId()).get))

  }

  /**
   * Add in a new sensed entity into the collection.
   *
   * @param entityDescription
   *          the new description
   */
  private def addNewSensedEntity(entityDescription: SensedEntityDescription): Unit = {
    val id = entityDescription.getId()

    var model: SensedEntityModel = null;
    if (entityDescription.isInstanceOf[PhysicalSpaceSensedEntityDescription]) {
      val observable =
        eventObservableService.getObservable(PhysicalLocationOccupancyEvent.EVENT_NAME,
          physicalLocationOccupancyEventCreator)
      model = new SimplePhysicalSpaceSensedEntityModel(
        entityDescription.asInstanceOf[PhysicalSpaceSensedEntityDescription], this, observable);
      idToPhysicalSpaceModels.put(id, model.asInstanceOf[PhysicalSpaceSensedEntityModel]);
    } else if (entityDescription.isInstanceOf[PersonSensedEntityDescription]) {
      model = new SimplePersonSensedEntityModel(entityDescription.asInstanceOf[PersonSensedEntityDescription],
        this);
      idToPersonModels.put(id, model.asInstanceOf[PersonSensedEntityModel]);
    } else {
      model = new SimpleSensedEntityModel(entityDescription, this);
    }

    idToModels.put(id, model)
  }

  override def getSensedEntityModel(id: String): Option[SensedEntityModel] = {
    idToModels.get(id)
  }

  override def getAllSensedEntityModels(): scala.collection.immutable.List[SensedEntityModel] = {
    idToModels.values.toList
  }

  override def getPhysicalSpaceSensedEntityModel(id: String): Option[PhysicalSpaceSensedEntityModel] = {
    idToPhysicalSpaceModels.get(id)
  }

  override def getAllPhysicalSpaceSensedEntityModels(): scala.collection.immutable.List[PhysicalSpaceSensedEntityModel] = {
    idToPhysicalSpaceModels.values.toList
  }

  override def getPersonSensedEntityModel(id: String): Option[PersonSensedEntityModel] = {
    idToPersonModels.get(id)
  }

  override def getAllPersonSensedEntityModels(): scala.collection.immutable.List[PersonSensedEntityModel] = {
    idToPersonModels.values.toList
  }

  override def getMarkedSensedEntityModel(markerId: String): Option[PersonSensedEntityModel] = {
    markerIdToPersonModels.get(markerId)
  }

  override def doReadTransaction(transaction: () => Unit): Unit = {
    readWriteLock.readLock().lock();

    try {
      transaction()
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  override def doWriteTransaction(transaction: () => Unit): Unit = {
    readWriteLock.writeLock().lock();

    try {
      transaction()
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }
}
