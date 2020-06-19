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
package org.alfresco.wcm.client.util;

/**
 * CMIS Url encoder. This interface is implemented by classes 
 * which translate between a CMIS object id and a valid url string
 * @author Chris Lack
 */
public interface CmisIdEncoder  
{
	/**
	 * Convert a CMIS object Id into a string which will be valid within a url.
	 * @param objectId CMIS object id
	 * @return String url portion
	 */
	String getUrlSafeString(String objectId);	
	
	/**
	 * Convert url valid encoding of a CMIS object Id back into the id.
	 * @param urlPortion String url portion
	 * @return objectId CMIS object id
	 */
	String getObjectId(String urlPortion);	
	
}
