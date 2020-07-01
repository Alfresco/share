#!/usr/bin/env bash

export GROUP=$1

mvn test \
    -DtestManagement.testRun=test \
    -DtestManagement.enabled=false \
    -DsuiteXmlFile=src/test/resources/share-po-runner-suite.xml \
    -DincludeGroups=${GROUP} \
    -DexcludeGroups="google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests" \
    -DrunBugs=false \
    -Dalfresco.server=localhost \
    -Dalfresco.port=8080 \
    -Dshare.port=8081 \
    -Djmx.useJolokiaAgent=true \
    -Dbrowser.name=Firefox \
    -Dalfresco.scheme=http \
    -Dadmin.user=admin \
    -Dadmin.password=admin \
    -Ddisplay.xport=99.0 \
    -Denv.platform=linux