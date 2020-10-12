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
