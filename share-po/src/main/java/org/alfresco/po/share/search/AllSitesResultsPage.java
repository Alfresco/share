package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;

/**
 * All sites search results page object, holds all element of the html page relating to
 * search results. This is the same as the other search result page but has then specific
 * render logic.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class AllSitesResultsPage extends SearchResultsPage
{

    @SuppressWarnings("unchecked")
    @Override
    public AllSitesResultsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AllSitesResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return {@link AllSitesResultsPage} page response
     */
    public HtmlPage search(final String term)
    {
        searchFor(term);
        return getCurrentPage();
    }
}
