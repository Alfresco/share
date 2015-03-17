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

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Simple Cloud Task page.
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
@Test(groups = {"Hybrid"})
@Listeners(FailedTestListener.class)
public class SimpleCloudTaskTest extends AbstractTest
{
    MyWorkFlowsPage myWorkFlowsPage;
    StartWorkFlowPage startWorkFlowPage;
    CloudTaskOrReviewPage cloudTaskOrReviewPage;
    DestinationAndAssigneePage destinationAndAssigneePage;
    AssignmentPage assignmentPage;
    String cloudNetwork;
    String cloudSite;
    String cloudFolder;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        cloudNetwork = cloudUserName.split("@")[1];
        cloudSite = "Auto Account's Home";
        cloudFolder = "Documents";

        loginAs(username, password);

        signInToCloud(drone, cloudUserName, cloudUserPassword);
        SharePage sharePage = drone.getCurrentPage().render();
        myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();

    }

    @Test
    public void isSimpleCloudTaskElementsPresent() throws Exception
    {
        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);

        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent(), "Verifying the Elements ");
    }

    @Test(dependsOnMethods = "isSimpleCloudTaskElementsPresent")
    public void verifyDestinationDetails() throws Exception
    {
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), "(None)");
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), "(None)");
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), "(None)");
    }

    @Test(dependsOnMethods = "verifyDestinationDetails")
    public void selectDestinationAndAssigneePage() throws Exception
    {
        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Select destination for documents on Cloud");

        destinationAndAssigneePage.selectCancelButton();
        cloudTaskOrReviewPage.render();

        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectCloseButton();
        cloudTaskOrReviewPage.render();
    }

    @Test(dependsOnMethods = "selectDestinationAndAssigneePage")
    public void isSelectAssigneeButtonEnabled() throws Exception
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled(), "Verifying the Select Assignee button is disabled when the destination is not chosen");
    }

    @Test(dependsOnMethods = "isSelectAssigneeButtonEnabled")
    public void isAssigneePresent() throws Exception
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying the Assignee is not present");
        Assert.assertEquals(cloudTaskOrReviewPage.getAssignee(), "", "Verify no assignee is present");
    }

    @Test(dependsOnMethods = "isAssigneePresent")
    public void isCloudReviewTaskElementsPresent()
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying Elements specific for Cloud Review Task are not present upon selecting the Task Type as Simple Cloud Task");
    }

    @Test(dependsOnMethods = "isCloudReviewTaskElementsPresent", expectedExceptions = PageOperationException.class)
    public void isSelectReviewersButtonEnabled()
    {
        cloudTaskOrReviewPage.isSelectReviewersButtonEnabled();
    }

    @Test(dependsOnMethods = "isSelectReviewersButtonEnabled", expectedExceptions = PageOperationException.class)
    public void getReviewers()
    {
        cloudTaskOrReviewPage.getReviewers();
    }

    @Test(dependsOnMethods = "getReviewers")
    public void selectDestination()
    {
        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        cloudTaskOrReviewPage.render();
    }

    @Test(dependsOnMethods = "selectDestination")
    public void verifyDestinationDetailsAfterDestinationSelection() throws Exception
    {
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), cloudNetwork);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), cloudFolder+"/");
    }

    @Test(dependsOnMethods = "verifyDestinationDetailsAfterDestinationSelection")
    public void selectAssignee()
    {
        Assert.assertTrue(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled());
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
    }

    @Test(dependsOnMethods = "selectAssignee")
    public void isEnterASearchTermMessageDisplayed()
    {
        Assert.assertTrue(assignmentPage.isEnterASearchTermMessageDisplayed());
    }

    @Test(dependsOnMethods = "isEnterASearchTermMessageDisplayed")
    public void getWarningMessage()
    {
        assignmentPage.clearSearchField();
        assignmentPage.selectSearchButton();
        Assert.assertEquals(assignmentPage.getWarningMessage(), "Enter at least 1 character(s) to search");
    }

    @Test(dependsOnMethods = "getWarningMessage")
    public void getUserList()
    {
        List<String> userList = assignmentPage.getUserList(cloudUserName);
        Assert.assertEquals(userList.size(), 1);
    }

    @Test(dependsOnMethods = "getUserList")
    public void isAddIconPresent()
    {
        Assert.assertTrue(assignmentPage.isAddIconPresent(cloudUserName));
        assignmentPage.selectUser(cloudUserName);
        Assert.assertFalse(assignmentPage.isAddIconPresent(cloudUserName));
        Assert.assertFalse(assignmentPage.isEnterASearchTermMessageDisplayed());
    }

    @Test(dependsOnMethods = "isAddIconPresent")
    public void isUserSelected()
    {
        Assert.assertTrue(assignmentPage.isUserSelected(cloudUserName));
        assignmentPage.removeUser(cloudUserName);
        Assert.assertFalse(assignmentPage.isUserSelected(cloudUserName));
    }

    @Test(dependsOnMethods = "isUserSelected", expectedExceptions = IllegalArgumentException.class)
    public void isUserSelectedExpectIllegalArgumentException()
    {
        assignmentPage.removeUser("");
    }

    @Test(dependsOnMethods = "isUserSelectedExpectIllegalArgumentException", expectedExceptions = PageOperationException.class)
    public void removeUserExpectPageOperationException()
    {
        assignmentPage.removeUser(cloudUserName);
    }

    @Test(dependsOnMethods = "removeUserExpectPageOperationException", expectedExceptions = IllegalArgumentException.class)
    public void removeUserExpectIllegalArgumentException()
    {
        assignmentPage.removeUser("");
    }

    @Test(dependsOnMethods = "removeUserExpectIllegalArgumentException", expectedExceptions = PageOperationException.class)
    public void removeUser()
    {
        assignmentPage.removeUser(cloudUserName);
    }

    @Test(dependsOnMethods = "removeUser")
    public void selectCloseButton()
    {
        assignmentPage.selectCloseButton();
        cloudTaskOrReviewPage.render();
        Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying Assignee is not present");
    }

    @Test(dependsOnMethods = "selectCloseButton")
    public void selectCancelButton()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectCancelButton();
        cloudTaskOrReviewPage.render();
        Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying Assignee is not present");
    }

    @Test(dependsOnMethods = "selectCancelButton")
    public void getAssignee()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectUser(cloudUserName);
        assignmentPage.selectOKButton();
        cloudTaskOrReviewPage.render();
        Assert.assertTrue(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying Assignee is present");
        Assert.assertTrue(cloudTaskOrReviewPage.getAssignee().contains(cloudUserName), "Verifying Assignee");
    }

    @Test(dependsOnMethods = "getAssignee", expectedExceptions = PageOperationException.class)
    public void getRequiredApprovalPercentageHelpText()
    {
        cloudTaskOrReviewPage.getRequiredApprovalPercentageHelpText();
    }
}
