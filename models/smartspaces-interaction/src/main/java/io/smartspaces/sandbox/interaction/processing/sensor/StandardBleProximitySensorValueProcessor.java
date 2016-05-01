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

import io.smartspaces.event.trigger.SimpleHysteresisThresholdValueTrigger;
import io.smartspaces.event.trigger.Trigger;
import io.smartspaces.event.trigger.TriggerEventType;
import io.smartspaces.event.trigger.TriggerListener;
import io.smartspaces.event.trigger.TriggerState;
import io.smartspaces.sandbox.interaction.entity.MarkableEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedEntityModelCollection;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.resource.ManagedResource;

/**
 * The standard processor for BLE proximity data.
 * 
 * @author Keith M. Hughes
 */
public class StandardBleProximitySensorValueProcessor implements ManagedResource {

  private SimpleHysteresisThresholdValueTrigger userTrigger =
      new SimpleHysteresisThresholdValueTrigger();

  @Override
  public void startup() {
    userTrigger.setThresholdsWithOffset(-56, 2);
    userTrigger.addListener(new TriggerListener() {

      @Override
      public void onTrigger(Trigger trigger, TriggerState state, TriggerEventType type) {
        if (type == TriggerEventType.RISING) {
          System.out.println("Trigger rising");
        } else {
          System.out.println("Trigger falling");
        }
      }
    });
  }

  @Override
  public void shutdown() {
    // TODO Auto-generated method stub

  }

  /**
   * Process the incoming data.
   * 
   * @param timestamp
   *          when the data came in
   * @param sensor
   *          the sensor platform that detected the data
   * @param sensedEntity
   *          the sensed entity that is associated with the sensor
   * @param sensedEntityModelCollection
   *          the collection of sensed entity models
   * @param data
   *          the data to process
   */
  public void processData(long timestamp, SensorEntityDescription sensor,
      SensedEntityDescription sensedEntity, SensedEntityModelCollection sensedEntityModelCollection,
      DynamicObject data) {
    String markerId = "ble" + ":" + data.getRequiredString("id");
    double rssi = data.getDouble("rssi");

    MarkableEntityDescription markedEntity =
        sensedEntityModelCollection.getSensorRegistry().getMarkableEntityByMarkerId(markerId);

    userTrigger.update((long) rssi);

    System.out.format("Detected ID %s,  RSSI= %f, %s\n", markerId, rssi, markedEntity);
  }
}