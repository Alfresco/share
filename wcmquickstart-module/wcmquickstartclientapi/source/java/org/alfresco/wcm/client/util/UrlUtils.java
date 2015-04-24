/*
 * Copyright ss(C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
	 * @param Asset asset
	 * @return String url
	 */
	String getUrl(Asset asset);
	
	/**
	 * Get the url of a section
	 * @param Section section
	 * @return String url
	 */
	String getUrl(Section section);
	
	/**
	 * Get the short url of an asset
	 * @param Asset asset
	 * @return String url
	 */
	String getShortUrl(Asset asset);
	
	/**
	 * Get the long url of an asset
	 * @param Asset asset
	 * @return String url
	 */
	String getLongUrl(Asset asset);	

	/**
     * Get the asset id from a short url.
     * @param url the url
     * @return String the asset id
     */
	String getAssetIdFromShortUrl(String uri);
	
	/**
	 * Reverse the URL encoding process to get the original resource name.
	 * @param resourceName
	 * @return
	 */
	String decodeResourceName(String resourceName);	
	
	/**
	 * Get the website domain as a string. 
	 * @return domain
	 */
	String getWebsiteDomain(WebSite website);	
}
