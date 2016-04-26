package org.alfresco.wcm.client;

/**
 * Factory class for creating Sections from the repository
 * @author Chris Lack
 */
public interface SectionFactory
{
	/**
	 * Get a section from its id.
	 * @param id the section id
	 * @return Section section object
	 */
    Section getSection(String id);
    
    /**
     * Get a section from it's path
     * @param websiteId the website id
     * @param pathSegments the path, split into segments
     * @return Section section object
     */
    Section getSectionFromPathSegments(String websiteId, String[] pathSegments);    
    
    void setAssetFactory(AssetFactory assetFactory);
}
