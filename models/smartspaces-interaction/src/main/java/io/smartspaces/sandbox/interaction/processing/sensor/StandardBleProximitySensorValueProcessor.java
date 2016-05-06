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
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.model.PersonSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.PhysicalSpaceSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModelCollection;
import io.smartspaces.sandbox.interaction.entity.model.updater.PersonPhysicalSpaceModelUpdater;
import io.smartspaces.util.data.dynamic.DynamicObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The standard processor for BLE proximity data.
 * 
 * @author Keith M. Hughes
 */
public class StandardBleProximitySensorValueProcessor {

  /**
   * The map from the BLE IDs to the trigger for that ID.
   */
  private Map<String, SimpleHysteresisThresholdValueTrigger> userTriggers = new HashMap<>();

  /**
   * The map from the triggers to the models to be updated by that trigger.
   */
  private Map<SimpleHysteresisThresholdValueTrigger, PersonPhysicalSpaceModelUpdater> userTriggerToUpdaters =
      new HashMap<>();

  /**
   * The listener for trigger events being shared across all triggers.
   */
  private TriggerListener triggerListener = new TriggerListener() {
    @Override
    public void onTrigger(Trigger trigger, TriggerState state, TriggerEventType type) {
      handleTrigger(trigger, state, type);
    }
  };

  /**
   * Process the incoming data.
   * 
   * @param timestamp
   *          when the data came in
   * @param sensor
   *          the sensor platform that detected the data
   * @param sensedEntityModel
   *          the sensed entity model that is associated with the sensor
   * @param sensedEntityModelCollection
   *          the collection of sensed entity models
   * @param data
   *          the data to process
   */
  public void processData(long timestamp, SensorEntityDescription sensor,
      SensedEntityModel sensedEntityModel, SensedEntityModelCollection sensedEntityModelCollection,
      DynamicObject data) {
    String markerId = "ble" + ":" + data.getRequiredString("id");
    double rssi = data.getDouble("rssi");

    MarkableEntityDescription markedEntity =
        sensedEntityModelCollection.getSensorRegistry().getMarkableEntityByMarkerId(markerId);

    SimpleHysteresisThresholdValueTrigger userTrigger =
        getTrigger(markerId, sensedEntityModel, sensedEntityModelCollection);
    userTrigger.update(rssi);

    System.out.format("Detected ID %s,  RSSI= %f, %s\n", markerId, rssi, markedEntity);
  }

  /**
   * Get the trigger for a given marker ID.
   * 
   * <p>
   * Creates the trigger if it didn't exist.
   * 
   * @param markerId
   *          the marker ID for the trigger
   * @param sensedEntityModel
   *          the sensed entity model that is associated with the sensor
   * @param sensedEntityModelCollection
   *          the collection of sensed entity models
   * 
   * @return the trigger for the marker
   */
  private SimpleHysteresisThresholdValueTrigger getTrigger(String markerId,
      SensedEntityModel sensedEntityModel,
      SensedEntityModelCollection sensedEntityModelCollection) {
    SimpleHysteresisThresholdValueTrigger userTrigger = userTriggers.get(markerId);
    if (userTrigger == null) {
      userTrigger = new SimpleHysteresisThresholdValueTrigger();

      // TODO(keith): Need some system to have persisted thresholds for a given
      // marker and room.
      userTrigger.setThresholdsWithOffset(-49, 2);
      userTrigger.addListener(triggerListener);

      userTriggers.put(markerId, userTrigger);

      PersonSensedEntityModel person =
          sensedEntityModelCollection.getMarkedSensedEntityModel(markerId);
      PersonPhysicalSpaceModelUpdater modelUpdater = new PersonPhysicalSpaceModelUpdater(
          (PhysicalSpaceSensedEntityModel) sensedEntityModel, person);
      userTriggerToUpdaters.put(userTrigger, modelUpdater);
    }

    return userTrigger;
  }

  /**
   * Handle a trigger change,
   * 
   * @param trigger
   *          the trigger that changed
   * @param state
   *          the new state of the trigger
   * @param type
   *          the type of the state change
   */
  private void handleTrigger(Trigger trigger, TriggerState state, TriggerEventType type) {
    PersonPhysicalSpaceModelUpdater modelUpdater = userTriggerToUpdaters.get(trigger);
    if (type == TriggerEventType.RISING) {
      modelUpdater.enterSpace();
    } else {
      modelUpdater.exitSpace();
    }
  }
}
