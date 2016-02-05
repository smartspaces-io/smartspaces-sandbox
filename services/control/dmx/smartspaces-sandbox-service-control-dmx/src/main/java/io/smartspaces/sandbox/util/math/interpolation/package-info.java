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

/**
 * This class contains support for interpolation.
 *
 * <p>
 * A stepping interpolator is an interpolator that steps over the range of the interpolation.
 * Implementations have the opportunity to pre-calculate the steps. All implementations are
 * stateful and cannot be used from multiple threads.
 *
 * <p>
 * A stepping interpolation runner steps the interpolator in a separate thread and handles all lifecycle management.
 *
 * <code><pre>
 *   long timeToFade = 5000;
 *    long updateInterval = 100;
 *
 *    ArrayInterpolatorRunner runner = StandardArrayInterpolatorRunner.newLinearRunner(
 *        6, timeToFade, updateInterval, new ArrayInterpolatorListener() {
 *
 *          @Override
 *          public void onArrayInterpolatorStep(int step, double[] currentValues) {
 *            ... your code ...
 *          }
 *        }, getManagedCommands(), getLog());
 *
 *    runner.getInterpolator().setStartValues(255, 0, 255, 0, 255, 0).setEndValues(0, 255, 0, 255, 0, 255);
 *
 *    runner.startup();
 * </pre></code>
 *
 * @author Keith M. Hughes
 */
package io.smartspaces.sandbox.util.math.interpolation;

