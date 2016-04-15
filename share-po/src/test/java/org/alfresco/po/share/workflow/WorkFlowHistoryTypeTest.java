package org.alfresco.po.share.workflow;

import static org.alfresco.po.share.workflow.WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.DOCUMENT_WAS_REJECTED_ON_CLOUD;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.REVIEW;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.START_REVIEW;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.TASK;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.VERIFY_TASK_COMPLETED_ON_CLOUD;
import static org.alfresco.po.share.workflow.WorkFlowHistoryType.getWorkFlowHistoryType;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class WorkFlowHistoryTypeTest
{
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getWorkFlowHistoryTypeWithNull()
    {
        getWorkFlowHistoryType(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getWorkFlowHistoryTypeWithEmpty()
    {
        getWorkFlowHistoryType("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid WorkFlowHistoryType Value : Alfresco")
    public void getWorkFlowHistoryTypeWithAlfresco()
    {
        getWorkFlowHistoryType("Alfresco");
    }
    
    @Test
    public void getWorkFlowHistoryTypeTest()
    {
        assertEquals(getWorkFlowHistoryType ("Document was approved on the cloud"), DOCUMENT_WAS_APPROVED_ON_CLOUD);
        assertEquals(getWorkFlowHistoryType("Document was rejected on the cloud"), DOCUMENT_WAS_REJECTED_ON_CLOUD);
        assertEquals(getWorkFlowHistoryType("Review"), REVIEW);
        assertEquals(getWorkFlowHistoryType("Start Review"), START_REVIEW);
        assertEquals(getWorkFlowHistoryType("Start a task or review on Alfresco Cloud"), START_TASK_OR_REVIEW_ON_CLOUD);
        assertEquals(getWorkFlowHistoryType("Task"), TASK);
        assertEquals(getWorkFlowHistoryType("Review"), REVIEW);
        assertEquals(getWorkFlowHistoryType("Verify task was completed on the cloud"), VERIFY_TASK_COMPLETED_ON_CLOUD);
    }
    
    @Test
    public void getDescriptionTest()
    {
        assertEquals(DOCUMENT_WAS_APPROVED_ON_CLOUD.getDescription(), "Document was approved on the cloud");
        assertEquals(DOCUMENT_WAS_REJECTED_ON_CLOUD.getDescription(), "Document was rejected on the cloud");
        assertEquals(REVIEW.getDescription(), "Review");
        assertEquals(START_REVIEW.getDescription(), "Start Review");
        assertEquals(START_TASK_OR_REVIEW_ON_CLOUD.getDescription(), "Start a task or review on Alfresco Cloud");
        assertEquals(TASK.getDescription(), "Task");
        assertEquals(REVIEW.getDescription(), "Review");
        assertEquals(VERIFY_TASK_COMPLETED_ON_CLOUD.getDescription(), "Verify task was completed on the cloud");

    }

}
