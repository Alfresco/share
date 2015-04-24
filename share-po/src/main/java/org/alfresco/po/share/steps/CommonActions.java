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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

public class CommonActions
{
    public static long refreshDuration = 25000;
    protected static final String MY_DASHBOARD = " Dashboard";
    public final static String DOCLIB = "DocumentLibrary";

    /**
     * Checks if driver is null, throws UnsupportedOperationException if so.
     *
     * @param driver WebDrone Instance
     * @throws UnsupportedOperationException if driver is null
     */
    public void checkIfDriverIsNull(WebDrone driver)
    {
        if (driver == null)
        {
            throw new UnsupportedOperationException("WebDrone is required");
        }
    }
    
    /**
     * Checks if the current page is share page, throws PageException if not.
     *
     * @param driver WebDrone Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
     */
    public SharePage getSharePage(WebDrone driver)
    {
        checkIfDriverIsNull(driver);
        try
        {
            HtmlPage generalPage = driver.getCurrentPage().render(refreshDuration);
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
     * @param driver WebDrone Instance
     * @return HtmlPage
     * */
    public HtmlPage refreshSharePage(WebDrone driver)
    {
        checkIfDriverIsNull(driver);
        driver.refresh();
        return getSharePage(driver);
    }

    /**
     * Common method to wait for the next solr indexing cycle.
     * 
     * @param driver WebDrone Instance
     * @param waitMiliSec Wait duration in milliseconds
     */
    public HtmlPage webDriverWait(WebDrone driver, long waitMiliSec)
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
}
