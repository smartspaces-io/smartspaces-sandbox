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

package io.smartspaces.sandbox.service.database.document.orientdb;

import io.smartspaces.service.SupportedService;

import org.apache.commons.logging.Log;

import java.io.File;

/**
 * A database service for OrientDB.
 * 
 * @author Keith M. Hughes
 */
public interface OrientDbDocumentDatabaseService extends SupportedService {

  /**
   * Creates new endpoint for a database with the given name.
   *
   * @param databaseName
   *          OrientDB database name
   * @param log
   *          logger
   *
   * @return new endpoint for accessing the database
   */
  OrientDbDocumentDatabaseEndpoint getOrientDbDocumentDatabaseEndpoint(String databaseName,
      Log log);

  /**
   * Creates new endpoint for a database with at the given location.
   *
   * @param dbDirectory
   *          OrientDB database storage
   * @param log
   *          logger
   *
   * @return new endpoint for accessing the database
   */
  OrientDbDocumentDatabaseEndpoint getOrientDbDocumentDatabaseEndpoint(File dbDirectory, Log log);

  /**
   * Creates new endpoint for a database with the given URL and credentials.
   *
   * @param databaseUrl
   *          OrientDB database URL
   * @param login
   *          login for the database access
   * @param password
   *          password for the database access
   * @param log
   *          logger
   *
   * @return new endpoint for accessing the database
   */
  OrientDbDocumentDatabaseEndpoint getOrientDbDocumentDatabaseEndpoint(String databaseUrl,
      String login, String password, Log log);
}