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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
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
public class AssignmentPageTest extends AbstractTest
{
    DashBoardPage dashBoardPage;
    MyWorkFlowsPage myWorkFlowsPage;
    StartWorkFlowPage startWorkFlowPage;
    CloudTaskOrReviewPage cloudTaskOrReviewPage;
    AssignmentPage assignmentPage;
    WorkFlowDetailsPage workFlowDetailsPage;
    String workFlow1;
    String workFlow2;
    String dueDate;
    DateTime due;
    String workFlowComment;
    int requiredApprovalPercentage;
    String cloudNetwork;
    String cloudUserSite;
    String cloudFolder;
    private static String siteName;
    private File wfTestFile;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "Hybrid")
    public void prepare() throws Exception
    {
        dashBoardPage = loginAs(username, password);

        workFlow1 = "MyWF-" + System.currentTimeMillis() + "-1";
        workFlow2 = "MyWF-" + System.currentTimeMillis() + "-2";
        dueDate = "17/09/2015";
        due = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate);
        workFlowComment = System.currentTimeMillis() + "-Comment";
        requiredApprovalPercentage = 50;

        cloudNetwork = cloudUserName.split("@")[1];
        cloudUserSite = "Auto Account's Home";
        cloudFolder = "Documents";

        siteName = "site" + System.currentTimeMillis();
        wfTestFile = SiteUtil.prepareFile("WF-F");
        SiteUtil.createSite(drone, siteName, "description", "Public");

        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibraryPage.getNavigation().selectFileUpload().render();
        uploadForm.uploadFile(wfTestFile.getCanonicalPath()).render();

        signInToCloud(drone, cloudUserName, cloudUserPassword);
    }

    @AfterClass(groups = "Hybrid")
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
        disconnectCloudSync(drone);
        drone.refresh();
        SharePage sharePage = drone.getCurrentPage().render();
        myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        myWorkFlowsPage.render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow1))
        {
            myWorkFlowsPage.cancelWorkFlow(workFlow1);
        }
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow1))
        {
            myWorkFlowsPage.deleteWorkFlow(workFlow1);
        }


    }

    @Test(groups = "Hybrid")
    public void enterRequiredApprovalPercentage() throws Exception
    {
        myWorkFlowsPage = dashBoardPage.getNav().selectWorkFlowsIHaveStarted().render();

        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);

        cloudTaskOrReviewPage.enterMessageText(workFlow1);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage(requiredApprovalPercentage);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "enterRequiredApprovalPercentage")
    public void verifyDestinationDetails()
    {
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudUserSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        destinationAndAssigneePage.selectSubmitButtonToSync();

        cloudTaskOrReviewPage.render();

        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), cloudNetwork);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudUserSite);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), cloudFolder + "/");
    }

    @Test(groups = "Hybrid", dependsOnMethods = "verifyDestinationDetails")
    public void isNoItemsFoundMessageDisplayed()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        Assert.assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed("RandomUserString"));
        Assert.assertFalse(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUserName));
    }

    @Test(groups = "Hybrid", dependsOnMethods = "isNoItemsFoundMessageDisplayed")
    public void isUserFound()
    {
        Assert.assertFalse(assignmentPage.isUserFound("RandomUserString"));
        Assert.assertTrue(assignmentPage.isUserFound(cloudUserName));

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUserName);

        assignmentPage.selectReviewers(userNames).render();
    }

    @Test(groups = "Hybrid", dependsOnMethods = "isUserFound")
    public void selectItem()
    {
        cloudTaskOrReviewPage.render();
        cloudTaskOrReviewPage.selectItem(wfTestFile.getName(), siteName);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "selectItem")
    public void selectStartWorkflow()
    {
        cloudTaskOrReviewPage.render();
        myWorkFlowsPage = cloudTaskOrReviewPage.selectStartWorkflow().render();
    }

    @Test(groups = "Hybrid", dependsOnMethods = "selectStartWorkflow")
    public void getAssignee()
    {
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        Assert.assertTrue(workFlowDetailsPage.getAssignee().contains(cloudUserName));
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getAssignee")
    public void getWorkFlowDetailsGeneralInfo()
    {
        WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();
        Assert.assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
        Assert.assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
        Assert.assertEquals(generalInfo.getStartedBy(), "Administrator");
        Assert.assertEquals(generalInfo.getDueDate().toLocalDate(), due.toLocalDate());
        Assert.assertEquals(generalInfo.getCompleted(), "<in progress>");
        Assert.assertEquals(generalInfo.getStartDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
        Assert.assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
        Assert.assertEquals(generalInfo.getMessage(), workFlow1);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getWorkFlowDetailsGeneralInfo")
    public void getWorkFlowDetailsMoreInfo()
    {
        WorkFlowDetailsMoreInfo moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

        Assert.assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
        Assert.assertEquals(moreInfo.getDestination(), cloudNetwork);
        Assert.assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.getKeepContentStrategy("Keep content on cloud and remove sync"));
        Assert.assertFalse(moreInfo.isLockOnPremise());
        Assert.assertEquals(moreInfo.getAssignmentList().size(), 1);
        Assert.assertEquals(moreInfo.getAssignmentList().get(0), "Auto Account (user1@premiernet.test)");
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getWorkFlowDetailsMoreInfo")
    public void getWorkFlowItems()
    {
        List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName(), wfTestFile.getName());
        Assert.assertEquals(items.get(0).getDescription(), "(None)");
        Assert.assertEquals(items.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getWorkFlowItems")
    public void getWorkFlowItem()
    {
        List<WorkFlowDetailsItem> item = workFlowDetailsPage.getWorkFlowItem(wfTestFile.getName());
        Assert.assertEquals(item.size(), 1);
        Assert.assertEquals(item.get(0).getItemName(), wfTestFile.getName());
        Assert.assertEquals(item.get(0).getDescription(), "(None)");
        Assert.assertEquals(item.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getWorkFlowItem")
    public void isNoTasksMessageDisplayed()
    {
        Assert.assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());
    }

    @Test(groups = "Hybrid", dependsOnMethods = "isNoTasksMessageDisplayed")
    public void getCurrentTasksList()
    {
        List<WorkFlowDetailsCurrentTask> currentTasks = workFlowDetailsPage.getCurrentTasksList();
        Assert.assertEquals(currentTasks.size(), 0);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "getCurrentTasksList")
    public void getWorkFlowHistoryList()
    {
        List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

        Assert.assertEquals(historyList.size(), 1);
        Assert.assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
        Assert.assertEquals(historyList.get(0).getCompletedBy(), "admin");
        Assert.assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        Assert.assertEquals(historyList.get(0).getComment(), "");
    }
    // Workflow1 is created and currently the user is on WorkFlowDetailsPage
    
    @Test(groups = "Hybrid", dependsOnMethods = "getWorkFlowHistoryList")
    public void isDeleteWorkFlowButtonDisplayed()
    {
    	Assert.assertTrue(workFlowDetailsPage.isDeleteWorkFlowButtonDisplayed());
    }
    
}
