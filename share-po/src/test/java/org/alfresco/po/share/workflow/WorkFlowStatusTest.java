package org.alfresco.po.share.workflow;

import static org.alfresco.po.share.workflow.WorkFlowStatus.TASK_COMPLETE;
import static org.alfresco.po.share.workflow.WorkFlowStatus.TASK_IN_PROGRESS;
import static org.alfresco.po.share.workflow.WorkFlowStatus.WORKFLOW_COMPLETE;
import static org.alfresco.po.share.workflow.WorkFlowStatus.WORKFLOW_IN_PROGRESS;
import static org.alfresco.po.share.workflow.WorkFlowStatus.getWorkFlowStatus;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class WorkFlowStatusTest
{
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getWorkFlowStatusWithNull()
    {
        getWorkFlowStatus(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getWorkFlowStatusWithEmpty()
    {
        getWorkFlowStatus("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid WorkFlowStatus Value : Alfresco")
    public void getWorkFlowStatusWithAlfresco()
    {
        getWorkFlowStatus("Alfresco");
    }
    
    @Test
    public void getWorkFlowStatusTest()
    {
        assertEquals(getWorkFlowStatus ("Task is Complete"), TASK_COMPLETE);
        assertEquals(getWorkFlowStatus("Task is in Progress"), TASK_IN_PROGRESS);
        assertEquals(getWorkFlowStatus("Workflow is Complete"), WORKFLOW_COMPLETE);
        assertEquals(getWorkFlowStatus("Workflow is in Progress"), WORKFLOW_IN_PROGRESS);
    }
    
    @Test
    public void getDescriptionTest()
    {
        assertEquals(TASK_COMPLETE.getDescription(), "Task is Complete");
        assertEquals(TASK_IN_PROGRESS.getDescription(), "Task is in Progress");
        assertEquals(WORKFLOW_COMPLETE.getDescription(), "Workflow is Complete");
        assertEquals(WORKFLOW_IN_PROGRESS.getDescription(), "Workflow is in Progress");
    }

}
