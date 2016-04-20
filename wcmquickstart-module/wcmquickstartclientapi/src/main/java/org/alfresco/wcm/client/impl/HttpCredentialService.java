package org.alfresco.wcm.client.impl;

public interface HttpCredentialService
{
    UsernameAndPassword getHttpCredentials();
    
    public class UsernameAndPassword
    {
        public String username;
        public String password;
        
        public UsernameAndPassword(String username, String password)
        {
            super();
            this.username = username;
            this.password = password;
        }
        
    }
}
