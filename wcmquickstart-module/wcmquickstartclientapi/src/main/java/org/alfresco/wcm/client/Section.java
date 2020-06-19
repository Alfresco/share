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

import java.util.List;
import java.util.Map;

/**
 * Web Site Section Interface
 * 
 * @author Roy Wetherall
 */
public interface Section extends Resource
{
    /** Section properties */
    static final String PROPERTY_SECTION_CONFIG = "ws:sectionConfig";
    static final String PROPERTY_EXCLUDE_FROM_NAV = "ws:excludeFromNavigation";
    static final String PROPERTY_REDIRECT_CONFIG = "ws:redirectConfig";

    /**
     * Gets the child sections.
     * 
     * @return List<Section> child sections
     */
    List<Section> getSections();

    /**
     * Find a subsection with the specified name
     * 
     * @param sectionName
     *            The name of the section that the caller is asking for
     * @return The subsection of this section that has the request name
     *         (case-sensitive) or null if not found
     */
    Section getSection(String sectionName);

    /**
     * Get the template mappings for this section
     * 
     * @return the template mappings. Key/value pairs where
     *         the key is the name of the content type and the value is the name
     *         of the template to use when rendering content of that type.
     */
    Map<String, String> getTemplateMappings();

    /**
     * Gets the asset with the given name in this section.
     * 
     * @param name String
     *            the name of the required asset. If null or empty then the
     *            section's index page is returned
     * @return Asset the named asset if it exists or null otherwise
     */
    Asset getAsset(String name);

    /**
     * Gets the template for a given type of asset for this section
     * 
     * @param type
     *            type
     * @return String template
     */
    String getTemplate(String type);

    /**
     * Gets the path of the section
     * 
     * @return String url
     */
    String getPath();

    /**
     * Gets a section's index page
     * 
     * @return Asset index page for the section
     */
    Asset getIndexPage();

    /**
     * Should this section be excluded from navigation components?
     * 
     * @return boolean
     */
    boolean getExcludeFromNav();

    /**
     * Carry out a search for resources below this section
     * 
     * @param query
     *            The query attributes to use
     * @return The results of the search
     */
    SearchResults search(Query query);

    /**
     * Carry out a search for resources below this section that contain all the
     * words in the supplied phrase
     * 
     * @param phrase
     *            The words to search for. This is considered to be a
     *            space-separated set of words that must all appear in an asset
     *            for it to match.
     * @param maxResults
     *            The maximum number of results to return
     * @param resultsToSkip
     *            The number of results to skip over before returning. In
     *            combination with maxResults, this is useful for pagination of
     *            results. For example, calling this operation with maxResults
     *            set to 10 and resultsToSkip set to 0 will return the first
     *            "page" of 10 results. Calling it with maxResults set to 10 and
     *            resultsToSkip set to 10 will return the second "page" of 10
     *            results, and so on.
     * @return The results of the search
     */
    SearchResults search(String phrase, int maxResults, int resultsToSkip);

    /**
     * Gets the tags used within this section (and below) order by their usage
     * (most popular first)
     * 
     * @return the tags
     */
    List<Tag> getTags();

    /**
     * Carry out a search for resources below this section that are tagged with
     * the specified tag
     * 
     * @param tag
     *            The words to search for. This is considered to be a
     *            space-separated set of words that must all appear in an asset
     *            for it to match.
     * @param maxResults
     *            The maximum number of results to return
     * @param resultsToSkip
     *            The number of results to skip over before returning. In
     *            combination with maxResults, this is useful for pagination of
     *            results. For example, calling this operation with maxResults
     *            set to 10 and resultsToSkip set to 0 will return the first
     *            "page" of 10 results. Calling it with maxResults set to 10 and
     *            resultsToSkip set to 10 will return the second "page" of 10
     *            results, and so on.
     * @return The results of the search
     */
    SearchResults searchByTag(String tag, int maxResults, int resultsToSkip);

    /**
     * A factory method for returning an empty search query object.
     * 
     * @return Query
     */
    Query createQuery();
    
    /**
     * Retrieve the named asset collection from this section. Returns null if this section
     * does not have an asset collection with that name.
     * @param name String
     * @return AssetCollection
     */
    AssetCollection getAssetCollection(String name);

    AssetCollection getAssetCollection(String name, int resultsToSkip, int maxResults);

    PathResolutionDetails resolvePath(String path);
}
