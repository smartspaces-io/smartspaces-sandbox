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

import io.smartspaces.SmartSpacesException;

/**
 * An interpolator that interpolates an array of values.
 *
 * <p>
 * Stepping interpolators perform their interpolation over a discrete number of
 * steps. Implementations can pre-calculate the changes per step.
 *
 * <p>
 * These objects are stateful. If you want to use them from multiple threads,
 * beware!
 *
 * <p>
 * {@link #initialize()} must be called after specifying a particular set of
 * start and end data. These objects can be used more than once, though
 * {@code #reset()} must be called before any subsequent uses after
 * {@link #initialize()} is called.
 *
 * @author Keith M. Hughes
 */
public interface ArraySteppingInterpolator {

  /**
   * Set the start values for the interpolator.
   *
   * @param startValues
   *          the start values
   *
   * @return the interpolator
   *
   * @throws SmartSpacesException
   *           the number of values provided is not equal to the number of
   *           values expected by the interpolator
   */
  ArraySteppingInterpolator setStartValues(double... startValues) throws SmartSpacesException;

  /**
   * Get the start values.
   *
   * <p>
   * Modifying this array modifies the start values.
   *
   * @return the start values
   */
  double[] getStartValues();

  /**
   * Set the end values for the interpolator.
   *
   * @param endValues
   *          the end values
   *
   * @return the interpolator
   *
   * @throws SmartSpacesException
   *           the number of values provided is not equal to the number of
   *           values expected by the interpolator
   */
  ArraySteppingInterpolator setEndValues(double... endValues) throws SmartSpacesException;

  /**
   * Get the end values.
   *
   * <p>
   * Modifying this array modifies the end values.
   *
   * @return the end values
   */
  double[] getEndValues();

  /**
   * Get the number of values in the array.
   *
   * @return the number of values in the array
   */
  int getNumberValues();

  /**
   * Get the number of steps the interpolator will step through.
   *
   * @return the number of steps
   */
  int getNumberSteps();

  /**
   * Get the current step number the interpolator is on.
   *
   * <p>
   * The first frame will be number {@code 0}.
   *
   * @return the current step number
   */
  int getStep();

  /**
   * Initialize the interpolator.
   *
   * <p>
   * This allows implementations to do any pre-computations for the current
   * start and end points. This method must be called before using the
   * interpolator.
   */
  void initialize();

  /**
   * Reset the interpolator.
   *
   * <p>
   * This takes the interpolator back to its start values and resets the step to
   * the beginning.
   */
  void reset();

  /**
   * Are there more steps to take?
   *
   * @return {@code true} if more steps
   */
  boolean canStep();

  /**
   * Step to the next set of values.
   */
  void step();

  /**
   * Get the current values for the interpolator.
   *
   * <p>
   * These values change each time {@link #step()} is called.
   *
   * @return the current values
   */
  double[] getCurrentValues();
}
