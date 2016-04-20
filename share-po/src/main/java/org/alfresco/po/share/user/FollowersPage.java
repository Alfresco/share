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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Created by olga.lokhach
 */

public class FollowersPage extends SharePage
{

    private static Log logger = LogFactory.getLog(FollowersPage.class);
    private static final By NO_FOLLOWERS_MESSAGE = By.cssSelector("div.viewcolumn p");
    private static final By USERS_LIST = By.xpath(".//div[@class='profile']//ul[1]");
    private static final By FOLLOWERS_COUNT = By.cssSelector("div>a[href='followers']");

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
                    if (driver.findElement(USERS_LIST).isDisplayed())
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                }

                if (driver.findElement(NO_FOLLOWERS_MESSAGE).isDisplayed() || driver.findElement(NO_FOLLOWERS_MESSAGE).getText().equals(getValue("user.profile.followers.nofollowers")))
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

    /**
     * Get the navigation bar.
     *
     * @return {@link ProfileNavigation}
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(driver, factoryPage);
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
            List<WebElement> elements = driver.findElements(USERS_LIST);

            for (WebElement element : elements)
            {
                WebElement result = element.findElement(By.tagName("a"));
                shareLinks.add(new ShareLink(result, driver, factoryPage));
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
            present = findAndWait(NO_FOLLOWERS_MESSAGE).getText().equals(getValue("user.profile.followers.nofollowers"));
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
            count = findAndWait(FOLLOWERS_COUNT).getText().split("[()]+")[1];
        }
        catch (TimeoutException nsee)
        {
            logger.error("Element :" + FOLLOWERS_COUNT + " does not exist", nsee);
        }
        return count;
    }
}
