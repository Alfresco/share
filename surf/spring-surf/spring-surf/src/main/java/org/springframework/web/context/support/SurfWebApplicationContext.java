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

package org.springframework.web.context.support;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Custom web application context object that is utilized by Alfresco Surf
 * to provide resource loading capabilities on top of Alfresco Stores.
 * 
 * @author muzquiano
 */
public class SurfWebApplicationContext extends XmlWebApplicationContext implements ConfigurableWebApplicationContext, ServletContextListener 
{
    private static Log logger = LogFactory.getLog(SurfWebApplicationContext.class);
    
    private String searchPathBeanId = "webframework.webflow.searchpath";
    
    private SearchPath searchPath;
    
    /**
     * Constructs a new Surf web application context
     */
    public SurfWebApplicationContext() 
    {
        setDisplayName("Surf WebApplicationContext");
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)
    {        
        this.searchPathBeanId = event.getServletContext().getInitParameter("searchpath");
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)
    {        
    }
    
    /**
     * Returns the search path object
     * 
     * @return search path
     */
    protected SearchPath getSearchPath()
    {
        if (this.searchPath == null)
        {
            try
            {
                // TODO: fix this
                /*
                ApplicationContext rootApplicationContext = FrameworkHelper.getApplicationContext();
                if (rootApplicationContext != null)
                {
                    this.searchPath = (SearchPath) rootApplicationContext.getBean(this.searchPathBeanId);
                }
                */
            }
            catch(Exception e) { }
        }
        
        return this.searchPath;        
    }
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.support.AbstractRefreshableWebApplicationContext#onRefresh()
     */
    protected void onRefresh() 
    {
        // TODO: insert additional Surf-specific logic here (if needed)
        
        // do the theme thing on the super class
        super.onRefresh();
    }
    
    

    /**
     * @ResourceLoader
     * Retrieves a resource at a given location
     * 
     * If the resource is not a classpath resource, then the search
     * path is consulted.
     * 
     * If a match cannot be found, the superclass method is consulted.
     */
    public Resource getResource(String location) 
    {
        Resource resource = null;
        
        if (getSearchPath() != null)
        {
            resource = getResourceFromSearchPath(location);
        }
        else
        {
            resource = super.getResource(location);
        }
        
        return resource;
    }

    /**
     * This implementation supports file paths beneath the root of the ServletContext.
     * @see ServletContextResource
     */
    protected Resource getResourceByPath(String path) 
    {
        Resource resource = null;
        
        if (getSearchPath() != null)
        {
            resource = getResourceFromSearchPath(path);
        }
        else
        {
            resource = super.getResourceByPath(path);
        }
        
        return resource;
    }
    
    
    /**
     * @ResourcePatternResolver
     * Resolve the given location pattern into Resource objects.
     * 
     * Resources may either be conventional servlet context resources or they may
     * additionally be remote store resources (Alfresco).
     * 
     * @param locationPattern the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException in case of I/O errors
     */
    public Resource[] getResources(String locationPattern) throws IOException
    {
        Resource[] resources = null;
        
        if (getSearchPath() != null)
        {
            ArrayList<Resource> list = new ArrayList<Resource>();
            for (Store apiStore : getSearchPath().getStores())
            {
                String[] paths = apiStore.getDocumentPaths("", locationPattern);

                if (paths != null && paths.length > 0)
                {
                    for (int i = 0; i < paths.length; i++)
                    {
                        Resource resource = new StoreResource(apiStore, paths[i]);
                        list.add(resource);
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Found Spring Resource '" + paths[i] + " in store: " + apiStore.getClass().getSimpleName() + "(" + apiStore.getBasePath() + ")");                                                
                    }
                }
            }
            
            // convert list to resource array            
            resources = list.toArray(new Resource[list.size()]);
        }
        else
        {
            if (locationPattern.indexOf(":") > -1)
            {
                resources = super.getResources(locationPattern);
            }
        }
        
        return resources;
    }

    
    /**
     * Retrieves the resource with the given location string from
     * the current search path.
     * 
     * If the resource is a found, a StoreResource instance is returned.
     * 
     * @param location the location
     * 
     * @return the store resource
     */
    protected Resource getResourceFromSearchPath(String location)
    {
        Resource resource = null;
        
        if (getSearchPath() != null)
        {
           for (Store apiStore : getSearchPath().getStores())
           {
               String path = location;
               
               try
               {
                   if (apiStore.hasDocument(path))
                   {
                       if (logger.isDebugEnabled())
                           logger.debug("Found Spring Resource '" + location + " in store: " + apiStore.getClass().getSimpleName() + "(" + apiStore.getBasePath() + ")");
                       
                       resource = new StoreResource(apiStore, path);
                   }
               }
               catch (IOException ioe)
               {
                   throw new PlatformRuntimeException("Unable to get resource: " + location, ioe);
               }
           }
        }
        
        return resource;
    }
}
