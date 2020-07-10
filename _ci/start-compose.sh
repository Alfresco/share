#!/usr/bin/env bash

echo "=========================== Starting Docker compose Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

export DOCKER_COMPOSE_PATH=$1
if [ -z "$DOCKER_COMPOSE_PATH" ]
then
  echo "Please provide path to docker-compose.yml: \"${0##*/} /path/to/docker-compose.yml\""
  exit 1
fi

echo "Starting Share stack in ${DOCKER_COMPOSE_PATH}"
source _ci/init_tag.sh

# Change tag if you are on a branch
if [ ! -z "$TRAVIS_BRANCH" -a "$TRAVIS_BRANCH" != "master" ]; then
  sed  -i "s/alfresco-share:latest/alfresco-share:$TAG_NAME/"  ${DOCKER_COMPOSE_PATH}
fi

# .env files are picked up from project directory correctly on docker-compose 1.23.0+
docker-compose --file "${DOCKER_COMPOSE_PATH}" --project-directory $(dirname "${DOCKER_COMPOSE_PATH}") up -d

if [ $? -eq 0 ]
then
  echo "Docker Compose started ok"
else
  echo "Docker Compose failed to start" >&2
  exit 1
fi

echo "=========================== Ending Docker compose Script ==========================="