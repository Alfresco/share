package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.HtmlPage;

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
