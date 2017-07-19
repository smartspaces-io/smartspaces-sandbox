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

package io.smartspaces.sandbox.service.database.document.orientdb

import io.smartspaces.resource.managed.ManagedResource

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx

/**
 * Endpoint for an OrientDB document database.
 *
 * @author Oleksandr Kelepko
 * @author Keith M. Hughes
 */
trait OrientDbDocumentDatabaseEndpoint extends ManagedResource {
  
  /**
   * The creator for the database.
   * 
   * This is called on startup.
   */
  var creator: Option[OrientDbDatabaseCreator]
  
  /**
   * The initializer for the database.
   * 
   * This is called on startup.
   */
  var initializor: Option[OrientDbEndpointInitializer]

  /**
   * Creates a new connection to the database managed by this endpoint.
   * 
   * [[p]]
   * You must close this connection when through with it.
   *
   * @return OrientDB database connection
   */
  def createConnection(): ODatabaseDocumentTx
  
  /**
   * Perform a void operation.
   * 
   * [[p]]
   * Do not save the database connection. It is closed upon completion of the operation.
   * The operation is rolled back if an exception is thrown.
   *
   * @param operation
   *          the code to run
   */
  def doVoidOperation(operation: (ODatabaseDocumentTx) => Unit): Unit

  /**
   * Perform an operation.
   * 
   * [[p]]
   * Do not save the database connection. It is closed upon completion of the operation.
   * The operation is rolled back if an exception is thrown.
   *
   * @param operation
   *          the code to run
   *
   * @returns the result of the operation
   */
  def doOperation[T](operation: (ODatabaseDocumentTx) => T): T
}
