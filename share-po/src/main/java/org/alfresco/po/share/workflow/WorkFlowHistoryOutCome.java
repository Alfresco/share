package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow History OutCome
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowHistoryOutCome
{

    TASK_DONE("Task Done"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private String outCome;

    WorkFlowHistoryOutCome(String outCome)
    {
        this.outCome = outCome;
    }

    public String getOutCome()
    {
        return outCome;
    }

    /**
     * Returns {@link WorkFlowHistoryOutCome} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowHistoryOutCome}
     */
    public static WorkFlowHistoryOutCome getWorkFlowHistoryOutCome(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowHistoryOutCome historyOutCome : WorkFlowHistoryOutCome.values())
        {
            if (value.equals(historyOutCome.outCome))
            {
                return historyOutCome;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowHistoryOutCome Value : " + value);
    }
}
