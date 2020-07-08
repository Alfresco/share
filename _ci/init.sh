#!/usr/bin/env bash

echo "=========================== Starting Init Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

echo "${QUAY_PASSWORD}" | docker login -u="alfresco+bamboo" --password-stdin quay.io

# Enable experimental docker features (e.g. squash options)
echo '{"experimental":true}' | sudo tee /etc/docker/daemon.json
sudo service docker restart

popd
set +vex
echo "=========================== Finishing Init Script =========================="