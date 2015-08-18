/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Share Link page is operating correctly.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
public class ShareLinkTest extends AbstractDocumentTest
{
    private static String siteName;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private File tempFile;
    private static String folderName1;
    private static String folderDescription;
    ViewPublicLinkPage viewPage;

    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        loginAs(userName, UNAME_PASSWORD);
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
        file = siteUtil.prepareFile("alfresco123");
        tempFile = siteUtil.prepareFile("tempFile123");
        createData();
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }


    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     * 
     * @throws Exception
     */
    public void createData() throws Exception
    {
        folderName1 = "The first folder";
        folderDescription = String.format("Description of %s", folderName1);
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName1, folderDescription).render();
    }

    @Test(groups = { "alfresco-one" }, priority = 1)
    public void testViewLink()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertTrue(thisRow.isShareLinkVisible());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(shareLinkPage.isViewLinkPresent());
        String shareLink = shareLinkPage.getShareURL();

        viewPage = shareLinkPage.clickViewButton().render();
        Assert.assertEquals(driver.getCurrentUrl(), shareLink);
        Assert.assertTrue(viewPage.isDocumentViewDisplayed());
        Assert.assertEquals(viewPage.getButtonName(), "Document Details");
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());
        viewPage.clickOnDocumentDetailsButton();
    }
    

    @Test(groups = { "alfresco-one" }, priority = 3)
    public void testVerifyUnShareLink()
    {
    	documentLibPage.getSiteNav().selectDocumentLibrary().render();
    	FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
    }

    @Test(groups = { "alfresco-one" }, priority = 4)
    public void testVerifyEmailLink()
    {
    	documentLibPage.getSiteNav().selectDocumentLibrary().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isEmailLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();
    }

    @Test(groups = { "alfresco-one" }, priority = 6, expectedExceptions = UnsupportedOperationException.class)
    public void clickShareLinkFolder()
    {
    	documentLibPage.getSiteNav().selectDocumentLibrary().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName1);
        thisRow.clickShareLink().render();
    }
}
