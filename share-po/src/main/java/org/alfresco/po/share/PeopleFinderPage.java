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
package org.alfresco.po.share;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * People finder page object, holds all element of the html page relating to
 * share's people finder page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class PeopleFinderPage extends SharePage
{
    private static final By SEARCH_BUTTON = By.cssSelector("button[id$='people-finder_x0023_default-search-button-button']");
    private static final By SEACH_INPUT = By.cssSelector("input[id$='people-finder_x0023_default-search-text']");
    private static Log logger = LogFactory.getLog(PeopleFinderPage.class);
    private List<ShareLink> shareLinks;

    @SuppressWarnings("unchecked")
    @Override
    public PeopleFinderPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                // Check button is displayed and is not disabled
                WebElement searchBtn = driver.findElement(SEARCH_BUTTON);
                if (searchBtn.isEnabled())
                {
                    if (hasNoResultMessage())
                    {
                        break;
                    }
                    if (isVisibleResults())
                    {
                        break;
                    }
                    // This is the default html content hence it left to last.
                    if (isHelpScreenDisplayed())
                    {
                        if (!isVisibleResults() && !hasNoResultMessage())
                        {
                            break;
                        }
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
                // Repeat till we see it
            }
            finally
            {
                timer.end();
            }
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PeopleFinderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    public boolean isTitlePresent()
    {
        return isBrowserTitle("People Finder");
    }

    /**
     * Completes the search form on the people
     * finders page.
     * 
     * @param person String name
     * @return PeopleFinderPage page response
     */
    public HtmlPage searchFor(final String person)
    {
        WebElement input = findAndWait(SEACH_INPUT);
        input.clear();
        input.sendKeys(person);
        WebElement button = findAndWait(SEARCH_BUTTON);
        button.click();
        return getCurrentPage();
    }

    /**
     * Checks for people finder help screen on the page.
     * 
     * @return true is splash screen is displayed
     */
    protected boolean isHelpScreenDisplayed()
    {
        boolean screenDisplayed = false;
        try
        {
            WebElement element = driver.findElement(By.cssSelector("div[id$='default-help']"));
            screenDisplayed = element.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return screenDisplayed;
    }

    /**
     * Checks if results table is displayed
     * 
     * @return true if visible
     */
    private boolean isVisibleResults()
    {
        try
        {
            return driver.findElement(By.cssSelector("tbody.yui-dt-data > tr")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks if no result message is displayed.
     * 
     * @return true if the no result message is found
     */
    private boolean hasNoResultMessage()
    {
        boolean noResults = true;
        try
        {
            // Search for no data message
            WebElement message = driver.findElement(By.cssSelector("tbody.yui-dt-message"));
            noResults = message.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            noResults = false;
        }
        return noResults;
    }

    /**
     * Gets the names of the search result.
     * 
     * @return List of names from search result
     */
    public synchronized List<ShareLink> getResults()
    {
        if (shareLinks == null)
        {
            populateData();
        }
        return shareLinks;
    }

    private synchronized void populateData()
    {
        shareLinks = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("tbody.yui-dt-data > tr"));
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Search results has yeilded %d results", elements.size()));
            }
            for (WebElement element : elements)
            {
                WebElement result = element.findElement(By.tagName("a"));
                shareLinks.add(new ShareLink(result, driver, factoryPage));
            }
        }
        catch (TimeoutException nse)
        {
        }
    }

    /**
     * Clear the input before completing the search form on the people
     * finders page.
     * 
     * @param person String name
     * @return HtmlPage
     */
    public HtmlPage clearAndSearchFor(final String person)
    {
        try
        {

            WebElement input = findAndWait(SEACH_INPUT);
            input.clear();
            input.sendKeys(person);
            WebElement button = findAndWait(SEARCH_BUTTON);
            button.click();
            return getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve control.", te);
        }
    }

    public void selectFollowForUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        List<WebElement> elements = findAndWaitForElements(By.cssSelector("tbody.yui-dt-data > tr"));

        for (WebElement webElement : elements)
        {
            if ((webElement.findElement(By.tagName("a")).getText()).contains(userName))
            {
                webElement.findElement(By.tagName("button")).click();
                try
                {
                    TimeUnit.SECONDS.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public String getTextForFollowButton(String userName)
    {
        String buttonText="";
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        List<WebElement> elements = findAndWaitForElements(By.cssSelector("tbody.yui-dt-data > tr"));

        for (WebElement webElement : elements)
        {
            if ((webElement.findElement(By.tagName("a")).getText()).contains(userName))
            {
                buttonText=webElement.findElement(By.tagName("button")).getText();
                break;
            }
        }
        return buttonText;
    }
}
