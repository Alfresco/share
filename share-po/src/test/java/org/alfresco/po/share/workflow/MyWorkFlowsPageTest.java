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
package org.alfresco.po.share.workflow;

import static org.alfresco.po.share.workflow.DueFilters.NEXT_7_DAYS;
import static org.alfresco.po.share.workflow.DueFilters.NO_DATE;
import static org.alfresco.po.share.workflow.DueFilters.OVERDUE;
import static org.alfresco.po.share.workflow.DueFilters.TODAY;
import static org.alfresco.po.share.workflow.DueFilters.TOMORROW;
import static org.alfresco.po.share.workflow.Priority.HIGH;
import static org.alfresco.po.share.workflow.Priority.LOW;
import static org.alfresco.po.share.workflow.Priority.MEDIUM;
import static org.alfresco.po.share.workflow.StartedFilter.LAST_14_DAYS;
import static org.alfresco.po.share.workflow.StartedFilter.LAST_28_DAYS;
import static org.alfresco.po.share.workflow.StartedFilter.LAST_7_DAYS;
import static org.alfresco.po.share.workflow.WorkFlowType.NEW_WORKFLOW;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskFilters;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.test.FailedTestListener;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify MyWorkFlowsPage.
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
@Listeners(FailedTestListener.class)
public class MyWorkFlowsPageTest extends AbstractTest
{
    DashBoardPage dashBoardPage;
    MyWorkFlowsPage myWorkFlowsPage;
    WorkFlowDetailsPage workFlowDetailsPage;
    MyTasksPage myTasksPage;
    EditTaskPage editTaskPage;

