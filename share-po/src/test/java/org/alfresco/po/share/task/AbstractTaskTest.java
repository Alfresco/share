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
package org.alfresco.po.share.task;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;

/**
 * Purpose of this class is to abstract functions of Task related test.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public abstract class AbstractTaskTest extends AbstractTest
{

    protected String taskName;
    protected MyTasksPage myTasksPage;
    protected String siteName;
    protected DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;


    /**
     * 
     */
    public AbstractTaskTest()
    {
        super();
    }

    /**
     * @throws Exception
     * @throws InterruptedException
     */
    protected void createTask(String username, String password) throws Exception, InterruptedException
    {
        loginAs(username, password);
        myTasksPage = ((DashBoardPage) drone.getCurrentPage()).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, taskName, reviewers);
        newWorkflowPage.startWorkflow(formDetails);
    }

}