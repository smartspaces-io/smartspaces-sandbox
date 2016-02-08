/*
 * Copyright (C) 2014 Keith M. Hughes
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

package io.smartspaces.sandbox.service.hardware.philipshue;

/**
 * A Philips Hue Light.
 * 
 * @author Keith M. Hughes
 */
public class PhilipsHueLight {

  /**
   * Unique ID of the light.
   * 
   * <p>
   * Every light has a unique identifier for the particular physical bulb. This
   * is it.
   */
  private String uniqueId;

  /**
   * The name of the light.
   */
  private String name;

  /**
   * Control ID of the light.
   * 
   * <p>
   * This is used by the API for controlling the light.
   */
  private String controlId;

  /**
   * {@code true} if the light is on.
   */
  private boolean on;

  /**
   * The color saturation for the light.
   */
  private int saturation = 255;

  /**
   * The brightness for the light.
   */
  private int brightness = 255;

  /**
   * The hue of the light.
   */
  private int hue = 4500;

  /**
   * Construct a new light.
   * 
   * @param uniqueId
   *          the unique ID for the light
   */
  public PhilipsHueLight(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name for the light.
   * 
   * @param name
   *          the new name
   * 
   * @return the light
   */
  public PhilipsHueLight setName(String name) {
    this.name = name;

    return this;
  }

  /**
   * Set the control ID for the light.
   * 
   * @param controlId
   *          the new control ID
   * 
   * @return the light
   */
  public PhilipsHueLight setControlId(String controlId) {
    this.controlId = controlId;

    return this;
  }

  public String getControlId() {
    return controlId;
  }

  public boolean isOn() {
    return on;
  }

  public PhilipsHueLight setOn(boolean on) {
    this.on = on;

    return this;
  }

  public int getSaturation() {
    return saturation;
  }

  public PhilipsHueLight setSaturation(int saturation) {
    this.saturation = saturation;

    return this;
  }

  public int getBrightness() {
    return brightness;
  }

  public PhilipsHueLight setBrightness(int brightness) {
    this.brightness = brightness;

    return this;
  }

  public int getHue() {
    return hue;
  }

  public PhilipsHueLight setHue(int hue) {
    this.hue = hue;

    return this;
  }

  @Override
  public String toString() {
    return "PhilipsHueLight [uniqueId=" + uniqueId + ", name=" + name + ", controlId=" + controlId
        + ", on=" + on + ", saturation=" + saturation + ", brightness=" + brightness + ", hue="
        + hue + "]";
  }
}
