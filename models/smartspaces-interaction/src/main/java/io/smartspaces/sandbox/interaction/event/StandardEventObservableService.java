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

package io.smartspaces.sandbox.interaction.event;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.service.BaseSupportedService;

import rx.Observable;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The standard event observable service.
 * 
 * @author Keith M. Hughes
 */
public class StandardEventObservableService extends BaseSupportedService
    implements EventObservableService {

  /**
   * The service name.
   */
  public static final String SERVICE_NAME = "event.observable";

  /**
   * The map of obervables.
   */
  private ConcurrentMap<String, Observable<?>> observables = new ConcurrentSkipListMap<>();

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public <T> EventObservable<T> newEventObservable(ExtendedLog log) {
    List<Subscriber<? super T>> subscribers = new CopyOnWriteArrayList<>();
    return new EventObservable<T>(subscribers, log);
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

  /**
   * A reactive RXJava observable for Smartspace events.
   *
   * @param <T>
   *          the type of the event
   * 
   * @author Keith M. Hughes
   */
  public static class EventObservable<T> extends Observable<T> {

    /**
     * The subscribers for the observable.
     */
    private List<Subscriber<? super T>> subscribers;

    /**
     * The logger to use.
     */
    private ExtendedLog log;

    /**
     * Construct a new observable.
     * 
     * @param subscribers
     *          the subscriber list
     * @param log
     *          the logger to use
     */
    EventObservable(final List<Subscriber<? super T>> subscribers, ExtendedLog log) {
      super(new OnSubscribe<T>() {

        @Override
        public void call(Subscriber<? super T> subscriber) {
          subscribers.add(subscriber);
        }
      });

      this.subscribers = subscribers;
      this.log = log;
    }

    /**
     * Emit an event to all subscribers.
     * 
     * @param event
     *          the event to emit
     * 
     * @return this observable
     */
    public EventObservable<T> emitEvent(T event) {
      for (Subscriber<? super T> subscriber : subscribers) {
        if (!subscriber.isUnsubscribed()) {
          try {
            subscriber.onNext(event);
          } catch (Throwable e) {
            log.error("Error while calling onNext for subscriber", e);
          }
        }
      }

      return this;
    }
  }
}
