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
package org.alfresco.po.share.task;

import java.util.List;

import org.alfresco.po.share.workflow.TaskDetailsType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Representation of Task details that can be used to verify Task details
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
public class TaskDetails
{

    private String taskName;
    private String due;
    private DateTime startDate;
    private DateTime endDate;
    private String status;
    private TaskDetailsType type;
    private String description;
    private String startedBy;
    private boolean isEditTaskDisplayed;
    private boolean isViewTaskDisplayed;
    private boolean isViewWorkFlowDisplayed;
    private List<String> taskLabels;

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getDue()
    {
        return due;
    }

    public void setDue(String due)
    {
        this.due = due;
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = DateTimeFormat.forPattern("dd MMMMM, yyyy").parseDateTime(startDate);
    }

    public DateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = DateTimeFormat.forPattern("dd MMMMM, yyyy").parseDateTime(endDate);
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public TaskDetailsType getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = TaskDetailsType.getTaskDetailsType(type);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public List<String> getTaskLabels()
    {
        return taskLabels;
    }

    public void setTaskLabels(List<String> taskLabels)
    {
        this.taskLabels = taskLabels;
    }

    public String getStartedBy()
    {
        return startedBy;
    }

    public void setStartedBy(String startedBy)
    {
        this.startedBy = startedBy;
    }

    public boolean isEditTaskDisplayed()
    {
        return isEditTaskDisplayed;
    }

    public void setEditTaskDisplayed(boolean isEditTaskDisplayed)
    {
        this.isEditTaskDisplayed = isEditTaskDisplayed;
    }

    public boolean isViewTaskDisplayed()
    {
        return isViewTaskDisplayed;
    }

    public void setViewTaskDisplayed(boolean isViewTaskDisplayed)
    {
        this.isViewTaskDisplayed = isViewTaskDisplayed;
    }

    public boolean isViewWorkFlowDisplayed()
    {
        return isViewWorkFlowDisplayed;
    }

    public void setViewWorkFlowDisplayed(boolean isViewWorkFlowDisplayed)
    {
        this.isViewWorkFlowDisplayed = isViewWorkFlowDisplayed;
    }
}
