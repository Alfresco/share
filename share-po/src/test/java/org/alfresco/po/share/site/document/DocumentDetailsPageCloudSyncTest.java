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
 * Integration test to verify document CRUD is operating correctly.
 *
 * @author Ranjith Manyam
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Hybrid")
public class DocumentDetailsPageCloudSyncTest extends AbstractDocumentTest
{
        private String siteName1;
        private File file1;
        private File file2;
        private File file3;
        private DocumentLibraryPage documentLibPage;
        private DocumentDetailsPage documentDetailsPage;
        private DestinationAndAssigneePage destinationAndAssigneePage;

        /**
         * Pre test setup of a dummy file to upload.
         *
         * @throws Exception
         */
        @BeforeClass
        public void prepare() throws Exception
        {
                siteName1 = "Site-1" + System.currentTimeMillis();
                file1 = SiteUtil.prepareFile("File-1" + System.currentTimeMillis());
                file2 = SiteUtil.prepareFile("File-2" + System.currentTimeMillis());
                file3 = SiteUtil.prepareFile("File-3" + System.currentTimeMillis());

                loginAs(username, password);

                signInToCloud(drone, cloudUserName, cloudUserPassword);

                //SiteUtil.createSite(drone, siteName2, "Public");
                SiteUtil.createSite(drone, siteName1, "Public");
                SitePage site = drone.getCurrentPage().render();
                documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
                UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
                documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
                uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
                documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
                uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
                documentLibPage = uploadForm.uploadFile(file3.getCanonicalPath()).render();
        }

        @AfterClass
        public void tearDown()
        {
                //Disconnect Cloud Account
                MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
                CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
                cloudSyncPage.disconnectCloudAccount();
                // Delete site
                SiteUtil.deleteSite(drone, siteName1);
                logout(drone);
        }

        @Test
        public void syncStatusBeforeSync() throws Exception
        {
                documentDetailsPage = documentLibPage.selectFile(file1.getName()).render();
                Assert.assertFalse(documentDetailsPage.isRequestSyncIconDisplayed());
                Assert.assertEquals(documentDetailsPage.getSyncStatus(), "");
                Assert.assertEquals(documentDetailsPage.getLocationInCloud(), "");

        }

        @Test(dependsOnMethods = "syncStatusBeforeSync")
        public void syncStatusAfterSync()
        {
                destinationAndAssigneePage = documentDetailsPage.selectSyncToCloud().render();
                documentDetailsPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
                Assert.assertTrue(checkIfContentIsSynced(drone));
        }

        @Test(dependsOnMethods = "syncStatusAfterSync")
        public void getLocationInCloud()
        {
                // Verify location in the cloud
                Assert.assertEquals(documentDetailsPage.getLocationInCloud(), "premiernet.test > Auto Account's Home > Documents");
        }

        @Test(dependsOnMethods = "getLocationInCloud")
        public void isRequestSyncIconDisplayed()
        {
                // Verify Request to Sync icon is displayed.
                Assert.assertTrue(documentDetailsPage.isRequestSyncIconDisplayed());
        }

        @Test(dependsOnMethods = "isRequestSyncIconDisplayed")
        public void isSyncMessagePresent() throws Exception
        {
                documentLibPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
                destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(file3.getName()).selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibPage.isSyncMessagePresent());

        }

}
