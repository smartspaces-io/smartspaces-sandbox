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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimplePhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.model.CompleteSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensorEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SimpleSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SimpleSensorEntityModel;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectBuilder;
import scala.Option;

/**
 * Tests for the {@link StandardSensedEntitySensorHandler}.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntitySensorHandlerTest {

  private StandardSensedEntitySensorHandler handler;

  @Mock
  private CompleteSensedEntityModel allModels;

  @Mock
  private SensorProcessor sensorProcessor;

  @Mock
  private UnknownSensedEntityHandler unknownSensedEntityHandler;

  @Mock
  private SensedEntitySensorListener sensedEntitySensorListener;

  @Mock
  private ExtendedLog log;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    
    handler = new StandardSensedEntitySensorHandler(allModels, unknownSensedEntityHandler, log);
    handler.sensorProcessor_$eq(sensorProcessor);
    handler.addSensedEntitySensorListener(sensedEntitySensorListener);
  }

  /**
   * Test that an unknown sensor gets handled by the registered unknown sensor
   * handler.
   */
  @Test
  public void testUnknownSensor() {
    String sensorId = "foo";
    long timestamp = 1000;
    StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

    builder.setProperty(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_SENSOR, sensorId);

    handler.handleSensorData(timestamp, builder.toDynamicObject());

    Mockito.verify(unknownSensedEntityHandler, Mockito.times(1)).handleUnknownSensor(sensorId);
  }

  /**
   * Test that a known sensor calls the listener properly.
   */
  @Test
  public void testKnownSensor() {
    String sensorId = "foo";
    SensorEntityDescription sensor = new SimpleSensorEntityDescription(sensorId, "foo", "foo", null);
    SensorEntityModel sensorModel = new SimpleSensorEntityModel(sensor, allModels);
    
    SensedEntityDescription sensedEntity =
        new SimplePhysicalSpaceSensedEntityDescription("foo", "foo", "foo");
    SensedEntityModel sensedEntityModel = new SimpleSensedEntityModel(sensedEntity, allModels);
    
    Mockito.when(allModels.getSensorEntityModel(sensor.id())).thenReturn(Option.apply(sensorModel));
    Mockito.when(allModels.getSensedEntityModel(sensedEntity.id())).thenReturn(Option.apply(sensedEntityModel));
    handler.associateSensorWithEntity(sensor, sensedEntity);
    
    long timestamp = 1000;
    StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

    builder.setProperty(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_SENSOR, sensorId);

    DynamicObject data = builder.toDynamicObject();
    handler.handleSensorData(timestamp, data);

    Mockito.verify(unknownSensedEntityHandler, Mockito.times(0)).handleUnknownSensor(sensorId);
    Mockito.verify(sensedEntitySensorListener, Mockito.times(1)).handleSensorData(handler,
        timestamp, sensorModel, sensedEntityModel, data);
  }

}
