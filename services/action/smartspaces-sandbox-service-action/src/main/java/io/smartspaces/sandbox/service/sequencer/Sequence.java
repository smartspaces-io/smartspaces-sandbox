/*
 * Copyright (C) 2016 Keith M. Hughes
 * Copyright (C) 2015 Google Inc.
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

package io.smartspaces.sandbox.service.sequencer;

import java.util.Collection;

import io.smartspaces.resource.managed.ManagedResource;

/**
 * A sequence of actions to take place.
 *
 * @author Keith M. Hughes
 */
public interface Sequence extends ManagedResource {

  /**
   * Add a.collection of new element.
   *
   * @param elements
   *          the elements to add
   *
   * @return this sequence
   */
  Sequence add(SequenceElement... elements);

  /**
   * Add a.collection of new element.
   *
   * @param elements
   *          the elements to add
   *
   * @return this sequence
   */
  Sequence add(Collection<SequenceElement> elements);
  
  /**
   * Get the current state of the sequence.
   * 
   * @return the current state of the sequence
   */
  SequenceState getState();
  
  /**
   * The state of the sequence.
   * 
   * @author Keith M. Hughes
   */
  enum SequenceState {
    
    /**
     * The sequence hasn't been started yet.
     */
    NOT_STARTED,
    
    /**
     * The sequence is running.
     */
    RUNNING,
    
    /**
     * The sequence has successfully completed.
     */
    COMPLETED,
    
    /**
     * The sequence has stopped from an error.
     */
    ERROR,
  }
}
