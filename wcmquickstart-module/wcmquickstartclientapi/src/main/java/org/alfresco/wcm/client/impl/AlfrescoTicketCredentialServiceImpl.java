package org.alfresco.wcm.client.impl;


public class AlfrescoTicketCredentialServiceImpl implements HttpCredentialService
{
    private WebScriptCaller webscriptCaller;
    private String username;
    private String password;
    private UsernameAndPassword credentials = null;

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public synchronized UsernameAndPassword getHttpCredentials()
    {
        if (credentials == null)
        {
            String ticket = webscriptCaller.getTicket(username, password);
            if (ticket == null)
            {
                credentials = new UsernameAndPassword(username, password);
            }
            else
            {
                credentials = new UsernameAndPassword("", ticket);
            }
        }
        return credentials;
    }
}
