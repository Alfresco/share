/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document CRUD is operating correctly.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
@SuppressWarnings("unused")
public class DocumentDetailsPageTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(DocumentDetailsPageTest.class);
    private static final String COMMENT = "adding a comment to document is easy!!.";
    private static final String EDITED_COMMENT = "editing a comment is even easier!";

    private String siteName;
    private File file;
    private String fileName;
    private String fileExt;
    private File newfile;
    private File uploadFile;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        siteName = "ddSiteTest" + System.currentTimeMillis();
        loginAs(username, password);
      //  WebDroneUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile();
        uploadFile = SiteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        fileExt = st.nextToken();
    }

    @AfterMethod(groups={"alfresco-one"})
    public void deleteFile()
    {
        if (newfile != null)
        {
            newfile.delete();
        }
    }

    @AfterClass(groups={"alfresco-one"})
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Test upload file functionality.
     * 
     * @throws Exception
     * @throws Exception
     *             if error
     */
    @Test
    public void uploadFile() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====uploadFile====");
        SitePage site = (SitePage) drone.getCurrentPage();
        site.render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        docPage.render();
        docPage = (docPage.getNavigation().selectDetailedView()).render();
        // DocumentLibraryPage docPage = getDocumentLibraryPage(siteName).render();
        Assert.assertTrue(docPage.isTitlePresent(siteName));
        Assert.assertTrue(docPage.isDocumentLibrary());
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();

        List<FileDirectoryInfo> results = docPage.getFiles();
        if (logger.isTraceEnabled())
        {
            logger.trace("results are not null: " + results);
            logger.trace("results empty: " + results.isEmpty());
        }
        Assert.assertNotNull(results);
        if (results.isEmpty())
        {
            saveScreenShot("DocumentDetailsPageTest.uploadFile.empty");
        }
        Assert.assertFalse(results.isEmpty());

        boolean isFolder = results.get(0).isTypeFolder();
        Assert.assertEquals(isFolder, false);
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes.
     */
    //Grouped as bug Due to https://issues.alfresco.com/jira/browse/ACE-1628 ACE Bug
    @Test(dependsOnMethods = "uploadFile", groups="ACEBug", enabled = false)
    public void minorVersionUpdateOfAnExistingFile() throws Exception
    {
        DocumentDetailsPage docDetailsPage = selectDocument(file).render();
        if (logger.isTraceEnabled()) logger.trace("====updateAnExistingFile====");
        // Assert the current file exists and is version 1
        Assert.assertEquals(file.getName(), docDetailsPage.getDocumentTitle());
        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.0");
        if (logger.isTraceEnabled()) logger.trace("---update with minor version----");
        // Update file with a minor version
        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        if (logger.isTraceEnabled()) logger.trace("---selected new version to upload----");
        updatePage.selectMinorVersionChange();
        updatePage.setComment("Reloading the file with correct image");
        updatePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updatePage.submit().render();
        if (logger.isTraceEnabled()) logger.trace("---upload submited----");

        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.1");
        Assert.assertEquals("Reloading the file with correct image", docDetailsPage.getCommentsOfLastCommit());
        if (logger.isTraceEnabled()) logger.trace("---update with major version----");
    }

    //Grouped as bug Due to https://issues.alfresco.com/jira/browse/ACE-1628 ACE Bug
    @Test(dependsOnMethods = "minorVersionUpdateOfAnExistingFile", groups="ACEBug", enabled=false)
    public void majorVersionUpdateOfAnExistingFile() throws Exception
    {
        DocumentDetailsPage docDetailsPage = drone.getCurrentPage().render();
        // Update file with a major version change
        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        updatePage.selectMajorVersionChange();
        updatePage.setComment("Reloading the final image");
        updatePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updatePage.submit().render();

        Assert.assertEquals("2.0", docDetailsPage.getDocumentVersion());
        Assert.assertEquals("Reloading the final image", docDetailsPage.getCommentsOfLastCommit());
        Assert.assertEquals(true, docDetailsPage.isUploadNewVersionDisplayed());
    }

    /**
     * Test the function of add a like to a document
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "uploadFile")
    public void addLikeDislike() throws Exception
    {
        DocumentDetailsPage docDetailsPage = selectDocument(file).render();
        if (logger.isTraceEnabled()) logger.trace("====addLikeDislike====");
        DocumentDetailsPage docsPage = drone.getCurrentPage().render();
        Assert.assertEquals("0", docsPage.getLikeCount());
        Assert.assertFalse(docsPage.isLiked());
        docsPage = docsPage.selectLike().render();
        Assert.assertEquals("1", docsPage.getLikeCount());
        Assert.assertTrue(docsPage.isLiked());
        Assert.assertNotNull(docsPage.getToolTipForLike());
        docsPage = docsPage.selectLike().render();
        Assert.assertEquals("0", docsPage.getLikeCount());
        Assert.assertFalse(docsPage.isLiked());
        docsPage.getToolTipForFavourite();
        Assert.assertNotNull(docsPage.getToolTipForLike());
    }

    @Test(dependsOnMethods = "addLikeDislike")
    public void favouriteUnfavourite() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====favouriteUnfavourite====");
        DocumentDetailsPage docsPage = drone.getCurrentPage().render();
        Assert.assertFalse(docsPage.isFavourite());
        Assert.assertNotNull(docsPage.getToolTipForFavourite());
        docsPage = docsPage.selectFavourite().render();
        Assert.assertTrue(docsPage.isFavourite());
    }

    @Test(dependsOnMethods = "favouriteUnfavourite")
    public void getDocumentProperties()
    {
        DocumentDetailsPage docDetailsPage = drone.getCurrentPage().render();
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), file.getName());
        Assert.assertEquals(properties.get("Title"), "(None)");
        Assert.assertEquals(properties.get("Description"), "(None)");
        Assert.assertEquals(properties.get("Mimetype"), "Plain Text");
        Assert.assertEquals(properties.get("Author"), "(None)");
        Assert.assertEquals(properties.get("Size"), "33 bytes");
        Assert.assertEquals(properties.get("Creator"), username.trim());
        Assert.assertNotNull(properties.get("CreatedDate"));
        Assert.assertEquals(properties.get("Modifier"), username.trim());
        Assert.assertNotNull(properties.get("ModifiedDate"));
    }

    /**
     * Test adding comments to document.
     */
    @Test(dependsOnMethods = "getDocumentProperties")
    public void addComments() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====addComments====");
        DocumentDetailsPage docsPage = drone.getCurrentPage().render();
        Assert.assertEquals(docsPage.getCommentCount(), 0);
        docsPage = docsPage.addComment(COMMENT).render();
        DocumentLibraryPage libPage = docsPage.getSiteNav().selectSiteDocumentLibrary().render();
        int commentCount = libPage.getCommentCount();
        Assert.assertEquals(commentCount, 1);
        docsPage = selectDocument(file).render();
        List<String> comments = docsPage.getComments();
        Assert.assertEquals(COMMENT, comments.get(0));
    }

    /**
     * Test removing comments to document.
     */
    @Test(dependsOnMethods = "addComments")
    public void removeComment() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====removeComment====");
        DocumentDetailsPage docsPage = drone.getCurrentPage().render();
        Assert.assertEquals(docsPage.getCommentCount(), 1);
        docsPage.removeComment(COMMENT);
        DocumentLibraryPage libPage = docsPage.getSiteNav().selectSiteDocumentLibrary().render();
        int commentCount = libPage.getCommentCount();
        Assert.assertEquals(commentCount, 0);
    }

    @Test(dependsOnMethods = "removeComment")
    public void downloadFile() throws IOException
    {
        DocumentDetailsPage docDetailsPage = selectDocument(file).render();
        newfile = File.createTempFile(fileName, fileExt);
        newfile.createNewFile();

        docDetailsPage = docDetailsPage.selectDownload(newfile).render();
        long fileSize = newfile.length();
        if (fileSize < 1) saveScreenShot("DocumentDetailsPageTest.downloadFile");
        Assert.assertTrue(fileSize > 0);
        Assert.assertTrue(docDetailsPage.isDocumentDetailsPage());

        String size = docDetailsPage.getDocumentSize();
        Assert.assertEquals(size, "33 bytes");
    }

    /**
     * This test case needs flash player installed on linux box and till that it will be disabled. Test that selected document is previewed on detail page.
     * 
     * @throws IOException
     */
    //TODO Disbaled since windows OS selenium node is used with grid
    @Test(dependsOnMethods = "downloadFile", enabled= false)
    public void testIsPreviewDisplayed() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====testIsPreviewDisplayed====");

        DocumentDetailsPage docDetailsPage = drone.getCurrentPage().render();
        Assert.assertTrue(docDetailsPage.isFlashPreviewDisplayed());
    }
    
    /**
     * Test updating an existing file with a new uploaded file.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "downloadFile")
    public void deleteAnExistingFile()
    {
        if (logger.isTraceEnabled()) logger.trace("====deleteAnExistingFile====");
        DocumentDetailsPage docDetailsPage = drone.getCurrentPage().render();
        DocumentLibraryPage docLibPage = docDetailsPage.delete().render();
        Assert.assertFalse(docLibPage.isFileVisible(file.getName()));
    }

    /**
     * Test that selected document is not previewed on detail page
     * 
     * @throws IOException
     */
    @Test(dependsOnMethods = "deleteAnExistingFile")
    public void testIsNoPreviewMessageDisplayed() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====testIsNoPreviewMessageDisplayed====");
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        file = SiteUtil.prepareFile("UnkownFormat");
        upLoadPage.uploadFile(file.getCanonicalPath()).render();
        DocumentDetailsPage docDetailsPage = selectDocument(file).render();
        EditDocumentPropertiesPage editPropertiesPage = docDetailsPage.selectEditProperties().render();
        editPropertiesPage.selectMimeType(MimeType.AlfrescContentPackage);
        docDetailsPage = editPropertiesPage.selectSave().render();

        Assert.assertTrue(docDetailsPage.isNoPreviewMessageDisplayed());
    }

    /**
     * Test that selected document is not previewed and download link should be present on detail page
     * 
     * @throws IOException
     */
    @Test(dependsOnMethods = "testIsNoPreviewMessageDisplayed")
    public void testClickOnDownloadLinkForUnsupportedPreview() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DocumentDetailsPage page = drone.getCurrentPage().render();
            DocumentDetailsPage docDetailsPage = page.clickOnDownloadLinkForUnsupportedDocument().render();
            Assert.assertNotNull(docDetailsPage);
        }
    }

    /**
     * Tests is the document is shared and that share pane is not present for cloud
     * 
     */
    @Test(dependsOnMethods = "testClickOnDownloadLinkForUnsupportedPreview")
    public void testIsFileShared()
    {
        DocumentDetailsPage page = drone.getCurrentPage().render();
        if (alfrescoVersion.isCloud())
        {
            assertFalse(page.isSharePanePresent()); 
        }           
        else
        {
            assertTrue(page.isSharePanePresent()); 
        }
        assertFalse(page.isFileShared());
        ShareLinkPage shareLinkPage = page.clickShareLink().render();
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(page.isFileShared());
    }

    /**
     * Test for cover isCheckedOut() method
     */
    @Test(dependsOnMethods = "testIsFileShared")
    public void editOffline() throws IOException {
        DocumentDetailsPage docDetailsPage = drone.getCurrentPage().render();
        if (logger.isTraceEnabled()) logger.trace("====editOffline====");
        docDetailsPage.selectEditOffLine(null).render();
        Assert.assertTrue(docDetailsPage.isCheckedOut());

        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        if (logger.isTraceEnabled()) logger.trace("---selected new version to upload----");
        updatePage.selectMinorVersionChange();
        updatePage.setComment("Reloading the file with correct image");
        updatePage.uploadFile(uploadFile.getCanonicalPath());
        updatePage.render();
        docDetailsPage = updatePage.submit().render();
        if (logger.isTraceEnabled()) logger.trace("---upload submited----");

        Assert.assertFalse(docDetailsPage.isCheckedOut());
        if (logger.isTraceEnabled()) logger.trace("---update with major version----");
        docDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    /**
     * Test the function of get document body - the content of the document
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "editOffline")
    public void getDocumentBody() throws Exception
    {
        DocumentLibraryPage libraryPage = drone.getCurrentPage().render();
        CreatePlainTextContentPage contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        String content = "Content Test Doc";
        contentDetails.setContent(content);
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        assertEquals(detailsPage.getDocumentBody(), content);
    }
}
