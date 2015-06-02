Installing OrientDB
===================


Master
------

Rename `orientdb-core-2.0.10.jar` into `com.orientechnologies.orientdb-core-2.0.10.jar`
and copy to
`master/master/repository/interactivespaces/resources/bundles/`

Controller
----------

### Jars

Copy the following jars to `controller/bootstrap/`:
* concurrentlinkedhashmap-lru-1.4.1.jar
* orientdb-client-2.0.10.jar
* orientdb-enterprise-2.0.10.jar
* orientdb-server-2.0.10.jar
* snappy-java-1.1.0.1.jar

### Ext file
Create a file named `orientdb.ext` in `controller/config/environment/` with the following content:

 `package:sun.misc`