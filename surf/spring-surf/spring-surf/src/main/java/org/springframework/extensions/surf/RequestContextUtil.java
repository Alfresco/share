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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.support.ServletRequestContextFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Helper functions for working with request contexts outside of the
 * Spring MVC framework.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class RequestContextUtil 
{
    private static Log logger = LogFactory.getLog(RequestContextUtil.class);
    
    /**
     * Instantiates a default request context for the given http servlet request
     * 
     * @param applicationContext
     * @param request
     * @return
     * 
     * @throws RequestContextException
     */
    public static RequestContext initRequestContext(ApplicationContext applicationContext, HttpServletRequest request)
        throws RequestContextException
    {
        return initRequestContext(applicationContext, request, false);
    }
    
    /**
     * Instantiates a default request context for the given http servlet request
     * 
     * @param applicationContext
     * @param request
     * @param silentInit
     * @return
     * @throws RequestContextException
     */
    public static RequestContext initRequestContext(ApplicationContext applicationContext, HttpServletRequest request, boolean silentInit)
        throws RequestContextException
    {
        // store request into the request attributes
        ServletUtil.setRequest(request);

        // set up the request context
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context == null)
        {
            // build the request context
            ServletRequestContextFactory factory = (ServletRequestContextFactory) applicationContext.getBean("webframework.factory.requestcontext.servlet");
            context = factory.newInstance(new ServletWebRequest(request));
            
            // set onto request attribute (required)
            request.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context);
            
            // populate the request context
            try
            {
                populateRequestContext(context, request, silentInit);
            }
            catch (UserFactoryException ufe)
            {
                throw new RequestContextException("Unable to load user during request context population", ufe);                
            }
            catch (ResourceLoaderException rle)
            {
                throw new RequestContextException("Unable to load resource during request context population", rle);
            }
        }
        else
        {
            // set onto request attribute (required)
            request.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context);

            // ensure at the very least that the request context user is faulted
            if (!silentInit)
            {
                try
                {
                    initialiseUser(context, request);
                }
                catch (UserFactoryException ufe)
                {
                    throw new RequestContextException("Unable to initialise user during Request Context init. Probably a stale user Session.", ufe);                
                }
            }
        }
        
        return context;
    }
    
    /**
     * Populates an existing request context with context and user information
     * 
     * @param context
     * @param request
     * 
     * @throws UserFactoryException
     * @throws ResourceLoaderException
     */
    public static void populateRequestContext(RequestContext context, HttpServletRequest request)
        throws UserFactoryException, ResourceLoaderException
    {
        populateRequestContext(context, request, false);    
    }
    
    /**
     * Populates an existing request context with environment and user information
     * 
     * @param context
     * @param request
     * @param silentInit
     * 
     * @throws UserFactoryException
     * @throws ResourceLoaderException
     */
    public static void populateRequestContext(RequestContext context, HttpServletRequest request, boolean silentInit)
        throws UserFactoryException, ResourceLoaderException
    {            
        // Determine the format id
        // This always arrives as a request parameter
        // If no format id is provided, use web framework default
        String formatId = (String) request.getParameter("f");
        if (formatId == null || formatId.length() == 0)
        {
            formatId = context.getServiceRegistry().getWebFrameworkConfiguration().getDefaultFormatId();
        }
        if (formatId != null)
        {
            context.setFormatId(formatId);
        }
        
        // Determine the object id
        // This always arrives as a request parameter
        // If provided, load content and bind into request context
        String resourceId = (String) request.getParameter("o");
        if (resourceId != null && resourceId.length() != 0)
        {
            Resource resource = context.getServiceRegistry().getResourceService().getResource(resourceId);
            if (resource != null)
            {
                context.setCurrentObject(resource);
            }
        }
        
        // load the user (if applicable)
        if (!silentInit)
        {
            initialiseUser(context, request);
        }
        
        // initialise the theme if not already done so
        if (context.getTheme() == null)
        {
            Theme theme = null;
            String themeId = null;
            
            // look for a theme in the Site Configuration
            Configuration siteConfiguration = context.getSiteConfiguration();
            if (siteConfiguration != null)
            {
                themeId = siteConfiguration.getProperty("theme");
            }
            
            // look for a theme in the web-framework configuration
            if (themeId == null)
            {
                themeId = getDefaultThemeId();
            }
            
            if (themeId != null)
            {
                theme = context.getObjectService().getTheme(themeId);
                if (theme == null)
                {
                    // fallback - if theme object no longer exists i.e. a theme has been deleted - then
                    // we must have a sensible fallback path - attempt to resolve default theme if this occurs.
                    themeId = getDefaultThemeId();
                    
                    theme = context.getObjectService().getTheme(themeId);
                }
            }
            
            if (theme != null)
            {
                context.setTheme(theme);
            }
        }
        
        // if we don't have a site configuration, blank everything
        // TODO: do we even need this now?
        if (context.getSiteConfiguration() == null)
        {
            context.setPage(null);
            context.setCurrentObject(null);
        }
    }
    
    private static String getDefaultThemeId()
    {
        String themeId = FrameworkUtil.getWebFrameworkConfiguration().getDefaultThemeId();
        if (themeId == null)
        {
            // select a default if no theme set elsewhere
            themeId = WebFrameworkConstants.DEFAULT_THEME_ID;
        }
        return themeId;
    }
    
    /**
     * Loads the user and places them onto the request context
     * 
     * @param context
     * @param request
     * @throws Exception
     */
    private static void initialiseUser(RequestContext context, HttpServletRequest request)
        throws UserFactoryException
    {
        if (context.getUser() == null)
        {
            // allow the endpoint id to be explicitly overridden via a request attribute
            String userEndpointId = (String) context.getAttribute(RequestContext.USER_ENDPOINT);
            
            UserFactory userFactory = context.getServiceRegistry().getUserFactory();
            User user = userFactory.initialiseUser(context, (HttpServletRequest)request, userEndpointId);
            context.setUser(user);
        }
    }
}   
