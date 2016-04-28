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

import io.smartspaces.sandbox.interaction.entity.MarkableEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensedEntityModelCollection;
import io.smartspaces.util.data.dynamic.DynamicObject;

/**
 * The standard processor for BLE proximity data.
 * 
 * @author Keith M. Hughes
 */
public class StandardBleProximitySensorValueProcessor {

  /**
   * Process the incoming data.
   * 
   * @param sensedEntityModelCollection
   *          the collection of sensed entity models
   * @param data
   *          the data to process
   */
  public void processData(SensedEntityModelCollection sensedEntityModelCollection,
      DynamicObject data) {
    String markerId = "ble" + ":" + data.getRequiredString("id");
    double rssi = data.getDouble("rssi");

    MarkableEntityDescription markedEntity =
        sensedEntityModelCollection.getSensorRegistry().getMarkableEntityByMarkerId(markerId);

    System.out.format("Detected ID %s,  RSSI= %f, %s\n", markerId, rssi, markedEntity);
  }
}
