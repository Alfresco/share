#!/usr/bin/env bash

echo "=========================== Starting Build&Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

source init_tag.sh

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
