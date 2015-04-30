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
import org.springframework.extensions.surf.cache.BasicCache;
import org.springframework.extensions.surf.exception.ResourceLoaderException;

/**
 * Provides caching of resource objects by object id
 * 
 * @author muzquiano
 */
public abstract class AbstractCachingResourceLoader extends AbstractResourceLoader
{
    private BasicCache<Resource> cache = null;
    private long cacheTimeout = 1000*60*5; // five minutes
    
    /**
     * Sets the cache timeout.
     * 
     * @param cacheTimeout the new cache timeout
     */
    public void setCacheTimeout(long cacheTimeout)
    {
        this.cacheTimeout = cacheTimeout;         
    }
    
    /**
     * Instantiates a new abstract resource loader
     * 
     * @param protocolId the protocol id
     * @param endpointId the endpoint id
     */
    public AbstractCachingResourceLoader(String protocolId, String endpointId, FrameworkBean frameworkUtil)
    {
        super(protocolId, endpointId, frameworkUtil);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceLoader#load(java.lang.String)
     */
    public synchronized Resource load(String objectId)
        throws ResourceLoaderException
    {
        if (cache == null)
        {
            // init with a five minute cache
            cache = new BasicCache<Resource>(this.cacheTimeout);
        }
        
        Resource resource = (Resource) cache.get(objectId);
        if (resource == null)
        {
            resource = buildResource(objectId);
            cache.put(objectId, resource);
        }
        
        return resource;
    }
    
    /**
     * Builds the resource bound to the given object id
     * 
     * @param objectId
     * 
     * @return resource
     */
    public abstract Resource buildResource(String objectId)
        throws ResourceLoaderException;
}
