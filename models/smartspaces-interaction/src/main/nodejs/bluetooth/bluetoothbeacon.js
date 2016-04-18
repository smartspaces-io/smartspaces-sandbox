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

function calculateDistance(rssi) {

	// Set on the beacon device.
	var txPower = -12;

	if (rssi == 0) {
		return -1.0;
	}

	var ratio = rssi * 1.0 / txPower;
	if (ratio < 1.0) {
		return Math.pow(ratio, 10);
	} else {
		var distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
		return distance;
	}
}

var noble = require('noble');
var mqtt = require('mqtt');

var mqttOptions = {
	'clientId' : "/sensornode/proximity2"
};

var mqttClient = mqtt.connect("mqtt://192.168.188.109", mqttOptions);
mqttClient.on('connect', function() {
	console.log("MQTT connected");
});

// The set of bluetooth MAC addresses that are being tracked.
var addressesToTrack = new Set();
addressesToTrack.add('fc0d12fe7e5c');

noble.on('addressChange', function(address) {
	console.log("Address is " + address);
});

noble.on('discover', function(peripheral) {
	if (addressesToTrack.has(peripheral.uuid)) {
		var message = {
			sensor : "/home/livingroom/proximity",
			data : {
				proximity : {
					id : peripheral.uuid,
					rssi : peripheral.rssi,
					type : 'proximity.ble'
				}
			}
		};

		var strMessage = JSON.stringify(message);
		console.log(strMessage);
		if (mqttClient) {
			var buf = new Buffer(strMessage, 'utf8');
			mqttClient.publish("/home/sensor", buf);
		}
	}
});

noble.on('stateChange', function(state) {
	if (state === 'poweredOn') {
		// allows duplicates while scanning
		noble.startScanning([], true);
	} else {
		noble.stopScanning();
	}
});
