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

import static org.alfresco.po.share.workflow.WorkFlowTitle.CLOUD_TASK_OR_REVIEW;
import static org.alfresco.po.share.workflow.WorkFlowTitle.HYBRID_ADHOC_TASK_PROCESS;
import static org.alfresco.po.share.workflow.WorkFlowTitle.HYBRID_REVIEW_AND_APPROVE_PROCESS;
import static org.alfresco.po.share.workflow.WorkFlowTitle.NEW_TASK;
import static org.alfresco.po.share.workflow.WorkFlowTitle.getTitle;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class WorkFlowTitleTest
{

    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getTitleWithNull()
    {
        getTitle(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void ggetTitleWithEmpty()
    {
        getTitle("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid WorkFlowTitle Value : Alfresco")
    public void getTitleWithAlfresco()
    {
        getTitle("Alfresco");
    }
    
    @Test
    public void getTitleTest()
    {
        assertEquals(getTitle("Cloud Task or Review"), CLOUD_TASK_OR_REVIEW);
        assertEquals(getTitle("Hybrid Adhoc Task Process"), HYBRID_ADHOC_TASK_PROCESS);
        assertEquals(getTitle("Hybrid Review And Approve Process"), HYBRID_REVIEW_AND_APPROVE_PROCESS);
        assertEquals(getTitle("New Task"), NEW_TASK);
    }
    
    @Test
    public void getTitleStringTest()
    {
        assertEquals(CLOUD_TASK_OR_REVIEW.getTitle(), "Cloud Task or Review");
        assertEquals(HYBRID_ADHOC_TASK_PROCESS.getTitle(), "Hybrid Adhoc Task Process");
        assertEquals(HYBRID_REVIEW_AND_APPROVE_PROCESS.getTitle(), "Hybrid Review And Approve Process");
        assertEquals(NEW_TASK.getTitle(), "New Task");
    }
    
}
