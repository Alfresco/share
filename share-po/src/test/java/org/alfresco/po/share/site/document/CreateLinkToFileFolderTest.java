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

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.ActivityType;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.search.*;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests to verify the Create Link functionality.
 *
 * @author adinap
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class CreateLinkToFileFolderTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(CreateLinkToFileFolderTest.class);

    private String siteName1;
    private String siteName2;
    private File file1;
    private File file2;
    private File file3;

    private String folderName1;

    private String file1linkName;
    private String folder1linkName;

    private DocumentLibraryPage docLib;

    @Autowired
    SiteActions siteActions;

    @BeforeClass
    public void prepare() throws Exception
    {
        try
        {
            loginAs(username, password);

            siteName1 = "site1-" + System.currentTimeMillis();
            siteName2 = "site2-" + System.currentTimeMillis();
            file1 = siteUtil.prepareFile("myfile1-" + System.currentTimeMillis());
            file2 = siteUtil.prepareFile("myfile2-" + System.currentTimeMillis());
            file3 = siteUtil.prepareFile("myfile3-" + System.currentTimeMillis());
            folderName1 = "folder1-" + System.currentTimeMillis();

            siteUtil.createSite(driver, username, password, siteName1, "description", "Public");
            siteUtil.createSite(driver, username, password, siteName2, "description", "Public");

            docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
            docLib = docLib.getNavigation().selectDetailedView().render();
            siteActions.uploadFile(driver, file1);
            siteActions.uploadFile(driver, file2);
            siteActions.uploadFile(driver, file3);

            siteActions.createFolder(driver, folderName1, "folder title", "folder description");

            file1linkName = "Link to " + file1.getName();

            folder1linkName = "Link to " + folderName1;
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
        siteUtil.deleteSite(username, password, siteName1);
        siteUtil.deleteSite(username, password, siteName2);
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Document Library
     */
    @Test(priority = 0)
    public void testCreateLinkButtonFromDocLib()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo fileRow = siteActions.getFileDirectoryInfo(driver, file1.getName());

        CopyOrMoveContentPage copyOrMoveContentPage = fileRow.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = fileRow.selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");

        copyOrMoveContentPage.selectCancelButton().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Document Library - Selected Items
     */
    @Test(priority = 1)
    public void testCreateLinkButtonFromDocLibSelectedItems()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo fileRow = docLib.getFileDirectoryInfo(file1.getName());
        fileRow.selectCheckbox();

        CopyOrMoveContentPage copyOrMoveContentPage = docLib.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = docLib.getNavigation().selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");

        copyOrMoveContentPage.selectCancelButton().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Document Library - multiple Selected Items
     */
    @Test(priority = 2)
    public void testCreateLinkButtonFromDocLibMultiSelectedItems()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo file1Row = docLib.getFileDirectoryInfo(file1.getName());
        file1Row.selectCheckbox();
        FileDirectoryInfo file2Row = docLib.getFileDirectoryInfo(file2.getName());
        file2Row.selectCheckbox();

        CopyOrMoveContentPage copyOrMoveContentPage = docLib.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = docLib.getNavigation().selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");

        copyOrMoveContentPage.selectCancelButton().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Document Details
     */
    @Test(priority = 3)
    public void testCreateLinkButtonFromDocDetails()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        DocumentDetailsPage documentDetailsPage = docLib.selectFile(file1.getName()).render();

        CopyOrMoveContentPage copyOrMoveContentPage = documentDetailsPage.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = documentDetailsPage.selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");

        copyOrMoveContentPage.selectCancelButton().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results
     */
    @Test(priority = 4)
    public void testCreateLinkButtonFromSearchResults()
    {
        siteActions.checkSearchResultsWithRetry(driver, file1.getName(), file1.getName(), true, 3);

        FacetedSearchResult resultItem = siteActions.getFacetedSearchResult(driver, file1.getName());

        CopyAndMoveContentFromSearchPage copyDialog = resultItem.selectAction(FacetedSearchResultActionsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");

        copyDialog.cancelCopyOrMove().render();
        
        resultItem = siteActions.getFacetedSearchResult(driver, file1.getName());

        copyDialog = resultItem.selectAction(FacetedSearchResultActionsMenu.MOVE_TO).render();
        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");

        copyDialog.cancelCopyOrMove().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results - Selected Items
     */
    @Test(priority = 5)
    public void testCreateLinkButtonFromSearchResultsSelectedItems()
    {
        siteActions.checkSearchResultsWithRetry(driver, file1.getName(), file1.getName(), true, 3);

        FacetedSearchResult resultItem = siteActions.getFacetedSearchResult(driver, file1.getName());
        
        FacetedSearchPage resultPage = resultItem.selectItemCheckBox().render();

        CopyAndMoveContentFromSearchPage copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");

        resultPage = copyDialog.cancelCopyOrMove().render();

        copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();

        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");

        copyDialog.cancelCopyOrMove().render();
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results - multiple Selected Items
     */
    @Test(priority = 6)
    public void testCreateLinkButtonFromSearchResultsMultiSelectedItems()
    {
        FacetedSearchPage resultPage = siteActions.search(driver, "myfile").render();
        Assert.assertTrue(resultPage.hasResults());

        resultPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();

        CopyAndMoveContentFromSearchPage copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");

        resultPage = copyDialog.cancelCopyOrMove().render();

        copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();

        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");

        copyDialog.cancelCopyOrMove().render();
    }

    /**
     * Check that Create Link button is not displayed for Repository -> Sites -> siteName
     */
    @Test(priority = 7)
    public void testCreateLinkButtonForSites() throws Exception
    {
        loginAs(username, password);

        RepositoryPage repoPage = siteActions.openUserDashboard(driver).getNav().selectRepository().render();
        repoPage = repoPage.selectFolder("Sites").render();

        FileDirectoryInfo row = repoPage.getFileDirectoryInfo(siteName1);
        CopyOrMoveContentPage copyOrMoveContentPage = row.selectCopyTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button is visible for sites!!");

        copyOrMoveContentPage.selectCancelButton().render();
    }

    /**
     * Check if a link to a file is created
     */
    @Test(priority = 8)
    public void testCreateLinkToFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // create link to file1 in siteName1
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file1.getName(), ACTION.CREATE_LINK);

        Assert.assertTrue(docLib.isFileVisible(file1linkName));

        FileDirectoryInfo row = docLib.getFileDirectoryInfo(file1linkName);
        Assert.assertTrue(row.isLinkToFile(), "Element is not a link to a file");
    }

    /**
     * Check if a link to a folder is created
     */
    @Test(priority = 9)
    public void testCreateLinkToFolder()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // create link to folderName1 in siteName1
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", folderName1, ACTION.CREATE_LINK);

        Assert.assertTrue(docLib.isItemVisble(folder1linkName));

        FileDirectoryInfo row = docLib.getFileDirectoryInfo(folder1linkName);
        Assert.assertTrue(row.isLinkToFolder(), "Element is not a link to a folder");
    }

    /**
     * Check that multiple links can be created on a single document, in different locations
     */
    @Test(priority = 10)
    public void testCreateMultiLinksToFile()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // create link to file1 in siteName1 -> folderName1
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file1.getName(), ACTION.CREATE_LINK, folderName1);

        // create link to file1 in siteName2
        docLib = siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName2, "", file1.getName(), ACTION.CREATE_LINK).render();

        docLib = docLib.selectFolder(folderName1).render();
        Assert.assertTrue(docLib.isFileVisible(file1linkName));

        siteActions.navigateToDocumentLibrary(driver, siteName2).render();
        Assert.assertTrue(docLib.isFileVisible(file1linkName));
    }

    /**
     * Check that duplicate links cannot be created in the same location
     */
    @Test(priority = 11)
    public void testCreateDuplicateLinksToFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        
        String file2linkName = "Link to " + file2.getName();

        // Create link to file2 in siteName1
        docLib = siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file2.getName(), ACTION.CREATE_LINK).render();
        Assert.assertTrue(docLib.isFileVisible(file2linkName));
        int origFileCount = docLib.getFiles().size();
        
        // try to create a duplicate link
        docLib = siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file2.getName(), ACTION.CREATE_LINK).render();
        int newFileCount = docLib.getFiles().size();
        
        Assert.assertEquals(origFileCount, newFileCount, "Duplicate Shortcut link is created");
    }

    /**
     * Check that create link activities appear in Site Activities
     */
    @Test(priority = 12)
    public void testCreateLinkSiteActivities()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // create link to file2 in siteName2
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName2, "", file2.getName(), ACTION.CREATE_LINK);

        String activityEntry = "Administrator created link to " + file2.getName();

        Assert.assertTrue(siteActions.searchSiteDashBoardWithRetry(driver, Dashlets.SITE_ACTIVITIES, activityEntry, true, siteName2, ActivityType.DESCRIPTION));

    }

    /**
     * Check that create link activities appear in My Activities
     */
    @Test(priority = 13)
    public void testCreateLinkMyActivities()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        // create link to folderName1 in siteName2
        siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName2, "", folderName1, ACTION.CREATE_LINK);

        siteActions.openUserDashboard(driver);

        String activityEntry = "Administrator created link to " + folderName1 + " in " + siteName2;

        Assert.assertTrue(siteActions.searchUserDashBoardWithRetry(driver, Dashlets.MY_ACTIVITIES, activityEntry, true));

    }

}
