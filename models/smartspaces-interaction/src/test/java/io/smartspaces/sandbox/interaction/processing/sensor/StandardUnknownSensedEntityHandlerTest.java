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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Tests for {@link StandardUnknownSensedEntityHandler}.
 * 
 * @author Keith M. Hughes
 */
public class StandardUnknownSensedEntityHandlerTest {
  private StandardUnknownSensedEntityHandler handler;

  @Before
  public void setup() {
    handler = new StandardUnknownSensedEntityHandler();
  }

  /**
   * Ensure that is empty when starts
   */
  @Test
  public void testEmptyWhenStarts() {
    assertTrue(handler.getAllUnknownSensorIds().isEmpty());
  }

  /**
   * Test that when adds, all added are all that are there.
   */
  @Test
  public void testAdd() {
    String sensorId1 = "foo1";
    handler.handleUnknownSensor(sensorId1);

    String sensorId2 = "foo2";
    handler.handleUnknownSensor(sensorId2);

    assertEquals(Sets.newHashSet(sensorId1, sensorId2), handler.getAllUnknownSensorIds());
  }

  /**
   * Test that when adds, duplicates are not added..
   */
  @Test
  public void testAddSame() {
    String sensorId1 = "foo1";
    handler.handleUnknownSensor(sensorId1);
    handler.handleUnknownSensor(sensorId1);

    String sensorId2 = "foo2";
    handler.handleUnknownSensor(sensorId2);

    assertEquals(Sets.newHashSet(sensorId1, sensorId2), handler.getAllUnknownSensorIds());
  }

  /**
   * Test removing one of the sensors.
   */
  @Test
  public void testRemoval() {
    String sensorId1 = "foo1";
    handler.handleUnknownSensor(sensorId1);

    String sensorId2 = "foo2";
    handler.handleUnknownSensor(sensorId2);

    handler.removeUnknownSensorId(sensorId1);

    assertEquals(Sets.newHashSet(sensorId2), handler.getAllUnknownSensorIds());
  }

}
