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

package interactivespaces.sandbox.service.control.dmx;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * A basic container for DMX data instances.
 *
 * @author Keith M. Hughes
 */
public class BasicDmxDataCollection implements DmxDataCollection {

  /**
   * The items in the collection.
   */
  private List<DmxData> items = Lists.newArrayList();

  @Override
  public void writeDmxData(DmxControlEndpoint endpoint) {
    for (DmxData item : items) {
      item.writeDmxData(endpoint);
    }
  }

  @Override
  public void addDmxData(DmxData data) {
    items.add(data);
  }

  @Override
  public void removeDmxData(DmxData data) {
    items.remove(data);
  }


  @Override
  public Iterator<DmxData> iterator() {
    return items.iterator();
  }
}
