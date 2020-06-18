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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.Query;

/**
 * Collection of assets with meta-data for the collection itself.
 * @author Chris Lack
 * @author Brian Remmington
 */
public class AssetCollectionImpl extends ResourceBaseImpl implements AssetCollection
{

	private static final long serialVersionUID = 1L;

	private List<String> assetIds = new ArrayList<String>();
	
	private boolean isDynamic;
	
	// When is this asset collection due to be refreshed?
	private Date refreshTime;
	
	/** The wrapped collection */
	protected List<Asset> assets = new ArrayList<Asset>();
	
	/** Pagination details */
    private Query query;
    private long totalSize;	

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Map<String, Serializable> props)
    {
        super.setProperties(props);
        assetIds = (List<String>)props.get("ws:containedAssets");
        refreshTime = (Date)props.get("ws:refreshAt");
        isDynamic = (Boolean)props.get("ws:isDynamic");
        totalSize = assetIds == null ? 0 : assetIds.size();
    }

    /**
	 *  @see org.alfresco.wcm.client.AssetCollection#getAssets()
	 */	
	@Override
	public List<Asset> getAssets()
	{
        return Collections.unmodifiableList(assets);
	}

	public void setAssets(List<Asset> assets)
	{
		this.assets = assets;
	}

	/**
	 * Add a single asset to the collection.
	 * @param asset asset object
	 */
	public void add(Asset asset)
	{
		this.assets.add(asset);
	}

    @Override
    public Query getQuery()
    {
        return query;
    }

    @Override
    public long getSize()
    {
        return assets.size();
    }

    @Override
    public long getTotalSize()
    {
        return totalSize;
    }

    public void setQuery(Query query)
    {
        this.query = query;
    }

    public List<String> getAssetIds()
    {
        return Collections.unmodifiableList(assetIds);
    }

    public void setAssetIds(List<String> assetIds)
    {
        this.assetIds = assetIds;
    }

    /**
     * The time at which this asset collection was last refreshed
     * @return Date
     */
    public Date getNextRefreshTime()
    {
        return refreshTime;
    }
}
