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
cd packaging/docker
mvn install -Plocal
mvn fabric8:push

#build community repo with share services image
cd ../docker-acs-share-services/community
mvn install -Plocal
mvn fabric8:push

#build enterprise repo with share services image
cd ../enterprise
mvn install -Plocal
mvn fabric8:push

popd
set +vex
echo "=========================== Finishing Build&Test Script =========================="
