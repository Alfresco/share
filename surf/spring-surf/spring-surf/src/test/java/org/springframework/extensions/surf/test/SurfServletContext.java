/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.springframework.mock.web.MockServletContext;

/**
 * Servlet Context for Surf Testing
 * 
 * Eliminates JAR file lookups into /WEB-INF/lib by Freemarker
 * 
 * @author muzquiano
 */
public class SurfServletContext extends MockServletContext
{
	protected File rootFolder;
	
	public SurfServletContext(File rootFolder)
	{
		this.rootFolder = rootFolder;
	}
	
	/**
	 * This prevents Freemarker Taglibs from working (during tests)
	 */
	public Set<String> getResourcePaths(String path) 
	{
		if ("/WEB-INF/lib".equals(path))
		{
			return new HashSet<String>();
		}
		
		return super.getResourcePaths(path);
	}
	
	/**
	 * This allows local file system stores to resolve paths to maven test directories
	 */
	public String getRealPath(String path) 
	{
		if (path != null && path.startsWith("/"))
		{
			return this.rootFolder.getPath() + path.substring(1);
		}
		
		return super.getRealPath(path);
	}
	
}
