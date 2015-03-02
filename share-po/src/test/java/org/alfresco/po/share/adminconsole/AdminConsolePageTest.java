package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Antonik Olga
 */
@Listeners(FailedTestListener.class)
public class AdminConsolePageTest extends AbstractTest
{
    AdminConsolePage adminConsolePage;

    @Test(groups = "Enterprise-only")
    public void checkThatFactoryReturnApplicationPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getAdminConsolePage().render();
        drone.getCurrentPage().render();

    }

    @Test(dependsOnMethods = "checkThatFactoryReturnApplicationPage", groups = "Enterprise-only", alwaysRun = true)
    public void selectThemeTest() throws Exception
    {
        adminConsolePage = drone.getCurrentPage().render();
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.googleDocs).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.googleDocs));
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.light).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.light));


    }

    @Test(dependsOnMethods = "selectThemeTest", groups = "Enterprise-only", alwaysRun = true)
    public void uploadPictureTest() throws IOException, TimeoutException, InterruptedException
    {
        adminConsolePage = drone.getCurrentPage().render();

        // negative test, that it is impossible to upload not picture file (for example, txt)
        File pic = SiteUtil.prepareFile("ff.txt");
        String srcBeforeUpload = drone.find(AdminConsolePage.LOGO_PICTURE).getAttribute("src");
        adminConsolePage.uploadPicture(pic.getCanonicalPath());
        String srcAfterUpload = drone.find(AdminConsolePage.LOGO_PICTURE).getAttribute("src");

        assertEquals(srcBeforeUpload, srcAfterUpload);
    }

}
