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

package org.springframework.extensions.surf.mvc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.WrappedHttpServletRequest;
import org.springframework.extensions.surf.util.WrappedHttpServletResponse;
import org.springframework.extensions.webscripts.servlet.mvc.ResourceController;

/**
 * <p>
 * Virtualized Spring controller for retrieving and serving
 * resources.
 * </p><p>
 * This builds on the default resource controller provided by the
 * Web Script Framework.  It empowers the controller to retrieve
 * resources from remote CMIS stores ahead of the web application.
 * This empowers the web application with preview capabilities.
 * </p><p>
 * This controller retrieves content by interrogating resource providers
 * in the following order:
 * </p><ol>
 * <li>CMIS Resource Store (i.e. Alfresco Store)</li>
 * <li>Web application path</li>
 * <li>Delegation to default url handler (typically the Spring JS resource servlet)</li>
 * </ol><p>
 * The first provider is only consulted if the Web Framework
 * is running in preview mode.  If the Web Framework is configured
 * to run in production mode, virtualized resource stores are
 * never considered.
 * </p><p>
 * The following URL formats are supported:
 * </p><ul>
 * <li>/resource/<{@code}path>?e=<{@code}endpointId>&s=<{@code}storeId>&<{@code}webappId></li>
 * <li>/resource/<{@code}path>?e=<{@code}endpointId>&s=<{@code}storeId></li>
 * <li>/resource/<{@code}path>?e=<{@code}endpointId></li>
 * <li>/resource/<{@code}path>?s=<{@code}storeId></li>    
 * <li>/resource/<{@code}path></li>
 * </ul><p>
 * In the latter cases, the web framework's selected store and webapp
 * are determined from session and reused.  This enables the case for
 * resources to be loaded as though they were local to disk (and located
 * in the /resource context). 
 * </p>  
 * @author muzquiano
 * @author David Draper
 */
public class VirtualizedResourceController extends ResourceController
{
    private static Log logger = LogFactory.getLog(VirtualizedResourceController.class);
        
    // the web framework service registry
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry;
    
    /**
     * Sets the service registry.
     * 
     * @param webFrameworkServiceRegistry the new service registry
     */
    public void setServiceRegistry(WebFrameworkServiceRegistry webFrameworkServiceRegistry)
    {
        this.webFrameworkServiceRegistry = webFrameworkServiceRegistry;
    }
    
    /**
     * Gets the service registry.
     * 
     * @return the service registry
     */
    public WebFrameworkServiceRegistry getServiceRegistry()
    {
        return this.webFrameworkServiceRegistry;
    }
    
    /**
     * <p>The RemoteConfigElement.</p>
     * TODO: Need a description of what this is and what it is used for!
     */
    private RemoteConfigElement remoteConfig;
    
    public void setRemoteConfig(RemoteConfigElement remoteConfig)
    {
        this.remoteConfig = remoteConfig;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractWebFrameworkController#getLogger()
     */
    public Log getLogger()
    {
        return logger;        
    }

    @Override
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.servlet.mvc.ResourceController#dispatchResource(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public boolean dispatchResource(String path, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // are we in preview mode
        boolean previewMode = this.getServiceRegistry().getWebFrameworkConfiguration().isPreviewEnabled();

        boolean resolved = false;
        if (!resolved && previewMode)
        {
            // look up endpoint id, store id and webapp id
            String endpointId = (String) request.getParameter("e");
            String storeId = (String) request.getParameter("s");
            String webappId = (String) request.getParameter("w");

            // see if we can resolve and serve back the remote resource            
            resolved = retrieveRemoteResource(request, response, path, endpointId, storeId, webappId);
        }
        
        if (!resolved)
        {
            // call to super class method which does all of the
            // other lookup patterns, such as looking up in the web
            // application context and web app jar files
            
            resolved = super.dispatchResource(path, request, response);
        }
        
        return resolved;
    }
    
