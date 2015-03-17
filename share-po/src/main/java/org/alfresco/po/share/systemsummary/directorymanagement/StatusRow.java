/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share.systemsummary.directorymanagement;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Object associated with rows in sync status table. Can't interacting with rows.
 * Methods only return information.
 *
 * @author Aliaksei Boole
 */
public class StatusRow extends HtmlElement
{
    private final static By BEAN_NAME = By.xpath("./td[1]");
    private final static By SYNC_TIME = By.xpath("./td[2]");
    private final static By STATUS_COUNT = By.xpath("./td[3]");
    private final static By TOTAL_COUNT = By.xpath("./td[4]");

    public StatusRow(WebElement webElement, WebDrone drone)
    {
        super(webElement, drone);
    }


    public String getBeanNameInfo()
    {
        try
        {
            return findAndWait(BEAN_NAME).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getBeanNameInfo();
        }
    }

    public String getSyncTimeInfo()
    {
        try
        {
            return findAndWait(SYNC_TIME).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getSyncTimeInfo();
        }
    }

    public String getStatusCountInfo()
    {
        try
        {
            return findAndWait(STATUS_COUNT).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getStatusCountInfo();
        }
    }

    public String getTotalCount()
    {
        return findAndWait(TOTAL_COUNT).getText();
    }

    /**
     * Return true if all information in row displayed.
     *
     * @return
     */
    public boolean isAllInfoDisplayed()
    {
        boolean allOk;
        String beanNameInfo = getBeanNameInfo();
        String syncTimeInfo = getSyncTimeInfo();
        String statusCountInfo = getStatusCountInfo();
        String totalCount = getTotalCount();
        allOk = !(beanNameInfo == null || beanNameInfo.isEmpty());
        allOk = allOk && !(syncTimeInfo == null || syncTimeInfo.isEmpty());
        allOk = allOk && !(statusCountInfo == null || statusCountInfo.isEmpty());
        allOk = allOk && !(totalCount == null || totalCount.isEmpty());
        return allOk;
    }

}
