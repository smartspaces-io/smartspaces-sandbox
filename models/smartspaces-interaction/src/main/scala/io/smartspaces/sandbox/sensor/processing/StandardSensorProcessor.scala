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
package io.smartspaces.sandbox.sensor.processing

import io.smartspaces.logging.ExtendedLog
import io.smartspaces.resource.managed.ManagedResources
import io.smartspaces.resource.managed.StandardManagedResources
import io.smartspaces.util.data.dynamic.DynamicObject

import scala.collection.mutable.ListBuffer

/**
 * The standard processor for sensor data.
 *
 * @author Keith M. Hughes
 */
class StandardSensorProcessor(val log: ExtendedLog) extends SensorProcessor {

  /**
   * The managed resources controlled by the processor.
   */
  private val managedResources: ManagedResources = new StandardManagedResources(log)

  /**
   * All sensor handlers added to the processor.
   */
  private val sensorHandlers: ListBuffer[SensorHandler] = new ListBuffer

  override def startup(): Unit = {
    managedResources.startupResources()
  }

  override def shutdown(): Unit = {
    managedResources.shutdownResourcesAndClear()
  }

  override def addSensorInput(sensorInput: SensorInput): SensorProcessor = {
    sensorInput.setSensorProcessor(this)

    managedResources.addResource(sensorInput)

    this
  }

  override def addSensorHandler(sensorHandler: SensorHandler): SensorProcessor = {
    sensorHandler.sensorProcessor = this
    sensorHandlers += sensorHandler

    managedResources.addResource(sensorHandler)

    this
  }

  override def processSensorData(timestamp: Long, sensorDataEvent: DynamicObject): Unit = {
    sensorHandlers.foreach(handler => {
      try {
        handler.handleSensorData(timestamp, sensorDataEvent)
      } catch {
        case e: Throwable => log.error("Could not process sensor data event", e)
      }
    })
  }
}
