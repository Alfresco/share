
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Content I'm Editing dashlet web elements
 * Created by olga Lokhach
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one", "ProductBug"})
public class EditingContentDashletTest extends AbstractSiteDashletTest
{
    private DashBoardPage dashBoardPage;
    private CustomiseUserDashboardPage customiseUserDashboardPage;
    private String userName;
    private DocumentLibraryPage documentLibPage;
    private EditingContentDashlet editingContentDashlet;
    private static final String EXP_HELP_BALLOON_MSG = "Check this dashlet to quickly see the items you are working on.";

    @BeforeClass
    public void setup()throws Exception
    {
        userName = "User_" + System.currentTimeMillis();
        siteName = "MySiteTests" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        File file = siteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
        FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOfflineAndCloseFileWindow();
    }

    @Test
    public void instantiateDashlet()
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();

        customiseUserDashboardPage = dashBoardPage.getNav().selectCustomizeUserDashboard();
        customiseUserDashboardPage.render();
        dashBoardPage = customiseUserDashboardPage.addDashlet(Dashlets.CONTENT_I_AM_EDITING, 1).render();
        editingContentDashlet = dashletFactory.getDashlet(driver, EditingContentDashlet.class).render();
        assertNotNull(editingContentDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        editingContentDashlet.clickOnHelpIcon();
        assertTrue(editingContentDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        String actualHelpBalloonMsg = editingContentDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        editingContentDashlet.closeHelpBallon();
        assertFalse(editingContentDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void isItemDisplayed() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(fileName, siteName), "Item is not found");
    }

    @Test(dependsOnMethods = "isItemDisplayed")
    public void clickItem() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        documentLibPage = editingContentDashlet.clickItem(fileName).render();
        assertNotNull(documentLibPage);
    }

    @Test(dependsOnMethods = "clickItem")
    public void clickSite() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        siteDashBoard = editingContentDashlet.clickSite(siteName).render();
        assertNotNull(siteDashBoard);
    }
}
