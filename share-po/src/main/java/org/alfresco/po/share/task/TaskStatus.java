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
package org.alfresco.po.share.task;

/**
 * This enums used to describe the task status.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public enum TaskStatus
{
    NOTYETSTARTED("Not Yet Started"), 
    INPROGRESS("In Progress"), 
    ONHOLD("On Hold"), 
    CANCELLED("Cancelled"), 
    COMPLETED("Completed");

    private String taskName;

    TaskStatus(String taskName)
    {
        this.taskName = taskName;
    }

    public String getTaskName()
    {
        return taskName;
    }

    /**
     * Returns the TaskStatus from string value.
     * 
     * @param value - string value of enum eg - "Not Yet Started"
     * @return
     */
    public static TaskStatus getTaskFromString(String value)
    {
        for (TaskStatus status : TaskStatus.values())
        {
            if (value.equalsIgnoreCase(status.taskName))
            {
                return status;
            }
        }
        return null;
    }

}