
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

var mdns = require('mdns');
var noble = require('noble');
var mqtt = require('mqtt');

function AveragingWindowFilter() {
  this.MAX_SAMPLES = 10;
  this.sampleCurrent = 0;
  this.samples = [];
  this.samplesSum = this.MAX_SAMPLES - 1;
  this.samplesWrapped = false;
  this.numberSamples = 0;
}

AveragingWindowFilter.prototype = {
  'addSample': function(value) {
      this.samples[this.sampleCurrent] = value;
      this.samplesSum += value
      if (this.samplesWrapped) {
        var index = (this.sampleCurrent + this.MAX_SAMPLES - 1) % this.MAX_SAMPLES;
        this.samplesSum -= this.samples[index];
      } else {
        this.numberSamples++;
      }
      this.sampleCurrent++;

      if (this.sampleCurrent >= this.MAX_SAMPLES) {
        this.sampleCurrent = 0;
	this.samplesWrapped = true;
      }

      console.log(this.numberSamples);

      return this.samplesSum / this.numberSamples;
  }
};

function SensorServer() {
  this.mdns = require('mdns');
  this.noble = require('noble');
  this.mqtt = require('mqtt');

  this.mqttClient = null;

  this.addressesToTrack = new Set();
  //replace with your hardware address
  this.addressesToTrack.add('fc0d12fe7e5c'); 

  this.sensorFilter = new AveragingWindowFilter();
}

SensorServer.prototype = {
  'startSensing': function() {
    console.log("Starting sensors");
    this.startNoble();
  },

  'sendMqttMessage': function(message) {
    var strMessage = JSON.stringify(message);
    console.log(JSON.stringify(message));    
    if (this.mqttClient) {
      var buf = new Buffer(strMessage, 'utf8');
      this.mqttClient.publish("/home/sensor", buf);
    }
  },

  'processBle': function(peripheral) {
    if(this.addressesToTrack.has(peripheral.uuid)){
      var rssi = peripheral.rssi;

      var value = this.sensorFilter.addSample(rssi);

      var message = {
        sensor: "/home/livingroom/proximity", 
        data: {
          proximity: {
            id: peripheral.uuid, 
            rssi: value,
            type: 'proximity.ble'
          }
        }
      };

      this.sendMqttMessage(message);
    }
  },

  'startNoble': function() {
    console.log("Starting noble");

    var self = this;

    this.noble.on('addressChange', function(address) {
      console.log("Address is " + address);
    });

    this.noble.on('discover', function(peripheral){
      self.processBle(peripheral);
    });

    this.noble.on('stateChange', function(state) {
      if (state === 'poweredOn') {
        self.noble.startScanning([], true) //allows dubplicates while scanning
      } else {
        self.noble.stopScanning();
      }
    });

  },

  'calculateDistance' : function(rssi) {
    //hard coded power value. Usually ranges between -59 to -65
    var txPower = -12 
  
    if (rssi == 0) {
      return -1.0; 
    }

    var ratio = rssi*1.0/txPower;
    if (ratio < 1.0) {
      return Math.pow(ratio,10);
    } else {
      var distance =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;    
      return distance;
    }
  } ,

  'connectMqtt': function(host, port) {
    // If already connect, don't connect again.
    if (this.mqttClient) return;

    var self = this;

    var mqttOptions={
      'clientId': "/sensornode/proximity2",
      'host': host,
      'port': port
    };
    this.mqttClient = mqtt.connect(mqttOptions);
    this.mqttClient.on('connect', function() {
      console.log("MQTT connected");
//      self.startSensing();
    });
  },

  'startMdns': function() {
    var self = this;

    // watch all mqtt servers 
    var sequence = [
      this.mdns.rst.DNSServiceResolve(),
      'DNSServiceGetAddrInfo' in this.mdns.dns_sd ?
          this.mdns.rst.DNSServiceGetAddrInfo() : 
          mdns.rst.getaddrinfo({families:[4]}),
      this.mdns.rst.makeAddressesUnique()
    ];

    var browser = mdns.createBrowser(mdns.tcp('mqtt'),
      {resolverSequence: sequence});
    browser.on('serviceUp', function(service) {
      console.log("Got MQTT server: ", service);
      self.connectMqtt(service.host, service.port);
    });
    browser.on('serviceDown', function(service) {
      console.log("service down: ", service);
    });
    browser.start();
  },

  'start': function() {
    this.startMdns();
    this.startSensing();
  },
};

var server = new SensorServer();
server.start();

