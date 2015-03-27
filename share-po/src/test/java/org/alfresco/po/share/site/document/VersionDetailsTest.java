/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class Tests the Version Details bean
 *
 * @author Ranjith Manyam
 * @since 5.0
 */
@Listeners(FailedTestListener.class)
public class VersionDetailsTest extends AbstractTest
{
        private static String siteName;
        private static DocumentLibraryPage documentLibPage;
        private static DocumentDetailsPage detailsPage;
        private final Log logger = LogFactory.getLog(this.getClass());
        private VersionDetails versionDetails;
        private File file;
        private File file1;
        private File file2;
        private String comment1;
        private String comment2;

        /**
         * Pre test setup: Site creation, file upload, folder creation
         *
         * @throws Exception
         */
        @BeforeClass(groups = { "alfresco-one" })
        public void prepare() throws Exception
        {
                if (logger.isTraceEnabled())
                {
                        logger.trace("====prepare====");
                }

                siteName = "site" + System.currentTimeMillis();
                file = SiteUtil.prepareFile();
                file1 = SiteUtil.prepareFile();
                file2 = SiteUtil.prepareFile();
                comment1 = String.valueOf(System.currentTimeMillis()) + "-1";
                comment2 = String.valueOf(System.currentTimeMillis()) + "-2";

                ShareUtil.loginAs(drone, shareUrl, username, password).render();

                SiteUtil.createSite(drone, siteName, "description", "Public");

                SitePage page = drone.getCurrentPage().render();
                documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
                UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
                documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
                detailsPage = documentLibPage.selectFile(file.getName()).render();
        }

        @AfterClass(groups = { "alfresco-one" })
        public void teardown()
        {
                //        SiteUtil.deleteSite(drone, siteName);
        }

        @Test(groups = { "alfresco-one" })
        public void getCurrentVersionDetails() throws Exception
        {
                versionDetails = detailsPage.getCurrentVersionDetails();
                Assert.assertEquals(versionDetails.getVersionNumber(), "1.0", "Verifying Version Number");
                Assert.assertEquals(versionDetails.getFileName(), file.getName(), "Verifying File Name");
                Assert.assertTrue(!versionDetails.getLastModified().isEmpty());
                Assert.assertTrue(versionDetails.getUserName().getDescription().contains("Administrator"), "Verifying user name");
                Assert.assertEquals(versionDetails.getComment(), "(No Comment)", "Verifying Version comment");
        }

        @Test(groups = { "alfresco-one" })
        public void getOlderVersionDetails() throws Exception
        {
                UpdateFilePage updatePage = detailsPage.selectUploadNewVersion().render();
                if (logger.isTraceEnabled())
                        logger.trace("---selected new version to upload----");
                updatePage.selectMinorVersionChange();
                updatePage.setComment(comment1);
                updatePage.uploadFile(file1.getCanonicalPath());
                detailsPage = updatePage.submit().render();

                updatePage = detailsPage.selectUploadNewVersion().render();
                if (logger.isTraceEnabled())
                        logger.trace("---selected new version to upload----");
                updatePage.selectMajorVersionChange();
                updatePage.setComment(comment2);
                updatePage.uploadFile(file2.getCanonicalPath());
                detailsPage = updatePage.submit().render();

                versionDetails = detailsPage.getCurrentVersionDetails();
                Assert.assertEquals(versionDetails.getVersionNumber(), "2.0", "Verifying Version Number");
                Assert.assertEquals(versionDetails.getFileName(), file.getName(), "Verifying File Name");
                Assert.assertFalse(versionDetails.getLastModified().isEmpty());
                Assert.assertTrue(versionDetails.getUserName().getDescription().toLowerCase().contains("admin"), "Verifying user name");
                Assert.assertEquals(versionDetails.getComment(), comment2, "Verifying Version comment");
                Assert.assertTrue(versionDetails.getFullDetails().startsWith("Administrator"));
                Assert.assertTrue(versionDetails.getFullDetails().contains(comment2));

                List<VersionDetails> olderVersions = detailsPage.getOlderVersionDetails();
                Assert.assertEquals(olderVersions.size(), 2);

                Assert.assertEquals(olderVersions.get(0).getVersionNumber(), "1.1", "Verifying Version Number");
                Assert.assertEquals(olderVersions.get(0).getFileName(), file.getName(), "Verifying File Name");
                Assert.assertFalse(StringUtils.isEmpty(olderVersions.get(0).getLastModified()), "Verifying Last modified");
                Assert.assertTrue(olderVersions.get(0).getUserName().getDescription().toLowerCase().contains("admin"), "Verifying user name");
                Assert.assertEquals(olderVersions.get(0).getComment(), comment1, "Verifying Version comment");

                Assert.assertEquals(olderVersions.get(1).getVersionNumber(), "1.0", "Verifying Version Number");
                Assert.assertEquals(olderVersions.get(1).getFileName(), file.getName(), "Verifying File Name");
                Assert.assertFalse(StringUtils.isEmpty(olderVersions.get(1).getLastModified()), "Verifying Last modified");
                Assert.assertTrue(olderVersions.get(1).getUserName().getDescription().toLowerCase().contains("admin"), "Verifying user name");
                Assert.assertEquals(olderVersions.get(1).getComment(), "(No Comment)", "Verifying Version comment");
        }
}
