/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.webdrone.HtmlPage;

/**
 * Interface for search result based pages in Alfresco Share.
 * @author Michael Suzuki
 * @since 2.6
 *
 */
public interface SearchResultPage
{
    /**
     * Verify search yielded results.-
     * 
     * @return true if results returned
     */
    public boolean hasResults();
    /**
     * Gets the search results as a collection of SearResultItems.
     * 
     * @return Collections of search result
     */
    public List<SearchResult> getResults();
    
    /**
     * Select a particular search result item link
     * based on the match of the title.
     * 
     * @param title String Search result item title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectItem(String title);
    /**
     * Select a particular search result item link
     * based on the count, the accepted range is 1 onwards.
     * 
     * @param number int Search result item index
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectItem(int number);
}
