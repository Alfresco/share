package org.alfresco.po.share.site.document.download;


import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups="download")
public class DownloadFileAndFolderTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibraryPage;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        SitePage page = resolvePage(driver).render();
        documentLibraryPage = page.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
    }

    @AfterClass
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test
    public void downloadTextFile() throws Exception
    {
        CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails details = new ContentDetails();
        details.setName("TextFile");
        DocumentDetailsPage detailsPage = contentPage.create(details).render();
        documentLibraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        FileDirectoryInfo row = documentLibraryPage.getFileDirectoryInfo("TextFile");
        row.selectDownload();
        documentLibraryPage.waitForFile(downloadDirectory + "TextFile");
        documentLibraryPage.render();
    }

    @Test(dependsOnMethods="downloadTextFile")
    public void uploadFile() throws Exception
    {
        documentLibraryPage.getFileDirectoryInfo(folderName).selectDownloadFolderAsZip();
        documentLibraryPage.waitForFile(downloadDirectory + folderName + ".zip");
        documentLibraryPage.render();
    }
    
}
