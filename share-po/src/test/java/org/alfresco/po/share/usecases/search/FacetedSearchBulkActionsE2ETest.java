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
package org.alfresco.po.share.usecases.search;

import java.io.File;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.CopyOrMoveFailureNotificationPopUp;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.SearchSelectedItemsMenu;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Search Bulk Actions is operating correctly.
 *
 * @author Charu
 * 
 */
@Listeners(FailedTestListener.class)
public class FacetedSearchBulkActionsE2ETest extends AbstractDocumentTest 
{
    private static String siteName;
    private static String siteName1;
    private static String siteName2;
    private static String folderName1, folderName2, folderName3, folderName4,folderName5;
    private static String folderDescription1;
    private static String folderDescription2;
    private static String folderDescription3;
    private static String folderDescription4;
    private static String folderDescription5;
    private String filename = "search-file-";
    private static FacetedSearchPage resultsPage;
    private static DocumentLibraryPage documentLibPage;
    CopyOrMoveFailureNotificationPopUp copyOrMoveFailureNotificationPopUp;
    AddUsersToSitePage addUsersToSitePage;
    private File bulkfile1;
    private File bulkfile2;
    private File bulkfile3;
    private File bulkfile4; 
    private File bulkfile5; 
    private String userName1 = "user1-" + System.currentTimeMillis();
    private String userName2 = "user2-" + System.currentTimeMillis();
    private String userName3 = "user3-" + System.currentTimeMillis();
    @Autowired SiteActions siteActions;
   
