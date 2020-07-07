#!/usr/bin/env bash

echo "=========================== Starting Build&Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# substitude all '/' to '-' as Docker doesn't allow it
TAG_NAME=`echo $TRAVIS_BRANCH | tr / - `

# Change tag if you are on a branch
if [ ! -z "$TRAVIS_BRANCH" -a "$TRAVIS_BRANCH" != "master" ]; then
  sed  -i "s/<image.tag>latest/<image.tag>latest-$TAG_NAME/" packaging/docker/pom.xml
fi

mvn -B -U \
    clean install \
    -Dbuildnumber=${TRAVIS_BUILD_NUMBER} \
    "-Plocal"

cd packaging/docker
mvn fabric8:push

popd
set +vex
echo "=========================== Finishing Build&Test Script =========================="
