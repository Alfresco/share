/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.sitesearch")
/**
 * Site Search dashlet object, holds all element of the HTML relating to site Notice dashlet.
 * 
 * @author Shan Nagarajan
 */
public class SiteSearchDashlet extends AbstractDashlet implements Dashlet
{
    private static final long WAIT_TIME = 50L;
    private static Log logger = LogFactory.getLog(SiteSearchDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.sitesearch");
    private static final By HELP_ICON = By.cssSelector("div.dashlet.sitesearch div.titleBarActionIcon.help");
    private static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By DASHLET_HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");
    private static final By DASHLET_HELP_BALLOON_CLOSE_BUTTON = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.closeButton");
    private static final By DASHLET_TITLE = By.cssSelector("div.dashlet.sitesearch .title");
    private static final By INPUT_BOX = By.cssSelector("input[id$='default-search-text']");
    private static final By RESULT_SIZE_BUTTON = By.cssSelector("button[id$='default-resultSize-button']");
    private static final By RESULT_SIZES = By.cssSelector("div.dashlet.sitesearch div[class*='yui-menu-button-menu'] li>a");
    private static final By SEARCH_RESULTS = By.cssSelector("div[id$='default-search-results']");
    private static final By SEARCH_BUTTON = By.cssSelector("button[id$='default-search-button']");
    private static final By LOADING_MESSAGE = By.cssSelector("table>tbody>tr>td.yui-dt-loading>div");
    public static Integer retrySearchCount = 3;
//    /**
//     * Constructor.
//     */
//    protected SiteSearchDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public SiteSearchDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(By.cssSelector(".yui-resize-handle"));
            while (true)
            {
                synchronized (this)
                {
                    timer.start();
                    try
                    {
                        this.wait(WAIT_TIME);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    getFocus();
                    driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    driver.findElement(HELP_ICON);
                    driver.findElement(DASHLET_TITLE);
                    driver.findElement(SEARCH_BUTTON);
                    driver.findElement(INPUT_BOX);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteSearchDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site notice dashlet", te);
        }
        return this;
    }

    /**
     * The following items are displayed:Search field with the "Search" button;
     */
    public boolean isSearchFieldDisplayed()
    {
        try
        {
            return findAndWait(INPUT_BOX).isDisplayed();
        }
        catch (TimeoutException elementException)
        {

        }
        return false;
    }

    public boolean isSearchButtonDisplayed()
    {
        try
        {
            return findAndWait(RESULT_SIZE_BUTTON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
        }
        return false;
    }

    /**
     * The following items are displayed: Drop down menu with the number of items to be displayed;
     */
    public boolean isDropDownResultsSizeDisplayed()
    {
        try
        {
            return findAndWait(RESULT_SIZE_BUTTON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
        }
        return false;
    }

    /**
     * Finds whether help icon is displayed or not.
     * 
     * @return True if the help icon displayed else false.
     */
    public boolean isHelpIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            return findAndWait(HELP_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }

        return false;
    }

    /**
     * This method is used to Finds Help icon and clicks on it.
     */
    public void clickOnHelpIcon()
    {
        try
        {
            findAndWait(HELP_ICON).click();
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find the help icon.", te);
            throw new PageOperationException("Unable to click the Help icon");
        }
    }

    /**
     * Finds whether help balloon is displayed on this page.
     * 
     * @return True if the balloon displayed else false.
     */
    public boolean isBalloonDisplayed()
    {
        try
        {
            return findAndWait(DASHLET_HELP_BALLOON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into string.
     * 
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return findAndWait(DASHLET_HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            logger.error("Exceeded time to find the help ballon text", elementException);
        }
        throw new UnsupportedOperationException("Not able to find the help text");
    }

    /**
     * This method closes the Help balloon message.
     */
    public SiteSearchDashlet closeHelpBallon()
    {
        try
        {
            findAndWait(DASHLET_HELP_BALLOON_CLOSE_BUTTON).click();
            waitUntilElementDisappears(DASHLET_HELP_BALLOON, TimeUnit.SECONDS.convert(getDefaultWaitTime(), TimeUnit.MILLISECONDS));
            return this;
        }
        catch (TimeoutException elementException)
        {
            throw new UnsupportedOperationException("Exceeded time to find the help ballon close button.", elementException);
        }
    }

    /**
     * This method gets the Site Search Dashlet title.
     * 
     * @return String
     */
    public String getTitle()
    {
        try
        {
            return findAndWait(DASHLET_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * This method gets the Site Search Dashlet content.
     * 
     * @return String
     */
    public String getContent()
    {
        try
        {
            return findAndWait(SEARCH_RESULTS).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * To get search item reults from the dashlet after searching.
     * 
     * @return {@link List} of {@link SiteSearchItem}
     */
    public List<SiteSearchItem> getSearchItems()
    {
        try
        {
            List<WebElement> resultItems = findAndWaitForElements(By.cssSelector("div[id$='default-search-results'] .yui-dt-data>tr"));
            List<SiteSearchItem> searchItems = Collections.emptyList();
            if (resultItems != null && resultItems.size() > 0)
            {
                searchItems = new ArrayList<SiteSearchItem>(resultItems.size());
                for (WebElement webElement : resultItems)
                {
                    searchItems.add(new SiteSearchItem(webElement, driver, factoryPage));
                }
            }
            return searchItems;
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the element: ", e);
            }
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the element: ", e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Input the given search text and click the search button.
     * 
     * @param text - The text to be searched
     * @return {@link SiteSearchDashlet}
     */
    public SiteSearchDashlet search(String text)
    {
        try
        {
            WebElement inputElement = findAndWait(INPUT_BOX);
            inputElement.clear();
            inputElement.sendKeys(text);
            findAndWait(SEARCH_BUTTON).click();
            waitUntilElementDisappears(LOADING_MESSAGE, TimeUnit.SECONDS.convert(getDefaultWaitTime(), TimeUnit.MILLISECONDS));
        }
        catch (TimeoutException e)
        {
            logger.error("Not able to search ", e);
        }
        return this;
    }

    /**
     * Method to perform search with retry
     *
     * @param fileName String
     * @return true is item is found
     */
    public boolean siteSearchWithRetry(String fileName)
    {
        logger.info("Start search with retry on Site Search dashlet");
        int counter = 0;
        int waitInMilliSeconds = 10000;
        boolean found = false;
        while (counter < retrySearchCount)
        {
            search(fileName);
            List<SiteSearchItem> siteSearchResults = getSearchItems();
            for (SiteSearchItem results : siteSearchResults)
            {
                if (results.getItemName().toString().contains(fileName))
                {
                    found = true;
                    return found;
                }
            }
            counter++;
            //double wait time to not over do solr search
            waitInMilliSeconds = (waitInMilliSeconds * 2);
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                    throw new PageException("site search dashlet failed to retrieve results");
                }
            }
        }
        return found;
    }

    /**
     * Returns the list result sizes.
     * 
     * @return {@link List} of {@link String}
     */
    public List<String> getAvailableResultSizes()
    {
        scrollDownToDashlet();
        List<String> resultSizeList = new ArrayList<String>();
        findAndWait(RESULT_SIZE_BUTTON).click();
        List<WebElement> resultSizeElements = findAndWaitForElements(RESULT_SIZES);
        for (WebElement webElement : resultSizeElements)
        {
            resultSizeList.add(webElement.getText());
        }
        findAndWait(RESULT_SIZE_BUTTON).click();
        return resultSizeList;
    }


    /**
     * @return The Search text from input box.
     */
    public String getSearchText()
    {
        try
        {
            return findAndWait(INPUT_BOX).getAttribute("value");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the input box ", e);
        }
    }

    /**
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }


    /**
     * Method to set result size
     * @param searchLimit SearchLimit
     */
    public void setResultSize(SearchLimit searchLimit)
    {
       
        if(searchLimit == null)
        {
            throw new UnsupportedOperationException("This "+searchLimit+" Search Limit is not supproted :"); 
        }
        scrollDownToDashlet();
        try
        {
            findAndWait(RESULT_SIZE_BUTTON).click();
            List<WebElement> resultSizeElements = findAndWaitForElements(RESULT_SIZES);

            for (WebElement webElement : resultSizeElements)
            {
                if(webElement.getText().equals(String.valueOf(searchLimit.getValue())))
                {
                    webElement.click();
                    break;
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to select result size", te);
        }
    }

    /**
     * Method to perform search with required search limit
     * @param searchString String
     * @param searchLimit SearchLimit
     * @return SiteSearchDashlet
     */
    public SiteSearchDashlet search(String searchString, SearchLimit searchLimit)
    {
        setResultSize(searchLimit);
        return search(searchString);
    }

    /**
     * Method to get selected Search Limit
     * @return SearchLimit
     */
    public SearchLimit getSelectedSearchLimit()
    {
        try
        {
            return SearchLimit.getSearchLimit(Integer.parseInt(driver.findElement(RESULT_SIZE_BUTTON).getText().substring(0, 3).trim()));
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isErrorEnabled())
            {
                logger.error("Unable to locate Result Size", nse);
            }
            throw new PageOperationException("Unable to locate Result Size");
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteSearchDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
