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

import java.util.List;

/**
 * Representation of More Info Section on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class WorkFlowDetailsMoreInfo
{
    private TaskType type;
    private String destination;
    private KeepContentStrategy afterCompletion;
    private boolean lockOnPremise;
    private List<String> assignmentList;
    private SendEMailNotifications notification;

    public TaskType getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = TaskType.getTaskType(type);
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public KeepContentStrategy getAfterCompletion()
    {
        return afterCompletion;
    }

    public void setAfterCompletion(String afterCompletion)
    {
        this.afterCompletion = KeepContentStrategy.getKeepContentStrategy(afterCompletion);
    }

    public boolean isLockOnPremise()
    {
        return lockOnPremise;
    }

    public void setLockOnPremise(String lockOnPremise)
    {
        this.lockOnPremise = lockOnPremise.equals("Yes");
    }

    public List<String> getAssignmentList()
    {
        return assignmentList;
    }

    public void setAssignmentList(List<String> assignmentList)
    {
        this.assignmentList = assignmentList;
    }

    public SendEMailNotifications getNotification()
    {
        return notification;
    }

    public void setNotification(String notification)
    {
        this.notification = SendEMailNotifications.getValue(notification);
    }
}
