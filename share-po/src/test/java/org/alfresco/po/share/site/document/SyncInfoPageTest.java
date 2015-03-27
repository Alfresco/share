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
import org.alfresco.po.share.ShareUtil;
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
 * Unit tests to verify methods of Sync Info functions are working correctly.
 * 
 * @author nshah
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Hybrid")
public class SyncInfoPageTest extends AbstractDocumentTest
{

    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private DestinationAndAssigneePage desAndAsgPage;
    private String folder;
    private String folder2;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folder = "TempFolder" + System.currentTimeMillis();
        folder2 = "TempFolder-2" + System.currentTimeMillis();
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        file = SiteUtil.prepareFile();
    }

    @Test(groups = "Hybrid")
    public void prepareCloudSyncData() throws Exception
    {
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();

        CloudSignInPage cloudSignInPage = cloudSyncPage.selectCloudSign().render();
        cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();

        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        drone.refresh();
        desAndAsgPage = (DestinationAndAssigneePage) documentLibPage.getFileDirectoryInfo(file.getName()).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage) desAndAsgPage.selectSubmitButtonToSync()).render();
        documentLibPage.render().getNavigation().selectCreateNewFolder().render().createNewFolder(folder);
        drone.refresh();
        documentLibPage.render().getNavigation().selectCreateNewFolder().render().createNewFolder(folder2);
        drone.refresh();
        desAndAsgPage = (DestinationAndAssigneePage) documentLibPage.getFileDirectoryInfo(folder).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage) desAndAsgPage.selectSubmitButtonToSync()).render();
        documentLibPage = documentLibPage.renderItem(maxWaitTime_CloudSync, folder2);
        desAndAsgPage = documentLibPage.getFileDirectoryInfo(folder2).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage) desAndAsgPage.selectSubmitButtonToSync()).render();
        documentLibPage = documentLibPage.renderItem(maxWaitTime_CloudSync, folder2);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "prepareCloudSyncData")
    public void testSyncInfoIcon() throws Exception
    {
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(folder).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isViewCloudSyncInfoLinkPresent());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(folder).isViewCloudSyncInfoLinkPresent());
        Assert.assertEquals("Click to view sync info", documentLibPage.getFileDirectoryInfo(folder).getCloudSyncType());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(file.getName()).isCloudSyncFailed());
    }

    @Test(groups = "Hybrid", dependsOnMethods = "prepareCloudSyncData")
    public void testSyncInfoPopupMethods() throws Exception
    {
        drone.refresh();
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
        syncInfoPage.render(5000);
        Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Sync") ? true : false);
        Assert.assertEquals("premiernet.test>Auto Account's Home>Documents>" + folder, syncInfoPage.getCloudSyncLocation());
        Assert.assertEquals(folder, syncInfoPage.getCloudSyncDocumentName());

        Assert.assertTrue(syncInfoPage.isLogoPresent());
        Assert.assertTrue(syncInfoPage.isRequestSyncButtonPresent());
        Assert.assertTrue(syncInfoPage.isSyncStatusPresent());
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        Assert.assertNotNull(syncInfoPage.getSyncPeriodDetails());
        Assert.assertFalse(syncInfoPage.isUnableToRetrieveLocation());
        syncInfoPage.clickOnCloseButton();
        documentLibPage = (DocumentLibraryPage) drone.getCurrentPage().render();
        Assert.assertTrue(documentLibPage instanceof DocumentLibraryPage);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "testSyncInfoPopupMethods")
    public void testSyncInfoPopup() throws Exception
    {
        drone.refresh();
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
        syncInfoPage.render(5000);
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

        documentLibPage = (DocumentLibraryPage) drone.getCurrentPage().render();
        fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);
        Assert.assertFalse(fileDirInfo.isViewCloudSyncInfoLinkPresent());

        syncInfoPage = documentLibPage.getFileDirectoryInfo(folder2).clickOnViewCloudSyncInfo();
        syncInfoPage.render(1000);
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        syncInfoPage.selectUnsyncRemoveContentFromCloud(false);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "testSyncInfoPopup")
    public void testSyncFailedInfoDetails() throws Exception
    {
        int i = 0;
        SyncInfoPage syncInfoPage;
        drone.refresh();
        documentLibPage.render();
        syncInfoPage = documentLibPage.getFileDirectoryInfo(file.getName()).clickOnViewCloudSyncInfo().render();
        syncInfoPage.render(1000);
        syncInfoPage.selectUnsyncRemoveContentFromCloud(false);

        drone.refresh();
        documentLibPage.render();
        desAndAsgPage = (DestinationAndAssigneePage) documentLibPage.getFileDirectoryInfo(file.getName()).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage) desAndAsgPage.selectSubmitButtonToSync()).render();
        documentLibPage.render();
        while (i <= 15)
        {
            if (documentLibPage.getFileDirectoryInfo(file.getName()).isCloudSyncFailed())
            {
                syncInfoPage = new SyncInfoPage(drone);
                syncInfoPage = documentLibPage.getFileDirectoryInfo(file.getName()).clickOnViewCloudSyncInfo().render();
                syncInfoPage.clickShowDetails();
                Assert.assertTrue(syncInfoPage.getSyncFailedErrorDetail().equals("Content with the same name already exists in the target folder."));
                Assert.assertTrue(syncInfoPage.getTechnicalReport().contains("Content with the same name already exists in the target folder."));
                break;
            }
            else
            {
                i++;
                drone.refresh();
            }
        }
    }

    @AfterClass
    public void teardown()
    {
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        cloudSyncPage.disconnectCloudAccount();
        SiteUtil.deleteSite(drone, siteName);
    }
}
