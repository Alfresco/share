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
package org.alfresco.wcm.client;

/** 
 * Path represents a uri as a resource name and array of path segments.
 * @author Chris Lack
 */
public interface Path
{
	/**
	 * Get the path without the filename, split into individual segments.
	 * eg from /news/world/index.html this will return {"news","world"}.
	 * @return String[] array of path segments.
	 */
	String[] getPathSegments();

	/**
	 * Get the filename from a path.
	 * eg from /news/world/index.html this will return "index.html".
	 * @return String resource name
	 */
	String getResourceName();
}
