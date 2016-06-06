package org.alfresco.wcm.client.util;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;

/**
 * Url utility methods
 * @author Chris Lack
 */
public interface UrlUtils  
{
	/**
	 * Get the url of an asset
	 * @param asset Asset
	 * @return String url
	 */
	String getUrl(Asset asset);
	
	/**
	 * Get the url of a section
	 * @param section Section
	 * @return String url
	 */
	String getUrl(Section section);
	
	/**
	 * Get the short url of an asset
	 * @param asset Asset
	 * @return String url
	 */
	String getShortUrl(Asset asset);
	
	/**
	 * Get the long url of an asset
	 * @param asset Asset
	 * @return String url
	 */
	String getLongUrl(Asset asset);	

	/**
     * Get the asset id from a short url.
     * @param uri the url
     * @return String the asset id
     */
	String getAssetIdFromShortUrl(String uri);
	
	/**
	 * Reverse the URL encoding process to get the original resource name.
	 * @param resourceName String
	 * @return String
	 */
	String decodeResourceName(String resourceName);	
	
	/**
	 * Get the website domain as a string. 
	 * @return domain
	 */
	String getWebsiteDomain(WebSite website);	
}
