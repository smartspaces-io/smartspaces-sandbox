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
package io.smartspaces.sandbox.interaction.processing.sensor;

import java.util.ArrayList;
import java.util.List;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.resource.ManagedResources;
import io.smartspaces.util.resource.StandardManagedResources;

/**
 * The standard processor for sensor data.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensorProcessor implements SensorProcessor {

  /**
   * The logger for the processor.
   */
  private ExtendedLog log;

  /**
   * The managed resources controlled by the processor.
   */
  private ManagedResources managedResources;

  /**
   * All sensor handlers added to the processor.
   */
  private List<SensorHandler> sensorHandlers = new ArrayList<>();

  /**
   * Construct a new processor.
   * 
   * @param log
   *          the logger for the processor
   */
  public StandardSensorProcessor(ExtendedLog log) {
    this.log = log;

    managedResources = new StandardManagedResources(log);
  }

  @Override
  public void startup() {
    managedResources.startupResources();
  }

  @Override
  public void shutdown() {
    managedResources.shutdownResourcesAndClear();
  }

  @Override
  public SensorProcessor addSensorInput(SensorInput sensorInput) {
    sensorInput.setSensorProcessor(this);

    managedResources.addResource(sensorInput);

    return this;
  }

  @Override
  public SensorProcessor addSensorHandler(SensorHandler sensorHandler) {
    sensorHandler.setSensorProcessor(this);
    sensorHandlers.add(sensorHandler);

    managedResources.addResource(sensorHandler);

    return this;
  }

  @Override
  public void processSensorData(long timestamp, DynamicObject sensorDataEvent) {
    for (SensorHandler sensorHandler : sensorHandlers) {
      try {
        sensorHandler.handleSensorData(timestamp, sensorDataEvent);
      } catch (Throwable e) {
        log.error("Could not process sensor data event", e);
      }
    }
  }

  @Override
  public ExtendedLog getLog() {
    return log;
  }
}
