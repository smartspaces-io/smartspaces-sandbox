/*
 * Copyright (C) 2015 Keith M. Hughes
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

package io.smartspaces.sandbox.service.action.internal;

import io.smartspaces.SimpleSmartSpacesException;
import io.smartspaces.resource.NamedVersionedResourceCollection;
import io.smartspaces.resource.Version;
import io.smartspaces.resource.VersionRange;
import io.smartspaces.sandbox.service.action.Action;
import io.smartspaces.sandbox.service.action.ActionReference;
import io.smartspaces.sandbox.service.action.ActionService;
import io.smartspaces.sandbox.service.action.ActionSource;
import io.smartspaces.sandbox.service.action.GroupActionReference;
import io.smartspaces.sandbox.service.action.SimpleActionReference;
import io.smartspaces.service.BaseSupportedService;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A service for actions.
 * 
 * @author Keith M. Hughes
 */
public class StandardActionService extends BaseSupportedService implements ActionService {

  /**
   * The default version to give action sources.
   */
  public static Version DEFAULT_VERSION = new Version(0, 0, 0);

  /**
   * The action sources.
   */
  private NamedVersionedResourceCollection<ActionSource> sources = NamedVersionedResourceCollection
      .newNamedVersionedResourceCollection();

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public void addActionSource(String sourceName, ActionSource source) {
    addActionSource(sourceName, DEFAULT_VERSION, source);
  }

  @Override
  public void addActionSource(String sourceName, Version sourceVersion, ActionSource source) {
    sources.addResource(sourceName, sourceVersion, source);
  }

  @Override
  public void performAction(String actionSourceName, String actionName,
      Map<String, ? extends Object> data) {
    performAction(actionSourceName, actionName, null, data);
  }

  @Override
  public void performActionReference(ActionReference actionReference,
      Map<String, ? extends Object> data) {
    actionReference.performAction(this, data);
  }

  @Override
  public void performAction(String actionSourceName, String actionName, VersionRange versionRange,
      Map<String, ? extends Object> data) {
    ActionSource source = sources.getHighestResource(actionSourceName);
    if (source != null) {
      Action action = source.getAction(actionName);
      if (action != null) {
        action.perform(data);
      } else {
        throw new SimpleSmartSpacesException(String.format(
            "Action %s:%s for version %s not found", actionSourceName, actionName, versionRange));
      }
    } else {
      throw new SimpleSmartSpacesException(String.format(
          "No action source found for action %s:%s for version %s", actionSourceName, actionName,
          versionRange));
    }
  }

  @Override
  public void performSimpleActionReference(SimpleActionReference actionReference,
      Map<String, ? extends Object> data) {
    Map<String, ? extends Object> mergedData = getMergedData(actionReference, data);

    performAction(actionReference.getActionSource(), actionReference.getActionName(),
        actionReference.getVersionRange(), mergedData);
  }

  @Override
  public void performGroupActionReference(GroupActionReference actionReference,
      Map<String, ? extends Object> data) {
    Map<String, ? extends Object> mergedData = getMergedData(actionReference, data);

    for (ActionReference reference : actionReference.getActionReferences()) {
      performActionReference(reference, mergedData);
    }
  }

  /**
   * Merge the action reference data with the supplied data.
   * 
   * @param actionReference
   *          the action reference
   * @param data
   *          the supplied data
   * 
   * @return the merged data
   */
  private Map<String, ? extends Object> getMergedData(ActionReference actionReference,
      Map<String, ? extends Object> data) {
    Map<String, Object> mergedMap = Maps.newHashMap(actionReference.getData());
    mergedMap.putAll(data);

    return mergedMap;
  }
}
