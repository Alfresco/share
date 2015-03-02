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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * User Notification page object has a checkbox to enable and disable
 * notification feed emails.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public class UserSitesPage extends SharePage
{
    private static final By NO_SITES_MESSAGE = By.cssSelector("div.viewcolumn p");
    private static final By SITES_LIST = By.cssSelector("ul[id$='default-sites'] li");

    private final Log logger = LogFactory.getLog(UserSitesPage.class);

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public UserSitesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserSitesPage render(RenderTime timer)
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
                    if (drone.find(SITES_LIST).isDisplayed())
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                }

                if (drone.find(NO_SITES_MESSAGE).isDisplayed() || drone.find(NO_SITES_MESSAGE).getText().equals(drone.getValue("user.profile.sites.nosite")))
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
    public UserSitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserSitesPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Get the navigation bar.
     * 
     * @return
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }

    /**
     * Return <code>true</code> if the No Site message is displayed on screen.
     * 
     * @return
     */
    public boolean isNoSiteMessagePresent()
    {
        boolean present = false;
        try
        {
            present = drone.find(NO_SITES_MESSAGE).getText().equals(drone.getValue("user.profile.sites.nosite"));

            if (present && drone.find(SITES_LIST).isDisplayed())
            {
                throw new PageException("No Sites message and site list displayed at the same time.");
            }

            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    /**
     * Get a list of sites.
     * 
     * @return A {@link List} of {@link UserSiteItem}.
     */
    public List<UserSiteItem> getSites()
    {
        List<UserSiteItem> sites = new ArrayList<>();
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(SITES_LIST);

            for (WebElement el : elements)
            {
                UserSiteItem site = new UserSiteItem(el, drone);
                sites.add(site);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find any sites in " + SITES_LIST, e);
        }
        return sites;
    }

    /**
     * Get a {@link UserSiteItem} for the named site
     * 
     * @param siteName
     * @return
     */
    public UserSiteItem getSite(String siteName)
    {
        List<UserSiteItem> sites = getSites();

        for (UserSiteItem site : sites)
        {
            if (site.getSiteName().equals(siteName))
            {
                return site;
            }
        }

        throw new PageOperationException("Unable to find site: " + siteName);
    }
}