#!/usr/bin/env bash

export GROUP=$1

mvn test \
        -DsuiteXmlFile='src/test/resources/share-po-runner-suite.xml' \
        -Dskip.automationtests=false \
        -Dalfresco.port=8080 \
        -Dalfresco.restApi.basicAuthScheme=true \
        -Djmx.useJolokiaAgent=true \
        -DincludeGroups=${GROUP} \
        -DexcludeGroups='google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests' \
        -DrunBugs=false \
        -Dalfresco.server='localhost' \
        -Dalfresco.host='localhost' \
        -Dalfresco.port=8080 \
        -Dalfresco.url='http://localhost:8080/alfresco' \
        -Dshare.host='localhost' \
        -Dshare.port=8181 \
        -Dshare.url='http://localhost:8181/share' \
        -Dalfresco.scheme='http' \
        -Dadmin.user='admin' \
        -Dadmin.password='admin' \
        -Dwebdriver.grid.url='http://127.0.0.1:4444/wd/hub' \
        -Dwebdriver.local.grid=false \
        -Dwebdriver.localGrid=false \
        -Dwebdriver.element.wait.time=30000 \
        -Dwebdriver.page.render.wait.time=60000 \ 
        -Dbrowser.name='Firefox' \
        -DtestManagement.enabled=false \
        -DtestManagement.testRun='test' \
        -Ddisplay.xport=99.0 \
        -Denv.platform='linux'
               