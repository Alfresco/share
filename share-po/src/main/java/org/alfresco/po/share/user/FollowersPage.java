/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olga.lokhach
 */

public class FollowersPage extends SharePage
{

    private static Log logger = LogFactory.getLog(FollowersPage.class);
    private static final By HEADER_BAR = By.cssSelector(".header-bar");
    private static final By NO_FOLLOWERS_MESSAGE = By.cssSelector("div.viewcolumn p");
    private static final By USERS_LIST = By.xpath(".//div[@class='profile']//ul[1]");
    private static final By FOLLOWERS_COUNT = By.cssSelector("div>a[href='followers']");


    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public FollowersPage (WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FollowersPage render(RenderTime timer)
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
                try
                {
                    if (drone.find(USERS_LIST).isDisplayed())
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                }

                if (drone.find(NO_FOLLOWERS_MESSAGE).isDisplayed() || drone.find(NO_FOLLOWERS_MESSAGE).getText().equals(drone.getValue("user.profile.followers.nofollowers")))
                {
                    break;
                }
            }
            catch (Exception e)
            {
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
    public FollowersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FollowersPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Get the navigation bar.
     *
     * @return {@link ProfileNavigation}
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }

    /**
     * Gets the list of the user links.
     *
     * @return List of user links
     */

    public List<ShareLink> getUserLinks()
    {
        List<ShareLink> shareLinks = new ArrayList<>();
        try
        {
            List<WebElement> elements = drone.findAll(USERS_LIST);

            for (WebElement element : elements)
            {
                WebElement result = element.findElement(By.tagName("a"));
                shareLinks.add(new ShareLink(result, drone));
            }
        }
        catch (TimeoutException nse)
        {
            throw new PageOperationException("Unable to find any users", nse);
        }
        return shareLinks;
    }

    /**
     * Return <code>true</code> if the user name is displayed on screen.
     *
     * @return boolean present
     */

    public boolean isUserLinkPresent (String testUser)
    {
        List<ShareLink> userLink = getUserLinks();

        try
        {
            for (ShareLink shareLink : userLink)
            {
                if (shareLink.getDescription().contains(testUser))
                {
                    return true;
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Time out while finding user", e);
            return false;
        }
        return false;
    }

    /**
     * Return <code>true</code> if the No Followers message is displayed on screen.
     *
     * @return boolean present
     */

    public boolean isNoFollowersMessagePresent()
    {
        boolean present = false;
        try
        {
            present = drone.findAndWait(NO_FOLLOWERS_MESSAGE).getText().equals(drone.getValue("user.profile.followers.nofollowers"));
            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    /**
     * Get count of users following me.
     *
     * @return String number of followers
     */
    public String getFollowersCount()
    {
        String count = "";
        try
        {
            count = drone.findAndWait(FOLLOWERS_COUNT).getText().split("[()]+")[1];
        }
        catch (TimeoutException nsee)
        {
            logger.error("Element :" + FOLLOWERS_COUNT + " does not exist", nsee);
        }
        return count;
    }
}
