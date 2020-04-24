Encrypting configuration for share

You can encrypt sensitive properties from share-custom-config.xml

1. Run the Alfresco Share Encryption String Tool 
    
    a.Navigate to https://github.com/Alfresco/share/share-encryption
    
    b.Build the project using maven: mvn clean install
    
    c.Run the executable jar:
         
      java -jar alfresco-share-encryption-{version}.jar 
      Alfresco Share  Encrypted String Tool
      USAGE : org.alfresco.encryptor.ShareStringEncryption initkey | encrypt | validate <shared dir> 
        initkey : initialise the public and private keystores
        encrypt : encrypt a value 
        validate : compare an encrypted value with a value to see if they match
   
2.  Initkey : initialise the public and private keystores in the classpath (<ALFRESCO_HOME>/tomcat/shared/classes⁩) 
        
    java -jar alfresco-share-encryption-{version}.jar initkey <ALFRESCO_HOME>/tomcat/shared/classes⁩
    public key created file: <ALFRESCO_HOME>/tomcat/shared/classes⁩/alfresco/web-extension/alfrescoSpringKey.pub
    private key created file:<ALFRESCO_HOME>/tomcat/shared/classes⁩/alfresco/web-extension/alfrescoSpringKey.pri
    The key files have been generated, please set permissions on the private key to keep it protected  

3.  Encrypt : encrypt a value 
        
    java -jar alfresco-share-encryption-{version}.jar encrypt <ALFRESCO_HOME>/tomcat/shared/classes
    Please Enter Value: 
    Please Repeat Value: 
    fe6z6Is2VzD8wFTZ3eSikAbv0OpNxCikwVBnfe/LhPdqevCb4G1Vrvt7cTSA9z6OHkSh8ZzyKdEfVNPmTH66QA==

4.  Validate : compare an encrypted value with a value to see if they match
    
    java -jar alfresco-share-encryption-{version}.jarvalidate /Users/p3700670/work/share/tomcat/shared/classes fe6z6Is2VzD8wFTZ3eSikAbv0OpNxCikwVBnfe/LhPdqevCb4G1Vrvt7cTSA9z6OHkSh8ZzyKdEfVNPmTH66QA==
    Please Enter Value: 
    Please Repeat Value: 
    The value and encrypted value MATCH

 5. Add the encrypted password to <ALFRESCO_HOME>/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml 
 using the format: ENC('encypted-value').

      <config evaluator="string-compare" condition="Kerberos" replace="true">
         <kerberos>
            <!--
                  Password for HTTP service account.
                  The account name *must* be built from the HTTP server name, in the format :
                     HTTP/<server_name>@<realm>
                  (NB this is because the web browser requests an ST for the
                  HTTP/<server_name> principal in the current realm, so if we're to decode
                  that ST, it has to match.)
               -->
            <password>ENC(hvDxYTho75MVB4sbEzkdTrus6KBV6S5MaDJ/Jpk78b2X5uAvIi02c9A4BEYIu6sHV0mOnJsfHXjLjdQekq4BcQ==)</password>
            <!--
                  Kerberos realm and KDC address.
               -->
            <realm>SOME_KERBEROS_REALM</realm>
            <!--
                  Service Principal Name to use on the repository tier.
                  This must be like: HTTP/host.name@REALM
               -->
            <endpoint-spn>HTTP/SOME_HOST@SOME_KERBEROS_REALM</endpoint-spn>
            <!--
                  JAAS login configuration entry name.
               -->
            <config-entry>ShareHTTP</config-entry>
            <!--
               A Boolean which when true strips the @domain sufix from Kerberos authenticated usernames.
               Use together with stripUsernameSuffix property in alfresco-global.properties file.
            -->
            <stripUserNameSuffix>true</stripUserNameSuffix>

         </kerberos>
      </config>
