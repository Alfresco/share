package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the html page relating to the search
 * functionality.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.1
 */
public class SearchBox extends SharePage
{
    private final Log logger = LogFactory.getLog(SearchBox.class);
    private final By selector = By.cssSelector("input[id='HEADER_SEARCHBOX_FORM_FIELD']");

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return true when actioned
     */
    public HtmlPage search(final String term)
    {
        if (term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        try
        {
            WebElement input = driver.findElement(selector);
            input.clear();
            input.sendKeys(term + "\n");
            if (logger.isTraceEnabled())
            {
                logger.trace("Apply search on the keyword: " + term);
            }

        }
        catch (NoSuchElementException nse)
        {
        }
        return getCurrentPage();
    }

    /**
     * Performs the live search by typing the term into search field
     * 
     * @param term String term to search
     * @return true when actioned
     */
    public HtmlPage liveSearch(final String term)
    {
        if (term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        try
        {

            WebElement input = findAndWait(selector);
            input.clear();
            input.sendKeys(term);
            input.click();
            if (logger.isTraceEnabled())
            {
                logger.trace("Apply live search on the keyword: " + term);
            }
            return factoryPage.instantiatePage(driver, LiveSearchDropdown.class);
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Live search not displayed.");
        }
    }
}
