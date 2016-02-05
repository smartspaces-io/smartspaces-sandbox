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
import io.smartspaces.sandbox.service.sequencer.SequenceElements;
import io.smartspaces.sandbox.service.sequencer.Sequencer;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.concurrency.ManagedCommand;
import io.smartspaces.util.concurrency.ManagedCommands;
import io.smartspaces.util.concurrency.SimpleManagedCommands;

/**
 * An implementation of the Sequencer interface that uses ManagedCommands.
 *
 * @author Keith M. Hughes
 */
public class ManagedCommandSequencer implements Sequencer {

  public static void main(String[] args) {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();

    SimpleManagedCommands commands =
        new SimpleManagedCommands(spaceEnvironment.getExecutorService(), spaceEnvironment.getLog());

    ManagedCommandSequencer sequencer = new ManagedCommandSequencer(commands);

    Sequence sequence = sequencer.newSequence();
    sequence.add(SequenceElements.runnable(new Runnable() {

      @Override
      public void run() {
        System.out.println("Hello!");
      }

    })).add(SequenceElements.repeat(10, SequenceElements.runnable(new Runnable() {

      @Override
      public void run() {
        System.out.println("Repeat");
      }

    })));

    sequence.startup();
    SmartSpacesUtilities.delay(10000);

    commands.shutdownAll();
    spaceEnvironment.shutdown();
  }

  /**
   * The instance of ManagedCommands to use for scheduling.
   */
  private ManagedCommands managedCommands;

  /**
   * Create a ManagedCommandScheduler with the given ManagedCommands instance.
   *
   * @param managedCommands
   *          an instance of ManagedCommands that will be used to schedule
   *          runnables
   */
  public ManagedCommandSequencer(ManagedCommands managedCommands) {
    this.managedCommands = managedCommands;
  }

  @Override
  public Sequence newSequence() {
    return new ManagedCommandSequence(this);
  }

  /**
   * Start executing the sequence.
   *
   * @param sequence
   *          the sequence to start
   *
   * @return the managed command running the sequence
   */
  ManagedCommand startSequence(final ManagedCommandSequence sequence) {
    return managedCommands.submit(new Runnable() {
      @Override
      public void run() {
        sequence.runSequence();
      }
    });
  }
}
