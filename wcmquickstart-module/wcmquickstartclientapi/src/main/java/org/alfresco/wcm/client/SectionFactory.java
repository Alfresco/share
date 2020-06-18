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
