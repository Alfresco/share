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

package org.alfresco.po.share.user;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Represents 1 site from the User Sites List page.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public class UserSiteItem extends PageElement
{
    private static final By ACTIVITY_FEED_BUTTON = By.cssSelector("button");

    private static Log logger = LogFactory.getLog(UserSiteItem.class);

    @FindBy(css="p a") Link site;
    /**
     * Get the site name as displayed on screen.
     * 
     * @return String
     */
    public String getSiteName()
    {
        String name = site.getText();
        return name;
    }

    /**
     * Check if the activity feed is enabled for the site.
     * 
     * @return boolean
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
     * @return HtmlPage
     */
    public HtmlPage clickOnSiteName()
    {
        site.click();
        return getCurrentPage();
    }

    /**
     * Enable or disable activity feed.
     * 
     * @param enabled <code>true</code> to enable avtivities. <code>false</code> to disable.
     * @return HtmlPage
     */
    public void toggleActivityFeed(boolean enabled)
    {
        boolean check =  isActivityFeedEnabled();
        if (enabled != check)
        {
            findAndWait(ACTIVITY_FEED_BUTTON).click();
        }
    } 
    
    /**
     * Returns the text from the activity feed button for the site.
     * 
     * @return String
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
