# Encrypting configuration for share
You can encrypt sensitive properties from share-custom-config.xml

1. Run the Alfresco Share Encryption String Tool 
    a.Navigate to https://github.com/Alfresco/share/share-encryption
    b.Build the project using maven: mvn clean install
    c.Run the executable jar:
         
         ```bash
          java -jar share-encryption-1.0-SNAPSHOT.jar
          Alfresco Share  Encrypted String Tool
          USAGE : org.alfresco.encryptor.ShareStringEncription initkey | encrypt | validate <shared dir> 
            initkey : initialise the public and private keystores
            encrypt : encrypt a value 
            validate : compare an encrypted value with a value to see if they match
          ```
2.  Initkey : initialise the public and private keystores:
        
        ```bash
        java -jar alfresco-spring-encryptor-6.1.jar initkey /Users/p3700670/work/share/tomcat/shared/classes
        public key created file: /Users/p3700670/work/share/tomcat/shared/classes/alfresco/extension/enterprise/alfrescoSpringKey.pub
        private key created file:/Users/p3700670/work/share/tomcat/shared/classes/alfresco/extension/enterprise/alfrescoSpringKey.pri
        The key files have been generated, please set permissions on the private key to keep it protected  
         ```

3.  Encrypt : encrypt a value 
        
         ```bash
        java -jar alfresco-spring-encryptor-6.1.jar encrypt /Users/p3700670/work/share/tomcat/shared/classes
        Please Enter Value: 
        Please Repeat Value: 
        fe6z6Is2VzD8wFTZ3eSikAbv0OpNxCikwVBnfe/LhPdqevCb4G1Vrvt7cTSA9z6OHkSh8ZzyKdEfVNPmTH66QA==
        ```

4.  Validate : compare an encrypted value with a value to see if they match
    
    ```bash
    java -jar alfresco-spring-encryptor-6.1.jar validate /Users/p3700670/work/share/tomcat/shared/classes fe6z6Is2VzD8wFTZ3eSikAbv0OpNxCikwVBnfe/LhPdqevCb4G1Vrvt7cTSA9z6OHkSh8ZzyKdEfVNPmTH66QA==
    Please Enter Value: 
    Please Repeat Value: 
    The value and encrypted value MATCH
    ```