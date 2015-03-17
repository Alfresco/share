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
public class CloudConsoleTest extends AbstractCloudConsoleTest
{

    @BeforeMethod(groups = { "Cloud2" })
    public void beforeTest() throws Exception
    {
        cloudConsolePage.openCloudConsole(consoleUrl);
        if (cloudConsolePage.isLoggedToCloudConsole())
        {
            cloudConsolePage = cloudConsolePage.logOutFromCloudConsole().render();
        }
    }

    @Test(groups = { "Cloud2" })
    public void openCloudConsolePage()
    {
        cloudConsolePage.openCloudConsole(consoleUrl).render();
        Assert.assertTrue(cloudConsolePage.isTitlePresent());
    }

    @Test(groups = { "Cloud2" })
    public void loginToCloudConsole()
    {
        cloudConsolePage.openCloudConsole(consoleUrl).loginAs(USERNAME, PASSWORD);
        Assert.assertTrue(cloudConsolePage.isLoggedToCloudConsole());
    }

    @Test(groups = { "Cloud2" })
    public void logOutFromCloudConsole()
    {
        cloudConsolePage.openCloudConsole(consoleUrl).loginAs(USERNAME, PASSWORD).logOutFromCloudConsole();
        Assert.assertFalse(cloudConsolePage.isLoggedToCloudConsole());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups = "Cloud2")
    public void searchQueryIsEmpty()
    {
        cloudConsolePage.openCloudConsole(consoleUrl).loginAs(USERNAME, PASSWORD).executeSearch(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, groups = "Cloud2")
    public void logInWithOutCredentials()
    {
        cloudConsolePage.openCloudConsole(consoleUrl).loginAs("", null);
    }

}
