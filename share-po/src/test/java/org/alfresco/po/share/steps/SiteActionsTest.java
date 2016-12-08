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
package org.alfresco.po.share.steps;

/**
 * Test Class to test SiteActions > utils
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class SiteActionsTest extends AbstractTest
{
	@Autowired SiteActions siteActions;
    private String  siteName = "swsdp";
    private String newSite = "site" + System.currentTimeMillis();
    private String[] folderHierarchy = new String[]{"parent", "childA", "childB"};

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs(username, password);
    }
    @Test(groups = "Enterprise-only", priority=1)
    public void testCheckIfDriverNull() throws Exception
    {
        try
        {
            siteActions.checkIfDriverIsNull(null);
        }
        catch(UnsupportedOperationException e)
        {
            Assert.assertTrue(e.getMessage().contains("WebDriver is required"));
        }
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testCheckIfDriverNotNull() throws Exception
    {
        siteActions.checkIfDriverIsNull(driver);
    }

    @Test(groups = "Enterprise-only", priority=3)
    public void testRefreshSharePage() throws Exception
    {
            SharePage page = resolvePage(driver).render();
            SharePage pageRefreshed = siteActions.refreshSharePage(driver).render();
            Assert.assertTrue(page.getClass() == pageRefreshed.getClass());
            Assert.assertTrue(page != pageRefreshed);
    }
    
    @Test(groups = "Enterprise-only", priority=4)
    public void testsWebDriverWait() throws Exception
    {
        long startTime = System.currentTimeMillis();
        long waitDuration = 7000;
        
        
        siteActions.webDriverWait(driver, waitDuration);
        
        long endTime = System.currentTimeMillis();
        Assert.assertTrue(endTime >= startTime + waitDuration);
        
    }
    @Test(groups = "Enterprise-only", priority=5)
    public void testopenSiteDashBoard() throws Exception
    {
            SiteDashboardPage siteDashPage = siteActions.openSiteDashboard(driver, siteName);
            Assert.assertNotNull(siteDashPage);
    }
    
    @Test(groups = "Enterprise-only", priority=6)
    public void testopenSitesContentLibrary() throws Exception
    {
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
            Assert.assertNotNull(docLibPage);
    }
    
    @Test(groups = "Enterprise-only", priority=7)
    public void testCreateSite() throws Exception
    {
            siteActions.createSite(driver, newSite, newSite, "Public");
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(driver, newSite);
            Assert.assertNotNull(docLibPage);
    }
    
    @Test(groups = "Enterprise-only", priority=8)
    public void testAddRemoveAspect() throws Exception
    {
        File file = siteUtil.prepareFile();
        DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        docLibPage = siteActions.uploadFile(driver, file).render();
        docLibPage.selectFile(file.getName()).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(DocumentAspect.EXIF.getValue());
        siteActions.addAspects(driver, aspects);
        
        SelectAspectsPage aspectsPage = siteActions.getAspectsPage(driver);  
        
        Assert.assertTrue(aspectsPage.isAspectAdded(aspects.get(0)));
        
        aspectsPage.clickCancel().render();        
        
        siteActions.removeAspects(driver, aspects);
        
        aspectsPage = siteActions.getAspectsPage(driver);
        
        Assert.assertTrue(aspectsPage.isAspectAvailable(aspects.get(0)));
        
        aspectsPage.clickCancel().render();
    }
    
    @Test(groups = "Enterprise-only", priority=9)
    public void testViewDetailsForFolder() throws Exception
    {
        String folderName = "folder" + System.currentTimeMillis();
        
        siteActions.openSitesDocumentLibrary(driver, siteName);
        
        siteActions.createFolder(driver, folderName, folderName, folderName);
        
        DetailsPage detailsPage = siteActions.viewDetails(driver, folderName).render();
        
        Assert.assertNotNull(detailsPage, "Error during View Folder Details");
        Assert.assertTrue(detailsPage instanceof FolderDetailsPage, "Error during View Folder Details");
        
    }
       
    @Test(groups = "Enterprise-only", priority=10)
    public void testViewDetailsForFile() throws Exception
    {
        File file = siteUtil.prepareFile();
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.uploadFile(driver, file).render();

        DetailsPage detailsPage = siteActions.viewDetails(driver, file.getName()).render();

        Assert.assertNotNull(detailsPage, "Error during View Document Details");
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage, "Error during View Document Details");

    }
    
    @Test(groups = "Enterprise-only", priority=11)
    public void createFolderHierarchy() {
	  siteActions.openSitesDocumentLibrary(driver, newSite);
  	  for (int i = 0; i < folderHierarchy.length; i++) {
  		  siteActions.createFolder(driver, folderHierarchy[i], folderHierarchy[i], folderHierarchy[i]);
  		  siteActions.navigateToFolder(driver, folderHierarchy[i]);
  	  }
  	  
//  	 siteName = "old" + System.currentTimeMillis();
//  	 siteActions.createSite(driver, siteName, siteName, "Public");
    }
    
    @Test(groups = "Enterprise-only", priority=12, dataProvider="tempFilesData")
    public void testCopyArtifact(File file) throws Exception
    {
	  copyOrMoveAction(file,ACTION.COPY, new String[] {});
	  Assert.assertTrue(siteActions.isFileVisible(driver, file.getName()),"File: " + file.getName() + " was copied in document library of site ");  
    }
     
    @Test(groups = "Enterprise-only", priority=13, dataProvider="tempFilesData")
    public void testCopyArtifactInFolderHyerarcy(File file) throws Exception
    {
	  String fullHyerarcy ="";
	  copyOrMoveAction(file, ACTION.COPY, folderHierarchy);
	  
	  for (int i = 0; i < folderHierarchy.length; i++) {
		  fullHyerarcy += "/" + folderHierarchy[i];
		  siteActions.navigateToFolder(driver, folderHierarchy[i]);
	  }
	  	  
	  Assert.assertTrue(siteActions.isFileVisible(driver, file.getName()),String.format("File: [%s[ was copied in document library[%s] of site %s ", file.getName(), fullHyerarcy, newSite));  
    }
    
    @Test(groups = "Enterprise-only", priority=14, dataProvider="tempFilesData")
    public void testMoveArtifact(File file) throws Exception
    {
	  copyOrMoveAction(file,ACTION.MOVE, new String[] {});	  
	  Assert.assertTrue(siteActions.isFileVisible(driver, file.getName()),"File: " + file.getName() + " was moved in document library of site ");  
    }
    
    @Test(groups = "Enterprise-only", priority=15, dataProvider="tempFilesData")
    public void testMoveArtifactInFolderHyerarcy(File file) throws Exception
    {
	  String fullHyerarcy ="";
	  copyOrMoveAction(file, ACTION.MOVE, folderHierarchy);
	  
	  for (int i = 0; i < folderHierarchy.length; i++) {
		  fullHyerarcy += "/" + folderHierarchy[i];
		  siteActions.navigateToFolder(driver, folderHierarchy[i]);
	  }
	  Assert.assertTrue(siteActions.isFileVisible(driver, file.getName()),String.format("File: [%s] was moved in document library[%s] of site %s ",file.getName(),fullHyerarcy, newSite));  
    }
    
    /**
     * Private method used for copying/moving artifacts between sites.
     * @param file
     * @param action
     */
    private void copyOrMoveAction(File file, ACTION action, String[] structure) {
	  siteActions.openSiteDashboard(driver, siteName);
	  siteActions.openDocumentLibrary(driver);
	  siteActions.uploadFile(driver, file);
	  siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, newSite, "", file.getName(), action, structure);
	  siteActions.openSiteDashboard(driver, newSite);
	  siteActions.openDocumentLibrary(driver);
    }
    
    @DataProvider
    public Object[][] tempFilesData() {
		return new Object[][] { {siteUtil.prepareFile()}};  
    }    
}
