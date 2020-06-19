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

/**
 * A class representing results of a search. 
 * @author Brian
 *
 */
public interface SearchResults
{
    /**
     * Obtain the list of results held by this object
     * @return List<SearchResult>
     */
    List<SearchResult> getResults();
    
    /**
     * Obtain the total results count.
     * This is the total number of results that the query returned before any pagination filters were applied.
     * @return long
     */
    long getTotalSize();
    
    /**
     * Obtain the number of results held by this object.
     * @return long
     */
    long getSize();

    /**
     * Obtain the query that was executed to return these results.
     * @return Query
     */
    Query getQuery();
}
