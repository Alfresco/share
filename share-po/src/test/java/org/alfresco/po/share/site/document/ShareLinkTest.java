/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
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
    private String firstName = userName;
    private String lastName = userName;
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
        createUser();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile("alfresco123");
        tempFile = SiteUtil.prepareFile("tempFile123");
        createData();
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Create User
     * 
     * @throws Exception
     */
    public void createUser() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.inputFirstName(firstName);
            newPage.inputLastName(lastName);
            newPage.inputEmail(userName);
            newPage.inputUsername(userName);
            newPage.inputPassword(userName);
            newPage.inputVerifyPassword(userName);
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userName).render();
            Assert.assertTrue(userCreated.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            // loginAs(username, password);
            loginAs("user1@premiernet.test", "qu@ck3rs");
            firstName = anotherUser.getfName();
            lastName = anotherUser.getlName();
            userName = firstName + " " + lastName;
        }
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
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName1, folderDescription).render();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectGalleryView()).render();
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
        Assert.assertEquals(viewPage.getDrone().getCurrentUrl(), shareLink);
        Assert.assertTrue(viewPage.isDocumentViewDisplayed());
        Assert.assertEquals(viewPage.getButtonName(), "Document Details");
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());
    }
    
    @Test(groups = { "alfresco-one" }, priority = 2)
    public void testViewLogo()
    {
        String logoSrc = viewPage.getLogoImgSrc();
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (!version.isCloud())
        {
            Assert.assertTrue(logoSrc.contains("logo-enterprise.png"));
        }
        else
        {
            Assert.assertTrue(logoSrc.contains("logo.png"));
        }
        DocumentDetailsPage detailsPage = viewPage.clickOnDocumentDetailsButton().render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    @Test(groups = { "alfresco-one" }, priority = 3)
    public void testVerifyUnShareLink()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
    }

    @Test(groups = { "alfresco-one" }, priority = 4)
    public void testVerifyEmailLink()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isEmailLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    @Test(groups = { "alfresco-one" }, priority = 5)
    public void testOtherShareLinks() throws InterruptedException
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(tempFile.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();

        Assert.assertTrue(shareLinkPage.isFaceBookLinkPresent());
        shareLinkPage.clickFaceBookLink();
        waitInSeconds(2);
        
        String mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        Assert.assertTrue(shareLinkPage.isTwitterLinkPresent());
        shareLinkPage.clickTwitterLink();
        waitInSeconds(2);

        mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        Assert.assertTrue(shareLinkPage.isGooglePlusLinkPresent());
        shareLinkPage.clickGooglePlusLink();
        waitInSeconds(2);

        mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
    }

    @Test(groups = { "alfresco-one" }, priority = 6, expectedExceptions = UnsupportedOperationException.class)
    public void clickShareLinkFolder()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName1);
        thisRow.clickShareLink().render();
    }

    @Test(groups = { "alfresco-one", "ChromeIssue" }, priority = 7)
    public void testDocPreviewController() throws Exception
    {
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        File file1 = SiteUtil.prepareFile("File-1" + System.currentTimeMillis(), "This is a sample file");
        DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        documentLibPage.render();

        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName());
        Assert.assertTrue(thisRow.isShareLinkVisible());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();

        String shareLink = shareLinkPage.getShareURL();
        drone.createNewTab();
        drone.navigateTo(shareLink);
        ViewPublicLinkPage viewPage = new ViewPublicLinkPage(drone);
        viewPage.render();

        // get the zoom scale
        int normalZoom = viewPage.getIntegerZoomScale();
        Assert.assertTrue(viewPage.getZoomScale().matches("[0-9][0-9]?[0-9]%"));

        viewPage.clickZoomIn();
        int firstZoom = viewPage.getIntegerZoomScale();
        Assert.assertTrue(firstZoom > normalZoom);

        // zoom in
        viewPage.clickZoomIn();
        int secondZoom = viewPage.getIntegerZoomScale();
        Assert.assertTrue(secondZoom > firstZoom);

        // zoom out
        viewPage.clickZoomOut();
        int zoomOut = viewPage.getIntegerZoomScale();
        Assert.assertTrue(zoomOut < secondZoom);

        drone.closeTab();
        documentLibPage.render();
    }
}