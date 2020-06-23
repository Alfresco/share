#!/usr/bin/env bash

export GROUP=$1

mvn clean install \
               -DsuiteXmlFile=src/test/resources/share-po-runner-suite.xml \
               -Dskip.automationtests=false \
               -Dalfresco.restApi.basicAuthScheme=true \
               -Djmx.useJolokiaAgent=true \
               -DincludeGroups=${GROUP} \
               -DexcludeGroups="google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests" \
               -DrunBugs=false \
               -Dalfresco.server=localhost \
               -Dalfresco.port=8080 \
               -Dshare.host=localhost \
               -Dshare.port=8181 \
               -Dbrowser.name=Firefox \
               -Dbrowser.version=76.0 \
               -DtestManagement.enabled=false \
               -DtestManagement.testRun=test \
               -DtestManagement.apiKey=6z8dAK8oYhO89N36iDtB-Lwmp2k8.aDmFfXLeR90o \
               -DtestManagement.project=1 \
               -DtestManagement.suiteId=1 \
               -Ddisplay.xport=99.0 \
               -Denv.platform=linux
