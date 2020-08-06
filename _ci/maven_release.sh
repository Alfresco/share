#!/usr/bin/env bash
set -e

# Use full history for release
git checkout -B "${TRAVIS_BRANCH}"
# Add email to link commits to user
git config user.email "${GIT_EMAIL}"

if [ -z ${RELEASE_VERSION} ] || [ -z ${DEVELOPMENT_VERSION} ];
    then echo "Please provide a Release and Development verison in the format <share>-<additional-info> (7.0.0-EA or 7.0.0-SNAPSHOT)"
         exit -1
else
   mvn --batch-mode \
   -DreleaseVersion=${RELEASE_VERSION} \
   -DdevelopmentVersion=${DEVELOPMENT_VERSION} \
   -Dusername="${GIT_USERNAME}" \
   -Dpassword="${GIT_PASSWORD}" \
   -Dbuild-number=${TRAVIS_BUILD_NUMBER} \
   -Dbuild-name="${TRAVIS_BUILD_STAGE_NAME}" \
   -DskipTests \
   -Darguments=-DskipTests \
   -Darguments=-Dmaven.javadoc.skip=true \
   -Prelease release:clean release:prepare release:perform
fi