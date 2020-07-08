#!/usr/bin/env bash

echo "=========================== Starting Project Alfresco Tas Share ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"


git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Alfresco/alfresco-tas-share-test.git
cd alfresco-tas-share-test
git checkout 6.2.N

export GROUP=$1
mvn clean install \
               -DsuiteXmlFile='src/test/resources/share-po-runner-suite.xml' \
               -Dalfresco.port=8080 \
               -Dalfresco.restApi.basicAuthScheme=true \
               -Djmx.useJolokiaAgent=true \
               -DincludeGroups=${GROUP} \
               -DexcludeGroups='google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests' \
               -DrunBugs=false \
               -Dalfresco.server=localhost \
               -Dalfresco.host=localhost \
               -Dalfresco.port=8080 \
               -Dalfresco.url='http://localhost:8080/alfresco' \
               -Dshare.host=localhost \
               -Dshare.port=8181 \
               -Dshare.url='http://localhost:8181/share' \
               -Dalfresco.scheme=http \
               -Dadmin.user=admin \
               -Dadmin.password=admin \
               -Dwebdriver.grid.url='http://127.0.0.1:4444/wd/hub' \
               -Dwebdriver.local.grid=false \
               -Dwebdriver.localGrid=false \
               -Dwebdriver.element.wait.time=30000 \
               -Dwebdriver.page.render.wait.time=60000 \
               -Dbrowser.name=Firefox \
               -Dbrowser.version=44.0 \
               -Ddisplay.xport=99.0 \
               -Daims.enabled=false \
               -Denv.platform=linux
               
popd
set +vex
echo "=========================== Finishing Project Alfresco Tas Share =========================="

exit ${SUCCESS}

