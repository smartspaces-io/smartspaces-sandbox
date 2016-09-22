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
import io.smartspaces.sandbox.interaction.entity.SensorDetail;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimplePhysicalSpaceSensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SimpleSensorDetail;
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
 * Tests for the {@link StandardSensedEntityModelProcessor}.
 * 
 * @author Keith M. Hughes
 */
public class StandardSensedEntityModelProcessorTest {

	private StandardSensedEntityModelProcessor processor;

	@Mock
	private CompleteSensedEntityModel completeSensedEntityModel;

	@Mock
	private ExtendedLog log;

	@Mock
	private SensedEntitySensorHandler handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		processor = new StandardSensedEntityModelProcessor(completeSensedEntityModel, log);
	}

	/**
	 * Test using the sensor value processor when can't find the sensed entity
	 * model.
	 */
	@Test
	public void testModelNoValueUpdate() {
		SensorValueProcessor sensorValueProcessor = Mockito.mock(SensorValueProcessor.class);
		String sensorValueType = "sensor.type";
		Mockito.when(sensorValueProcessor.sensorValueType()).thenReturn(sensorValueType);

		processor.addSensorValueProcessor(sensorValueProcessor);

		StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

		builder.newObject(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA);

		DynamicObject data = builder.toDynamicObject();

		long timestamp = 10000;
		SensorEntityDescription sensor = new SimpleSensorEntityDescription("foo", "foo", "foo", null);
		SensorEntityModel sensorModel = new SimpleSensorEntityModel(sensor, completeSensedEntityModel);

		SensedEntityDescription sensedEntity = new SimplePhysicalSpaceSensedEntityDescription("foo", "foo", "foo");
		SensedEntityModel sensedEntityModel = new SimpleSensedEntityModel(sensedEntity, completeSensedEntityModel);

		processor.handleSensorData(handler, timestamp, sensorModel, sensedEntityModel, data);

		Mockito.verify(sensorValueProcessor, Mockito.never()).processData(Mockito.anyLong(),
				Mockito.any(SensorEntityModel.class), Mockito.any(SensedEntityModel.class),
				Mockito.any(SensorValueProcessorContext.class), Mockito.any(DynamicObject.class));
	}

	/**
	 * Test using the sensor value processor.
	 */
	@Test
	public void testModelValueUpdate() {
		SensorValueProcessor sensorValueProcessor = Mockito.mock(SensorValueProcessor.class);
		String sensorValueType = "sensor.type";
		Mockito.when(sensorValueProcessor.sensorValueType()).thenReturn(sensorValueType);

		processor.addSensorValueProcessor(sensorValueProcessor);

		StandardDynamicObjectBuilder builder = new StandardDynamicObjectBuilder();

		builder.newObject(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA);
		builder.newObject("test");
		builder.setProperty(SensorMessages.SENSOR_MESSAGE_FIELD_NAME_DATA_TYPE, sensorValueType);

		long timestamp = 10000;
		SensorDetail sensorDetail = new SimpleSensorDetail("foo", "foo", "foo");
		SensorEntityDescription sensor = new SimpleSensorEntityDescription("foo", "foo", "foo", Option.apply(sensorDetail));
		SensorEntityModel sensorModel = new SimpleSensorEntityModel(sensor, completeSensedEntityModel);

		SensedEntityDescription sensedEntity = new SimplePhysicalSpaceSensedEntityDescription("foo", "foo", "foo");
		SensedEntityModel sensedEntityModel = new SimpleSensedEntityModel(sensedEntity, completeSensedEntityModel);

		Mockito.when(completeSensedEntityModel.getSensedEntityModel(sensedEntity.id()))
				.thenReturn(scala.Option.apply(sensedEntityModel));

		DynamicObject data = builder.toDynamicObject();
		processor.handleSensorData(handler, timestamp, sensorModel, sensedEntityModel, data);

		Mockito.verify(sensorValueProcessor, Mockito.times(1)).processData(timestamp, sensorModel, sensedEntityModel,
				processor.processorContext(), data);

	}
}
