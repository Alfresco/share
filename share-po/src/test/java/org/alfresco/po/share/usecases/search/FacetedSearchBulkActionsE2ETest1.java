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

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.SearchSelectedItemsMenu;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
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
public class FacetedSearchBulkActionsE2ETest1 extends AbstractDocumentTest 
{
    private static String siteName;
    
    private static String folderName1, folderName2,folderName3,folderName4,folderName5;
    private static String folderDescription1;
    private static String folderDescription2;
    private static String folderDescription3;
    private static String folderDescription4;
    private static String folderDescription5;    
    private static FacetedSearchPage resultsPage;
    private static DocumentLibraryPage documentLibPage;
    private static StartWorkFlowPage startWorkFlowPage;
    AddUsersToSitePage addUsersToSitePage;
    private File bulkfile1;
    private File bulkfile2;
    private File bulkfile3;
    private File bulkfile4;
    private File bulkfile5;
    private File bulkfile6;
    private File bulkfile7;
    private File bulkfile8;    
    private String filename = "tfile";    
    private String userName1 = "user1" + System.currentTimeMillis();
    private String userName2 = "user2" + System.currentTimeMillis();
    private String userName3 = "user3" + System.currentTimeMillis();
    private String newtaskname1 = "newtask"+ System.currentTimeMillis();
    
    @Autowired SiteActions siteActions;
   
    /**
     * Pre test setup to create site and add users to site
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "Asite" + System.currentTimeMillis();        
        folderName1 = "tfile11folder9"+ System.currentTimeMillis(); 
        folderName4 = "tfile11folder13"+ System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription4 = String.format("Description of %s", folderName4);
       
        bulkfile1 = siteUtil.prepareFile(filename);        
        bulkfile3 = siteUtil.prepareFile(filename);        
        bulkfile4 = siteUtil.prepareFile(filename);       
        bulkfile6 = siteUtil.prepareFile(filename);
              
        createUser();
        
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName, "description", "private");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();        
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName2, UserRole.CONSUMER);        
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName3, UserRole.COLLABORATOR);        
        
       //Open user1 Site document library
    	documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
                
    	//Create Folder
        siteActions.createFolder(driver, folderName1, folderName1, folderDescription1); 
        //Create Folder
        siteActions.createFolder(driver, folderName4, folderName4, folderDescription4);       
    	    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile1);
        siteActions.uploadFile(driver, bulkfile3);
        siteActions.uploadFile(driver, bulkfile4); 
        siteActions.uploadFile(driver, bulkfile6);
        
                
        //Logout as user1
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
        loginAs(userName1, UNAME_PASSWORD);        
       
     }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    } 
        
    /**
     * Delete and move option not displayed for Collaborator
     * when own and other user files/folders are selected
     * 
     */
   
