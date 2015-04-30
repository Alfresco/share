/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.ZoomStyle;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class DocumentLibraryPageTest extends AbstractDocumentTest
{
    private static final String NEW_TEST_FILENAME = "test.txt";

    private static String siteName;

    private static String folderName, folderName2, folderName3, folderNameDelete;

    private static String folderDescription;

    private static DocumentLibraryPage documentLibPage;

    private File file1;

    private File file2;
    
    private File file3; 

    private File tempFile;

    private String userName = "user" + System.currentTimeMillis() + "@test.com";

    private String firstName = userName;

    private String lastName = userName;

    @SuppressWarnings("unused")
    private String uname = "dlpt1user" + System.currentTimeMillis();

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderName2 = folderName + "-1";
        folderName3 = folderName + "-2";
        folderNameDelete = folderName + "delete";
        folderDescription = String.format("Description of %s", folderName);
        createUser();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file1 = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile();
        tempFile = SiteUtil.prepareFile();
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUser() throws Exception
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
            assertTrue(userCreated.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, groups = "alfresco-one")
    public void getShareContentWithNull()
    {
        DocumentLibraryPage lib = new DocumentLibraryPage(drone);
        String t = null;
        lib.getFileDirectoryInfo(t);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one")
    public void getShareContentWithEmptyName()
    {
        DocumentLibraryPage lib = new DocumentLibraryPage(drone);
        lib.getFileDirectoryInfo("");
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers
     * major and minor version changes
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "getShareContentWithEmptyName", groups = "alfresco-one")
    public void createNewFolder() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        documentLibPage = documentLibPage.clickOnMyFavourites().render();

        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        // documentLibPage = getDocumentLibraryPage(siteName).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        documentLibPage = (documentLibPage.getNavigation().selectDetailedView()).render();

        files = documentLibPage.getFiles();
        FileDirectoryInfo folder = files.get(0);

        assertTrue(documentLibPage.paginatorRendered());
        assertEquals(files.size(), 1);
        assertEquals(folder.isTypeFolder(), true);
        assertEquals(folder.getName(), folderName);
        assertEquals(folder.getDescription(), folderDescription);
        assertNotNull(documentLibPage.getFileDirectoryInfo(folderName));
    }

    @Test(dependsOnMethods = "createNewFolder", groups = "alfresco-one")
    public void uploadFile() throws Exception
    {
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        assertEquals(results.size(), 2);
        assertNotNull(documentLibPage.getFileDirectoryInfo(file1.getName()));
    }

    @Test(dependsOnMethods = "uploadFile", groups = "alfresco-one")
    public void testBrowseToEntry() throws Exception
    {
        documentLibPage = documentLibPage.browseToEntry(folderName).render();
        Assert.assertEquals(documentLibPage.getNavigation().getFoldersInNavBar().get(1).getLink().getText(), folderName);

        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        DocumentDetailsPage detailsPage = documentLibPage.browseToEntry(tempFile.getName()).render();
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());

        documentLibPage = detailsPage.delete().render();
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();


    }

    @Test(dependsOnMethods = "uploadFile", groups="alfresco-one")
    public void editProperites()
    {
        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(1);
        assertEquals(fileInfo.getName(), file1.getName());
        assertTrue(fileInfo.isEditPropertiesLinkPresent());
        EditDocumentPropertiesPage editPage = fileInfo.selectEditProperties().render();
        assertNotNull(editPage);
        editPage.setDescription("the description");
        editPage.setName(NEW_TEST_FILENAME);
        documentLibPage = editPage.selectSave().render();
    }

    @Test(dependsOnMethods = "editProperites", groups = "alfresco-one")
    public void cancelEditProperties()
    {
        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(1);
        EditDocumentPropertiesPage editPage = fileInfo.selectEditProperties().render();
        editPage.setDocumentTitle("hello world");
        editPage.setName("helloworld");
        documentLibPage = editPage.selectCancel().render();
        fileInfo = documentLibPage.getFiles().get(1);
        assertEquals(fileInfo.getName(), NEW_TEST_FILENAME);
    }

    @Test(dependsOnMethods = "cancelEditProperties", groups = "alfresco-one")
    public void deleteFile()
    {
        documentLibPage = documentLibPage.deleteItem(NEW_TEST_FILENAME).render();
        assertEquals(documentLibPage.getFiles().size(), 1);
    }

    @Test(dependsOnMethods = "deleteFile", groups = "alfresco-one")
    public void navigateToFolder() throws IOException
    {
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
    }

    @Test(dependsOnMethods = "navigateToFolder", groups = "alfresco-one")
    public void goToSubFolders() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName2, folderDescription).render();
        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName3, folderDescription).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 2);

        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        files = documentLibPage.getFiles();
        if (files.isEmpty())
            saveScreenShot("DocumentLibraryPageTest.goToSubFolders.empty");
        assertEquals(files.size(), 2);
        documentLibPage = documentLibPage.selectFolder(folderName2).render();
        files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
    }

    @Test(dependsOnMethods = "goToSubFolders", groups = "alfresco-one")
    public void deleteFileOrFolder()
    {
        SitePage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = (DocumentLibraryPage) page.getSiteNav().selectSiteDocumentLibrary();
        documentLibPage.render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 1);
        documentLibPage = (DocumentLibraryPage) documentLibPage.deleteItem(folderName);
        documentLibPage.render();
        files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
    }

    @Test(dependsOnMethods = "deleteFileOrFolder", groups = "alfresco-one")
    public void createNewFolderWithTitle() throws Exception
    {
        String title = "TestingFolderTitle";
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder(folderName, title, folderDescription).render();
        files = documentLibPage.getFiles();
        FileDirectoryInfo folder = files.get(0);

        assertEquals(files.size(), 1);
        assertTrue(folder.getTitle().length() > 0);
        assertTrue(folder.getTitle().contains(title));
    }

    @Test(dependsOnMethods = "createNewFolderWithTitle", groups = "alfresco-one")
    public void selectDetailedView() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        int noOfFiles = documentLibPage.getFiles().size();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectDetailedView()).render();
        assertNotNull(documentLibPage);
        assertEquals(documentLibPage.getFiles().size(), noOfFiles);
    }

    @Test(dependsOnMethods = "selectDetailedView", groups = "alfresco-one")
    public void createFolderSelectCancel() throws Exception
    {
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.selectCancel().render();
    }

    @Test
    public void testBadCaseOfPaginationRendered()
    {
        DocumentLibraryPage l = new DocumentLibraryPage(drone);
        assertFalse(l.paginatorRendered());
    }

    /**
     * Test to check the uploaded file is created succesful
     *
     * @param - String
     * @throws Exception
     * @author sprasanna
     */
    @Test(dependsOnMethods = "createFolderSelectCancel", groups = "alfresco-one")
    public void isContentUploadedSucessfulTest() throws Exception
    {
        file3 = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file3.getCanonicalPath()).render();
        assertTrue(documentLibPage.isContentUploadedSucessful(file3.getName()), "File is uploaded successfully");
    }

    @Test(dependsOnMethods = "isContentUploadedSucessfulTest", groups = { "alfresco-one" }, expectedExceptions = IllegalArgumentException.class)
    public void selectZoomIllegalArgumentException()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        documentLibPage = ((DocumentLibraryPage) navigation.selectGalleryView()).render();

        navigation.selectZoom(null);
    }

    @Test(dependsOnMethods = "selectZoomIllegalArgumentException", groups = { "alfresco-one" })
    public void testZoom()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        documentLibPage = ((DocumentLibraryPage) navigation.selectGalleryView()).render();

        assertTrue(navigation.isZoomControlVisible());

        navigation.selectZoom(ZoomStyle.SMALLER);

        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file3.getName());
        double fileHeightSize = thisRow.getFileOrFolderHeight();

        navigation.selectZoom(ZoomStyle.SMALLEST);
        ZoomStyle actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.SMALLEST);

        double actualFileHeight = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(fileHeightSize > actualFileHeight);

        navigation.selectZoom(ZoomStyle.BIGGER);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.BIGGER);

        fileHeightSize = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(fileHeightSize > actualFileHeight);

        navigation.selectZoom(ZoomStyle.BIGGEST);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.BIGGEST);

        actualFileHeight = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(fileHeightSize < actualFileHeight);

        navigation.selectZoom(ZoomStyle.BIGGER);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.BIGGER);

        fileHeightSize = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(fileHeightSize < actualFileHeight);

        navigation.selectZoom(ZoomStyle.SMALLEST);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.SMALLEST);

        actualFileHeight = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(actualFileHeight < fileHeightSize);

        navigation.selectZoom(ZoomStyle.SMALLER);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.SMALLER);

        fileHeightSize = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(actualFileHeight < fileHeightSize);

        navigation.selectZoom(ZoomStyle.BIGGEST);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.BIGGEST);

        actualFileHeight = documentLibPage.getFileDirectoryInfo(file3.getName()).getFileOrFolderHeight();
        assertTrue(actualFileHeight > fileHeightSize);

        documentLibPage = ((DocumentLibraryPage) navigation.selectDetailedView()).render();
    }

    @Test(dependsOnMethods = "testZoom", groups = { "alfresco-one" }, expectedExceptions = UnsupportedOperationException.class)
    public void selectZoomUnsupportedOperationException()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        documentLibPage = ((DocumentLibraryPage) navigation.selectDetailedView()).render();

        navigation.selectZoom(ZoomStyle.BIGGEST);
    }

    @Test(dependsOnMethods = "selectZoomUnsupportedOperationException", groups = { "alfresco-one" })
    public void isZoomControlVisibleFalse()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        documentLibPage = ((DocumentLibraryPage) navigation.selectDetailedView()).render();
        assertFalse(navigation.isZoomControlVisible());
    }

    @Test(dependsOnMethods = "selectZoomUnsupportedOperationException", groups = { "alfresco-one" })
    public void testClickFolderUp()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        String folderUpTest = "folderUpTest";
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderUpTest, folderUpTest).render();
        documentLibPage = documentLibPage.selectFolder(folderUpTest).render();
        assertTrue(documentLibPage.getFiles().size() == 0);
        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        assertTrue(documentLibPage.getNavigation().isFolderUpVisible());
        documentLibPage = navigation.clickFolderUp().render();
        assertTrue(documentLibPage.isDocumentLibrary());
    }

    @Test(dependsOnMethods = "testClickFolderUp", groups = { "alfresco-one" })
    public void testGetPreviewUrl()
    {
        String urlRegexp = "^http(s?)://((\\w+\\.)?\\w+\\.\\w+|((2[0-5]{2}|1[0-9]{2}|[0-9]{1,2})\\.){3}(2[0-5]{2}|1[0-9]{2}|[0-9]{1,2})):[0-9]{0,4}(/).+";
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(file3.getName());
        assertTrue(fileDirectoryInfo.getPreViewUrl().matches(urlRegexp));
    }

    @Test(dependsOnMethods = "testGetPreviewUrl", groups = { "alfresco-one" })
    public void testPaginationForm()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        PaginationForm paginationForm = documentLibPage.getBottomPaginationForm();
        assertTrue(paginationForm.isDisplay());
        assertFalse(paginationForm.isPreviousButtonEnable());
        assertFalse(paginationForm.isNextButtonEnable());
        assertEquals(paginationForm.getCurrentPageNumber(), 1);
        assertEquals(paginationForm.getPaginationLinks().size(), 1);
        assertEquals(paginationForm.getPaginationInfo(), "1 - 3 of 3");
    }

    @Test(dependsOnMethods = "testPaginationForm", groups = { "alfresco-one" })
    public void testContentCreationButtons()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        assertTrue(documentLibPage.getNavigation().isFileUploadVisible());
        assertTrue(documentLibPage.getNavigation().isCreateContentVisible());
        assertTrue(documentLibPage.getNavigation().isFileUploadEnabled());
        assertTrue(documentLibPage.getNavigation().isCreateContentEnabled());

        documentLibPage = documentLibPage.getNavigation().selectCreateContentDropdown().render();
        assertTrue(documentLibPage.getNavigation().isNewFolderVisible());
    }

    @Test(dependsOnMethods = "testContentCreationButtons", groups = { "alfresco-one" })
    public void testSelectItems()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        assertTrue(documentLibPage.getNavigation().isSelectVisible());

        documentLibPage = documentLibPage.getNavigation().selectAll().render();

        assertTrue(documentLibPage.getNavigation().isSelectedItemVisible());
        assertTrue(documentLibPage.getNavigation().isSelectedItemEnabled());

        documentLibPage = documentLibPage.getNavigation().selectNone().render();

        assertTrue(documentLibPage.getNavigation().isSelectedItemVisible());
        assertFalse(documentLibPage.getNavigation().isSelectedItemEnabled());
    }

    @Test(dependsOnMethods = "testSelectItems", groups = { "alfresco-one" })
    public void isCrumbTrailVisible()
    {
        assertTrue(documentLibPage.getNavigation().isCrumbTrailVisible());

        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();

        assertFalse(documentLibPage.getNavigation().isCrumbTrailVisible());
    }

    @Test(dependsOnMethods = "isCrumbTrailVisible", groups = { "alfresco-one" })
    public void isOptionPresent()
    {
        // Check the Options menu
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.SHOW_FOLDERS)
                    ^ documentLibPage.getNavigation().isOptionPresent(LibraryOption.HIDE_FOLDERS));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.SHOW_BREADCRUMB)
                    ^ documentLibPage.getNavigation().isOptionPresent(LibraryOption.HIDE_BREADCRUMB));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.RSS_FEED));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.FULL_WINDOW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.FULL_SCREEN));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.SIMPLE_VIEW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.DETAILED_VIEW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.GALLERY_VIEW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.TABLE_VIEW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.AUDIO_VIEW));
        assertTrue(documentLibPage.getNavigation().isOptionPresent(LibraryOption.MEDIA_VIEW));
    }

    @Test(dependsOnMethods = "isCrumbTrailVisible", groups = "Enterprise4.2")
    public void isSelectedItemMenuVisible()
    {
        assertFalse(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }

    @Test(dependsOnMethods = "isSelectedItemMenuVisible", groups = "Enterprise4.2", expectedExceptions = PageException.class, expectedExceptionsMessageRegExp = "Selected Items Button found, but is not enabled please select one or more item")
    public void clickSelectedItemsWithException()
    {
        drone.refresh();// refresh to unselect rows
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
    }

    @Test(dependsOnMethods = "clickSelectedItemsWithException", groups = "Enterprise4.2")
    public void clickSelectedItems()
    {
        documentLibPage.getFileDirectoryInfo(folderName).selectCheckbox();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }

    @Test(dependsOnMethods = "clickSelectedItems", groups = "Enterprise4.2")
    public void isSelectedItemMenuCorrectForDocument()
    {
        drone.refresh();//refresh to unselect rows
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo fileInfo = documentLibPage.getFileDirectoryInfo(file3.getName());
        documentLibPage.getFileDirectoryInfo(fileInfo.getName()).selectCheckbox();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuCorrectForDocument());
    }

    @Test(dependsOnMethods = "isSelectedItemMenuCorrectForDocument", groups = "Enterprise4.2")
    public void isSelectedItemMenuCorrectForFolder()
    {
        drone.refresh();//refresh to unselect rows
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage.getFileDirectoryInfo(folderName).selectCheckbox();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuCorrectForFolder());
    }

    @Test(dependsOnMethods = "isSelectedItemMenuCorrectForFolder", groups = "Enterprise4.2")
    public void selectDownloadAsZip()
    {
        if (documentLibPage.getNavigation().isSelectedItemMenuVisible())
        {
            documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        }
        documentLibPage.getNavigation().selectDownloadAsZip();
    }

    @Test(dependsOnMethods = "selectDownloadAsZip", groups = "Enterprise4.2")
    public void selectAll() throws Exception
    {
        documentLibPage.render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().render().selectFileUpload().render();
        documentLibPage = (DocumentLibraryPage) uploadForm.uploadFile(file2.getCanonicalPath());
        documentLibPage.render();
        documentLibPage = documentLibPage.getNavigation().selectAll().render();
        List<FileDirectoryInfo> fileList = documentLibPage.getFiles();
        for (FileDirectoryInfo file : fileList)
        {
            assertTrue(file.isCheckboxSelected());
        }
    }

    @Test(dependsOnMethods = "selectAll", groups = "Enterprise4.2")
    public void testIsCheckBoxPresent() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
        assertTrue(documentLibPage.isCheckBoxPresent());
    }

    @Test(dependsOnMethods = "testIsCheckBoxPresent", groups = "Enterprise4.2")
    public void deleteFolderFromNavigation() throws Exception
    {
        documentLibPage.render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderNameDelete, folderDescription).render();
        documentLibPage.getFileDirectoryInfo(folderNameDelete).selectCheckbox();
        ConfirmDeletePage deletePage = documentLibPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();
    }

    @Test(dependsOnMethods = "deleteFolderFromNavigation", groups = "Enterprise4.2")
    public void selectCopyTo() throws Exception
    {
        String copyToFolder = "copyFolder";
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(copyToFolder, copyToFolder).render();
        documentLibPage.getFileDirectoryInfo(copyToFolder).selectCheckbox();
        CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().render().selectCopyTo().render();
        documentLibPage = copyTo.selectOkButton().render();

        for (FileDirectoryInfo dirInfo : documentLibPage.getFiles())
        {
            boolean isCopied = dirInfo.getName().contains("Copy of") ? true : false;
            if (isCopied)
            {
                assertTrue(dirInfo.getName().contains(copyToFolder));
            }
        }

    }

    @Test(dependsOnMethods = "selectCopyTo", groups = "Enterprise4.2")
    public void selectMoveTo() throws Exception
    {
        String moveToFolder = "moveFolder";
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(moveToFolder, moveToFolder).render();
        documentLibPage.getFileDirectoryInfo(moveToFolder).selectCheckbox();
        CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().render().selectMoveTo().render();
        documentLibPage = copyTo.selectOkButton().render();

        for (FileDirectoryInfo dirInfo : documentLibPage.getFiles())
        {
            boolean isMoved = dirInfo.getName().contains("Move of") ? true : false;
            if (isMoved)
            {
                assertTrue(dirInfo.getName().contains(moveToFolder));
            }
        }

    }

    /**
     * test to upload new version
     *
     * @throws Exception
     * @author sprasanna
     */
    @Test(dependsOnMethods = "selectMoveTo", groups = "Enterprise4.2")
    public void selectUploadNewVersion() throws Exception
    {
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        UpdateFilePage updateFilePage = documentLibPage.getFileDirectoryInfo(tempFile.getName())
                    .selectUploadNewVersion().render();
        updateFilePage.selectMajorVersionChange();
        updateFilePage.selectCancel();
        documentLibPage = drone.getCurrentPage().render();
        assertTrue(documentLibPage.getTitle().contains("Document Library"));
    }

    @Test(dependsOnMethods = "selectUploadNewVersion", groups = "Enterprise4.2")
    public void testTagsCount() throws IOException
    {
        String tagName = "tagcount";
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(tempFile.getName()).render();
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        TagPage tagPage = propertiesPage.getTag().render();
        tagPage = tagPage.enterTagValue(tagName).render();
        propertiesPage = tagPage.clickOkButton().render();
        detailsPage = propertiesPage.selectSave().render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();

        assertNotNull(documentLibPage);
        assertTrue(documentLibPage.getTagsCountUnderTagsTreeMenu(tagName) == 1);
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    @Test(dependsOnMethods = "testTagsCount", groups = "Enterprise4.2")
    public void testDocumentsTree() throws IOException
    {
        assertTrue(documentLibPage.isDocumentsTreeExpanded());
        documentLibPage.clickDocumentsTreeExpanded();
        assertFalse(documentLibPage.isDocumentsTreeExpanded());
        documentLibPage.clickDocumentsTreeExpanded();
    }

    @Test(dependsOnMethods = "testDocumentsTree", groups = "Enterprise4.2")
    public void selectStartWorkFlow() throws Exception
    {
        documentLibPage.render();
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();

        if (documentLibPage.getFileDirectoryInfo("copyFolder").isCheckboxSelected())
        {
            documentLibPage.getFileDirectoryInfo("copyFolder").selectCheckbox();
        }

        if (!documentLibPage.getFileDirectoryInfo(tempFile.getName()).isCheckboxSelected())
        {
            documentLibPage.getFileDirectoryInfo(tempFile.getName()).selectCheckbox();
        }

        StartWorkFlowPage workFlowPage = documentLibPage.getNavigation().render().selectStartWorkFlow().render();

        assertNotNull(workFlowPage);
        assertTrue(workFlowPage.getTitle().contains("Start Workflow"));
    }

    @Test(dependsOnMethods = "selectStartWorkFlow", groups = "Enterprise4.2")
    public void testFilterLinks() throws Exception
    {
        SiteFinderPage siteFinder = ((SharePage) drone.getCurrentPage()).getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(tempFile.getName());

        assertFalse(thisRow.isFavourite());
        thisRow.selectFavourite();

        siteFinder = documentLibPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();

        documentLibPage = documentLibPage.clickOnMyFavourites().render();
        int i = 0;
        do
        {
            i++;
            drone.refresh();
            documentLibPage = drone.getCurrentPage().render();
        } while (!documentLibPage.isFileVisible(tempFile.getName()) && i < 5);
        assertTrue(documentLibPage.getFiles().size() == 1);
        assertTrue(documentLibPage.getFiles().get(0).getName().equalsIgnoreCase(tempFile.getName()));

        documentLibPage.clickOnRecentlyAdded();
        assertEquals(documentLibPage.getFiles().size(), 4);
        documentLibPage.clickOnMyFavourites();
        assertEquals(documentLibPage.getFiles().size(), 1);
        documentLibPage.clickOnRecentlyModified();
        assertEquals(documentLibPage.getFiles().size(), 4);
        documentLibPage.clickOnMyFavourites();
        assertEquals(documentLibPage.getFiles().size(), 1);
        documentLibPage.clickOnAllDocuments();
        assertEquals(documentLibPage.getFiles().size(), 4);
    }

    @Test(dependsOnMethods = "testFilterLinks", groups = "Enterprise4.2", alwaysRun = true)
    public void clickCrumbsElementDetailsLinkName() throws Exception
    {
        String folder = "newFolder";
        SiteFinderPage siteFinder = ((SharePage) drone.getCurrentPage()).getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
        documentLibPage = documentLibPage.selectFolder("copyFolder").render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folder, folder).render();

        FolderDetailsPage detailsPage = documentLibPage.getNavigation().clickCrumbsElementDetailsLinkName().render();
        assertNotNull(detailsPage);
    }

    @Test(dependsOnMethods = "clickCrumbsElementDetailsLinkName", groups = "Enterprise4.2")
    public void getCrumbsElementDetailsLinkName() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder("copyFolder").render();
        String linkName = documentLibPage.getNavigation().getCrumbsElementDetailsLinkName();

        assertEquals(linkName, "copyFolder");
    }

    @Test(dependsOnMethods = "getCrumbsElementDetailsLinkName", groups = "Enterprise4.2")
    public void clickCrumbsParentLinkName() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder("copyFolder").render();
        documentLibPage = documentLibPage.getNavigation().clickCrumbsParentLinkName().render();

        assertNotNull(documentLibPage);
    }

    @Test(dependsOnMethods = "clickCrumbsParentLinkName", groups = "Enterprise4.2")
    public void testIsCreateFromTemplatePresent () throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.getNavigation().selectCreateContentDropdown().render();
        assertTrue(documentLibPage.getNavigation().isCreateFromTemplatePresent(true));
        assertTrue(documentLibPage.getNavigation().isCreateFromTemplatePresent(false));

    }

    @Test(dependsOnMethods = "testIsCreateFromTemplatePresent", groups = "Enterprise4.2")
    public void testIsCreateNewFolderPresent() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.getNavigation().selectCreateContentDropdown().render();
        assertTrue(documentLibPage.getNavigation().isCreateNewFolderPresent());

    }

    @Test(dependsOnMethods = "testIsCreateNewFolderPresent", groups = "Enterprise4.2")
    public void testIsCreateContentPresent() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.getNavigation().selectCreateContentDropdown().render();
        assertTrue(documentLibPage.getNavigation().isCreateContentPresent(ContentType.PLAINTEXT));

    }

    @Test(dependsOnMethods = "testIsCreateContentPresent", groups = "Enterprise4.2")
    public void testIsSelectedItemsOptionPresent() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.getFileDirectoryInfo("copyFolder").selectCheckbox();
        assertTrue(documentLibPage.getNavigation().isSelectedItemsOptionPresent(SelectedItemsOptions.DELETE));

    }
    
    @Test(groups = "alfresco-one")
    public void testSelectDocumentLibrary() throws Exception
    {
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        assertNotNull(docPage.selectDocumentLibrary(drone).render());        
    }

}
