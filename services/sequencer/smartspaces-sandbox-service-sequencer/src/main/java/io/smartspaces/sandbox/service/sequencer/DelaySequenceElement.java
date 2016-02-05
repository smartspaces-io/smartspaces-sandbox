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

import io.smartspaces.util.events.EventDelay;

/**
 * A sequence element that deplays for some amount of time.
 *
 * @author Keith M. Hughes
 */
public class DelaySequenceElement implements SequenceElement {

  /**
   * The duration, in seconds, to delay.
   */
  private EventDelay delay;

  /**
   * Create a DelayActionElement that will delay for the given duration.
   *
   * @param delay
   *          the amount of time to delay
   */
  public DelaySequenceElement(EventDelay delay) {
    this.delay = delay;
  }

  @Override
  public void run(Sequencer scheduler) {
    try {
      Thread.sleep(delay.getUnit().toMillis(delay.getDelay()));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
