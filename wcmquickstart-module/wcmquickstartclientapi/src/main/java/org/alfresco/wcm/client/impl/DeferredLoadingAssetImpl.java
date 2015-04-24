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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.Section;

public class DeferredLoadingAssetImpl implements Asset
{
    private static final long serialVersionUID = -6315185984769557540L;

    private AssetFactory assetFactory;
    private String id;
    private Asset delegate;

    public DeferredLoadingAssetImpl(String assetId, AssetFactory assetFactory)
    {
        this.id = assetId;
        this.assetFactory = assetFactory;
    }
    
    public String getId()
    {
        return id;
    }
    
    public Section getContainingSection()
    {
        return getDelegate().getContainingSection();
    }

    public ContentStream getContentAsInputStream()
    {
        return getDelegate().getContentAsInputStream();
    }

    public String getDescription()
    {
        return getDelegate().getDescription();
    }

    public String getMimeType()
    {
        return getDelegate().getMimeType();
    }

    public String getName()
    {
        return getDelegate().getName();
    }

    public Map<String, Serializable> getProperties()
    {
        return getDelegate().getProperties();
    }

    public Serializable getProperty(String name)
    {
        return getDelegate().getProperty(name);
    }

    public Asset getRelatedAsset(String relationshipName)
    {
        return getDelegate().getRelatedAsset(relationshipName);
    }

    public Map<String, List<Asset>> getRelatedAssets()
    {
        return getDelegate().getRelatedAssets();
    }

    public List<Asset> getRelatedAssets(String relationshipName)
    {
        return getDelegate().getRelatedAssets(relationshipName);
    }

    public Map<String, Rendition> getRenditions()
    {
        return getDelegate().getRenditions();
    }

    public long getSize()
    {
        return getDelegate().getSize();
    }

    public List<String> getTags()
    {
        return getDelegate().getTags();
    }

    public String getTemplate()
    {
        return getDelegate().getTemplate();
    }

    public String getTitle()
    {
        return getDelegate().getTitle();
    }

    public String getType()
    {
        return getDelegate().getType();
    }

    private Asset getDelegate()
    {
        if (delegate == null)
        {
            delegate = assetFactory.getAssetById(id, false);
        }
        return delegate;
    }
}
