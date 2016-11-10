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
package org.alfresco.po.share.search;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.UploadFilePage;
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
public class FacetedSearchBulkActionsE2ETest extends AbstractDocumentTest 
{
    private static String siteName;
    private static String siteName1;
    private static String folderName1, folderName2;
    private static String folderDescription1;
    private static String folderDescription2;
    private static FacetedSearchPage resultsPage;
    private static DocumentLibraryPage documentLibPage;
    AddUsersToSitePage addUsersToSitePage;
    private File bulkfile1;
    private File bulkfile2;
        
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
        siteName1 = "Asite1" + System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        createUser();
        
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName, "description", "Public");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();        
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName2, UserRole.CONSUMER);        
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName3, UserRole.COLLABORATOR);
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName1, "description", "Public");
              
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

    @Test(expectedExceptions = IllegalArgumentException.class, groups = "alfresco-one")
    public void getShareContentWithNull()
    {
        DocumentLibraryPage lib = factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        String t = null;
        lib.getFileDirectoryInfo(t);
    }
    
    //collaborator can copy his own and other user files/folders
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = false)
    public void CopyFileFolderAsCollaboratorTest() throws Exception
    {    	
    	folderName1 = "myfilefolder1"+ System.currentTimeMillis();
        folderName2 = "myfilefolder2"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
    	
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        
    	NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();        
       
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();
        
        logout(driver);        
        loginAs(userName3, UNAME_PASSWORD);        
        
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        NewFolderPage newFolderPage2 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage2.createNewFolder(folderName2, folderDescription2).render();
        UploadFilePage uploadForm2 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm2.uploadFile(bulkfile2.getCanonicalPath()).render();
        SearchBox search = documentLibPage.getSearch();       
        
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile2.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());        
        Assert.assertTrue(resultsPage.hasResults(),folderName1);
        Assert.assertTrue(resultsPage.hasResults(),folderName2);
    	
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(bulkfile2.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        resultsPage.getResultByName(folderName2).selectItemCheckBox();
            	
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("Shared").render();        
        resultsPage = copyAndMoveContentFromSearchPage.clickCopy().render();    
                
        openSiteDocumentLibraryFromSearch(driver, siteName);
       
        assertTrue(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        assertTrue(documentLibPage.isItemVisble(bulkfile2.getName()), "File not displayed");  
        assertTrue(documentLibPage.isItemVisble(folderName1), "File not displayed");
        assertTrue(documentLibPage.isItemVisble(folderName2), "File not displayed");
        
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();        
        Assert.assertTrue(repositoryPage.isFileVisible(bulkfile1.getName()));
        Assert.assertTrue(repositoryPage.isFileVisible(bulkfile2.getName()));
        Assert.assertTrue(repositoryPage.isFileVisible(folderName1));
        Assert.assertTrue(repositoryPage.isFileVisible(folderName2));       
               
        logout(driver);
        
    }
    
    //Collaborator can  move his own files/ folders
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = false)
    public void MoveOwnFileFolderAsCollaboratorTest() throws Exception
    {     	
    	folderName1 = "myfile11folder3"+ System.currentTimeMillis();
        folderName2 = "myfile12folder4"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
        logout(driver);        
        loginAs(userName3, UNAME_PASSWORD);        
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        
        NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
                
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();       
        
        SearchBox search = documentLibPage.getSearch();             
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());   
       
    	resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();       
            	
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("Shared").render();        
        resultsPage = copyAndMoveContentFromSearchPage.clickMove().render();          
               
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);              
        
        assertFalse(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        assertFalse(documentLibPage.isItemVisble(folderName1), "File not displayed");
                
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();        
        Assert.assertTrue(repositoryPage.isFileVisible(bulkfile1.getName()));
        Assert.assertTrue(repositoryPage.isFileVisible(folderName1));              
        
        logout(driver);   
                
    }

    
    //Collaborator cannot  move other user files/ folders with identical folder names ( change this with the bug)
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = true)
    public void MoveOtherUserFileFolderTest() throws Exception
    {    	   	

    	folderName1 = "myfile11folder5"+ System.currentTimeMillis();
        folderName2 = "myfile12folder6"+ System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();        
    	
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                
    	NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
                
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName1);
        
    	NewFolderPage newFolderPage2 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage2.createNewFolder(folderName1, folderDescription1).render();           
                
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName1);        
               
        SearchBox search = documentLibPage.getSearch();        
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());      	
    	
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
		for (int i = 0; i <=resultsPage.getResultCount(); i++) {
			if (resultsPage.getResults().get(i).getName().contains(folderName1)) {
				if (resultsPage.getResultByName(folderName1).isItemCheckBoxSelected() == false){
				resultsPage.getResultByName(folderName1).selectItemCheckBox();
				}
			}
		}
        //resultsPage.getResultByName(folderName1).selectItemCheckBox();  
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();  
                   	
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("Shared").render();        
        CopyOrMoveFailureNotificationPopUp copyOrMoveFailureNotificationPopUp = copyAndMoveContentFromSearchPage.clickMove().render();  
        
        copyOrMoveFailureNotificationPopUp.selectOk().render();
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);        
       
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        assertFalse(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
       
        assertTrue(documentLibPage.isItemVisble(folderName1), "File not displayed");
               
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();
        Assert.assertTrue(repositoryPage.isFileVisible(bulkfile1.getName()));
        Assert.assertFalse(repositoryPage.isFileVisible(folderName1));
        
        logout(driver);    
                
    }
    
    //Consumer cannot copy other user files/folders to any destination folder without permission
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = true)
    public void CopyFileFolderConsumerTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder7"+ System.currentTimeMillis();
        folderName2 = "myfile12folder8"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
    	
    	
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                
    	NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
                
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();  
                
        logout(driver);        
        loginAs(userName2, UNAME_PASSWORD);
        
        SearchBox search = documentLibPage.getSearch();
           
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();        
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
                    	
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("User Homes").render();       
        
        copyAndMoveContentFromSearchPage.clickCopy().render();
        //CopyOrMoveFailureNotificationPopUp copyOrMoveFailureNotificationPopUp = copyAndMoveContentFromSearchPage.selectCopyButton().render();  
        //copyOrMoveFailureNotificationPopUp.selectOk().render();       
      
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        assertTrue(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");          
        assertTrue(documentLibPage.isItemVisble(folderName1), "File not displayed");        
        
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();
        Assert.assertFalse(repositoryPage.isFileVisible(bulkfile1.getName()));
        Assert.assertFalse(repositoryPage.isFileVisible(folderName1));        
          
        logout(driver);               
    }
    
    //Collaborator cannot move his own files/folders to any destination folder without permission
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = true)
    public void MoveFileFolderCollaboratorTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder7"+ System.currentTimeMillis();
        folderName2 = "myfile12folder8"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
    	
    	
    	loginAs(userName3, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                
    	NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
                
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();           
               
        SearchBox search = documentLibPage.getSearch();
           
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();        
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
                    	
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("User Homes").render();       
        
        copyAndMoveContentFromSearchPage.clickMove().render();
        //CopyOrMoveFailureNotificationPopUp copyOrMoveFailureNotificationPopUp = copyAndMoveContentFromSearchPage.selectCopyButton().render();  
        //copyOrMoveFailureNotificationPopUp.selectOk().render();       
      
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        assertTrue(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");          
        assertTrue(documentLibPage.isItemVisble(folderName1), "File not displayed");        
        
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();
        Assert.assertFalse(repositoryPage.isFileVisible(bulkfile1.getName()));
        Assert.assertFalse(repositoryPage.isFileVisible(folderName1));        
          
        logout(driver);               
    }


    //Delete and move option not displayed for Collaborator when own and other user files/folders are selected
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = false)
    public void BulkDeleteAndMoveTest() throws Exception
    {    	   	

    	folderName1 = "myfile11folder9"+ System.currentTimeMillis();
        folderName2 = "myfile12folder10"+ System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
    	
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                
    	NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
                
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();       
        
        logout(driver);        
        loginAs(userName3, UNAME_PASSWORD);
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        
        NewFolderPage newFolderPage2 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage2.createNewFolder(folderName2, folderDescription2).render();
        
        UploadFilePage uploadForm2 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm2.uploadFile(bulkfile2.getCanonicalPath()).render();
        
        SearchBox search = documentLibPage.getSearch();
           
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile2.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());        
          	
    	resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(bulkfile2.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        resultsPage.getResultByName(folderName2).selectItemCheckBox();
            	
        assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.DELETE));
        assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.MOVE_TO));
                 
        logout(driver);        
        
    }
    
    //Collaborator can delete his own file/folders in bulk
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = false)
    public void BulkDeleteOwnFileFoldersTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder11"+ System.currentTimeMillis();
        folderName2 = "myfile12folder12"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();    	
    	        
        logout(driver);        
        loginAs(userName3, UNAME_PASSWORD);
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        
        NewFolderPage newFolderPage2 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage2.createNewFolder(folderName1, folderDescription1).render();
        
        UploadFilePage uploadForm2 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm2.uploadFile(bulkfile1.getCanonicalPath()).render();
        
        SearchBox search = documentLibPage.getSearch();
            
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);          	
    	
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        
        SearchConfirmDeletePage searchConfirmDeletePage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.DELETE).render();
        searchConfirmDeletePage.clickDelete().render();
               
        SiteFinderPage sitefinder = siteUtil.searchSite(driver, siteName);
        SiteDashboardPage sitedash = sitefinder.selectSite(siteName).render();
        documentLibPage = sitedash.getSiteNav().selectDocumentLibrary().render();
       
        assertFalse(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        assertFalse(documentLibPage.isItemVisble(folderName1), "File not displayed");
        
        logout(driver);    
                
    }
    
    
    //Collaborator can download own and other user files and folders
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one")
    public void CollaboratorBulkDownloadTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder13"+ System.currentTimeMillis();
        folderName2 = "myfile12folder14"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
        
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                        
        NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
        
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();       
        
        logout(driver);        
        loginAs(userName3, UNAME_PASSWORD);        
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
                
        NewFolderPage newFolderPage2 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage2.createNewFolder(folderName2, folderDescription2).render();
        
        UploadFilePage uploadForm2 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm2.uploadFile(bulkfile2.getCanonicalPath()).render();
        
        SearchBox search = documentLibPage.getSearch();        
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);
        Assert.assertTrue(resultsPage.hasResults(),folderName2);        
          	
    	resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(bulkfile2.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        resultsPage.getResultByName(folderName2).selectItemCheckBox();
        
        resultsPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.DOWNLOAD_AS_ZIP).render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);
        Assert.assertTrue(resultsPage.hasResults(),folderName2);
                      
        logout(driver);            
        
        
    }
     
    //Consumer can create a work flow on other user files and folders 
    
    @Test(dependsOnMethods = "getShareContentWithNull", groups = "alfresco-one", enabled = false)
    public void bulkStartWorkFlowTest() throws Exception
    {    	   	

    	folderName1 = "myfile11folder15"+ System.currentTimeMillis();
        folderName2 = "myfile12folder16"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
        
    	loginAs(userName1, UNAME_PASSWORD);
    	documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);        
                        
        NewFolderPage newFolderPage1 = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage1.createNewFolder(folderName1, folderDescription1).render();
        
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(bulkfile1.getCanonicalPath()).render();       
        
        logout(driver);        
        loginAs(userName2, UNAME_PASSWORD);        
        
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
            
        SearchBox search = documentLibPage.getSearch();          
        resultsPage = search.search("myfile").render();
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);     
           	
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
                
        StartWorkFlowPage startWorkFlowPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.START_WORKFLOW).render();
        Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, newtaskname1, reviewers);
        newWorkflowPage.startWorkflow(formDetails).render();
        openSiteDocumentLibraryFromSearch(driver, siteName);
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(bulkfile1.getName());
        assertTrue(thisRow.isPartOfWorkflow(), "Document should not be part of workflow.");
        FileDirectoryInfo thisRow1 = documentLibPage.getFileDirectoryInfo(folderName1);
        assertTrue(thisRow1.isPartOfWorkflow(), "Document should not be part of workflow.");
                              
        logout(driver);   
                
    } 
        

}

