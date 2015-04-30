/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
