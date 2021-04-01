#!/usr/bin/env bash

echo "=========================== Starting Project Alfresco Tas Share ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Alfresco/alfresco-tas-share-test.git
cd alfresco-tas-share-test
git checkout master

if [[ "$TRAVIS_BRANCH" = "develop" ]]; then
  NAMESPACE="develop-share"
else
  NAMESPACE="travis-share-$TRAVIS_BUILD_NUMBER"
fi

export HOST="${NAMESPACE}.${HOSTED_ZONE}"

export XML_SUITE=$1
mvn install \
               -DsuiteXmlFile="src/test/resources/test-suites/${XML_SUITE}" \
               -Djmx.useJolokiaAgent=true \
               -DexcludeGroups='google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests' \
               -DrunBugs=false \
               -Dalfresco.scheme=https \
               -Dalfresco.server=$HOST \
               -Dalfresco.port=443 \
               -Dalfresco.url="https://$HOST/alfresco" \
               -Dshare.port=443 \
               -Dshare.url="https://$HOST/share" \
               -Dadmin.user=admin \
               -Dadmin.password=$ALF_PASSWORD \
               -Dbrowser.headless=true \
               -Dwebdriver.grid.url='http://127.0.0.1:4444/wd/hub' \
               -Dbrowser.name=Firefox \
               -Dbrowser.version=44.0 \
               -Ddisplay.xport=99.0 \
               -Daims.enabled=false & # send the long living command to background!

minutes=0
limit=30
while kill -0 $! >/dev/null 2>&1; do
  echo -n -e " \b" # never leave evidences!

  if [ $minutes == $limit ]; then
    break;
  fi

  minutes=$((minutes+1))

  sleep 60
done

# wait for the exit code of the background process
wait $!
SUCCESS=$?

popd
set +vex
echo "=========================== Finishing Project Alfresco Tas Share =========================="

exit ${SUCCESS}


