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
     * @return TaskStatus
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