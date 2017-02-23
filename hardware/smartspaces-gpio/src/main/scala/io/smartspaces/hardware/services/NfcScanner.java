/**
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

package io.smartspaces.hardware.services;

import io.smartspaces.resource.managed.ManagedResource;

import io.reactivex.Observable;

/**
 * An NFC scanner.
 * 
 * @author Keith M. Hughes
 */
public interface NfcScanner extends ManagedResource {

  /**
   * Get the observable for the scanner that publishes the UUIDs scanned.
   * 
   * <p>
   * The event will be a string of only the scanned UUID.
   * 
   * @return the observable
   */
  Observable<String> getObservable();
}