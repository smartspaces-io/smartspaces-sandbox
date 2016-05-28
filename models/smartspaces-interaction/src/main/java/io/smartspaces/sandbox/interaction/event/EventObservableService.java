package io.smartspaces.sandbox.interaction.event;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.interaction.event.StandardEventObservableService.EventObservable;
import io.smartspaces.service.SupportedService;

import rx.Observable;

/**
 * The service for creating event observables and maintaining a global registry
 * of observables.
 * 
 * @author Keith M. Hughes
 */
public interface EventObservableService extends SupportedService {

  /**
   * Create a new event observable.
   * 
   * @param log
   *          the logger to use
   * @param <T>
   *          the type of the event class
   * 
   * @return the new event observable
   */
  <T> EventObservable<T> newEventObservable(ExtendedLog log);

  /**
   * Add in a new observable.
   * 
   * @param observableName
   *          the name of the observable
   * @param observable
   *          the observable
   * 
   * @return this service
   */
  EventObservableService registerObservable(String observableName, Observable<?> observable);

  /**
   * Remove an observable.
   * 
   * <p>
   * Does nothing if the observable wasn't there.
   * 
   * @param observableName
   *          the name of the observable
   * 
   * @return this service
   */
  EventObservableService unregisterObservable(String observableName);

  /**
   * Get the observable with a given name.
   * 
   * @param observableName
   *          the name of the observable
   * 
   * @return the named observable, or {@code null} if no such observable
   */
  <T extends Observable<?>> T getObservable(String observableName);

}