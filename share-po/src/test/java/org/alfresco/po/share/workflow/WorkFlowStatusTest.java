/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
