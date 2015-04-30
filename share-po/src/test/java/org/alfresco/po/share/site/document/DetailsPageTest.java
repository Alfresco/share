/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class Tests the common functionalities in Details page
 * 
 * @author Meenal Bhave
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class DetailsPageTest extends AbstractTest
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static String fileName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private FolderDetailsPage folderDetails;
    private static String comment = "test comment";
    private static String xssComment = "";    

    /**
     * Pre test setup: Site creation, file upload, folder creation
     * 
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====prepare====");
        }
        
        siteName = "site" + System.currentTimeMillis();
        fileName = "File";
        folderName = "The first folder";
        folderDescription = folderName;        
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<IMG \"\"\">");
        stringBuilder.append("<SCRIPT>alert(\"test\")</SCRIPT>");
        stringBuilder.append("\">");
        xssComment = stringBuilder.toString();

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");

        // Select DocLib
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        // Create Folder
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();

        // Upload File
        File file = SiteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
    }

    @AfterClass(alwaysRun = true)
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test (groups = {"alfresco-one"})
    public void isCommentSectionPresent() throws Exception
    {
        folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetails.isCommentSectionPresent(), "Failed to Comment section present on the folder's details page");

    }

    @Test(dependsOnMethods = "isCommentSectionPresent", groups = { "alfresco-one" })
    public void addCommentsToFolder() throws Exception
    {
        //folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        // Add text comment
        folderDetails.addComment(null);
        folderDetails.addComment(comment);
        
        folderDetails.addComment(null, null);
        folderDetails.addComment(comment, null);
        folderDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(comment, Encoder.ENCODER_HTML);
        folderDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);

        // Add comment for xss related test
        folderDetails.addComment(xssComment);
        folderDetails.addComment(xssComment, null);
        folderDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        folderDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(folderDetails.getComments().contains(xssComment), "Problem adding XSS Comment");
    }
    
    @Test(dependsOnMethods="addCommentsToFolder", groups = { "alfresco-one" }, expectedExceptions={UnsupportedOperationException.class})
    public void isLinkUnspportedTest() throws Exception
    {
        folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_DOC);
    }    
    
    @Test(dependsOnMethods="isLinkUnspportedTest", groups = { "alfresco-one" })
    public void isCommentsPanelPresent() throws Exception
    {
        Assert.assertTrue(folderDetails.isCommentsPanelPresent());
    }    
    
    @Test(dependsOnMethods="isCommentsPanelPresent", groups = { "alfresco-one" })
    public void isAddCommentsButtonEnbaled() throws Exception
    {
        Assert.assertTrue(folderDetails.isAddCommentsButtonEnbaled());
    }    
    
    @Test(dependsOnMethods="isAddCommentsButtonEnbaled", groups = { "alfresco-one" })
    public void isCopyShareLinkPresent() throws Exception
    {
        Assert.assertTrue(folderDetails.isCopyShareLinkPresent());
    }    
    
    @Test(dependsOnMethods="isCopyShareLinkPresent", groups = { "alfresco-one" })
    public void addCommentsToFile() throws Exception
    {

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        DocumentDetailsPage docDetails = documentLibPage.selectFile(fileName).render();

        // Add text comment
        docDetails.addComment(null);
        docDetails.addComment(comment);
        
        docDetails.addComment(null, null);
        docDetails.addComment(comment, null);
        docDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(comment, Encoder.ENCODER_HTML);
        docDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);

        // Add comment for xss related test
        docDetails.addComment(xssComment);
        docDetails.addComment(xssComment, null);
        docDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        docDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(xssComment), "Problem adding XSS Comment");
    }
   
    @Test(dependsOnMethods="addCommentsToFile", groups = { "alfresco-one" })
    public void isPropertiesPanelPresent() throws Exception
    {
        DetailsPage detailsPage = drone.getCurrentPage().render();
        
        Assert.assertTrue(detailsPage.isPropertiesPanelPresent());
    }
    
    @Test(dependsOnMethods="isPropertiesPanelPresent", groups = { "alfresco-one" })
    public void isTagsPanelPresent() throws Exception
    {
        DetailsPage detailsPage = drone.getCurrentPage().render();
        
        Assert.assertTrue(detailsPage.isTagsPanelPresent());
    }
    
    @Test(dependsOnMethods="isTagsPanelPresent", groups = { "alfresco-one" })
    public void isLikeLinkPresent() throws Exception
    {
        DetailsPage detailsPage = drone.getCurrentPage().render();
        
        Assert.assertTrue(detailsPage.isLikeLinkPresent());
    }
    
    @Test(dependsOnMethods="isLikeLinkPresent", groups = { "alfresco-one" })
    public void isAddCommentButtonPresent() throws Exception
    {
        DetailsPage detailsPage = drone.getCurrentPage().render();
        
        Assert.assertTrue(detailsPage.isAddCommentButtonPresent());
    }
    
    @Test(dependsOnMethods="isAddCommentButtonPresent", groups = { "Enterprise4.2" })
    public void isLinkPresentForDocumentTest() throws Exception
    {

        DocumentDetailsPage docDetails = drone.getCurrentPage().render();
           
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.COPY_TO), "Copy to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MOVE_TO), "Move to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Delete is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Manage Aspect is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_DOC), "Manage Permission is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.CHNAGE_TYPE), "Chnage Type is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Edit Properties is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.DOWNLOAD_DOCUMENT), "Download Document is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.VIEW_IN_EXLPORER), "View In Exlporer to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.UPLOAD_DOCUMENT), "Upload Document is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.EDIT_OFFLINE), "Edit offline is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.GOOGLE_DOCS_EDIT), "Edit Google docs is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.START_WORKFLOW), "Start workflow is not present");
        
    }
    
    @Test(dependsOnMethods="isLinkPresentForDocumentTest", groups = { "Enterprise4.2" })
    public void isDocumentActionPresent() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
           
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.COPY_TO), "Copy to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MOVE_TO), "Move to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Delete to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Manager Aspect not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_FOL), "Manage Permission not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.CHNAGE_TYPE), "Change Type is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Edit properties to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Manage Rules is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.DOWNLOAD_FOLDER), "Download Folder is not present");

    }    

    @Test(dependsOnMethods="isDocumentActionPresent", groups = { "Hybrid" })
    public void isSynPanelPresent() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetails.isSynPanelPresent());
    }
    
    @Test(dependsOnMethods="isSynPanelPresent", groups = { "Hybrid" })
    public void isErrorEditOffline() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        String fileName = "fileName" + System.currentTimeMillis();
         
        // Upload File
        File file = SiteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
        
        DocumentDetailsPage docDetails = documentLibPage.selectFile(fileName).render();  
        docDetails.selectEditOffLine();
        
        Assert.assertFalse(docDetails.isErrorEditOfflineDocument(fileName));      
    }
}