    /**
     * Pre test setup to create site and add users to site
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        String random = String.valueOf(System.currentTimeMillis());

        siteName = "site1-" + System.currentTimeMillis();
        siteName1 = "site2-" + System.currentTimeMillis();
        siteName2 = "site3-" + System.currentTimeMillis();

        folderName1 = "search-folder1-" + random;
        folderDescription1 = String.format("Description of %s", folderName1);
        folderName3 = "search-folder3-" + random;
        folderDescription3 = String.format("Description of %s", folderName3);
        folderName4 = "search-folder4-" + random;
        folderDescription4 = String.format("Description of %s", folderName4);      
        
        bulkfile3 = siteUtil.prepareFile(filename);               
        bulkfile1 = siteUtil.prepareFile(filename);        
        bulkfile4 = siteUtil.prepareFile(filename);         
        
        createUser();
        loginAs(userName1, UNAME_PASSWORD);        
            
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName, "description", "Private");     
                      
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();        
        
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName2, UserRole.CONSUMER);        
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName3, UserRole.COLLABORATOR);
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName2, "description", "public");
        
        //open user1 site document library (siteName)
    	documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
    	
    	//create folder1 
    	siteActions.createFolder(driver, folderName1, folderName1, folderDescription1);
    	
    	//upload file1
    	siteActions.uploadFile(driver, bulkfile1);
    	
    	 //create folder1 
    	siteActions.createFolder(driver, folderName4, folderName4, folderDescription4);
                
    	//upload file1
    	siteActions.uploadFile(driver, bulkfile4);
        
        logout(driver);

        loginAs(userName3, UNAME_PASSWORD);      
        siteUtil.createSite(driver, userName3, UNAME_PASSWORD, siteName1, "description", "public");
        
        logout(driver);   
        
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUser() throws Exception
    {
        createEnterpriseUser(userName1);
        createEnterpriseUser(userName2);
        createEnterpriseUser(userName3);
     }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    } 
        
    /**
     * This test is to check collaborator can copy his own and other user files/folders
     * to his own site
     * 
     */
    @Test(groups = "alfresco-one", enabled = true)
    public void CopyFileFolderAsCollaboratorTest() throws Exception
    {     	
        folderName2 = "search-folder2-"+ System.currentTimeMillis();
        folderDescription2 = String.format("Description of %s", folderName2);
        
        //bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile(filename);  	
    	        
        //Login as user3 (collaborator to siteName)
        loginAs(userName3, UNAME_PASSWORD);      
        
        //Open user1 site document library (siteName)
        siteActions.openSitesDocumentLibrary(driver, siteName);
        
    	//Create Folder2
        siteActions.createFolder(driver, folderName2, folderName2, folderDescription2);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile2);       
            	
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "search-file-", bulkfile2.getName(), true, 3));
        
        resultsPage = siteActions.search(driver, "search-").render();
        
        //Verify files and folders are displayed in search results page
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName()); 
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());        
        Assert.assertTrue(resultsPage.hasResults(),folderName1);
        Assert.assertTrue(resultsPage.hasResults(),folderName2);        
       
        String[] selectedItems = {bulkfile1.getName(), bulkfile2.getName(), folderName1, folderName2};

        //Select the files and folders and copy to user3 own site (siteName1)
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.COPY_TO, siteName1,false);
                
        //open siteName document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Verify files and folders are still displayed in user1 own site (siteName) 
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile2.getName()), "File not displayed");  
        Assert.assertTrue(documentLibPage.isItemVisble(folderName1), "Folder not displayed");
        Assert.assertTrue(documentLibPage.isItemVisble(folderName2), "Folder not displayed");
      
        //open siteName1 document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName1);
        
       //Verify files and folders are displayed/copied to user3 own site (siteName1) 
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile2.getName()), "File not displayed");  
        Assert.assertTrue(documentLibPage.isItemVisble(folderName1), "Folder not displayed");
        Assert.assertTrue(documentLibPage.isItemVisble(folderName2), "Folder not displayed");
               
        logout(driver);
    }    
    
    /**
     * This test is to check collaborator can move his own file/folder
     * to his own site
     * 
     */
    
    @Test(groups = "alfresco-one", enabled = true)
    public void MoveOwnFileFolderAsCollaboratorTest() throws Exception
    {
        folderName3 = "search-folder3-" + System.currentTimeMillis();
        folderDescription3 = String.format("Description of %s", folderName3);

        bulkfile3 = siteUtil.prepareFile(filename);

        //Login as user3(collaborator) to siteName
        loginAs(userName3, UNAME_PASSWORD);

        //open user1 site document library (siteName)
        siteActions.openSitesDocumentLibrary(driver, siteName);

        //create folder1
        siteActions.createFolder(driver, folderName3, folderName3, folderDescription3);

        //upload file1
        siteActions.uploadFile(driver, bulkfile3);

        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "search-file-",bulkfile3.getName(), true, 3));

        resultsPage = siteActions.search(driver, "search-").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile3.getName());

        String[] selectedItems = {bulkfile3.getName(), folderName3};

        //Select the files and folders and Move to user3 own site (siteName1)
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.MOVE_TO, siteName1,false);

        //open siteName document library
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);

        //Verify files and folders are not displayed in user1 own site (siteName)
        Assert.assertFalse(documentLibPage.isItemVisble(bulkfile3.getName()), "File not displayed");
        Assert.assertFalse(documentLibPage.isItemVisble(folderName3), "Folder not displayed");

        //open siteName1 document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName1);

        //Verify files and folders are moved to user3 own site (siteName1)
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile3.getName()), "File not displayed");
        Assert.assertTrue(documentLibPage.isItemVisble(folderName3), "Folder not displayed");

        logout(driver);
    }

    /**
     * Consumer cannot copy other user files/folders to any destination folder
     * without permission
     *
     */

    @Test(groups = "alfresco-one", enabled = true)
    public void CopyFileFolderConsumerTest() throws Exception
    {
        //Login as consumer (user2)
        loginAs(userName2, UNAME_PASSWORD);

        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "search-file-",bulkfile4.getName(), true, 3));

        resultsPage = siteActions.search(driver, "search-").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile4.getName());

        String[] selectedItems = {bulkfile4.getName(), folderName4};

        //Select the files and folders and copy to user1 own site (siteName1 where user is not a member)
        copyOrMoveFailureNotificationPopUp = siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.COPY_TO, siteName1,false).render();
        copyOrMoveFailureNotificationPopUp.selectOk().render();

        //Open user1 site document library (siteName)
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);

        //Verify files and folders are still displayed in user2 own site (siteName)
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile4.getName()), "File not displayed");
        Assert.assertTrue(documentLibPage.isItemVisble(folderName4), "Folder not displayed");

        //Open user1 site document library (siteName1)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName1);

        //Verify files and folders are not displayed in user1 own site (siteName1)
        Assert.assertFalse(documentLibPage.isItemVisble(bulkfile4.getName()), "File not displayed");
        Assert.assertFalse(documentLibPage.isItemVisble(folderName4), "Folder not displayed");

        logout(driver);
    }

    /**
     * Collaborator cannot move his own files/folders to any destination folder
     * without permission
     *
     */

    @Test(groups = "alfresco-one", enabled = true)
    public void MoveFileFolderCollaboratorTest() throws Exception
    {
        folderName5 = "search-folder5-" + System.currentTimeMillis();
        folderDescription5 = String.format("Description of %s", folderName5);
        bulkfile5 = siteUtil.prepareFile(filename);

    	//Login as user3(collaborator) to siteName
        loginAs(userName3, UNAME_PASSWORD);

        //Open user1 site document library (siteName)
        siteActions.openSitesDocumentLibrary(driver, siteName);

        //create folder1
    	siteActions.createFolder(driver, folderName5, folderName5, folderDescription5);

    	//upload file1
        siteActions.uploadFile(driver, bulkfile5);

        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "search-file-", bulkfile5.getName(), true, 3));
        resultsPage = siteActions.search(driver, "search-").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile5.getName());

        String[] selectedItems = {bulkfile5.getName(), folderName5};

        //Select the files and folders and Move to user1 own site (siteName2 where user3 is not a member)
        copyOrMoveFailureNotificationPopUp = siteActions.performBulkActionOnSelectedResults(driver, selectedItems, SearchSelectedItemsMenu.MOVE_TO, siteName2,false).render();
        copyOrMoveFailureNotificationPopUp.selectOk().render();

        //Open user1 site document library (siteName)
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);

        //Verify files and folders are still displayed in user1 own site (siteName)
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile5.getName()), "File not displayed");
        Assert.assertTrue(documentLibPage.isItemVisble(folderName5), "Folder not displayed");

        //Open user1 site document library (siteName2 where user3 is not a site member)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName2);

        //Verify files and folders are not displayed in user1 own site (siteName2)
        Assert.assertFalse(documentLibPage.isItemVisble(bulkfile5.getName()), "File not displayed");
        Assert.assertFalse(documentLibPage.isItemVisble(folderName5), "Folder not displayed");

        logout(driver);
    }
}

