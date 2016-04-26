package org.alfresco.po.share.site;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class Tests the common functionalities in NewFolderPage
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class NewFolderPageTest extends AbstractTest
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static DocumentLibraryPage documentLibPage;

    /**
     * Pre test setup: Site creation, file upload, folder creation
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====prepare====");
        }
        
        siteName = "site" + System.currentTimeMillis();


        shareUtil.loginAs(driver, shareUrl, username, password).render();
        
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(groups = { "alfresco-one" })
    public void createNewFolderWithValidation() throws Exception
    {
        String folderName = "New Folder";

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        newFolderPage = newFolderPage.createNewFolderWithValidation("", folderName, folderName).render();
        Assert.assertTrue(newFolderPage.getMessage(NewFolderPage.Fields.NAME).length() > 0);
        documentLibPage = newFolderPage.selectCancel().render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolderWithValidation(folderName, folderName, folderName).render();
        Assert.assertNotNull(documentLibPage);

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolderWithValidation(folderName + "-1").render();
        Assert.assertNotNull(documentLibPage);
    }
}
