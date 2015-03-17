/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests to verify selectSyncToCloud methods in FileDirectoryInfo and DocumentLibraryNavigation are working correctly.
 * @author Ranjith Manyam
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Hybrid")
public class SelectSyncToCloudTest extends AbstractDocumentTest
{
    private String siteName1;
    private File file1;
    private DocumentLibraryPage documentLibPage;
    private DestinationAndAssigneePage destinationAndAssigneePage ;

    /**
     * Pre test setup to disconnect CloudSync
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName1 = "Site-1" + System.currentTimeMillis();
        file1 = SiteUtil.prepareFile("File-1"+System.currentTimeMillis());
        loginAs(username, password);
        disconnectCloudSync(drone);
        SiteUtil.createSite(drone, siteName1, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();

        // User File2 if required
        // uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        // documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
    }

    @Test
    public void selectSyncToCloudBeforeSyncSetUp() throws Exception
    {
        CloudSignInPage cloudSignInPage = documentLibPage.getFileDirectoryInfo(file1.getName()).selectSyncToCloud().render();
        // Verify selectSyncToCloud returns Cloud Sign In page
        Assert.assertEquals(cloudSignInPage.getPageTitle(), "Sign in to Alfresco in the cloud");
        cloudSignInPage.selectCancelButton();
        documentLibPage = drone.getCurrentPage().render();

        if(!documentLibPage.getFileDirectoryInfo(file1.getName()).isCheckboxSelected())
        {
            FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(file1.getName());
            fileDirectoryInfo.selectCheckbox();
        }
        DocumentLibraryNavigation docLibNav = documentLibPage.getNavigation();
        cloudSignInPage = docLibNav.selectSyncToCloud().render();
        // Verify selectSyncToCloud returns Cloud Sign In page
        Assert.assertEquals(cloudSignInPage.getPageTitle(), "Sign in to Alfresco in the cloud");
        cloudSignInPage.selectCancelButton();
        documentLibPage = drone.getCurrentPage().render();
    }

    @Test(dependsOnMethods = "selectSyncToCloudBeforeSyncSetUp")
    public void selectSyncToCloudAfterSyncSetUp() throws Exception
    {
        signInToCloud(drone, cloudUserName, cloudUserPassword);

        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName1);
        FileDirectoryInfo fileInfo = documentLibPage.getFileDirectoryInfo(file1.getName());
        destinationAndAssigneePage = fileInfo.selectSyncToCloud().render();

        String title = "Sync " + file1.getName() + " to The Cloud";
        // Verify selectSyncToCloud returns DestinationAndAssignee page
        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), title);
        documentLibPage = destinationAndAssigneePage.selectCancelButton().render();

        FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(file1.getName());
        fileDirectoryInfo.selectCheckbox();
        DocumentLibraryNavigation docLibNav = documentLibPage.getNavigation();
        destinationAndAssigneePage = docLibNav.selectSyncToCloud().render();
        // Verify selectSyncToCloud returns DestinationAndAssignee page
        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Sync selected content to the Cloud");
        documentLibPage = destinationAndAssigneePage.selectCancelButton().render();
    }

    @Test (dependsOnMethods = "selectSyncToCloudAfterSyncSetUp")
    public void selectRequestSync()
    {
        DocumentLibraryNavigation docLibNav = documentLibPage.getNavigation();
        destinationAndAssigneePage = docLibNav.selectSyncToCloud().render();
        documentLibPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
        if(!documentLibPage.getFileDirectoryInfo(file1.getName()).isCheckboxSelected())
        {
            documentLibPage.getFileDirectoryInfo(file1.getName()).selectCheckbox();
        }
        documentLibPage = documentLibPage.getNavigation().selectRequestSync().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file1.getName()).isCloudSynced());
    }

    @AfterClass
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName1);
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        cloudSyncPage.disconnectCloudAccount();
    }
}
