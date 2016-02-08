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

package org.robotbrains.house.sensors;

import io.smartspaces.service.comm.serial.xbee.RxResponseXBeeFrame;
import io.smartspaces.service.comm.serial.xbee.XBeeCommunicationEndpoint;
import io.smartspaces.service.comm.serial.xbee.XBeeResponseListenerSupport;

import org.apache.commons.logging.Log;

import java.util.Map;

/**
 * An XBee response handler which translates room sensor messages.
 *
 * @author Keith M. Hughes
 */
public class RoomSensorXBeeHandler extends XBeeResponseListenerSupport {

  /**
   * The logger to use.
   */
  Log log;

  /**
   * The translator for the XBee message.
   */
  private RoomSensorMapByteMessageTranslator translator = new RoomSensorMapByteMessageTranslator();

  /**
   * @param log
   */
  public RoomSensorXBeeHandler(Log log) {
    this.log = log;
  }

  @Override
  public void onRxXBeeResponse(XBeeCommunicationEndpoint endpoint, RxResponseXBeeFrame response) {
    translateMessage(response);
  }

  private void translateMessage(RxResponseXBeeFrame response) {
    Map<String, Object> sensorData = translator.translate(response.getReceivedData());
    log.info(String.format("Radio %s: Data %s", response.getAddress64(), sensorData));
  }
}
