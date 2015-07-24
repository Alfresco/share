/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */

package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold Content I'm Editing dashlet
 * Created by olga lokhach
 */
public class EditingContentDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div[id$='-my-docs-dashlet']");
    private static final By ITEMS_DETAILS = By.cssSelector(".detail-list-item>div.details");
    private static final By ITEM_LINKS = By.cssSelector(".detail-list-item>div.details>h4>a");
    private static final By SITE_LINKS = By.cssSelector(".detail-list-item>div.details>div>a[class$='site-link']");
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     */
    protected EditingContentDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditingContentDashlet render(RenderTime timer)
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
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = drone.find((DASHLET_CONTAINER_PLACEHOLDER));
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for EditingContentDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
                }

                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find Content I'm Editing dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditingContentDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditingContentDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    private List<WebElement> getItemsDetailsElem()
    {
        try
        {
            return dashlet.findElements(ITEMS_DETAILS);
        }
        catch (StaleElementReferenceException e)
        {
            return getItemsDetailsElem();
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Return true if item link with the details is displayed.
     *
     * @param itemName String
     * @param siteName String
     * @return boolean
     */
    public boolean isItemWithDetailDisplayed(String itemName, String siteName)
    {
        checkNotNull(itemName);
        List<WebElement> itemLinks = getItemsDetailsElem();
        for (WebElement itemLink : itemLinks)
        {
            String linkText = itemLink.getText();
            if (linkText.contains(itemName) && linkText.contains(siteName))
            {
                return itemLink.isDisplayed();
            }
        }
        return false;
    }

    private List<WebElement> getItemLinksElem()
    {
        try
        {
            return dashlet.findElements(ITEM_LINKS);
        }
        catch (StaleElementReferenceException e)
        {
            return getItemLinksElem();
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Click on item
     *
     * @param itemName String
     * @return HTMLPage
     */
    public HtmlPage clickItem(String itemName)
    {
        checkNotNull(itemName);
        List<WebElement> itemLinks = getItemLinksElem();
        for (WebElement itemLink : itemLinks)
        {
            String linkText = itemLink.getText();
            if (linkText.equalsIgnoreCase(itemName))
            {
                itemLink.click();
                return FactorySharePage.resolvePage(drone);
            }
        }

        throw new PageOperationException(itemName + "' was not found!");
    }

    private List<WebElement> getSitesDetailsElem()
    {
        try
        {
            return dashlet.findElements(SITE_LINKS);
        }
        catch (StaleElementReferenceException e)
        {
            return getSitesDetailsElem();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Click on site
     *
     * @param siteName String
     * @return SitePage
     */
    public SiteDashboardPage clickSite(String siteName)
    {
        checkNotNull(siteName);
        List<WebElement> eventLinks = getSitesDetailsElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(siteName))
            {
                eventLink.click();
                return new SiteDashboardPage(drone);
            }
        }

        throw new PageOperationException("Site '" + siteName + "' was not found!");
    }


}


