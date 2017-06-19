/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.test.FailedTestListener;
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
    private static String folderName, folderName1, folderName2, folderName3, folderNameDelete;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File file1;
    private File file2;
    private File file3;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
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
        folderName1 = folderName + "-1";
        folderName2 = folderName + "-2";
        folderName3 = folderName + "-3";
        folderNameDelete = folderName + "delete";
        folderDescription = String.format("Description of %s", folderName);
        createUser();
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
        file1 = siteUtil.prepareFile();
        file2 = siteUtil.prepareFile();
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUser() throws Exception
    {
        createEnterpriseUser(userName);
        loginAs(userName, UNAME_PASSWORD);
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, groups = "alfresco-one", priority = 1)
    public void getShareContentWithNull()
    {
        DocumentLibraryPage lib = factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        String t = null;
        lib.getFileDirectoryInfo(t);
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers
     * major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups = "alfresco-one", priority = 2)
    public void createNewFolder() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        documentLibPage = documentLibPage.clickOnMyFavourites().render();
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();

        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();

        files = documentLibPage.getFiles();
        FileDirectoryInfo folder = files.get(0);

        assertTrue(documentLibPage.paginatorRendered());
        assertEquals(files.size(), 1);
        assertEquals(folder.isTypeFolder(), true);
        assertEquals(folder.getName(), folderName);
        assertEquals(folder.getDescription(), folderDescription);

        assertNotNull(documentLibPage.getFileDirectoryInfo(folderName));
    }

    @Test(groups = "alfresco-one", priority = 3)
    public void uploadFile() throws Exception
    {
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        assertEquals(results.size(), 2);
        assertNotNull(documentLibPage.getFileDirectoryInfo(file1.getName()));
    }

    @Test(groups = "alfresco-one", priority = 4)
    public void testBrowseToEntry() throws Exception
    {
        FileDirectoryInfo folderEntry = documentLibPage.getFileDirectoryInfo(folderName);
        assertNotNull(folderEntry);

        documentLibPage = documentLibPage.selectFolder(folderName).render();
        Assert.assertEquals(documentLibPage.getNavigation().getFoldersInNavBar().get(1).getLink().getText(), folderName);

        String fileName = "newFile";

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle("newFile");
        contentDetails.setDescription("newFile");
        contentDetails.setContent("newFile");

        documentLibPage = siteActions.createContent(driver, contentDetails, ContentType.PLAINTEXT);

        documentLibPage = documentLibPage.selectFolder(folderName).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName).render();
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
    }

    @Test(groups = "alfresco-one", priority = 5)
    public void editProperites()
    {
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFileDirectoryInfo(file1.getName());
        assertEquals(fileInfo.getName(), file1.getName());
        assertTrue(fileInfo.isEditPropertiesLinkPresent());

        EditDocumentPropertiesPage editPage = fileInfo.selectEditProperties().render();
        assertNotNull(editPage);

        editPage.setDescription("the description");
        editPage.setName(NEW_TEST_FILENAME);
        documentLibPage = editPage.selectSave().render();
    }

    @Test(groups = "alfresco-one", priority = 6)
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

    @Test(groups = "alfresco-one", priority = 7)
    public void deleteFile()
    {
        documentLibPage = documentLibPage.deleteItem(NEW_TEST_FILENAME).render();
        assertEquals(documentLibPage.getFiles().size(), 1);
    }

    @Test(priority = 8, groups = "alfresco-one")
    public void navigateToFolder() throws IOException
    {
        SitePage page = resolvePage(driver).render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName1, folderDescription).render();
        documentLibPage = documentLibPage.selectFolder(folderName1).render();

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
    }

    @Test(groups = "alfresco-one", priority = 9)
    public void goToSubFolders() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        documentLibPage = documentLibPage.selectFolder(folderName1).render();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName2, folderDescription).render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName3, folderDescription).render();

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertEquals(files.size(), 2);

        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        documentLibPage = documentLibPage.selectFolder(folderName1).render();
        files = documentLibPage.getFiles();
        assertEquals(files.size(), 2);

        documentLibPage = documentLibPage.selectFolder(folderName2).render();
        files = documentLibPage.getFiles();
        assertEquals(files.size(), 0);
    }

    @Test(groups = "alfresco-one", priority = 10)
    public void deleteFileOrFolder()
    {
        SitePage page = resolvePage(driver).render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        int fileCount = files.size();

        documentLibPage = documentLibPage.deleteItem(folderName).render();

        files = documentLibPage.getFiles();
        assertEquals(files.size(), fileCount - 1);
    }

    @Test(groups = "alfresco-one", priority = 11)
    public void createNewFolderWithTitle() throws Exception
    {
        String title = "TestingFolderTitle";

        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder(folderName, title, folderDescription).render();

        FileDirectoryInfo folder = documentLibPage.getFileDirectoryInfo(folderName);

        assertTrue(folder.getTitle().contains(title));
    }

    @Test(groups = "alfresco-one", priority = 12)
    public void selectDetailedView() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        int noOfFiles = documentLibPage.getFiles().size();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();

        assertNotNull(documentLibPage);
        assertEquals(documentLibPage.getFiles().size(), noOfFiles);
    }

    @Test(groups = "alfresco-one", priority = 13)
    public void createFolderSelectCancel() throws Exception
    {
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.selectCancel().render();
    }

    // Disabling test as its not relevant to Share
    @Test(priority = 14, enabled = false)
    public void testBadCaseOfPaginationRendered()
    {
        DocumentLibraryPage lib = factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        assertFalse(lib.paginatorRendered());
    }

    /**
     * Test to check the uploaded file is created succesful
     *
     * @throws Exception
     * @author sprasanna
     */
    @Test(groups = "alfresco-one", priority = 15)
    public void isContentUploadedSucessfulTest() throws Exception
    {
        file3 = siteUtil.prepareFile();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file3.getCanonicalPath()).render();

        assertTrue(documentLibPage.isItemVisble(file3.getName()), "File is uploaded successfully");
    }

    @Test(groups = { "alfresco-one" }, priority = 16)
    public void testGetPreviewUrl()
    {
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();

        FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(file3.getName());

        String filePreviewUrl = fileDirectoryInfo.getPreViewUrl();

        assertTrue(filePreviewUrl.startsWith(shareUrl), "URL Found: " + filePreviewUrl + "Share URL: " + shareUrl);
    }

    @Test(groups = { "alfresco-one" }, priority = 17)
    public void testContentCreationButtons()
    {
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();

        assertTrue(documentLibPage.getNavigation().isFileUploadVisible());
        assertTrue(documentLibPage.getNavigation().isCreateContentVisible());
        assertTrue(documentLibPage.getNavigation().isFileUploadEnabled());
        assertTrue(documentLibPage.getNavigation().isCreateContentEnabled());

        documentLibPage = documentLibPage.getNavigation().selectCreateContentDropdown().render();

        assertTrue(documentLibPage.getNavigation().isNewFolderVisible());
    }

    @Test(groups = { "alfresco-one" }, priority = 18)
    public void testSelectItems()
    {
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();
        assertTrue(documentLibPage.getNavigation().isSelectVisible());
        documentLibPage = documentLibPage.getNavigation().selectAll().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemVisible());
        assertTrue(documentLibPage.getNavigation().isSelectedItemEnabled());
        documentLibPage = documentLibPage.getNavigation().selectNone().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemVisible());
        assertFalse(documentLibPage.getNavigation().isSelectedItemEnabled());
    }

    @Test(groups = { "alfresco-one" }, priority = 19)
    public void isCrumbTrailVisible()
    {
        assertTrue(documentLibPage.getNavigation().isCrumbTrailVisible());
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
        assertFalse(documentLibPage.getNavigation().isCrumbTrailVisible());
    }

    @Test(groups = { "alfresco-one" }, priority = 20)
    public void isOptionPresent()
    {
        // Check the Options menu
        DocumentLibraryNavigation nav = documentLibPage.getNavigation();
        
        assertTrue(nav.isOptionPresent(LibraryOption.SHOW_FOLDERS) ^ nav.isOptionPresent(LibraryOption.HIDE_FOLDERS));

        assertTrue(nav.isOptionPresent(LibraryOption.SHOW_BREADCRUMB) ^ nav.isOptionPresent(LibraryOption.HIDE_BREADCRUMB));

        assertTrue(nav.isOptionPresent(LibraryOption.RSS_FEED));
        assertTrue(nav.isOptionPresent(LibraryOption.FULL_WINDOW));
        assertTrue(nav.isOptionPresent(LibraryOption.FULL_SCREEN));
        assertTrue(nav.isOptionPresent(LibraryOption.SIMPLE_VIEW));
        assertTrue(nav.isOptionPresent(LibraryOption.DETAILED_VIEW));
        assertTrue(nav.isOptionPresent(LibraryOption.GALLERY_VIEW));
        assertTrue(nav.isOptionPresent(LibraryOption.TABLE_VIEW));
        assertTrue(nav.isOptionPresent(LibraryOption.AUDIO_VIEW));
        assertTrue(nav.isOptionPresent(LibraryOption.MEDIA_VIEW));
    }

    @Test(groups = "Enterprise4.2", priority = 21)
    public void isSelectedItemMenuVisible()
    {
        assertFalse(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }

    @Test(priority = 22, groups = "Enterprise4.2", expectedExceptions = PageException.class, expectedExceptionsMessageRegExp = "Selected Items Button found, but is not enabled please select one or more item")
    public void clickSelectedItemsWithException()
    {
        driver.navigate().refresh();// refresh to unselect rows
        documentLibPage = resolvePage(driver).render();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
    }

    @Test(priority = 23, groups = "Enterprise4.2")
    public void clickSelectedItems()
    {
        documentLibPage.getFileDirectoryInfo(folderName).selectCheckbox();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }

    @Test(priority = 24, groups = "Enterprise4.2")
    public void isSelectedItemMenuCorrectForDocument()
    {
        driver.navigate().refresh();// refresh to unselect rows
        documentLibPage = resolvePage(driver).render();
        
        FileDirectoryInfo fileInfo = documentLibPage.getFileDirectoryInfo(file3.getName());
        
        documentLibPage.getFileDirectoryInfo(fileInfo.getName()).selectCheckbox();
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuCorrectForDocument());
    }

    @Test(priority = 25, groups = "Enterprise4.2")
    public void isSelectedItemMenuCorrectForFolder()
    {
        driver.navigate().refresh();// refresh to unselect rows
        documentLibPage = resolvePage(driver).render();
        
        documentLibPage.getFileDirectoryInfo(folderName).selectCheckbox();
        
        assertTrue(documentLibPage.getNavigation().isSelectedItemMenuCorrectForFolder());
    }

    @Test(priority = 26, groups = "Enterprise4.2")
    public void selectDownloadAsZip()
    {
        if (documentLibPage.getNavigation().isSelectedItemMenuVisible())
        {
            documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        }
        documentLibPage.getNavigation().selectDownloadAsZip();
    }

    @Test(priority = 27, groups = "Enterprise4.2")
    public void selectAll() throws Exception
    {
        documentLibPage.render();
        
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
        documentLibPage.render();
        
        documentLibPage = documentLibPage.getNavigation().selectAll().render();
        
        List<FileDirectoryInfo> fileList = documentLibPage.getFiles();
        for (FileDirectoryInfo file : fileList)
        {
            assertTrue(file.isCheckboxSelected());
        }
    }

    @Test(priority = 28, groups = "Enterprise4.2")
    public void testIsCheckBoxPresent() throws Exception
    {
        documentLibPage = resolvePage(driver).render();
        assertTrue(documentLibPage.isCheckBoxPresent());
    }

    @Test(priority = 29, groups = "Enterprise4.2")
    public void deleteFolderFromNavigation() throws Exception
    {
        documentLibPage.render();
        
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderNameDelete, folderDescription).render();
        
        documentLibPage.getFileDirectoryInfo(folderNameDelete).selectCheckbox();
        
        ConfirmDeletePage deletePage = documentLibPage.getNavigation().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();
    }

    @Test(priority = 30, groups = "Enterprise4.2")
    public void selectCopyTo() throws Exception
    {
        String copyToFolder = "copyFolder";
        
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(copyToFolder, copyToFolder).render();
        
        documentLibPage.getFileDirectoryInfo(copyToFolder).selectCheckbox();
        
        CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().selectCopyTo().render();
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

    @Test(priority = 31, groups = "Enterprise4.2")
    public void selectMoveTo() throws Exception
    {
        String moveToFolder = "moveFolder";

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(moveToFolder, moveToFolder).render();
        
        documentLibPage.getFileDirectoryInfo(moveToFolder).selectCheckbox();
        
        CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().selectMoveTo().render();
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
    @Test(priority = 32, groups = "Enterprise4.2")
    public void selectUploadNewVersionCancel() throws Exception
    {
        File tempFile = siteUtil.prepareFile();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();

        FileDirectoryInfo file = documentLibPage.getFileDirectoryInfo(tempFile.getName());

        UpdateFilePage updateFilePage = file.selectUploadNewVersion().render();
        updateFilePage.selectMajorVersionChange();
        updateFilePage.selectCancel();

        documentLibPage = resolvePage(driver).render();
        assertTrue(documentLibPage.getTitle().contains("Document Library"));
    }

    @Test(priority = 33, groups = "Enterprise4.2")
    public void testTagsCount() throws IOException
    {
        String tagName = "tagcount";

        File tempFile = siteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();

        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(tempFile.getName()).render();

        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();

        TagPage tagPage = propertiesPage.getTag().render();
        tagPage = tagPage.enterTagValue(tagName).render();
        propertiesPage = tagPage.clickOkButton().render();
        detailsPage = propertiesPage.selectSave().render();

        documentLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        documentLibPage = documentLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();

        assertNotNull(documentLibPage);
        assertTrue(documentLibPage.getTagsCountUnderTagsTreeMenu(tagName) == 1);
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();
    }


}
