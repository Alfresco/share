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
package org.alfresco.wcm.client.util.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.util.CmisIdEncoder;
import org.alfresco.wcm.client.util.UrlUtils;

/**
 * Url utility
 * @author Chris Lack
 */
public class UrlUtilsImpl implements UrlUtils 
{
	private CmisIdEncoder cmisIdEncoder;

	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#getUrl(Asset)
	 */
	@Override
	public String getUrl(Asset asset) 
	{
		boolean image = asset.getMimeType() != null && asset.getMimeType().startsWith("image/");
		if (image) 
		{
			// Return images as short path 
			return getShortUrl(asset);
		}
		else 
		{
			return getLongUrl(asset);
		}
	}
	
	
	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#getShortUrl(Asset)
	 */
	@Override
	public String getShortUrl(Asset asset) 
	{
		try 
		{
			return "/asset/"+cmisIdEncoder.getUrlSafeString(asset.getId())+"/"+URLEncoder.encode(asset.getName(), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
	}	
	
	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#getLongUrl(Asset)
	 */	
	@Override
	public String getLongUrl(Asset asset) 
	{
		// Return full, friendly path for other assets
		try
        {
            return asset.getContainingSection().getPath()+URLEncoder.encode(asset.getName(), "UTF-8");
        } 
		catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }		
	}
	
	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#decodeResourceName(String)
	 */
	@Override	
	public String decodeResourceName(String resourceName)
	{
		try
        {
			return URLDecoder.decode(resourceName, "UTF-8");
        } 
		catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
	}

	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#getUrl(Section)
	 */
	@Override
	public String getUrl(Section section) 
	{
		return section.getPath();
	}

	/**
	 * @see org.alfresco.wcm.client.util.UrlUtils#getAssetIdFromShortUrl(String)
	 */
	@Override
    public String getAssetIdFromShortUrl(String uri)
    {
		StringTokenizer st = new StringTokenizer(uri, "/");
		String prefix = st.nextToken();
		if ( ! prefix.equals("asset") || st.countTokens() < 1 || st.countTokens() > 2 || uri.trim().endsWith("/")) 
		{
			throw new IllegalArgumentException("Asset URL expected to be in format /asset/{url-safe-object-id}/{filename} or /asset/{url-safe-object-id}"); 
		}
		String urlSafeObjectId = st.nextToken();
		
		String objectId = cmisIdEncoder.getObjectId(urlSafeObjectId);
		return objectId;
    }
	
	public void setCmisIdEncoder(CmisIdEncoder cmisIdEncoder) 
	{
		this.cmisIdEncoder = cmisIdEncoder;
	}

    /**
     * @see org.alfresco.wcm.client.util.UrlUtils#getWebsiteDomain(WebSite)
     */
    @Override
    public String getWebsiteDomain(WebSite webSite)
    {
        return "http://"+webSite.getHostName()+(webSite.getHostPort() == 80 ? "" : ":"+webSite.getHostPort()+"/"+webSite.getContext());
    }
}
