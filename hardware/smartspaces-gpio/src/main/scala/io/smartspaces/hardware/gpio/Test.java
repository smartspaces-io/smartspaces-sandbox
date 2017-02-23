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

package io.smartspaces.hardware.gpio;

import io.smartspaces.event.observable.BaseObserver;
import io.smartspaces.hardware.services.NfcScanner;
import io.smartspaces.hardware.services.Pn532NfcScanner;
import io.smartspaces.system.StandaloneSmartSpacesEnvironment;

/**
 * Test driver for the PN532.
 * 
 * @author Keith M. Hughes
 */
public class Test {

  public static void main(String[] args) {
    StandaloneSmartSpacesEnvironment spaceEnvironment =
        StandaloneSmartSpacesEnvironment.newStandaloneSmartSpacesEnvironment();
    
    spaceEnvironment.registerAndStartService(new Pi4jGpioService());
    
    NfcScanner nfc =
        new Pn532NfcScanner(spaceEnvironment, spaceEnvironment.getContainerManagedScope(), spaceEnvironment.getLog());

    spaceEnvironment.addManagedResource(nfc);

    nfc.getObservable().subscribe(new BaseObserver<String>() {

      @Override
      public void onNext(String value) {
        System.out.println("UUID is " + value);
      }
    });
  }

}
