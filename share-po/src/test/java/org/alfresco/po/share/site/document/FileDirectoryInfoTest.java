/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoTest extends AbstractDocumentTest
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private String uname = "ms1user" + System.currentTimeMillis();
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private File testLockedFile;
    private String doclibUrl;

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
        if(!alfrescoVersion.isCloud())
        {
            createEnterpriseUser(uname);
            loginAs(uname, UNAME_PASSWORD).render();
        }
        else
        {
            loginAs(username, password);
        }
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile("alfresco123");
        testLockedFile = SiteUtil.prepareFile("Alfresco456");     
        createData();
    }



    @AfterClass(groups={"alfresco-one"})
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
        
        if (isHybridEnabled())
        {
            // go to profile
            MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();

            // Click cloud sync
            CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
            if (cloudSyncPage.isDisconnectButtonDisplayed())
            {
                cloudSyncPage.disconnectCloudAccount().render();
            }
        }
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
   
    private void createData() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testLockedFile.getCanonicalPath()).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
        doclibUrl = drone.getCurrentUrl();
    }

    /**
     * Method renders the documentlibrary page and returns the file as FileDirectoryInfo
     * @return FileDirectoryInfo element for file / row at index 1
     * @throws Exception
     */
    private FileDirectoryInfo getFile() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
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
    
    @Test(groups = { "alfresco-one" }, priority = 1)
    public void test101SelectManageRules()
    {
     // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        FolderRulesPage page = thisRow.selectManageRules().render();
        Assert.assertNotNull(page);
        drone.navigateTo(doclibUrl);
        documentLibPage = (DocumentLibraryPage) drone.getCurrentPage();
        documentLibPage.render();
    }
    
    
    @Test(groups = { "alfresco-one" }, priority = 2)
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

    @Test(groups = { "alfresco-one" }, priority = 3)
    public void test103NodeRefForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // NodeRef
        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
        logger.info("NodeRef:" + thisRow.getContentNodeRef());
    }

    @Test(groups = { "alfresco-one" }, priority = 4)
    public void test104ContentEditInfoForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
    }

    @Test(groups = { "alfresco-one" }, priority = 5)
    public void test105LikeMethodsForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Like
        Assert.assertFalse(thisRow.isLiked());
        Assert.assertEquals(thisRow.getLikeCount(), "0");
        thisRow.selectLike();
    }

    @Test(groups = { "alfresco-one" }, priority = 6)
    public void test106FavouriteMethodsForFolder() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // Favourite
        Assert.assertFalse(thisRow.isFavourite());
        thisRow.selectFavourite();
        Assert.assertTrue(thisRow.isFavourite());
    }

    @Test(groups = { "alfresco-one" }, priority = 7)
    public void test107TagsForFolder() throws Exception
    {
        String tagName = "Folder Tag";
        String tagName2 = "Folder Tag 2";

        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        Assert.assertFalse(thisRow.hasTags());

        thisRow.addTag(tagName);
        thisRow.addTag(tagName2);

        Assert.assertTrue(thisRow.getTags().contains(tagName.toLowerCase()));
        Assert.assertTrue(thisRow.getTags().contains(tagName2.toLowerCase()));
        Assert.assertEquals(thisRow.getTags().size(), 2);
        Assert.assertFalse(thisRow.getTags().contains("No Tags"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, priority = 8)
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

    @Test(groups = { "alfresco-one" }, priority = 10)
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
    }

    @Test(groups = { "alfresco-one", "TestBug" }, priority = 11)
    public void test111ContentEditInfoForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = getFile();

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
    }

    @Test(groups = { "alfresco-one" }, priority = 12)
    public void test112LikeMethodsForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Like
        Assert.assertFalse(thisRow.isLiked());
        Assert.assertEquals(thisRow.getLikeCount(), "0");

        thisRow.selectLike();

        Assert.assertTrue(thisRow.isLiked());
        Assert.assertEquals(thisRow.getLikeCount(), "1");
    }

    @Test(groups = { "alfresco-one" }, priority = 13)
    public void test113FavouriteMethodsForFile() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Favorite
        Assert.assertFalse(thisRow.isFavourite());
        thisRow.selectFavourite();
        Assert.assertTrue(thisRow.isFavourite());
    }

    @Test(groups = { "alfresco-one" }, priority = 14)
    public void test114TagsForFile() throws Exception
    {
        String tagName = "File Tag";
        String tagName2 = "File Tag 2";
        documentLibPage = documentLibPage.render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertFalse(thisRow.hasTags());

        thisRow.addTag(tagName);
        thisRow.addTag(tagName2);

        Assert.assertTrue(thisRow.hasTags());
        Assert.assertTrue(thisRow.getTags().contains(tagName.toLowerCase()));
        Assert.assertTrue(thisRow.getTags().contains(tagName2.toLowerCase()));
        Assert.assertEquals(thisRow.getTags().size(), 2);
    }

    @Test(groups = { "alfresco-one" }, priority = 15)
    public void test115SelectDownloadForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownload();
        Assert.assertNotNull(documentLibPage);
    }

    @Test(groups = { "alfresco-one" }, priority = 16)
    public void test116IsDeleteLinkPresent()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        assertTrue(thisRow.isDeletePresent());
    }

    @Test(groups = { "alfresco-one" }, priority = 17)
    public void test117SelectThumbnailForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        SitePage sitePage = thisRow.selectThumbnail().render();
        Assert.assertTrue(sitePage instanceof DocumentDetailsPage);
    }

    @Test(groups = { "alfresco-one" }, priority = 18)
    public void test118SelectThumbnailForFolder() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        try
        {
            assertNotNull(page);
            documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

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
           documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        }
    }

    @Test(groups = { "Enterprise4.2", "Cloud2" }, priority = 19)
    public void test119managePermissionTest()
    {
    	documentLibPage.render();
    	Assert.assertTrue(documentLibPage.getFileDirectoryInfo(folderName).isManagePermissionLinkPresent());
        ManagePermissionsPage mangPermPage = (documentLibPage.getFileDirectoryInfo(folderName).selectManagePermission()).render();       
        Assert.assertTrue(mangPermPage.isInheritPermissionEnabled());
        documentLibPage = ((DocumentLibraryPage)mangPermPage.selectSave()).render();
    }

    
    @Test(expectedExceptions = PageOperationException.class, groups = { "Enterprise4.2" }, priority = 21)
    public void test121SelectDownloadFolderAsZipForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownloadFolderAsZip();
    }

    // @Test(expectedExceptions = AlfrescoVersionException.class, groups = {"Enterprise4.1","nonCloud"})
