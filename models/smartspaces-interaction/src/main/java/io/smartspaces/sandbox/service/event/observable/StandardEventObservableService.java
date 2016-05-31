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

package io.smartspaces.sandbox.service.event.observable;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import io.smartspaces.service.BaseSupportedService;
import rx.Observable;

/**
 * The standard event observable service.
 * 
 * @author Keith M. Hughes
 */
public class StandardEventObservableService extends BaseSupportedService
    implements EventObservableService {

  /**
   * The map of observables.
   */
  private ConcurrentMap<String, Observable<?>> observables = new ConcurrentSkipListMap<>();

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public EventObservableService registerObservable(String observableName,
      Observable<?> observable) {
    observables.put(observableName, observable);

    return this;
  }

  @Override
  public EventObservableService unregisterObservable(String observableName) {
    observables.remove(observableName);

    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Observable<?>> T getObservable(String observableName) {
    return (T) observables.get(observableName);
  }
}
