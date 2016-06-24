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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(xpath="//div[count(./div[@class='toolbar'])=0 and contains(@class,'rssfeed')]")
/**
 * Page object to hold RSS Feed dashlet
 *
 * @author Marina.Nenadovets
 */
public class RssFeedDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=0 and contains(@class,'rssfeed')]");
    private static final By titleBarActions = By.cssSelector(".titleBarActions");

//    /**
//     * Constructor.
//     */
//    protected RssFeedDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.xpath(".//div[contains(@class, 'yui-resize-handle')]"));
//    }


    /**
     * This method gets the focus by placing mouse over on Site RSS Feed Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
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
            mouseOver(driver.findElement(titleBarActions));
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return factoryPage.instantiatePage(driver,RssFeedUrlBoxPage.class).render();
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
            List<WebElement> links = driver.findElements(By.cssSelector(".headline>h4>a"));
            for (WebElement div : links)
            {
                rssLinks.add(new ShareLink(div, driver, factoryPage));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }

        return rssLinks;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RssFeedDashlet render(RenderTime timer)
    {
        return render(timer);
    }
    @SuppressWarnings("unchecked")
    @Override
    public RssFeedDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
