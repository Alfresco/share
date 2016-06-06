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

import org.alfresco.po.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * People finder page result object, holds all element of the html page relating to
 * share's people finder page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class PeopleFinderResultPage extends PeopleFinderPage
{
    private static Log logger = LogFactory.getLog(PeopleFinderPage.class);

    private List<ShareLink> shareLinks;

    public PeopleFinderResultPage render(RenderTime timer)
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
            if (hasNoResult())
            {
                break;
            }
            else if (isVisibleResults())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    /**
     * Checks if results table is displayed
     * 
     * @return true if visible
     */
    private synchronized boolean isVisibleResults()
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
    private boolean hasNoResult()
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
}
