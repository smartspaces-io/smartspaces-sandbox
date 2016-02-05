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

import io.smartspaces.sandbox.service.sequencer.Sequence;
import io.smartspaces.sandbox.service.sequencer.SequenceElement;
import io.smartspaces.util.concurrency.ManagedCommand;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A basic implementation of the Sequencer interface.
 *
 * @author Keith M. Hughes
 */
public class ManagedCommandSequence implements Sequence {

  /**
   * The sequencer that created this sequence.
   */
  private ManagedCommandSequencer scheduler;

  /**
   * The sequencer elements.
   */
  private List<SequenceElement> sequencerElements = Lists.newArrayList();

  /**
   * The managed command running this sequence.
   */
  private ManagedCommand managedCommand;

  /**
   * Construct a new sequence.
   *
   * @param scheduler
   *          the sequencer that created this sequence
   */
  public ManagedCommandSequence(ManagedCommandSequencer scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Sequence add(SequenceElement... elements) {
    Collections.addAll(sequencerElements, elements);

    return this;
  }

  @Override
  public Sequence add(List<SequenceElement> elements) {
    sequencerElements.addAll(elements);

    return this;
  }

  @Override
  public synchronized void startup() {
    managedCommand = scheduler.startSequence(this);
  }

  @Override
  public synchronized void shutdown() {
    managedCommand.cancel();
  }

  /**
   * Run the sequence.
   */
  void runSequence() {
    for (SequenceElement currentElement : sequencerElements) {
      if (Thread.interrupted()) {
        break;
      }

      currentElement.run(scheduler);
    }
  }
}
