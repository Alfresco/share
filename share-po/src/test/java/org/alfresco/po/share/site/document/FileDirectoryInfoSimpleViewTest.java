/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
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

import java.io.File;
import java.util.List;

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;

import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 *
 * @author Subashni Prasanna
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoSimpleViewTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private final Log logger = LogFactory.getLog(this.getClass());
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    private File file;
    private File testLockedFile;

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        createUser();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        file = siteUtil.prepareFile("alfresco123");
        testLockedFile = siteUtil.prepareFile("Alfresco456");
        createData();
    }
    @AfterClass(groups={"alfresco-one"})
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    /**
     * Create User
     * @throws Exception
     */
    public void createUser() throws Exception
    {
        loginAs(username, password);
        firstName = anotherUser.getfName();
        lastName = anotherUser.getlName();
        userName = firstName + " " + lastName;
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */

    public void createData() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testLockedFile.getCanonicalPath()).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectSimpleView()).render();
    }


    /**
     * Method renders the documentlibrary page and returns the file as FileDirectoryInfo
     * @return FileDirectoryInfo element for file / row at index 1
     * @throws Exception
     */
    private FileDirectoryInfo getFile() throws Exception
    {
        documentLibPage = resolvePage(driver).render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        if(results.isEmpty())
        {
            throw new Exception("Error getting file");
        }
        else
        {
            // Get file
            return results.get(1);
        }
    }

    @Test(groups={"alfresco-one"}, priority=1)
    public void test101SelectManageRules()
    {
     // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        FolderRulesPage page = thisRow.selectManageRules().render();
        Assert.assertNotNull(page);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectDocumentLibrary().render();
    }


    @Test(groups={"alfresco-one"}, priority=2)
    public void test102ContentCheckBoxForFolder() throws Exception
    {
        // Get folder row
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        String thisRowName = thisRow.getName();
        Assert.assertEquals(thisRowName, folderName);
        // Content CheckBox
        Assert.assertFalse(thisRow.isCheckboxSelected());
        thisRow.selectCheckbox();
        Assert.assertTrue(thisRow.isCheckboxSelected());

        // UnSelect
        thisRow.selectCheckbox();
        Assert.assertFalse(thisRow.isCheckboxSelected());
    }

    @Test(groups={"alfresco-one"}, priority=3)
    public void test103NodeRefForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // NodeRef
        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
        logger.info("NodeRef:" + thisRow.getContentNodeRef());
    }

    @Test(groups={"alfresco-one"}, priority=4)
    public void test104ContentEditInfoForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
    }

    @Test(expectedExceptions =UnsupportedOperationException.class, groups={"alfresco-one"}, priority=5)
    public void test105LikeMethodsForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Like
        thisRow.selectLike();
    }

    @Test(expectedExceptions =UnsupportedOperationException.class,groups={"alfresco-one"}, priority=6)
    public void test106FavouriteMethodsForFolder() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Favourite
        thisRow.selectFavourite();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups={"alfresco-one"}, priority=8)
    public void test108SelectDownloadForFolderWithExpection() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);

        if (thisRow.isFolder())
        {
            thisRow.selectDownload();
        }
    }

