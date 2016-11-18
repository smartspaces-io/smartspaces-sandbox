/**
 * Adafruit PN532 breakout control library.
 * Author: Tony DiCola
 * Copyright (c) 2015 Adafruit Industries
 * 
 * Translated from Python to java by Keith M. Hughes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.smartspaces.hardware.gpio.device;

import java.util.Arrays;

import io.smartspaces.hardware.bits.BitOrder;
import io.smartspaces.hardware.bits.ByteOperations;
import io.smartspaces.hardware.gpio.Spi;

/**
 * An SPI-based PN532 breakout board implementation.
 * 
 * <p>
 * A software SPI connection is recommended as the hardware SPI on the Raspberry
 * Pi has some issues with the LSB first mode used by the PN532 (see:
 * http://www.raspberrypi.org/forums/viewtopic.php?f=32&t=98070&p=720659#
 * p720659)
 * 
 * <p>
 * This Java version was transliterated from the Python version mentioned in the
 * license.
 * 
 * @author keith M. Hughes
 */
public class SpiPn532Device implements Pn532Device {
	public static final byte PN532_PREAMBLE = (byte) 0x00;
	public static final byte PN532_STARTCODE1 = (byte) 0x00;
	public static final byte PN532_STARTCODE2 = (byte) 0xFF;
	public static final byte PN532_POSTAMBLE = (byte) 0x00;

	public static final byte PN532_HOSTTOPN532 = (byte) 0xD4;
	public static final byte PN532_PN532TOHOST = (byte) 0xD5;

	// PN532 Commands
	public static final byte PN532_COMMAND_DIAGNOSE = (byte) 0x00;
	public static final byte PN532_COMMAND_GETFIRMWAREVERSION = (byte) 0x02;
	public static final byte PN532_COMMAND_GETGENERALSTATUS = (byte) 0x04;
	public static final byte PN532_COMMAND_READREGISTER = (byte) 0x06;
	public static final byte PN532_COMMAND_WRITEREGISTER = (byte) 0x08;
	public static final byte PN532_COMMAND_READGPIO = (byte) 0x0C;
	public static final byte PN532_COMMAND_WRITEGPIO = (byte) 0x0E;
	public static final byte PN532_COMMAND_SETSERIALBAUDRATE = (byte) 0x10;
	public static final byte PN532_COMMAND_SETPARAMETERS = (byte) 0x12;
	public static final byte PN532_COMMAND_SAMCONFIGURATION = (byte) 0x14;
	public static final byte PN532_COMMAND_POWERDOWN = (byte) 0x16;
	public static final byte PN532_COMMAND_RFCONFIGURATION = (byte) 0x32;
	public static final byte PN532_COMMAND_RFREGULATIONTEST = (byte) 0x58;
	public static final byte PN532_COMMAND_INJUMPFORDEP = (byte) 0x56;
	public static final byte PN532_COMMAND_INJUMPFORPSL = (byte) 0x46;
	public static final byte PN532_COMMAND_INLISTPASSIVETARGET = (byte) 0x4A;
	public static final byte PN532_COMMAND_INATR = (byte) 0x50;
	public static final byte PN532_COMMAND_INPSL = (byte) 0x4E;
	public static final byte PN532_COMMAND_INDATAEXCHANGE = (byte) 0x40;
	public static final byte PN532_COMMAND_INCOMMUNICATETHRU = (byte) 0x42;
	public static final byte PN532_COMMAND_INDESELECT = (byte) 0x44;
	public static final byte PN532_COMMAND_INRELEASE = (byte) 0x52;
	public static final byte PN532_COMMAND_INSELECT = (byte) 0x54;
	public static final byte PN532_COMMAND_INAUTOPOLL = (byte) 0x60;
	public static final byte PN532_COMMAND_TGINITASTARGET = (byte) 0x8C;
	public static final byte PN532_COMMAND_TGSETGENERALBYTES = (byte) 0x92;
	public static final byte PN532_COMMAND_TGGETDATA = (byte) 0x86;
	public static final byte PN532_COMMAND_TGSETDATA = (byte) 0x8E;
	public static final byte PN532_COMMAND_TGSETMETADATA = (byte) 0x94;
	public static final byte PN532_COMMAND_TGGETINITIATORCOMMAND = (byte) 0x88;
	public static final byte PN532_COMMAND_TGRESPONSETOINITIATOR = (byte) 0x90;
	public static final byte PN532_COMMAND_TGGETTARGETSTATUS = (byte) 0x8A;

