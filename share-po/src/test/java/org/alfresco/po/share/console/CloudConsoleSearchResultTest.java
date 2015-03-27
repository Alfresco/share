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
public class CloudConsoleSearchResultTest extends AbstractCloudConsoleTest
{
    @BeforeMethod(groups = { "Cloud2" })
    public void beforeTest() throws Exception
    {
        cloudConsolePage = cloudConsolePage.openCloudConsole(consoleUrl);
        if (cloudConsolePage.isLoggedToCloudConsole())
        {
            cloudConsolePage = cloudConsolePage.logOutFromCloudConsole().render();
        }
    }

    @Test(groups = { "Cloud2" })
    public void executeCloudConsoleSearch()
    {
        CloudConsoleSearchResultPage resultPage = cloudConsolePage.openCloudConsole(consoleUrl).loginAs(USERNAME, PASSWORD).executeSearch("alfresco.com")
                .render();
        Assert.assertTrue(resultPage.isVisibleResults());
    }

    @Test(groups = { "Cloud2" })
    public void executeCloudConsoleIncorrectSearch()
    {
        CloudConsoleSearchResultPage resultPage = cloudConsolePage.openCloudConsole(consoleUrl).loginAs(USERNAME, PASSWORD)
                .executeSearch("fake-searchzsdadahdskajhsdkahDqweqweq1234721423").render();

        Assert.assertFalse(resultPage.isVisibleResults());
    }
}
