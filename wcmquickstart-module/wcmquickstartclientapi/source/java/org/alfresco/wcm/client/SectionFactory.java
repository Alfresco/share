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
 * Factory class for creating Sections from the repository
 * @author Chris Lack
 */
public interface SectionFactory
{
	/**
	 * Get a section from its id.
	 * @param id the section id
	 * @return Section section object
	 */
    Section getSection(String id);
    
    /**
     * Get a section from it's path
     * @param websiteId the website id
     * @param pathSegments the path, split into segments
     * @return Section section object
     */
    Section getSectionFromPathSegments(String websiteId, String[] pathSegments);    
    
    void setAssetFactory(AssetFactory assetFactory);
}
