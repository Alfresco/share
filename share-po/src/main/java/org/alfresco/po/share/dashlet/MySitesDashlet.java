/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * My site dashlet object, holds all element of the HTML page relating to share's my site dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MySitesDashlet extends AbstractDashlet implements Dashlet
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String DATA_LIST_CSS_LOCATION = "h3.site-title > a";
    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.my-sites";
    private static final String DASHLET_CONTENT_DIV_ID_PLACEHOLDER = "div[id$='default-sites']";
    private static final String DASHLET_EMPTY_PLACEHOLDER = "table>tbody>tr>td.yui-dt-empty>div";
    private static final String ROW_OF_SITE = "div[id$='default-sites'] tr[class*='yui-dt-rec']";
    private static final String SITE_NAME_IN_ROW = "div h3[class$='site-title']";
    private static final String DELETE_SYMB_IN_ROW = "a[class*='delete-site']";
    private static final String MY_SITES_BUTTON = "div[class*='my-sites'] div span span button";
    private static final String SITES_TYPE = "div[class*='my-sites'] div.bd ul li a";
    private static final String DELETE_CONFIRM = "div#prompt div.ft span span button";
    private static final By CREATE_SITE = By.cssSelector("div[class*='my-sites'] div span span a");
    private static final String FAVORITE_SYMB_IN_ROW = "a[class*='favourite-action']";

    /**
     * Constructor.
     */
    protected MySitesDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
        
        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    public MySitesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MySitesDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Implemented a new render method with optionally waiting for 'Loading...' message to go away
     */
    @SuppressWarnings("unchecked")
    public MySitesDashlet render(RenderTime timer)
    {
        return render(timer, true);
    }

    /**
     * The active sites displayed on my site dashlet.
     * 
     * @return List<ShareLink> site links
     */
    public synchronized List<ShareLink> getSites()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Retrieves the link that match the site name.
     * 
     * @param name identifier
     * @return {@link ShareLink} that matches siteName
     */
    public synchronized ShareLink selectSite(final String name)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        List<ShareLink> shareLinks = getList(DATA_LIST_CSS_LOCATION);
        for (ShareLink link : shareLinks)
        {
            if (name.equalsIgnoreCase(link.getDescription()))
            {
                return link;
            }
        }
        throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
    }

    /**
     * Render logic to determine if loaded and ready for use.
     * 
     * @param timer - {@link RenderTime}
     * @param waitForLoading boolean to whether check for waiting for Loading text to disappear.
     * @return {@link MySitesDashlet}
     */
    public MySitesDashlet render(RenderTime timer, boolean waitForLoading)
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    WebElement dashlet = drone.find(By.cssSelector(DASHLET_CONTENT_DIV_ID_PLACEHOLDER));
                    if (dashlet.isDisplayed())
                    {
                        this.dashlet = drone.find(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
                        if (waitForLoading)
                        {
                            if (!isLoading(dashlet))
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }

                }
                catch (NoSuchElementException e)
                {
                    logger.info("Unable to find the dashlet container " + e);
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    private boolean isLoading(WebElement dashletPlaceholder)
    {
        try
        {
            WebElement sitesDash = dashletPlaceholder.findElement(By.cssSelector(DASHLET_EMPTY_PLACEHOLDER));
            if (sitesDash.isDisplayed() && sitesDash.getText().startsWith("Loading..."))
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Checks the site is favourite.
     * 
     * @param siteName Site Name checked for is in Favourite.
     * @return boolean
     */
    public boolean isSiteFavourite(String siteName)
    {
        try
        {
            if (siteName == null)
            {
                throw new UnsupportedOperationException("Name of the site is required");
            }
            WebElement siteRow = getSiteRow(siteName);

            // If site is favourite, anchor does not contain any text. Checking
            // length of text rather than string 'Favourite' to support i18n.
            return !(siteRow.findElement(By.cssSelector("div > span > a")).getText().length() > 1);
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Returns the div that hold the site info.
     * 
     * @return WebElement
     */
    private WebElement getSiteRow(String siteName)
    {

        return this.dashlet.findElement(By.xpath("//h3[a='" + siteName + "']/.."));
    }

    public enum FavouriteType
    {
        ALL, 
        MyFavorites
        {
            public String toString()
            {
                return "My Favorites";
            }
        },
        Recent;
    }

    /**
     * Delete site from the delete symbol of My site Dashlets.
     * 
     * @param siteName String
     * @return HtmlPage
     */
    public HtmlPage deleteSite(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(ROW_OF_SITE));
            for (WebElement webElement : elements)
            {
                if (webElement.findElement(By.cssSelector(SITE_NAME_IN_ROW)).getText().equals(siteName))
                {
                    drone.mouseOver(webElement);
                    webElement.findElement(By.cssSelector(DELETE_SYMB_IN_ROW)).click();
                    return confirmDelete();
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("My Site  Dashlet is not present", nse);
        }
        throw new PageOperationException("My Site  Dashlet is not present in the page.");
    }

    /**
     * Confirm delete dialog acceptance action.
     */
    private HtmlPage confirmDelete()
    {
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(DELETE_CONFIRM));
            WebElement delete = findButton("Delete", elements);
            delete.click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Delete dialouge not present");
        }
        return finalConfirmation();
    }

    /**
     * Final step to confirm delete dialog acceptance action.
     */

    private HtmlPage finalConfirmation()
    {
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(DELETE_CONFIRM));
            WebElement button = findButton("Yes", elements);
            button.click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Delete dialouge not present");
        }
        if (canResume())
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Site message indicating site deleted has been displayed");
            }
        }

        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Select My Favourties sites from My Sites Dashlets.
     * 
     * @param type FavouriteType
     * @return HtmlPage
     */
    public HtmlPage selectMyFavourites(FavouriteType type)
    {
        if (type == null)
        {
            throw new UnsupportedOperationException("Favpurite type is required");
        }
        try
        {
            WebElement myFavourties = drone.find(By.cssSelector(MY_SITES_BUTTON));
            myFavourties.click();
            List<WebElement> types = drone.findAll(By.cssSelector(SITES_TYPE));
            for (WebElement typeFav : types)
            {
                if (type.toString().equalsIgnoreCase(typeFav.getText()))
                {
                    typeFav.click();
                    return FactorySharePage.resolvePage(drone);
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("My Sites option not present" + nse.getMessage());
        }
        throw new PageOperationException("My Site DashLets not present.");
    }

    /**
     * Select favorite for a site in My site Dashlets.
     * 
     * @param siteName String
     */
    public void selectFavorite(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(ROW_OF_SITE));
            for (WebElement webElement : elements)
            {
                if (webElement.findElement(By.cssSelector(SITE_NAME_IN_ROW)).getText().equals(siteName))
                {
                    drone.mouseOver(webElement);
                    webElement.findElement(By.cssSelector(FAVORITE_SYMB_IN_ROW)).click();
                    break;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("My Site  Dashlet is not present", nse);
        }
    }

    /**
     * Method to verify Create Site is displayed
     */
    public boolean isCreateSiteButtonDisplayed()
    {
        try
        {
            return drone.findAndWait(CREATE_SITE).isDisplayed();
        }
        catch (TimeoutException elementException)
        {

        }
        return false;
    }

    /**
     * Click on Create Site button
     */
    public CreateSitePage clickCreateSiteButton()
    {
        try
        {
            drone.find(CREATE_SITE).click();
//            return drone.getCurrentPage().render();
            return new CreateSitePage(drone).render();

        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find the New Topic icon.", nse);

        }
        throw new PageOperationException("Unable to click the New Topic icon");
    }

    /**
     * Method to check if a site name is displayed in My Sites Dashlet
     *
     * @param siteName String
     * @return True if Site exists
     */
    public boolean isSitePresent(String siteName)
    {
        List<ShareLink> siteLinks = getSites();
        try
        {
            for (ShareLink siteLink : siteLinks)
            {
                if (siteLink.getDescription().contains(siteName))
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

}
