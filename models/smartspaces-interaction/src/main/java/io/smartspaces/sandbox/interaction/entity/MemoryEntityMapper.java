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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A memory based entity mapper.
 * 
 * @author Keith M. Hughes
 */
public class MemoryEntityMapper implements EntityMapper {

  /**
   * The map of one type of entity to another.
   */
  private Map<String, String> map = new ConcurrentHashMap<>();

  @Override
  public void put(String entityIdFrom, String entityIdTo) {
    map.put(entityIdFrom, entityIdTo);
  }

  @Override
  public String get(String entityIdFrom) {
    return map.get(entityIdFrom);
  }
}
