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
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResult;
import org.alfresco.wcm.client.Section;

/**

 * @author Brian
 *
 */
public class SearchResultAssetImpl implements SearchResult
{
    private static final long serialVersionUID = -9077718168532110705L;

    private Asset delegate = null;
    private int score;
    
    @SuppressWarnings("unused")
    private SearchResultAssetImpl()
    {
    }
    
    SearchResultAssetImpl(Asset delegate, int score)
    {
        this.delegate = delegate;
        this.score = score;
    }

    public Section getContainingSection()
    {
        return delegate.getContainingSection();
    }

    public ContentStream getContentAsInputStream()
    {
        return delegate.getContentAsInputStream();
    }

    public String getDescription()
    {
        return delegate.getDescription();
    }

    public String getId()
    {
        return delegate.getId();
    }

    public String getMimeType()
    {
        return delegate.getMimeType();
    }

    public String getName()
    {
        return delegate.getName();
    }

    public Map<String, Serializable> getProperties()
    {
        return delegate.getProperties();
    }

    public Serializable getProperty(String name)
    {
        return delegate.getProperty(name);
    }

    public Asset getRelatedAsset(String relationshipName)
    {
        return delegate.getRelatedAsset(relationshipName);
    }

    public List<Asset> getRelatedAssets(String relationshipName)
    {
        return delegate.getRelatedAssets(relationshipName);
    }
    
    public Map<String,List<Asset>> getRelatedAssets() {
    	return delegate.getRelatedAssets();
    }    

    public long getSize()
    {
        return delegate.getSize();
    }

    public List<String> getTags()
    {
        return delegate.getTags();
    }

    public String getTitle()
    {
        return delegate.getTitle();
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    @Override
    public String getTemplate()
    {
        return delegate.getTemplate();
    }

    @Override
    public String getType()
    {
        return delegate.getType();
    }

    @Override
    public Map<String, Rendition> getRenditions()
    {
        return delegate.getRenditions();
    }
    
}
