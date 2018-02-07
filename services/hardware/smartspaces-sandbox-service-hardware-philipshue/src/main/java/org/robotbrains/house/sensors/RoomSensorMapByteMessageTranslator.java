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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import com.google.common.collect.Maps;

import io.smartspaces.SmartSpacesException;
import io.smartspaces.messages.ByteMessageTranslator;

/**
 * A translator for byte messages from the XBee radio sensor module.
 * 
 * <p>
 * The module has temperature, humidity, and light.
 * 
 * @author Keith M. Hughes
 */
public class RoomSensorMapByteMessageTranslator implements
    ByteMessageTranslator<Map<String, Object>> {

  @Override
  public Map<String, Object> translate(byte[] message) throws SmartSpacesException {
    Map<String, Object> result = Maps.newHashMap();
    ByteBuffer data = ByteBuffer.wrap(message).order(ByteOrder.LITTLE_ENDIAN);

    result.put("temperature", data.getFloat(0));
    result.put("humidity", data.getFloat(4));
    result.put("light", data.getShort(8));
    result.put("temperature2", data.getFloat(10));

    return result;
  }
}
