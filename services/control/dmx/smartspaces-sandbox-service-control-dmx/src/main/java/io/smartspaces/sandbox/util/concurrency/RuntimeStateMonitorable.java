/**
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

package io.smartspaces.sandbox.util.concurrency;

import io.smartspaces.util.concurrency.RuntimeState;

/**
 * Implementations of this interface expose their runtime state so that it can
 * be more easily monitored.
 *
 * @author Keith M. Hughes
 */
public interface RuntimeStateMonitorable {

  /**
   * Is the runner running?
   *
   * @return {@code true} if running
   */
  boolean isRunning();

  /**
   * Get the runtime state of the runner.
   *
   * @return the runtime state
   */
  RuntimeState getRuntimeState();

  /**
   * Get an exception from the runner.
   *
   * <p>
   * This value will not be {@code null} only if the runtime state is
   * {@code RuntimeState.CRASHED}.
   *
   * @return the exception, or {@code null}
   */
  Throwable getRuntimeStateException();
}
