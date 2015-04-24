/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.impl.cache;

import java.io.Serializable;
import java.util.Collection;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;


/**
 * A thin adapter for <b>Ehcache</b> support.
 * <p>
 * Thread-safety is taken care of by the underlying <b>Ehcache</b>
 * instance.
 *
 * @see org.springframework.cache.ehcache.EhCacheFactoryBean
 * @see org.springframework.cache.ehcache.EhCacheManagerFactoryBean
 * 
 * @author Derek Hulley
 */
public class EhCacheAdapter<K extends Serializable, V extends Object>
        implements SimpleCache<K, V>
{
    private net.sf.ehcache.Cache cache;
    
    public EhCacheAdapter()
    {
    }

    /**
     * @param cache the backing Ehcache instance
     */
    public void setCache(Cache cache)
    {
        this.cache = cache;
    }

    public boolean contains(K key)
    {
        try
        {
            return (cache.get(key) != null);
        }
        catch (CacheException e)
        {
            throw new org.alfresco.wcm.client.impl.cache.CacheException("contains failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<K> getKeys()
    {
        return cache.getKeys();
    }

    @SuppressWarnings("unchecked")
    public V get(K key)
    {
        try
        {
            Element element = cache.get(key);
            if (element != null)
            {
                return (V) element.getObjectValue();
            }
            else
            {
                return null;
            }
        }
        catch (IllegalStateException ie)
        {
           throw new org.alfresco.wcm.client.impl.cache.CacheException("Failed to get from EhCache as state invalid: \n" +
                 "  state: " + cache.getStatus() + "\n" +
                 "   key: " + key,
                 ie);
        }
        catch (CacheException e)
        {
            throw new org.alfresco.wcm.client.impl.cache.CacheException("Failed to get from EhCache: \n" +
                    "   key: " + key,
                    e);
        }
    }

    public void put(K key, V value)
    {
        Element element = new Element(key, value);
        cache.put(element);
    }

    public void remove(K key)
    {
        cache.remove(key);
    }

    public void clear()
    {
        cache.removeAll();
    }
}
