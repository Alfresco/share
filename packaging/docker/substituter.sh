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

echo "NEW -csrf.filter.referer is '$CSRF_FILTER_REFERER'"
echo "NEW -csrf.filter.origin is '$CSRF_FILTER_ORIGIN'"

if [ $CSRF_FILTER_REFERER != "" ] && [   $CSRF_FILTER_ORIGIN != "" ]; then
# set CSRFPolicy to true and set both properties referer and origin
   sed -i -e "s|<config evaluator=\"string-compare\" condition=\"CSRFPolicy\" replace=\"false\">|<config evaluator=\"string-compare\" condition=\"CSRFPolicy\" replace=\"true\">|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
   sed -i -e "s|<referer><\/referer>|<referer>$CSRF_FILTER_REFERER<\/referer>|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
   sed -i -e "s|<origin><\/origin>|<origin>$CSRF_FILTER_ORIGIN<\/origin>|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
   
else
# set CSRFPolicy to false and leave empty the properties referer and origin
   sed -i -e "s|<config evaluator=\"string-compare\" condition=\"CSRFPolicy\" replace=\"false\">|<config evaluator=\"string-compare\" condition=\"CSRFPolicy\" replace=\"false\">|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
   sed -i -e "s|<referer><\/referer>|<referer><\/referer>|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
   sed -i -e "s|<origin><\/origin>|<origin><\/origin>|" /usr/local/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml
fi

bash -c "$@"
