/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.exception.ConnectorProviderException;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorProvider;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.User;

/**
 * An implementation of connector provider that provides access to the
 * Web Framework request context to build connectors
 *
 * @author Kevin Roast
 * @author muzquiano
 */
public class WebFrameworkConnectorProvider implements ConnectorProvider
{    
    private static final Log logger = LogFactory.getLog(WebFrameworkConnectorProvider.class);

    private ConnectorService connectorService;
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    /**
     * Implementation of the contract to provide a Connector for our remote store.
     * This allows lazy providing of the Connector object only if the remote store actually needs
     * it. Otherwise acquiring the Connector when rarely used is an expensive overhead as most
     * objects are cached by the persister in which case the remote store isn't actually called.
     */
    public Connector provide(String endpoint) throws ConnectorProviderException
    {
        Connector conn = null;
        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        
        if (rc != null)
        {
            try
            {
                // check whether we have a current user
                User user = rc.getUser();
                if (user == null || rc.getCredentialVault() == null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("No user was found, creating unauthenticated connector");
                    
                    // return the non-credential'ed connector to this endpoint
                    conn = connectorService.getConnector(endpoint);
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("User '" + user.getId() + "' was found, creating authenticated connector");
                    
                    // return the credential'ed connector to this endpoint
                    HttpSession httpSession = ServletUtil.getSession(true);
                    conn = connectorService.getConnector(endpoint, rc.getUserId(), httpSession);
                }
            }
            catch (ConnectorServiceException cse)
            {
                throw new ConnectorProviderException("Unable to provision connector for endpoint: " + endpoint, cse);
            }
        }
        else
        {
            // if we don't have a request context, we can still provision a connector with no credentials
            try
            {
                conn = connectorService.getConnector(endpoint);
            }
            catch (ConnectorServiceException cse)
            {
                throw new ConnectorProviderException("Unable to provision non-credential'd connector for endpoint: " + endpoint, cse);
            }
        }
        
        return conn;
    }
}
