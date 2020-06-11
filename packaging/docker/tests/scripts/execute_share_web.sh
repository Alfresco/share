#!/usr/bin/env bash

export SHARE_SUITE=$1

mvn clean install \
               -DsuiteXmlFile=${SHARE_SUITE} \
               -Dskip.automationtests=false \
               -Dalfresco.port=8080 \
               -Dalfresco.restApi.basicAuthScheme=true \
               -Djmx.useJolokiaAgent=true \
               -DincludeGroups='sanity' \
               -DexcludeGroups='google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests' \
               -DrunBugs=false \
               -Dalfresco.server=localhost \
               -Dalfresco.host=localhost \
               -Dalfresco.port=8080 \
               -Dalfresco.url='http://localhost:8080/alfresco' \
               -Dshare.host=localhost \
               -Dshare.port=8181 \
               -Dshare.url='http://localhost:8181/share'
               -Dalfresco.scheme=http \
               -Dadmin.user=admin \
               -Dadmin.password=admin \
               -Dwebdriver.grid.url='http://127.0.0.1:4444/wd/hub' \
               -Dwebdriver.local.grid=false \
               -Dwebdriver.localGrid=false \
               -Dwebdriver.element.wait.time=20000 \
               -Dwebdriver.page.render.wait.time=60000 \ 
               -Dgrid.url='http://127.0.0.1:4444/wd/hub' \
               -Dbrowser.name=firefox \
               -Dbrowser.version=59 \
               -Denv.platform=linux
    
               