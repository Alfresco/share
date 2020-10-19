#!/usr/bin/env bash

echo "=========================== Starting Init Tag==========================="

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "The branch is ${TRAVIS_BRANCH}"
echo "Project version: ${PROJECT_VERSION}"
echo "Travis commit message: ${TRAVIS_COMMIT_MESSAGE}"
echo "Release version: ${RELEASE_VERSION}"

if [ "${TRAVIS_BRANCH}" = "develop" ]; then
  if [ "${TRAVIS_EVENT_TYPE}" != "pull_request" ]; then
    TAG_NAME="latest"
  else
    TAG_NAME="pr-${TRAVIS_PULL_REQUEST}"
  fi
else
  # substitude all '/' to '-' as Docker doesn't allow it
  TAG_NAME=`echo ${TRAVIS_BRANCH} | tr / - `
  TAG_NAME=${TAG_NAME}-${PROJECT_VERSION}
fi

#in case of release
if [[ "${TRAVIS_COMMIT_MESSAGE}" = *"release"* ]]; then
  TAG_NAME=${RELEASE_VERSION}
fi

echo "Saving tag name as ${TAG_NAME}"

echo "=========================== Ending Init Tag =========================="

