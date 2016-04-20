 package org.alfresco.wcm.client.util.impl;

import org.alfresco.wcm.client.util.CmisIdEncoder;

/**
 * Add/remove the usual Alfresco prefix to a UUID
 * @author Chris Lack
 */
public class AlfrescoCmisIdEncoderImpl implements CmisIdEncoder
{
	private static final String UUID_PREFIX = "workspace://SpacesStore/";

	/**
	 * @see org.alfresco.wcm.client.util.CmisIdEncoder#getObjectId(String)
	 */
	@Override
	public String getObjectId(String urlPortion)
	{
		if ( ! urlPortion.startsWith(UUID_PREFIX)) 
		{
			return UUID_PREFIX+urlPortion;
		}
		return urlPortion;
	}

	/**
	 * @see org.alfresco.wcm.client.util.CmisIdEncoder#getUrlSafeString(String)
	 */
	@Override
	public String getUrlSafeString(String objectId)
	{
		if (objectId.startsWith(UUID_PREFIX))
		{
			return objectId.substring(UUID_PREFIX.length());
		}
		return objectId;
	}

}
