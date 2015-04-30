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

/**
 * Abstract implementation of a resource.  Useful for developers
 * who wish to implement new resource loaders and resource types.
 * 
 * ResourceIds arrive in the following format:
 * 
 * <protocolId>://<endpointId>/<objectId>
 * 
 * @author muzquiano
 */
public abstract class AbstractResource implements Resource
{
    protected String name = null;
    protected String protocolId = null;
    protected String objectId = null;
    protected String endpointId = null;
    protected byte[] bytes = null;
    protected FrameworkBean frameworkUtil;
        
    public AbstractResource(String protocolId, String endpointId, String objectId, FrameworkBean frameworkUtil)
    {
        this.protocolId = protocolId;
        this.endpointId = endpointId;
        this.objectId = objectId;
        this.frameworkUtil = frameworkUtil;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getResourceId()
     */
    public String getResourceId()
    {
        String resourceId = getProtocolId() + "://" + getEndpointId();
        if (getObjectId() != null)
        {
            resourceId = resourceId + '/' + getObjectId();
        }
        
        return resourceId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getResourceProtocol()
     */
    public String getProtocolId()
    {
        return this.protocolId;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getObjectId()
     */
    public String getObjectId()
    {
        return this.objectId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#setObjectId(java.lang.String)
     */
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#setEndpoint(java.lang.String)
     */
    public void setEndpointId(String endpointId)
    {
        this.endpointId = endpointId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getEndpoint()
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }      
}
