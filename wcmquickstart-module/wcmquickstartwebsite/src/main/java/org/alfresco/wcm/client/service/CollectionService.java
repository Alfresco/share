package org.alfresco.wcm.client.service;

import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.CollectionFactory;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

public class CollectionService extends BaseProcessorExtension implements CollectionFactory
{
    private CollectionFactory collectionFactory;

	public void setCollectionFactory(CollectionFactory collectionFactory) {
		this.collectionFactory = collectionFactory;
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName) {
		return collectionFactory.getCollection(sectionId, collectionName);
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults) {
		return collectionFactory.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
	}

}
