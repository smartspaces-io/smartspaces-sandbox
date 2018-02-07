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
import java.io.File
import com.orientechnologies.orient.core.command.OCommandOutputListener
import java.io.OutputStream
import java.util.concurrent.Callable
import io.smartspaces.util.io.FileSupportImpl
import java.io.FileOutputStream

object StandardOrientDbDocumentDatabaseEndpoint {

  /**
   * The role the admin will have for the OrientDB database.
   */
  val ORIENTDB_ADMIN_ROLE = "admin"
}

/**
 * Endpoint for an OrientDB document database.
 *
 * @author Oleksandr Kelepko
 * @author Keith M. Hughes
 */
class StandardOrientDbDocumentDatabaseEndpoint(
  service: StandardOrientDbDocumentDatabaseService,
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

    initializor.foreach((i) => {
      doVoidOperation(i.onDatabaseInit)
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

  override def backup(outputLocation: File): Unit = {
    var out: FileOutputStream = null
    try {
      val connection = createConnection
      try {
        connection.backup(out,  null,
          new Callable[Object]() {
            override def call(): Object = {
              null
            }
          },
          new OCommandOutputListener() {
            override def onMessage(text: String): Unit = {
              log.info(s"Database backup update: ${text}")
            }
          },
          1, 4096)
      } finally {
        connection.close
      }
    } catch {
      case e: Throwable => log.error(s"Database backup to ${outputLocation} failed", e)
    } finally {
      if (out != null) {
        out.flush
        out.close
      }
    }
  }

  /**
   * Check to see if the database exists. If it doesn't, create it.
   */
  private def checkDataBaseExists(): Unit = {
    log.info(s"Creating OrientDB database connection to ${databaseUrl}")
    val db = new ODatabaseDocumentTx(databaseUrl)

    try {
      // Can only test existence of databases if they are not remote
      if (!db.getStorage.isRemote() && !db.exists()) {
        db.create()

        val sm = db.getMetadata().getSecurity()
        val user = sm.createUser(username, password, 
            StandardOrientDbDocumentDatabaseEndpoint.ORIENTDB_ADMIN_ROLE)
      } else {
        db.open(username, password)
      }

      creator.foreach((c) => {
        val schema = db.getMetadata().getSchema()

        c.onDatabaseCreate(db, schema)
      })
    } finally {
      db.close
    }
  }
}
