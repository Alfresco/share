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

import org.springframework.extensions.surf.support.BaseFactory;

/**
 * Abstract class that provides caching of resource loaders
 * for endpoints
 * 
 * @author muzquiano
 */
public abstract class AbstractResourceLoaderFactory extends BaseFactory implements ResourceLoaderFactory
{
    protected int order = 0;
    
    /**
     * Sets the order.
     * 
     * @param order the new order
     */
    public void setOrder(int order)
    {
        this.order = order;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceLoaderFactory#getOrder()
     */
    public int getOrder()
    {
        return this.order;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractCachingResourceLoaderFactory#canHandle(java.lang.String)
     */
    public boolean canHandle(String protocolId)
    {
        boolean canHandle = false;

        if (protocolId != null)
        {
            String[] protocols = this.getSupportedProtocols();
            for (int i = 0; i < protocols.length; i++)
            {
                if (protocolId.equalsIgnoreCase(protocols[i]))
                {
                    canHandle = true;
                }
            }
        }

        return canHandle;
    }    
    
    /**
     * Gets the supported protocols.
     * 
     * @return the supported protocols
     */
    public abstract String[] getSupportedProtocols();    
}
