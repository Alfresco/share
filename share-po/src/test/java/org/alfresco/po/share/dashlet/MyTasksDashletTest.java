/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.*;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.workflow.*;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Integration test my activities dashlet page elements.
 *
 * @author Michael Suzuki
 * @since 1.3
 */
public class MyTasksDashletTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(MyTasksDashletTest.class);
    private DashBoardPage dashBoard;
    private MyTasksPage myTasksPage;
    private String taskName = "myTask";

    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password).render();
    }

    @AfterClass(groups={"Enterprise-only"})
    public void tearDown()
    {
        cancelWorkFlow(taskName + "1");
        cancelWorkFlow(taskName + "2");
    }

    @Test(groups = { "alfresco-one" })
    public void instantiateMyTasksDashlet()
    {
        MyTasksDashlet dashlet = new MyTasksDashlet(drone);
        assertNotNull(dashlet);
    }

    /**
     * Gets empty collection when no tasks are visible.
     */
    @Test(dependsOnMethods = "selectMyTasksDashlet", groups = { "alfresco-one" })
    public void getTasksShouldBeEmpty()
    {
        MyTasksDashlet dashlet = new MyTasksDashlet(drone).render();
        List<ShareLink> tasks = dashlet.getTasks();

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    /**
     * Test process of accessing my documents
     * dashlet from the dash board view.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "selectFake", groups = { "alfresco-one" })
    public void selectMyTasksDashlet() throws Exception
    {
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        final String title = dashlet.getDashletTitle();
        assertEquals("My Tasks", title);
    }

    @Test(dependsOnMethods = "instantiateMyTasksDashlet", expectedExceptions = PageException.class, groups = { "alfresco-one" })
    public void selectFake() throws Exception
    {
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        dashlet.selectTask("bla");
    }

    @Test(dependsOnMethods = "getTasksShouldBeEmpty", groups = { "Enterprise4.2" })
    public void selectStartWorkFlow() throws Exception
    {
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        StartWorkFlowPage startWorkFlow = dashlet.selectStartWorkFlow().render();

        assertNotNull(startWorkFlow);
        assertTrue(startWorkFlow.getTitle().contains("Start Workflow"));
    }

    @Test(dependsOnMethods = "selectStartWorkFlow", groups = { "Enterprise-only" })
    public void isTaskPresent() throws Exception
    {
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

        for (int i = 1; i < 3; i++)
        {
            SharePage page = drone.getCurrentPage().render();
            myTasksPage = page.getNav().selectMyTasks();
            StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton();
            NewWorkflowPage workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
            formDetails.setMessage(taskName + i);
            formDetails.setDueDate(dueDate);
            formDetails.setReviewers(Arrays.asList(username));
            formDetails.setTaskPriority(Priority.MEDIUM);
            workFlow.startWorkflow(formDetails).render();
        }
        SharePage page = drone.getCurrentPage().render();
        myTasksPage = page.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(taskName + "2");
        editTaskPage.selectTaskDoneButton().render();

        page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        dashlet.selectTasksFilter(MyTasksFilter.ACTIVE_TASKS).render();
        assertTrue(dashlet.isTaskPresent(taskName + "1"), taskName + "1 is not found");
        dashlet.selectTasksFilter(MyTasksFilter.COMPLETED_TASKS).render();
        assertTrue(dashlet.isTaskPresent(taskName + "2"), taskName + "2 is not found");
    }

    @Test(dependsOnMethods = "isTaskPresent", groups = { "Enterprise-only" })
    public void selectEditTask() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        dashlet.selectTasksFilter(MyTasksFilter.ACTIVE_TASKS).render();
        assertTrue(dashlet.isTaskEditButtonEnabled(taskName + "1"), "Edit Task button is disabled");
        EditTaskPage editTaskPage = dashlet.selectEditTask(taskName + "1").render();
        assertNotNull(editTaskPage);
    }

    @Test(dependsOnMethods = "selectEditTask", groups = { "Enterprise-only" })
    public void selectViewTask() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        dashlet.selectTasksFilter(MyTasksFilter.ACTIVE_TASKS).render();
        assertTrue (dashlet.isTaskViewButtonEnabled(taskName + "1"), "View Task button is disabled");
        TaskDetailsPage taskDetailsPage = dashlet.selectViewTask(taskName + "1").render();
        assertNotNull(taskDetailsPage);
    }

    @Test(dependsOnMethods = "selectViewTask", groups = { "Enterprise-only" })
    public void selectComplete() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        myTasksPage = dashlet.selectComplete();
        assertTrue(myTasksPage.isFilterTitle("Completed Tasks"), "Completed Tasks page don't open");
    }

    @Test(dependsOnMethods = "selectComplete", groups = { "Enterprise-only" })
    public void selectActive() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        MyTasksPage myTasksPage = dashlet.selectActive().render();
        assertTrue (myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open");
    }


}
