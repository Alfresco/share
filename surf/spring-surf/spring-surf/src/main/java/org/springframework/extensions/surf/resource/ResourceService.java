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

package org.springframework.extensions.surf.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.ContentLoaderException;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.resource.support.VirtualizedWebappResourceLoaderFactory;

/**
 * Resource service for web framework
 * 
 * @author muzquiano
 */
public class ResourceService implements ApplicationContextAware, ApplicationListener
{
    private static final Log logger = LogFactory.getLog(ResourceService.class);
        
    private ApplicationContext applicationContext;
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry;
    private List<ResourceLoaderFactory> resourceLoaderFactories;

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

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
     * Gets the web framework configuration.
     * 
     * @return the web framework configuration
     */
    public WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return getServiceRegistry().getWebFrameworkConfiguration();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;            
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(this.applicationContext))
            {
                onBootstrap();
            }
        }
    }
    
    /**
     * Initialization of Resource Service
     */
    protected void onBootstrap()
    {
        // pre-load resource loaders
        // find all resource loader factories in the application context
        Map<String, ResourceLoaderFactory> resourceLoaderFactoryBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, ResourceLoaderFactory.class, true, false);
        
        if (!resourceLoaderFactoryBeans.isEmpty()) 
        {
            this.resourceLoaderFactories = new ArrayList<ResourceLoaderFactory>(resourceLoaderFactoryBeans.values());
            
            // add in the default virtualized web application resource loader
            // place it automatically at the end
            VirtualizedWebappResourceLoaderFactory webappLoader = new VirtualizedWebappResourceLoaderFactory();
            webappLoader.setOrder(9999); // high number
            webappLoader.setCacheTimeout(0);
            this.resourceLoaderFactories.add(webappLoader);
            
            // keep resource loader factories in sorted order
            OrderComparator.sort(this.resourceLoaderFactories);
        }
        else
        {
            // empty list
            this.resourceLoaderFactories = new ArrayList<ResourceLoaderFactory>();
        }
    }
    
    /**
     * Gets the resource for the given resource id
     * 
     * @param resourceId the resource id
     * 
     * @return the resource
     * 
     * @throws ResourceLoaderException the resource loader exception
     */
    public Resource getResource(String resourceId)
        throws ResourceLoaderException
    {
        String[] ids = getResourceDescriptorIds(resourceId);        
        return getResource(ids[0], ids[1], ids[2]);
    }
    
    /**
     * Gets a resource for the given protocol, endpoint and object id
     * 
     * @param protocolId
     * @param endpointId
     * @param objectId
     * 
     * @return the resource
     */
    public Resource getResource(String protocolId, String endpointId, String objectId)
        throws ResourceLoaderException
    {
        Resource resource = null;

        // determine the best fit resource loader for the given object and endpoint
        ResourceLoader resourceLoader = getResourceLoader(protocolId, endpointId);
        if (resourceLoader != null)
        {
            // load the resource from the resource loader
            resource = resourceLoader.load(objectId);
        }
        
        return resource;        
    }

    /**
     * Returns an appropriate resource loader for the given
     * protocol and endpoint
     * 
     * @param protocolId
     * @param endpointId
     * 
     * @return
     */
    public ResourceLoader getResourceLoader(String protocolId, String endpointId)
    {
        ResourceLoader resourceLoader = null;
        
        // find the resource loader factory that can provide
        // us with a loader for this object id
        ResourceLoaderFactory resourceLoaderFactory = getResourceLoaderFactory(protocolId);
        if (resourceLoaderFactory != null)
        {
            resourceLoader = resourceLoaderFactory.getResourceLoader(protocolId, endpointId);
        }
        
        return resourceLoader;
    }
    
    /**
     * Returns a resource loader factory for the given protocol
     * 
     * @param protocolId
     * 
     * @return resource loader factory
     */
    public ResourceLoaderFactory getResourceLoaderFactory(String protocolId)
    {
        ResourceLoaderFactory factory = null;
        
        for (ResourceLoaderFactory resourceLoaderFactory: this.resourceLoaderFactories)
        {
            if (resourceLoaderFactory.canHandle(protocolId))
            {
                factory = resourceLoaderFactory;
                break;
            }
        }
        
        return factory;
    }

    /**
     * Retrieves the content payload for the described resource
     * 
     * @param resourceId
     * @return
     * @throws ResourceLoaderException
     * @throws ContentLoaderException
     */    
    public ResourceContent getResourceContent(String resourceId)
        throws IOException, ResourceLoaderException
    {
        ResourceContent content = null;
        
        // get the resource
        Resource resource = getResource(resourceId);
        if (resource != null)
        {
            content = resource.getContent();
        }
        
        return content;
    
    }
    
    /**
     * Retrieves the content payload for the described resource
     * 
     * @param protocolId
     * @param objectId
     * @param endpointId
     * @return
     * @throws ResourceLoaderException
     * @throws ContentLoaderException
     */    
    public ResourceContent getResourceContent(String protocolId, String endpointId, String objectId)
        throws IOException, ResourceLoaderException
    {
        ResourceContent content = null;
        
        // get the resource
        Resource resource = getResource(protocolId, endpointId, objectId);
        if (resource != null)
        {
            content = resource.getContent();
        }
        
        return content;
    }

    /**
     * Retrieves the metadata payload for the described resource
     * 
     * @param resourceId
     * @return
     * @throws ResourceLoaderException
     * @throws ContentLoaderException
     */    
    public ResourceContent getResourceMetadata(String resourceId)
        throws IOException, ResourceLoaderException
    {
        ResourceContent content = null;
        
        // get the resource
        Resource resource = getResource(resourceId);
        if (resource != null)
        {
            content = resource.getMetadata();
        }
        
        return content;
    }

    public ResourceContent getResourceMetadata(String protocolId, String endpointId, String objectId)
        throws IOException, ResourceLoaderException
    {
        ResourceContent content = null;
        
        // get the resource
        Resource resource = getResource(protocolId, endpointId, objectId);
        if (resource != null)
        {
            content = resource.getMetadata();
        }
        
        return content;
    }
    
    /**
     * Returns an array of the constituent parts of a resource id -
     * the protocol id, the endpoint id and the object id.
     * 
     * @param resourceId the resource id
     * 
     * @return the resource ids
     */
    public String[] getResourceDescriptorIds(String resourceId)
    {
        // protocol, endpoint, object
        String[] parts = new String[3];
        
        // break up the resource id
        int x = resourceId.indexOf("://");
        if (x > -1)
        {
            parts[0] = resourceId.substring(0, x);
            
            String cdr = resourceId.substring(x + 3);
            int y = cdr.indexOf('/');
            if (y > -1)
            {
                parts[1] = cdr.substring(0, y);
                parts[2] = cdr.substring(y+1);
            }
            
            // cleanup
            if (parts[0].length() == 0)
            {
                parts[0] = null;
            }
            if (parts[1].length() == 0)
            {
                parts[1] = null;
            }
            if (parts[2].length() == 0)
            {
                parts[2] = null;
            }
            
        }
        else
        {
            if (resourceId.startsWith("/"))
            {
                resourceId = resourceId.substring(1);                
            }
            
            // assume it is a virtualized web application reference
            parts[0] = "webapp";
            parts[1] = null;
            parts[2] = resourceId;            
        }
        
        return parts;        
    }
}
