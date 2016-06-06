package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow Description
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowDescription
{

    CREATE_A_TASK_OR_START_A_REVIEW("Assign a task or review in Alfresco Cloud"),
    ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE("Assign a new task to yourself or a colleague"),
    HYBRID_ADHOC_TASK_PROCESS("Hybrid Adhoc Task Process"),
    HYBRID_REVIEW_AND_APPROVE_PROCESS("Hybrid Review And Approve Process"),
    ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD("Assign a new task to someone on the Cloud"),
    POOLED_REVIEW_AND_APPROVAL_OF_CONTENT_USING_ACTIVITI_WORKFLOW_ENGINE("Pooled review and approval of content using Activiti workflow engine"),
    REQUEST_DOCUMENT_APPROVAL("Request document approval from someone on the Cloud"),
    REVIEW_AND_APPROVAL_OF_CONTENT_USING_ACTIVITI_WORKFLOW_ENGINE("Review and approval of content using Activiti workflow engine"),
    REQUEST_DOCUMENT_APPROVAL_FROM_ONE_OR_MORE_COLLEAGUES("Request document approval from one or more colleagues"),
    GROUP_REVIEW_AND_APPROVAL_OF_CONTENT_USING_ACTIVITI_WORKFLOW_ENGINE("Group review and approval of content using Activiti workflow engine"),
    ASSIGN_REVIEW_TASK_SINGLE_REVIEWER("Assign a review task to a single reviewer"),
    ASSIGN_REVIEW_TASK_MULTIPLE_REVIEWERS("Assign a review task to multiple reviewers"),
    ASSIGN_REVIEW_TASK_GROUP("Assign a review task to a group"),
    ASSIGN_REVIEW_TASK_MULTIPLE_REVIEWERS_OWNERSHIP("Assign a review task to multiple reviewers, who can take ownership of the task");

    private String description;

    WorkFlowDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    /**
     * Returns {@link WorkFlowDescription} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowDescription}
     */
    public static WorkFlowDescription getWorkFlowDescription(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowDescription desc : WorkFlowDescription.values())
        {
            if (value.equals(desc.description))
            {
                return desc;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowDescription Value : " + value);
    }
}
