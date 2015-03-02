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

/**
 * Representation of General Info Section on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class WorkFlowDetailsGeneralInfo
{
    private WorkFlowTitle title;
    private WorkFlowDescription description;
    private String startedBy;
    private DateTime dueDate;
    private String dueDateString;
    private String completed;
    private DateTime completedDate;
    private DateTime startDate;
    private Priority priority;
    private WorkFlowStatus status;
    private String message;

    public WorkFlowTitle getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = WorkFlowTitle.getTitle(title);
    }

    public WorkFlowDescription getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = WorkFlowDescription.getWorkFlowDescription(description);
    }

    public String getStartedBy()
    {
        return startedBy;
    }

    public void setStartedBy(String startedBy)
    {
        this.startedBy = startedBy;
    }

    public DateTime getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(String due)
    {
        try
        {
            this.dueDate = DateTimeFormat.forPattern("E d MMM yyyy").parseDateTime(due);
        }
        catch (IllegalArgumentException e)
        {
            this.dueDate = null;
        }
    }

    public String getDueDateString()
    {
        return dueDateString;
    }

    public void setDueString(String due)
    {
        this.dueDateString = due;

    }

    public String getCompleted()
    {
        return completed;
    }

    public void setCompleted(String completed)
    {
        this.completed = completed;
    }

    public DateTime getCompletedDate()
    {
        return completedDate;
    }

    public void setCompletedDate(String completedDate)
    {
        try
        {
            this.completedDate = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(completedDate);
        }
        catch (IllegalArgumentException e)
        {
            this.completedDate = null;
        }
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        try
        {
            this.startDate = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(startDate);
        }
        catch (IllegalArgumentException e)
        {
            this.startDate = null;
        }
    }

    public Priority getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {

        this.priority = Priority.getPriority(priority);
    }

    public WorkFlowStatus getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = WorkFlowStatus.getWorkFlowStatus(status);
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
