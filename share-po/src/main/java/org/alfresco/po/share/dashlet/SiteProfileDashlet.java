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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.site-profile")
/**
 * Page object to represent Site Profile dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteProfileDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteSearchDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-profile");
    private static final By DASHLET_CONTENT = By.cssSelector(".msg");
//
//    /**
//     * Constructor.
//     */
//    protected SiteProfileDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector("div.dashlet.site-profile .yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public SiteProfileDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                synchronized (this)
                {
                    timer.start();

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
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteSearchDashlet dashlet was not found ", e);
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
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site links dashlet", te);
        }
        return this;
    }

    /**
     * This method gets the focus by placing mouse over on Site Wiki Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to get contents from the dashlet
     *
     * @return String value
     */
    public String getContent()
    {
        getFocus();
        WebElement content = findAndWait(DASHLET_CONTENT);
        return content.getText();
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteProfileDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
