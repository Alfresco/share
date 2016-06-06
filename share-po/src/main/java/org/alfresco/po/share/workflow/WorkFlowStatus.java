package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow Status
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowStatus
{

    WORKFLOW_IN_PROGRESS("Workflow is in Progress"),
    WORKFLOW_COMPLETE("Workflow is Complete"),
    TASK_IN_PROGRESS("Task is in Progress"),
    TASK_COMPLETE("Task is Complete");

    private String status;

    WorkFlowStatus(String description)
    {
        this.status = description;
    }

    public String getDescription()
    {
        return status;
    }

    /**
     * Returns {@link WorkFlowStatus} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowStatus}
     */
    public static WorkFlowStatus getWorkFlowStatus(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowStatus workFlowStatus : WorkFlowStatus.values())
        {
            if (value.equals(workFlowStatus.status))
            {
                return workFlowStatus;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowStatus Value : " + value);
    }
}
