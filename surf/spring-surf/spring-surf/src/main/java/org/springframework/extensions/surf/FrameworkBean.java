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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.CredentialVault;

/**
 * Static methods which are useful for working with services and
 * beans within the Web Framework.
 * 
 * @author David Draper
 */
public final class FrameworkBean
{
    private static Log logger = LogFactory.getLog(FrameworkBean.class);
        
    public FrameworkBean()
    {
        // No args constructor is required for Spring Beans.
    }
    
    private Container webFrameworkContainer;
    
    public void setWebFrameworkContainer(Container container)
    {
        this.webFrameworkContainer = container;
    }

    /**
     * <p>The RemoteConfigElement.</p>
     */
    private RemoteConfigElement remoteConfig;
    
    public void setRemoteConfig(RemoteConfigElement remoteConfig)
    {
        this.remoteConfig = remoteConfig;
    }
    
    private ConnectorService connectorService;
    
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    /**
     * Retrieves the web framework services registry.
     * 
     * @return
     * @deprecated
     */
    public static WebFrameworkServiceRegistry getServiceRegistry()
    {
        return ThreadLocalRequestContext.getRequestContext().getServiceRegistry();
    }
    
    /**
     * 
     * @return
     * @deprecated
     */
    public static WebFrameworkConfigElement getConfig()
    {
        return getServiceRegistry().getWebFrameworkConfiguration();
    }
    
    /**
     * Gets the render service.
     * 
     * @return the render service
     * @deprecated
     */
    public static RenderService getRenderService()
    {
        return getServiceRegistry().getRenderService();
    }    
    
    /**
     * Gets the resource service.
     * 
     * @return the resource service
     * @deprecated
     */
    public static ResourceService getResourceService()
    {
        return getServiceRegistry().getResourceService();
    }
            
    /**
     * Loads the endpoint descriptor for a given endpoint.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the descriptor
     */
    public EndpointDescriptor getEndpoint(String endpointId)
    {
        return this.remoteConfig.getEndpointDescriptor(endpointId);
    }
    
    /**
     * Creates an unauthenticated connector to a given endpoint.
     * 
     * @param endpointId endpoint id
     * 
     * @return the connector
     * 
     * @throws ConnectorServiceException the connector service exception
     */
    public Connector getConnector(String endpointId)
        throws ConnectorServiceException
    {
        return connectorService.getConnector(endpointId);
    }
    
    /**
     * Creates an authenticated connector to a given endpoint.
     * 
     * @param context the request context
     * @param endpointId the endpoint
     * 
     * @return the connector
     * 
     * @throws ConnectorServiceException the connector service exception
     */
    public Connector getConnector(RequestContext context, String endpointId)
        throws ConnectorServiceException
    {
        HttpSession httpSession = ServletUtil.getSession();
        return getConnector(httpSession, context.getUserId(), endpointId);
    }
    
    /**
     * Creates an authenticated connector to a given endpoint.
     * 
     * @param httpSession the http session
     * @param userId the user id
     * @param endpointId the endpoint id
     * 
     * @return the connector
     * 
     * @throws ConnectorServiceException the connector service exception
     */
    public Connector getConnector(HttpSession httpSession, String userId, String endpointId)
        throws ConnectorServiceException
    {
        return connectorService.getConnector(endpointId, userId, httpSession);
    }
    
    /**
     * Retrieves the session-bound credential vault for a given user.
     * 
     * @param httpSession the http session
     * @param userId the user id
     * 
     * @return the credential vault
     */
    public CredentialVault getCredentialVault(HttpSession httpSession, String userId)
    {
        CredentialVault vault = null;
        try
        {
            vault = connectorService.getCredentialVault(httpSession, userId);
        }
        catch (CredentialVaultProviderException cvpe)
        {
            logger.error("Unable to retrieve credential vault for user: " + userId, cvpe);
        }
        
        return vault;
    }
    
    /**
     * Retrieves the session-bound credential vault for a given user.
     * 
     * @param context the context
     * @param userId the user id
     * 
     * @return the credential vault
     */
    public CredentialVault getCredentialVault(RequestContext context, String userId)
    {
        HttpSession httpSession = ServletUtil.getSession(false);
        CredentialVault credentialVault = (httpSession != null ? getCredentialVault(httpSession, userId) : null);
        return credentialVault;
    }
    
    /**
     * Retrieves the Connector Session instance for the current 
     * user and given endpoint.
     * 
     * @param context the context
     * @param endpointId the endpoint id
     * 
     * @return the connector session
     */
    public ConnectorSession getConnectorSession(RequestContext context, String endpointId)
    {
        HttpSession httpSession = ServletUtil.getSession();
        return connectorService.getConnectorSession(httpSession, endpointId);
    }

    /**
     * Retrieves the Connector Session instance for the current
     * session and given endpoint.
     * 
     * @param httpSession the http session
     * @param endpointId the endpoint id
     * 
     * @return the connector session
     */
    public ConnectorSession getConnectorSession(HttpSession httpSession, String endpointId)
    {
        return connectorService.getConnectorSession(httpSession, endpointId);
    }
    
    /**
     * Removes all session-bound Connector Sessions for the current user
     * 
     * @param context the context
     */
    public void removeConnectorSessions(RequestContext context)
    {
        try
        {
            HttpSession httpSession = ServletUtil.getSession();
            
            String[] endpointIds = this.remoteConfig.getEndpointIds();
            for (int i = 0; i < endpointIds.length; i++)
            {
                connectorService.removeConnectorSession(httpSession, endpointIds[i]);
            }
        }
        catch (Exception ex)
        {
            logger.error("Unable to remove connector sessions", ex);
        }
    }
    
    /**
     * Helper function to reset all web scripts in the web framework
     * web scripts container
     */
    public void resetWebScripts()
    {
        if (this.webFrameworkContainer != null)
        {
            int previousCount = this.webFrameworkContainer.getRegistry().getWebScripts().size();
            int previousFailures = this.webFrameworkContainer.getRegistry().getFailures().size();
            
            this.webFrameworkContainer.reset();
            
            // debug out
            logger.info("Reset Web Scripts Registry; registered " + this.webFrameworkContainer.getRegistry().getWebScripts().size() + " Web Scripts.  Previously, there were " + previousCount + ".");
            
            int newFailures = this.webFrameworkContainer.getRegistry().getFailures().size();
            if (newFailures != 0 || previousFailures != 0)
            {
                logger.info("Warning: found " + newFailures + " broken Web Scripts.  Previously, there were " + previousFailures + ".");
            }
        }        
    }
        
    public void logFullStacktrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        // dump out stack trace into writer
        throwable.printStackTrace(pw);
                
        logger.error(sw.toString());
    }   
}
