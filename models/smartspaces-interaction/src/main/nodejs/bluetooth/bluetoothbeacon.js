
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

function AveragingWindowFilter(_maxSamples) {
  this.maxSamples = _maxSamples;
  this.sampleCurrent = 0;
  this.samples = [];
  this.samplesSum = this.maxSamples - 1;
  this.samplesWrapped = false;
  this.numberSamples = 0;
}

AveragingWindowFilter.prototype = {
  'addSample': function(value) {
      this.samples[this.sampleCurrent] = value;
      this.samplesSum += value
      if (this.samplesWrapped) {
        var index = (this.sampleCurrent + this.maxSamples - 1) % this.maxSamples;
        this.samplesSum -= this.samples[index];
      } else {
        this.numberSamples++;
      }
      this.sampleCurrent++;

      if (this.sampleCurrent >= this.maxSamples) {
        this.sampleCurrent = 0;
	this.samplesWrapped = true;
      }

      console.log(this.numberSamples);

      return this.samplesSum / this.numberSamples;
  }
};

function FilterCollection(filterFactory) {
  this.filterFactory = filterFactory;

  this.filters = {};
}

FilterCollection.prototype = {
  'getFilter': function(id) {
    var filter = this.filters[id];
    
    if (!filter) {
      filter = this.filterFactory();

      this.filters[id] = filter;
    }

    return filter;
  },
};

function SensorServer(mqttClientId, sensorMqttTopic, sensorId) {
  this.mdns = require('mdns');
  this.noble = require('noble');
  this.mqtt = require('mqtt');

  this.mqttClientId = mqttClientId;
  this.sensorMqttTopic = sensorMqttTopic;
  this.sensorId = sensorId;

  this.mqttClient = null;

  this.addressesToTrack = new Set();
  //replace with your hardware address
  this.addressesToTrack.add('fc0d12fe7e5c'); 

  // For now the sensor filters will be merely an averaging window.
  this.sensorFilters = new FilterCollection(function() { return new AveragingWindowFilter(10); });
}

SensorServer.prototype = {
  'startSensing': function() {
    console.log("Starting sensors");
    this.startNoble();
  },

  'sendMessage': function(message) {
    var strMessage = JSON.stringify(message);
    console.log(JSON.stringify(message));    
    if (this.mqttClient) {
      var buf = new Buffer(strMessage, 'utf8');
      this.mqttClient.publish(this.sensorMqttTopic, buf);
    }
  },

  'processBle': function(peripheral) {
    if(this.addressesToTrack.has(peripheral.uuid)){
      var id = peripheral.uuid;
      var rssi = peripheral.rssi;
      
      var filter = this.sensorFilters.getFilter(id);

      var value = filter.addSample(rssi);

      var message = {
        sensor: this.sensorId,
        data: {
          proximity: {
            id: peripheral.uuid, 
            rssi: value,
            type: 'proximity.ble'
          }
        }
      };

      this.sendMessage(message);
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
      'clientId': this.mqttClientId,
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

var server = new SensorServer("/sensornode/proximity2", "/home/sensor", "/home/livingroom/proximity");
server.start();

