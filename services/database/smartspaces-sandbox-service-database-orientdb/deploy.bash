#!/bin/bash

mvn deploy:deploy-file -DgroupId=io.smartspaces \
  -DartifactId=smartspaces.sandbox.service.database.document.orientdb \
  -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar \
  -Dfile=build/smartspaces.sandbox.service.database.document.orientdb-1.0.0.jar \
  -DrepositoryId=smartspaces-io-snapshot \
  -Durl=https://eng.inhabitech.com:8084/repository/smartspaces-io-snapshot

mvn install:install-file -DgroupId=io.smartspaces \
  -DartifactId=smartspaces.sandbox.service.database.document.orientdb \
  -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar \
  -Dfile=build/smartspaces.sandbox.service.database.document.orientdb-1.0.0.jar

