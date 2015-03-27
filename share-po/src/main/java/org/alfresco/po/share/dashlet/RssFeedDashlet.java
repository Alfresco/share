/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to hold RSS Feed dashlet
 *
 * @author Marina.Nenadovets
 */

public class RssFeedDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=0 and contains(@class,'rssfeed')]");
    private static final By titleBarActions = By.cssSelector(".titleBarActions");

    /**
     * Constructor.
     */
    protected RssFeedDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.xpath(".//div[contains(@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized RssFeedDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RssFeedDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RssFeedDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site RSS Feed Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to click Configure icon from RSS Feed dashlet
     *
     * @return RssFeedUrlBoxPage
     */
    public RssFeedUrlBoxPage clickConfigure()
    {
        try
        {
            drone.mouseOver(drone.find(titleBarActions));
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return new RssFeedUrlBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find configure button");
        }
    }

    /**
     * Method to get the headline sites from the dashlet
     *
     * @return List<String>
     */
    public List<ShareLink> getHeadlineLinksFromDashlet()
    {
        List<ShareLink> rssLinks = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(By.cssSelector(".headline>h4>a"));
            for (WebElement div : links)
            {
                rssLinks.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }

        return rssLinks;
    }
}
