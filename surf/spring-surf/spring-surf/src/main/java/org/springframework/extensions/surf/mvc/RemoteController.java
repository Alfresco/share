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

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.site.CacheUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Default Spring controller for processing Surf remote control calls.
 * 
 * Surf remote control calls are essentially REST-style calls which tell
 * the Surf server to perform operations against its cache or its
 * internal state.
 * 
 * The following URLs are supported:
 * 
 *     /cache/invalidate
 *     /webscripts/reset
 *     
 * These are generally of the form:
 * 
 *     /<system>/<action>
 * 
 * @author muzquiano
 */
public class RemoteController extends AbstractController
{
    private static final String MODE_CACHE = "cache";
    private static final String MODE_CACHE_COMMAND_INVALIDATE = "invalidate";
    
    private static final String MODE_WEBSCRIPTS = "webscripts";
    private static final String MODE_WEBSCRIPTS_COMMAND_RESET = "reset";
    
    /**
     * <p>The <code>FrameworkUtil</code> is needed for resetting WebScripts. It is defined as a Spring Bean and is instantiated
     * and set by the Spring Framework.</p>
     */
    private FrameworkBean frameworkUtil;
        
    /**
     * <p>Setter required by the Spring Framework to set the <code>FrameworkUtil</code> bean used for resetting WebScripts</p>
     * @param frameworkUtil
     */
    public void setFrameworkUtil(FrameworkBean frameworkUtil)
    {
        this.frameworkUtil = frameworkUtil;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // get the request context
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        
        String uri = request.getRequestURI();
        
        // skip server context path and build the path to the resource we are looking for
        uri = uri.substring(request.getContextPath().length());
        
        // validate and return the resource path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String controller = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String mode = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String command = t.nextToken();
        
        // load additional arguments, if any
        ArrayList<String> args = new ArrayList<String>();
        if (t.hasMoreTokens())
        {
            args.add(t.nextToken());            
        }
                
        // CACHE
        if (MODE_CACHE.equals(mode))
        {
            if (MODE_CACHE_COMMAND_INVALIDATE.equals(command))
            {
                // invalidate the model service object cache
                CacheUtil.invalidateModelObjectServiceCache(context);
            }
        }
        
        // WEBSCRIPTS
        if (MODE_WEBSCRIPTS.equals(mode))
        {
            if (MODE_WEBSCRIPTS_COMMAND_RESET.equals(command))
            {
                this.frameworkUtil.resetWebScripts();
            }
        }
        
        return null;
    }
}
