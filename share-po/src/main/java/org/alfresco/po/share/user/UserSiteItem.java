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

package org.alfresco.po.share.user;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represents 1 site from the User Sites List page.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public class UserSiteItem extends HtmlElement
{
    private static final By SITE_NAME = By.cssSelector("p a");
    private static final By ACTIVITY_FEED_BUTTON = By.cssSelector("button");

    private static Log logger = LogFactory.getLog(UserSiteItem.class);

    /**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param drone
     */
    public UserSiteItem(WebElement element, WebDrone drone)
    {
        super(element, drone);
    }

    /**
     * Get the site name as displayed on screen.
     * 
     * @return
     */
    public String getSiteName()
    {
        try
        {
            return findAndWait(SITE_NAME).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find site name: " + SITE_NAME, e);
        }

        throw new PageOperationException("Unable to find the site name: " + SITE_NAME);
    }

    /**
     * Check if the activity feed is enabled for the site.
     * 
     * @return
     */
    public boolean isActivityFeedEnabled()
    {
        try
        {
            String name = findAndWait(ACTIVITY_FEED_BUTTON).getAttribute("name");
            return name.equals("disable");
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find activity feed button: " + ACTIVITY_FEED_BUTTON, e);
        }

        throw new PageOperationException("Unable to find the activity feed button: " + ACTIVITY_FEED_BUTTON);
    }

    /**
     * Click on the Site name to go to the site daskboard.
     * 
     * @return
     */
    public HtmlPage clickOnSiteName()
    {
        try
        {
            findAndWait(SITE_NAME).click();
            domEventCompleted();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find site name: " + SITE_NAME, e);
        }

        throw new PageOperationException("Unable to find the site name: " + SITE_NAME);
    }

    /**
     * Enable or disable activity feed.
     * 
     * @param enabled <code>true</code> to enable avtivities. <code>false</code> to disable.
     * @return
     */
    public HtmlPage toggleActivityFeed(boolean enabled)
    {
        try
        {
            if (enabled != isActivityFeedEnabled())
            {
                findAndWait(ACTIVITY_FEED_BUTTON).click();
                domEventCompleted();
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find activity feed button: " + ACTIVITY_FEED_BUTTON, e);
        }

        throw new PageOperationException("Unable to find the activity feed button: " + ACTIVITY_FEED_BUTTON);
    }
    
    /**
     * Returns the text from the activity feed button for the site.
     * 
     * @return
     */
    public String getActivityFeedButtonLabel()
    {
        try
        {
                return findAndWait(ACTIVITY_FEED_BUTTON).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find activity feed button: " + ACTIVITY_FEED_BUTTON, e);
        }

        throw new PageOperationException("Unable to find the activity feed button: " + ACTIVITY_FEED_BUTTON);
    }
}