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
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * The servlet implementation of LinkBuilderFactory
 * 
 * @author muzquiano
 */
public class ServletLinkBuilderFactory extends AbstractLinkBuilderFactory
{
    protected String pageUri;
    protected String pageTypeUri;
    protected String objectUri;
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.support.AbstractLinkBuilderFactory#newInstance()
     */
    public LinkBuilder newInstance()
    {
        WebFrameworkConfigElement webFrameworkConfigElement = getWebFrameworkConfigElement();
        ModelObjectService modelObjectService = getModelObjectService();
        ResourceService resourceService = getResourceService();
        ServletLinkBuilder linkBuilder = new ServletLinkBuilder(webFrameworkConfigElement, modelObjectService, resourceService);
        linkBuilder.setPageTypeUri(pageTypeUri);
        linkBuilder.setPageUri(pageUri);        
        return linkBuilder;
    }
    
    /**
     * Specifies the uri base for dispatching to pages
     * 
     * @param pageUri
     */
    public void setPageUri(String pageUri)
    {
        this.pageUri = pageUri;
    }
    
    /**
     * Specifies the uri base for dispatching to page types
     * 
     * @param pageTypeUri
     */
    public void setPageTypeUri(String pageTypeUri)
    {
        this.pageTypeUri = pageTypeUri;
    }
    
    /**
     * Specifies the uri base for dispatching to objects
     * 
     * @param objectUri
     */
    public void setObjectUri(String objectUri)
    {
        this.objectUri = objectUri;
    }
}
