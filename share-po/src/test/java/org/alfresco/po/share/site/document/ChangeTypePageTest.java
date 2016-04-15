package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups="Enterprise-only")
public class ChangeTypePageTest extends AbstractTest
{

    private FolderDetailsPage folderDetailsPage;
    private DocumentLibraryPage documentLibPage;
    private String siteName;
    private ChangeTypePage changeTypePage;

    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        String folderName = "The first folder";
        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        documentLibPage = ((SitePage) resolvePage(driver).render()).getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderName).render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        folderDetailsPage = thisRow.selectViewFolderDetails().render();
        changeTypePage = folderDetailsPage.selectChangeType().render();
    }

    @AfterClass
    public void teardown() throws Throwable
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(priority = 0)
    public void getTypesTest() throws Throwable
    {
        List<String> types = changeTypePage.getTypes();
        assertTrue(types.contains("Select type..."));
    }

    @Test(dependsOnMethods = "getTypesTest")
    public void isChangeTypeDisplayedTest()
    {
        assertTrue(changeTypePage.isChangeTypeDisplayed(), "The dialog should be displyed");
    }

    @Test(dependsOnMethods = "isChangeTypeDisplayedTest")
    public void selectCancelTest() throws Throwable
    {
        folderDetailsPage = changeTypePage.selectCancel().render();
        assertTrue(folderDetailsPage.isBrowserTitle("Folder Details"), "The dialog should be displyed");
    }
}
