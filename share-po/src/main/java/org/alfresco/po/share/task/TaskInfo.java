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

import org.alfresco.po.share.workflow.Priority;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Representation of Info Section on Task details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class TaskInfo
{
    private String message;
    private String owner;
    private Priority priority;
    private DateTime dueDate;
    private String dueDateString;
    private String identifier;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {

        this.priority = Priority.getPriority(priority);
    }

    public DateTime getDueDate()
    {
        return dueDate;
    }

    // TODO - ALF-20756 raised on inconsistent date format
    public void setDueDate(String due)
    {
        try
        {
            try
            {
                this.dueDate = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime(due);
            }
            catch (IllegalArgumentException e)
            {
                try
                {
                    this.dueDate = DateTimeFormat.forPattern("E d MMM yyyy").parseDateTime(due);
                }
                catch (IllegalArgumentException e2)
                {
                    this.dueDate = DateTimeFormat.forPattern("d MMM, yyyy").parseDateTime(due);
                }
            }
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

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
}
