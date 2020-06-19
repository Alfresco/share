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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.Section;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Asset interface implementation
 * 
 * @author Roy Wetherall
 * @author Brian
 */
public class AssetImpl extends ResourceBaseImpl implements Asset
{
    private static final long serialVersionUID = 1L;
    private static final String RELATIONSHIPS_PROP_NAME = "ws:sourceRelationships";

    private Map<String, List<String>> relationships = null;
    private Map<String, List<Asset>> relatedAssets;

    private List<String> parentSectionIds = Collections.emptyList();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Map<String, Serializable> props)
    {
        this.writeLock.lock();
        try
        {
            super.setProperties(props);
            Serializable relations = props.get(RELATIONSHIPS_PROP_NAME);
            if (relations != null)
            {
                relationships = Collections.unmodifiableMap((Map<String, List<String>>) relations);
            }
        }
        finally
        {
            this.writeLock.unlock();
        }
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAssets()
     */
    @Override
    public Map<String, List<Asset>> getRelatedAssets()
    {
        /*
         * Note: This method call is expensive if used for every asset in a
         * collection as a query is performed. In mitigation the results are
         * cached within the object.
         */
        this.readLock.lock();
        try
        {
            if (relatedAssets != null)
            {
                return relatedAssets;
            }
        }
        finally
        {
            this.readLock.unlock();
        }
        this.writeLock.lock();
        try
        {
            if (relatedAssets == null)
            {
                Map<String, List<Asset>> assetMap = new TreeMap<String, List<Asset>>();
                for (Entry<String, List<String>> entry : getRelationships().entrySet())
                {
                    List<String> relatedAssetIds = entry.getValue();
                    if (relatedAssetIds != null)
                    {
                        List<Asset> assets = getAssetFactory().getAssetsById(relatedAssetIds);
                        if (assets.size() > 0)
                            assetMap.put(entry.getKey(), assets);
                    }
                }
                relatedAssets = Collections.unmodifiableMap(assetMap);
            }
            return relatedAssets;
        }
        finally
        {
            this.writeLock.unlock();
        }
    }

    /**
     * Returns a map of all related assets stored in an Asset.
     * Unlike getRelatedAssets(), this method can return null if the related assets were not cached within Asset.
     * @return Map of Assets lists keyed by relationship name.
     */
    public Map<String, List<Asset>> getCachedRelatedAssets()
    {
        return relatedAssets;
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAssets(String)
     */
    @Override
    public List<Asset> getRelatedAssets(String relationshipName)
    {
        List<String> relatedAssetIds = getRelationships().get(relationshipName);
        if (relatedAssetIds == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return getAssetFactory().getAssetsById(relatedAssetIds);
        }
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAsset(String)
     */
    @Override
    public Asset getRelatedAsset(String relationshipName)
    {
        Asset result = null;
        List<String> relatedAssetIds = getRelationships().get(relationshipName);
        if (relatedAssetIds != null && !relatedAssetIds.isEmpty())
        {
            result = getAssetFactory().getAssetById(relatedAssetIds.get(0));
        }
        return result;
    }

    /**
     * Set the parent sections id's
     * 
     * @param sectionIds
     *            collection of parent section id
     */
    public void setParentSectionIds(Collection<String> sectionIds)
    {
        this.writeLock.lock();
        try
        {
            if (sectionIds != null)
            {
                this.parentSectionIds = Collections.unmodifiableList(new LinkedList<String>(sectionIds));
                if (!sectionIds.isEmpty())
                {
                    setPrimarySectionId(parentSectionIds.get(0));
                }
            }
            else
            {
                parentSectionIds = Collections.emptyList();
            }
        }
        finally
        {
            this.writeLock.unlock();
        }
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getTags()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getTags()
    {
        return (List<String>) getProperties().get(PROPERTY_TAGS);
    }

    /**
     * @see Asset#getMimeType()
     */
    @Override
    public String getMimeType()
    {
        return (String) getProperties().get(PropertyIds.CONTENT_STREAM_MIME_TYPE);
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getSize()
     */
    @Override
    public long getSize()
    {
        BigInteger streamLength = (BigInteger) getProperties().get(PropertyIds.CONTENT_STREAM_LENGTH);
        if (streamLength == null)
            return 0;
        return streamLength.longValue();
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getContentAsInputStream()
     */
    @Override
    public ContentStream getContentAsInputStream()
    {
        return getAssetFactory().getContentStream(getId());
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getTemplate()
     */
    @Override
    public String getTemplate()
    {
        String template = null;

        // Only "text" assets have templates associated with them
        String mimeType = getMimeType();
        if ((mimeType != null) && mimeType.startsWith("text"))
        {
            template = (String) getProperties().get(PROPERTY_TEMPLATE_NAME);
            if ((template == null) || template.trim().length() == 0)
            {
                Section section = getContainingSection();
                template = section.getTemplate(getType());
            }
        }
        return template;
    }

    private Map<String, List<String>> getRelationships()
    {
        this.readLock.lock();
        try
        {
            if (relationships == null)
            {
                this.readLock.unlock();
                this.writeLock.lock();
                try
                {
                    if (relationships == null)
                    {
                        relationships = getAssetFactory().getSourceRelationships(getId());
                        if (relationships != null)
                        {
                            relationships = Collections.unmodifiableMap(relationships);
                        }
                    }
                }
                finally
                {
                    this.readLock.lock();
                    this.writeLock.unlock();
                }
            }
            return relationships;
        }
        finally
        {
            this.readLock.unlock();
        }
    }

    @Override
    public Map<String, Rendition> getRenditions()
    {
        // Already returns unmodifiable map
        return getAssetFactory().getRenditions(getId());
    }
}
