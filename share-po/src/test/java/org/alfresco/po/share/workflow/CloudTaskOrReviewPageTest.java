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
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.Language;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.HtmlPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * Integration test to verify CloudTaskOrReviewPage page load.
 *
 * @author Siva Kaliyappan, Bogdan Bocancea
 * @since 1.6.2
 */
@Test(groups = {"Hybrid"})
@Listeners(FailedTestListener.class)
public class CloudTaskOrReviewPageTest extends AbstractTest
{
    private String siteName;
    private String cloudSite;
    private File file;
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;
    private StartWorkFlowPage startWorkFlowPage;
    private CloudTaskOrReviewPage cloudTaskOrReviewPage;
    private int requiredApprovalPercentage;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "CloudTaskOrReviewPage" + System.currentTimeMillis();
        requiredApprovalPercentage = 100;
        cloudSite = "Auto Account's Home";
        file = SiteUtil.prepareFile();
        loginAs(username, password);
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        if (!cloudSyncPage.isDisconnectButtonDisplayed())
        {
            signInToCloud(cloudUserName, cloudUserPassword).render();
        }
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        drone.refresh();

        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(file.getName()).render();
        startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
    }

    @AfterClass
    public void teardown() throws Exception
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * This test is to assert and fill the cloud task page form.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "selectViewMoreActionsBtnTest")
    public void completeCloudTaskOrReviewPage() throws Exception
    {
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
        assertFalse(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));
        
        Assert.assertTrue(cloudTaskOrReviewPage.isAfterCompletionDropdownPresent());
        Assert.assertTrue(cloudTaskOrReviewPage.isAddButtonPresent());
        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.KEEPCONTENT);
        Assert.assertTrue(cloudTaskOrReviewPage.isAfterCompletionSelected(KeepContentStrategy.KEEPCONTENT));
        Assert.assertTrue(cloudTaskOrReviewPage.isRemoveAllButtonPresent());
        
        List<String> formLabels = cloudTaskOrReviewPage.getAllLabels();
        Assert.assertTrue(formLabels.contains("Message:"));
        Assert.assertTrue(formLabels.contains("Type:"));
        Assert.assertTrue(formLabels.contains("Due:"));
        Assert.assertTrue(formLabels.contains("Priority:"));
        Assert.assertTrue(formLabels.contains("Reviewers:*"));
        Assert.assertTrue(formLabels.contains("Required approval percentage:*"));
        Assert.assertTrue(formLabels.contains("After completion:*"));
        Assert.assertTrue(formLabels.contains("Lock on-premise content"));
        Assert.assertTrue(formLabels.contains("Items:*"));
        
        // assertFalse(isButtonSubmitted());
        WorkFlowFormDetails formDetails = createWorkflowForm();
        // Fill form Detail
        DocumentDetailsPage documentDetailsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
        assertTrue(documentDetailsPage.isDocumentDetailsPage());
    }

    /**
     * This test is to assert and fill the cloud task page form.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "completeCloudTaskOrReviewPage")
    public void checkExceptionForNullDocs() throws Exception
    {
        MyTasksPage myTasksPage = ((SharePage) drone.getCurrentPage()).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        WorkFlowFormDetails formDetails = createWorkflowForm();
        SharePopup returnedPage = (SharePopup) cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
        assertTrue(returnedPage.isShareMessageDisplayed(), "Error should be displayed");
    }

    @Test
    public void selectViewMoreActionsBtnTest()
    {
        cloudTaskOrReviewPage.render();
        SharePage returnedPage = cloudTaskOrReviewPage.selectViewMoreActionsBtn(file.getName()).render();
        assertTrue(returnedPage instanceof DocumentDetailsPage, "A document details page should be returned.");
        startWorkFlowPage = ((DocumentDetailsPage) returnedPage).selectStartWorkFlowPage().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
        
        String date = cloudTaskOrReviewPage.getItemDate(file.getName());
        Assert.assertFalse(date.isEmpty());
    }
    
    @Test(dependsOnMethods = "checkExceptionForNullDocs")
    public void checkSelectCloudReviewWithLanguage()
    {
        SiteFinderPage siteFinder;
        
        SharePage page = drone.getCurrentPage().render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
        siteFinder.selectSite(siteName);
        
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(file.getName()).render();
        
        startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getCloudTaskOrReviewPageInLanguage(Language.ENGLISH_US));
        
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
        assertFalse(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));
        
        Assert.assertTrue(cloudTaskOrReviewPage.isAfterCompletionDropdownPresent());       
    }

    /**
     * Created bean {@link WorkFlowFormDetails}.
     * 
     * @return {@link WorkFlowFormDetails}
     */
    private WorkFlowFormDetails createWorkflowForm()
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(cloudSite);
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(cloudUserName);
        formDetails.setReviewers(reviewers);
        formDetails.setMessage(siteName);
        formDetails.setDueDate("01/10/2016");
        formDetails.setLockOnPremise(false);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setTaskPriority(Priority.HIGH);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        return formDetails;
    }
    
    /**
     * Method to sign in Cloud page and return Cloud Sync page
     *
     * @return boolean
     */
    public HtmlPage signInToCloud(final String username, final String password)
    {
        final By SIGN_IN_BUTTON = By.cssSelector("button#template_x002e_user-cloud-auth_x002e_user-cloud-auth_x0023_default-button-signIn-button");
        drone.findAndWait(SIGN_IN_BUTTON).click();
        CloudSignInPage cloudSignInPage = new CloudSignInPage(drone);
        cloudSignInPage.loginAs(username, password);
        return drone.getCurrentPage();
    }

}
