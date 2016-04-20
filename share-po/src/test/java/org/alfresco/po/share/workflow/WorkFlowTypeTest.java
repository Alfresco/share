package org.alfresco.po.share.workflow;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.alfresco.po.share.workflow.WorkFlowType.CLOUD_TASK_OR_REVIEW;
import static org.alfresco.po.share.workflow.WorkFlowType.GROUP_REVIEW_AND_APPROVE;
import static org.alfresco.po.share.workflow.WorkFlowType.NEW_WORKFLOW;
import static org.alfresco.po.share.workflow.WorkFlowType.POOLED_REVIEW_AND_APPROVE;
import static org.alfresco.po.share.workflow.WorkFlowType.REVIEW_AND_APPROVE;
import static org.alfresco.po.share.workflow.WorkFlowType.SEND_DOCS_FOR_REVIEW;

import org.testng.annotations.Test;

/**
 * Unit test for {@link WorkFlowType}
 *
 * @author Shan Nagarajan
 * @since  1.7.1
 */
public class WorkFlowTypeTest
{

    @Test(groups={"unit"}, expectedExceptions=IllegalArgumentException.class)
    public void getWorkflowTypeByTitle()
    {
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(CLOUD_TASK_OR_REVIEW.getTitle()), CLOUD_TASK_OR_REVIEW);
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(GROUP_REVIEW_AND_APPROVE.getTitle()), GROUP_REVIEW_AND_APPROVE);
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(NEW_WORKFLOW.getTitle()), NEW_WORKFLOW);
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(POOLED_REVIEW_AND_APPROVE.getTitle()), POOLED_REVIEW_AND_APPROVE);
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(REVIEW_AND_APPROVE.getTitle()), REVIEW_AND_APPROVE);
        assertEquals(WorkFlowType.getWorkflowTypeByTitle(SEND_DOCS_FOR_REVIEW.getTitle()), SEND_DOCS_FOR_REVIEW);
        assertNull(WorkFlowType.getWorkflowTypeByTitle("Alfresco Test Task"));
    }
    
    @Test(groups={"unit"}, expectedExceptions=IllegalArgumentException.class)
    public void getWorkflowTypeByTitleWithEmptyName()
    {
        assertNull(WorkFlowType.getWorkflowTypeByTitle(""));
    }
    
}
