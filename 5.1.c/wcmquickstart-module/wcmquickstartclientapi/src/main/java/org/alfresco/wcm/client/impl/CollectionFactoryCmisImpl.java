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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.ResourceNotFoundException;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.SqlUtils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectionFactoryCmisImpl implements CollectionFactory
{	
	private final static Log log = LogFactory.getLog(CollectionFactoryCmisImpl.class);
	
	private static final String COLUMNS = "f.cmis:objectId, f.cmis:name, t.cm:title, t.cm:description";
	
	private static final String QUERY = "select " + COLUMNS + " "+
									    "from cmis:folder as f "+
										"join cm:titled as t on t.cmis:objectId = f.cmis:objectId ";
	
	private static final String QUERY_COLLECTION = QUERY + "where in_folder(f, {0}) and f.cmis:name = {1}";	
	
    private SectionFactory sectionFactory;
	private AssetFactory assetFactory;
    
    /**
	 * Create a ResourceCollection from a QueryResult
	 * @param result query result
	 * @return ResourceCollectionImpl collection object
	 */
	private AssetCollectionImpl buildCollection(QueryResult result) 
	{
		AssetCollectionImpl collection = new AssetCollectionImpl();		
		return collection;
	}
	
    /**
     * Build a list of related target node ids for the collection
     * @param result
     * @return List<String> list of related resource ids
     */
    private List<String> buildRelatedAssetList(QueryResult result)
    {
        List<String> relatedIds = new ArrayList<String>();

        List<Relationship> relationships = result.getRelationships();
        for (Relationship relationship : relationships)
        {
            String name = relationship.getPropertyValue(PropertyIds.NAME);
            String targetId = relationship.getPropertyValue(PropertyIds.TARGET_ID);
            log.debug(name+" "+targetId);
            relatedIds.add(targetId);
        }
        return relatedIds;
    }
    
	@Override
    public AssetCollection getCollection(String sectionId, String collectionName)
    {
		if (sectionId == null || sectionId.length() == 0)
		{
			throw new IllegalArgumentException("sectionId must be supplied");
		}
		if (collectionName == null || collectionName.length() == 0)
		{
			throw new IllegalArgumentException("collectionName must be supplied");
		}
		
		// Get the section so that we have the section's collections folder id
		Section section = sectionFactory.getSection(sectionId);
		
		Session session = CmisSessionHelper.getSession();
		
		// Query the named collection under the collections folder
		String cquery = MessageFormat.format(
							QUERY_COLLECTION, 
							//TODO replace this line along with code in SectionFactoryImpl if this class is used again... SqlUtils.encloseSQLString(section.getCollectionFolderId()), 
							SqlUtils.encloseSQLString(collectionName));
		log.debug("Querying "+cquery);
		
		// Include relationships
        OperationContext oc = session.createOperationContext();
        oc.setIncludeRelationships(IncludeRelationships.SOURCE);
		
		ItemIterable<QueryResult> cresults = session.query(cquery, false, oc);	
		if (cresults.getTotalNumItems() == 0)
		{			
			throw new ResourceNotFoundException(section.getId()+"/"+collectionName);
		}
		QueryResult cresult = cresults.iterator().next();
		AssetCollectionImpl collection = buildCollection(cresult);	
		
		// Get the list of ids of assets in the collection
		List<String> assetIds = buildRelatedAssetList(cresult);
		
		// Get the actual asset objects.
		if (assetIds.size() > 0) {
			List<Asset> assets = assetFactory.getAssetsById(assetIds);		
			collection.setAssets(assets);
		}
		
		return collection;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }
    
    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

	@Override
    public AssetCollection getCollection(String sectionId,
            String collectionName, int resultsToSkip, int maxResults)
    {
	    // TODO Auto-generated method stub
	    return null;
    }    
}