//    public void test122SelectDownloadFolderAsZipForInvalidAlfrescoVersion() throws Exception
//    {
//        // Get Folder as a zip only works for alfresco 4.2
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
//        thisRow.selectDownloadFolderAsZip();
//    }

    @Test(groups = "Enterprise4.2", priority = 23)
    public void test123SelectDownloadFolderAsZipForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        thisRow.selectDownloadFolderAsZip();
        drone.waitUntilElementDisappears(By.cssSelector("div[id*='archive-and-download'] a"), 2000);
    }

    @Test(groups = { "Enterprise4.2" }, priority = 24)
    public void test124SelectStartWorkFlow() throws Exception
    {
        // Select SyncToCloud
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        StartWorkFlowPage startWorkFlowPage = thisRow.selectStartWorkFlow().render();
        Assert.assertTrue(startWorkFlowPage.getTitle().contains("Start Workflow"));
        
        SiteFinderPage siteFinder = startWorkFlowPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        Assert.assertNotNull(documentLibPage);
    }
    
    
    @Test(groups = { "Hybrid" }, priority = 25)
    public void test125SelectSyncToCloud() throws Exception
    {
        // Select SyncToCloud
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        DestinationAndAssigneePage destinationAndAssigneePage = (DestinationAndAssigneePage) thisRow.selectSyncToCloud().render();
        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Sync " + file.getName() + " to The Cloud");
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue(thisRow.isCloudSynced(), "File should be synced");
    }


    @Test(groups = { "Hybrid" }, priority = 26)
    public void test126selectInlineEdit()
    {
        documentLibPage = drone.getCurrentPage().render();        
        InlineEditPage inlineEditPage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = (EditTextDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT);
        ContentDetails contentDetails = editTextDocumentPage.getDetails();
        Assert.assertEquals(contentDetails.getName(), testLockedFile.getName());
        documentLibPage = editTextDocumentPage.selectCancel().render();
        documentLibPage.render();
        /*inlineEditPage = documentLibPage.getFileDirectoryInfo(HTMLDocument).selectInlineEdit().render();
        EditHtmlDocumentPage editHtmlDocumentPage = (EditHtmlDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
        Assert.assertTrue(editHtmlDocumentPage.isEditHtmlDocumentPage());
        documentLibPage = ((DocumentLibraryPage)editHtmlDocumentPage.saveText()).render();*/
    }

    @Test(groups = { "Hybrid" }, priority = 27)
    public void test127isLockedTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked(), "Verify the file is not locked");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is displayed");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is displayed");
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork("premiernet.test");
        Assert.assertFalse(destinationAndAssigneePage.isFolderDisplayed(String.valueOf(Math.random())));
        Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed("Documents"));
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.selectLockOnPremCopy();
        DocumentLibraryPage documentLibraryPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked());
        Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).getContentInfo(), "This document is locked by you.");
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is NOT displayed");
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is NOT displayed");
    }
    
    @Test(groups = { "alfresco-one" }, priority = 28)
    public void test128isCommentOptionPresent()
    {
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isCommentLinkPresent(), "Verify the Comment option is displayed");
    }

    @Test(enabled = true, groups = "Enterprise4.2", priority = 29)
    public void renameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        folderName = folderName + " updated";
        thisRow.renameContent(folderName);
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
    }

    @Test(enabled = true, groups = "Enterprise4.2", priority = 30)
    public void cancelRenameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        assertFalse(thisRow.isSaveLinkVisible());
        assertFalse(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnableEdit();
        assertTrue(thisRow.isSaveLinkVisible());
        assertTrue(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnter(folderName + " not updated");
        thisRow.contentNameClickCancel();
        drone.refresh();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, priority = 32)
    public void testGetContentNameFromInfoMenu() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        thisRow.getContentNameFromInfoMenu();
    }
    
    @Test(enabled = true, groups = "Enterprise4.2", priority = 31)
    public void clickOnCategoryLink()
    {
        documentLibPage = drone.getCurrentPage().render();
        SelectAspectsPage selectAspectsPage = documentLibPage.getFileDirectoryInfo(folderName).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

         // Click on Apply changes on select aspects page
        selectAspectsPage.clickApplyChanges().render();
        
        EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
        // Add category and click ok
        CategoryPage categoryPage = editDocumentPropertiesPopup.getCategory().render();
        // Verify added category is displayed beneath categories part
        
        // Select add category
        categoryPage.addCategories(Arrays.asList(Categories.LANGUAGES.getValue()));

        // Click on save button in edit document properties pop up page
        categoryPage.clickOk().render();

        editDocumentPropertiesPopup.selectSave().render();;
        
        documentLibPage = documentLibPage.getFileDirectoryInfo(folderName).clickOnCategoryNameLink(Categories.LANGUAGES.getValue()).render();
        int i = 0;
        do
        {
            i++;
            drone.refresh();
            documentLibPage = drone.getCurrentPage().render();
        }while(!documentLibPage.isFileVisible(folderName)  && i < 5);
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
    }

    @Test(enabled = true, groups = "alfresco-one", priority = 32)
    public void testSelectViewInBrowser()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        String mainWinHandle = drone.getWindowHandle();
        thisRow.selectViewInBrowser();
        assertTrue(drone.getCurrentUrl().toLowerCase().contains(file.getName().toLowerCase()));
        drone.closeWindow();
        drone.switchToWindow(mainWinHandle);
    }

    @Test(enabled = true, groups = "alfresco-one", priority = 33)
    public void testIsFileShared()
    {
        documentLibPage = drone.getCurrentPage().render();
        ShareLinkPage shareLinkPage = documentLibPage.getFileDirectoryInfo(file.getName()).clickShareLink().render();
        Assert.assertNotNull(shareLinkPage);
        assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isFileShared());
    }
    @Test(enabled = true, groups = "alfresco-one", priority = 34)
    public void testClickTitle()
    {
        documentLibPage = drone.getCurrentPage().render();
        DocumentDetailsPage detailsPAge = documentLibPage.getFileDirectoryInfo(file.getName()).clickOnTitle().render();
        Assert.assertTrue(detailsPAge.isDocumentDetailsPage());
    }   
}