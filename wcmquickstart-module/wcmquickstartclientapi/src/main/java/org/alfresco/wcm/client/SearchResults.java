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
