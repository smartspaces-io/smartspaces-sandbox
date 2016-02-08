/*
 * Copyright (C) 2014 Keith M. Hughes.
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
 * Message components for the Philips Hue REST API.
 * 
 * @author Keith M. Hughes
 */
public class PhilipsHueRestMessages {

  /**
   * The name field in a Philips Hue message.
   */
  public static final String PHILIPS_HUE_FIELD_LIGHT_NAME = "name";

  /**
   * The state field in a Philips Hue message.
   */
  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE = "state";

  /**
   * The unique ID field in a Philips Hue message.
   */
  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE_UNIQUEID = "uniqueid";

  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE_HUE = "hue";

  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE_BRIGHTNESS = "bri";

  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE_SATURATION = "sat";

  public static final String PHILIPS_HUE_FIELD_LIGHT_STATE_ON = "on";

}
