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
import java.util.Map;

/**
 * A base support class for action references.
 * 
 * @author Keith M. Hughes
 */
@SuppressWarnings("unchecked")
public abstract class BaseActionReference implements ActionReference {

  /**
   * The no data map.
   */
  public static Map<String, ? extends Object> NO_DATA;

  static {
    NO_DATA = (Map<String, ? extends Object>) Collections.unmodifiableMap(Collections.EMPTY_MAP);
  }

  /**
   * Name of the reference.
   */
  private String name;

  /**
   * Description of the reference, can be {@code null}.
   */
  private String description;

  /**
   * The data for the reference.
   */
  private Map<String, ? extends Object> data;

  /**
   * Construct a new base action.
   * 
   * @param actionName
   *          the action name
   * @param data
   *          the data
   */
  public BaseActionReference(String name, String description, Map<String, ? extends Object> data) {
    this.name = name;
    this.description = description;
    this.data = (data != null) ? data : NO_DATA;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ActionReference setName(String name) {
    this.name = name;

    return this;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public ActionReference setDescription(String description) {
    this.description = description;

    return this;
  }

  @Override
  public Map<String, ? extends Object> getData() {
    return data;
  }
}
