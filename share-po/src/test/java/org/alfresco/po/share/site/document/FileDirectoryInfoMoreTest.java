/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify File Directory info methods are operating correctly.
 * 
 * @author Ranjith Manyam
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoMoreTest extends AbstractDocumentTest
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static String folderName;
    @SuppressWarnings("unused")
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File testSyncFailedFile;
    private File googleTestFile;
    private String folder2Name;
    private String userName = "FileDirectoryInfoMoreTest" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    private String premiunDomain;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folder2Name = "The Second Folder";
        premiunDomain = "cloud.test";
        folderDescription = String.format("Description of %s", folderName);
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.createEnterpriseUserWithGroup(userName, firstName, lastName, userName, userName, "ALFRESCO_ADMINISTRATORS");
            UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
            userPage.searchFor(userName).render();
            Assert.assertTrue(userPage.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            loginAs(username, password);
            firstName = anotherUser.getfName();
            lastName = anotherUser.getlName();
            userName = firstName + " " + lastName;
        }
        if(isHybridEnabled())
        {
            signInToCloud(drone, cloudUserName, cloudUserPassword);
        }
        drone.navigateTo(shareUrl);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        testSyncFailedFile = SiteUtil.prepareFile("1-SyncFailFile");
        googleTestFile = SiteUtil.prepareFile("googleTestFile");
    }



    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
        disconnectCloudSync(drone);
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="alfresco-one")
    public void createData() throws Exception
    {
        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testSyncFailedFile.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(googleTestFile.getCanonicalPath()).render();
        NewFolderPage folderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = folderPage.createNewFolder(folderName).render();
    }

    @Test(dependsOnMethods = "createData", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionHideFolders()
    {
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectHideFolders().render();
        Assert.assertFalse(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectHideFolders().render();
    }

    @Test(dependsOnMethods = "selectOptionHideFolders", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionShowFolders()
    {
        Assert.assertFalse(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectShowFolders().render();
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectShowFolders().render();
    }
    
    @Test(dependsOnMethods = "selectOptionShowFolders", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionHideBreadcrump()
    {
        Assert.assertTrue(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
        Assert.assertFalse(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
    }
    
    @Test(dependsOnMethods = "selectOptionHideBreadcrump", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionShowBreadcrump()
    {
        Assert.assertFalse(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
        Assert.assertTrue(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
    }
    
    @Test(dependsOnMethods = "selectOptionShowBreadcrump", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void getFoldersInNavBarWithException()
    {
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
        documentLibPage.getNavigation().getFoldersInNavBar();
    }
    
    @Test(dependsOnMethods = "getFoldersInNavBarWithException", groups = {"Enterprise4.2"})
    public void getFoldersInNavBar()
    {
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
        List<ShareLink> links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 1);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 2);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        NewFolderPage folderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = folderPage.createNewFolder(folder2Name).render(); 
        Assert.assertTrue(documentLibPage.isFileVisible(folder2Name));
        documentLibPage = documentLibPage.selectFolder(folder2Name).render();
        Assert.assertFalse(documentLibPage.isFileVisible(folder2Name));
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 3);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        Assert.assertTrue(links.get(2).getDescription().equals(folder2Name));
        documentLibPage = documentLibPage.getNavigation().selectFolderInNavBar(folderName).render();
        Assert.assertTrue(documentLibPage.isFileVisible(folder2Name));
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 2);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        documentLibPage = documentLibPage.getNavigation().selectFolderInNavBar("Documents").render();
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 1);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
    }
    
    //WEBDRONE-558 Create web drone to disabling now
    @Test(dependsOnMethods = "getFoldersInNavBar", groups = { "Enterprise4.2" }, enabled=false)
    public void testSelectEditInGoogleDocsCloud() throws Exception
    {
        // Get File row
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(googleTestFile.getName());

        GoogleDocsAuthorisation returnPage = thisRow.selectEditInGoogleDocs().render();
        logger.info(returnPage.getClass() + "");
        Assert.assertTrue(returnPage.isAuthorisationDisplayed());

        GoogleSignUpPage signUpPage = returnPage.submitAuth().render();
        Assert.assertTrue(signUpPage.isSignupWindowDisplayed());

        EditInGoogleDocsPage googleDocsPage = signUpPage.signUp(googleusername, googlepassword).render();
        assertTrue(googleDocsPage.isBrowserTitle("Google Docs Editor"));

        googleDocsPage.selectDiscard().render().clickOkButton();
        thisRow = documentLibPage.getFileDirectoryInfo(googleTestFile.getName());
        SharePage returnedPage = thisRow.selectEditInGoogleDocs().render();
        assertTrue((returnedPage instanceof EditInGoogleDocsPage), "Returned page should be EditInGoogleDocsPage page.");

        ((EditInGoogleDocsPage) returnedPage).selectDiscard().render().clickOkButton().render();
        assertTrue((drone.getCurrentPage() instanceof DocumentLibraryPage), "Returned page should be EditInGoogleDocsPage page.");
    }
    
    @Test(dependsOnMethods = "getFoldersInNavBar", groups = {"Hybrid"})
    public void isSyncToCloudLinkPresent()
    {
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncToCloudLinkPresent(), "Verifying \"Sync to Cloud\" link is present");
    }

    @Test(dependsOnMethods = "isSyncToCloudLinkPresent", groups = { "Hybrid" })
    public void isSyncFailedIconPresent()
    {
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(premiunDomain);
        destinationAndAssigneePage.render();
        documentLibPage = (DocumentLibraryPage)destinationAndAssigneePage.selectSubmitButtonToSync();
        documentLibPage.render();
        // Verify the Sync Failed icon is not displayed
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isCloudSynced());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncFailedIconPresent(5000));
        // Disconnect CloudSync
        disconnectCloudSync(drone);
        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(testSyncFailedFile.getName()).render();
        EditTextDocumentPage inlineEditPage = detailsPage.selectInlineEdit().render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(testSyncFailedFile.getName());
        contentDetails.setDescription("isSyncFailedIconPresent test");
        detailsPage = inlineEditPage.save(contentDetails).render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();

        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncToCloudLinkPresent(), "Verifying \"Sync to Cloud\" link is NOT present");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isRequestToSyncLinkPresent());
        // Select Request to sync option from more options
        documentLibPage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectRequestSync().render();
        // Verify the Sync Failed icon is displayed
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncFailedIconPresent(70000));
    }

    @Test(dependsOnMethods = "isSyncFailedIconPresent", groups = {"Hybrid"})
    public void isIndirectlySyncedIconPresent()
    {
        ContentDetails contentDetails = new ContentDetails(testName);
        CloudSignInPage cloudSignInPage = documentLibPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
        DestinationAndAssigneePage destinationAndAssigneePage = cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();
        destinationAndAssigneePage.selectNetwork(premiunDomain);
        destinationAndAssigneePage.render();
        documentLibPage = (DocumentLibraryPage)destinationAndAssigneePage.selectSubmitButtonToSync();
        documentLibPage.render();
        assertTrue(documentLibPage.getFileDirectoryInfo(folderName).isCloudSynced(), folderName + " wasn't synced");
        documentLibPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        DocumentDetailsPage detailsPage = (DocumentDetailsPage)contentPage.createWithValidation(contentDetails);
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().getFileDirectoryInfo(folderName).clickOnTitle().render();
        boolean isIconPresent = documentLibPage.getFileDirectoryInfo(testName).isIndirectlySyncedIconPresent();
        boolean isIconPresent1 = documentLibPage.getFileDirectoryInfo(folder2Name).isIndirectlySyncedIconPresent();
        assertTrue(isIconPresent && isIconPresent1, "Indirectly synced icon isn't present");
    }
}