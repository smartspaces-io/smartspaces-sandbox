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

package io.smartspaces.sandbox.interaction.entity;

/**
 * The description of an entity.
 * 
 * @author Keith M. Hughes
 */
public interface EntityDescription {

  /**
   * Get the ID of the entity.
   * 
   * @return the ID of the entity
   */
  String getId();

  /**
   * Get the display name of the entity.
   * 
   * @return the display name of the entity
   */
  String getDisplayName();

  /**
   * Get the description of the entity.
   * 
   * @return the description of the entity
   */
  String getDescription();
}