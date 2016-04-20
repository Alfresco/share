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
package org.alfresco.po.share.workflow;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Sergey Kardash
 */
public class ReassignPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ReassignPage.class);

    @SuppressWarnings("unused")
    private static final int LOADING_WAIT_TIME = 2000;
    private static final By SEARCH_PEOPLE = By.cssSelector("input[id$='peopleFinder-search-text']");
    private static final By SEARCH_BUTTON = By.cssSelector("button[id$='peopleFinder-search-button-button']");
    private final By closeButton = By.cssSelector("div[id$='_default-reassignPanel']>a.container-close");
    private static final By LIST_REASSIGN = (By.cssSelector("table>tbody.yui-dt-data>tr"));
    private static final By SELECT_REASSIGN = By.cssSelector("td[class$='actions yui-dt-last']>div>span>span>span>button");


    @SuppressWarnings("unchecked")
    @Override
    public ReassignPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(SEARCH_PEOPLE), getVisibleRenderElement(SEARCH_BUTTON), getVisibleRenderElement(closeButton));
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReassignPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to select a single user for reassign
     * test is EditTaskPageTest.selectReassign
     * 
     * @param userName String
     * @return HtmlPage
     */
    public HtmlPage selectUser(String userName)
    {

        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        List<WebElement> elements = retrieveUsers(userName);

        userName = userName.toLowerCase();
        for (WebElement webElement : elements)
        {

            if (webElement.findElement(By.cssSelector(".itemname>a")).getText().toLowerCase().contains(userName))
            {
                mouseOver(webElement.findElement(SELECT_REASSIGN));
                webElement.findElement(SELECT_REASSIGN).click();
                break;
            }
        }
        waitUntilAlert();
        return getCurrentPage();
    }

    /**
     * Method to get the list of assignees
     * 
     * @return List of users
     */
    public List<WebElement> retrieveUsers(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        try
        {
            searchForUser(userName);
            //waitForElement(LIST_REASSIGN, SECONDS.convert(driver.getDefaultWaitTime(), MILLISECONDS));
//            waitForElement(LIST_REASSIGN, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return findDisplayedElements(LIST_REASSIGN);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding element", toe);
        }
        throw new PageException();
    }

    /**
     * Method to search for given user
     * 
     * @param userName String
     */
    public void searchForUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            clearSearchField();
            getVisibleElement(SEARCH_PEOPLE).sendKeys(userName);
            selectSearchButton();
            try
            {
                waitForElement(LIST_REASSIGN, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
                waitUntilAlert(5);
            }
            catch (TimeoutException toe)
            {
            }
            // driver.waitFor(LOADING_WAIT_TIME);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element Not found", nse);
        }
        catch (TimeoutException toe)
        {
            logger.error("Timed out: ", toe);
        }
    }

    /**
     * Method to clear Search Field
     */
    public void clearSearchField()
    {
        try
        {
            findFirstDisplayedElement(SEARCH_PEOPLE).clear();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Unable to find Search Field", nse);
            }
        }
    }

    /**
     *
     */
    public void selectSearchButton()
    {
        try
        {
            getVisibleElement(SEARCH_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Search button", nse);
        }
    }
}