    /**
     * Retrieves content from the given remote store and streams back to the response
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param path the path to be included
     * @param endpointId the endpoint to utilize (optional)
     * @param storeId the store to utilize (optional)
     * @param webappId the webapp to utilize (optional)
     * 
     * @return whether the resource was served back successfully
     */
    public boolean retrieveRemoteResource(HttpServletRequest request, HttpServletResponse response, String path, String endpointId, String storeId, String webappId)
        throws ServletException, IOException
    {
        boolean resolved = false;
        
        // get the request context
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        
        // default treatment of these fellows
        if (endpointId == null)
        {
            endpointId = this.remoteConfig.getDefaultEndpointId();
        }
        if (storeId != null)
        {
            // use the currently bound request context store id
            storeId = context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getStoreId();
        }
        if (webappId == null)
        {
            // use the currently bound request context web app id
            webappId = context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getWebappId();
        }
        
        // check whether the resource exists on the remote store
        // TODO: this is expensive... is this the cost of preview-able virtualized remote retrieval?
        // TODO: ideally, we could hold a cache and have the authoring server notify test servers of changes, but that will require more investigation
        boolean exists = checkRemoteResourceExists(context, request, response, path, endpointId, storeId, webappId);
        if (exists)
        {
            // build a URL to the endpoint proxy servlet
            StringBuilder fb = new StringBuilder(128);
            fb.append(request.getServletPath());
            fb.append("/endpoint/");
            fb.append(endpointId);
            fb.append("/avmstore/get/s/");
            fb.append(storeId);
            fb.append("/w/");
            fb.append(webappId);
        
            if (!path.startsWith("/"))
            {
                fb.append("/");
            }
            fb.append(path);
            
            String newUri = fb.toString();
            
            if (logger.isDebugEnabled())
                logger.debug("Formed virtual retrieval path: " + newUri);
        
            // make sure the request uri is properly established so that we can
            // flow through the proxy servlet
            if (request instanceof WrappedHttpServletRequest)
            {
                ((WrappedHttpServletRequest)request).setRequestURI(request.getContextPath() + newUri);
            }
        
            // dispatch to the endpoint proxy servlet
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(newUri);        
            dispatcher.include(request, response);
            
            resolved = true;
        }
        
        return resolved;
    }
    
    /**
     * Checks for the existence of a resource on a remote store
     * 
     * @param context the request context
     * @param request http servlet request
     * @param response http servlet response
     * @param path the path to the asset
     * @param endpointId the endpoint where the resource lives
     * @param storeId the store within which the resource lives
     * @param webappId the web application that the resource lives in
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public boolean checkRemoteResourceExists(RequestContext context, HttpServletRequest request, HttpServletResponse response, String path, String endpointId, String storeId, String webappId)
        throws ServletException, IOException
    {
        boolean exists = false;
        
        // default treatment of these fellows
        if (endpointId == null)
        {
            endpointId = this.remoteConfig.getDefaultEndpointId();
        }
        if (storeId != null)
        {
            // use the currently bound request context store id
            storeId = context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getStoreId();
        }
        if (webappId == null)
        {
            // use the currently bound request context web app id
            webappId = context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getWebappId();
        }        
        
        // build a URL to the endpoint proxy servlet
        StringBuilder fb = new StringBuilder(128);
        fb.append(request.getServletPath());
        fb.append("/endpoint/");
        fb.append(endpointId);
        fb.append("/avmstore/has/s/");
        fb.append(storeId);
        fb.append("/w/");
        fb.append(webappId);
    
        if (!path.startsWith("/"))
        {
            fb.append("/");
        }
        fb.append(path);
        
        String newUri = fb.toString();
        
        if (logger.isDebugEnabled())
            logger.debug("Formed virtual retrieval path: " + newUri);
    
        // build some wrapper objects so that we can dispatch to endpoint proxy servlet
        // this means that we can avoid building connectors by hand
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
        WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);

        // dispatch to the endpoint proxy servlet        
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(newUri);        
        dispatcher.include(wrappedRequest, wrappedResponse);

        // check whether the resource exists
        String result = wrappedResponse.getOutput();
        if ("true".equalsIgnoreCase(result))
        {
            exists = true;
        }
        
        return exists;        
    }
}
