#!/usr/bin/env bash

echo "=========================== Starting Init Tag==========================="

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "The branch is ${TRAVIS_BRANCH}"
echo "Project version: ${PROJECT_VERSION}"

if [ "${TRAVIS_BRANCH}" = "master" ]; then
  TAG_NAME="latest"
else
  # substitude all '/' to '-' as Docker doesn't allow it
  TAG_NAME=`echo ${TRAVIS_BRANCH} | tr / - `
  TAG_NAME=${TAG_NAME}-${PROJECT_VERSION}
fi
#in case of release 
if [[ ${TRAVIS_COMMIT_MESSAGE} = *"release"* ]]; then
    echo "Release version: ${RELEASE_VERSION}"
    TAG_NAME=$RELEASE_VERSION
fi


echo "Saving tag name as ${TAG_NAME}"

echo "=========================== Ending Init Tag =========================="

