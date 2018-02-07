/*
 * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.sandbox.service.database.document.orientdb.internal

import io.smartspaces.resource.managed.BaseManagedResource
import io.smartspaces.resource.managed.IdempotentManagedResource
import io.smartspaces.sandbox.service.database.document.orientdb.OrientDbDocumentDatabaseEndpoint
import io.smartspaces.sandbox.service.database.document.orientdb.OrientDbDocumentDatabaseService
import io.smartspaces.service.BaseSupportedService

import org.apache.commons.logging.Log

import com.orientechnologies.orient.core.Orient

import java.io.File
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener

object StandardOrientDbDocumentDatabaseService {

  /**
   * Protocol used by the server.
   */
  val PROTOCOL = "binary"

  /**
   * Name of the class that implements the protocol used by the server.
   */
  val PROTOCOL_IMPLEMENTATION =
    "com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary"

  /**
   * IP address to which server binds.
   */
  val IP_ADDRESS = "0.0.0.0"

  /**
   * Range of ports to which the server tries to bind.
   */
  val PORT_RANGE = "2424-2500"

  /**
   * Username of a server user.
   */
  val SERVER_USER_LOGIN = "root"

  /**
   * Password of the server user.
   */
  val SERVER_USER_PASSWORD = "ThisIsA_TEST"

  /**
   * Resources accessible to the server user.
   */
  val SERVER_USER_RESOURCES = ""

  /**
   * Configuration property for caching static contents. If enabled the files
   * will be kept in memory the first time they are loaded. Changes to the files
   * will be taken on the next restart.
   */
  val CONFIGURATION_CACHE_STATIC_RESOURCES = "server.cache.staticResources"

  /**
   * Configuration property for the logging level of the logger that outputs to
   * the console.
   */
  val CONFIGURATION_LOG_CONSOLE_LEVEL = "log.console.level"

  /**
   * Configuration property for the logging level of the logger that outputs to
   * a file.
   */
  val CONFIGURATION_LOG_FILE_LEVEL = "log.file.level"

  /**
   * Configuration property for attaching dynamic plugins to the server.
   */
  val CONFIGURATION_PLUGIN_DYNAMIC = "plugin.dynamic"

  /**
   * Username of the database user.
   */
  val DEFAULT_LOGIN = "root"

  /**
   * Password of the database user.
   */
  val DEFAULT_PASSWORD = "admin"

  /**
   * Protocol string to use for local databases.
   */
  val DATABASE_URL_PROTOCOL_PLOCAL = "plocal:"

}

/**
 * The standard implementation of the orientDB Database Service.
 *
 * @author Oleksandr Kelepko
 * @author Keith M. Hughes
 */
class StandardOrientDbDocumentDatabaseService extends BaseSupportedService
  with OrientDbDocumentDatabaseService with IdempotentManagedResource {

  override def getName(): String = {
    return OrientDbDocumentDatabaseService.SERVICE_NAME
  }

  override def onStartup(): Unit = {
    // Make sure when SmartSpaces shuts down that orientDB is shut down.
    // Other code may be using OrientDB so we want this at container shutdown.
    getSpaceEnvironment().getContainerManagedScope().managedResources.addResource(new BaseManagedResource() {
      override def shutdown(): Unit = {
        new Thread() {
          override def run(): Unit = {
            Orient.instance().shutdown()
          }
        }.start()
      }
    })
  }

  override def getOrientDbDocumentDatabaseEndpoint(databaseName: String, log: Log): OrientDbDocumentDatabaseEndpoint = {
    val databaseUrl = StandardOrientDbDocumentDatabaseService.DATABASE_URL_PROTOCOL_PLOCAL +
      getSpaceEnvironment().getFilesystem().getDataDirectory(getName()).getAbsolutePath() +
      File.separator + databaseName
    return getOrientDbDocumentDatabaseEndpoint(
      databaseUrl,
      StandardOrientDbDocumentDatabaseService.DEFAULT_LOGIN, StandardOrientDbDocumentDatabaseService.DEFAULT_PASSWORD, log)
  }

  override def getOrientDbDocumentDatabaseEndpoint(
    dbDirectory: File,
    log: Log): OrientDbDocumentDatabaseEndpoint = {
    val databaseUrl = StandardOrientDbDocumentDatabaseService.DATABASE_URL_PROTOCOL_PLOCAL + dbDirectory.getAbsolutePath()
    return getOrientDbDocumentDatabaseEndpoint(
      databaseUrl,
      StandardOrientDbDocumentDatabaseService.DEFAULT_LOGIN, StandardOrientDbDocumentDatabaseService.DEFAULT_PASSWORD, log)
  }

  override def getOrientDbDocumentDatabaseEndpoint(
    databaseUrl: String,
    login: String, password: String, log: Log): OrientDbDocumentDatabaseEndpoint = {
    return new StandardOrientDbDocumentDatabaseEndpoint(this, databaseUrl, login, password, log)
  }
}
