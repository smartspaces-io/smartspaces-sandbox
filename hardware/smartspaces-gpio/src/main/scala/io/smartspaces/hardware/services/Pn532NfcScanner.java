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

import io.smartspaces.SmartSpacesException;
import io.smartspaces.event.observable.EventPublisherSubject;
import io.smartspaces.hardware.gpio.GpioService;
import io.smartspaces.hardware.gpio.device.Pn532Device;
import io.smartspaces.hardware.gpio.device.SpiPn532Device;
import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.scope.ManagedScope;
import io.smartspaces.system.SmartSpacesEnvironment;
import io.smartspaces.tasks.ManagedTask;

import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

/**
 * An NFC scanner that uses a local SPI-based PN532 board.
 * 
 * <p>
 * The scanner has a debounce interval. This specifies how much time must pass
 * before a second scan of the last known scan counts as a new scan. If the next
 * scan is a different tag, the scanner will immediately emit it, even if within
 * the debounce interval.
 * 
 * @author Keith M. Hughes
 */
public class Pn532NfcScanner implements NfcScanner {

  /**
   * The default interval between scans, in milliseconds.
   */
  private static final long SCAN_INTERVAL_DEFAULT = 2000;

  /**
   * The default interval for debounce, in milliseconds.
   */
  private static final long DEBOUNCE_INTERVAL_DEFAULT = 10000;

  /**
   * The device for controlling a PN532.
   */
  private Pn532Device pn532;

  /**
   * The interval between scans, in milliseconds.
   */
  private long scanInterval = SCAN_INTERVAL_DEFAULT;

  /**
   * The interval for debounce, in milliseconds.
   */
  private long debounceInterval = DEBOUNCE_INTERVAL_DEFAULT;

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
  private EventPublisherSubject<String> observable;

  /**
   * The space environment to run under.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  /**
   * The managed scope to run this scanner under.
   */
  private ManagedScope managedScope;

  /**
   * The logger for the scanner.
   */
  private ExtendedLog log;

  /**
   * The last time a scan was done.
   */
  private long lastScanTime;

  /**
   * The pin name for the SPI system clock pin.
   */
  private String sclkPinName;

  /**
   * The pin name for the SPI MOSI pin.
   */
  private String mosiPinName;

  /**
   * The pin name for the SPI MISO pin.
   */
  private String misoPinName;

  /**
   * The pin name for the SPI chip select pin.
   */
  private String csPinName;

  /**
   * Construct a scanner.
   * 
   * @param spaceEnvironment
   *          the space environment to run under
   * @param managedScope
   *          the managed scope to run under.
   */
  public Pn532NfcScanner(SmartSpacesEnvironment spaceEnvironment, ManagedScope managedScope,
      ExtendedLog log) {
    this.spaceEnvironment = spaceEnvironment;
    this.managedScope = managedScope;
    this.log = log;

    observable = EventPublisherSubject.create(log);
  }

  @Override
  public void startup() {
    if (csPinName == null || sclkPinName == null || mosiPinName == null || misoPinName == null) {
      throw new SmartSpacesException("A pin name has not been set for the PN532 Scanner");
    }
    
    GpioService gpioService =
        spaceEnvironment.getServiceRegistry().getRequiredService(GpioService.SERVICE_NAME);

    try {
      pn532 = new SpiPn532Device(
          gpioService.getSoftwareSpi(sclkPinName, mosiPinName, misoPinName, csPinName));
      pn532.startup();

      pn532.useSamConfiguration();

      log.info("Successfully initialized PN532 NFC scanner.");

      tagScanTask = managedScope.managedTasks().scheduleWithFixedDelay(new Runnable() {
        @Override
        public void run() {
          scanForTag();
        }
      }, 0, scanInterval, TimeUnit.MILLISECONDS);
    } catch (Throwable e) {
      pn532.shutdown();

      throw new SmartSpacesException("Could not start up PN532 Scanner", e);
    }
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
   * Set the scan interval between scans for tags.
   * 
   * <p>
   * Calling this after running {@link #startup()} will have no effect.
   * 
   * @param scanInterval
   *          the scan interval, in milliseconds
   */
  public void setScanInterval(long scanInterval) {
    this.scanInterval = scanInterval;
  }

  /**
   * Set the names of the pins that the scanner uses for control via SPI.
   * 
   * @param mosiPinName
   *          the MOSI pin
   * @param misoPinName
   *          the MISO pin
   * @param sclkPinName
   *          the clock pin
   * @param csPinName
   *          the chip select pin
   */
  public void setPinNames(String mosiPinName, String misoPinName, String sclkPinName,
      String csPinName) {
    this.mosiPinName = mosiPinName;
    this.misoPinName = misoPinName;
    this.sclkPinName = sclkPinName;
    this.csPinName = csPinName;
  }

  /**
   * Scan for a tag and generate an event if found.
   */
  private void scanForTag() {
    try {
      byte[] uuidComponents = pn532.readPassiveTarget();

      if (uuidComponents != null) {
        long newScanTime = spaceEnvironment.getTimeProvider().getCurrentTime();

        String uuid = toHexString(uuidComponents);

        if (lastGoodUuid != null) {
          if (!lastGoodUuid.equals(uuid)) {
            lastScanTime = newScanTime;
            processUuid(uuid);
          } else {
            // UUID is the same as was last scanned. See if in debounce time.
            long timeDiff = newScanTime - lastScanTime;

            // Immediate change the time so that if the tag is left on the
            // scanner, we never time out.
            lastScanTime = newScanTime;

            if (timeDiff > debounceInterval) {
              processUuid(uuid);
            }
          }
        } else {
          lastScanTime = newScanTime;
          processUuid(uuid);
        }
      } else {
        // No scan this time so clear things out.
        lastGoodUuid = null;
      }
    } catch (Throwable e) {
      log.error("PN532 RFID scan failed", e);
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
