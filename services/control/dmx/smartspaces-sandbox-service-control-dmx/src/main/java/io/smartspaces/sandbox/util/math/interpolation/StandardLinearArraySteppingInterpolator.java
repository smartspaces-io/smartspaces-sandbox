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

import io.smartspaces.SimpleSmartSpacesException;

/**
 * A standard implementation of the {@link ArraySteppingInterpolator}.
 *
 * <p>
 * Linear interplotation is used. The increments are precalculated and only
 * additions are done for each step.
 *
 * @author Keith M. Hughes
 */
public class StandardLinearArraySteppingInterpolator implements ArraySteppingInterpolator {

  /**
   * The array of start values.
   */
  private double[] startValues;

  /**
   * The array of end values.
   */
  private double[] endValues;

  /**
   * The array of increments that are added to the current values to move one
   * step.
   */
  private double[] incrementValues;

  /**
   * The array of current values.
   */
  private double[] currentValues;

  /**
   * The number of values.
   */
  private int numberValues;

  /**
   * The number of steps.
   */
  private int numberSteps;

  /**
   * The step currently being processed.
   */
  private int step;

  /**
   * Construct a new interpolator.
   *
   * @param numValues
   *          the number of values
   * @param numSteps
   *          he number of steps
   */
  public StandardLinearArraySteppingInterpolator(int numValues, int numSteps) {
    this.numberValues = numValues;
    this.numberSteps = numSteps;

    startValues = new double[numValues];
    endValues = new double[numValues];
    incrementValues = new double[numValues];
    currentValues = new double[numValues];
  }

  @Override
  public ArraySteppingInterpolator setStartValues(double... startValues) {
    checkValues(startValues);

    System.arraycopy(startValues, 0, this.startValues, 0, numberValues);

    return this;
  }

  @Override
  public double[] getStartValues() {
    return startValues;
  }

  @Override
  public ArraySteppingInterpolator setEndValues(double... endValues) {
    checkValues(endValues);

    System.arraycopy(endValues, 0, this.endValues, 0, numberValues);

    return this;
  }

  @Override
  public double[] getEndValues() {
    return endValues;
  }

  @Override
  public double[] getCurrentValues() {
    return currentValues;
  }

  @Override
  public int getNumberValues() {
    return numberValues;
  }

  @Override
  public int getNumberSteps() {
    return numberSteps;
  }

  @Override
  public int getStep() {
    return step;
  }

  @Override
  public void initialize() {
    for (int i = 0; i < numberValues; i++) {
      incrementValues[i] = (endValues[i] - startValues[i]) / numberSteps;
    }

    reset();
  }

  @Override
  public void reset() {
    for (int i = 0; i < numberValues; i++) {
      currentValues[i] = startValues[i];
    }

    step = 0;
  }

  @Override
  public boolean canStep() {
    return step <= numberSteps;
  }

  @Override
  public void step() {
    for (int i = 0; i < numberValues; i++) {
      currentValues[i] += incrementValues[i];
    }

    step++;
  }

  /**
   * Check the value array to see if it matches the number expected for the
   * interpolator.
   *
   * @param values
   *          the value array to check
   */
  private void checkValues(double[] values) {
    if (values == null || values.length != numberValues) {
      throw new SimpleSmartSpacesException(String.format(
          "Exactly %d values are needed by the interpolator and %d were supplied.", numberValues,
          (values != null) ? values.length : 0));
    }
  }

}
