#!/usr/bin/env bash

echo "=========================== Starting Init Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

mkdir -p ${HOME}/.m2 && cp -rf _ci/settings.xml ${HOME}/.m2/
echo "${QUAY_PASSWORD}" | docker login -u="alfresco+bamboo" --password-stdin quay.io
find "${HOME}/.m2/repository/" -type d -name "*-SNAPSHOT*" | xargs -r -l rm -rf

popd
set +vex
echo "=========================== Finishing Init Script =========================="