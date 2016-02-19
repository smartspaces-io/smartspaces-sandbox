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

/**
 * A simple action source. that references a source and an action from the
 * source.
 * 
 * @author Keith M. Hughes
 */
public interface SimpleActionReference extends ActionReference {

  /**
   * Get the action source.
   * 
   * @return the action source
   */
  String getActionSource();

  /**
   * Get the name of the action.
   * 
   * @return the name of the action
   */
  String getActionName();

  /**
   * Get the version range for the action.
   * 
   * @return the version range
   */
  VersionRange getActionSourceVersionRange();
}