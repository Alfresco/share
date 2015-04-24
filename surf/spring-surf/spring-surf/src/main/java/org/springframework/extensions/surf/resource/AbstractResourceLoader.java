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

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;

/**
 * Abstract class that developers can extend to build their own custom resource
 * loaders.
 * 
 * @author muzquiano
 */
public abstract class AbstractResourceLoader implements ResourceLoader
{
    private WebFrameworkServiceRegistry serviceRegistry;
    
    protected String endpointId;
    protected String protocolId;
    protected FrameworkBean frameworkUtil;
    
    /**
     * Instantiates a new abstract resource loader
     * 
     * @param protocolId the protocol id
     * @param endpointId the endpoint id
     */
    public AbstractResourceLoader(String protocolId, String endpointId, FrameworkBean frameworkUtil)
    {
        this.protocolId = protocolId;
        this.endpointId = endpointId;
        this.frameworkUtil = frameworkUtil;
    }
    
    /**
     * Sets the service registry.
     * 
     * @param serviceRegistry the new service registry
     */
    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
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
     * Spring initialization method
     */
    public void init()
    {
    }    

    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }
    
    /**
     * Gets the protocol id.
     * 
     * @return the protocol id
     */
    public String getProtocolId()
    {
        return this.protocolId;
    }
}
