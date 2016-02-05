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

package io.smartspaces.sandbox.util.math.interpolation;

import io.smartspaces.sandbox.util.concurrency.BaseManagedCommandRunner;
import io.smartspaces.util.concurrency.ManagedCommand;
import io.smartspaces.util.concurrency.ManagedCommands;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

/**
 * A standard implementation of an {@link ArraySteppingInterpolatorRunner}.
 *
 * @author Keith M. Hughes
 */
public class StandardArraySteppingInterpolatorRunner extends BaseManagedCommandRunner implements
    ArraySteppingInterpolatorRunner {

  /**
   * Create a runner with associated linear interpolator.
   *
   * <p>
   * This class uses the {@link StandardLinearArraySteppingInterpolator}, see
   * the class for details.
   *
   * @param numberValues
   *          the number of values in the array for the array interpolator
   * @param totalDuration
   *          the amount of time the runner runs, in milliseconds
   * @param updateInterval
   *          the amount of time between updates, in milliseconds
   * @param listener
   *          the listener for update events
   * @param managedCommands
   *          the managed commands to use for creating the runner thread
   * @param log
   *          the logger to use
   *
   * @return the new runner
   */
  public static StandardArraySteppingInterpolatorRunner newLinearRunner(int numberValues,
      long totalDuration, long updateInterval, ArraySteppingInterpolatorRunnerListener listener,
      ManagedCommands managedCommands, Log log) {

    ArraySteppingInterpolator interpolator =
        new StandardLinearArraySteppingInterpolator(numberValues,
            (int) (totalDuration / updateInterval));

    return new StandardArraySteppingInterpolatorRunner(totalDuration, updateInterval, interpolator,
        listener, managedCommands, log);
  }

  /**
   * The total amount of time for the interpolation to be done over, in msec.
   */
  private long totalDuration;

  /**
   * How often the runner should be rerun, in msec.
   */
  private long updateInterval;

  /**
   * The interpolator.
   */
  private ArraySteppingInterpolator interpolator;

  /**
   * The listener for interpolation updates.
   */
  private ArraySteppingInterpolatorRunnerListener listener;

  /**
   * Construct a runner.
   *
   * @param totalDuration
   *          the amount of time the runner runs, in milliseconds
   * @param updateInterval
   *          the amount of time between updates, in milliseconds
   * @param interpolator
   *          the interpolator to be run
   * @param listener
   *          the listener for update events
   * @param managedCommands
   *          the managed commands to use for creating the runner thread
   * @param log
   *          the logger to use
   */
  public StandardArraySteppingInterpolatorRunner(long totalDuration, long updateInterval,
      ArraySteppingInterpolator interpolator, ArraySteppingInterpolatorRunnerListener listener,
      ManagedCommands managedCommands, Log log) {
    super(managedCommands, log);

    this.totalDuration = totalDuration;
    this.updateInterval = updateInterval;
    this.interpolator = interpolator;
    this.listener = listener;
  }

  @Override
  public long getTotalDuration() {
    return totalDuration;
  }

  @Override
  public long getUpdateInterval() {
    return updateInterval;
  }

  @Override
  public ArraySteppingInterpolator getInterpolator() {
    return interpolator;
  }

  @Override
  protected ManagedCommand newManagedCommand(ManagedCommands managedCommands, Runnable runnable) {
    return managedCommands.scheduleAtFixedRate(runnable, 0, updateInterval, TimeUnit.MILLISECONDS);
  }

  @Override
  protected void onStartup() {
    interpolator.initialize();
  }

  @Override
  protected void performTask() {
    try {
      if (interpolator.canStep()) {
        listener.onArrayInterpolatorStep(interpolator.getStep(), interpolator.getCurrentValues());

        interpolator.step();
      } else {
        shutdownSuccessfullyFromTask();
      }
    } catch (Exception e) {
      // TODO(keith): Decide if this should then abort the interpolation run or
      // if the abortion should be settable.
      handleTaskError("Array interpolator listener threw exception", e, true);
    }
  }
}
