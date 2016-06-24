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
package org.alfresco.po.share;

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.TagPage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Functional test to test ShareDialogue
 * 
 * @author Meenal Bhave
 * @since 4.3HBG
 */
@Listeners(FailedTestListener.class)
public class ShareDialoguePageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(ShareDialoguePageTest.class);
    
    private DashBoardPage dashBoard;
    private SitePage site;
    private DocumentLibraryPage documentLibPage;
    private DocumentDetailsPage docDetailsPage;
    private EditDocumentPropertiesPage editPropPage;
    private ShareDialogue dialogue;
    
    private String siteName;
    private static String fileName;
    private static String folderName;
    private static String folderDescription;

    @BeforeClass(groups = { "Enterprise-only", "Cloud-only" })
    public void setup() throws Exception
    {
        siteName = "site-" + System.currentTimeMillis();
        fileName = "File";
        folderName = "The first folder";
        folderDescription = folderName;  
        
        dashBoard = loginAs(username, password);
        dashBoard = dashBoard.getNav().selectMyDashBoard().render();
        
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");

        // Select DocLib
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        // Create Folder
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();

        // Upload File
        File file = siteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
        
        // Back to DashBoardPage
        documentLibPage = resolvePage(driver).render();        
    }

    @Test
    public void resolveCreateSiteDialogue() throws Exception
    {
        documentLibPage.getNav().selectCreateSite().render();
        dialogue = factoryPage.getPage(driver).render();
        Assert.assertTrue(dialogue.isShareDialogueDisplayed());
        
        dialogue.getShareDialoguePageName();
        CreateSitePage page = factoryPage.getPage(driver).render();
        Assert.assertNotNull(page);

        logger.info("Title: " + dialogue.getDialogueTitle());
    }

    @Test(dependsOnMethods="resolveCreateSiteDialogue")
    public void closeCreateSiteDialogue() throws Exception
    {
        documentLibPage = closeDialogue().render();
    }
    
    // Create Folder
    @Test(dependsOnMethods="closeCreateSiteDialogue")
    public void resolveCreateFolderDialogue() throws Exception
    {        
        // Select DocLib
        site = resolvePage(driver).render();
        documentLibPage = site.getSiteNav().selectDocumentLibrary().render();

        // Create Folder
        documentLibPage.getNavigation().selectCreateNewFolder();

        dialogue = factoryPage.getPage(driver).render();
        Assert.assertTrue(dialogue.isShareDialogueDisplayed());
        
        dialogue.getShareDialoguePageName();
        
        NewFolderPage page = factoryPage.getPage(driver).render();
        Assert.assertNotNull(page);
        
        logger.info("Title: " + dialogue.getDialogueTitle());    
    }
    
    @Test(dependsOnMethods="resolveCreateFolderDialogue")
    public void closeCreateFolderDialogue() throws Exception
    {
        documentLibPage = closeDialogue().render();
    }

    // Upload File
    @Test(dependsOnMethods="closeCreateFolderDialogue")
    public void resolveCreateFileDialogue() throws Exception
    {
        // Select Upload File
        documentLibPage.getNavigation().selectFileUpload().render();

        dialogue = factoryPage.getPage(driver).render();
        Assert.assertTrue(dialogue.isShareDialogueDisplayed());
        
        dialogue.getShareDialoguePageName();
        
        UploadFilePage page = factoryPage.getPage(driver).render();
        Assert.assertNotNull(page);

        logger.info("Title: " + dialogue.getDialogueTitle());
    }

    @Test(dependsOnMethods = "resolveCreateFileDialogue")
    public void closeUploadFileDialogue() throws Exception
    {
        documentLibPage = closeDialogue().render();
    }

    // Edit Properties
    @Test(dependsOnMethods="closeUploadFileDialogue")
    public void resolveEditPropertiesDialogue() throws Exception
    {
        // Edit Properties
        documentLibPage.getFileDirectoryInfo(fileName).selectEditProperties().render();      
        
        dialogue = factoryPage.getPage(driver).render();
        Assert.assertTrue(dialogue.isShareDialogueDisplayed());
        
        dialogue.getShareDialoguePageName();
        
        EditDocumentPropertiesPage page = factoryPage.getPage(driver).render();
        Assert.assertNotNull(page);

        logger.info("Title: " + dialogue.getDialogueTitle());
    }
    
    @Test(dependsOnMethods="resolveEditPropertiesDialogue")
    public void closeEditPropertiesDialogue() throws Exception
    {
        documentLibPage = closeDialogue().render();
    }
    
    // Edit Properties > Tags
    @Test(dependsOnMethods="closeEditPropertiesDialogue")
    public void resolveEditPropertiesTagsDialogue() throws Exception
    {
        // Select File, Edit Properties > Tag
        docDetailsPage = documentLibPage.selectFile(fileName).render();
        editPropPage = docDetailsPage.selectEditProperties().render();
        editPropPage.getTag().render();        
        
        dialogue = factoryPage.getPage(driver).render();
        Assert.assertTrue(dialogue.isShareDialogueDisplayed());
        
        dialogue.getShareDialoguePageName();
        
        TagPage page = factoryPage.getPage(driver).render();
        Assert.assertNotNull(page);

        logger.info("Title: " + dialogue.getDialogueTitle());
    }
    
    @Test(dependsOnMethods="resolveEditPropertiesTagsDialogue")
    public void closeEditPropertiesTagsDialogue() throws Exception
    {
        editPropPage = closeDialogue().render();
    }

/*
    // Select Assignee (My tasks)
    @Test(dependsOnMethods="closeEditPropertiesTagsDialogue")
    public void resolveSelectAssigneeDialogue() throws Exception
    {
        
    }
    
    @Test(dependsOnMethods="resolveSelectAssigneeDialogue")
    public void closeSelectAssigneeDialogue() throws Exception
    {
        closeDialogue().render();
    }
    
    // Edit Properties > Aspect
    @Test(dependsOnMethods="closeSelectAssigneeDialogue")
    public void resolveEditPropertiesAspect() throws Exception
    {
        
    }
    
    @Test(dependsOnMethods="resolveEditPropertiesAspect", groups={"EnterpriseOnly"})
    public void closeEditPropertiesAspect() throws Exception
    {
        closeDialogue().render();
    }

    // Edit Properties > Categories
    @Test(dependsOnMethods="closeEditPropertiesAspect")
    public void resolveEditPropertiesCategories() throws Exception
    {
        
    }
    
    @Test(dependsOnMethods="resolveEditPropertiesCategories", groups={"EnterpriseOnly"})
    public void closeEditPropertiesCategories() throws Exception
    {
        closeDialogue().render();
    }
*/
public HtmlPage closeDialogue() throws Exception
{
    ShareDialogue dialogue = factoryPage.getPage(driver).render();
    HtmlPage sharePage = dialogue.clickClose().render();
    
    Assert.assertNotNull(sharePage);
    return sharePage;
}
}
