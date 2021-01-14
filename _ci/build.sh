#!/usr/bin/env bash

echo "=========================== Starting Build&Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

source _ci/init_tag.sh

# Change tag image name with the value of TAG_NAME
sed  -i "s/<image.tag>latest/<image.tag>$TAG_NAME/" packaging/docker/pom.xml

#build share
mvn -B -q install \
    -DskipTests \
    -Dmaven.javadoc.skip=true


#build image
cd ${TRAVIS_BUILD_DIR}/packaging/docker
mvn install -Plocal
mvn fabric8:push

if [ $TRAVIS_BRANCH != "master" ]; then
  #build enterprise repo with share services image
  cd ${TRAVIS_BUILD_DIR}/packaging/enterprise
  mvn install -Plocal
  mvn fabric8:push
fi

popd
set +vex
echo "=========================== Finishing Build&Test Script =========================="
