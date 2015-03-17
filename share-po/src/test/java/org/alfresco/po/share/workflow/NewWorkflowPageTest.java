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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify CloudTaskOrReviewPage page load.
 * 
 * @author Abhijeet Bharade
 * @since 1.6.2
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2"})
public class NewWorkflowPageTest extends AbstractTest
{
    private String siteName;
    private NewWorkflowPage newWorkflowPage = null;
    private File fileForWorkflow;
    private DocumentLibraryPage documentLibPage;
    private DocumentDetailsPage documentDetailsPage;
    private MyTasksPage myTasksPage;
    private TaskDetailsPage taskDetailsPage;
    private EditTaskPage editTaskPage;
    private String taskComment;


    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        // assertTrue(WebDroneUtilTest.checkAlfrescoVersionBeforeClassRun(drone));
        siteName = "AdhocReassign" + System.currentTimeMillis();
        taskComment = "Comment" + System.currentTimeMillis();
        fileForWorkflow = SiteUtil.prepareFile("WF-File");
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "Public");
        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(fileForWorkflow.getCanonicalPath()).render();
        documentDetailsPage = documentLibPage.selectFile(fileForWorkflow.getName()).render();
    }

    @AfterClass(groups = "Enterprise4.2")
    public void afterClass()
    {
        cancelWorkFlow(siteName);
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "Enterprise4.2")
    public void isPartOfWorkflow() throws Exception
    {
        Assert.assertFalse(documentDetailsPage.isPartOfWorkflow());
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        
    }

    /**
     * This test is to fill the new task workflow form, press cancel 
     * and assert workflow is cancelled
     *
     * @throws Exception
     */
    
    
    @Test(groups = "Enterprise4.2", dependsOnMethods = "isPartOfWorkflow")
    public void cancelAdhocReview() throws Exception
    {
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        newWorkflowPage.cancelCreateWorkflow(formDetails).render();
        openSiteDocumentLibraryFromSearch(drone, siteName);
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(fileForWorkflow.getName());
        assertFalse(thisRow.isPartOfWorkflow(), "Document should not be part of workflow.");
        documentDetailsPage = documentLibPage.selectFile(fileForWorkflow.getName()).render();
        Assert.assertFalse(documentDetailsPage.isPartOfWorkflow());
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
    }
    
    
    
    /**
     * This test is to assert and fill the cloud task page form.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", dependsOnMethods = "cancelAdhocReview")
    public void startAdhocReview() throws Exception
    {
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        newWorkflowPage.startWorkflow(formDetails).render();
        openSiteDocumentLibraryFromSearch(drone, siteName);
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(fileForWorkflow.getName());
        assertTrue(thisRow.isPartOfWorkflow(), "Document should be part of workflow.");
        documentDetailsPage = documentLibPage.selectFile(fileForWorkflow.getName()).render();
        Assert.assertTrue(documentDetailsPage.isPartOfWorkflow());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "startAdhocReview")
    public void getTaskDetailsHeader() throws Exception
    {
        myTasksPage = documentDetailsPage.getNav().selectMyTasks().render();
        TaskDetails details = myTasksPage.getTaskLabels(siteName);
        List<String> taskLabels = details.getTaskLabels();
        assertTrue(taskLabels.contains("Due:"));
        assertTrue(taskLabels.contains("Started:"));
        assertTrue(taskLabels.contains("Status:"));
        assertTrue(taskLabels.contains("Type:"));
        assertTrue(taskLabels.contains("Description:"));
        assertTrue(taskLabels.contains("Started by:"));
            
        taskDetailsPage = myTasksPage.selectViewTasks(siteName).render();
        Assert.assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: "+siteName+" (Task)");
        
        List<String> formLabels = taskDetailsPage.getAllLabels();       
        assertTrue(formLabels.contains("Message:"));
        assertTrue(formLabels.contains("Owner:"));
        assertTrue(formLabels.contains("Priority:"));
        assertTrue(formLabels.contains("Due:"));
        assertTrue(formLabels.contains("Identifier:"));
        assertTrue(formLabels.contains("Status:"));
        assertTrue(formLabels.contains("Items:"));
        assertTrue(formLabels.contains("Comment:"));
        
        myTasksPage = documentDetailsPage.getNav().selectMyTasks().render();
        TaskHistoryPage historyPage = myTasksPage.selectTaskHistory(siteName).render();
        
        List<String> formLabelsHistory = historyPage.getAllLabels();       
        assertTrue(formLabelsHistory.contains("Completed on:"));
        assertTrue(formLabelsHistory.contains("Completed by:"));
        assertTrue(formLabelsHistory.contains("Outcome:"));
        assertTrue(formLabelsHistory.contains("Title:"));
        assertTrue(formLabelsHistory.contains("Description:"));
        assertTrue(formLabelsHistory.contains("Due:"));
        assertTrue(formLabelsHistory.contains("Completed:"));
        assertTrue(formLabelsHistory.contains("Started:"));
        assertTrue(formLabelsHistory.contains("Priority:"));
        assertTrue(formLabelsHistory.contains("Status:"));
        assertTrue(formLabelsHistory.contains("Message:"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskDetailsHeader")
    public void getTaskDetailsInfo() throws Exception
    {
        myTasksPage = documentDetailsPage.getNav().selectMyTasks().render();
        taskDetailsPage = myTasksPage.selectViewTasks(siteName).render();
        TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

        Assert.assertEquals(taskInfo.getMessage(), siteName);
        Assert.assertEquals(taskInfo.getOwner(), "Administrator");
        Assert.assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
        Assert.assertNull(taskInfo.getDueDate());
        Assert.assertEquals(taskInfo.getDueDateString(), "(None)");
        Assert.assertNotNull(taskInfo.getIdentifier());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskDetailsInfo")
    public void getTaskStatus() throws Exception
    {
        Assert.assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskDetailsInfo")
    public void getTaskItems() throws Exception
    {
        List<TaskItem> items = taskDetailsPage.getTaskItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName(), fileForWorkflow.getName());
        Assert.assertEquals(items.get(0).getDescription(), "(None)");
        Assert.assertEquals(items.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskItems", expectedExceptions = PageOperationException.class)
    public void getViewMoreActionsLink() throws Exception
    {
        List<TaskItem> items = taskDetailsPage.getTaskItems();
        items.get(0).getViewMoreActionsLink();
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getViewMoreActionsLink")
    public void getTaskItem() throws Exception
    {
        List<TaskItem> items = taskDetailsPage.getTaskItem(fileForWorkflow.getName());
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName(), fileForWorkflow.getName());
        Assert.assertEquals(items.get(0).getDescription(), "(None)");
        Assert.assertEquals(items.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskItem", expectedExceptions = PageOperationException.class)
    public void getViewMoreActionsLinkThrowsException() throws Exception
    {
        List<TaskItem> items = taskDetailsPage.getTaskItem(fileForWorkflow.getName());
        items.get(0).getViewMoreActionsLink();
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getViewMoreActionsLinkThrowsException")
    public void getComment() throws Exception
    {
        Assert.assertEquals(taskDetailsPage.getComment(), "(None)");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getComment")
    public void selectEditButton() throws Exception
    {
        SharePage returnedPage = taskDetailsPage.selectEditButton().render();

        Assert.assertTrue(returnedPage instanceof EditTaskPage);
        editTaskPage = ((EditTaskPage) returnedPage).render();
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectEditButton")
    public void getTaskDetailsInfoFromEditTaskPage() throws Exception
    {
        TaskInfo taskInfo = editTaskPage.getTaskDetailsInfo();

        Assert.assertEquals(taskInfo.getMessage(), siteName);
        Assert.assertEquals(taskInfo.getOwner(), "Administrator");
        Assert.assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
        Assert.assertNull(taskInfo.getDueDate());
        Assert.assertEquals(taskInfo.getDueDateString(), "(None)");
        Assert.assertNotNull(taskInfo.getIdentifier());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskDetailsInfoFromEditTaskPage")
    public void getTaskItemsFromEditTaskPage() throws Exception
    {
        List<TaskItem> items = editTaskPage.getTaskItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName(), fileForWorkflow.getName());
        Assert.assertEquals(items.get(0).getDescription(), "(None)");
        Assert.assertEquals(items.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertNotNull(items.get(0).getViewMoreActionsLink());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskItemsFromEditTaskPage")
    public void getTaskItemFromEditTaskPage() throws Exception
    {
        List<TaskItem> items = editTaskPage.getTaskItem(fileForWorkflow.getName());
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName(), fileForWorkflow.getName());
        Assert.assertEquals(items.get(0).getDescription(), "(None)");
        Assert.assertEquals(items.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertNotNull(items.get(0).getViewMoreActionsLink());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getTaskItemFromEditTaskPage")
    public void getStatusOptions() throws Exception
    {
        List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
        Assert.assertEquals(statusOptions.size(), TaskStatus.values().length);
        Assert.assertTrue(statusOptions.containsAll(Arrays.asList(TaskStatus.values())));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getStatusOptions")
    public void selectCancelButtonToReturnTaskDetailsPage() throws Exception
    {
        editTaskPage.enterComment(taskComment);
        SharePage returnedPage = editTaskPage.selectCancelButton().render();
        Assert.assertTrue(returnedPage instanceof TaskDetailsPage);
        taskDetailsPage.render();
        Assert.assertEquals(taskDetailsPage.getComment(), "(None)");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectCancelButtonToReturnTaskDetailsPage")
    public void selectCancelButtonToReturnMyTasksPage() throws Exception
    {
        myTasksPage = taskDetailsPage.getNav().selectMyTasks().render();
        editTaskPage = myTasksPage.navigateToEditTaskPage(siteName).render();
        SharePage returnedPage = editTaskPage.selectCancelButton().render();
        Assert.assertTrue(returnedPage instanceof MyTasksPage);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectCancelButtonToReturnMyTasksPage")
    public void selectSaveButtonToReturnTaskDetailsPage() throws Exception
    {
        myTasksPage.render();
        taskDetailsPage = myTasksPage.selectViewTasks(siteName).render();
        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
        editTaskPage.enterComment(taskComment);
        taskDetailsPage = editTaskPage.selectSaveButton().render();
        Assert.assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        Assert.assertEquals(taskDetailsPage.getComment(), taskComment);
        Assert.assertTrue(taskDetailsPage.isEditButtonPresent());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectSaveButtonToReturnTaskDetailsPage")
    public void selectSaveButtonToReturnMyTasksPage() throws Exception
    {
        myTasksPage = taskDetailsPage.getNav().selectMyTasks().render();
        editTaskPage = myTasksPage.navigateToEditTaskPage(siteName).render();
        editTaskPage.selectStatusDropDown(TaskStatus.ONHOLD);
        myTasksPage = editTaskPage.selectSaveButton().render();

        TaskDetails taskDetails = myTasksPage.getTaskDetails(siteName);
        Assert.assertEquals(taskDetails.getStatus(), TaskStatus.ONHOLD.getTaskName());
        Assert.assertEquals(taskDetails.getDue(), "(None)");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectSaveButtonToReturnMyTasksPage")
    public void isEditButtonPresent() throws Exception
    {
        taskDetailsPage =  myTasksPage.selectViewTasks(siteName).render();
        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(taskComment);
        taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
        Assert.assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        Assert.assertEquals(taskDetailsPage.getComment(), taskComment);
        Assert.assertFalse(taskDetailsPage.isEditButtonPresent());
    }
}
