package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.HtmlPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Functional test to test View Properties Page
 * 
 * @author Maryia Zaichanka
 */
@Listeners(FailedTestListener.class)
public class ViewPropertiesPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(ViewPropertiesPageTest.class);

    private DashBoardPage dashBoard;
    private DocumentLibraryPage documentLibPage;
    private DocumentDetailsPage docDetailsPage;
    private ViewPropertiesPage viewPropPage;

    private String siteName;
    private static String fileName;
    private String v = "1.0";

    @BeforeClass(groups = { "Enterprise4.2"})
    public void setup() throws Exception
    {
        siteName = "site-" + System.currentTimeMillis();
        fileName = "File";

        dashBoard = loginAs(username, password);
        dashBoard = dashBoard.getNav().selectMyDashBoard().render();

        SiteUtil.createSite(drone, siteName, "description", "Public");

        // Select DocLib
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        // Upload File
        File file = SiteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();

        // Open Details Page of the uploaded file
        docDetailsPage = documentLibPage.selectFile(fileName).render();

        // Upload new version
        UpdateFilePage updateFilePage = docDetailsPage.selectUploadNewVersion().render();

        updateFilePage.selectMajorVersionChange();
        updateFilePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updateFilePage.submit().render();

    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void resolveViewPropDialogue() throws Exception
    {
        docDetailsPage.selectViewProperties(v).render();
        viewPropPage = FactorySharePage.resolvePage(drone).render();
        Assert.assertTrue(viewPropPage.isVersionButtonDisplayed());

        String title = viewPropPage.getVersionButtonTitle();
        viewPropPage = FactorySharePage.resolvePage(drone).render();
        Assert.assertNotNull(viewPropPage);

        logger.info("Version button title: " + title);

    }

    @Test(dependsOnMethods = "resolveViewPropDialogue")
    public void selectOtherVersionWindow() throws Exception
    {
        String versionTitle = viewPropPage.getVersionButtonTitle();
        viewPropPage.selectOtherVersion(false);
        String otherVersionTitle = viewPropPage.getVersionButtonTitle();
        viewPropPage = FactorySharePage.resolvePage(drone).render();
        Assert.assertNotNull(viewPropPage);
        Assert.assertNotSame(versionTitle, otherVersionTitle);

        logger.info("Version button title: " + viewPropPage.getVersionButtonTitle());
    }

    @Test(dependsOnMethods = "selectOtherVersionWindow")
    public void closeCreateSiteDialogue() throws Exception
    {
        docDetailsPage = closeDialogue().render();
    }

    public HtmlPage closeDialogue() throws Exception
    {
        ViewPropertiesPage dialogue = FactorySharePage.resolvePage(drone).render();
        HtmlPage sharePage = dialogue.closeDialogue().render();

        Assert.assertNotNull(sharePage);
        return sharePage;
    }
}
