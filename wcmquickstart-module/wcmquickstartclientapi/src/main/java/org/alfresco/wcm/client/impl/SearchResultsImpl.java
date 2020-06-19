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

import java.util.Collections;
import java.util.List;

import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.SearchResult;
import org.alfresco.wcm.client.SearchResults;

public class SearchResultsImpl implements SearchResults
{
    private List<SearchResult> results = Collections.emptyList();
    private Query query = null;
    private long totalSize = 0L;
    
    @Override
    public Query getQuery()
    {
        return query;
    }

    @Override
    public List<SearchResult> getResults()
    {
        return results;
    }

    @Override
    public long getSize()
    {
        return results.size();
    }

    @Override
    public long getTotalSize()
    {
        return totalSize;
    }

    public void setResults(List<SearchResult> results)
    {
        this.results = results;
    }

    public void setQuery(Query query)
    {
        this.query = query;
    }

    public void setTotalSize(long totalNumItems)
    {
        this.totalSize = totalNumItems;
    }

}
