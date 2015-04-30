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

package org.springframework.extensions.surf.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * This is an implementation of a purely in-memory cache that uses a
 * HashMap to provide a basic form of caching.
 * 
 * This class is thread safe for concurrent usage of the cache.
 * 
 * @author muzquiano
 * 
 * @deprecated This class provides poor performance due to the heavy use of synchronized methods.
 */
public class BasicCache<K> implements ContentCache<K>
{
    protected final HashMap<String, CacheItem<K>> cache;
    protected final long timeout;
    
    /**
     * Instantiates a new basic cache.
     * 
     * @param timeout   the timeout
     */
    public BasicCache(long timeout)
    {
        this(timeout, 256);
    }
    
    /**
     * Instantiates a new basic cache.
     * 
     * @param timeout   the timeout
     * @param size      Cache size
     */
    public BasicCache(long timeout, int size)
    {
        this.timeout = timeout;
        if (size < 1)
        {
            throw new IllegalArgumentException("Cache size must be +ve value");
        }
        this.cache = new HashMap<String, CacheItem<K>>(size);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#get(java.lang.String)
     */
    public synchronized K get(String key)
    {
        // get the content item from the cache
        CacheItem<K> item = cache.get(key);
        
        // if the cache item is null, return right away
        if (item == null)
        {
            return null;
        }
        else
        {
            // ask the cache item if it's still valid
            if (item.isExpired())
            {
                // it's not valid, throw it away
                remove(key);
                return null;
            }
            
            return item.object;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#remove(java.lang.String)
     */
    public synchronized void remove(String key)
    {
        if (key == null)
        {
            return;
        }
        cache.remove(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#put(java.lang.String, java.lang.Object)
     */
    public synchronized void put(String key, K obj)
    {
        put(key, obj, timeout);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#put(java.lang.String, java.lang.Object, long)
     */
    public synchronized void put(String key, K obj, long timeout)
    {
        // create the cache item
        CacheItem<K> item = new CacheItem<K>(key, obj, timeout);
        cache.put(key, item);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#invalidate()
     */
    public synchronized void invalidate()
    {
        cache.clear();
    }
    
    /**
     * Returns the values for the items in the cache
     * 
     * @return the collection
     */
    public Collection<CacheItem<K>> values()
    {
        return this.cache.values();
    }
    
    /**
     * Returns keys for the items in the cache
     * 
     * @return the set
     */
    public Set<String> keys()
    {
        return this.cache.keySet();
    }
}
