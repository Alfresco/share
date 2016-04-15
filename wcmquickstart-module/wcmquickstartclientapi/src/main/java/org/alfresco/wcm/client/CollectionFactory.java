package org.alfresco.wcm.client;

public interface CollectionFactory
{
	/**
	 * Get a collection and its contents
	 * @param sectionId the parent section id
	 * @param collectionName the name of the collection required
	 * @return AssetCollection collection 
	 */
    AssetCollection getCollection(String sectionId, String collectionName);
    
	/**
	 * Get a collection and its contents
	 * @param sectionId the parent section id
	 * @param collectionName the name of the collection required
	 * @param resultsToSkip the number of returned assets to skip
	 * @param maxResults the number of results to return
	 * @return AssetCollection collection 
	 */
    AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults);    
}
