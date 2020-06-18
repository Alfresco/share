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

import org.alfresco.wcm.client.Path;

/** 
 * Path splits a uri into a resource name and array of path segments.
 * @author Chris Lack
 * @author Brian Remmington
 */
public class PathImpl implements Path
{
	private String[] pathSegments;
	private String resourceName;
	
	public PathImpl(String uri) 
	{
        pathSegments = new String[0];
        resourceName = "";

        if (uri != null) 
		{
            uri = uri.trim();
		    String[] splitPath = uri.split("/", -1);
		    if (splitPath.length > 0)
		    {
		        resourceName = splitPath[splitPath.length - 1];
		        pathSegments = new String[splitPath.length - 1];
		        System.arraycopy(splitPath, 0, pathSegments, 0, pathSegments.length);
		    }
		}
	}
	
	/**
	 * @see org.alfresco.wcm.client.Path#getPathSegments()
	 */
	public String[] getPathSegments()
    {
    	return pathSegments;
    }
	
	/**
	 * @see org.alfresco.wcm.client.Path#getResourceName()
	 */
	public String getResourceName()
    {
    	return resourceName;
    }

}
