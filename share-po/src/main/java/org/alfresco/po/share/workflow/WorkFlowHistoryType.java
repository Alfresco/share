package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow History Type
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowHistoryType
{

    VERIFY_TASK_COMPLETED_ON_CLOUD("Verify task was completed on the cloud"),
    START_TASK_OR_REVIEW_ON_CLOUD("Start a task or review on Alfresco Cloud"),
    DOCUMENT_WAS_REJECTED_ON_CLOUD("Document was rejected on the cloud"),
    DOCUMENT_WAS_APPROVED_ON_CLOUD("Document was approved on the cloud"),
    TASK("Task"),
    REVIEW("Review"),
    START_REVIEW("Start Review");

    private String type;

    WorkFlowHistoryType(String type)
    {
        this.type = type;
    }

    public String getDescription()
    {
        return type;
    }

    /**
     * Returns {@link WorkFlowHistoryType} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowHistoryType}
     */
    public static WorkFlowHistoryType getWorkFlowHistoryType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowHistoryType historyType : WorkFlowHistoryType.values())
        {
            if (value.equals(historyType.type))
            {
                return historyType;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowHistoryType Value : " + value);
    }
}
