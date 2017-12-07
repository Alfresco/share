FROM quay.io/alfresco/alfresco-base-tomcat:0.1.0

RUN mkdir -p /usr/local/tomcat/shared/classes/alfresco/web-extension

COPY target/war /usr/local/tomcat/webapps

COPY target/classes/web-extension-samples/share-config-custom.xml /usr/local/tomcat/shared/classes/alfresco/web-extension
COPY target/classes/web-extension-samples/custom-slingshot-application-context.xml.sample /usr/local/tomcat/shared/classes/alfresco/web-extension
COPY target/classes/web-extension-samples/smartfolders-amp-actions-config.xml /usr/local/tomcat/shared/classes/alfresco/web-extension

#not sure if this is needed for share:
RUN sed -i "s/shared.loader=/shared.loader=\${catalina.base}\/shared\/classes/" /usr/local/tomcat/conf/catalina.properties

COPY substituter.sh /usr/local/tomcat/shared/classes/alfresco
RUN chmod +x /usr/local/tomcat/shared/classes/alfresco/substituter.sh

ENTRYPOINT ["/usr/local/tomcat/shared/classes/alfresco/substituter.sh", "catalina.sh run"]