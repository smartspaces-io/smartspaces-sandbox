/*
 * Copyright (C) 2014 Google Inc.
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
 * This package supports a very simple DMX protocol that can be implemented with simple hardware like
 * Arduinos.
 *
 * <p>
 * A packet consists of a sequence of numbers followed by a designator.
 *
 * <p>
 * Designators are:
 *
 * <ul>
 * <li>{@code c} for a channel</li>
 * <li>{@code w} for a value</li>
 * </ul>
 *
 * <p>
 * A sample sequence would be {@code 1c12w} which would place the value {@code 12} on channel {@code 1}.
 *
 * <p>
 * This works with the <a href="http://store.arduino.cc/product/T040060">TinkerKit DMX Master example Arduino code</a>.
 */
package interactivespaces.sandbox.service.control.dmx.internal.genericserial;

