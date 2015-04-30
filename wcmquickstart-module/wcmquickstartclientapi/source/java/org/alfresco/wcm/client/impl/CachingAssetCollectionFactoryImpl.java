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
package org.alfresco.wcm.client.impl;

import java.util.Date;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.AssetCollectionFactory;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Resource;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachingAssetCollectionFactoryImpl implements AssetCollectionFactory
{
    private static final Log log = LogFactory.getLog(CachingAssetCollectionFactoryImpl.class);
    
    private AssetCollectionFactory delegate;
    private SimpleCache<String, CacheEntry> cache;
    private long minimumCacheMilliseconds = 30000L;
    private AssetFactory assetFactory;

    public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults)
    {
        AssetCollection result = null;
        String cacheKey = sectionId + "/" + collectionName;
        CacheEntry cacheEntry = cache.get(cacheKey);
        long now = System.currentTimeMillis();
        long earliestPermittedCacheTime = now - getMinimumCacheMilliseconds();
        
        AssetCollectionImpl cachedAssetCollection;
        if (cacheEntry == null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Failed to find asset collection in cache: " + cacheKey);
            }
            result = delegate.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
            cachedAssetCollection = copyAssetCollection(result);
            if (cachedAssetCollection != null)
            {
                cache.put(cacheKey, new CacheEntry(cachedAssetCollection));
            }
        }
        else
        {
            cachedAssetCollection = cacheEntry.assetCollection;
            if (log.isDebugEnabled())
            {
                log.debug("Found an asset collection in the cache. now == " + now + "; cache time == " + cacheEntry.cacheTime);
            }
            if (cacheEntry.cacheTime < earliestPermittedCacheTime)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Checking whether we need to reload asset collection");
                }
                //Set the cache time to now - other threads requesting this asset collection won't also
                //try to reload it
                cacheEntry.cacheTime = now;

                //We must check whether the asset collection has changed since the version that we have cached
                Date nextRefreshTime = cachedAssetCollection.getNextRefreshTime(); 
                //We only take any notice of the asset collection's refresh time if the minimum cache time is not zero. 
                //If it is zero then we assume that we're looking at a very volatile website so we will go and
                //check the asset collection's modified time instead
                if ((earliestPermittedCacheTime != now) && (nextRefreshTime != null))
                {
                    long nextRefreshTimeMillis = nextRefreshTime.getTime(); 
                    if (log.isDebugEnabled())
                    {
                        log.debug("Found asset collection has a refresh time of " + nextRefreshTimeMillis);
                    }
                    //We've been told when this asset collection is going to be refreshed.
                    //If we haven't got there yet then we don't need to reload, but if we have then
                    //we do
                    if (now > nextRefreshTimeMillis)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Reloading asset collection " + cacheKey);
                        }
                        result = delegate.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
                        cachedAssetCollection = copyAssetCollection(result);
                        if (cachedAssetCollection != null)
                        {
                            cache.put(cacheKey, new CacheEntry(cachedAssetCollection));
                        }
                    }
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Found asset collection has no refresh time set. Checking modified time.");
                    }

                    //We haven't been told when a refresh is planned so we need to check the modified
                    //time of the asset collection
                    Date currentModifiedTime = delegate.getModifiedTimeOfAssetCollection(cachedAssetCollection.getId());
                    Date cachedModifiedTime = (Date)cachedAssetCollection.getProperty(Resource.PROPERTY_MODIFIED_TIME);

                    if (log.isDebugEnabled())
                    {
                        log.debug("Cached modified time == " + cachedModifiedTime.getTime() + 
                                "; Current modified time == " + currentModifiedTime.getTime());
                    }
                    if (currentModifiedTime.after(cachedModifiedTime))
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Reloading asset collection " + cacheKey);
                        }
                        result = delegate.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
                        cachedAssetCollection = copyAssetCollection(result);
                        if (cachedAssetCollection != null)
                        {
                            cache.put(cacheKey, new CacheEntry(cachedAssetCollection));
                        }
                    }
                }
            }
            if (result == null)
            {
                //If we get here then we have found the asset collection in the cache.
                //We need to create a new object to return that contains the necessary Asset objects
                AssetCollectionImpl resultImpl = copyAssetCollection(cachedAssetCollection);
                
                Query query = new Query();
                query.setSectionId(sectionId);
                query.setMaxResults(maxResults);
                query.setResultsToSkip(resultsToSkip);
                resultImpl.setQuery(query);

                List<String> assetIds = resultImpl.getAssetIds();
                if (assetIds.size() > 0)
                {
                    // If this is a paginated query then select the subset of ids
                    // for which the assets should be fetched.
                    if (maxResults != -1)
                    {
                        int end = resultsToSkip + maxResults;
                        assetIds = assetIds.subList(resultsToSkip, end > assetIds.size() ? assetIds.size() : end);
                    }

                    // Get the actual asset objects.
                    List<Asset> assets = assetFactory.getAssetsById(assetIds);
                    resultImpl.setAssets(assets);
                }
                result = resultImpl;
            }
        }
        return result;
    }

    protected AssetCollectionImpl copyAssetCollection(AssetCollection objToCopy)
    {
        AssetCollectionImpl copy = null;
        if (AssetCollectionImpl.class.isAssignableFrom(objToCopy.getClass()))
        {
            copy = new AssetCollectionImpl();
            AssetCollectionImpl other = (AssetCollectionImpl) objToCopy;
            copy.setAssetFactory(other.getAssetFactory());
            copy.setCollectionFactory(other.getCollectionFactory());
            copy.setSectionFactory(other.getSectionFactory());
            copy.setProperties(other.getProperties());
            copy.setPrimarySectionId(other.getPrimarySectionId());
        }
        return copy;
    }

    public AssetCollection getCollection(String sectionId, String collectionName)
    {
        return getCollection(sectionId, collectionName, 0, -1);
    }

    @Override
    public Date getModifiedTimeOfAssetCollection(String assetCollectionId)
    {
        return delegate.getModifiedTimeOfAssetCollection(assetCollectionId);
    }
    
    public void setDelegate(AssetCollectionFactory delegate)
    {
        this.delegate = delegate;
    }

    public void setCache(SimpleCache<String, CacheEntry> cache)
    {
        this.cache = cache;
    }

    public void setMinimumCacheMilliseconds(long minimumCacheMilliseconds)
    {
        this.minimumCacheMilliseconds = minimumCacheMilliseconds;
    }


    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }


    private static class CacheEntry
    {
        public long cacheTime;
        public final AssetCollectionImpl assetCollection;
        
        public CacheEntry(AssetCollectionImpl assetCollection)
        {
            this.assetCollection = assetCollection;
            this.cacheTime = System.currentTimeMillis();
        }
    }
    
    private long getMinimumCacheMilliseconds()
    {
        long result = 0L;
        WebSite currentSite = WebSiteService.getThreadWebSite();
        if (currentSite == null || !currentSite.isEditorialSite())
        {
            result = minimumCacheMilliseconds;
        }
        return result;
    }
}
