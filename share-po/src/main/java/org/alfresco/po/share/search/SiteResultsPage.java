package org.alfresco.po.share.search;

import java.util.NoSuchElementException;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.WebElement;

/**
 * Site search results page object, holds all element of the html page relating to
 * search results. This is the same as the other search result page but has then specific
 * render logic.
 * 
 * @author Michael Suzuki
 * @since 1.4
 */
public class SiteResultsPage extends SearchResultsPage
{

    @SuppressWarnings("unchecked")
    @Override
    public SiteResultsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return {@link SiteResultsPage} page response
     */
    public HtmlPage search(final String term)
    {
        searchFor(term);
        return getCurrentPage();
    }

    /**
     * Method to verify if 'Back to..Site' link is displayed
     *
     * @param siteName String
     * @return true if displayed
     */
    public boolean isBackToSiteDisplayed(String siteName)
    {
        boolean isDisplayed = false;
        try
        {
            WebElement backLink = driver.findElement(BACK_TO_SITE_LINK);
            if(backLink.getText().contains(siteName))
                isDisplayed = true;
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        return isDisplayed;
    }
}
