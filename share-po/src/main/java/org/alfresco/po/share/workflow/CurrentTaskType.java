package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the Task/WorkFlow Current Task Type
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum CurrentTaskType
{
    TASK("Task"),
    REVIEW("Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    TASK_COMPLETED("Task Completed"),
    VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD("Verify task was completed on the cloud"),
    DOCUMENT_WAS_REJECTED_ON_CLOUD("Document was rejected on the cloud"),
    DOCUMENT_WAS_APPROVED_ON_CLOUD("Document was approved on the cloud");

    private String type;

    CurrentTaskType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /**
     * Returns {@link CurrentTaskType} based on given value.
     * 
     * @param value String
     * @return {@link CurrentTaskType}
     */
    public static CurrentTaskType getCurrentTaskType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (CurrentTaskType taskType : CurrentTaskType.values())
        {
            if (value.equals(taskType.type))
            {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowHistoryType Value : " + value);
    }
}
