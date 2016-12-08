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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;

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

        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");

        // Select DocLib
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        // Create Folder
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();

        // Upload File
        File file = siteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
    }

    @AfterClass(alwaysRun = true)
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test(groups = { "alfresco-one" })
    public void deleteFileWithVersionableAspectUsingWebdav() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        
        //create plain text file
        String fileWithVersionableAspectName = "plainTextFileWithVersionableAspect" + System.currentTimeMillis();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileWithVersionableAspectName);
        contentDetails.setTitle("Text File With Versionable Aspect Title");
        contentDetails.setDescription("Text File With Versionable Aspect Description");
        contentDetails.setContent("Text File With Versionable Aspect Content");
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        Assert.assertNotNull(detailsPage);
        
        //add versionable ascpect to the file
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.VERSIONABLE);
        aspectsPage = aspectsPage.add(aspects).render();
        Assert.assertFalse(aspectsPage.getAvailableSystemAspects().contains(DocumentAspect.VERSIONABLE));
        detailsPage = aspectsPage.clickApplyChanges().render();
        documentLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        Assert.assertTrue(documentLibPage.isFileVisible(fileWithVersionableAspectName));
        
        //delete file with versionable aspect using webdav
        String fileWebdavUrl = "http://" + alfrescoSever + ":" + alfrescoPort +"/alfresco/webdav/Sites/"
                               + siteName + "/documentLibrary/" + fileWithVersionableAspectName;
        int response = executeDeleteRequest(fileWebdavUrl, username, password);

        //check the http response
        Assert.assertEquals(response, 200);

        //wait for webdav to delete file and then check the file is not present in document library 
        Thread.sleep(solrWaitTime);
        page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        Assert.assertFalse(documentLibPage.isFileVisible(fileWithVersionableAspectName));
       
    }

    @Test (dependsOnMethods = "deleteFileWithVersionableAspectUsingWebdav", groups = {"alfresco-one"})
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

        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
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
        DetailsPage detailsPage = resolvePage(driver).render();
        
        Assert.assertTrue(detailsPage.isPropertiesPanelPresent());
    }
    
    @Test(dependsOnMethods="isPropertiesPanelPresent", groups = { "alfresco-one" })
    public void isTagsPanelPresent() throws Exception
    {
        DetailsPage detailsPage = resolvePage(driver).render();
        
        Assert.assertTrue(detailsPage.isTagsPanelPresent());
    }
    
    @Test(dependsOnMethods="isTagsPanelPresent", groups = { "alfresco-one" })
    public void isLikeLinkPresent() throws Exception
    {
        DetailsPage detailsPage = resolvePage(driver).render();
        
        Assert.assertTrue(detailsPage.isLikeLinkPresent());
    }
    
    @Test(dependsOnMethods="isLikeLinkPresent", groups = { "alfresco-one" })
    public void isAddCommentButtonPresent() throws Exception
    {
        DetailsPage detailsPage = resolvePage(driver).render();
        
        Assert.assertTrue(detailsPage.isAddCommentButtonPresent());
    }
    
    @Test(dependsOnMethods="isAddCommentButtonPresent", groups = { "Enterprise4.2" })
    public void isLinkPresentForDocumentTest() throws Exception
    {

        DocumentDetailsPage docDetails = resolvePage(driver).render();
           
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.COPY_TO), "Copy to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MOVE_TO), "Move to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Delete is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Manage Aspect is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_DOC), "Manage Permission is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.CHANGE_TYPE), "Chnage Type is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Edit Properties is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.VIEW_IN_EXLPORER), "View In Exlporer to is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.UPLOAD_DOCUMENT), "Upload Document is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.EDIT_OFFLINE), "Edit offline is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.GOOGLE_DOCS_EDIT), "Edit Google docs is not present");
        Assert.assertTrue(docDetails.isDocumentActionPresent(DocumentAction.START_WORKFLOW), "Start workflow is not present");
        
    }
    
    @Test(dependsOnMethods="isLinkPresentForDocumentTest", groups = { "Enterprise4.2" })
    public void isDocumentActionPresent() throws Exception
    {
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
           
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.COPY_TO), "Copy to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MOVE_TO), "Move to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Delete to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Manager Aspect not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_FOL), "Manage Permission not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.CHANGE_TYPE), "Change Type is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Edit properties to is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Manage Rules is not present");
        Assert.assertTrue(folderDetails.isDocumentActionPresent(DocumentAction.DOWNLOAD_FOLDER), "Download Folder is not present");
    }
    
    //@Test(dependsOnMethods="isDocumentActionPresent", groups = { "Enterprise4.2" })
    public void isPermissionsPanelPresent() throws Exception
    {
        Assert.assertTrue(folderDetails.isPermissionsPanelPresent());
    }  
    
}
