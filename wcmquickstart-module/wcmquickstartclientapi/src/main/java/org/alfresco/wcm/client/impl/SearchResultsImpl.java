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
