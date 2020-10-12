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