	public static final byte PN532_RESPONSE_INDATAEXCHANGE = (byte) 0x41;
	public static final byte PN532_RESPONSE_INLISTPASSIVETARGET = (byte) 0x4B;

	public static final byte PN532_WAKEUP = (byte) 0x55;

	public static final byte PN532_SPI_STATREAD = (byte) 0x02;
	public static final byte PN532_SPI_DATAWRITE = (byte) 0x01;
	public static final byte PN532_SPI_DATAREAD = (byte) 0x03;
	public static final byte PN532_SPI_READY = (byte) 0x01;

	public static final byte PN532_MIFARE_ISO14443A = (byte) 0x00;

	// Mifare Commands
	public static final byte MIFARE_CMD_AUTH_A = (byte) 0x60;
	public static final byte MIFARE_CMD_AUTH_B = (byte) 0x61;
	public static final byte MIFARE_CMD_READ = (byte) 0x30;
	public static final byte MIFARE_CMD_WRITE = (byte) 0xA0;
	public static final byte MIFARE_CMD_TRANSFER = (byte) 0xB0;
	public static final byte MIFARE_CMD_DECREMENT = (byte) 0xC0;
	public static final byte MIFARE_CMD_INCREMENT = (byte) 0xC1;
	public static final byte MIFARE_CMD_STORE = (byte) 0xC2;
	public static final byte MIFARE_ULTRALIGHT_CMD_WRITE = (byte) 0xA2;

	// Prefixes for NDEF Records (to identify record type)
	public static final byte NDEF_URIPREFIX_NONE = (byte) 0x00;
	public static final byte NDEF_URIPREFIX_HTTP_WWWDOT = (byte) 0x01;
	public static final byte NDEF_URIPREFIX_HTTPS_WWWDOT = (byte) 0x02;
	public static final byte NDEF_URIPREFIX_HTTP = (byte) 0x03;
	public static final byte NDEF_URIPREFIX_HTTPS = (byte) 0x04;
	public static final byte NDEF_URIPREFIX_TEL = (byte) 0x05;
	public static final byte NDEF_URIPREFIX_MAILTO = (byte) 0x06;
	public static final byte NDEF_URIPREFIX_FTP_ANONAT = (byte) 0x07;
	public static final byte NDEF_URIPREFIX_FTP_FTPDOT = (byte) 0x08;
	public static final byte NDEF_URIPREFIX_FTPS = (byte) 0x09;
	public static final byte NDEF_URIPREFIX_SFTP = (byte) 0x0A;
	public static final byte NDEF_URIPREFIX_SMB = (byte) 0x0B;
	public static final byte NDEF_URIPREFIX_NFS = (byte) 0x0C;
	public static final byte NDEF_URIPREFIX_FTP = (byte) 0x0D;
	public static final byte NDEF_URIPREFIX_DAV = (byte) 0x0E;
	public static final byte NDEF_URIPREFIX_NEWS = (byte) 0x0F;
	public static final byte NDEF_URIPREFIX_TELNET = (byte) 0x10;
	public static final byte NDEF_URIPREFIX_IMAP = (byte) 0x11;
	public static final byte NDEF_URIPREFIX_RTSP = (byte) 0x12;
	public static final byte NDEF_URIPREFIX_URN = (byte) 0x13;
	public static final byte NDEF_URIPREFIX_POP = (byte) 0x14;
	public static final byte NDEF_URIPREFIX_SIP = (byte) 0x15;
	public static final byte NDEF_URIPREFIX_SIPS = (byte) 0x16;
	public static final byte NDEF_URIPREFIX_TFTP = (byte) 0x17;
	public static final byte NDEF_URIPREFIX_BTSPP = (byte) 0x18;
	public static final byte NDEF_URIPREFIX_BTL2CAP = (byte) 0x19;
	public static final byte NDEF_URIPREFIX_BTGOEP = (byte) 0x1A;
	public static final byte NDEF_URIPREFIX_TCPOBEX = (byte) 0x1B;
	public static final byte NDEF_URIPREFIX_IRDAOBEX = (byte) 0x1C;
	public static final byte NDEF_URIPREFIX_FILE = (byte) 0x1D;
	public static final byte NDEF_URIPREFIX_URN_EPC_ID = (byte) 0x1E;
	public static final byte NDEF_URIPREFIX_URN_EPC_TAG = (byte) 0x1F;
	public static final byte NDEF_URIPREFIX_URN_EPC_PAT = (byte) 0x20;
	public static final byte NDEF_URIPREFIX_URN_EPC_RAW = (byte) 0x21;
	public static final byte NDEF_URIPREFIX_URN_EPC = (byte) 0x22;
	public static final byte NDEF_URIPREFIX_URN_NFC = (byte) 0x23;

