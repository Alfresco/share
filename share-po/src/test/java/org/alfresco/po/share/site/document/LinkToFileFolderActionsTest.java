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


import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.LiveSearchDropdown;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.workflow.*;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Tests to verify the Interactions with Links created on files / folders.
 *
 * @author adinap
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class LinkToFileFolderActionsTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LinkToFileFolderActionsTest.class);

    private String siteName1;
    private String siteName2;
    private File file1;
    private File file2;
    private File file3;
    private File file4;
    private File file5;

    private String folderName1;
    private String folderName2;
    private String file1LinkName;
    private String file2LinkName;
    private String file3LinkName;
    private String file4LinkName;
    private String file5LinkName;

    private String folder1LinkName;

    private DocumentLibraryPage docLib;
    private DashBoardPage dashBoard;

    private String deleteLinkAction;
    private String locateLinkAction;
    private String copyLinkAction;
    private String moveLinkAction;
    
    @Autowired
    SiteActions siteActions;

    @BeforeClass(groups = "alfresco-one", alwaysRun=true)
    public void prepare() throws Exception
    {
        try
        {
            deleteLinkAction = factoryPage.getValue("actions.link.delete");
            locateLinkAction = factoryPage.getValue("actions.link.locate");
            copyLinkAction = factoryPage.getValue("actions.link.copy");
            moveLinkAction = factoryPage.getValue("actions.link.move");
            
            dashBoard = loginAs(username, password);

            siteName1 = "site1-" + System.currentTimeMillis();
            siteName2 = "site2-" + System.currentTimeMillis();

            file1 = siteUtil.prepareFile("file1-" + System.currentTimeMillis());
            file2 = siteUtil.prepareFile("file2-" + System.currentTimeMillis());
            file3 = siteUtil.prepareFile("file3-" + System.currentTimeMillis());
            file4 = siteUtil.prepareFile("file4-" + System.currentTimeMillis());
            file5 = siteUtil.prepareFile("file5-" + System.currentTimeMillis());

            folderName1 = "folder1-" + System.currentTimeMillis();
            folderName2 = "folder2-" + System.currentTimeMillis();

            siteUtil.createSite(driver, username, password, siteName1, "description", "Public");
            siteUtil.createSite(driver, username, password, siteName2, "description", "Public");
            
            docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
            siteActions.uploadFile(driver, file1);
            siteActions.uploadFile(driver, file2);
            siteActions.uploadFile(driver, file3);
            siteActions.uploadFile(driver, file4);
            siteActions.uploadFile(driver, file5);
            
            file1LinkName = "Link to " + file1.getName();
            file2LinkName = "Link to " + file2.getName();
            file3LinkName = "Link to " + file3.getName();
            file4LinkName = "Link to " + file4.getName();
            file5LinkName = "Link to " + file5.getName();

            siteActions.createFolder(driver, folderName1, "folder 1 title", "folder 1 description");
            siteActions.createFolder(driver, folderName2, "folder 2 title", "folder 2 description");
            
            folder1LinkName = "Link to " + folderName1;

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file1.getName(), ACTION.CREATE_LINK);
            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName2, "", file1.getName(), ACTION.CREATE_LINK);           

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file2.getName(), ACTION.CREATE_LINK);

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file3.getName(), ACTION.CREATE_LINK);

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file4.getName(), ACTION.CREATE_LINK);

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file5.getName(), ACTION.CREATE_LINK);

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", folderName1, ACTION.CREATE_LINK);
            
            docLib = docLib.getNavigation().selectDetailedView().render();
        }
        catch (Exception pe)
        {
            saveScreenShot("CreateLinkFileUpload");
            logger.error("Cannot upload file to site ", pe);
        }
    }

    @AfterClass
    public void tearDown()
    {
        //docLib = docLib.getNavigation().selectDetailedView().render();
        siteUtil.deleteSite(username, password, siteName1);
        siteUtil.deleteSite(username, password, siteName2);
    }

    /**
     * Check that actions available on a link to a file are correct
     */
    @Test(priority = 1)
    public void testLinkActionsDocLib()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        List<String> linkActions = linkRow.getContentActions();
        Assert.assertTrue(linkActions.size() > 0);
        for (String action : linkActions)
        {
            Assert.assertTrue(action.equals(deleteLinkAction) || action.equals(locateLinkAction) ||
                action.equals(copyLinkAction) || action.equals(moveLinkAction));
        }
    }

    /**
     * Check that Locate Linked Item for a link to a file redirects to 
     * document library page where the original file is located
     */
    @Test(priority = 2)
    public void testLocateLinkedItemFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        docLib = linkRow.selectLocateLinkedItem().render();
        Assert.assertTrue(docLib.isFileVisible(file1.getName()));
    }

    /**
     * Check that Locate Linked Item for a link to a folder redirects to
     * document library page where the original folder is located
     */
    @Test(priority = 3, enabled = false)
    public void testLocateLinkedItemFolder()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(folder1LinkName);

        docLib = linkRow.selectLocateLinkedItem().render();
        Assert.assertTrue(docLib.isFileVisible(folderName1));

    }

    /**
     * Check that Delete Link action for a link deletes the link
     */
    @Test(priority = 4)
    public void testDeleteLink()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        Assert.assertTrue(docLib.isFileVisible(file2LinkName));

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file2LinkName);
        docLib = linkRow.deleteLink().render();

         while (docLib.isFileVisible(file2LinkName))
         {
             linkRow.deleteLink().render();
             logger.info("trying to delete link");
             try
             {
                 wait(5000);
             }
             catch (InterruptedException e)
             {
                 e.printStackTrace();
             }
         }

        Assert.assertFalse(docLib.isFileVisible(file2LinkName));

        dashBoard = docLib.getNav().selectMyDashBoard().render();
        MyProfilePage myProfile = dashBoard.getNav().selectMyProfile().render();
        TrashCanPage trashCan = myProfile.getProfileNav().selectTrashCan().render();
        
        Assert.assertFalse(trashCan.getTrashCanItems().contains(file2LinkName));
    }

    /**
     * Check that clicking on a link to a file redirects to document Details page
     */
    @Test(priority = 5)
    public void testClickLinkToFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        DocumentDetailsPage docDetailsPage = docLib.getFileDirectoryInfo(file1LinkName).clickOnTitle().render();

        Assert.assertTrue(docDetailsPage.getDocumentTitle().equalsIgnoreCase(file1.getName()));
    }

    /**
     * Check that clicking on a link to a folder redirects to folder contents page
     */
    @Test(priority = 6, enabled = false)
    public void testClickLinkToFolder()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1);
        
        RepositoryPage repoPage = siteActions.getFileDirectoryInfo(driver, folder1LinkName).selectThumbnail().render();
        
        String path = repoPage.getNavigation().getCrumbsElementDetailsLinkName();

        Assert.assertTrue(path.equalsIgnoreCase(folderName1));
    }

    /**
     * Check that a link to a file cannot be liked
     */
    @Test(priority = 7)
    public void testLinkLike()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isLikeVisible(), "Like is visible for link!");
    }

    /**
     * Check that a link to a file cannot be favoured
     */
    @Test(priority = 8)
    public void testLinkFavorite()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isFavoriteVisible(), "Favorite is visible for link!");
    }

    /**
     * Check that a link to a file cannot be commented on
     */
    @Test(priority = 9)
    public void testLinkComment()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isCommentLinkPresent(), "Comment is visible for link!");
    }

    /**
     * Check that a link to a file cannot be shared
     */
    @Test(priority = 10)
    public void testLinkShare()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isShareLinkVisible(), "Share is visible for link!");
    }

    /**
     * Check that link is deleted when the original file is deleted
     */
    @Test(priority = 11)
    public void testLinkDeletedWhenOriginalDeleted()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        Assert.assertTrue(docLib.isItemVisble(file3LinkName));

        FileDirectoryInfo fileRow = docLib.getFileDirectoryInfo(file3.getName());
        docLib = fileRow.delete().render();

        Assert.assertFalse(docLib.isItemVisble(file3LinkName));
    }

    /**
     * Check that links are not displayed in Live Search Results
     */
    @Test(priority = 12)
    public void testLinkNotDisplayedLiveSearch()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(file1LinkName).render();

        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
    }

    /**
     * Check that links are not displayed in Faceted Search Results
     */
    @Test(priority = 13)
    public void testLinkNotDisplayedFacetedSearch()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(file1LinkName).render();
        Assert.assertNotNull(resultPage);

        Assert.assertFalse(resultPage.hasResults());
    }

    /**
     * Check that a link can pe copied to another location
     */
    @Test(priority = 14)
    public void testClickCopyLink()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        siteActions.copyOrMoveArtifact(driver, DESTINATION.MY_FILES, "", "", file1LinkName, ACTION.COPY);

        MyFilesPage myFilesPage = dashBoard.getNav().selectMyFilesPage().render();

        Assert.assertTrue(myFilesPage.isItemVisble(file1LinkName));
    }

    /**
     * Check that a link can be moved to another location
     */
    @Test(priority = 15)
    public void testClickMoveLink()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName2).render();

        siteActions.copyOrMoveArtifact(driver, DESTINATION.SHARED_FILES, "", "", file1LinkName, ACTION.MOVE);

        Assert.assertFalse(docLib.isItemVisble(file1LinkName));

        SharedFilesPage sharedFilesPage = dashBoard.getNav().selectSharedFilesPage().render();

        Assert.assertTrue(sharedFilesPage.isItemVisble(file1LinkName));
    }

    /**
     * Check that actions available on a link to a file are correctly displayed in Gallery View
     */
    // getFileDirectoryInfo fails on Gallery View
    @Test(priority = 16, enabled = false)
    public void testLinkActionsGalleryView()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        docLib = docLib.getNavigation().selectGalleryView().render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        List<String> linkActions = linkRow.getContentActions();
        Assert.assertTrue(linkActions.size() > 0);
        for (String action : linkActions)
        {
            Assert.assertTrue(action.equals(deleteLinkAction) || action.equals(locateLinkAction) ||
                    action.equals(copyLinkAction) || action.equals(moveLinkAction));
        }

        docLib = docLib.getNavigation().selectDetailedView().render();
    }

    /**
     * Check that clicking on a link to a locked file redirects to original document Details page
     */
    @Test(priority = 17)
    public void testClickLinkToLockedFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // lock the file for offline editing
        docLib = siteActions.getFileDirectoryInfo(driver, file4.getName()).selectEditOffline().render();

        DocumentDetailsPage docDetailsPage = docLib.getFileDirectoryInfo(file4LinkName).clickOnTitle().render();
        Assert.assertTrue(docDetailsPage.getDocumentTitle().equalsIgnoreCase(file4.getName()));
    }

    /**
     * Check that clicking on a link to a file redirects to original document Details page after the file is moved
     */
    @Test(priority = 18, enabled = false)
    public void testClickLinkToMovedFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // move original document
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName2, "description", file5.getName(), ACTION.MOVE).render();

        docLib = siteActions.getSharePage(driver).render();
        DocumentDetailsPage docDetailsPage = docLib.getFileDirectoryInfo(file5LinkName).clickOnTitle().render();

        Assert.assertTrue(docDetailsPage.getDocumentTitle().equalsIgnoreCase(file5.getName()));
    }

    /**
     * Check that clicking on a link to a file redirects to original document Details page after the file is moved
     */
    @Test(priority = 19)
    public void testLinkNotDisplayedInStartWorkflow()
    {
        MyWorkFlowsPage myWorkFlowsPage = dashBoard.getNav().selectWorkFlowsIHaveStarted().render();

        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

        SelectContentPage selectContentPage = newWorkflowPage.clickAddItems().render();

        int size = selectContentPage.getAddedItems().size();

        // try to add the link
        selectContentPage.addItemFromSite(file1LinkName, siteName1);

        Assert.assertTrue(selectContentPage.getAddedItems().size() == size);
    }
    
}
