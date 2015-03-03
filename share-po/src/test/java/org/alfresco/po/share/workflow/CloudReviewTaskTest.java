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
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
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
 * Integration test to verify Simple Cloud Task page.
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
@Test(groups = {"Hybrid"})
@Listeners(FailedTestListener.class)
public class CloudReviewTaskTest extends AbstractTest
{
    MyWorkFlowsPage myWorkFlowsPage;
    StartWorkFlowPage startWorkFlowPage;
    CloudTaskOrReviewPage cloudTaskOrReviewPage;
    DestinationAndAssigneePage destinationAndAssigneePage;
    AssignmentPage assignmentPage;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    SelectContentPage selectContentPage;
    CloudSignInPage cloudSignInPage;
    MyTasksPage myTasksPage;
    EditTaskPage editTaskPage;
    TaskDetailsPage taskDetailsPage;
    TaskHistoryPage taskHistoryPage;
    String cloudNetwork;
    String cloudSite;
    String cloudFolder;
    List<String> userList = new ArrayList<String>();
    int requiredApprovalPercentage;
    private String siteName;
    private String workFlowName;
    private String cloudComment;
    private File file1;
    private File file2;
    private File file3;
    private String file1Description;
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
        userList.add(cloudUserName);
        workFlowName = "WF-" + System.currentTimeMillis();
        requiredApprovalPercentage = 50;
        cloudComment = "Comment-" + System.currentTimeMillis();
        siteName = "CloudReviewTask-" + System.currentTimeMillis();
        file1 = SiteUtil.prepareFile("File-1");
        file2 = SiteUtil.prepareFile("File-2");
        file3 = SiteUtil.prepareFile("File-3");

        file1Description = "Description" + System.currentTimeMillis();

