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

package interactivespaces.sandbox.service.control.dmx.internal.enttecpro;

import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.sandbox.service.control.dmx.DmxControlEndpoint;
import interactivespaces.sandbox.service.control.dmx.DmxData;
import interactivespaces.service.comm.serial.SerialCommunicationEndpoint;
import interactivespaces.service.comm.serial.SerialCommunicationEndpoint.Parity;
import interactivespaces.util.ByteUtils;
import interactivespaces.util.concurrency.CancellableLoop;

import org.apache.commons.logging.Log;

import java.util.concurrent.ScheduledExecutorService;

/**
 * A DMX control endpoint that works with the Enttec Pro DMX interface.
 *
 * @author Keith M. Hughes
 */
public class EnttecProDmxControlEndpoint implements DmxControlEndpoint {

  /**
   * The number of stop bits for serial communication with the Enttec Pro.
   */
  public static final int ENTTEC_PRO_STOP_BITS = 1;

  /**
   * The parity for serial communication with the Enttec Pro.
   */
  public static final Parity ENTTEC_PRO_PARITY = SerialCommunicationEndpoint.Parity.NONE;

  /**
   * The number of data bits for serial communication with the Enttec Pro.
   */
  public static final int ENTTEC_PRO_DATA_BITS = 8;

  /**
   * The baud rate for serial communication with the Enttec Pro.
   */
  public static final int ENTTEC_PRO_BAUD_RATE = 9600;

  /**
   * This value is used to open all DMX512 commands.
   */
  public static final byte DMXOPEN = (byte) 0x7e;

  /**
   * This value is used to close all DMX512 commands.
   */
  public static final byte DMXCLOSE = (byte) 0xe7;

  /**
   * This value is used to set the intensity of a DMX value.
   */
  public static final byte[] DMXINTENSITY = new byte[] { (byte) 0x06, (byte) 0x01, (byte) 0x02 };

  /**
   * The first initialization string to be sent to the Enttec Pro.
   */
  public static final byte[] ENTTECPPRO_INIT1 = new byte[] { (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00,
      (byte) 0x00 };

  /**
   * The second initialization string to be sent to the Enttec Pro.
   */
  public static final byte[] ENTTECPPRO_INIT2 = new byte[] { (byte) 0x0a, (byte) 0x02, (byte) 0x00, (byte) 0x00,
      (byte) 0x00 };

  /**
   * The number of bytes in an Entec Pro DMX control packet.
   */
  public static final int ENTTEC_PRO_CONTROL_PACKET_LENGTH = 513;

  /**
   * Where in the EntTech Pro length we start writing data.
   *
   * <p>
   * Do be aware that there is a {@code 0) spacer byte between the end of the DMX Intensity command and the data. Since
   * channels start at {@code 1} it means this offset is enough.
   */
  public static final int ENTTECH_PRO_DATA_OFFSET = DMXINTENSITY.length + 1;

  /**
   * The full length of the intensity packet command.
   *
   * <p>
   * This includes the DMXOPEN and DMXCLOSE bytes.
   */
  public static final int ENTTEC_PRO_FULL_INTENSITY_PACKET_LENGTH = ENTTEC_PRO_CONTROL_PACKET_LENGTH
      + DMXINTENSITY.length + 2;

  /**
   * The sampling time for testing whether the Enttec Pro has written information back.
   */
  private static final long READER_LOOP_WAIT_DELAY = 100;

  /**
   * The communication endpoint for speaking with the DMX controller.
   */
  private SerialCommunicationEndpoint commEndpoint;

  /**
   * The executor service for running the reader loop.
   */
  private final ScheduledExecutorService executorService;

  /**
   * Log for the endpoint.
   */
  private final Log log;

  /**
   * Loop for reading info from the DMX controller.
   */
  private CancellableLoop readerLoop;

  /**
   * The data for a DMX universe. This represents the entire state of the entire universe.
   */
  private byte[] universeData;

  /**
   * The buffer for reading data back from the Enttec Pro.
   */
  private byte[] dmxReadBuffer = new byte[256];

  /**
   * Construct a new endpoint.
   *
   * @param commEndpoint
   *          the serial communication endpoint
   * @param executorService
   *          the executor service for obtaining threads
   * @param log
   *          the logger
   */
  public EnttecProDmxControlEndpoint(SerialCommunicationEndpoint commEndpoint,
      ScheduledExecutorService executorService, Log log) {
    this.commEndpoint = commEndpoint;
    this.executorService = executorService;
    this.log = log;

    universeData = new byte[ENTTEC_PRO_FULL_INTENSITY_PACKET_LENGTH];

    universeData[0] = DMXOPEN;
    System.arraycopy(DMXINTENSITY, 0, universeData, 1, DMXINTENSITY.length);
    universeData[ENTTEC_PRO_FULL_INTENSITY_PACKET_LENGTH - 1] = DMXCLOSE;
  }

  @Override
  public void startup() {
    log.info("Starting up DMX serial connection");
    commEndpoint.setBaud(ENTTEC_PRO_BAUD_RATE).setDataBits(ENTTEC_PRO_DATA_BITS).setParity(ENTTEC_PRO_PARITY)
        .setStopBits(ENTTEC_PRO_STOP_BITS);
    commEndpoint.startup();

    readerLoop = new CancellableLoop() {
      @Override
      protected void loop() throws InterruptedException {
        readDmxFrame();
      }

      @Override
      protected void handleException(Exception e) {
        log.error("Error while reading DMX control frame", e);
      }

      @Override
      protected void cleanup() {
        log.info("DMX control serial connection read loop shut down");
      }
    };

    executorService.submit(readerLoop);

    commEndpoint.write(ENTTECPPRO_INIT1);
    commEndpoint.write(ENTTECPPRO_INIT2);
  }

  @Override
  public void shutdown() {
    log.info("Shutting down DMX serial connection");

    if (readerLoop != null) {
      readerLoop.cancel();

      readerLoop = null;
    }

    if (commEndpoint != null) {
      commEndpoint.shutdown();
      commEndpoint = null;
    }
  }

  @Override
  public void writeDmxData(int channel, int... data) {
    if (data == null) {
      throw new SimpleInteractiveSpacesException("No DMX data");
    } else if (channel < DMX_CHANNEL_MINIMUM || channel > DMX_CHANNEL_MAXIMUM) {
      throw new SimpleInteractiveSpacesException(String.format("The DMX channel %d is out of range of 1 to 512",
          channel));
    } else if (data.length + channel > DMX_CHANNEL_MAXIMUM + 1) {
      throw new SimpleInteractiveSpacesException(String.format(
          "The DMX channel %d + data of length %d goes outside of the DMX range", channel, data.length));
    }

    int writePos = ENTTECH_PRO_DATA_OFFSET + channel;
    for (int item : data) {
      universeData[writePos++] = (byte) (item & 0xff);
    }

    commEndpoint.write(universeData);
  }

  @Override
  public void writeDmxData(DmxData data) {
    data.writeDmxData(this);
  }

  /**
   * Read a DMX frame from the serial device.
   *
   * @throws InterruptedException
   *           the read was interrupted
   */
  private void readDmxFrame() throws InterruptedException {
    while (commEndpoint.available() != 0) {
      commEndpoint.read(dmxReadBuffer);
      log.debug(String.format("DMX bytes read %s", ByteUtils.toHexString(dmxReadBuffer)));
    }

    Thread.sleep(READER_LOOP_WAIT_DELAY);
  }

  @Override
  public String toString() {
    return "EnttecProDmxControlEndpoint [commEndpoint=" + commEndpoint + "]";
  }
}
