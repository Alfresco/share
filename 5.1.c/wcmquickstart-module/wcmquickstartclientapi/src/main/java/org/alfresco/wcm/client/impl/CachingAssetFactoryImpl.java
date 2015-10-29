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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A proxying implementation of the {@link AssetFactory} interface that caches
 * loaded assets
 * 
 * @author Brian
 * 
 */
public class CachingAssetFactoryImpl implements AssetFactory
{
    private static final Log log = LogFactory.getLog(CachingAssetFactoryImpl.class);

    private AssetFactory delegate;
    private SimpleCache<String, CacheEntry> cache;
    private long minimumCacheMilliseconds = 30000L;
    private boolean cacheContent = true;
    private boolean cacheRelationships = true;

    public void setDelegate(AssetFactory delegate)
    {
        this.delegate = delegate;
    }

    public void setCache(SimpleCache<String, CacheEntry> newCache)
    {
        this.cache = newCache;
    }

    public void setMinimumCacheSeconds(int seconds)
    {
        minimumCacheMilliseconds = seconds * 1000L;
    }

    public void setCacheContent(boolean cacheContent)
    {
        this.cacheContent = cacheContent;
    }

    public void setCacheRelationships(boolean cacheRelationships)
    {
        this.cacheRelationships = cacheRelationships;
    }

    public SearchResults findByQuery(Query query)
    {
        return delegate.findByQuery(query);
    }

    public Asset getAssetById(String id, boolean deferredLoad)
    {
        CacheEntry cacheEntry = loadCacheEntry(id, deferredLoad);
        return cacheEntry.asset;
    }

    private CacheEntry loadCacheEntry(String id, boolean deferredLoad)
    {
        CacheEntry cacheEntry = getCacheEntry(id);
        if (cacheEntry == null)
        {
            // We have not found the asset in the cache. Load it using our
            // delegated factory and cache the result
            if (log.isDebugEnabled())
            {
                log.debug("Missed cache for asset: " + id);
            }
            Asset asset = delegate.getAssetById(id, deferredLoad);
            //Make sure that subsequent requests from this asset come through this asset factory
            if (AssetImpl.class.isAssignableFrom(asset.getClass()))
            {
                ((AssetImpl)asset).setAssetFactory(this);
            }
            cacheEntry = new CacheEntry(asset);
            cache.put(id, cacheEntry);
        }
        return cacheEntry;
    }

