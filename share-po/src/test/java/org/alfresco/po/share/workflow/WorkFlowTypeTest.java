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
