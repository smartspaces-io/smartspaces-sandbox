/*
 * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.sandbox.interaction.processing.sensor;

import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.base.Charsets;

import io.smartspaces.messaging.codec.MessageCodec;
import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;
import io.smartspaces.util.data.json.JsonMapper;
import io.smartspaces.util.data.json.StandardJsonMapper;

/**
 * A codec for translating between {@link DynamicObject}s and byte arrays.
 * 
 * @author Keith M. Hughes
 */
public class DynamicObjectByteArrayCodec implements MessageCodec<DynamicObject, byte[]> {

  /**
   * The default charset for the codec.
   */
  public static final Charset CHARSET_DEFAULT = Charsets.UTF_8;

  /**
   * The JSON mapper for message translation.
   */
  private static final JsonMapper MAPPER = StandardJsonMapper.INSTANCE;

  /**
   * The character set for the generic message encoding.
   */
  private Charset charset;

  /**
   * Construct a codec that supports JSON ended in charset
   * {@link #CHARSET_DEFAULT}.
   */
  public DynamicObjectByteArrayCodec() {
    this(CHARSET_DEFAULT);
  }

  /**
   * Construct a codec that supports JSON ended in the given charset.
   * 
   * @param charset
   *          the charset to use
   */
  public DynamicObjectByteArrayCodec(Charset charset) {
    this.charset = charset;
  }

  @Override
  public byte[] encode(DynamicObject out) {
    return MAPPER.toString(out.asMap()).getBytes(charset);
  }

  @Override
  public DynamicObject decode(byte[] in) {
    Map<String, Object> msg = MAPPER.parseObject(new String(in, charset));
    return new StandardDynamicObjectNavigator(msg);
  }
}
