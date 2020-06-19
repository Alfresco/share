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

import java.util.List;
import java.util.Map;


/**
 * Asset Interface
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public interface Asset extends Resource
{	
	/** Asset properties */
	public final static String PROPERTY_TAGS = "ws:tags";
    public final static String PROPERTY_PARENT_SECTIONS = "ws:parentSections";
    public final static String PROPERTY_COMMENT_COUNT = "ws:derivedCommentCount";
    public final static String PROPERTY_AVERAGE_RATING = "ws:derivedAverageRating";
    public final static String PROPERTY_PUBLISHED_TIME = "ws:publishedTime";
    public final static String PROPERTY_TEMPLATE_NAME = "ws:templateName";

	/** Associations */
	public final static String RELATED_PRIMARY_IMAGE = "ws:primaryImage";
	public final static String RELATED_SECONDARY_IMAGE = "ws:secondaryImage";
	
	/**
	 * The asset tags
	 * @return Collection of tags
	 */
	List<String> getTags();
	
	/**
	 * Gets the mimetype of the asset.
	 * 
	 * @return	String	 mimetype, null if none
	 */
	String getMimeType();
	
	/**
	 * Gets the size of the assets content.
	 * 
	 * @return	long	content size
	 */
	long getSize();
	
	/**
	 * Gets the assets content input stream
	 * 
	 * @return	ContentStream		assets content stream, null if none
	 */
	ContentStream getContentAsInputStream();

    /**
     * Get a single related asset
     * @param relationshipName
     * @return Asset - a related asset (null if none found)
     */
    Asset getRelatedAsset(String relationshipName);

    /**
     * Get a list of related assets
     * @param relationshipName
     * @return List<Asset> - a list of related assets (empty if none found)
     */
    List<Asset> getRelatedAssets(String relationshipName);

    /** 
     * Returns a Map of all the related assets.
     * @return Map<String,List<Asset>> Map of Assets lists keyed by relationship name (empty if none found)
     */    
    Map<String,List<Asset>> getRelatedAssets();
    
    /**
     * Gets the template associated with this asset, null if none.
     * @return String template associated with the asset, null if none.
     */
    String getTemplate();
    
    /**
     * Get the renditions that are available for this asset.
     * @return A map of available renditions keyed by rendition name
     */
    Map<String,Rendition> getRenditions();
}
