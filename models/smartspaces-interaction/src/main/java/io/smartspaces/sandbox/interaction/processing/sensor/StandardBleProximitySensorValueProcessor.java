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

import java.util.HashMap;
import java.util.Map;

import io.smartspaces.event.trigger.SimpleHysteresisThresholdValueTrigger;
import io.smartspaces.event.trigger.Trigger;
import io.smartspaces.event.trigger.TriggerEventType;
import io.smartspaces.event.trigger.TriggerListener;
import io.smartspaces.event.trigger.TriggerState;
import io.smartspaces.sandbox.interaction.entity.MarkableEntityDescription;
import io.smartspaces.sandbox.interaction.entity.MarkerEntityDescription;
import io.smartspaces.sandbox.interaction.entity.SensorEntityDescription;
import io.smartspaces.sandbox.interaction.entity.model.PersonSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.PhysicalSpaceSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.SensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.updater.PersonPhysicalSpaceModelUpdater;
import io.smartspaces.sandbox.interaction.entity.sensor.StandardSensorData;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;

/**
 * The standard processor for BLE proximity data.
 * 
 * @author Keith M. Hughes
 */
public class StandardBleProximitySensorValueProcessor implements SensorValueProcessor {

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
   * support for working with BLE proximity devices.
   */
  private StandardBleProximitySupport bleProximitySupport = new StandardBleProximitySupport();

  @Override
  public String getSensorValueType() {
    return StandardSensorData.SENSOR_TYPE_PROXIMITY_BLE;
  }

  @Override
  public void processData(long timestamp, SensorEntityDescription sensor,
      SensedEntityModel sensedEntityModel, SensorValueProcessorContext processorContext,
      DynamicObject data) {
    String markerId = "ble" + ":" + data.getRequiredString("id");
    double rssi = data.getDouble("rssi");

    SimpleHysteresisThresholdValueTrigger userTrigger =
        getTrigger(markerId, sensor, sensedEntityModel, processorContext);
    userTrigger.update(rssi);

    MarkableEntityDescription markedEntity = processorContext.getSensedEntityModelCollection()
        .getSensorRegistry().getMarkableEntityByMarkerId(markerId);
    processorContext.getLog().formatInfo("Detected ID %s,  RSSI= %f, %s\n", markerId, rssi,
        markedEntity);
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
   * @param processorContext
   *          the context for processor handling
   * 
   * @return the trigger for the marker
   */
  private SimpleHysteresisThresholdValueTrigger getTrigger(String markerId,
      SensorEntityDescription sensor, SensedEntityModel sensedEntityModel,
      SensorValueProcessorContext processorContext) {
    SimpleHysteresisThresholdValueTrigger userTrigger = userTriggers.get(markerId);
    if (userTrigger == null) {
      userTrigger = new SimpleHysteresisThresholdValueTrigger();

      MarkerEntityDescription markerEntity = processorContext.getSensedEntityModelCollection()
          .getSensorRegistry().getMarkerEntityByMarkerId(markerId);

      Map<String, Object> configData = processorContext.getSensedEntityModelCollection()
          .getSensorRegistry().getConfigurationData(markerEntity.getId());
      bleProximitySupport.configureTrigger(userTrigger, configData, sensor, processorContext);

      userTrigger.addListener(triggerListener);
      userTriggers.put(markerId, userTrigger);

      PersonSensedEntityModel person =
          processorContext.getSensedEntityModelCollection().getMarkedSensedEntityModel(markerId);
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
