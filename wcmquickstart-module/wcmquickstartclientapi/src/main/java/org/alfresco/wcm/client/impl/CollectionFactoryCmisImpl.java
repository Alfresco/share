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
