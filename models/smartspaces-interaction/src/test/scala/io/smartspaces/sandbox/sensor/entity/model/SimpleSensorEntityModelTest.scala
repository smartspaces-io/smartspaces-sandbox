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

package io.smartspaces.sandbox.sensor.entity.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.MockitoAnnotations.Mock
import org.scalatest.junit.JUnitSuite

import io.smartspaces.sandbox.sensor.entity.MeasurementTypeDescription
import io.smartspaces.sandbox.sensor.entity.SensorDetail
import io.smartspaces.sandbox.sensor.entity.SensorEntityDescription
import io.smartspaces.sandbox.sensor.entity.model.event.SensorOfflineEvent

/**
 * Tests for the SimpleSensorEntityModel.
 *
 * @author Keith M. Hughes
 */
class SimpleSensorEntityModelTest extends JUnitSuite {
  var model: SimpleSensorEntityModel = null

  @Mock var sensorEntityDescription: SensorEntityDescription = null

  @Mock var allModels: CompleteSensedEntityModel = null

  @Mock var sensorDetail: SensorDetail = null

  val modelCreationTime = 123456l

  @Before def setup(): Unit = {
    MockitoAnnotations.initMocks(this)

    Mockito.when(sensorEntityDescription.sensorDetail).thenReturn(Option(sensorDetail))

    model = new SimpleSensorEntityModel(sensorEntityDescription, allModels, modelCreationTime)
  }

  /**
   * Test to see if offline happens when there has been no update time.
   */
  @Test def testOfflineFromInitialTimeout(): Unit = {
    val timeoutTime = 1000l

    Mockito.when(sensorEntityDescription.sensorUpdateTimeLimit).thenReturn(Option(timeoutTime))

    //The model starts offline until proven online
    Assert.assertFalse(model.online)

    val argumentCaptor =
      ArgumentCaptor.forClass(classOf[SensorOfflineEvent])

    // Model didn't change from short change
    model.checkIfOfflineTransition(modelCreationTime + timeoutTime / 2)
    Assert.assertFalse(model.online)
    Mockito.verify(allModels, Mockito.times(0)).broadcastSensorOfflineEvent(argumentCaptor.capture())

    // Now trigger it
    model.checkIfOfflineTransition(modelCreationTime + timeoutTime + 1)
    Assert.assertFalse(model.online)
    Mockito.verify(allModels, Mockito.times(0)).broadcastSensorOfflineEvent(argumentCaptor.capture())
  }

  /**
   * Test to see if offline happens when there has been no update time.
   */
  @Test def testOfflineFromOnline(): Unit = {
    val lastUpdate = 30000
    val timeoutTime = 1000l

    Mockito.when(sensorEntityDescription.sensorUpdateTimeLimit).thenReturn(Option(timeoutTime))

    // Set the model online and last update time
    model.online = true
    model.setLastUpdateTime(lastUpdate)

    val argumentCaptor =
      ArgumentCaptor.forClass(classOf[SensorOfflineEvent])

    // Model didn't change from short change
    model.checkIfOfflineTransition(lastUpdate + timeoutTime / 2)
    Assert.assertTrue(model.online)
    Mockito.verify(allModels, Mockito.times(0)).broadcastSensorOfflineEvent(argumentCaptor.capture())

    // Now trigger it
    var offlineTime = lastUpdate + timeoutTime + 1
    model.checkIfOfflineTransition(offlineTime)
    Assert.assertFalse(model.online)
    Mockito.verify(allModels, Mockito.times(1)).broadcastSensorOfflineEvent(argumentCaptor.capture())

    val event = argumentCaptor.getValue
    Assert.assertEquals(model, event.sensorModel)
    Assert.assertEquals(offlineTime, event.timestamp)
  }

  /**
   * Test a model update and confirm that it makes the sensor model online, and properly
   * records the value and when it happened.
   * 
   */
  @Test def testValueUpdate(): Unit = {
    val valueTypeExternalId = "glorp"
    val valueType = Mockito.mock(classOf[MeasurementTypeDescription])
    Mockito.when(valueType.externalId).thenReturn(valueTypeExternalId)
    
    
    val value = Mockito.mock(classOf[SensedValue[Double]])
    Mockito.when(value.valueType).thenReturn(valueType)

    val currentTime = 10000l
    
    model.online = false

    Assert.assertTrue(model.getLastUpdate().isEmpty)

    model.updateSensedValue(value, currentTime)

    Assert.assertEquals(currentTime, model.getLastUpdate().get)
    Assert.assertEquals(List(value), model.getAllSensedValues())
    Assert.assertTrue(model.online)
  }
}