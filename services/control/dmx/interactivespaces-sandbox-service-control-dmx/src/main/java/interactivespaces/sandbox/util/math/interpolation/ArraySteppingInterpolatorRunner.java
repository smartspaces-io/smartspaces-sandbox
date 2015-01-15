/*
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

package interactivespaces.sandbox.util.math.interpolation;

import interactivespaces.sandbox.util.concurrency.Runner;

/**
 * A runner that runs an interpolator in a separate thread according to a schedule.
 *
 * @author Keith M. Hughes
 */
public interface ArraySteppingInterpolatorRunner extends Runner {

  /**
   * Get the total time duration of the run.
   *
   * @return the time duration in milliseconds
   */
  long getTotalDuration();

  /**
   * Get how often the interpolator is updated.
   *
   * @return the update interval in milliseconds
   */
  long getUpdateInterval();

  /**
   * Get the interpolator.
   *
   * @return the interpolator
   */
  ArraySteppingInterpolator getInterpolator();
}
