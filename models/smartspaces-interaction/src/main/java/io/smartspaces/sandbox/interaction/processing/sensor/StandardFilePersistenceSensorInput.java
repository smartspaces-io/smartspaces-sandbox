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

package io.smartspaces.sandbox.interaction.processing.sensor;

import io.smartspaces.util.SmartSpacesUtilities;
import io.smartspaces.util.data.dynamic.StandardDynamicObjectNavigator;
import io.smartspaces.util.data.json.JsonMapper;
import io.smartspaces.util.data.json.StandardJsonMapper;
import io.smartspaces.util.io.FileSupport;
import io.smartspaces.util.io.FileSupportImpl;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A sensor input that reads the output of
 * {@link StandardFilePersistenceSensorHandler}.
 * 
 * @author Keith M. Hughes
 */
public class StandardFilePersistenceSensorInput implements SensorInput {

  /**
   * The file that will store the sensor data.
   */
  private File inputFile;

  /**
   * The file support for writing data.
   */
  private FileSupport fileSupport = FileSupportImpl.INSTANCE;

  /**
   * The JSON mapper for serializing data.
   */
  private JsonMapper jsonMapper = StandardJsonMapper.INSTANCE;

  /**
   * The sensor processor being run under.
   */
  private SensorProcessor sensorProcessor;

  /**
   * Construct the handler.
   * 
   * @param inputFile
   *          the file where the data should be read from
   */
  public StandardFilePersistenceSensorInput(File inputFile) {
    this.inputFile = inputFile;
  }

  @Override
  public void startup() {
    // Nothing to do.
  }

  @Override
  public void shutdown() {
    // Nothing to do.
  }

  @Override
  public void setSensorProcessor(SensorProcessor sensorProcessor) {
    this.sensorProcessor = sensorProcessor;
  }

  /**
   * Play back the data.
   */
  @SuppressWarnings("unchecked")
  public void play() {
    Map<String, Object> completeData = jsonMapper.parseObject(fileSupport.readFile(inputFile));

    List<Map<String, Object>> samples = (List<Map<String, Object>>) completeData
        .get(StandardFilePersistenceSensorHandler.FIELD_NAME_SAMPLES);

    long lastTimestamp = 0;
    for (Map<String, Object> sample : samples) {
      long newTimestamp =
          (Long) sample.get(StandardFilePersistenceSensorHandler.FIELD_NAME_SAMPLE_TIMESTAMP);

      if (lastTimestamp != 0) {
        SmartSpacesUtilities.delay(newTimestamp - lastTimestamp);
      }
      lastTimestamp = newTimestamp;

      Map<String, Object> sampleData = (Map<String, Object>) sample
          .get(StandardFilePersistenceSensorHandler.FIELD_NAME_SAMPLE_SAMPLE);

      sensorProcessor.processSensorData(newTimestamp,
          new StandardDynamicObjectNavigator(sampleData));
    }
  }
}
