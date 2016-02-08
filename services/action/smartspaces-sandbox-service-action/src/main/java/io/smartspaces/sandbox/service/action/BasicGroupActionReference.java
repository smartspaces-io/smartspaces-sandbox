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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * A group of action references.
 * 
 * @author Keith M. Hughes
 */
public class BasicGroupActionReference extends BaseActionReference implements
    GroupActionReference {

  /**
   * The action references in the group.
   */
  private List<ActionReference> actionReferences = Lists.newArrayList();

  /**
   * Construct a new group with no data.
   * 
   * @param name
   *          the name of the group
   * @param description
   *          the description of the group
   */
  public BasicGroupActionReference(String name, String description) {
    this(name, description, BaseActionReference.NO_DATA);
  }

  /**
   * Construct a new group.
   * 
   * @param name
   *          the name of the group
   * @param description
   *          the description of the group
   * @param data
   *          the data for the group
   */
  public BasicGroupActionReference(String name, String description,
      Map<String, ? extends Object> data) {
    super(name, description, data);
  }

  @Override
  public GroupActionReference addActionReference(ActionReference... references) {
    if (references != null) {
      Collections.addAll(actionReferences, references);
    }

    return this;
  }

  @Override
  public List<ActionReference> getActionReferences() {
    return actionReferences;
  }

  @Override
  public void performAction(ActionService actionService, Map<String, ? extends Object> data) {
    actionService.performGroupActionReference(this, data);
  }
}
