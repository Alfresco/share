/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.dynamic-welcome")
/**
 * Site Welcome dashlet object, holds all element of the HTML relating to site welcome dashlet.
 *
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class SiteWelcomeDashlet extends AbstractDashlet implements Dashlet
{
    private static final String REMOVE_WELCOME_DASHLET = ".alf-welcome-hide-button";
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.dynamic-welcome");
    private static final String PROMPT_PANEL_ID = "prompt.panel.id";
    private static final String OPTIONS_CSS_LOCATION = ".welcome-details-column-info>a";
    private static Log logger = LogFactory.getLog(SiteWelcomeDashlet.class);

    @SuppressWarnings("unchecked")
    public SiteWelcomeDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(DASHLET_CONTAINER_PLACEHOLDER);
            while (true)
            {
                timer.start();
                synchronized(this)
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
                    dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("== found it == " + dashlet.isDisplayed());
                    }
                    break;

                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteWelcomeDashlet dashlet was not found ", e);
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
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find welcome dashlet", te);
        }
        return this;
    }

    /**
     * Find Welcome Dashlet remove button and close the dashlet.
     *
     * @return {@link HtmlPage} page response
     */
    public HtmlPage removeDashlet()
    {
        WebElement removeElement = dashlet.findElement(By.cssSelector(REMOVE_WELCOME_DASHLET));
        removeElement.click();
        confirmRemoval();
        return getCurrentPage();
    }

    /**
     * Final step to confirm delete dialog acceptance action.
     */
    protected  void confirmRemoval()
    {
        WebElement prompt = findAndWaitById(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        // Find the delete button in the prompt
        WebElement button = elements.get(0);
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
    public  List<ShareLink> getOptions()
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
            return driver.findElement(DASHLET_CONTAINER_PLACEHOLDER).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException ste)
        {
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteWelcomeDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
