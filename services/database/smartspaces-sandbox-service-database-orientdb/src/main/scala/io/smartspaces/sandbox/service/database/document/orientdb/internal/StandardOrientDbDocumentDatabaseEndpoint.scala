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

package io.smartspaces.sandbox.service.database.document.orientdb.internal;

import io.smartspaces.resource.managed.IdempotentManagedResource
import io.smartspaces.sandbox.service.database.document.orientdb.OrientDbDocumentDatabaseEndpoint

import org.apache.commons.logging.Log

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool
import io.smartspaces.sandbox.service.database.document.orientdb.OrientDbDatabaseCreator
import io.smartspaces.sandbox.service.database.document.orientdb.OrientDbEndpointInitializer

/**
 * Endpoint for an OrientDB document database.
 *
 * @author Oleksandr Kelepko
 * @author Keith M. Hughes
 */
class StandardOrientDbDocumentDatabaseEndpoint(service: StandardOrientDbDocumentDatabaseService,
    databaseUrl: String, username: String, password: String, log: Log) extends OrientDbDocumentDatabaseEndpoint with IdempotentManagedResource {

  /**
   * The creator for the OrientDB database if the database didn't exist and is now being created.
   */
  override var creator: Option[OrientDbDatabaseCreator] = None
  
  /**
   * The initializer for the database session.
   */
  override var initializor: Option[OrientDbEndpointInitializer] = None

  /**
   * The pool of database connections.
   */
  private var pool: OPartitionedDatabasePool = null

  override def onStartup(): Unit = {
    checkDataBaseExists()

    pool = new OPartitionedDatabasePool(databaseUrl, username, password)
    
    initializor.foreach( (i) => {
      doVoidOperation( i.onDatabaseInit )
    })
  }

  override def onShutdown(): Unit = {
    pool.close()
    pool = null
  }

  override def createConnection(): ODatabaseDocumentTx = {
    return pool.acquire()
  }

  override def doVoidOperation(operation: (ODatabaseDocumentTx) => Unit): Unit = {
    val connection = createConnection
    try {
      operation(connection)
    } catch {
      case e: Throwable => {
        connection.rollback
        
        throw e
      }
    } finally {
      connection.close
    }
  }

  override def doOperation[T](operation: (ODatabaseDocumentTx) => T): T = {
    val connection = createConnection
    try {
      operation(connection)
    } catch {
      case e: Throwable => {
        connection.rollback
        
        throw e
      }
    } finally {
      connection.close
    }
  }

  /**
   * Check to see if the database exists. If it doesn't, create it.
   */
  private def checkDataBaseExists(): Unit = {
    log.debug(s"Creating connection to ${databaseUrl}")
    val db = new ODatabaseDocumentTx(databaseUrl)

    try {
      if (!db.exists()) {
        db.create()

        val sm = db.getMetadata().getSecurity()
        val user = sm.createUser(username, password, "admin")

        if (creator.isDefined) {
          val schema = db.getMetadata().getSchema()

          creator.get.onDatabaseCreate(db, schema)
        }
      }
    } finally {
      db.close
    }
  }
}