	public static final byte PN532_GPIO_VALIDATIONBIT = (byte) 0x80;
	public static final byte PN532_GPIO_P30 = (byte) 0;
	public static final byte PN532_GPIO_P31 = (byte) 1;
	public static final byte PN532_GPIO_P32 = (byte) 2;
	public static final byte PN532_GPIO_P33 = (byte) 3;
	public static final byte PN532_GPIO_P34 = (byte) 4;
	public static final byte PN532_GPIO_P35 = (byte) 5;

	/**
	 * A PN532 acknowledgement packet.
	 */
	public static final byte[] PN532_ACK = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00,
			(byte) 0xFF, (byte) 0x00 };

	/**
	 * A PN532 frame start packet.
	 */
	public static final byte[] PN532_FRAME_START = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xFF };

	/**
	 * The SPI driver for the PN532.
	 */
	private Spi spi;

	/**
	 * Construct the device.
	 * 
	 * @param spi
	 *            the SPI driver for the PN532
	 */
	public SpiPn532Device(Spi spi) {
		this.spi = spi;

		// Set SPI mode and LSB first bit order.
		spi.setMode(0);
		spi.setBitOrder(BitOrder.LSBFIRST);
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#startup()
	 */
	@Override
	public void startup() {
		spi.startup();

		// Assert CS pin low for a second for PN532 to be ready.
		spi.selectChip();
		busyWaitMs(1000);
		// Call GetFirmwareVersion to sync up with the PN532. This might not be
		// required but is done in the Arduino library and kept for consistency.
		getFirmwareVersion();
		spi.deselectChip();
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#shutdown()
	 */
	@Override
	public void shutdown() {
		spi.shutdown();
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#getFirmwareVersion()
	 */
	@Override
	public byte[] getFirmwareVersion() {
		byte[] response = callFunction(PN532_COMMAND_GETFIRMWAREVERSION, 4, null, 1000);
		if (response == null || response.length == 0) {
			throw new RuntimeException(
					"Failed to detect the PN532!  Make sure there is sufficient power (use a 1 amp or greater power supply), the PN532 is wired correctly to the device, and the solder joints on the PN532 headers are solidly connected.");
		}

		return response;
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#useSamConfiguration()
	 */
	@Override
	public void useSamConfiguration() {
		// Send SAM configuration command with configuration for:
		// - 0x01, normal mode
		// - 0x14, timeout 50ms * 20 = 1 second
		// - 0x01, use IRQ pin
		// Note that no other verification is necessary as call_function will
		// check the command was executed as expected.
		callFunction(PN532_COMMAND_SAMCONFIGURATION, 0, new byte[] { 0x01, 0x14, 0x01 }, 1000);
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#read_passive_target()
	 */
	@Override
	public byte[] readPassiveTarget() {
		return readPassiveTarget(PN532_MIFARE_ISO14443A, 1000);
	}

	/* (non-Javadoc)
	 * @see io.smartspaces.hardware.gpio.Pn532Device#readPassiveTarget(byte, long)
	 */
	@Override
	public byte[] readPassiveTarget(byte cardBaud, long timeout) {
		// Send passive read command for 1 card. Expect at most a 7 byte UUID.
		byte[] response = callFunction(PN532_COMMAND_INLISTPASSIVETARGET, 17, new byte[] { 0x01, cardBaud }, timeout);
		// If no response is available return None to indicate no card is
		// present.
		if (response == null) {
			return null;
		}

		// Check only 1 card with up to a 7 byte UID is present.
		if (response[0] != 0x01) {
			throw new RuntimeException("More than one card detected!");
		}
		if (response[5] > 7) {
			throw new RuntimeException("Found card with unexpectedly long UID!");
		}

		// Return UID of card.
		return Arrays.copyOfRange(response, 6, 6 + response[5]);
	}

	/**
	 * Busy wait for the specified number of milliseconds.
	 */
	private void busyWaitMs(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// Don't care.

		}
	}

	/**
	 * Write a frame to the PN532 with the specified data.
	 * 
	 * @param data
	 *            the data to write
	 */
	private void writeFrame(byte[] data) {
		// assert data is not None and 0 < len(data) < 255, 'Data must be array
		// of 1 to 255 bytes.'
		// Build frame to send as:
		// - SPI data write (0x01)
		// - Preamble (0x00)
		// - Start code (0x00, 0xFF)
		// - Command length (1 byte)
		// - Command length checksum
		// - Command bytes
		// - Checksum
		// - Postamble (0x00)
		int length = data.length;
		byte[] frame = new byte[length + 8];
		frame[0] = PN532_SPI_DATAWRITE;
		frame[1] = PN532_PREAMBLE;
		frame[2] = PN532_STARTCODE1;
		frame[3] = PN532_STARTCODE2;
		frame[4] = (byte) (length & 0xFF);
		frame[5] = ByteOperations.unsignedAdd((byte) (~length), (byte) 1);

		System.arraycopy(data, 0, frame, 6, length);

		byte checksum = ByteOperations.calculateChecksum(data, (byte) 0xff);

		frame[length + 6] = (byte) (~checksum & 0xFF);
		frame[length + 7] = PN532_POSTAMBLE;

		spi.selectChip();
		busyWaitMs(2);
		spi.write(frame);
		spi.deselectChip();
	}

	/**
	 * Read a specified count of bytes from the PN532.
	 * 
	 * @param length
	 *            the number of bytes to read
	 * 
	 * @return the bytes read
	 */
	private byte[] readData(int length) {
		// Build a read request frame.
		byte[] frame = new byte[length];
		frame[0] = PN532_SPI_DATAREAD;
		// Send the frame and return the response, ignoring the SPI header byte.
		spi.selectChip();
		busyWaitMs(2);
		byte[] response = spi.transfer(frame, false, false);
		spi.deselectChip();

		return response;
	}

	/**
	 * Read a response frame from the PN532 of at most length bytes in size.
	 * Returns the data inside the frame if found, otherwise raises an exception
	 * if there is an error parsing the frame. Note that less than length bytes
	 * might be returned!
	 * 
	 * @param length
	 *            the expected maximum number of bytes to be returned
	 * 
	 * @return the response, have have fewer bytes than the number requested
	 */
	private byte[] readFrame(int length) {
		// Read frame with expected length of data.
		byte[] response = readData(length + 8);

		// Check frame starts with 0x01 and then has 0x00FF (preceeded by
		// optional zeros).
		if (response[0] != 0x01) {
			throw new RuntimeException("Response frame does not start with 0x01!");
		}
		// Swallow all the 0x00 values that preceed 0xFF.
		int offset = 1;
		while (response[offset] == 0x00) {
			offset += 1;
			if (offset >= response.length) {
				throw new RuntimeException("Response frame preamble does not contain 0x00FF!");
			}
		}

		if (response[offset] != (byte) 0xFF) {
			throw new RuntimeException("Response frame preamble does not contain 0x00FF!");
		}

		offset += 1;
		if (offset >= response.length) {
			throw new RuntimeException("Response contains no data!");
		}

		// Check length & length checksum match.
		int frame_len = response[offset];
		if (((frame_len + response[offset + 1]) & 0xFF) != 0) {
			throw new RuntimeException("Response length checksum did not match length!");
		}

		// Check frame checksum value matches bytes.
		byte checksum = ByteOperations.calculateChecksum(response, offset + 2, frame_len + 1, (byte) 0);
		if (checksum != 0) {
			throw new RuntimeException("Response checksum did not match expected value!");
		}

		// Return frame data.
		byte[] result = Arrays.copyOfRange(response, offset + 2, offset + 2 + frame_len);
		return result;
	}

	/**
	 * Wait until the PN532 is ready to receive commands. if the wait is longer
	 * than the specified timeout, then the PN532 is not ready.
	 * 
	 * @param timeout
	 *            the timeout, in milliseconds
	 * 
	 * @return {@code true} if thePN532 is ready to receive commands
	 */
	private boolean waitReady(long timeout) {
		byte[] statReadPacket = new byte[] { PN532_SPI_STATREAD, 0x00 };
		long start = System.currentTimeMillis();
		// Send a SPI status read command and read response.
		spi.selectChip();
		busyWaitMs(2);
		byte[] response = spi.transfer(statReadPacket, false, false);
		spi.deselectChip();

		// Loop until a ready response is received.
		while (response[1] != PN532_SPI_READY) {
			// Check if the timeout has been exceeded.
			if (System.currentTimeMillis() - start >= timeout) {
				return false;
			}

			// Wait a little while and try reading the status again.
			busyWaitMs(10);

			spi.selectChip();
			busyWaitMs(2);
			response = spi.transfer(statReadPacket, false, false);
			spi.deselectChip();
		}

		return true;
	};

	/**
	 * Send specified command to the PN532.
	 * 
	 * @param command
	 *            the command to send
	 * @param responseLength
	 *            the number of bytes expected in a response, though the amount
	 *            returned may be less
	 * @param params
	 *            parameters for the command, can be {@code null}
	 * @param timeout
	 *            the timeout to wait for a response, in milliseconds
	 * 
	 * @return the response, or {@code null} if no response came within the
	 *         timeout
	 */
	private byte[] callFunction(byte command, int responseLength, byte[] params, long timeout) {
		// Build frame data with command and parameters.
		byte[] data = new byte[2 + ((params != null) ? params.length : 0)];
		data[0] = PN532_HOSTTOPN532;
		data[1] = (byte) (command & 0xFF);

		if (params != null) {
			System.arraycopy(params, 0, data, 2, params.length);
		}

		// Send frame and wait for response.
		writeFrame(data);
		if (!waitReady(timeout)) {
			return null;
		}

		// Verify ACK response and wait to be ready for function response.
		byte[] response = readData(PN532_ACK.length);
		if (!Arrays.equals(response, PN532_ACK)) {
			throw new RuntimeException("Did not receive expected ACK from PN532!");
		}

		if (!waitReady(timeout)) {
			return null;
		}

		// Read response bytes.
		response = readFrame(responseLength + 2);

		// Check that response is for the called function.
		if ((response[0] != PN532_PN532TOHOST) || (response[1] != (command + 1))) {
			throw new RuntimeException("Received unexpected command response!");
		}

		// Return response data.
		byte[] result = Arrays.copyOfRange(response, 2, response.length);
		return result;
	}
}
