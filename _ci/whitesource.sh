#!/usr/bin/env bash

echo "=========================== Starting WhiteSource Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# Download the latest version of WhiteSource Unified Agent
curl -LJO https://github.com/whitesource/unified-agent-distribution/releases/latest/download/wss-unified-agent.jar
# Run WhiteSource Unified Agent
java -jar wss-unified-agent.jar -apiKey ${WHITESOURCE_API_KEY} -c .wss-unified-agent.config 


popd
set +vex
echo "=========================== Finishing WhiteSource Script =========================="