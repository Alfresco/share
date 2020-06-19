/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
