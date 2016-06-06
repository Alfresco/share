package org.alfresco.wcm.client;

import java.util.Date;
import java.util.List;

/**
 * A collection of assets with meta-data for the collection itself.
 * 
 * @author Chris Lack
 */
public interface AssetCollection extends Resource 
{
	/**
	 * Get the collection of assets
	 * 
	 * @return List<Asset> the wrapped collection
	 */
	List<Asset> getAssets();
	
    /**
     * Obtain the total results count.
     * This is the total number of results that the query returned before any pagination filters were applied.
     * @return long
     */
    long getTotalSize();
    
    /**
     * Obtain the number of results held by this object.
     * @return long
     */
    long getSize();

    /**
     * Obtain the query that was executed to return these results.
     * @return Query
     */
    Query getQuery();

    public List<String> getAssetIds();

    /**
     * The time at which this asset collection was last updated
     * @return Date
     */
    public Date getNextRefreshTime();
}