        loginAs(username, password);

        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file1.getCanonicalPath()).render();
        upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file2.getCanonicalPath()).render();
        upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file3.getCanonicalPath()).render();
        EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(file1.getName()).selectEditProperties().render();
        editDocumentPropertiesPopup.setDescription(file1Description);
        documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();

        signInToCloud(drone, cloudUserName, cloudUserPassword);

        SharePage sharePage = drone.getCurrentPage().render();
        myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();

    }

    /**
     * TearDown method to delete the site
     * @throws Exception
     */
    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
        cancelWorkFlow(workFlowName);
    }

    @Test
    public void isSimpleCloudTaskElementsPresent() throws Exception
    {
        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        cloudTaskOrReviewPage.clickHelpIcon();
        Assert.assertEquals(cloudTaskOrReviewPage.getHelpText(), "This field must have between 0 and 250 characters.");
        cloudTaskOrReviewPage.clickHelpIcon();

        Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying the Elements ");
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
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
    }

    @Test(dependsOnMethods = "selectDestinationAndAssigneePage")
    public void isSelectReviewersButtonEnabled() throws Exception
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled(), "Verifying the Select Reviewers button is disabled when the destination is not chosen");
    }

    @Test(dependsOnMethods = "isSelectReviewersButtonEnabled")
    public void isReviewersPresent() throws Exception
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying the Reviewers not present");
        Assert.assertEquals(cloudTaskOrReviewPage.getReviewers().size(), 0, "Verify no reviewers present");
    }


    @Test(dependsOnMethods = "isReviewersPresent", expectedExceptions = PageOperationException.class)
    public void isSelectAssigneeButtonEnabled()
    {
        cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled();
    }

    @Test(dependsOnMethods = "isSelectAssigneeButtonEnabled", expectedExceptions = PageOperationException.class)
    public void getAssignee()
    {
        cloudTaskOrReviewPage.getAssignee();
    }

    @Test(dependsOnMethods = "getAssignee")
    public void selectDestination()
    {
        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        Assert.assertTrue(destinationAndAssigneePage.isSyncButtonEnabled());
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
    public void selectAssignmentPage()
    {
        Assert.assertTrue(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled());
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
    }

    @Test(dependsOnMethods = "selectAssignmentPage")
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
        Assert.assertTrue(userList.size() >= 1);
        Assert.assertTrue(userList.get(0).contains(cloudUserName));
    }

    @Test(dependsOnMethods = "getUserList")
    public void isAddIconPresent()
    {
        Assert.assertTrue(assignmentPage.isAddIconPresent(cloudUserName));
        assignmentPage.selectUsers(userList);
        Assert.assertFalse(assignmentPage.isAddIconPresent(cloudUserName));
    }

    @Test(dependsOnMethods = "isAddIconPresent")
    public void isUserSelected()
    {
        Assert.assertTrue(assignmentPage.isUserSelected(cloudUserName));
        assignmentPage.removeUsers(userList);
        Assert.assertFalse(assignmentPage.isUserSelected(cloudUserName));
    }

    @Test(dependsOnMethods = "isUserSelected", expectedExceptions = PageOperationException.class)
    public void removeUserExpectPageOperationException()
    {
        assignmentPage.removeUsers(userList);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "removeUserExpectPageOperationException", expectedExceptions = IllegalArgumentException.class)
    public void removeUserExpectIllegalArgumentException()
    {
        List<String> emptyList = new ArrayList<String>();
        assignmentPage.removeUsers(emptyList);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "removeUserExpectIllegalArgumentException", expectedExceptions = PageOperationException.class)
    public void removeUsers()
    {
        assignmentPage.removeUsers(userList);
    }

    @Test(dependsOnMethods = "removeUsers")
    public void selectCloseButton()
    {
        assignmentPage.selectCloseButton();
        cloudTaskOrReviewPage.render();
        Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying Assignee is not present");
    }

    @Test(dependsOnMethods = "selectCloseButton")
    public void selectCancelButton()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectCancelButton();
        cloudTaskOrReviewPage.render();
        Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying Assignee is not present");
    }

    @Test(dependsOnMethods = "selectCancelButton")
    public void getReviewers()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectUser(cloudUserName);
        assignmentPage.selectOKButton();
        cloudTaskOrReviewPage.render();
        Assert.assertTrue(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying Reviewer present");
        Assert.assertEquals(cloudTaskOrReviewPage.getReviewers().size(), 1, "Verifying Reviewer List");
        Assert.assertTrue(cloudTaskOrReviewPage.getReviewers().get(0).contains(cloudUserName), "Verifying Reviewer");
    }

    @Test(dependsOnMethods = "getReviewers")
    public void getRequiredApprovalPercentageHelpText()
    {
        Assert.assertEquals(cloudTaskOrReviewPage.getRequiredApprovalPercentageHelpText(), "This field must have a value between 1 and 100.");
    }

    @Test(dependsOnMethods = "getRequiredApprovalPercentageHelpText")
    public void getAfterCompletionOptions()
    {
        List<String> options = cloudTaskOrReviewPage.getAfterCompletionOptions();
        Assert.assertEquals(options.size(), KeepContentStrategy.values().length);
        Assert.assertTrue(options.contains(KeepContentStrategy.DELETECONTENT.getStrategy()));
        Assert.assertTrue(options.contains(KeepContentStrategy.KEEPCONTENT.getStrategy()));
        Assert.assertTrue(options.contains(KeepContentStrategy.KEEPCONTENTREMOVESYNC.getStrategy()));
    }

    @Test(dependsOnMethods = "getAfterCompletionOptions")
    public void getSelectedAfterCompletionOption()
    {
        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.DELETECONTENT);
        Assert.assertEquals(cloudTaskOrReviewPage.getSelectedAfterCompletionOption(), KeepContentStrategy.DELETECONTENT);

        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.KEEPCONTENT);
        Assert.assertEquals(cloudTaskOrReviewPage.getSelectedAfterCompletionOption(), KeepContentStrategy.KEEPCONTENT);

        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        Assert.assertEquals(cloudTaskOrReviewPage.getSelectedAfterCompletionOption(), KeepContentStrategy.KEEPCONTENTREMOVESYNC);
    }

    @Test(dependsOnMethods = "getSelectedAfterCompletionOption")
    public void isLockOnPremiseSelected()
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isLockOnPremiseSelected(), "Verifying by default, Lock On-Premise checkbox should not be selected");
        cloudTaskOrReviewPage.selectLockOnPremiseCheckbox(true);
        Assert.assertTrue(cloudTaskOrReviewPage.isLockOnPremiseSelected(), "Verifying Lock On-Premise checkbox should be selected");
    }

    @Test(groups = "Enterprise4.2" ,dependsOnMethods = "isLockOnPremiseSelected")
    public void isRemoveAllButtonEnabledWhenNoItemsAreSelected()
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isRemoveAllButtonEnabled(), "Verifying RemoveAll button should be disabled");
    }

    @Test(dependsOnMethods = "isRemoveAllButtonEnabledWhenNoItemsAreSelected")
    public void isNoItemsSelectedMessagePresent()
    {
        Assert.assertTrue(cloudTaskOrReviewPage.isNoItemsSelectedMessagePresent(), "Verifying \"No items selected\" button is displayed");
    }

    @Test(dependsOnMethods = "isNoItemsSelectedMessagePresent")
    public void isFolderUpButtonEnabled()
    {
        selectContentPage = cloudTaskOrReviewPage.clickAddItems().render();

        Assert.assertFalse(selectContentPage.isFolderUpButtonEnabled(), "Verifying Folder Up button is disabled");

        selectContentPage.addItemFromSite(file1.getName(), siteName);

        Assert.assertTrue(selectContentPage.isFolderUpButtonEnabled(), "Verifying Folder Up button is disabled");
    }

    @Test(dependsOnMethods = "isFolderUpButtonEnabled")
    public void getAddedItems()
    {
        List<String> itemsAdded = selectContentPage.getAddedItems();
        Assert.assertEquals(itemsAdded.size(), 1);
        Assert.assertEquals(itemsAdded.get(0), file1.getName());
    }

    @Test(dependsOnMethods = "getAddedItems")
    public void isAddItemIconPresent()
    {
        Assert.assertFalse(selectContentPage.isAddIconPresent(file1.getName()));
    }

    @Test(dependsOnMethods = "isAddItemIconPresent")
    public void removeItem()
    {
        selectContentPage.removeItem(file1.getName());
        Assert.assertTrue(selectContentPage.isAddIconPresent(file1.getName()));
    }

    @Test(dependsOnMethods = "removeItem")
    public void selectCloseButtonOnAddItems()
    {
        selectContentPage.addItemFromSite(file1.getName(), siteName);

        List<String> itemsAdded = selectContentPage.getAddedItems();
        Assert.assertEquals(itemsAdded.size(), 1);
        Assert.assertEquals(itemsAdded.get(0), file1.getName());

        selectContentPage.selectCloseButton();
        cloudTaskOrReviewPage.render();
        Assert.assertTrue(cloudTaskOrReviewPage.isNoItemsSelectedMessagePresent());

        selectContentPage = cloudTaskOrReviewPage.clickAddItems().render();

        itemsAdded = selectContentPage.getAddedItems();
        Assert.assertEquals(itemsAdded.size(), 1);
        Assert.assertEquals(itemsAdded.get(0), file1.getName());
    }

    @Test(dependsOnMethods = "selectCloseButtonOnAddItems")
    public void selectCancelButtonOnAddItems()
    {
        selectContentPage.selectCancelButton();
        cloudTaskOrReviewPage.render();
        Assert.assertTrue(cloudTaskOrReviewPage.isNoItemsSelectedMessagePresent());

        selectContentPage = cloudTaskOrReviewPage.clickAddItems().render();
        List<String> itemsAdded = selectContentPage.getAddedItems();
        Assert.assertEquals(itemsAdded.size(), 1);
        Assert.assertEquals(itemsAdded.get(0), file1.getName());
    }

    @Test(dependsOnMethods = "selectCancelButtonOnAddItems")
    public void selectOKButtonOnAddItems()
    {
        selectContentPage.selectOKButton();
        cloudTaskOrReviewPage.render();
        Assert.assertFalse(cloudTaskOrReviewPage.isNoItemsSelectedMessagePresent());
        Assert.assertTrue(cloudTaskOrReviewPage.isRemoveAllButtonEnabled());
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file1.getName()));
    }

    @Test(dependsOnMethods = "selectOKButtonOnAddItems")
    public void selectedWorkFlowItem()
    {
        List<SelectedWorkFlowItem> selectedWorkFlowItem = cloudTaskOrReviewPage.getSelectedItem(file1.getName());

        Assert.assertEquals(selectedWorkFlowItem.size(), 1);
        Assert.assertEquals(selectedWorkFlowItem.get(0).getItemName(), file1.getName());
        Assert.assertEquals(selectedWorkFlowItem.get(0).getDescription(), file1Description);
        Assert.assertEquals(selectedWorkFlowItem.get(0).getDateModified().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertTrue(selectedWorkFlowItem.get(0).isRemoveLinkPresent());
        Assert.assertTrue(selectedWorkFlowItem.get(0).isViewMoreActionsPresent());

        List<SelectedWorkFlowItem> selectedWorkFlowItemList = cloudTaskOrReviewPage.getSelectedItems();
        Assert.assertEquals(selectedWorkFlowItemList.size(), 1);

        String itemNameLinkURL = selectedWorkFlowItem.get(0).getItemNameLink().getHref();
        String viewMoreActionsURL = selectedWorkFlowItem.get(0).getViewMoreActions().getHref();

        String mainWindow = drone.getWindowHandle();

        drone.createNewTab();

        drone.navigateTo(itemNameLinkURL);

        documentDetailsPage = drone.getCurrentPage().render();

        Assert.assertFalse(documentDetailsPage.isFileSyncSetUp());
        Assert.assertFalse(documentDetailsPage.isPartOfWorkflow());

        drone.closeTab();
        drone.switchToWindow(mainWindow);

        drone.createNewTab();
        drone.navigateTo(viewMoreActionsURL);

        documentDetailsPage = drone.getCurrentPage().render();

        Assert.assertFalse(documentDetailsPage.isFileSyncSetUp());
        Assert.assertFalse(documentDetailsPage.isPartOfWorkflow());
        drone.closeTab();
        drone.switchToWindow(mainWindow);
    }

    @Test(dependsOnMethods = "selectedWorkFlowItem")
    public void selectRemoveButton()
    {
        List<SelectedWorkFlowItem> selectedWorkFlowItem = cloudTaskOrReviewPage.getSelectedItem(file1.getName());

        selectedWorkFlowItem.get(0).selectRemoveButton();

        Assert.assertFalse(cloudTaskOrReviewPage.isItemAdded(file1.getName()));
    }

    @Test(dependsOnMethods = "selectRemoveButton")
    public void testRemoveOneFile()
    {
        cloudTaskOrReviewPage.selectItem(file1.getName(), siteName);
        cloudTaskOrReviewPage.selectItem(file2.getName(), siteName);
        cloudTaskOrReviewPage.selectItem(file3.getName(), siteName);

        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file1.getName()));
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file2.getName()));
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file3.getName()));

        cloudTaskOrReviewPage.getSelectedItem(file2.getName()).get(0).selectRemoveButton();
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file1.getName()));
        Assert.assertFalse(cloudTaskOrReviewPage.isItemAdded(file2.getName()));
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(file3.getName()));
    }

    @Test(dependsOnMethods = "selectRemoveButton")
    public void selectRemoveAll()
    {
        cloudTaskOrReviewPage.selectRemoveAllButton();

        Assert.assertFalse(cloudTaskOrReviewPage.isItemAdded(file1.getName()));
        Assert.assertFalse(cloudTaskOrReviewPage.isItemAdded(file2.getName()));
        Assert.assertFalse(cloudTaskOrReviewPage.isItemAdded(file3.getName()));
    }

    @Test(dependsOnMethods = "selectRemoveAll")
    public void isErrorBalloonPresent()
    {
        Assert.assertFalse(cloudTaskOrReviewPage.isErrorBalloonPresent());

        SharePage sharePage = drone.getCurrentPage().render();
        myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) cloudTaskOrReviewPage.selectStartWorkflow();
        cloudTaskOrReviewPage.render();

        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "The value cannot be empty.");

        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        cloudTaskOrReviewPage.render();

        cloudTaskOrReviewPage.selectStartWorkflow();
        cloudTaskOrReviewPage.render();

        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "The value cannot be empty.");

        assignmentPage = cloudTaskOrReviewPage.selectReviewer().render();
        assignmentPage.selectReviewers(userList).render();

        cloudTaskOrReviewPage.render();
        cloudTaskOrReviewPage.selectStartWorkflow();

        Assert.assertFalse(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getWorkFlowCouldNotBeStartedPromptHeader(), "Workflow could not be started");
        Assert.assertTrue(cloudTaskOrReviewPage.getWorkFlowCouldNotBeStartedPromptMessage().contains("At least one content item is required to start a Cloud workflow"));
    }

    @Test(dependsOnMethods = "isErrorBalloonPresent")
    public void testSelectDestinationCloudSignInPage()
    {
        SharePopup sharePopupPage = drone.getCurrentPage().render();

        // Selecting Ok button present on Workflow could not be present prompt.
        try
        {
            sharePopupPage.handleMessage().render();
        }
        catch(ShareException e) {}

        startWorkFlowPage = drone.getCurrentPage().render();

        // Disconnecting the cloud sync.
        MyProfilePage myProfilePage = startWorkFlowPage.getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();

        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            cloudSyncPage = cloudSyncPage.disconnectCloudAccount().render();
            myWorkFlowsPage = cloudSyncPage.getNav().selectWorkFlowsIHaveStarted().render();
            startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
            cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
            cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

            Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying the Elements ");

            cloudSignInPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

            Assert.assertNotNull(cloudSignInPage);
        }
    }

    @Test(dependsOnMethods = "testSelectDestinationCloudSignInPage")
    public void createWorkFlow() throws Exception {
        destinationAndAssigneePage = cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();
        destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        cloudTaskOrReviewPage.render();

        cloudTaskOrReviewPage.enterMessageText(workFlowName);


        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectUser(cloudUserName);
        assignmentPage.selectOKButton();
        cloudTaskOrReviewPage.render();
    }
    @Test(dependsOnMethods = "createWorkFlow", expectedExceptions = IllegalArgumentException.class)
    public void enterRequiredApprovalPercentageWithValueLessThanZero(){
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage(-1);
    }
    @Test(dependsOnMethods = "enterRequiredApprovalPercentageWithValueLessThanZero", expectedExceptions = IllegalArgumentException.class)
    public void enterRequiredApprovalPercentageWithValueGreaterThanHundred(){
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage(101);
    }
    @Test(dependsOnMethods = "enterRequiredApprovalPercentageWithValueGreaterThanHundred")
    public void enterRequiredApprovalPercentage(){
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage(requiredApprovalPercentage);
    }

    @Test(dependsOnMethods = "enterRequiredApprovalPercentage", expectedExceptions = IllegalArgumentException.class)
    public void addItemFromSiteWithFileNameAsNull() throws Exception {
        cloudTaskOrReviewPage.selectItem(null, siteName);
    }
    @Test(dependsOnMethods = "addItemFromSiteWithFileNameAsNull", expectedExceptions = IllegalArgumentException.class)
    public void addItemFromSiteWithSiteNameAsNull() throws Exception {
        cloudTaskOrReviewPage.selectItem(file1.getName(), null);
    }

    @Test(dependsOnMethods = "addItemFromSiteWithSiteNameAsNull")
    public void addItemFromSite() throws Exception {
        cloudTaskOrReviewPage.selectItem(file1.getName(), siteName);
        cloudTaskOrReviewPage.selectStartWorkflow().render();
    }

    @Test(dependsOnMethods = "addItemFromSite")
    public void selectTaskHistoryLink() throws Exception
    {
        loginAs(hybridDrone, hybridShareUrl, cloudUserName, cloudUserPassword);
        SharePage sharePage = hybridDrone.getCurrentPage().render();
        myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(checkIfTaskIsPresent(hybridDrone, workFlowName));
        taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        taskHistoryPage = taskDetailsPage.selectTaskHistoryLink().render();
        Assert.assertEquals(taskHistoryPage.getPageTitle(), "Task History");
        myTasksPage = sharePage.getNav().selectMyTasks().render();
    }

    @Test(dependsOnMethods = "selectTaskHistoryLink")
    public void completeCloudTask() throws Exception
    {
        editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        Assert.assertFalse(editTaskPage.isReAssignButtonDisplayed());
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(cloudComment);
        myTasksPage = editTaskPage.selectApproveButton().render();
    }

    @Test(dependsOnMethods = "completeCloudTask")
    public void getRequiredApprovalPercentage() throws Exception
    {
        SharePage sharePage = drone.getCurrentPage().render();
        myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(checkIfTaskIsPresent(drone, workFlowName));

        taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        Assert.assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), requiredApprovalPercentage);
    }

    @Test(dependsOnMethods = "getRequiredApprovalPercentage")
    public void getActualApprovalPercentage() throws Exception
    {
        Assert.assertEquals(taskDetailsPage.getActualApprovalPercentage(), 100);
    }

    @Test(dependsOnMethods = "getRequiredApprovalPercentage")
    public void getComment() throws Exception
    {
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudComment));
    }

}
