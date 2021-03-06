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

package io.smartspaces.sandbox.service.sequencer.basic;

import io.smartspaces.SimpleSmartSpacesException;
import io.smartspaces.sandbox.service.sequencer.Sequence;
import io.smartspaces.sandbox.service.sequencer.SequenceElement;
import io.smartspaces.sandbox.service.sequencer.SequenceExecutionContext;
import io.smartspaces.util.concurrency.ManagedCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A basic implementation of the Sequencer interface.
 *
 * @author Keith M. Hughes
 */
public class ManagedCommandSequence implements Sequence {

  /**
   * The sequencer that created this sequence.
   */
  private ManagedCommandSequencer sequencer;

  /**
   * The sequencer elements.
   */
  private List<SequenceElement> sequencerElements = new ArrayList<>();

  /**
   * The managed command running this sequence.
   */
  private ManagedCommand managedCommand;

  /**
   * The current state of the sequence.
   */
  private AtomicReference<SequenceState> state =
      new AtomicReference<SequenceState>(SequenceState.NOT_STARTED);

  /**
   * Construct a new sequence.
   *
   * @param scheduler
   *          the sequencer that created this sequence
   */
  public ManagedCommandSequence(ManagedCommandSequencer scheduler) {
    this.sequencer = scheduler;
  }

  @Override
  public synchronized Sequence add(SequenceElement... elements) {
    Collections.addAll(sequencerElements, elements);

    return this;
  }

  @Override
  public synchronized Sequence add(Collection<SequenceElement> elements) {
    sequencerElements.addAll(elements);

    return this;
  }

  @Override
  public void startup() {
    synchronized (this) {
      SequenceState currentState = state.get();
      if (currentState == SequenceState.NOT_STARTED) {
        state.set(SequenceState.RUNNING);
      } else if (currentState == SequenceState.RUNNING) {
        return;
      } else {
        throw SimpleSmartSpacesException
            .newFormattedException("The sequence has completed with end state %s", currentState);
      }
    }
    managedCommand = sequencer.startSequence(this);
  }

  @Override
  public synchronized void shutdown() {
    managedCommand.cancel();
  }

  @Override
  public synchronized SequenceState getState() {
    return state.get();
  }

  /**
   * Run the sequence.
   * 
   * @param sequencer
   *          the sequencer to run under
   */
      void runSequence(ManagedCommandSequencer sequencer) {
    SequenceExecutionContext sequenceExecutionContext =
        new SequenceExecutionContext(sequencer, this, sequencer.getSpaceEnvironment());

    try {
      for (SequenceElement currentElement : sequencerElements) {
        if (Thread.interrupted()) {
          break;
        }

        currentElement.run(sequenceExecutionContext);
      }

      state.set(SequenceState.COMPLETED);
    } catch (Throwable e) {
      state.set(SequenceState.ERROR);
      sequencer.getLog().error("Sequence interrupted due to error", e);
    }
  }
}
