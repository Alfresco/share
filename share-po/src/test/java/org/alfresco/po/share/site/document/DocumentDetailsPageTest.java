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
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.alfresco.connect.exception.AlfrescoException;
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
@Test(groups = { "alfresco-one" })
@SuppressWarnings("unused")
public class DocumentDetailsPageTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(DocumentDetailsPageTest.class);
    private static final String COMMENT = "adding a comment to document is easy!!.";
    private static final String EDITED_COMMENT = "editing a comment is even easier!";
    private String zipFile = "ZipFile" + System.currentTimeMillis();
    private String acpFile = "AcpFile" + System.currentTimeMillis();
    private String zipFilePrepared = "";
    private String acpFilePrepared = "";
    private final String ZIPPED_TXT_FILE_NAME = zipFile + ".txt";
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
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        siteName = "ddSiteTest" + System.currentTimeMillis();
        loginAs(username, password);
        // PageUtils.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        file = siteUtil.prepareFile();
        uploadFile = siteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        fileExt = st.nextToken();
    }

    @AfterMethod(groups = { "alfresco-one" })
    public void deleteFile()
    {
        if (newfile != null)
        {
            newfile.delete();
        }
    }

    @AfterClass(groups = { "alfresco-one" })
    public void deleteSite()
    {
        try
        {
            siteUtil.deleteSite(username, password, siteName);
        }
        catch(AlfrescoException ex){}
        
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
        if (logger.isTraceEnabled())
        {
            logger.trace("====uploadFile====");
        }
        SitePage site = (SitePage) resolvePage(driver);
        site.render();
        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
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
    // Grouped as bug Due to https://issues.alfresco.com/jira/browse/ACE-1628 ACE Bug
    @Test(dependsOnMethods = "uploadFile", groups = "ACEBug", enabled = false)
    public void minorVersionUpdateOfAnExistingFile() throws Exception
    {
        DocumentDetailsPage docDetailsPage = selectDocument(file).render();
        if (logger.isTraceEnabled())
        {
            logger.trace("====updateAnExistingFile====");
        }
        // Assert the current file exists and is version 1
        Assert.assertEquals(file.getName(), docDetailsPage.getDocumentTitle());
        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.0");
        if (logger.isTraceEnabled())
        {
            logger.trace("---update with minor version----");
        }
        // Update file with a minor version
        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        if (logger.isTraceEnabled())
        {
            logger.trace("---selected new version to upload----");
        }
        updatePage.selectMinorVersionChange();
        updatePage.setComment("Reloading the file with correct image");
        updatePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updatePage.submitUpload().render();
        if (logger.isTraceEnabled())
        {
            logger.trace("---upload submited----");
        }
        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.1");
        Assert.assertEquals("Reloading the file with correct image", docDetailsPage.getCommentsOfLastCommit());
        if (logger.isTraceEnabled())
        {
            logger.trace("---update with major version----");
        }
    }

    // Grouped as bug Due to https://issues.alfresco.com/jira/browse/ACE-1628 ACE Bug
    @Test(dependsOnMethods = "minorVersionUpdateOfAnExistingFile", groups = "ACEBug", enabled = false)
    public void majorVersionUpdateOfAnExistingFile() throws Exception
    {
        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
        // Update file with a major version change
        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        updatePage.selectMajorVersionChange();
        updatePage.setComment("Reloading the final image");
        updatePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updatePage.submitUpload().render();
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
    	DocumentDetailsPage docsPage = selectDocument(file).render();
        if (logger.isTraceEnabled())
        {
            logger.trace("====addLikeDislike====");
        }

        Assert.assertEquals("0", docsPage.getLikeCount());
        Assert.assertFalse(docsPage.isLiked());
        
        docsPage = docsPage.selectLike().render();
        Assert.assertEquals("1", docsPage.getLikeCount());
        Assert.assertTrue(docsPage.isLiked());
        Assert.assertNotNull(docsPage.getToolTipForLike());
        
        docsPage = docsPage.selectUnlike().render();
        Assert.assertEquals("0", docsPage.getLikeCount());
        Assert.assertFalse(docsPage.isLiked());
        
        Assert.assertNotNull(docsPage.getToolTipForFavourite());
    }

    @Test(dependsOnMethods = "addLikeDislike")
    public void favouriteUnfavourite() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====favouriteUnfavourite====");
        }
        
        DocumentDetailsPage docsPage = resolvePage(driver).render();
        
        Assert.assertFalse(docsPage.isFavourite());
        Assert.assertNotNull(docsPage.getToolTipForFavourite());
        
        docsPage = docsPage.selectFavourite().render();
        Assert.assertTrue(docsPage.isFavourite());
    }

    @Test(dependsOnMethods = "favouriteUnfavourite")
    public void getDocumentProperties()
    {
        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
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
        if (logger.isTraceEnabled())
        {
            logger.trace("====addComments====");
        }
        DocumentDetailsPage docsPage = resolvePage(driver).render();
        
        Assert.assertEquals(docsPage.getCommentCount(), 0);
        docsPage = docsPage.addComment(COMMENT).render();
        
        DocumentLibraryPage libPage = docsPage.getSiteNav().selectDocumentLibrary().render();
        
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
        if (logger.isTraceEnabled())
        {
            logger.trace("====removeComment====");
        }
        DocumentDetailsPage docsPage = resolvePage(driver).render();
        
        Assert.assertEquals(docsPage.getCommentCount(), 1);
        docsPage.removeComment(COMMENT);
        
        DocumentLibraryPage libPage = docsPage.getSiteNav().selectDocumentLibrary().render();
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
        if (fileSize < 1)
            saveScreenShot("DocumentDetailsPageTest.downloadFile");
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
    // TODO Disbaled since windows OS selenium node is used with grid
    @Test(dependsOnMethods = "downloadFile", enabled = false)
    public void testIsPreviewDisplayed() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====testIsPreviewDisplayed====");
        }
        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
        Assert.assertTrue(docDetailsPage.isFlashPreviewDisplayed());
    }

    /**
     * Test updating an existing file with a new uploaded file.
     */
    @Test(dependsOnMethods = "downloadFile")
    public void deleteAnExistingFile()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====deleteAnExistingFile====");
        }
        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
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
        if (logger.isTraceEnabled())
        {
            logger.trace("====testIsNoPreviewMessageDisplayed====");
        }
        SitePage site = resolvePage(driver).render();
        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        file = siteUtil.prepareFile("UnkownFormat");
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
        DocumentDetailsPage page = resolvePage(driver).render();
        DocumentDetailsPage docDetailsPage = page.clickOnDownloadLinkForUnsupportedDocument().render();
        Assert.assertNotNull(docDetailsPage);
    }

    /**
     * Tests is the document is shared and that share pane is not present for cloud
     */
    @Test(dependsOnMethods = "testClickOnDownloadLinkForUnsupportedPreview")
    public void testIsFileShared()
    {
        DocumentDetailsPage page = resolvePage(driver).render();
        assertTrue(page.isSharePanePresent());
        assertFalse(page.isFileShared());
        ShareLinkPage shareLinkPage = page.clickShareLink().render();
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(page.isFileShared());
    }

    /**
     * Test for cover isCheckedOut() method
     */
    @Test(dependsOnMethods = "testIsFileShared")
    public void editOffline() throws IOException
    {
        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
        if (logger.isTraceEnabled())
        {
            logger.trace("====editOffline====");
        }
        docDetailsPage = docDetailsPage.selectEditOffLine(null).render();
        Assert.assertTrue(docDetailsPage.isCheckedOut());

        UpdateFilePage updatePage = docDetailsPage.selectUploadNewVersion().render();
        if (logger.isTraceEnabled())
        {
            logger.trace("---selected new version to upload----");
        }
        updatePage.selectMinorVersionChange();
        updatePage.setComment("Reloading the file with correct image");
        updatePage.uploadFile(uploadFile.getCanonicalPath());
        updatePage = updatePage.render();
        docDetailsPage = updatePage.submitUpload().render();
        if (logger.isTraceEnabled())
        {
            logger.trace("---upload submited----");
        }

        Assert.assertFalse(docDetailsPage.isCheckedOut());
        if (logger.isTraceEnabled())
        {
            logger.trace("---update with major version----");
        }
        docDetailsPage.getSiteNav().selectDocumentLibrary().render();
    }

    /**
     * Test for Unzip to... link for zip file
     */
    @Test(dependsOnMethods = "editOffline")
    public void unzipZipFileTo() throws Exception
    {
        DocumentLibraryPage docLibraryPage = resolvePage(driver).render();

        File prepareZipFile = siteUtil.prepareZipFile(zipFile, ".zip");
        UploadFilePage upLoadPage = docLibraryPage.getNavigation().selectFileUpload().render();
        docLibraryPage = upLoadPage.uploadFile(prepareZipFile.getCanonicalPath()).render();
        zipFilePrepared = prepareZipFile.getName();

        DocumentDetailsPage docDetailsPage = docLibraryPage.selectFile(zipFilePrepared).render();
        CopyOrMoveContentPage copyOrMoveContentPage = docDetailsPage.selectUnzipTo().render();
        copyOrMoveContentPage.selectOkButton().render();

        docLibraryPage = docDetailsPage.getSiteNav().selectDocumentLibrary().render();
        Assert.assertTrue(docLibraryPage.isItemVisble(ZIPPED_TXT_FILE_NAME));

    }


    /**
     * Test for Unzip to... link for acp file
     */
    @Test(dependsOnMethods = "unzipZipFileTo")
    public void unzipAcpFileTo() throws Exception
    {
        DocumentLibraryPage docLibraryPage = resolvePage(driver).render();


        File prepareAcpFile = siteUtil.prepareZipFile(acpFile, ".acp");

        UploadFilePage upLoadPage = docLibraryPage.getNavigation().selectFileUpload().render();
        docLibraryPage = upLoadPage.uploadFile(prepareAcpFile.getCanonicalPath()).render();
        acpFilePrepared = prepareAcpFile.getName();

        DocumentDetailsPage docDetailsPage = docLibraryPage.selectFile(acpFilePrepared).render();
        CopyOrMoveContentPage copyOrMoveContentPage = docDetailsPage.selectUnzipTo().render();
        copyOrMoveContentPage.selectOkButton().render();

        docLibraryPage = docDetailsPage.getSiteNav().selectDocumentLibrary().render();
        Assert.assertTrue(docLibraryPage.isItemVisble(ZIPPED_TXT_FILE_NAME));

    }


    /**
     * Test the function of get document body - the content of the document
     *
     * @throws Exception
     */
    //@Test(dependsOnMethods = "editOffline", groups = "communityIssue")
    @Test(dependsOnMethods = "unzipAcpFileTo", groups = "communityIssue")
    public void getDocumentBody() throws Exception
    {
        DocumentLibraryPage libraryPage = resolvePage(driver).render();
        CreatePlainTextContentPage contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        String content = "Content Test Doc";
        contentDetails.setContent(content);
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        assertEquals(detailsPage.getDocumentBody(), content);
        detailsPage.getSiteNav().selectDocumentLibrary().render();
    }

    /**
     * Test the function of view original document
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "getDocumentBody", groups = "communityIssue")
    public void testViewOriginalDocument() throws Exception
    {
        DocumentLibraryPage libraryPage = resolvePage(driver).render();
        libraryPage = libraryPage.getFileDirectoryInfo("Test Doc").selectEditOffline().render();
        DocumentEditOfflinePage docEditPage = libraryPage.selectFileEditedOffline("Test Doc").render();
        assertTrue(docEditPage.isViewOriginalLinkPresent());
        DocumentDetailsPage docDetailsPage = docEditPage.selectViewOriginalDocument().render();
        assertFalse(docDetailsPage.isViewOriginalLinkPresent());
        assertTrue(docDetailsPage.isViewWorkingCopyDisplayed());
        docDetailsPage.getSiteNav().selectDocumentLibrary().render();
    }

    /**
     * Test the function of view original document
     *
     * @throws Exception
     */
     /**
    @Test(dependsOnMethods = "testViewOriginalDocument", groups = "communityIssue")
    public void testGetCommentHtml() throws Exception
    {
        DocumentLibraryPage libraryPage = resolvePage(driver).render();
        libraryPage = libraryPage.getFileDirectoryInfo("Test Doc").selectCancelEditing().render();
        DocumentDetailsPage docDetailsPage = libraryPage.selectFile("Test Doc").render();
        AddCommentForm addCommentForm = docDetailsPage.clickAddCommentButton();
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText("comment");
        addCommentForm.clickAddCommentButton().render();
        String htmlComment = docDetailsPage.getCommentHTML("comment");
        assertFalse(htmlComment.isEmpty());
    }
    **/
}
