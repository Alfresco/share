package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Repository search results page object, holds all element of the html page relating to
 * search results. This is the same as the other search result page but has then specific
 * render logic.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class RepositoryResultsPage extends SearchResultsPage
{
    private static final By Go_TO_ADV_SEARCH = By.cssSelector("#HEADER_ADVANCED_SEARCH_text");

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryResultsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return {@link RepositoryResultsPage} page response
     */
    public HtmlPage search(final String term)
    {
        searchFor(term);
        return getCurrentPage();
    }

    /**
     * Gets numeric string value indicating the number of search results found
     * from the search query.
     * 
     * @return numeric value of String displaying total result count
     */
    public int getResultCount()
    {
        try
        {
            String val = driver.findElement(By.cssSelector("div[id$='_default-search-info']>b")).getText();
            return Integer.parseInt(val);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public HtmlPage goToAdvancedSearch()
    {
        try
        {
            WebElement backBtn = findAndWait(Go_TO_ADV_SEARCH);
            backBtn.click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + Go_TO_ADV_SEARCH);
        }
    }

}