    String workFlow1;
    String workFlow2;
    String workFlow3;
    String dueDate;
    String workFlowComment;
    String uname;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        uname = "workflow" + System.currentTimeMillis();
        createEnterpriseUser(uname);
        dashBoardPage = loginAs(uname, UNAME_PASSWORD);
        workFlow1 = "MyWF-" + System.currentTimeMillis() + "-1";
        workFlow2 = "MyWF-" + System.currentTimeMillis() + "-2";
        workFlow3 = "MyWF-" + System.currentTimeMillis() + "-3";
        dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        workFlowComment = System.currentTimeMillis() + "-Comment";
    }

    @AfterClass
    public void afterClass()
    {
        SharePage sharePage = resolvePage(driver).render();

        List<String> workFlowList = Arrays.asList(workFlow1, workFlow3);

        for (String workFlow : workFlowList)
        {
            myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
            if (myWorkFlowsPage.isWorkFlowPresent(workFlow))
            {
                myWorkFlowsPage.cancelWorkFlow(workFlow);
            }
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
            if (myWorkFlowsPage.isWorkFlowPresent(workFlow))
            {
                myWorkFlowsPage.deleteWorkFlow(workFlow);
            }
        }
    }

    private WorkFlowFormDetails getFormDetails(String workFlowName)
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        List<String> userNames = new ArrayList<String>();
        userNames.add(uname);
        formDetails.setReviewers(userNames);
        return formDetails;
    }

    /**
     * This test is to select WorkFlows I've Started from
     * Navigation bar and verify the title.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void selectWorkFlowsIHaveStarted() throws Exception
    {
        myWorkFlowsPage = dashBoardPage.getNav().selectWorkFlowsIHaveStarted().render();

        assertTrue(myWorkFlowsPage.isTitlePresent());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectWorkFlowsIHaveStarted")
    public void selectActiveWorkFlows()
    {
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        assertEquals(myWorkFlowsPage.getSubTitle(), "Active Workflows");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectActiveWorkFlows")
    public void selectCompletedWorkFlows()
    {
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectCompletedWorkFlows")
    public void selectStartWorkflowButton() throws InterruptedException
    {
        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage workFlow = startWorkFlowPage.getWorkflowPage(NEW_WORKFLOW).render();
        WorkFlowFormDetails formDetails = getFormDetails(workFlow1);
        myWorkFlowsPage = workFlow.startWorkflow(formDetails).render();
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow1));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectStartWorkflowButton")
    public void getWorkFlowDetails()
    {
        List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(workFlow1);
        assertEquals(workFlowDetailsList.size(), 1, "Verifying there is only one workflow that matches the workflow name");
        assertEquals(workFlowDetailsList.get(0).getWorkFlowName(), workFlow1);
        assertEquals(workFlowDetailsList.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate));
        assertEquals(workFlowDetailsList.get(0).getStartDate().toLocalDate(), new DateTime().toLocalDate());
        assertNull(workFlowDetailsList.get(0).getEndDate());
        assertEquals(workFlowDetailsList.get(0).getType(), NEW_WORKFLOW);
        assertEquals(workFlowDetailsList.get(0).getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "getWorkFlowDetails", expectedExceptions = IllegalArgumentException.class)
    public void selectWorkFlowWithNullData()
    {
        myWorkFlowsPage.selectWorkFlow(null);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectWorkFlowWithNullData", expectedExceptions = PageException.class)
    public void selectWorkFlowWithIncorrectData()
    {
        myWorkFlowsPage.selectWorkFlow(String.valueOf(System.currentTimeMillis()));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectWorkFlowWithIncorrectData")
    public void selectWorkFlowWithValidData()
    {
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        assertTrue(workFlowDetailsPage.isTitlePresent());
        assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is in Progress");
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectWorkFlowWithValidData")
    public void getWorkFlowDetailsHeader()
    {
        String workFlowDetailsHeader = "Details: " + workFlow1 + " (Task)";
        assertEquals(workFlowDetailsPage.getPageHeader(), workFlowDetailsHeader);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "getWorkFlowDetailsHeader")
    public void verifyTaskDetails()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlow1);

        assertEquals(taskDetails.getTaskName(), workFlow1);
        assertEquals(taskDetails.getDue(), getDueDateOnMyTaskPage(dueDate));
        assertEquals(taskDetails.getStartDate().toLocalDate(), new DateTime().toLocalDate());
        assertNull(taskDetails.getEndDate());
        assertEquals(taskDetails.getStatus(), "Not Yet Started");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK);
        assertEquals(taskDetails.getDescription(), "Task allocated by colleague");
        assertEquals(taskDetails.getStartedBy(), uname);
        assertTrue(taskDetails.isViewWorkFlowDisplayed());
        assertTrue(taskDetails.isViewTaskDisplayed());
        assertTrue(taskDetails.isEditTaskDisplayed());
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskDetails")
    public void selectEditTask()
    {
        myWorkFlowsPage = myWorkFlowsPage.getNav().selectWorkFlowsIHaveStarted().render();
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        editTaskPage = workFlowDetailsPage.getCurrentTasksList().get(0).getEditTaskLink().click().render();
        assertTrue(editTaskPage.isBrowserTitle("Edit Task"));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectEditTask")
    public void completeWorkFlow()
    {
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(workFlowComment);
        workFlowDetailsPage = editTaskPage.selectTaskDoneButton().render();
        assertTrue(workFlowDetailsPage.isTitlePresent());
        assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is in Progress");

        editTaskPage = workFlowDetailsPage.getCurrentTasksList().get(0).getEditTaskLink().click().render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(workFlowComment);
        workFlowDetailsPage = editTaskPage.selectTaskDoneButton().render();
        assertTrue(workFlowDetailsPage.isTitlePresent());
        assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is Complete");
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "completeWorkFlow")
    public void verifyCompletedWorkFlowDetails()
    {
        myWorkFlowsPage = workFlowDetailsPage.getNav().selectWorkFlowsIHaveStarted().render();
        myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlow1));

        myWorkFlowsPage.selectCompletedWorkFlows().render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow1));

        List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(workFlow1);

        assertEquals(workFlowDetailsList.get(0).getWorkFlowName(), workFlow1);
        assertEquals(workFlowDetailsList.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate));
        assertEquals(workFlowDetailsList.get(0).getStartDate().toLocalDate(), new DateTime().toLocalDate());
        assertEquals(workFlowDetailsList.get(0).getEndDate().toLocalDate(), new DateTime().toLocalDate());
        assertEquals(workFlowDetailsList.get(0).getType(), NEW_WORKFLOW);
        assertEquals(workFlowDetailsList.get(0).getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE);
        assertFalse(workFlowDetailsList.get(0).isCancelWorkFlowDisplayed());
        assertTrue(workFlowDetailsList.get(0).isViewHistoryDisplayed());
        assertTrue(workFlowDetailsList.get(0).isDeleteWorkFlowDisplayed());
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyCompletedWorkFlowDetails")
    public void verifyGetWorkFlowCount()
    {
        myWorkFlowsPage = resolvePage(driver).render();
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyGetWorkFlowCount")
    public void verifyWorkFlowFilterCount()
    {
        myWorkFlowsPage = resolvePage(driver).render();
        WorkFlowFilters workFlowFilters = myWorkFlowsPage.getWorkFlowsFilter();
        workFlowFilters.select(NEXT_7_DAYS);
        workFlowFilters.select(TOMORROW);
        workFlowFilters.select(TODAY);
        workFlowFilters.select(OVERDUE);
        workFlowFilters.select(NO_DATE);

        workFlowFilters.select(LAST_14_DAYS);
        workFlowFilters.select(LAST_28_DAYS);
        workFlowFilters.select(LAST_7_DAYS);

        workFlowFilters.select(LOW);
        workFlowFilters.select(HIGH);
        workFlowFilters.select(MEDIUM);

        workFlowFilters.select(NEW_WORKFLOW);
    }

    private String getDueDateOnMyTaskPage(String dueDateString)
    {
        DateTime date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDateString);
        return date.toString(DateTimeFormat.forPattern("dd MMMM, yyyy"));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyWorkFlowFilterCount")
    public void selectStartWorkflowButtonWithDueDateNone() throws InterruptedException
    {
        dueDate = "";
        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage workFlow = startWorkFlowPage.getWorkflowPage(NEW_WORKFLOW).render();
        WorkFlowFormDetails formDetails = getFormDetails(workFlow3);
        myWorkFlowsPage = workFlow.startWorkflow(formDetails).render();
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow3));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "selectStartWorkflowButtonWithDueDateNone")
    public void verifyTaskDetailsWithDueDateNone()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlow3);

        assertEquals(taskDetails.getTaskName(), workFlow3);
        assertEquals(taskDetails.getDue(), "(None)");
        assertEquals(taskDetails.getStartDate().toLocalDate(), new DateTime().toLocalDate());
        assertNull(taskDetails.getEndDate());
        assertEquals(taskDetails.getStatus(), "Not Yet Started");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK);
        assertEquals(taskDetails.getDescription(), "Task allocated by colleague");
        assertEquals(taskDetails.getStartedBy(), uname);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskDetailsWithDueDateNone")
    public void verifyTaskButtonsPresent()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        myTasksPage.renderTask(3000, workFlow3);

        assertTrue(myTasksPage.isTaskEditButtonEnabled(workFlow3), "Task Edit button is not present.");
        assertTrue(myTasksPage.isTaskViewButtonEnabled(workFlow3), "Task View button is not present.");
        assertTrue(myTasksPage.isTaskWorkflowButtonEnabled(workFlow3), "Task Workflow View button is not present.");
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskButtonsPresent")
    public void verifyTaskCountMethod()
    {
        myTasksPage = resolvePage(driver).render();
        assertEquals(myTasksPage.getTasksCount(), 1);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskCountMethod", expectedExceptions = PageOperationException.class)
    public void verifyTaskFilterByWorkFlowTypeWithException()
    {
        myTasksPage = resolvePage(driver).render();
        TaskFilters taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(NEW_WORKFLOW);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskFilterByWorkFlowTypeWithException", expectedExceptions = PageOperationException.class)
    public void verifyTaskFilterByStartedWithException()
    {
        myTasksPage = resolvePage(driver).render();
        TaskFilters taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(LAST_28_DAYS);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "verifyTaskFilterByStartedWithException")
    public void checkFilterTitle()
    {
        myTasksPage = resolvePage(driver).render();
        TaskFilters taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(LOW);
        assertTrue(myTasksPage.isFilterTitle("Low Priority Tasks"));
        taskFilters.select(HIGH);
        assertTrue(myTasksPage.isFilterTitle("High Priority Tasks"));
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "checkFilterTitle")
    public void checkTaskCount()
    {
        TaskFilters taskFilters = myTasksPage.getTaskFilters();
        taskFilters.select(MEDIUM);
        assertEquals(myTasksPage.getTaskCount(workFlow3), 1);
    }

    @Test(groups = {"Enterprise4.2", "bug"}, dependsOnMethods = "checkTaskCount")
    public void checkTaskCountForFake()
    {
        assertEquals(myTasksPage.getTaskCount("azazazazazazaaza"), 0);
    }

}
