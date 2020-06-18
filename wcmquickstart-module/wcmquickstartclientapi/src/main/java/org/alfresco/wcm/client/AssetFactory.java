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
     * @param id String
     * @return the asset with the given identifier or null if not found
     */
    Asset getAssetById(String id);

    /**
     * Obtain a list of assets with the identifiers contained by the supplied collection
     * of identifiers.
     * @param ids Collection<String>
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
     * @param id String
     * @param deferredLoad boolean
     * @return Asset
     */
    Asset getAssetById(String id, boolean deferredLoad);
    
    /**
     * Similar to {@link #getAssetsById(Collection)}, but if deferredLoad is set to true then
     * this operation will not actually load the assets. They will be loaded if a subsequent
     * operation needs them to be. 
     * @param ids Collection<String>
     * @param deferredLoad boolean
     * @return List<Asset>
     */
    List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad);
    
    /**
     * Loads the asset with the specified name that is located in the specified section.
     * @param sectionId String
     * @param assetName String
     * @return The corresponding asset or null if not found.
     */
    Asset getSectionAsset(String sectionId, String assetName);

    /**
     * Similar to {@link #getSectionAsset(String, String)}, but if wildcardsAllowedInName is set
     * to true then this operation will allow wildcards in the specified assetName. Permitted wildcards
     * are '_' to match any single character and '%' to match any sequence of characters. These 
     * special characters may be escaped with a preceding '\' if their literal value is needed.
     * @param sectionId String
     * @param assetName String
     * @param wildcardsAllowedInName boolean
     * @return Asset
     */
    Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName);

    /**
     * Execute the specified query and return the results
     * @param query Query
     * @return SearchResults
     */
    SearchResults findByQuery(Query query);
    
    /**
     * Retrieve the identifiers of all assets that are related to the specified one, where
     * the specified asset is the source of the relationship.
     * @param assetId String
     * @return A map in which the keys are the names of the relationships and the values
     * are lists of asset identifiers that are related to the specified asset via that kind of
     * relationship.
     */
    Map<String, List<String>> getSourceRelationships(String assetId);
    
    /**
     * Retrieve all the renditions of the specified asset
     * @param assetId String
     * @return A map in which a key is the kind of the rendition and the value is the rendition
     * itself
     */
    Map<String, Rendition> getRenditions(String assetId);
    
    /**
     * Fetch the modified time of a specified asset from the repository
     * @param assetId String
     * @return The modified time of the specified asset or null if the asset
     * could not be found
     */
    Date getModifiedTimeOfAsset(String assetId);
    
    /**
     * Fetch the modified times of the assets identified in the supplied collection.
     * @param assetIds Collection<String>
     * @return A map in which a key is the identifier of an asset and the value is the modified
     * time of that asset. Note that the returned map may contain fewer entries than the
     * supplied collection of identifiers, as any assets that could not be found in the repository
     * are omitted.
     */
    Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds);
    
    ContentStream getContentStream(String assetId);
}
