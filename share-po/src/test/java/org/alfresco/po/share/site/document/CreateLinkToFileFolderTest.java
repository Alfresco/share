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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.search.*;

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
 *
 */

@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class CreateLinkToFileFolderTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(CreateLinkToFileFolderTest.class);

    private String siteName1;
    private String siteName2;
    private File file1;
    private File file2;
    private String folderName1 = "folder1";
    
    private RepositoryPage repoPage;
    private DashBoardPage dashBoard;
    private DocumentLibraryPage docLib;
    

    @Autowired
    SiteActions siteActions;

    private CopyOrMoveContentPage copyOrMoveContentPage;

    List<String> destinations = new LinkedList<String>();
    List<String> sites = new LinkedList<String>();
    List<String> folders = new LinkedList<String>();

    @BeforeClass
    public void prepare() throws Exception
    {
        try
        {
            loginAs(username, password);

            String random = UUID.randomUUID().toString();

            siteName1 = "site1-" + random;
            siteName2 = "site2-" + random;
            file1 = siteUtil.prepareFile();
            file2 = siteUtil.prepareFile();

            siteUtil.createSite(driver, username, password, siteName1, "description", "Public");
            siteUtil.createSite(driver, username, password, siteName2, "description", "Public");

            siteActions.navigateToDocumentLibrary(driver, siteName1);
            siteActions.uploadFile(driver, file1);
            siteActions.uploadFile(driver, file2);

            siteActions.createFolder(driver, folderName1, "folder title", "folder description");
        }
        catch (Throwable pe)
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
        
        copyOrMoveContentPage = fileRow.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");
        
        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = fileRow.selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");
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

        copyOrMoveContentPage = docLib.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");
        
        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = docLib.getNavigation().selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");
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

        copyOrMoveContentPage = docLib.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");
        
        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = docLib.getNavigation().selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");
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
        
        copyOrMoveContentPage = documentDetailsPage.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");
        
        copyOrMoveContentPage.selectCancelButton().render();

        copyOrMoveContentPage = documentDetailsPage.selectMoveTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button visible");
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results
     */
    @Test(priority = 4)
    public void testCreateLinkButtonFromSearchResults()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        SearchBox search = docLib.getSearch();
        FacetedSearchPage resultPage = search.search(file1.getName()).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(file1.getName());

        CopyAndMoveContentFromSearchPage copyDialog = resultItem.selectAction(FacetedSearchResultActionsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");
        
        copyDialog.cancelCopyOrMove().render();

        copyDialog = resultItem.selectAction(FacetedSearchResultActionsMenu.MOVE_TO).render();
        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results - Selected Items
     */
    @Test(priority = 5)
    public void testCreateLinkButtonFromSearchResultsSelectedItems()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        SearchBox search = docLib.getSearch();
        FacetedSearchPage resultPage = search.search(file1.getName()).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(file1.getName());
        resultItem.selectItemCheckBox().render();

        CopyAndMoveContentFromSearchPage copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");

        copyDialog.cancelCopyOrMove().render();

        copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();;
        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");
    }

    /**
     * Check if Create Link button is displayed in "Copy to" and not displayed in "Move to"
     * opened for file from Search Results - multiple Selected Items
     */
    @Test(priority = 6)
    public void testCreateLinkButtonFromSearchResultsMultiSelectedItems()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        SearchBox search = docLib.getSearch();
        FacetedSearchPage resultPage = search.search("myfile").render();
        Assert.assertTrue(resultPage.hasResults());

        resultPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();

        CopyAndMoveContentFromSearchPage copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        Assert.assertTrue(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button not visible");
        Assert.assertFalse(copyDialog.isCreateLinkButtonEnabled(), "Create Link button is enabled!!");

        copyDialog.cancelCopyOrMove().render();

        copyDialog = resultPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();;
        Assert.assertFalse(copyDialog.isCreateLinkButtonDisplayed(), "Create Link button visible");
    }

    /**
     * Check that Create Link button is not displayed for Repository -> Sites -> siteName
     */
    @Test(priority = 7)
    public void testCreateLinkButtonForSites() throws Exception
    {
        loginAs(username, password);

        dashBoard = adminActions.openUserDashboard(driver);
        repoPage = dashBoard.getNav().selectRepository().render();
        repoPage = repoPage.selectFolder("Sites").render();

        FileDirectoryInfo row = repoPage.getFileDirectoryInfo(siteName1);
        CopyOrMoveContentPage copyOrMoveContentPage = row.selectCopyTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button is visible for sites!!");
    }

    /**
     * Check if a link to a file is created
     */
    @Test(priority = 8)
    public void testCreateLinkToFile()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo fileRow = siteActions.getFileDirectoryInfo(driver, file1.getName());

        copyOrMoveContentPage = fileRow.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectSite(siteName2).render();
        copyOrMoveContentPage.selectCreateLinkButton().render();

        docLib = siteActions.navigateToDocumentLibrary(driver, siteName2).render();
        
        Assert.assertTrue(docLib.isFileVisible("Link to " + file1.getName()));
    }

    /**
     * Check if a link to a folder is created
     */
    @Test(priority = 9)
    public void testCreateLinkToFolder()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo folderRow = siteActions.getFileDirectoryInfo(driver, folderName1);

        copyOrMoveContentPage = folderRow.selectCopyTo().render();
        Assert.assertTrue(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button not visible");

        copyOrMoveContentPage.selectSite(siteName2).render();
        copyOrMoveContentPage.selectCreateLinkButton().render();

        docLib = siteActions.navigateToDocumentLibrary(driver, siteName2).render();
        
        Assert.assertTrue(docLib.isItemVisble("Link to " + folderName1));
    }

    /**
     * Check that multiple links can be created on a single document, in different locations
     */
    //@Test(priority = 10)
    public void test() throws Exception
    {
        //loginAs(username, password);

        dashBoard = adminActions.openUserDashboard(driver);
        repoPage = dashBoard.getNav().selectRepository().render();
        repoPage = repoPage.selectFolder("Sites").render();

        FileDirectoryInfo row = repoPage.getFileDirectoryInfo(siteName1);
        CopyOrMoveContentPage copyOrMoveContentPage = row.selectCopyTo().render();
        Assert.assertFalse(copyOrMoveContentPage.isCreateLinkButtonVisible(), "Create Link button is visible for sites!!");
    }
    
}
