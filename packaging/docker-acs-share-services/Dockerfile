#Fetch image of ACS Community Edition
FROM alfresco/alfresco-community-repo-base:latest

USER root

# Set default Docker context
ARG RESOURCE_PATH=target

# Set default environment args
ARG GROUPNAME=Alfresco
ARG IMAGEUSERNAME=alfresco
ARG TOMCAT_DIR=/usr/local/tomcat

# Copy the amps from build context to the appropriate location for your application server
COPY ${RESOURCE_PATH}/amps ${TOMCAT_DIR}/amps

RUN chgrp -R ${GROUPNAME} ${TOMCAT_DIR}/amps && \
    chmod 664 ${TOMCAT_DIR}/amps/alfresco-share-services-*.amp

# Apply Share Services amp
RUN java -jar ${TOMCAT_DIR}/alfresco-mmt/alfresco-mmt*.jar install \
    ${TOMCAT_DIR}/amps/alfresco-share-services*.amp \
    ${TOMCAT_DIR}/webapps/alfresco -nobackup -force

EXPOSE 8000

USER ${IMAGEUSERNAME}

