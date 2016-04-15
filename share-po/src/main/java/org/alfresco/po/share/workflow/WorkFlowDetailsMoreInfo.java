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
