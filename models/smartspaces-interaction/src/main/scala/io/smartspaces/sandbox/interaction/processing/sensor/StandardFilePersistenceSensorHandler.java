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

import io.smartspaces.util.data.dynamic.DynamicObject;
import io.smartspaces.util.data.json.JsonMapper;
import io.smartspaces.util.data.json.StandardJsonMapper;
import io.smartspaces.util.io.FileSupport;
import io.smartspaces.util.io.FileSupportImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A standard file sensor handler that persists the data in a file.
 * 
 * <p>
 * The data is stored in a JSON format.
 * 
 * @author Keith M. Hughes
 */
public class StandardFilePersistenceSensorHandler implements SensorHandler {

  /**
   * The field name for all samples. The field is a list.
   */
  public static final String FIELD_NAME_SAMPLES = "samples";

  /**
   * The field name for the timestamp of a sample. The field is a long.
   */
  public static final String FIELD_NAME_SAMPLE_TIMESTAMP = "timestamp";

  /**
   * The field name for the sample data of a sample. The field is a map.
   */
  public static final String FIELD_NAME_SAMPLE_SAMPLE = "sample";

  /**
   * The file that will store the sensor data.
   */
  private File outputFile;

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
   * The data to save.
   */
  private Map<String, Object> dataToSave;

  /**
   * The array that will store the sample objects.
   */
  private List<Map<String, Object>> samples;

  /**
   * Construct the handler.
   * 
   * @param outputFile
   *          the file where the data should be written
   */
  public StandardFilePersistenceSensorHandler(File outputFile) {
    this.outputFile = outputFile;
  }

  @Override
  public void startup() {
    dataToSave = new HashMap<>();

    samples = new ArrayList<>();
    dataToSave.put(FIELD_NAME_SAMPLES, samples);
  }

  @Override
  public void shutdown() {
    saveData();
  }

  @Override
  public void handleSensorData(long timestamp, DynamicObject data) {
    Map<String, Object> sample = new HashMap<>();
    samples.add(sample);

    sample.put(FIELD_NAME_SAMPLE_TIMESTAMP, timestamp);
    sample.put(FIELD_NAME_SAMPLE_SAMPLE, data.asMap());
  }

  @Override
  public void setSensorProcessor(SensorProcessor sensorProcessor) {
    this.sensorProcessor = sensorProcessor;
  }

  @Override
  public SensorProcessor getSensorProcessor() {
    return sensorProcessor;
  }

  /**
   * Save the data in the file.
   */
  private void saveData() {
    String rendered = jsonMapper.toString(dataToSave);

    fileSupport.writeFile(outputFile, rendered);
  }
}
