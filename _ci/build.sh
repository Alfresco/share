#!/usr/bin/env bash

echo "=========================== Starting Build&Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

PROJECT_VERSION = echo '${project.version}' | mvn -N -q -DforceStdout help:evaluate

echo "The branch is ${TRAVIS_BRANCH}"
echo "Project version: ${PROJECT_VERSION}"

if [ "${TRAVIS_BRANCH}" = "master" ]; then
  TAG_NAME="latest"
else
  # substitude all '/' to '-' as Docker doesn't allow it
  TAG_NAME=`echo ${TRAVIS_BRANCH} | tr / - `
  TAG_NAME=${TAG_NAME}-${PROJECT_VERSION}
fi

echo "Saving tag name as ${TAG_NAME}"

# Change tag if you are on a branch
if [ ! -z "$TRAVIS_BRANCH" -a "$TRAVIS_BRANCH" != "master" ]; then
  sed  -i "s/<image.tag>latest/<image.tag>$TAG_NAME/" packaging/docker/pom.xml
fi

cd packaging/docker

mvn install -Plocal 
mvn fabric8:push

popd
set +vex
echo "=========================== Finishing Build&Test Script =========================="
