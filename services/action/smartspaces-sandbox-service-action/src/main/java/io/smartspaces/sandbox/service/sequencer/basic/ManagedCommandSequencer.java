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
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;
import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.concurrency.ManagedCommand;
import io.smartspaces.util.concurrency.SimpleManagedCommands;
import org.apache.commons.logging.Log;

/**
 * An implementation of the Sequencer interface that uses ManagedCommands.
 *
 * @author Keith M. Hughes
 */
public class ManagedCommandSequencer implements Sequencer {

  public static void main(String[] args) {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();

    ManagedCommandSequencer sequencer =
        new ManagedCommandSequencer(spaceEnvironment, spaceEnvironment.getLog());

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

    sequencer.shutdown();
    spaceEnvironment.shutdown();
  }

  /**
   * THe space environment for the sequencer.
   */
  private final SmartSpacesEnvironment spaceEnvironment;

  /**
   * The instance of ManagedCommands to use for scheduling.
   */
  private final SimpleManagedCommands managedCommands;

  /**
   * The logger for the sequencer.
   */
  private final Log log;

  /**
   * Create a ManagedCommandScheduler with the given ManagedCommands instance.
   *
   * @param spaceEnvironment
   *          the space environment to execute under
   * @param log
   *          the logger to use
   */
  public ManagedCommandSequencer(SmartSpacesEnvironment spaceEnvironment, Log log) {
    this.spaceEnvironment = spaceEnvironment;
    this.managedCommands = new SimpleManagedCommands(spaceEnvironment.getExecutorService(), log);
    this.log = log;
  }

  @Override
  public void startup() {
    // Nothing to do.
  }

  @Override
  public void shutdown() {
    managedCommands.shutdownAll();
  }

  @Override
  public Sequence newSequence() {
    return new ManagedCommandSequence(this);
  }

  @Override
  public Log getLog() {
    return log;
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
    return managedCommands.submit(() -> sequence.runSequence(ManagedCommandSequencer.this));
  }

  /**
   * Get the space environment.
   * 
   * @return the space environment
   */
      SmartSpacesEnvironment getSpaceEnvironment() {
    return spaceEnvironment;
  }
}
