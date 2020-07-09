#!/usr/bin/env bash

SOURCE_IMAGE=quay.io/alfresco/alfresco-share
TARGET_IMAGE=alfresco/alfresco-share
TAG=$(mvn help:evaluate -Dexpression=project.version | grep -v '\[')
docker pull $SOURCE_IMAGE:$TAG
docker tag $SOURCE_IMAGE:$TAG $TARGET_IMAGE:$TAG
docker push $TARGET_IMAGE:$TAG
