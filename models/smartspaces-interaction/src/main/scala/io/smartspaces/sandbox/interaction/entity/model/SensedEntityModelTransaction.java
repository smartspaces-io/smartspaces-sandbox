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

package io.smartspaces.sandbox.interaction.entity.model;

/**
 * A transaction taking place for a sensed entity model.
 * 
 * @param <T>
 *          the type of the sensed entity
 * @param <U>
 *          the type of the return value
 * 
 * @author Keith M. Hughes
 */
public interface SensedEntityModelTransaction<T extends SensedEntityModel, U> {
  
  /**
   * The code to perform inside the transaction.
   * 
   * @param model
   *          the model
   * 
   * @return the result of the transaction
   */
  U perform(T model);
}
