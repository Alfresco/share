/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Saved Search dashlet object, holds all element of the HTML relating to Saved
 * Search dashlet.
 * 
 * @author Ranjith Manyam
 */
public class SavedSearchDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SavedSearchDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.savedsearch");
    private static final By HELP_ICON = By.cssSelector("div.dashlet.savedsearch div.titleBarActionIcon.help");
    private static final By CONFIGURE_DASHLET_ICON = By.cssSelector("div.dashlet.savedsearch div.titleBarActionIcon.edit");
    private static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By DASHLET_HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");
    private static final By DASHLET_HELP_BALLOON_CLOSE_BUTTON = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.closeButton");
    private static final By DASHLET_TITLE = By.cssSelector("div.dashlet.savedsearch .title");
    private static final By SEARCH_RESULTS = By.cssSelector("div.dashlet.savedsearch div[id$='default-search-results']");
    private static final By titleBarActions = By.xpath("//div[starts-with(@class,'dashlet savedsearch')] //div[@class='titleBarActions']");

    /**
     * Constructor.
     */
    protected SavedSearchDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.savedsearch .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SavedSearchDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    scrollDownToDashlet();
                    getFocus();
                    drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                    drone.find(CONFIGURE_DASHLET_ICON);
                    drone.find(HELP_ICON);
                    drone.find(DASHLET_TITLE);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
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

    @SuppressWarnings("unchecked")
    @Override
    public SavedSearchDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SavedSearchDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
            drone.mouseOverOnElement(drone.find(titleBarActions));
            return drone.findAndWait(HELP_ICON).isDisplayed();
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
            drone.mouseOverOnElement(drone.find(titleBarActions));
            drone.findAndWait(HELP_ICON).click();
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
            return drone.findAndWait(DASHLET_HELP_BALLOON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into
     * string.
     * 
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return drone.findAndWait(DASHLET_HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            logger.error("Exceeded time to find the help ballon text");
        }
        throw new UnsupportedOperationException("Not able to find the help text");
    }

    /**
     * This method closes the Help balloon message.
     */
    public SavedSearchDashlet closeHelpBallon()
    {
        try
        {
            drone.findAndWait(DASHLET_HELP_BALLOON_CLOSE_BUTTON).click();
            drone.waitUntilElementDisappears(DASHLET_HELP_BALLOON, TimeUnit.SECONDS.convert(WAIT_TIME_3000, TimeUnit.MILLISECONDS));
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
            return drone.findAndWait(DASHLET_TITLE).getText();
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
            return drone.findAndWait(SEARCH_RESULTS).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * To get search item reults from the dashlet after searching.
     * 
     * @return {@link java.util.List} of
     *         {@link org.alfresco.po.share.dashlet.SiteSearchItem}
     */
    public List<SiteSearchItem> getSearchItems()
    {
        try
        {
            List<WebElement> resultItems = drone.findAndWaitForElements(By
                    .cssSelector("div.dashlet.savedsearch div[id$='default-search-results'] .yui-dt-data>tr"));
            List<SiteSearchItem> searchItems = Collections.emptyList();
            if (resultItems != null && resultItems.size() > 0)
            {
                searchItems = new ArrayList<SiteSearchItem>(resultItems.size());
                for (WebElement webElement : resultItems)
                {
                    searchItems.add(new SiteSearchItem(webElement, drone));
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
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * This method is used to Finds Edit icon and clicks on it.
     */
    public ConfigureSavedSearchDialogBoxPage clickOnEditButton()
    {
        try
        {
            drone.mouseOverOnElement(drone.find(titleBarActions));
            drone.findAndWait(CONFIGURE_DASHLET_ICON).click();
            return new ConfigureSavedSearchDialogBoxPage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find the Edit (Configure) icon.", te);
            throw new PageOperationException("Unable to click the Edit (Configure) icon");
        }
    }

    /**
     * Method to check if a given item is listed in the search results or not
     * 
     * @param itemName
     * @return True if item found in search results
     */
    public boolean isItemFound(String itemName)
    {
        if (itemName == null)
        {
            throw new IllegalArgumentException("Item Name can't be null.");
        }
        List<SiteSearchItem> items = getSearchItems();
        for (SiteSearchItem item : items)
        {
            if (item.getItemName().getDescription().equals(itemName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to verify Configure icon is displayed
     */
    public boolean isConfigIconDisplayed()
    {
        try
        {
            return drone.isElementDisplayed(CONFIGURE_DASHLET_ICON);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find configure icon");
        }
        return false;
    }
}
