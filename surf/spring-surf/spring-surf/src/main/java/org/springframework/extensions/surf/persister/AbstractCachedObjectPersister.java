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

package org.springframework.extensions.surf.persister;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.cache.ContentCache;

/**
 * Extends the abstract implementation by offering the basics of a caching layer.
 *
 * @author muzquiano
 * @author kevinr
 */
public abstract class AbstractCachedObjectPersister extends AbstractObjectPersister implements CachedPersister
{
    private final static Log logger = LogFactory.getLog(AbstractCachedObjectPersister.class);

    protected final static String GLOBAL_STORE_ID_SUFFIX = ":_global";

    final protected Map<String, ContentCache<ModelObject>> caches;

    protected boolean useCache = true;
    protected long cacheDelay = -1L;
    protected int cacheMaxSize = 10240;


    /**
     * Constructor
     */
    public AbstractCachedObjectPersister()
    {
        this.caches = new HashMap<String, ContentCache<ModelObject>>(128);
    }

    /**
     * Gets the cache for a particular model persistence context
     *
     * @param context       ModelPersistenceContext
     * @param bucket        Cache bucket to pick
     *
     * @return the cache
     */
    protected abstract ContentCache<ModelObject> getCache(ModelPersistenceContext context, String bucket);

    /**
     * Creates a new cache.
     *
     * @return the cache
     */
    protected abstract ContentCache<ModelObject> createCache();

    /**
     * Places an object into this persister's cache.
     *
     * @param context the context
     * @param id ID of the object in the cache
     * @param obj the obj
     */
    protected void cachePut(ModelPersistenceContext context, ModelObject obj)
    {
        if (this.useCache)
        {
            if (logger.isDebugEnabled())
                logger.debug("Put into cache: " + obj.getId());

            String typeId = obj.getTypeId();
            String objId = obj.getId();
            ContentCache<ModelObject> cache = getCache(context, typeId);
            cache.put(objId, obj);
        }
    }

    /**
     * Removes an object from the cache
     *
     * @param context the context
     * @param objectTypeId
     * @param objectId
     */
    protected void cacheRemove(ModelPersistenceContext context, ModelObject obj)
    {
        if (this.useCache)
        {
            if (logger.isDebugEnabled())
                logger.debug("Remove from cache: " + obj.getId());

            getCache(context, obj.getTypeId()).remove(obj.getId());
        }
    }

    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCache(boolean)
     */
    public void setCache(boolean useCache)
    {
        this.useCache = useCache;
    }

    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCacheCheckDelay(int)
     */
    public void setCacheCheckDelay(int cacheCheckDelay)
    {
        this.cacheDelay = (cacheCheckDelay * 1000L);
    }
    
    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCacheMaxSize(int)
     */
    public void setCacheMaxSize(int cacheMaxSize)
    {
        this.cacheMaxSize = cacheMaxSize;
    }

    /**
     * @see org.springframework.extensions.surf.persister.CachingPersister#invalidateCache()
     */
    public synchronized void invalidateCache()
    {
        for (ContentCache<ModelObject> cache: caches.values())
        {
            cache.invalidate();
        }
    }
}
