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

import io.smartspaces.hardware.gpio.GpioService;
import io.smartspaces.hardware.gpio.device.Pn532Device;
import io.smartspaces.hardware.gpio.device.SpiPn532Device;
import io.smartspaces.scope.ManagedScope;
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.tasks.ManagedTask;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.concurrent.TimeUnit;

/**
 * An NFC scanner that uses a local SPI-based PN532 board.
 * 
 * @author Keith M. Hughes
 */
public class Pn532NfcScanner implements NfcScanner {

  /**
   * The device for controlling a PN532.
   */
  private Pn532Device pn532;

  /**
   * The last good UUID obtained.
   */
  private String lastGoodUuid;

  /**
   * The future for the thread that scans for tags.
   */
  private ManagedTask tagScanTask;

  /**
   * The observable for the UUID events.
   */
  private Subject<String> observable = PublishSubject.create();

  /**
   * The space environment to run under.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  /**
   * The managed scope to run this scanner under.
   */
  private ManagedScope managedScope;

  /**
   * Construct a scanner.
   * 
   * @param spaceEnvironment
   *          the space environment to run under
   * @param managedScope
   *          the managed scope to run under.
   */
  public Pn532NfcScanner(SmartSpacesEnvironment spaceEnvironment, ManagedScope managedScope) {
    this.spaceEnvironment = spaceEnvironment;
    this.managedScope = managedScope;
  }

  @Override
  public void startup() {

    GpioService gpioService = spaceEnvironment.getServiceRegistry().getRequiredService(GpioService.SERVICE_NAME);
    gpioService.startup();

    Pin sclkPin = RaspiPin.GPIO_27;
    Pin mosiPin = RaspiPin.GPIO_04;
    Pin misoPin = RaspiPin.GPIO_17;
    Pin csPin = RaspiPin.GPIO_22;

    pn532 = new SpiPn532Device(gpioService.getSoftwareSpi(sclkPin, mosiPin, misoPin, csPin));
    pn532.startup();

    pn532.useSamConfiguration();

    tagScanTask = managedScope.managedTasks().scheduleWithFixedDelay(new Runnable() {

      @Override
      public void run() {
        scanForTag();
      }
    }, 0, 2000, TimeUnit.MILLISECONDS);

  }

  @Override
  public void shutdown() {
    tagScanTask.cancel();

    pn532.shutdown();
  }

  @Override
  public Observable<String> getObservable() {
    return observable;
  }

  /**
   * Scan for a tag and generate an event if found.
   */
  private void scanForTag() {
    byte[] uuidComponents = pn532.readPassiveTarget();

    if (uuidComponents != null) {
      String uuid = toHexString(uuidComponents);

      if (lastGoodUuid != null) {
        if (!lastGoodUuid.equals(uuid)) {
          processUuid(uuid);
        }
      } else {
        processUuid(uuid);
      }
    } else {
      // No scan this time so clear things out.
      lastGoodUuid = null;
    }
  }

  /**
   * Process a new UUID.
   * 
   * @param uuid
   *          the UUID to process
   */
  private void processUuid(String uuid) {
    lastGoodUuid = uuid;

    observable.onNext(uuid);
  }

  /**
   * Get the hex string for the bytes.
   *
   * <p>
   * The bytes are assumed to be most significant byte first.
   *
   * @param ba
   *          the array of bytes
   *
   * @return hex string for the bytes
   */
  private String toHexString(byte[] ba) {
    if (ba.length == 7) {
      return String.format("%02x%02x%02x%02x%02x%02x%02x", ba[0], ba[1], ba[2], ba[3], ba[4], ba[5],
          ba[6]);
    } else {
      return String.format("%02x%02x%02x%02x", ba[0], ba[1], ba[2], ba[3]);
    }
  }

}
