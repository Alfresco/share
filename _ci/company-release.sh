#!/usr/bin/env bash

echo "=========================== Starting Company Release ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set +e -v -x
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

source _ci/init_tag.sh

TAG=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

SOURCE_IMAGE=quay.io/alfresco/alfresco-share
TARGET_IMAGE=alfresco/alfresco-share
docker pull $SOURCE_IMAGE:$TAG_NAME
docker tag $SOURCE_IMAGE:$TAG_NAME $TARGET_IMAGE:$TAG_NAME
echo "${DOCKERHUB_PASSWORD}" | docker login -u="alfrescosystem" --password-stdin hub.docker.com
docker push $TARGET_IMAGE:$TAG_NAME


popd
set +vex
echo "=========================== Finishing Company ReleaseShare =========================="

exit ${SUCCESS}
