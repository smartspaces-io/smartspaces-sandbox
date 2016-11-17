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

package io.smartspaces.hardware.gpio;

import java.util.Arrays;

import io.smartspaces.hardware.bits.BitOrder;
import io.smartspaces.hardware.bits.ByteOperations;

/**
 * PN532 breakout board representation. Requires a SPI connection to the
 * breakout board. A software SPI connection is recommended as the hardware SPI
 * on the Raspberry Pi has some issues with the LSB first mode used by the PN532
 * (see: http://www.raspberrypi.org/forums/viewtopic.php?f=32&t=98070&p=720659#
 * p720659)
 */
public class StandardPn532 {
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

	public static final byte[] PN532_ACK = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00,
			(byte) 0xFF, (byte) 0x00 };
	public static final byte[] PN532_FRAME_START = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xFF };

	/**
	 * The SPI driver for the PN532.
	 */
	private Spi spi;

	/**
	 * Create an instance of the PN532 class using either software SPI (if the
	 * sclk, mosi, and miso pins are specified) or hardware SPI if a spi
	 * parameter is passed. The cs pin must be a digital GPIO pin. Optionally
	 * specify a GPIO controller to override the default that uses the board's
	 * GPIO pins.
	 * 
	 * @param spi
	 *            the SPI driver for the PN532
	 */
	public StandardPn532(Spi spi) {
		this.spi = spi;

		// Set SPI mode and LSB first bit order.
		spi.setMode(0);
		spi.setBitOrder(BitOrder.LSBFIRST);
	}

	/**
	 * Busy wait for the specified number of milliseconds.
	 */
	public void _busy_wait_ms(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// Don't care.

		}
	}

	/**
	 * Write a frame to the PN532 with the specified data bytearray.
	 */
	public void _write_frame(byte[] data) {
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
		_busy_wait_ms(2);
		spi.write(frame);
		spi.deselectChip();
	}

	/**
	 * 
	 * Read a specified count of bytes from the PN532.
	 */
	public byte[] _read_data(int count) {
		// Build a read request frame.
		byte[] frame = new byte[count];
		frame[0] = PN532_SPI_DATAREAD;
		// Send the frame and return the response, ignoring the SPI header byte.
		spi.selectChip();
		_busy_wait_ms(2);
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
	 */
	public byte[] _read_frame(int length) {
		// Read frame with expected length of data.
		byte[] response = _read_data(length + 8);
		// logger.debug('Read frame: 0x{0}'.format(binascii.hexlify(response)))
		// Check frame starts with 0x01 and then has 0x00FF (preceeded by
		// optional
		// zeros).
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

		if (response[offset] != 0xFF) {
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

	public boolean _wait_ready() {
		return _wait_ready(1000);
	}

	/**
	 * Wait until the PN532 is ready to receive commands. At most wait
	 * timeout_sec seconds for the PN532 to be ready. If the PN532 is ready
	 * before the timeout is exceeded then True will be returned, otherwise
	 * False is returned when the timeout is exceeded.
	 * 
	 */
	public boolean _wait_ready(long timeout) {
		byte[] statReadPacket = new byte[] { PN532_SPI_STATREAD, 0x00 };
		long start = System.currentTimeMillis();
		// Send a SPI status read command and read response.
		spi.selectChip();
		_busy_wait_ms(2);
		byte[] response = spi.transfer(statReadPacket, false, false);
		spi.deselectChip();
		// Loop until a ready response is received.
		while (response[1] != PN532_SPI_READY) {
			// Check if the timeout has been exceeded.
			if (System.currentTimeMillis() - start >= timeout) {
				return false;
			}

			// Wait a little while and try reading the status again.
			_busy_wait_ms(10);
			spi.selectChip();
			_busy_wait_ms(2);
			response = spi.transfer(statReadPacket, false, false);
			spi.selectChip();
		}

		return true;
	};

	/**
	 * Send specified command to the PN532 and expect up to response_length
	 * bytes back in a response. Note that less than the expected bytes might be
	 * returned! Params can optionally specify an array of bytes to send as
	 * parameters to the function call. Will wait up to timeout_secs seconds for
	 * a response and return a bytearray of response bytes, or None if no
	 * response is available within the timeout.
	 * 
	 * def call_function(self, command, response_length=0, params=[],
	 * timeout_sec=1):
	 */
	public byte[] call_function(byte command, int response_length, byte[] params, long timeout) {
		// Build frame data with command and parameters.
		byte[] data = new byte[2 + ((params != null) ? params.length : 0)];
		data[0] = PN532_HOSTTOPN532;
		data[1] = (byte) (command & 0xFF);

		if (params != null) {
			System.arraycopy(params, 0, data, 2, params.length);
		}

		// Send frame and wait for response.
		_write_frame(data);
		if (!_wait_ready(timeout)) {
			return null;
		}

		// Verify ACK response and wait to be ready for function response.
		byte[] response = _read_data(PN532_ACK.length);
		if (!Arrays.equals(response, PN532_ACK)) {
			throw new RuntimeException("Did not receive expected ACK from PN532!");
		}

		if (!_wait_ready(timeout)) {
			return null;
		}

		// Read response bytes.
		response = _read_frame(response_length + 2);

		// Check that response is for the called function.
		if ((response[0] != PN532_PN532TOHOST) || (response[1] != (command + 1))) {
			throw new RuntimeException("Received unexpected command response!");
		}

		// Return response data.
		byte[] result = Arrays.copyOfRange(response, 2, response.length);
		return result;
	}

	/**
	 * Initialize communication with the PN532. Must be called before any other
	 * calls are made against the PN532.
	 * 
	 */
	public void begin() {
		spi.startup();
		
		// Assert CS pin low for a second for PN532 to be ready.
		spi.selectChip();
		_busy_wait_ms(1000);
		// Call GetFirmwareVersion to sync up with the PN532. This might not be
		// required but is done in the Arduino library and kept for consistency.
		get_firmware_version();
		spi.deselectChip();
	}

	/**
	 * Call PN532 GetFirmwareVersion function and return a tuple with the IC,
	 * Ver, Rev, and Support values.
	 * 
	 */
	public void get_firmware_version() {
		byte[] response = call_function(PN532_COMMAND_GETFIRMWAREVERSION, 4, null, 1000);
		if (response == null || response.length == 0) {
			throw new RuntimeException(
					"Failed to detect the PN532!  Make sure there is sufficient power (use a 1 amp or greater power supply), the PN532 is wired correctly to the device, and the solder joints on the PN532 headers are solidly connected.");
		}

		// return (response[0], response[1], response[2], response[3])
	}

	/**
	 * Configure the PN532 to read MiFare cards.
	 */
	public void SAM_configuration() {
		// Send SAM configuration command with configuration for:
		// - 0x01, normal mode
		// - 0x14, timeout 50ms * 20 = 1 second
		// - 0x01, use IRQ pin
		// Note that no other verification is necessary as call_function will
		// check the command was executed as expected.
		call_function(PN532_COMMAND_SAMCONFIGURATION, 0, new byte[] { 0x01, 0x14, 0x01 }, 1000);
	}

	public byte[] read_passive_target() {
		return read_passive_target(PN532_MIFARE_ISO14443A, 1000);
	}

	/**
	 * Wait for a MiFare card to be available and return its UID when found.
	 * Will wait up to timeout_sec seconds and return None if no card is found,
	 * otherwise a bytearray with the UID of the found card is returned.
	 * 
	 */
	public byte[] read_passive_target(byte card_baud, long timeout) {
		// Send passive read command for 1 card. Expect at most a 7 byte UUID.
		byte[] response = call_function(PN532_COMMAND_INLISTPASSIVETARGET, 17, new byte[] { 0x01, card_baud }, timeout);
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
}
