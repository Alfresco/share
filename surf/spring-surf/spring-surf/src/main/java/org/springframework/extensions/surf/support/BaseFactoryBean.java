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

package org.springframework.extensions.surf.support;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * <p>Foundation class for factory beans.</p>
 * 
 * @author muzquiano
 * @author David Draper
 */
public abstract class BaseFactoryBean
{
    /**
     * @deprecated Because it was only ever used as a way of obtaining the other <code>WebFrameworkConfigElement</code>, 
     * <code>ModelObjectService</code> and <code>ResourceService</code> references. These are made available when using
     * the non-deprecated constructor. 
     */
    private WebFrameworkServiceRegistry serviceRegistry = null;
    
    /**
     * TODO: Provide a description of the WebFrameworkConfigElement
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * TODO: Provide a description of the ModelObjectService
     */
    private ModelObjectService modelObjectService;
    
    /**
     * TODO: Provide a description of the ResourceService
     */
    private ResourceService resourceService;

    /**
     * <p>This constructor has been deprecated as it relies on the <code>WebFrameworkServiceRegistry</code> argument
     * to access all the required Spring beans rather than using a properly configured Spring application context.</p>
     * @param serviceRegistry
     * @deprecated
     */
    public BaseFactoryBean(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.webFrameworkConfigElement = serviceRegistry.getWebFrameworkConfiguration();
        this.modelObjectService = serviceRegistry.getModelObjectService();
        this.resourceService = serviceRegistry.getResourceService();
    }
        
    /**
     * <p>This is the preferred constructor for subclasses to call when being instantiated as it sets the 
     * required Spring beans directly rather than relying on them being obtained from the <code>WebFrameworkServiceRegistry</code>
     * supplied to the deprecated constructor.</p>
     * 
     * @param webFrameworkConfigElement
     * @param modelObjectService
     * @param resourceService
     */
    public BaseFactoryBean(WebFrameworkConfigElement webFrameworkConfigElement,
                           ModelObjectService modelObjectService,
                           ResourceService resourceService)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
        this.modelObjectService = modelObjectService;
        this.resourceService = resourceService;
    }
    
    /**
     * Gets the service registry.
     * 
     * @return the service registry
     */
    public WebFrameworkServiceRegistry getServiceRegistry()
    {
        return this.serviceRegistry;
    }

    /**
     * Gets the web framework configuration.
     * 
     * @return the web framework configuration
     */
    public WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return this.webFrameworkConfigElement;
    }    
    
    /**
     * Gets the model object service
     * 
     * @return model object service
     */
    public ModelObjectService getObjectService()
    {
        return this.modelObjectService;
    }
    
    /**
     * Gets the resource service.
     * 
     * @return the resource service
     */
    public ResourceService getResourceService()
    {
        return this.resourceService;
    }
}
