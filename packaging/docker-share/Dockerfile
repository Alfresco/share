# Fetch image based on Tomcat 8.5 and Java 8
# More infos about this image: https://github.com/Alfresco/alfresco-docker-base-tomcat
FROM quay.io/alfresco/alfresco-legacy-repo:${project.version}

# Set default docker_context.
ARG RESOURCE_PATH=target

# Set default user information
ARG GROUPNAME=Alfresco
ARG GROUPID=1000
ARG IMAGEUSERNAME=alfresco
ARG USERID=33000

# Set default environment args
ARG TOMCAT_DIR=/usr/local/tomcat

# Create prerequisite to store tools, amps and properties
RUN mkdir -p ${TOMCAT_DIR}/amps_share && \
    mkdir -p ${TOMCAT_DIR}/amps

# Copy the amps from build context to the appropriate location for your application server
COPY target/war ${TOMCAT_DIR}/webapps
COPY ${RESOURCE_PATH}/amps_share ${TOMCAT_DIR}/amps_share
COPY ${RESOURCE_PATH}/amps ${TOMCAT_DIR}/amps

# Install amps on share.war and alfresco.war
RUN chmod -R =rwx ${TOMCAT_DIR}/webapps &&\
    java -jar ${TOMCAT_DIR}/alfresco-mmt/alfresco-mmt*.jar install \
              ${TOMCAT_DIR}/amps_share ${TOMCAT_DIR}/webapps/share -directory -nobackup -force && \
    java -jar ${TOMCAT_DIR}/alfresco-mmt/alfresco-mmt*.jar install \
              ${TOMCAT_DIR}/amps ${TOMCAT_DIR}/webapps/alfresco -directory -nobackup -force && \
    rm -f amps && \
    rm -f amps_share

RUN chmod -R =r ${TOMCAT_DIR}/webapps && \
# Add share.war
# Grant all security permissions to share webapp because of numerous permissions required in order to work properly.
    sed -i -e "\$a\grant\ codeBase\ \"file:\$\{catalina.base\}\/webapps\/share\/-\" \{\n\    permission\ java.security.AllPermission\;\n\};" /usr/local/tomcat/conf/catalina.policy