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
