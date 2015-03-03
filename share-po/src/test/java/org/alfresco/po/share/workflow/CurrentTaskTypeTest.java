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

import static org.testng.Assert.assertEquals;
import static org.alfresco.po.share.workflow.CurrentTaskType.TASK;
import static org.alfresco.po.share.workflow.CurrentTaskType.APPROVED;
import static org.alfresco.po.share.workflow.CurrentTaskType.DOCUMENT_WAS_APPROVED_ON_CLOUD;
import static org.alfresco.po.share.workflow.CurrentTaskType.DOCUMENT_WAS_REJECTED_ON_CLOUD;
import static org.alfresco.po.share.workflow.CurrentTaskType.REJECTED;
import static org.alfresco.po.share.workflow.CurrentTaskType.REVIEW;
import static org.alfresco.po.share.workflow.CurrentTaskType.TASK_COMPLETED;
import static org.alfresco.po.share.workflow.CurrentTaskType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD;

import org.testng.annotations.Test;

@Test(groups="unit")
public class CurrentTaskTypeTest
{
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getCurrentTaskTypeWithNull()
    {
        CurrentTaskType.getCurrentTaskType(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getCurrentTaskTypeWithEmpty()
    {
        CurrentTaskType.getCurrentTaskType("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid WorkFlowHistoryType Value : Alfresco")
    public void getCurrentTaskTypeWithAlfresco()
    {
        CurrentTaskType.getCurrentTaskType("Alfresco");
    }
    
    @Test
    public void getCurrentTaskType()
    {
        assertEquals(CurrentTaskType.getCurrentTaskType("Task"), TASK);
        assertEquals(CurrentTaskType.getCurrentTaskType("Approved"), APPROVED);
        assertEquals(CurrentTaskType.getCurrentTaskType("Document was approved on the cloud"), DOCUMENT_WAS_APPROVED_ON_CLOUD);
        assertEquals(CurrentTaskType.getCurrentTaskType("Document was rejected on the cloud"), DOCUMENT_WAS_REJECTED_ON_CLOUD);
        assertEquals(CurrentTaskType.getCurrentTaskType("Rejected"), REJECTED);
        assertEquals(CurrentTaskType.getCurrentTaskType("Review"), REVIEW);
        assertEquals(CurrentTaskType.getCurrentTaskType("Task Completed"), TASK_COMPLETED);
        assertEquals(CurrentTaskType.getCurrentTaskType("Verify task was completed on the cloud"), VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD);
    }
    
    @Test
    public void getType()
    {
        assertEquals(TASK.getType(), "Task");
        assertEquals(APPROVED.getType(), "Approved");
        assertEquals(DOCUMENT_WAS_APPROVED_ON_CLOUD.getType(), "Document was approved on the cloud");
        assertEquals(DOCUMENT_WAS_REJECTED_ON_CLOUD.getType(), "Document was rejected on the cloud");
        assertEquals(REJECTED.getType(), "Rejected");
        assertEquals(REVIEW.getType(), "Review");
        assertEquals(TASK_COMPLETED.getType(), "Task Completed");
        assertEquals(VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD.getType(), "Verify task was completed on the cloud");
    }
    
}
