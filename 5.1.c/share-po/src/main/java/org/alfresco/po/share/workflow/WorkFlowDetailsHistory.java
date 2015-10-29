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
package org.alfresco.po.share.workflow;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Representation of History rows on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class WorkFlowDetailsHistory
{
    private WorkFlowHistoryType type;
    private String completedBy;
    private DateTime completedDate;
    private String completedDateString;
    private WorkFlowHistoryOutCome outcome;
    private String comment;

    private static final By TYPE = By.cssSelector("td.yui-dt-col-name.yui-dt-first");
    private static final By COMPLETED_BY = By.cssSelector("td.yui-dt-col-owner");
    private static final By DATE_COMPLETED = By.cssSelector("td.yui-dt-col-id");
    private static final By OUTCOME = By.cssSelector("td.yui-dt-col-state");
    private static final By COMMENT = By.cssSelector("td.yui-dt-col-properties");

    public WorkFlowDetailsHistory(WebElement element)
    {
        try
        {
            type = WorkFlowHistoryType.getWorkFlowHistoryType(element.findElement(TYPE).getText());
            completedBy = element.findElement(COMPLETED_BY).getText();
            try
            {
                completedDate = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(element.findElement(DATE_COMPLETED).getText());
            }
            catch (IllegalArgumentException ie)
            {
                completedDate = null;
            }
            completedDateString = element.findElement(DATE_COMPLETED).getText();
            outcome = WorkFlowHistoryOutCome.getWorkFlowHistoryOutCome(element.findElement(OUTCOME).getText());
            comment = element.findElement(COMMENT).getText();
        }
        catch (NoSuchElementException nse)
        {
            type = null;
            completedBy = null;
            completedDate = null;
            completedDateString = null;
            outcome = null;
            comment = null;
        }
    }

    public WorkFlowHistoryType getType()
    {
        return type;
    }

    public String getCompletedBy()
    {
        return completedBy;
    }

    public DateTime getCompletedDate()
    {
        return completedDate;
    }

    public String getDueCompletedString()
    {
        return completedDateString;
    }

    public WorkFlowHistoryOutCome getOutcome()
    {
        return outcome;
    }

    public String getComment()
    {
        return comment;
    }
}
