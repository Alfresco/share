/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.share.steps;

/**
 * Class contains Common user steps / actions / utils for regression tests
 * 
 *  @author mbhave
 */

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommonActions
{
    public static long refreshDuration = 25000;
    protected static final String MY_DASHBOARD = " Dashboard";
    public final static String DOCLIB = "DocumentLibrary";
    @Autowired protected FactoryPage factoryPage;

    /**
     * Checks if driver is null, throws UnsupportedOperationException if so.
     *
     * @param driver WebDriver Instance
     * @throws UnsupportedOperationException if driver is null
     */
    public void checkIfDriverIsNull(WebDriver driver)
    {
        if (driver == null)
        {
            throw new UnsupportedOperationException("WebDriver is required");
        }
    }
    
    /**
     * Checks if the current page is share page, throws PageException if not.
     *
     * @param driver WebDriver Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
     */
    public SharePage getSharePage(WebDriver driver)
    {
        checkIfDriverIsNull(driver);
        try
        {
            HtmlPage generalPage = factoryPage.getPage(driver);
            return (SharePage) generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Can not cast to SharePage: Current URL: " + driver.getCurrentUrl());
        }
    }
    
    /**
     * Refreshes and returns the current page: throws PageException if not a share page.
     * 
     * @param driver WebDriver Instance
     * @return HtmlPage
     * */
    public HtmlPage refreshSharePage(WebDriver driver)
    {
        checkIfDriverIsNull(driver);
        driver.navigate().refresh();
        return getSharePage(driver);
    }

    /**
     * Common method to wait for the next solr indexing cycle.
     * 
     * @param driver WebDriver Instance
     * @param waitMiliSec Wait duration in milliseconds
     */
    public HtmlPage webDriverWait(WebDriver driver, long waitMiliSec)
    {
        checkIfDriverIsNull(driver);

        synchronized (this)
        {
            try
            {
                this.wait(waitMiliSec);
            }
            catch (InterruptedException e)
            {
                // Discussed not to throw any exception
            }
        }
        return getSharePage(driver);
    }
    /**
     * Navigate to User DashBoard page and waits for the page render to
     * complete. Assumes User is logged in
     * 
     * @param driver WebDriver Instance
     * @return DashBoardPage
     */
    public DashBoardPage refreshUserDashboard(WebDriver driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);
        return page.getNav().selectMyDashBoard().render();
    }

    /**
     * Navigate to User DashBoard and waits for the page render to complete.
     * Assumes User is logged in
     * 
     * @param driver WebDriver Instance
     * @return DashBoardPage
     */
    public DashBoardPage openUserDashboard(WebDriver driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);
        if (page.getPageTitle().contains(MY_DASHBOARD))
        {
            return (DashBoardPage) page;
        }

        return refreshUserDashboard(driver);
    }
}
