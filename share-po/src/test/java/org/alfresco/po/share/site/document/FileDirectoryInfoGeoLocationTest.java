package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.site.UploadFilePage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify File Directory info methods are operating correctly.
 * 
 * @author Ranjith Manyam
 * @since 5.0
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoGeoLocationTest extends AbstractDocumentTest
{

    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File testFile;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="Enterprise4.2")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        loginAs(username, password);

        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        testFile = siteUtil.prepareFile("testFile");
    }



    @AfterClass(groups="Enterprise4.2")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="Enterprise4.2")
    public void createData() throws Exception
    {
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testFile.getCanonicalPath()).render();
    }
}
