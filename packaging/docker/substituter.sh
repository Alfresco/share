#!/bin/sh
set -e

if [[ $REPO_HOST == "" ]]; then
   REPO_HOST=localhost
fi

if [[ $REPO_PORT == "" ]]; then
   REPO_PORT=8080
fi

echo "Replace 'REPO_HOST' with '$REPO_HOST' and 'REPO_PORT' with '$REPO_PORT'"

sed -i -e 's/REPO_HOST:REPO_PORT/'"$REPO_HOST:$REPO_PORT"'/g' /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml

bash -c "$@"
