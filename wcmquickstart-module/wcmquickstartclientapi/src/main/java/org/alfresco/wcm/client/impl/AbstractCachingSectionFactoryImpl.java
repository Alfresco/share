/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for creating Sections from the repository. This abstract implementation handles all the necessary caching logic.
 * Concrete implementations just need to implement the findSectionWithChildren(String) operation.
 * 
 * @author Chris Lack
 * @author Brian Remmington
 */
public abstract class AbstractCachingSectionFactoryImpl implements SectionFactory
{
    protected static final String PROPERTY_TAG_SUMMARY = "cm:tagScopeSummary";

    private final static Log log = LogFactory.getLog(AbstractCachingSectionFactoryImpl.class);

    private long sectionsRefreshAfter;

    /**
     * Map of sections. If a section is present then all of its descendents will
     * be too. (Note: All sections for a website will only be present when
     * rootSectionsByWebsite has also been populated.)
     */
    private ConcurrentMap<String, Section> sectionsById = new ConcurrentSkipListMap<String, Section>();

    /** Cache of all sections under a website */
    private Map<String, SectionCache> rootSectionsByWebsite = new ConcurrentSkipListMap<String, SectionCache>();

    private ConcurrentMap<String, String> sectionsBeingLoaded = new ConcurrentSkipListMap<String, String>();

    private AssetFactory assetFactory;
    private DictionaryService dictionaryService;
    private CollectionFactory collectionFactory;

    /**
     * Set the asset factory
     * 
     * @param assetFactory
     *            asset factory
     */
    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }

    public AssetFactory getAssetFactory()
    {
        return assetFactory;
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public CollectionFactory getCollectionFactory()
    {
        return collectionFactory;
    }

    /**
     * Create list of tag details from separate lists of names and counts
     * 
     * @param tagSummary List<String>
     * @return combined list of tags
     */
    protected List<Tag> createTags(List<String> tagSummary)
    {
        List<Tag> tags = new ArrayList<Tag>();
        if (tagSummary != null)
        {
            for (String tag : tagSummary)
            {
                String[] nameCountPair = tag.split("=");
                if (nameCountPair.length != 2)
                {
                    continue;
                }
                try
                {
                    tags.add(new TagImpl(nameCountPair[0], Integer.parseInt(nameCountPair[1])));
                }
                catch (Exception ex)
                {
                    log.warn("Ignoring invalid tag summary data: " + tag);
                }
            }
        }
        return tags;
    }

    /**
     * @see org.alfresco.wcm.client.SectionFactory#getSection(String)
     */
    @Override
    public Section getSection(String id)
    {
        // Try cache
        Section section = sectionsById.get(id);

        // Not in cache so fetch
        if (section == null)
        {
            Map<String,Section> loadedSections = findSectionWithChildren(id);
            if (loadedSections != null)
            {
                sectionsById.putAll(loadedSections);
                section = sectionsById.get(id);
            }
        }
        return section;
    }

    /**
     * @see org.alfresco.wcm.client.SectionFactory#getSectionFromPathSegments(String,
     *      String[])
     */
    @Override
    public Section getSectionFromPathSegments(String rootSectionId, String[] pathSegments)
    {
        refreshCacheIfRequired(rootSectionId);

        SectionCache cache = rootSectionsByWebsite.get(rootSectionId);
        Section currentSection = cache.rootSection;

        for (String segment : pathSegments)
        {
            if (segment.length() > 0)
                currentSection = currentSection.getSection(segment);
            if (currentSection == null)
                return null;
        }
        return currentSection;
    }

    /**
     * Refreshes the section cache if empty or expired.
     * 
     * @param rootSectionId
     *            the id of the parent web root
     */
    private void refreshCacheIfRequired(String rootSectionId)
    {
        SectionCache cache = rootSectionsByWebsite.get(rootSectionId);
        while (cache == null || cache.isExpired())
        {
            String existingSectionToken = sectionsBeingLoaded.putIfAbsent(rootSectionId, rootSectionId);
            if (existingSectionToken == null)
            {
                try
                {
                    //It looks like we have to load this section tree, 
                    //but before we do, let's just check that another thread hasn't got in between us checking the cache
                    //and checking whether it's already being loaded...
                    cache = rootSectionsByWebsite.get(rootSectionId);
                    if (cache != null && !cache.isExpired())
                    {
                        //This section is now in the cache, so we don't need to do anything 
                        return;
                    }
                    if (log.isDebugEnabled())
                    {
                        log.debug(Thread.currentThread().getName() + " started refreshing tree cache for section " + rootSectionId);
                    }

                    //This section isn't currently being loaded. Load it.
                    Map<String, Section> sections = findSectionWithChildren(rootSectionId);
                    Section rootSection = sections.get(rootSectionId);
                    SectionCache cachedRootSection = new SectionCache(rootSection);
                    rootSectionsByWebsite.put(rootSectionId, cachedRootSection);
                    sectionsById.putAll(sections);
                    if (log.isDebugEnabled())
                    {
                        log.debug(Thread.currentThread().getName() + " finished refreshing tree cache for section " + rootSectionId);
                    }
                    return;
                }
                finally
                {
                    //There may be other threads waiting for us to finish loading this section tree.
                    //Let them know that we've finished
                    synchronized (rootSectionId)
                    {
                        sectionsBeingLoaded.remove(rootSectionId);
                        rootSectionId.notifyAll();
                    }
                }
            }
            else
            {
                //This section is currently being loaded
                if (cache != null)
                {
                    //We are currently refreshing the cache, but the requested section does already 
                    //appear in the cache. Therefore we'll let the caller simply use the currently-cached 
                    //copy
                    return;
                }
                else
                {
                    //This section isn't currently cached, but is already being loaded
                    //Wait for it to be loaded...
                    synchronized (existingSectionToken)
                    {
                        if (sectionsBeingLoaded.containsKey(existingSectionToken))
                        {
                            try
                            {
                                if (log.isDebugEnabled())
                                {
                                    log.debug(Thread.currentThread().getName() + 
                                            " started waiting for section tree to be loaded " + 
                                            rootSectionId);
                                }
                                existingSectionToken.wait();
                            }
                            catch (InterruptedException e)
                            {
                            }
                            if (log.isDebugEnabled())
                            {
                                log.debug(Thread.currentThread().getName() + 
                                        " finished waiting for section tree to be loaded " + 
                                        rootSectionId);
                            }
                        }
                    }
                }
            }
            cache = rootSectionsByWebsite.get(rootSectionId);
        }
    }

    /**
     * Fetch a section and its children.
     * 
     * @param topSectionId
     *            the section id to start from
     * @return the section object with its children populated.
     */
    protected abstract Map<String,Section> findSectionWithChildren(String topSectionId);

    public void setSectionsRefreshAfter(int seconds)
    {
        this.sectionsRefreshAfter = seconds * 1000;
    }

    /**
     * Section with parent id. Used until the section is parented.
     */
    protected class SectionDetails
    {
        SectionImpl section;
        String objectTypeId;
        String parentId;
    }

    /**
     * A root section and the time the data was cached.
     */
    private class SectionCache
    {
        Section rootSection;
        long sectionsRefeshedAt;

        SectionCache(Section root)
        {
            this.rootSection = root;
            this.sectionsRefeshedAt = System.currentTimeMillis();
        }

        /**
         * Indicates whether the sections cache has expired or not
         * 
         * @return boolean true if expired, false otherwise
         */
        boolean isExpired()
        {
            long now = System.currentTimeMillis();
            long difference = now - sectionsRefeshedAt;
            return difference > sectionsRefreshAfter;
        }
    }

}
