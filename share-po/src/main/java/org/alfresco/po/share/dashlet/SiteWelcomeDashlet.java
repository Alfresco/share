/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Site Welcome dashlet object, holds all element of the HTML relating to site welcome dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class SiteWelcomeDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteWelcomeDashlet.class);
    private static final String REMOVE_WELCOME_DASHLET = ".welcome-close-button";
    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.dynamic-welcome";
    private static final String PROMPT_PANEL_ID = "prompt.panel.id";
    private static final String OPTIONS_CSS_LOCATION = ".welcome-details-column-info>a";

    /**
     * Constructor.
     */
    protected SiteWelcomeDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteWelcomeDashlet render(RenderTime timer)
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
                    if (isWelcomeMessageDisplayed())
                    {
                        dashlet = drone.find(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
                        if (logger.isTraceEnabled())
                        {
                            logger.trace("== found it == " + dashlet.isDisplayed());
                        }
                        break;
                    }
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find welcome dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteWelcomeDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteWelcomeDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Find Welcome Dashlet remove button and close the dashlet.
     * 
     * @return {@link HtmlPage} page response
     */
    public synchronized HtmlPage removeDashlet()
    {
        WebElement removeElement = dashlet.findElement(By.cssSelector(REMOVE_WELCOME_DASHLET));
        removeElement.click();
        confirmRemoval();
        return new SiteDashboardPage(drone);
    }

    /**
     * Final step to confirm delete dialog acceptance action.
     */
    protected synchronized void confirmRemoval()
    {
        WebElement prompt = drone.findAndWaitById(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        // Find the delete button in the prompt
        WebElement button = findButton("Yes", elements);
        button.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Final Welcome Daashlet Removal button has been found and selected");
        }
    }

    /**
     * The Welcome Dashlet shows some options on this Dashlet.
     * 
     * @return {@link List}<ShareLink> site links
     */
    public synchronized List<ShareLink> getOptions()
    {
        return getList(OPTIONS_CSS_LOCATION);
    }

    /**
     * Check if welcome message is displayed.
     * 
     * @return true if displayed.
     */
    public boolean isWelcomeMessageDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException ste)
        {
        }
        return false;
    }
}