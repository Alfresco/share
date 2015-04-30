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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * An interface for the retrieval of Asset objects
 * @author Brian
 *
 */
public interface AssetFactory
{
    /**
     * Obtain the asset with the specified identifier.
     * @param id
     * @return the asset with the given identifier or null if not found
     */
    Asset getAssetById(String id);

    /**
     * Obtain a list of assets with the identifiers contained by the supplied collection
     * of identifiers.
     * @param ids
     * @return A list of assets. Each returned asset will have an identifier contained by
     * the supplied collection of identifiers, but it is possible that not all of the requested
     * assets will be returned (if they don't exist, for example). Therefore the size of the
     * returned list will be less than or equal to the size of the the supplied collection
     * of identifiers. If the supplied collection is ordered then the returned list of assets
     * will retain the same ordering.
     */
    List<Asset> getAssetsById(Collection<String> ids);
    
    /**
     * Similar to {@link #getAssetById(String)}, but if deferredLoad is set to true then 
     * this operation does not attempt to load the asset until a subsequent operation on
     * it makes it necessary. Note that this operation may not even check that the requested
     * asset exists.
     * @param id
     * @param deferredLoad
     * @return
     */
    Asset getAssetById(String id, boolean deferredLoad);
    
    /**
     * Similar to {@link #getAssetsById(Collection)}, but if deferredLoad is set to true then
     * this operation will not actually load the assets. They will be loaded if a subsequent
     * operation needs them to be. 
     * @param ids
     * @param deferredLoad
     * @return
     */
    List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad);
    
    /**
     * Loads the asset with the specified name that is located in the specified section.
     * @param sectionId
     * @param assetName
     * @return The corresponding asset or null if not found.
     */
    Asset getSectionAsset(String sectionId, String assetName);

    /**
     * Similar to {@link #getSectionAsset(String, String)}, but if wildcardsAllowedInName is set
     * to true then this operation will allow wildcards in the specified assetName. Permitted wildcards
     * are '_' to match any single character and '%' to match any sequence of characters. These 
     * special characters may be escaped with a preceding '\' if their literal value is needed.
     * @param sectionId
     * @param assetName
     * @param wildcardsAllowedInName
     * @return
     */
    Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName);

    /**
     * Execute the specified query and return the results
     * @param query
     * @return
     */
    SearchResults findByQuery(Query query);
    
    /**
     * Retrieve the identifiers of all assets that are related to the specified one, where
     * the specified asset is the source of the relationship.
     * @param assetId
     * @return A map in which the keys are the names of the relationships and the values
     * are lists of asset identifiers that are related to the specified asset via that kind of
     * relationship.
     */
    Map<String, List<String>> getSourceRelationships(String assetId);
    
    /**
     * Retrieve all the renditions of the specified asset
     * @param assetId
     * @return A map in which a key is the kind of the rendition and the value is the rendition
     * itself
     */
    Map<String, Rendition> getRenditions(String assetId);
    
    /**
     * Fetch the modified time of a specified asset from the repository
     * @param assetId
     * @return The modified time of the specified asset or null if the asset
     * could not be found
     */
    Date getModifiedTimeOfAsset(String assetId);
    
    /**
     * Fetch the modified times of the assets identified in the supplied collection.
     * @param assetIds
     * @return A map in which a key is the identifier of an asset and the value is the modified
     * time of that asset. Note that the returned map may contain fewer entries than the
     * supplied collection of identifiers, as any assets that could not be found in the repository
     * are omitted.
     */
    Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds);
    
    ContentStream getContentStream(String assetId);
}
