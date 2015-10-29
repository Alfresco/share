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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.types.AbstractModelObject;
import org.springframework.extensions.webscripts.Store;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

/**
 * Cache for ModelObject instances. Based on an underlying ConcurrentHashMap
 * implementation that is resposible for handling multi-threaded access to
 * the cache.
 * <p>
 * The delay between checks against the underlying store is configurable.
 * 
 * @since 1.1
 * Improved version using ConcurrentLinkedHashmap to discard old items.
 * 
 * @author kevinr
 * @author dward
 */
public class ModelObjectCache implements ContentCache<ModelObject>
{
    private static final int MAX_SIZE = -1;
    
    protected final Store store;
    protected final long delay;
    protected final int maxSize;
    protected final Map<String, CacheItem<ModelObject>> cache;
    
    
    /**
     * Instantiates a new model object cache.
     * 
     * @param store     the store
     * @param delay     the delay to check modified dates for items in the cache
     */
    public ModelObjectCache(Store store, long delay)
    {
        this(store, MAX_SIZE, delay);
    }
    
    /**
     * Instantiates a new model object cache.
     * 
     * @param store     the store
     * @param maxSize   the maxSize
     * @param delay     the delay to check modified dates for items in the cache
     */
    public ModelObjectCache(Store store, int maxSize, long delay)
    {
        this.delay = delay;
        this.store = store;
        this.maxSize = maxSize;
        if (maxSize != -1)
        {
            // maximum size of the cache means we use a Linked Concurrent HashMap which is
            // responsible for discarding items from the cache when it hits capacity
            cache = new ConcurrentLinkedHashMap.Builder<String, CacheItem<ModelObject>>()
                 .maximumWeightedCapacity(maxSize)
                 .concurrencyLevel(32)
                 .weigher(Weighers.singleton())
                 .build();
        }
        else
        {
            // no maximum size specified - this cache uses a standard Concurrent HashMap
            cache = new ConcurrentHashMap<String, CacheItem<ModelObject>>(1024, 0.75f, 32);
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.cache.ContentCache#get()
     */
    public ModelObject get(String key)
    {
        ModelObject obj = null;
        CacheItem<ModelObject> item = this.cache.get(key);
        if (item != null)
        {
            synchronized (item)
            {
                obj = item.object;
                
                // check for validity of the object
                if (this.delay >= 0L)
                {
                    // get the content item from the cache
                    long now = System.currentTimeMillis();
                    if (this.delay < now - item.lastChecked)
                    {
                        // delay hit - check cached item
                        // modification time of our model object
                        // check the modification time in the store
                        item.lastChecked = now;
                        if (obj != ModelObjectSentinel.instance)
                        {
                            String path = obj.getStoragePath();
                            try
                            {
                                if (this.store.lastModified(path) > obj.getModificationTime())
                                {
                                    // the in-memory copy is stale, remove from cache
                                    remove(key);
                                    obj = null;
                                }
                            }
                            catch (IOException ex)
                            {
                                // unable to access the timestamp in the store
                                // could be many reasons but lets assume the worst case
                                // the file may have been deleted, so remove from the cache
                                remove(key);
                                obj = null;
                            }
                        }
                        else
                        {
                            // no point checking the store as this was a sentinel
                            remove(key);
                            obj = null;
                        }
                    }
                }
            }
        }
        return obj;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.cache.ContentCache#invalidate()
     */
    public void invalidate()
    {
        cache.clear();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.cache.ContentCache#put(java.lang.String, java.lang.Object)
     */
    public void put(String key, ModelObject obj)
    {
        put(key, obj, -1L);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.cache.ContentCache#put(java.lang.String, java.lang.Object, long)
     */
    public void put(String key, ModelObject obj, long timeout)
    {
        // create the cache item
        CacheItem<ModelObject> item = new CacheItem<ModelObject>(key, obj, timeout);
        cache.put(key, item);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.cache.ContentCache#remove(java.lang.String)
     */
    public void remove(String key)
    {
        if (key == null)
        {
            return;
        }
        cache.remove(key);
    }
    
    /**
     * Returns the values for the items in the cache
     * 
     * @return the collection
     */
    public Collection<CacheItem<ModelObject>> values()
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


    /**
     * Singleton sentinel class used to represent a 'null' value in some cache implementations
     *  
     * @author Kevin Roast
     */
    public static class ModelObjectSentinel extends AbstractModelObject
    {
        private static ModelObjectSentinel instance = new ModelObjectSentinel();
        
        private ModelObjectSentinel()
        {
        }
        
        public static ModelObjectSentinel getInstance()
        {
            return instance;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.framework.AbstractModelObject#getTypeId()
         */
        @Override
        public String getTypeId()
        {
            return "ModelObjectSentinel";
        }
    }
}
