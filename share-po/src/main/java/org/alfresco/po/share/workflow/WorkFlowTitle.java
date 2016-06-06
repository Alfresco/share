package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow Title values
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowTitle
{

    NEW_TASK("New Task"),
    CLOUD_TASK_OR_REVIEW("Cloud Task or Review"),
    HYBRID_TASK("Hybrid Task"),
    HYBRID_ADHOC_TASK_PROCESS("Hybrid Adhoc Task Process"),
    HYBRID_REVIEW_AND_APPROVE_PROCESS("Hybrid Review And Approve Process"),
    HYBRID_REVIEW("Hybrid Review"), ;

    private String title;

    WorkFlowTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    /**
     * Returns {@link WorkFlowTitle} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowTitle}
     */
    public static WorkFlowTitle getTitle(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowTitle workFlowTitle : WorkFlowTitle.values())
        {
            if (value.equals(workFlowTitle.title))
            {
                return workFlowTitle;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowTitle Value : " + value);
    }
}
