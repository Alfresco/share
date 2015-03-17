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

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.webdrone.WebDrone;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of Current Task row on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class WorkFlowDetailsCurrentTask
{
    private CurrentTaskType taskType;
    private String assignedTo;
    private DateTime dueDate;
    private String dueDateString;
    private TaskStatus taskStatus;
    private ShareLink taskDetailsLink;
    private ShareLink editTaskLink;

    private static final By TASK_TYPE = By.cssSelector("td.yui-dt-col-name.yui-dt-first");
    private static final By ASSIGNED_TO = By.cssSelector("td.yui-dt-col-owner");
    private static final By DUE_DATE = By.cssSelector("td.yui-dt-col-id");
    private static final By STATUS = By.cssSelector("td.yui-dt-col-state");
    private static final By TASK_DETAILS_LINK = By.cssSelector("td.yui-dt-col-properties a.task-details");
    private static final By EDIT_TASK_LINK = By.cssSelector("td.yui-dt-col-properties a.task-edit");

    public WorkFlowDetailsCurrentTask(WebElement element, WebDrone drone)
    {
        taskType = CurrentTaskType.getCurrentTaskType(element.findElement(TASK_TYPE).getText());
        assignedTo = element.findElement(ASSIGNED_TO).getText();
        try
        {
            dueDate = DateTimeFormat.forPattern("E d MMM yyyy").parseDateTime(element.findElement(DUE_DATE).getText());
        }
        catch (IllegalArgumentException ie)
        {
            dueDate = null;
        }
        dueDateString = element.findElement(DUE_DATE).getText();
        taskStatus = TaskStatus.getTaskFromString(element.findElement(STATUS).getText());
        taskDetailsLink = new ShareLink(element.findElement(TASK_DETAILS_LINK), drone);
        editTaskLink = new ShareLink(element.findElement(EDIT_TASK_LINK), drone);
    }

    public CurrentTaskType getTaskType()
    {
        return taskType;
    }

    public String getAssignedTo()
    {
        return assignedTo;
    }

    public DateTime getDueDate()
    {
        return dueDate;
    }

    public String getDueDateString()
    {
        return dueDateString;
    }

    public TaskStatus getTaskStatus()
    {
        return taskStatus;
    }

    public ShareLink getTaskDetailsLink()
    {
        return taskDetailsLink;
    }

    public ShareLink getEditTaskLink()
    {
        return editTaskLink;
    }
}
