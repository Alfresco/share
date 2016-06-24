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
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.wiki")
/**
 * Page object to represent site wiki dashlet
 *
 * @author Marina.Nenadovets
 */
public class WikiDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(WikiDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.wiki");
    private static final By TEXT_IN_DASHLET = By.cssSelector("div[class^=body]>div>*");

//    /**
//     * Constructor.
//     */
//    protected WikiDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public WikiDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(By.cssSelector(".yui-resize-handle"));
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
                    getFocus();
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.info("The Dashlate was not found " + e);
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
     * Method to click Configure icon
     *
     * @return SelectWikiDialogueBoxPage
     */
    public SelectWikiDialogueBoxPage clickConfigure()
    {
        try
        {
            getFocus();
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return factoryPage.instantiatePage(driver, SelectWikiDialogueBoxPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("timed out finding " + CONFIGURE_DASHLET_ICON);
        }
    }

    /**
     * Return content text from dashlet
     *
     * @return String
     */
    public String getContent()
    {
        try
        {
            return dashlet.findElement(TEXT_IN_DASHLET).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getContent();
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public WikiDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
