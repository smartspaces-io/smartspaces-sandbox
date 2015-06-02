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

package interactivespaces.sandbox.service.database.document.orientdb;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Lists;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.logging.Log;

import java.util.Collection;

/**
 * Endpoint for an OrientDB document database.
 */
public class BasicOrientDbDocumentDatabaseEndpoint implements OrientDbDocumentDatabaseEndpoint {
  /**
   * Database connections managed by this endpoint.
   */
  private Collection<ODatabase> connections;

  /**
   * Base service.
   */
  private final BasicOrientDbDocumentDatabaseService service;

  /**
   * Logger.
   */
  private final Log log;

  /**
   * Login for the database access.
   */
  private final String login;

  /**
   * Password for the database access.
   */
  private final String password;

  /**
   * Path to the database.
   */
  private final String url;

  /**
   * Constructs a new endpoint.
   *  @param service
   *          service that manages access to OrientDB databases
   * @param databaseUrl
   *          path to the database
   * @param login
   *          login for the database access
   * @param password
   *          password for the database access
   * @param log
   *          logger
   */
  public BasicOrientDbDocumentDatabaseEndpoint(BasicOrientDbDocumentDatabaseService service,
      String databaseUrl, String login, String password, Log log) {
    this.service = service;
    this.url = databaseUrl;
    this.login = login;
    this.password = password;
    this.log = log;
  }

  @Override
  public synchronized ODatabaseDocumentTx createConnection() {
    checkState(connections != null, url);
    log.debug("Creating connection to " + url);
    ODatabaseDocumentTx result = service.createOrOpenDatabase(url, login, password);
    connections.add(result);
    return result;
  }

  @Override
  public synchronized void startup() {
    checkState(connections == null, url);
    connections = Lists.newArrayList();
  }

  @Override
  public synchronized void shutdown() {
    if (connections != null) {
      for (ODatabase connection : connections) {
        try {
          connection.close();
        } catch (Throwable e) {
          log.error("Exception while closing " + connection, e);
        }
      }
      connections = null;
    }
  }
}
