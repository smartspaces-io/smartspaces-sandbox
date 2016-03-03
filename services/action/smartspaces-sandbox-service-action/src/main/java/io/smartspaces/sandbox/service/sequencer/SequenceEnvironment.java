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

package io.smartspaces.sandbox.service.sequencer;

import io.smartspaces.system.SmartSpacesEnvironment;

/**
 * An environment for executing a sequence.
 * 
 * @author Keith M. Hughes
 */
public class SequenceEnvironment {

  /**
   * The space environment to use.
   */
  private final SmartSpacesEnvironment spaceEnvironment;

  /**
   * The sequencer running the sequence
   */
  private final Sequencer sequencer;

  /**
   * The sequence being run
   */
  private final Sequence sequence;

  /**
   * Construct a new environment.
   * 
   * @param sequencer
   *          the sequencer running the sequence
   * @param sequence
   *          the sequence being run
   * @param spaceEnvironment
   *          the space environment for the sequence environment
   */
  public SequenceEnvironment(Sequencer sequencer, Sequence sequence,
      SmartSpacesEnvironment spaceEnvironment) {
    this.sequencer = sequencer;
    this.sequence = sequence;
    this.spaceEnvironment = spaceEnvironment;
  }

  /**
   * Get the sequencer the sequence is running under.
   * 
   * @return the sequencer
   */
  public Sequencer getSequencer() {
    return sequencer;
  }

  /**
   * Get the complete sequence being run.
   * 
   * @return the sequence
   */
  public Sequence getSequence() {
    return sequence;
  }

  /**
   * Get the space environment.
   * 
   * @return the space environment
   */
  public SmartSpacesEnvironment getSpaceEnvironment() {
    return spaceEnvironment;
  }
}
