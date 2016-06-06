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