//    @Test
//    public void test109ContentCheckBoxForFile() throws Exception
//    {
//        try
//        {
//            // Get File
////            FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
//
//            // Content CheckBox
//// TODO Michael WD-26           Assert.assertTrue(thisRow.isCheckboxSelected());
//// TODO Michael WD-26          thisRow.selectCheckbox();
//// TODO Michael WD-26          Assert.assertFalse(thisRow.isCheckboxSelected());
//
//            //Select
//            thisRow.selectCheckbox();
////            Assert.assertTrue(thisRow.isCheckboxSelected());
//        }
//        catch (Throwable e)
//        {
//            saveScreenShot("ShareContentRowTest.testContentCheckBoxForFile");
//            throw new Exception(e);
//        }
//    }

    @Test(groups={"alfresco-one"}, priority=10)
    public void test110NodeRefForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // NodeRef
        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
        logger.info("NodeRef:" + thisRow.getContentNodeRef());

        Assert.assertFalse(thisRow.isVersionVisible());
        Assert.assertTrue(thisRow.isCheckBoxVisible());
        Assert.assertTrue(thisRow.getVersionInfo().equalsIgnoreCase("1.0"));
        // Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equalsIgnoreCase(file.getName()));
    }

    @Test(groups={"alfresco-one"}, priority=11)
    public void test111ContentEditInfoForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = getFile();

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
    }

    @Test(expectedExceptions =UnsupportedOperationException.class,groups={"alfresco-one"}, priority=12)
    public void test112LikeMethodsForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Like
        thisRow.selectLike();
    }

    @Test(expectedExceptions =UnsupportedOperationException.class,groups={"alfresco-one"}, priority=13)
    public void test113FavouriteMethodsForFile() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Favorite
        thisRow.selectFavourite();
    }

    //There are no tags in simple view
    //@Test(expectedExceptions =PageException.class,groups={"alfresco-one"})
    //public void test114TagsForFile() throws Exception
    //{
        //String tagName = "File Tag";
        //documentLibPage = documentLibPage.render();
        // Get File
        //FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        //Assert.assertFalse(thisRow.hasTags());

        //thisRow.addTag(tagName);
    //}

    @Test(groups={"alfresco-one"}, priority=15)
    public void test115SelectDownloadForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownload();
        Assert.assertNotNull(documentLibPage);
    }

    @Test(groups={"alfresco-one"}, priority=16)
    public void test116IsDeleteLinkPresent()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertTrue(thisRow.isDeletePresent());
    }



    @Test(groups={"alfresco-one"}, priority=17)
    public void test117SelectThumbnailForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        SitePage sitePage = thisRow.selectThumbnail().render();
        Assert.assertTrue(sitePage instanceof DocumentDetailsPage);
    }

    @Test(groups={"alfresco-one"}, priority=18)
    public void test118SelectThumbnailForFolder() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        try
        {
            Assert.assertNotNull(page);
            documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

            // Get File
            FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
            SitePage sitePage = thisRow.selectThumbnail().render();
            Assert.assertTrue(sitePage instanceof DocumentLibraryPage);
        }
        catch (Throwable e)
        {
            saveScreenShot("ShareContentRowTest.testSelectThumbnailForFile");
            throw new Exception(e);
        }
        finally
        {
           page.getSiteNav().selectDocumentLibrary();
        }
    }

    @Test(groups = {  "Enterprise4.2" , "Cloud2"}, priority=19)
    public void test119managePermissionTest()
    {
    	documentLibPage.render();
        ManagePermissionsPage mangPermPage = (documentLibPage.getFileDirectoryInfo(folderName).selectManagePermission()).render();
        Assert.assertTrue(mangPermPage.isInheritPermissionEnabled());
        documentLibPage = ((DocumentLibraryPage)mangPermPage.selectSave()).render();
    }

    @Test(expectedExceptions = PageOperationException.class, groups = { "Enterprise4.2" }, priority=21)
    public void test121SelectDownloadFolderAsZipForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownloadFolderAsZip();
    }

    @Test(groups = "Enterprise4.2", priority=23)
    public void test123SelectDownloadFolderAsZipForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        thisRow.selectDownloadFolderAsZip();
    }

    @Test(expectedExceptions =UnsupportedOperationException.class, groups={"alfresco-one"}, priority=24)
    public void testGetDescription() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Like
        thisRow.getDescription();
    }

    @Test(expectedExceptions =UnsupportedOperationException.class, groups={"alfresco-one"}, priority=25)
    public void testGetCategories() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        // Like
        thisRow.getCategories();
    }

    @Test(groups = {"Enterprise4.2" }, priority=28)
    public void test124SelectStartWorkFlow() throws Exception
    {
        FileDirectoryInfo thisRow = documentLibPage.render().getFileDirectoryInfo(file.getName());
        StartWorkFlowPage startWorkFlowPage = thisRow.selectStartWorkFlow().render();
        Assert.assertTrue(startWorkFlowPage.getTitle().contains("Start Workflow"));

        SiteFinderPage siteFinder = startWorkFlowPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectDocumentLibrary().render();
        Assert.assertNotNull(documentLibPage);
    }
//
//
//    @Test(groups = { "alfresco-one" }, expectedExceptions = UnsupportedOperationException.class, priority=33)
//    public void test128isCommentOptionPresent() throws Exception
//    {
//        documentLibPage = resolvePage(driver).render();
//        documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isCommentLinkPresent();
//    }
//
//    @Test(enabled = true, groups = "alfresco-one", priority = 34)
//    public void renameContentTest()
//    {
//        documentLibPage = resolvePage(driver).render();
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//        folderName = folderName + " updated";
//        thisRow.renameContent(folderName);
//        documentLibPage.render();
//        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
//    }
//
//    @Test(enabled = true, groups = "alfresco-one", priority = 35)
//    public void cancelRenameContentTest()
//    {
//        documentLibPage = resolvePage(driver).render();
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//        assertFalse(thisRow.isSaveLinkVisible());
//        assertFalse(thisRow.isCancelLinkVisible());
//        thisRow.contentNameEnableEdit();
//        assertTrue(thisRow.isSaveLinkVisible());
//        assertTrue(thisRow.isCancelLinkVisible());
//        thisRow.contentNameEnter(folderName + " not updated");
//        thisRow.contentNameClickCancel();
//        driver.navigate().refresh();
//        documentLibPage = resolvePage(driver).render();
//        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
//    }
//
//    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, priority = 36)
//    public void testFileOrFolderHeight() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        // Like
//        thisRow.getFileOrFolderHeight();
//    }
//
//    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, priority = 37)
//    public void testGetContentNameFromInfoMenu() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//
//        thisRow.getContentNameFromInfoMenu();
//    }
//
//    @Test(expectedExceptions =UnsupportedOperationException.class, groups={"alfresco-one"}, priority=38)
//    public void testClickOnCategoryName() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        // Like
//        thisRow.clickOnCategoryNameLink(Categories.LANGUAGES.getValue());
//    }
//
//    @Test(enabled = true, groups = "alfresco-one", priority = 39)
//    public void testClickTitle()
//    {
//        SitePage page = resolvePage(driver).render();
//        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
//        documentLibPage.getFileDirectoryInfo(file.getName()).clickOnTitle().render();
//    }
}
