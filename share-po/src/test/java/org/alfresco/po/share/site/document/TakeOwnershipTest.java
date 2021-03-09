/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Take Ownership tests
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class TakeOwnershipTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(TakeOwnershipTest.class);

    public static long refreshDuration = 30000;
    private static String takeOwnershipSiteName = "TakeOwnershipSite" + System.currentTimeMillis();
    private String takeOwnershipUserName = "takeOwnershipUser" + System.currentTimeMillis() + "@test.com";
    private String takeOwnershipUserFirstName = takeOwnershipUserName;
    private String takeOwnershipUserLastName = takeOwnershipUserName;
    private String takeOwnershipFolder = "TakeOwnershipFolder" + System.currentTimeMillis();
    private String takeOwnershipFolderCancel = "TakeOwnershipFolderCancel" + System.currentTimeMillis();
    private String takeOwnershipFile = "TakeOwnershipFile" + System.currentTimeMillis();
    private String takeOwnershipFileCancel = "TakeOwnershipFileCancel" + System.currentTimeMillis();
    private String takeOwnershipFilePrepared = "";
    private String takeOwnershipFileCancelPrepared = "";
    private DocumentLibraryPage documentLibPage;
    private SiteMembersPage siteMembersPage;
    private FolderDetailsPage folderDetailsPage;
    private DashBoardPage dashBoard;
    private SiteFinderPage siteFinderPage;
    private SiteDashboardPage siteDashBoard;

    @BeforeClass
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====prepare====");

        dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();

        newPage.createEnterpriseUser(takeOwnershipUserName, takeOwnershipUserFirstName, takeOwnershipUserLastName, takeOwnershipUserName, takeOwnershipUserName);
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        userPage.searchFor(takeOwnershipUserName).render();
        Assert.assertTrue(userPage.hasResults());

        siteUtil.createSite(driver, username, password, takeOwnershipSiteName, "description", "Public");
        siteDashBoard = resolvePage(driver).render();
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, takeOwnershipUserName, UserRole.COLLABORATOR);
        logout(driver);

        // collaborator logs in and creates two folders and files
        dashBoard = loginAs(takeOwnershipUserName, takeOwnershipUserName);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, takeOwnershipSiteName);
        siteDashBoard = siteFinderPage.selectSite(takeOwnershipSiteName).render();

        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(takeOwnershipFolder, takeOwnershipFolder).render();

        File file = siteUtil.prepareFile(takeOwnershipFile, takeOwnershipFile, ".txt");
        takeOwnershipFilePrepared = file.getName();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(takeOwnershipFolderCancel, takeOwnershipFolderCancel).render();

        File fileCancel = siteUtil.prepareFile(takeOwnershipFileCancel, takeOwnershipFileCancel, ".txt");
        takeOwnershipFileCancelPrepared = fileCancel.getName();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(fileCancel.getCanonicalPath()).render();

        logout(driver);

        // collaborator becomes consumer
        dashBoard = loginAs(username, password);

        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        siteMembersPage = siteDashBoard.getSiteNav().selectMembers().render();

        List<String> siteMembersSearchUsers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount + 8; searchCount++)
        {
            try
            {
                siteMembersSearchUsers = siteMembersPage.searchUser(takeOwnershipUserName);
                siteMembersPage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (siteMembersSearchUsers != null && siteMembersSearchUsers.size() > 0  && (siteMembersSearchUsers.get(0).indexOf(takeOwnershipUserName) != -1))
            {
                break;
            }
        }
        Assert.assertTrue(siteMembersSearchUsers.size() > 0);

        siteMembersPage = siteMembersPage.assignRole(takeOwnershipUserName, UserRole.CONSUMER).render();

    }

    @AfterClass
    public void teardown()
    {
        siteUtil.deleteSite(username, password, takeOwnershipSiteName);
    }

    @Test
    public void testTakeOwnershipOfTheFolder() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====testTakeOwnershipOfTheFolder====");
        }

        logger.info("SITE **** " + takeOwnershipSiteName);
        logger.info("USER **** " + takeOwnershipUserName);

        // admin user takes ownership of the folder created by collaborator
        dashBoard = siteMembersPage.getNav().selectMyDashBoard().render();

        // userSitesPage = dashBoard.getNav().selectMySites().render();
        // siteDashBoard = userSitesPage.getSite(takeOwnershipSiteName).clickOnSiteName().render();

        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        List<FileDirectoryInfo> folders = documentLibPage.getFiles();
        FileDirectoryInfo folder = folders.get(0);

        folderDetailsPage = folder.selectViewFolderDetails().render();
        Assert.assertEquals(takeOwnershipFolder, folderDetailsPage.getContentTitle());

        TakeOwnershipPage takeOwnershipPage = folderDetailsPage.selectTakeOwnership().render();
        folderDetailsPage = takeOwnershipPage.clickOnTakeOwnershipButton().render();

        logout(driver);

        dashBoard = loginAs(takeOwnershipUserName, takeOwnershipUserName);

        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, takeOwnershipSiteName);
        siteDashBoard = siteFinderPage.selectSite(takeOwnershipSiteName).render();

        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();

        // consumer cannot create content anymore
        documentLibPage.selectFolder(takeOwnershipFolder);
        Assert.assertFalse(documentLibPage.getNavigation().isCreateContentEnabled());
        Assert.assertFalse(documentLibPage.getNavigation().isFileUploadEnabled());

        // folder cannot be deleted
        documentLibPage.getSiteNav().selectDocumentLibrary().render();
        documentLibPage.getFileDirectoryInfo(takeOwnershipFolder).selectCheckbox();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuVisible());
        Assert.assertFalse(documentLibPage.getNavigation().isDeleteActionForIncompleteWorkflowDocumentPresent());

        // consumer logs out
        logout(driver);

    }

    @Test(dependsOnMethods = "testTakeOwnershipOfTheFolder")
    public void testCancelTakeOwnershipOfTheFolder() throws Exception
    {
        // admin logs in
        dashBoard = loginAs(username, password);

        // admin user cancels taking ownership of the folder created by collaborator
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        List<FileDirectoryInfo> folders = documentLibPage.getFiles();
        FileDirectoryInfo folder = folders.get(1);

        folderDetailsPage = folder.selectViewFolderDetails().render();
        Assert.assertEquals(takeOwnershipFolderCancel, folderDetailsPage.getContentTitle());

        // admin takes ownership
        TakeOwnershipPage takeOwnershipPage = folderDetailsPage.selectTakeOwnership().render();
        folderDetailsPage = takeOwnershipPage.clickOnTakeOwnershipCancelButton().render();

        logout(driver);

        dashBoard = loginAs(takeOwnershipUserName, takeOwnershipUserName);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, takeOwnershipSiteName);
        siteDashBoard = siteFinderPage.selectSite(takeOwnershipSiteName).render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();

        // consumer can still create content
        documentLibPage.selectFolder(takeOwnershipFolderCancel);
        Assert.assertTrue(documentLibPage.getNavigation().isCreateContentEnabled());
        Assert.assertTrue(documentLibPage.getNavigation().isFileUploadEnabled());

        // folder can still be deleted
        documentLibPage.getSiteNav().selectDocumentLibrary().render();
        documentLibPage.getFileDirectoryInfo(takeOwnershipFolderCancel).selectCheckbox();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuVisible());
        Assert.assertTrue(documentLibPage.getNavigation().isDeleteActionForIncompleteWorkflowDocumentPresent());

        // consumer logs out
        logout(driver);

    }

    @Test(dependsOnMethods = "testTakeOwnershipOfTheFolder")
    public void testTakeOwnershipOfTheFile() throws Exception
    {
        // admin logs in
        dashBoard = loginAs(username, password);

        // admin user takes ownership of the file created by collaborator
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(takeOwnershipFilePrepared).render();
        TakeOwnershipPage takeOwnershipPage = documentDetailsPage.selectTakeOwnership().render();
        documentDetailsPage = takeOwnershipPage.clickOnTakeOwnershipButton().render();

        logout(driver);

        // consumer logs in
        dashBoard = loginAs(takeOwnershipUserName, takeOwnershipUserName);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, takeOwnershipSiteName);
        siteDashBoard = siteFinderPage.selectSite(takeOwnershipSiteName).render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();

        // consumer cannot edit file anymore
        documentDetailsPage = documentLibPage.selectFile(takeOwnershipFilePrepared).render();

        Assert.assertFalse(documentDetailsPage.isInlineEditLinkDisplayed());
        Assert.assertFalse(documentDetailsPage.isEditOfflineLinkDisplayed());

        // consumer cannot delete file anymore
        Assert.assertFalse(documentDetailsPage.isDeleteDocumentLinkDisplayed());

        logout(driver);

    }

    @Test(dependsOnMethods = "testTakeOwnershipOfTheFile")
    public void testCancelTakeOwnershipOfTheFile() throws Exception
    {
        // admin logs in
        dashBoard = loginAs(username, password);

        // admin user cancels taking ownership of the file created by collaborator
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(takeOwnershipFileCancelPrepared).render();
        TakeOwnershipPage takeOwnershipPage = documentDetailsPage.selectTakeOwnership().render();
        documentDetailsPage = takeOwnershipPage.clickOnTakeOwnershipCancelButton().render();

        logout(driver);

        // consumer logs in
        dashBoard = loginAs(takeOwnershipUserName, takeOwnershipUserName);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, takeOwnershipSiteName);
        siteDashBoard = siteFinderPage.selectSite(takeOwnershipSiteName).render();
        documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();

        // consumer can still edit file
        documentDetailsPage = documentLibPage.selectFile(takeOwnershipFileCancelPrepared).render();

        Assert.assertTrue(documentDetailsPage.isInlineEditLinkDisplayed());
        Assert.assertTrue(documentDetailsPage.isEditOfflineLinkDisplayed());

        // consumer can still delete file
        Assert.assertTrue(documentDetailsPage.isDeleteDocumentLinkDisplayed());

    }
}
