/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */

/**
 * This class tests the methods from ViewWorkflowPage.
 * 
 * @author Abhijeet Bharade
 * @since 1.7.0
 */
package org.alfresco.po.share.workflow;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = {"TestBug" })
public class ViewWorkflowPageTest extends AbstractTest
{
    private String siteName;
    private String message;
    private ViewWorkflowPage viewWorkflowPage;

    @BeforeClass(groups = "Enterprise4.2")
    public void beforeClass() throws Throwable
    {
        siteName = "AdhocReassign" + System.currentTimeMillis();
        message = siteName;
        loginAs(username, password);
        MyTasksPage myTasksPage = ((DashBoardPage) resolvePage(driver)).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, message, reviewers);
        myTasksPage = newWorkflowPage.startWorkflow(formDetails).render();
        myTasksPage.render();
        
        TaskDetails taskLabels = myTasksPage.getTaskLabels(message);
        List<String> labels = taskLabels.getTaskLabels();
        assertTrue(labels.contains("Due:"));
        assertTrue(labels.contains("Started:"));
        assertTrue(labels.contains("Status:"));
        assertTrue(labels.contains("Type:"));
        assertTrue(labels.contains("Description:"));
        assertTrue(labels.contains("Started by:"));
        
        EditTaskPage taskPage = myTasksPage.navigateToEditTaskPage(message).render();
        taskPage.enterComment(message);
        taskPage.selectSaveButton().render();
        viewWorkflowPage = myTasksPage.selectViewWorkflow(message).render();
    }

    @AfterClass
    public void afterClass()
    {
    }

    @Test(groups = "Enterprise4.2")
    public void selectCancelWorkflowButtonTest()
    {
        MyTasksPage myTasksPage = viewWorkflowPage.selectCancelWorkflowButton().render();
        myTasksPage.render();
        assertTrue(myTasksPage.isTitlePresent("My Tasks"), "MyTaskPage instance must be returned");
    }

}