    @Test(groups = "alfresco-one", enabled = true)
    public void BulkDeleteAndMoveTest() throws Exception
    {  	
    	folderName2 = "tfile12folder10"+ System.currentTimeMillis();      
        folderDescription2 = String.format("Description of %s", folderName2);       
        bulkfile2 = siteUtil.prepareFile(filename); 	
    
        //Login as collaborator(user3) to siteName
        loginAs(userName3, UNAME_PASSWORD);
        
        //Open site document library (siteName)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName2, folderName2, folderDescription2);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile2);        
               
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "tfile",bulkfile2.getName(), true, 3));
        
        resultsPage = siteActions.search(driver, "tfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());        
          	
    	//select the items created by user1 and user3
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(bulkfile2.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        resultsPage.getResultByName(folderName2).selectItemCheckBox();
            	
        //verify actions Delete and Move are not displayed in the selected items drop down list
        Assert.assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.DELETE));
        Assert.assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.MOVE_TO));
                 
        logout(driver);        
        
    }    
   
    /**
     * Collaborator can delete his own file/folders in bulk    
     */
    
    @Test(groups = "alfresco-one", enabled = true)
    public void BulkDeleteOwnFileFoldersTest() throws Exception
    {    	   	
    	folderName3 = "tfile11folder11"+ System.currentTimeMillis();     
        folderDescription3 = String.format("Description of %s", folderName1);
   
        bulkfile3 = siteUtil.prepareFile(filename);   	     
         
        //Login as collaborator(user3)
        loginAs(userName3, UNAME_PASSWORD);
        
        //Open user1 site document library (siteName)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName3, folderName3, folderDescription3);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile3);       
                 
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "tfile",bulkfile3.getName(), true, 3));
        resultsPage = siteActions.search(driver, "tfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile3.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName3);	
    	        
        String[] selectedItems = {bulkfile3.getName(), folderName3};
        String destination = " ";
        
        //Select the check box on files and folders and select 'Delete' from Selected Items drop down 
        //Confirm cancel delete
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.DELETE, destination,false);
               
        //open siteName document library where user3 is a collaborator
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
       
        //Verify all files and folders are still displayed in siteName
        Assert.assertTrue(documentLibPage.isItemVisble(bulkfile3.getName()), "File not displayed");  
        Assert.assertTrue(documentLibPage.isItemVisble(folderName3), "File not displayed");
        
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "tfile",bulkfile3.getName(), true, 3));
        resultsPage = siteActions.search(driver, "tfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile3.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName3);             
        
        //Select the check box on files and folders and select 'Delete' from Selected Items drop down and confirm Delete
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.DELETE, destination,true);
               
        //open siteName document library where user3 is a collaborator
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
       
        //Verify all files and folders are deleted from siteName
        Assert.assertFalse(documentLibPage.isItemVisble(bulkfile3.getName()), "File not displayed");  
        Assert.assertFalse(documentLibPage.isItemVisble(folderName3), "File not displayed");
        
        
        logout(driver);    
                
    }  
        
    /**
     * Collaborator can download own and other user files and folders in bulk
     **/
    
    @Test(groups = "alfresco-one", enabled = true)
    public void CollaboratorBulkDownloadTest() throws Exception
    {    	   	
    	folderName5 = "tfile12folder14"+ System.currentTimeMillis();        
        folderDescription5 = String.format("Description of %s", folderName5);
       
        bulkfile5 = siteUtil.prepareFile(filename);        
    	
        //Login as user3 (collaborator to siteName)
        loginAs(userName3, UNAME_PASSWORD);        
        
        //open siteName document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName5, folderName5, folderDescription5);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile5);
        
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "tfile",bulkfile5.getName(), true, 3));
        resultsPage = siteActions.search(driver, "tfile").render();
     
        String[] selectedItems = {bulkfile4.getName(), folderName4, bulkfile5.getName(), folderName5};
        String destination = " ";
        
        //Select the check box on files and folders created by user1 and user3 and select 'DOWNLOAD_AS_ZIP' from Selected Items drop down 
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.DOWNLOAD_AS_ZIP, destination,false);
        Assert.assertTrue(resultsPage.hasResults(),bulkfile4.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile5.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName4);
        Assert.assertTrue(resultsPage.hasResults(),folderName5);
                      
        logout(driver);            
        
        
    }     
   
    /**
     * Collaborator can create a work flow on his own and other user files in bulk
     **/
    @Test(groups = "alfresco-one", enabled = true)
    public void bulkStartWorkFlowTest() throws Exception
    {
    	
        bulkfile7 = siteUtil.prepareFile(filename);
        bulkfile8 = siteUtil.prepareFile(filename);      
                
        //Login as user3 (collaborator to siteName)
        loginAs(userName3, UNAME_PASSWORD);        
        
        //open siteName document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);        
        
        //Upload File
        siteActions.uploadFile(driver, bulkfile7); 
        siteActions.uploadFile(driver, bulkfile8);    
            
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "tfile",bulkfile8.getName(), true, 3));
        resultsPage = siteActions.search(driver, "tfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile6.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile7.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile8.getName());
        
        	
        String[] selectedItems = {bulkfile6.getName(), bulkfile7.getName()};
        String destination = " ";
        
        //Select all the items check box and click on Start work flow action from selected items drop down menu
        startWorkFlowPage = siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.START_WORKFLOW, destination,false).render();
        Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
        
        //Create a new workflow
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, newtaskname1, reviewers);
        newWorkflowPage.startWorkflow(formDetails).render();
        
        //Open siteName document library
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        //Verify file1 is part of workflow
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(bulkfile6.getName());
        assertTrue(thisRow.isPartOfWorkflow(), "Document should be part of workflow.");
        
        //Verify File2 is part of workflow
        FileDirectoryInfo thisRow1 = documentLibPage.getFileDirectoryInfo(bulkfile7.getName());
        assertTrue(thisRow1.isPartOfWorkflow(), "Document should be part of workflow.");
        
        //Verify File2 is part of workflow
        FileDirectoryInfo thisRow2 = documentLibPage.getFileDirectoryInfo(bulkfile8.getName());
        Assert.assertFalse(thisRow2.isPartOfWorkflow(), "Document should not be part of workflow.");
                            
        logout(driver);   
                
    } 
        

}