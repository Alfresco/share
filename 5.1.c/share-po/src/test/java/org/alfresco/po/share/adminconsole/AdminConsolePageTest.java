package org.alfresco.po.share.adminconsole;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;

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
        resolvePage(driver).render();

    }

    @Test(dependsOnMethods = "checkThatFactoryReturnApplicationPage", groups = "Enterprise-only", alwaysRun = true)
    public void selectThemeTest() throws Exception
    {
        adminConsolePage = resolvePage(driver).render();
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.googleDocs).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.googleDocs));
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.light).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.light));


    }

    @Test(dependsOnMethods = "selectThemeTest", groups = "Enterprise-only", alwaysRun = true)
    public void uploadPictureTest() throws IOException, TimeoutException, InterruptedException
    {
        adminConsolePage = resolvePage(driver).render();

        // negative test, that it is impossible to upload not picture file (for example, txt)
        File pic = siteUtil.prepareFile("ff.txt");
        String srcBeforeUpload = driver.findElement(AdminConsolePage.LOGO_PICTURE).getAttribute("src");
        adminConsolePage.uploadPicture(pic.getCanonicalPath());
        String srcAfterUpload = driver.findElement(AdminConsolePage.LOGO_PICTURE).getAttribute("src");

        assertEquals(srcBeforeUpload, srcAfterUpload);
    }

}
