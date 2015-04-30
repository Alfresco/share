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
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.LinkBuilderFactory;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * <p>Abstract base class for LinkBuilderFactory implementations.  This
 * is provided as a convenience to developers who wish to build their
 * own custom LinkBuilderFactory variations.</p>
 * 
 * @author muzquiano
 * @author David Draper
 */
public abstract class AbstractLinkBuilderFactory extends BaseFactory implements LinkBuilderFactory
{
    /**
     * <p>Extending classes must implement this method to instantiate the associated class of <code>LinkBuilder</code>.</p>
     */
    public abstract LinkBuilder newInstance();
    
    /**
     * <p>A <code>WebFrameworkConfigElement</code> is required in the <code>AbstractLinkBuilder</code> methods so 
     * should be provided as to the <code>AbstractLinkBuilderFactory</code> as a Spring bean property.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
       
    /**
     * <p>A <code>ModelObjectService</code> is required in the <code>AbstractLinkBuilder</code> methods so 
     * should be provided as to the <code>AbstractLinkBuilderFactory</code> as a Spring bean property.</p>
     */
    private ModelObjectService modelObjectService;
    
    /**
     * <p>A <code>ResourceService</code> is required in the <code>AbstractLinkBuilder</code> methods so 
     * should be provided as to the <code>AbstractLinkBuilderFactory</code> as a Spring bean property.</p>
     */
    private ResourceService resourceService;
    
    /**
     * <p>This method is supplied so that subclasses can get a reference to the <code>WebFrameworkConfigElement</code>
     * that is required to instantiate a <code>AbstractLinkBuilder</code>.</p>
     * @return
     */
    public WebFrameworkConfigElement getWebFrameworkConfigElement()
    {
        return webFrameworkConfigElement;
    }

    /**
     * <p>This method is supplied so that subclasses can get a reference to the <code>ModelObjectService</code>
     * that is required to instantiate a <code>AbstractLinkBuilder</code>.</p>
     * @return
     */
    public ModelObjectService getModelObjectService()
    {
        return modelObjectService;
    }

    /**
     * <p>This method is supplied so that subclasses can get a reference to the <code>ResourceService</code>
     * that is required to instantiate a <code>AbstractLinkBuilder</code>.</p>
     * @return
     */
    public ResourceService getResourceService()
    {
        return resourceService;
    }

    /**
     * This method is provided to allow Spring to set the <code>WebFrameworkConfigElement</code> as a bean property.</p>
     * @param webFrameworkConfigElement
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    /**
     * This method is provided to allow Spring to set the <code>ModelObjectService</code> as a bean property.</p>
     * @param modelObjectService
     */
    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    /**
     * This method is provided to allow Spring to set the <code>ResourceService</code> as a bean property.</p>
     * @param resourceService
     */
    public void setResourceService(ResourceService resourceService)
    {
        this.resourceService = resourceService;
    }
}