    private CacheEntry getCacheEntry(String id)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Checking cache for asset: " + id);
        }
        long now = System.currentTimeMillis();
        long refreshCutoffTime = now - getMinimumCacheMilliseconds();
        CacheEntry cacheEntry = cache.get(id);
        if (cacheEntry != null)
        {
            // We have found the asset in the cache. How long has it been there?
            // If it has been there
            // longer than the minimum cache time then we'll check its modified
            // time in the repo
            // to ensure it hasn't become out of date
            if (cacheEntry.cacheTime < refreshCutoffTime)
            {
                Asset asset = cacheEntry.asset;
                Date currentModifiedTime = delegate.getModifiedTimeOfAsset(id);
                Date cachedModifiedTime = (Date) asset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    // This asset has been updated in the repo, so flush this
                    // asset from the cache and
                    // forget we ever found it there...
                    cache.remove(id);
                    cacheEntry = null;
                }
                else
                {
                    // The asset has not been modified in the repo since we
                    // cached it, so we don't
                    // have to check it again until the minimum cache time has
                    // expired again...
                    cacheEntry.cacheTime = now;
                }
            }
        }
        return cacheEntry;
    }

    public Asset getAssetById(String id)
    {
        return getAssetById(id, false);
    }

    public List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad)
    {
        List<String> idsToLoad = new ArrayList<String>(ids.size());
        Map<String, Asset> assetsToCheck = new TreeMap<String, Asset>();
        Map<String, Asset> foundAssets = new TreeMap<String, Asset>();

        long now = System.currentTimeMillis();
        long refreshCutoffTime = now - getMinimumCacheMilliseconds();

        for (String id : ids)
        {
            // For each id that we've been given, see if we have the
            // corresponding
            // asset in our cache.
            CacheEntry cacheEntry = cache.get(id);
            if (cacheEntry != null)
            {
                // If we find it, work out whether its necessary to check its
                // modified time in the repo.
                // This is the case if we last checked it longer ago than the
                // "minimumCacheMilliseconds"
                if (cacheEntry.cacheTime < refreshCutoffTime)
                {
                    // Yes, we need to check this one. Record it in our
                    // collection of assets to check
                    assetsToCheck.put(id, cacheEntry.asset);
                }
                else
                {
                    // No, our cached copy hasn't reached its minimum age yet
                    foundAssets.put(id, cacheEntry.asset);
                }
            }
            else
            {
                idsToLoad.add(id);
            }
        }

        // Check the modified time of those assets found in the cache
        if (!assetsToCheck.isEmpty())
        {
            // Get the modified times from the repo for the assets we need to
            // check
            Map<String, Date> currentModifiedTimes = delegate.getModifiedTimesOfAssets(assetsToCheck.keySet());
            for (Map.Entry<String, Date> currentAssetModifiedTime : currentModifiedTimes.entrySet())
            {
                String assetId = currentAssetModifiedTime.getKey();
                Asset cachedAsset = assetsToCheck.get(assetId);
                Date currentModifiedTime = currentAssetModifiedTime.getValue();
                Date cachedModifiedTime = (Date) cachedAsset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    // This one has been modified since we cached it. Remove it
                    // from our cache and add it
                    // to our list of assets to load
                    cache.remove(assetId);
                    idsToLoad.add(assetId);
                }
                else
                {
                    // This one hasn't been modified since we cached it, so we
                    // can use the cached one.
                    foundAssets.put(assetId, cachedAsset);
                    CacheEntry cachedEntry = cache.get(assetId);
                    if (cachedEntry != null)
                    {
                        // Reset the cache time on the cached asset - we don't
                        // need to check it again until
                        // the minimum cache time expires on it again
                        cachedEntry.cacheTime = now;
                    }
                }
            }
        }

        // Load any that we haven't found in the cache (or that have been
        // modified since being cached)
        if (!idsToLoad.isEmpty())
        {
            List<Asset> assets = delegate.getAssetsById(idsToLoad, deferredLoad);
            for (Asset asset : assets)
            {
                //Make sure that subsequent requests from this asset come through this asset factory
                if (AssetImpl.class.isAssignableFrom(asset.getClass()))
                {
                    ((AssetImpl)asset).setAssetFactory(this);
                }
                foundAssets.put(asset.getId(), asset);
                cache.put(asset.getId(), new CacheEntry(asset));
            }
        }

        // Try to retain the correct order as given to us in the originally
        // supplied collection of ids...
        List<Asset> finalResults = new ArrayList<Asset>(foundAssets.size());
        for (String id : ids)
        {
            Asset asset = foundAssets.get(id);
            if (asset != null)
            {
                finalResults.add(asset);
            }
        }

        return finalResults;
    }

    public List<Asset> getAssetsById(Collection<String> ids)
    {
        return getAssetsById(ids, false);
    }

    public Date getModifiedTimeOfAsset(String assetId)
    {
        return delegate.getModifiedTimeOfAsset(assetId);
    }

    public Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds)
    {
        return delegate.getModifiedTimesOfAssets(assetIds);
    }

    public Map<String, Rendition> getRenditions(String assetId)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Checking cache for renditions: " + assetId);
        }
        CacheEntry cacheEntry = loadCacheEntry(assetId, false);
        if (cacheEntry.renditions == null)
        {
            synchronized (cacheEntry.mutex)
            {
                if (cacheEntry.renditions == null)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Missed cache for renditions: " + assetId);
                    }
                    Map<String, Rendition> returnRenditions = new TreeMap<String, Rendition>();
                    Map<String,Rendition> sourceRenditions = delegate.getRenditions(assetId);
                    for (Map.Entry<String, Rendition> entry : sourceRenditions.entrySet())
                    {
                        try
                        {
                            returnRenditions.put(entry.getKey(), new CachingRenditionImpl(entry.getValue()));
                        }
                        catch(IOException ex)
                        {
                        }
                    }
                    returnRenditions = Collections.unmodifiableMap(returnRenditions);
                    cacheEntry.renditions = returnRenditions;
                }
            }
        }
        return cacheEntry.renditions;
    }

    public Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName)
    {
        return delegate.getSectionAsset(sectionId, assetName, wildcardsAllowedInName);
    }

    public Asset getSectionAsset(String sectionId, String assetName)
    {
        return delegate.getSectionAsset(sectionId, assetName);
    }

    public Map<String, List<String>> getSourceRelationships(String assetId)
    {
        Map<String,List<String>> results;
        if (cacheRelationships)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Checking cache for source relationships: " + assetId);
            }
            CacheEntry cacheEntry = loadCacheEntry(assetId, false);
            if (cacheEntry.sourceRelationships == null)
            {
                synchronized (cacheEntry.mutex)
                {
                    if (cacheEntry.sourceRelationships == null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Missed cache for source relationships: " + assetId);
                        }
                        cacheEntry.sourceRelationships = delegate.getSourceRelationships(assetId);
                    }
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Hit cache for source relationships: " + assetId);
                }
            }
            results = cacheEntry.sourceRelationships;
        }
        else
        {
            results = delegate.getSourceRelationships(assetId);
        }
        return results;
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

    @Override
    public ContentStream getContentStream(String assetId)
    {
        ContentStream contentStream = null;
        if (cacheContent)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Checking cache for content stream: " + assetId);
            }
            CacheEntry cacheEntry = loadCacheEntry(assetId, false);
            if (cacheEntry.contentStream == null)
            {
                synchronized (cacheEntry.mutex)
                {
                    if (cacheEntry.contentStream == null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Missed cache for content stream: " + assetId);
                        }
                        contentStream = delegate.getContentStream(assetId);
                        try
                        {
                            cacheEntry.contentStream = new CachingContentStreamImpl(contentStream);
                            contentStream = cacheEntry.contentStream;
                        }
                        catch (Exception ex)
                        {
                            log.warn("Failed to create cached content stream for asset " + assetId, ex);
                        }
                    }
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Hit cache for content stream: " + assetId);
                }
                contentStream = cacheEntry.contentStream;
            }
        }
        else
        {
            contentStream = delegate.getContentStream(assetId);
        }
        return contentStream;
    }

    private static class CacheEntry
    {
        public long cacheTime;
        public final Asset asset;
        public volatile CachingContentStreamImpl contentStream;
        public volatile Map<String, Rendition> renditions;
        public volatile Map<String,List<String>> sourceRelationships;
        public final Object mutex = new Object();

        public CacheEntry(Asset asset)
        {
            this.asset = asset;
            this.cacheTime = System.currentTimeMillis();
        }
    }
}
