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

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModelCollection;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Tests for the {@link StandardSensedEntityModelProcessor}.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelProcessorTest {

  private StandardSensedEntityModelProcessor processor;

  @Mock
  private SensedEntityModelCollection sensedEntityModelCollection;

  @Mock
  private ExtendedLog log;

  @Mock
  private SensedEntitySensorHandler handler;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    processor = new StandardSensedEntityModelProcessor(sensedEntityModelCollection, log);
  }

  /**
   * Test using the sensor value processor when can't find the sensed entity
   * model.
   */
  @Test
  public void testModelNoValueUpdate() {
    SensorValueProcessor sensorValueProcessor = Mockito.mock(SensorValueProcessor.class);
    String sensorValueType = "sensor.type";
    Mockito.when(sensorValueProcessor.getSensorValueType()).thenReturn(sensorValueType);

    processor.addSensorValueProcessor(sensorValueProcessor);

    StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

    builder.newObject(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA);

    DynamicObject data = builder.toDynamicObject();

    long timestamp = 10000;
    SensorEntityDescription sensor = Mockito.mock(SensorEntityDescription.class);
    SensedEntityDescription sensedEntity = Mockito.mock(SensedEntityDescription.class);
    String sensedEntityId = "sensed.entity.1";
    Mockito.when(sensedEntity.getId()).thenReturn(sensedEntityId);

    processor.handleSensorData(handler, timestamp, sensor, sensedEntity, data);

    Mockito.verify(sensorValueProcessor, Mockito.never()).processData(Mockito.anyLong(),
        Mockito.any(SensorEntityDescription.class), Mockito.any(SensedEntityModel.class),
        Mockito.any(SensorValueProcessorContext.class), Mockito.any(DynamicObject.class));
  }

  /**
   * Test using the sensor value processor.
   */
  @Test
  public void testModelValueUpdate() {
    SensorValueProcessor sensorValueProcessor = Mockito.mock(SensorValueProcessor.class);
    String sensorValueType = "sensor.type";
    Mockito.when(sensorValueProcessor.getSensorValueType()).thenReturn(sensorValueType);

    processor.addSensorValueProcessor(sensorValueProcessor);

    StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

    builder.newObject(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA);
    builder.newObject("test");
    builder.setProperty(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_TYPE, sensorValueType);

    long timestamp = 10000;
    SensorEntityDescription sensor = Mockito.mock(SensorEntityDescription.class);
    SensedEntityDescription sensedEntity = Mockito.mock(SensedEntityDescription.class);

    String sensedEntityId = "sensed.entity.1";
    Mockito.when(sensedEntity.getId()).thenReturn(sensedEntityId);

    SensedEntityModel sensedEntityModel = Mockito.mock(SensedEntityModel.class);

    Mockito.when(sensedEntityModelCollection.getSensedEntityModel(sensedEntityId))
        .thenReturn(sensedEntityModel);

    DynamicObject data = builder.toDynamicObject();
    processor.handleSensorData(handler, timestamp, sensor, sensedEntity, data);

    ArgumentCaptor<SensorValueProcessorContext> sensorValueProcessorContextCaptor =
        ArgumentCaptor.forClass(SensorValueProcessorContext.class);
    ArgumentCaptor<Long> timestampCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<DynamicObject> dataCaptor = ArgumentCaptor.forClass(DynamicObject.class);
    ArgumentCaptor<SensedEntityModel> sensedEntityModelCaptor =
        ArgumentCaptor.forClass(SensedEntityModel.class);
    ArgumentCaptor<SensorEntityDescription> sensorCaptor =
        ArgumentCaptor.forClass(SensorEntityDescription.class);

    Mockito.verify(sensorValueProcessor, Mockito.times(1)).processData(timestampCaptor.capture(),
        sensorCaptor.capture(), sensedEntityModelCaptor.capture(),
        sensorValueProcessorContextCaptor.capture(), dataCaptor.capture());

    Assert.assertEquals(data, dataCaptor.getValue());
    Assert.assertEquals(timestamp, timestampCaptor.getValue().longValue());
    Assert.assertEquals(sensor, sensorCaptor.getValue());
    Assert.assertEquals(sensedEntityModel, sensedEntityModelCaptor.getValue());

    SensorValueProcessorContext sensorValueProcessorContext =
        sensorValueProcessorContextCaptor.getValue();

    Assert.assertEquals(log, sensorValueProcessorContext.getLog());
    Assert.assertEquals(sensedEntityModelCollection,
        sensorValueProcessorContext.getSensedEntityModelCollection());
  }
}
