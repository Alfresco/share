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
package org.alfresco.po.share.task;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.CreateNewFolderInCloudPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Integration test to verify DestinationAndAssignee page elements are in place.
 * 
 * @author Ranjith Manyam
 * @since 1.0
 */
@Test(groups="Hybrid")
public class DestinationAndAssigneePageTest extends AbstractTest
{
    private String siteName1;
    private File file1;
    private File file2;
    private DocumentLibraryPage documentLibPage;
    private DocumentDetailsPage documentDetailsPage;
    private DestinationAndAssigneePage destinationAndAssigneePage ;
    private String folder;
    /**
     * Test process of accessing DestinationAndAssignee page.
     * @throws Exception 
     */
    @BeforeClass
    public void setUp() throws Exception
    {
        siteName1 = "Site-1" + System.currentTimeMillis();
        file1 = SiteUtil.prepareFile("File-1"+System.currentTimeMillis());
        file2 = SiteUtil.prepareFile("File-2"+System.currentTimeMillis());

        loginAs(username, password);
        disconnectCloudSync(drone);
        // signInToCloud(drone, cloudUserName, cloudUserPassword);

        //SiteUtil.createSite(drone, siteName2, "Public");
        SiteUtil.createSite(drone, siteName1, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
    }

    @Test
    public void isFileSyncSetUp() throws Exception
    {
        documentDetailsPage = documentLibPage.selectFile(file1.getName()).render();
        // Verify the file sync has not been set up
        Assert.assertFalse(documentDetailsPage.isFileSyncSetUp());
    }

    @Test(dependsOnMethods = "isFileSyncSetUp")
    public void isSignUpDialogVisible() throws Exception
    {
        CloudSignInPage cloudSignInPage = documentDetailsPage.selectSyncToCloud().render();
        //Verify Sign-In dialog is displayed
        Assert.assertTrue(documentDetailsPage.isSignUpDialogVisible());

        cloudSignInPage.selectCancelButton();
    }

    @Test(dependsOnMethods = "isSignUpDialogVisible")
    public void getSyncToCloudTitle() throws Exception
    {
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();

        // Verify Title
        Assert.assertTrue(cloudSyncPage.isTitlePresent());

        CloudSignInPage cloudSignInPage = cloudSyncPage.selectCloudSign().render();
        cloudSyncPage = cloudSignInPage.loginAs(hybridUserName, hybridUserPassword).render();

        // Verify the disconnect button is displayed after set up cloud sync
        Assert.assertTrue(cloudSyncPage.isDisconnectButtonDisplayed());

        //Open the site and navigate to Document Details page

        SharePage sharePage = drone.getCurrentPage().render();
        SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(siteName1).render();
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(siteName1).render();
        documentLibPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        documentDetailsPage = documentLibPage.selectFile(file1.getName()).render();

        destinationAndAssigneePage = documentDetailsPage.selectSyncToCloud().render();

        //Verify Title of Destination And Assignee page title
        String expectedTitle = "Sync "+ file1.getName() + " to The Cloud";
        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), expectedTitle);
    }

    @Test(dependsOnMethods = "getSyncToCloudTitle")
    public void isSiteDisplayed() throws Exception
    {
        String randomString = Double.toString(Math.random());
        Assert.assertFalse(destinationAndAssigneePage.isSiteDisplayed(randomString), "Verify method should return false when searching with random site name");
        //ToDo - Check isSiteDisplayed True condition.
    }
    @Test(dependsOnMethods = "isSiteDisplayed")
    public void isNetworkDisplayed() throws Exception
    {
        String randomString = Double.toString(Math.random());
        //Tests isNetworkDisplayed method
        Assert.assertFalse(destinationAndAssigneePage.isNetworkDisplayed(randomString), "Verify method should return false when searching with random network name");
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed("premiernet.test"), "Verify method should return true when searching with random network name");
      
    }
    

    @Test(dependsOnMethods="isNetworkDisplayed")
    public void testLockOnPremCheckBox() throws Exception
    {       
        //Is Lock On-prem copy is unselected
        Assert.assertFalse(destinationAndAssigneePage.isLockOnPremCopy());
        // select Lock onprem is .
        destinationAndAssigneePage.selectLockOnPremCopy();
        Assert.assertTrue(destinationAndAssigneePage.isLockOnPremCopy());
        // set to default Lock on prem.
        destinationAndAssigneePage.selectLockOnPremCopy();
        
    }

    @Test(dependsOnMethods="testLockOnPremCheckBox")
    public void isSyncButtonEnabled() throws Exception
    {
        Assert.assertTrue(destinationAndAssigneePage.isSyncButtonEnabled());
    }

    @Test(dependsOnMethods="isSyncButtonEnabled")
    public void testIncludeSubFolderCheckBox() throws Exception
    {
        folder = "TempFolder"+System.currentTimeMillis();
        documentDetailsPage = (DocumentDetailsPage)destinationAndAssigneePage.selectSubmitButtonToSync();
        documentDetailsPage.render();
        documentLibPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();

        documentLibPage.getNavigation().selectCreateNewFolder().render().createNewFolder(folder);
        documentLibPage.render();
        FileDirectoryInfo folderInfo = documentLibPage.getFileDirectoryInfo(folder);
        destinationAndAssigneePage = folderInfo.selectSyncToCloud().render();

        //Is Include Sub-Folder is selected
        Assert.assertTrue(destinationAndAssigneePage.isIncludeSubFoldersSelected());
        // Unselect Sub-Folder.
        destinationAndAssigneePage.unSelectIncludeSubFolders();
        Assert.assertFalse(destinationAndAssigneePage.isIncludeSubFoldersSelected());
        documentLibPage = ((DocumentLibraryPage)destinationAndAssigneePage.selectSubmitButtonToSync()).render();
    }

    /**
     * Test disabled due to an existing bug: ALF-19634
     * @throws Exception
     */
    @Test(enabled = false, dependsOnMethods="testIncludeSubFolderCheckBox")
    public void testCreateFolderInCloud() throws Exception
    {
        String newFolderName = System.currentTimeMillis()+"-Folder";

        destinationAndAssigneePage = (DestinationAndAssigneePage)documentLibPage.getFileDirectoryInfo(file2.getName()).selectSyncToCloud().render();
        CreateNewFolderInCloudPage newFolderPage = destinationAndAssigneePage.selectCreateNewFolder().render();
        destinationAndAssigneePage = newFolderPage.createNewFolder(newFolderName).render();
        Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newFolderName));
    }

    @AfterClass
    public void tearDown()
    {
        
        //Disconnect Cloud Account
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        cloudSyncPage.disconnectCloudAccount();
        SiteUtil.deleteSite(drone, siteName1);
        logout(drone);
    }

}