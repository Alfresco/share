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

import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dashboard page object, holds all element of the HTML page relating to share's
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class DashBoardPage extends SharePage implements Dashboard
{
	@Autowired FactoryShareDashlet factoryDashlet;
	private final Log logger = LogFactory.getLog(DashBoardPage.class);
    @RenderWebElement
    MySitesDashlet mySitesDashlet;
    @RenderWebElement
    MyDocumentsDashlet myDocumentsDashlet;
    @RenderWebElement
    MyActivitiesDashlet myActivitiesDashlet;

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
        return factoryDashlet.getPage(driver, name);
    }
    
    /**
     * Click the 'View the tutorials' link
     */
    public void clickTutorialsLink()
    {
        try
        {
            findAndWait(By.xpath("//span[text()='View the tutorials']")).click();
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
