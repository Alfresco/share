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
