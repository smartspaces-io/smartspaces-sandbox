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
import com.orientechnologies.orient.client.remote.OEngineRemote

object StandardOrientDbDocumentDatabaseService {

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

  var _maxPoolSize: Int = 32
  
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
  
  override def maxPoolSize: Int = _maxPoolSize
  
  def maxPoolSize_=(newSize: Int): Unit = {
    _maxPoolSize = newSize
  }

  override def getOrientDbDocumentDatabaseEndpoint(databaseName: String, log: Log): OrientDbDocumentDatabaseEndpoint = {
    val databaseUrl = StandardOrientDbDocumentDatabaseService.DATABASE_URL_PROTOCOL_PLOCAL +
      getSpaceEnvironment().getFilesystem().getDataDirectory(getName()).getAbsolutePath() +
      File.separator + databaseName

    getOrientDbDocumentDatabaseEndpoint(
      databaseUrl,
      StandardOrientDbDocumentDatabaseService.DEFAULT_LOGIN, StandardOrientDbDocumentDatabaseService.DEFAULT_PASSWORD, log)
  }

  override def getOrientDbDocumentDatabaseEndpoint(
    dbDirectory: File,
    log: Log): OrientDbDocumentDatabaseEndpoint = {
    val databaseUrl = StandardOrientDbDocumentDatabaseService.DATABASE_URL_PROTOCOL_PLOCAL + dbDirectory.getAbsolutePath()

    getOrientDbDocumentDatabaseEndpoint(
      databaseUrl,
      StandardOrientDbDocumentDatabaseService.DEFAULT_LOGIN, StandardOrientDbDocumentDatabaseService.DEFAULT_PASSWORD, log)
  }

  override def getOrientDbDocumentDatabaseEndpoint(
    databaseUrl: String,
    login: String, password: String, log: Log): OrientDbDocumentDatabaseEndpoint = {

    new StandardOrientDbDocumentDatabaseEndpoint(this, databaseUrl, login, password, log)
  }
}
