package org.alfresco.po.share.console;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the Cloud Console Pages.
 *
 * @author Dmitry Yukhnovets
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class CloudConsoleDashboardTest extends AbstractCloudConsoleTest
{
    @BeforeMethod(groups = { "Cloud2" })
    public void beforeTest() throws Exception
    {
        //cloudConsolePage = new CloudConsolePage(drone);
        if (cloudConsolePage.isLoggedToCloudConsole())
        {
            cloudConsolePage = cloudConsolePage.logOutFromCloudConsole().render();
        }
    }

    @Test(groups = { "Cloud2" })
    public void openCloudConsoleDashboardPage()
    {
        CloudConsoleDashboardPage cloudConsoleDashboardPage = cloudConsolePage.openCloudConsole(consoleUrl)
                .loginAs(USERNAME, PASSWORD)
                .openDashboardPage().render();
        Assert.assertTrue(cloudConsoleDashboardPage.isDashboardOpened());

    }
}
