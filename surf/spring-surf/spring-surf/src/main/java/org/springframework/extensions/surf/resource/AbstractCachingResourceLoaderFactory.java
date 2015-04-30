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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.cache.BasicCache;

/**
 * Abstract class that provides caching of resource loaders
 * for endpoints
 * 
 * @author muzquiano
 */
public abstract class AbstractCachingResourceLoaderFactory extends AbstractResourceLoaderFactory
{
    private static final Log logger = LogFactory.getLog(AbstractCachingResourceLoaderFactory.class);
    
    private BasicCache<ResourceLoader> cache = null;
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
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceLoaderFactory#getResourceLoader(java.lang.String, java.lang.String)
     */
    public synchronized ResourceLoader getResourceLoader(String protocolId, String endpointId)
    {
        if (cache == null)
        {
            // init with a five minute cache
            cache = new BasicCache<ResourceLoader>(this.cacheTimeout);
        }
        
        String cacheKey = getCacheKey(protocolId, endpointId);
        
        ResourceLoader resourceLoader = (ResourceLoader) cache.get(cacheKey);
        if (resourceLoader == null)
        {
            resourceLoader = buildResourceLoader(protocolId, endpointId);
            cache.put(cacheKey, resourceLoader);
        }
        
        return resourceLoader;
    }
    
    private String getCacheKey(String protocolId, String endpointId)
    {
        StringBuilder key = new StringBuilder();
        
        key.append((protocolId != null ? protocolId : "null"));
        key.append("_");
        key.append((endpointId != null ? endpointId : "null"));
        
        return key.toString();
    }
    
    public abstract ResourceLoader buildResourceLoader(String protocolId, String endpointId);
}
