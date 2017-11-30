#!/bin/sh
set -e

if [[ $HOST == "" ]]; then
   HOST=localhost
fi

if [[ $PORT == "" ]]; then
   PORT=8080
fi

echo "Replace with 'HOST' with '$HOST' and 'PORT' with '$PORT'"

sed -i -e 's/HOST:PORT/'"$HOST:$PORT"'/g' /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml

bash -c "$@"