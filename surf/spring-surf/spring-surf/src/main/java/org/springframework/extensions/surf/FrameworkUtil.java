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
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceLoader;
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
 * @author muzquiano
 * @deprecated
 */
public final class FrameworkUtil
{
    private static Log logger = LogFactory.getLog(FrameworkUtil.class);
        
    /**
     * Retrieves the request context for the current thread.
     * 
     * @return RequestContext
     */
    public static RequestContext getCurrentRequestContext()
    {
        return ThreadLocalRequestContext.getRequestContext();
    }
    
    /**
     * Retrieves the web framework services registry.
     * 
     * @return
     */
    public static WebFrameworkServiceRegistry getServiceRegistry()
    {
        return getCurrentRequestContext().getServiceRegistry();
    }
    
    /**
     * Gets the config service.
     * 
     * @return the config service
     */
    public static ConfigService getConfigService()
    {
        return getServiceRegistry().getConfigService();
    }
    
    /**
     * Retrieves the web framework configuration.
     * 
     * @return
     */
    public static WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return getServiceRegistry().getWebFrameworkConfiguration();
    }

    /**
     * Retrieves the web framework configuration.
     * Note: Provided for convenience
     * 
     * @return
     * @deprecated
     */
    public static WebFrameworkConfigElement getConfig()
    {
        return getWebFrameworkConfiguration();
    }
    
    /**
     * Retrieves the web framework remote configuration.
     * @return
     */
    public static RemoteConfigElement getRemoteConfiguration()
    {
        return getServiceRegistry().getRemoteConfigElement();
    }
    
    /**
     * Retrieves the connector service.
     * 
     * @return
     */
    public static ConnectorService getConnectorService()
    {
        return getServiceRegistry().getConnectorService();
    }
    
    /**
     * Gets the render service.
     * 
     * @return the render service
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
     * Retrieves the general web framework logger.
     * 
     * @return general web framework logger
     */
    public static Log getLogger()
    {
        return logger;
    }
    
    /**
     * Loads the endpoint descriptor for a given endpoint.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the descriptor
     */
    public static EndpointDescriptor getEndpoint(String endpointId)
    {
        return getRemoteConfiguration().getEndpointDescriptor(endpointId);
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
    public static Connector getConnector(String endpointId)
        throws ConnectorServiceException
    {
        return getConnectorService().getConnector(endpointId);
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
    public static Connector getConnector(RequestContext context, String endpointId)
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
    public static Connector getConnector(HttpSession httpSession, String userId, String endpointId)
        throws ConnectorServiceException
    {
        return getConnectorService().getConnector(endpointId, userId, httpSession);
    }
    
    /**
     * Retrieves the session-bound credential vault for a given user.
     * 
     * @param httpSession the http session
     * @param userId the user id
     * 
     * @return the credential vault
     */
    public static CredentialVault getCredentialVault(HttpSession httpSession, String userId)
    {
        CredentialVault vault = null;
        try
        {
            vault = getConnectorService().getCredentialVault(httpSession, userId);
        }
        catch (CredentialVaultProviderException cvpe)
        {
            logger.error("Unable to retrieve credential vault for user: " + userId, cvpe);
        }
        
        return vault;
    }
    
    /**
     * <p>Retrieves the session-bound credential vault for a given user.</p>
     * 
     * @param context the context
     * @param userId the user id
     * 
     * @return the credential vault
     * @deprecated This has been deprecated as all static helper methods need to be removed.
     * TODO Indicate which bean to use once it has been named!
     */
    public static CredentialVault getCredentialVault(RequestContext context, String userId)
    {
        HttpSession httpSession = ServletUtil.getSession(false);
        
        return (httpSession != null ? getCredentialVault(httpSession, userId) : null);
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
    public static ConnectorSession getConnectorSession(RequestContext context, String endpointId)
    {
        HttpSession httpSession = ServletUtil.getSession();
        return getConnectorSession(httpSession, endpointId);
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
    public static ConnectorSession getConnectorSession(HttpSession httpSession, String endpointId)
    {
        return getConnectorService().getConnectorSession(httpSession, endpointId);
    }
    
    /**
     * Removes all session-bound Connector Sessions for the current user
     * 
     * @param context the context
     */
    public static void removeConnectorSessions(RequestContext context)
    {
        try
        {
            HttpSession httpSession = ServletUtil.getSession();
            
            String[] endpointIds = getRemoteConfiguration().getEndpointIds();
            for (int i = 0; i < endpointIds.length; i++)
            {
                getConnectorService().removeConnectorSession(httpSession, endpointIds[i]);
            }
        }
        catch (Exception ex)
        {
            logger.error("Unable to remove connector sessions", ex);
        }
    }
    
    /**
     * Returns the official title of this release of the Alfresco Web Framework
     * 
     * @return the framework title
     */
    public static String getFrameworkTitle()
    {
        return WebFrameworkConstants.FRAMEWORK_TITLE;
    }
    
    /**
     * Returns the official version of this release of the Alfresco Web Framework
     * 
     * @return the framework version
     */
    public static String getFrameworkVersion()
    {
        return WebFrameworkConstants.FRAMEWORK_VERSION;
    }    

    /**
     * Produces a ResourceLoader for the given object id on a given endpoint
     * 
     * @param objectId the object id
     * @param endpointId the endpoint id
     * 
     * @return the resource loader
     */
    public static ResourceLoader getResourceLoader(String objectId, String endpointId)
    {
        return getServiceRegistry().getResourceService().getResourceLoader(objectId, endpointId);
    }
    
    /**
     * Helper function to reset all web scripts in the web framework
     * web scripts container
     */
    public static void resetWebScripts()
    {
        Container container = getServiceRegistry().getWebFrameworkContainer();
        if (container != null)
        {
            int previousCount = container.getRegistry().getWebScripts().size();
            int previousFailures = container.getRegistry().getFailures().size();
            
            container.reset();
            
            // debug out
            FrameworkUtil.getLogger().info("Reset Web Scripts Registry; registered " + container.getRegistry().getWebScripts().size() + " Web Scripts.  Previously, there were " + previousCount + ".");
            
            int newFailures = container.getRegistry().getFailures().size();
            if (newFailures != 0 || previousFailures != 0)
            {
                FrameworkUtil.getLogger().info("Warning: found " + newFailures + " broken Web Scripts.  Previously, there were " + previousFailures + ".");
            }
        }        
    }
        
    /**
     * @deprecated
     * @param throwable
     */
    public static void logFullStacktrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        // dump out stack trace into writer
        throwable.printStackTrace(pw);
                
        FrameworkUtil.getLogger().error(sw.toString());
    }
    
}
