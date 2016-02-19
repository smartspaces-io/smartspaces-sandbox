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

package io.smartspaces.sandbox.service.action;

import io.smartspaces.resource.VersionRange;

import java.util.Map;

/**
 * The reference for an action.
 * 
 * @author Keith M. Hughes
 */
public class BasicSimpleActionReference extends BaseActionReference
    implements SimpleActionReference {

  /**
   * The name of the action source.
   */
  private String actionSource;

  /**
   * The name of the action.
   */
  private String actionName;

  /**
   * The version range for the action source.
   */
  private VersionRange actionSourceVersionRange;

  /**
   * Construct a reference.
   * 
   * <p>
   * The data is empty.
   * 
   * @param name
   *          the name of the reference
   * @param description
   *          the description of the reference, can be {@code null}
   * @param actionSource
   *          the name of the action source
   * @param actionSourceVersionRange
   *          the version range for the action source
   * @param actionName
   *          the action name
   */
  public BasicSimpleActionReference(String name, String description, String actionSource,
      VersionRange actionSourceVersionRange, String actionName) {
    this(name, description, actionSource, actionSourceVersionRange, actionName,
        BaseActionReference.NO_DATA);
  }

  /**
   * Construct a reference.
   * 
   * @param name
   *          the name of the reference
   * @param description
   *          the description of the reference, can be {@code null}
   * @param actionSource
   *          the name of the action source
   * @param actionSourceVersionRange
   *          the version range for the action source
   * @param actionName
   *          the action name
   * @param data
   *          the data for the action
   */
  public BasicSimpleActionReference(String name, String description, String actionSource,
      VersionRange actionSourceVersionRange, String actionName,
      Map<String, ? extends Object> data) {
    super(name, description, data);

    this.actionSource = actionSource;
    this.actionName = actionName;
  }

  @Override
  public String getActionSource() {
    return actionSource;
  }

  @Override
  public VersionRange getActionSourceVersionRange() {
    return actionSourceVersionRange;
  }

  @Override
  public String getActionName() {
    return actionName;
  }

  @Override
  public void performAction(ActionService actionService, Map<String, ? extends Object> data) {
    actionService.performSimpleActionReference(this, data);
  }
}
