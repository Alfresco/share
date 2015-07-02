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
package org.alfresco.po.share;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Dashboard page object, holds all element of the HTML page relating to share's
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class DashBoardPage extends SharePage implements Dashboard
{

    private final Log logger = LogFactory.getLog(DashBoardPage.class);

    /**
     * Constructor.
     */
    public DashBoardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DashBoardPage render(RenderTime timer)
    {
        basicRender(timer);
        // We don't know if the dashlets will appear so do the basic rendering
        try
        {
            getDashlet("my-sites").render(timer);
            getDashlet("my-documents").render(timer);
            getDashlet("activities").render(timer);
        }
        catch (PageException pe)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", pe);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DashBoardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DashBoardPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if home page banner web element is present
     *
     * @return true if exists
     */
    public boolean titlePresent()
    {
        try
        {
            return getPageTitle().contains("Dashboard");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        return false;
    }

    /**
     * Gets dashlets in the dashboard page.
     *
     * @param name String title of dashlet
     * @return HtmlPage page object
     */
    public Dashlet getDashlet(final String name)
    {
        return FactoryShareDashlet.getPage(drone, name);
    }
    
    /**
     * Click the 'View the tutorials' link
     */
    public void clickTutorialsLink()
    {
        try
        {
            drone.findAndWait(By.xpath("//span[text()='View the tutorials']")).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find tutorials link.", ex);
            throw new PageException("Unable to find tutorials link");
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find tutorials link.", e);
            throw new PageOperationException("Not able to find the tutorials link");
        }  
    }
}