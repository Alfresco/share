///*
// * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
// * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
// * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
// * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.po.share.site.document;
//
//import java.io.File;
//
//import org.alfresco.po.share.ShareUtil;
//import org.alfresco.po.share.site.UploadFilePage;
//import org.alfresco.po.share.util.SiteUtil;
//import org.alfresco.po.util.FailedTestListener;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify Revert to version page methods are operating correctly.
// * 
// * @author Ranjith Manyam
// * @since 1.7
// */
//@Listeners(FailedTestListener.class)
//@Test(groups="Enterprise4.2")
//public class RevertToVersionPageTest extends AbstractDocumentTest
//{
//    private static String siteName;
//    private static DocumentLibraryPage documentLibPage;
//    private static DocumentDetailsPage documentDetailsPage;
//    private File file1;
//
//
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @SuppressWarnings("unused")
//    @BeforeClass
//    private void prepare() throws Exception
//    {
//        siteName = "site" + System.currentTimeMillis();
//        ShareUtil.loginAs(drone, shareUrl, username, password).render();
//        SiteUtil.createSite(drone, siteName, "description", "Public");
//        file1 = SiteUtil.prepareFile("File1");
//    }
//
//
//
//    @AfterClass
//    public void teardown()
//    {
//        SiteUtil.deleteSite(drone, siteName);
//    }
//    /**
//     * Test Reverting a file to previous version
//     *
//     * @throws Exception
//     */
//    @Test
//    public void createData() throws Exception
//    {
//        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
//        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
//    }
//    
//
//    @Test(dependsOnMethods = "createData", expectedExceptions = IllegalArgumentException.class)
//    public void selectRevertToVersion()
//    {
//        documentDetailsPage = documentLibPage.selectFile(file1.getName()).render();
//        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
//        ContentDetails contentDetails = new ContentDetails();
//        contentDetails.setName(file1.getName());
//        contentDetails.setDescription("Test Description");
//        documentDetailsPage = inlineEditPage.save(contentDetails).render();
//        Assert.assertEquals(documentDetailsPage.getProperties().get("Description"), "Test Description");
//
//        RevertToVersionPage revertToVersionPage = documentDetailsPage.selectRevertToVersion("1.0").render();
//        documentDetailsPage = revertToVersionPage.submit().render();
//        String expected = documentDetailsPage.getProperties().get("Description");
//        Assert.assertEquals(expected, "(None)");
//
//        documentDetailsPage.selectRevertToVersion("test");
//        Assert.fail("Should have not reached this line as the above statement throw IllegalArgumentException");
//    }
//}
