package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the Type field in Task Details (My Tasks page)
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum TaskDetailsType
{

    TASK("Task"),
    REVIEW("Review"),
    VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD("Verify task was completed on the cloud"),
    DOCUMENT_WAS_APPROVED_ON_THE_CLOUD("Document was approved on the cloud"),
    DOCUMENT_WAS_REJECTED_ON_THE_CLOUD("Document was rejected on the cloud"),
    WORKFLOW_CANCELLED_ON_THE_CLOUD("Worklflow cancelled on the cloud");

    private String type;

    TaskDetailsType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /**
     * Returns {@link TaskDetailsType} based on given value.
     * 
     * @param value String
     * @return {@link TaskDetailsType}
     */
    public static TaskDetailsType getTaskDetailsType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (TaskDetailsType taskDetailsType : TaskDetailsType.values())
        {
            if (value.equals(taskDetailsType.type))
            {
                return taskDetailsType;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowDescription Value : " + value);
    }
}
