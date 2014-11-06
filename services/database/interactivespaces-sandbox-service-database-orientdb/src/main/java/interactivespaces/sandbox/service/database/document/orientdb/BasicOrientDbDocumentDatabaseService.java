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

import interactivespaces.InteractiveSpacesException;
import interactivespaces.service.BaseSupportedService;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Keith M. Hughes
 */
public class BasicOrientDbDocumentDatabaseService extends BaseSupportedService {

  private static final String SERVICE_NAME = "database.orientdb";

  /**
   * The database starter.
   */
  private DatabaseServiceStarter databaseStarter = new DatabaseServiceStarter();

  @Override
  public String getName() {
    return SERVICE_NAME;
  }

  @Override
  public void startup() {
    databaseStarter.startup();


    String content =
        "{" + "\"glossary\": {" + "\"title\": \"example glossary\"," + "\"GlossDiv\": {" + "\"title\": \"S\","
            + "\"GlossList\": {" + "\"GlossEntry\": {" + "\"ID\": \"SGML\"," + "\"SortAs\": \"SGML\","
            + "\"GlossTerm\": \"Standard Generalized Markup Language\"," + "\"Acronym\": \"SGML\","
            + "\"Abbrev\": \"ISO 8879:1986\"," + "\"GlossDef\": {"
            + "\"para\": \"A meta-markup language, used to create markup languages such as DocBook.\","
            + "\"GlossSeeAlso\": [\"GML\", \"XML\"]" + "}," + "\"GlossSee\": \"markup\"" + "}" + "}" + "}" + "}"
            + "}";

    ODatabaseDocumentTx test = new ODatabaseDocumentTx("plocal:/var/tmp/testorientdb").open("admin", "admin");
    for (int i = 0; i < 10; i++) {
      ODocument doc = new ODocument("foryou");
      doc.field("number", i);
      doc.field("bar", new ODocument("inner").field("othernumber", 2 * i).field("finalNumber", i * 12));
      doc.save();
    }
    ODocument doc = new ODocument("foryou");
    doc.fromJSON(content);
    doc.save();
    List<ODocument> result =
        test.query(new OSQLSynchQuery<ODocument>("select * from foryou where glossary.GlossDiv.title = 'S'"));

    System.out.println("No of ODocuments:\t" + result.size());

    for (ODocument od : result) {
      System.out.println(od.toJSON());
      Object field = od.field("glossary.GlossDiv");
      if (field != null)
        System.out.println(field.getClass());
      System.out.println(field);
    }

    test.close();
  }

  @Override
  public void shutdown() {
    databaseStarter.shutdown();
  }

  /**
   * Provide one off startup and shutdown of the database.
   *
   * @author Keith M. Hughes
   */
  private static class DatabaseServiceStarter {

    /**
     * {@code true} if the database has been started.
     */
    private AtomicBoolean started = new AtomicBoolean(false);

    /**
     * The database server.
     */
    private OServer orientDbServer;

    public void startup() {
      if (started.compareAndSet(false, true)) {
        try {
          String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                  + "<orient-server>"
                  + "<network>"
                  + "<protocols>"
                  + "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
                  + "</protocols>"
                  + "<listeners>"
                  + "<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>"
                  + "</listeners>"
                  + "</network>"
                  + "<users>"
                  + "<user name=\"root\" password=\"ThisIsA_TEST\" resources=\"\"/>"
                  + "</users>"
                  + "<properties>"
                  + "<entry name=\"orientdb.www.path\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/www/\"/>"
                  + "<entry name=\"orientdb.config.file\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/config/orientdb-server-config.xml\"/>"
                  + "<entry name=\"server.database.path\" value=\"/var/tmp/testorientdb/databases\"/>"
                  + "<entry name=\"server.cache.staticResources\" value=\"false\"/>"
                  + "<entry name=\"log.console.level\" value=\"info\"/>"
                  + "<entry name=\"log.file.level\" value=\"fine\"/>"
                  // The following is required to eliminate an error or warning
                  // "Error on resolving property: ORIENTDB_HOME"
                  + "<entry name=\"plugin.dynamic\" value=\"false\"/>" + "</properties>" + "</orient-server>";

          orientDbServer = OServerMain.create();
          orientDbServer.startup(config);
          orientDbServer.activate();
        } catch (Exception e) {
          throw new InteractiveSpacesException("Could not start up the orientDB server", e);
        }
      }
    }

    public void shutdown() {
      if (started.compareAndSet(true, false)) {
        orientDbServer.shutdown();
      }
    }
  }
}