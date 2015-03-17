package org.alfresco.po.share.console;

import org.alfresco.po.share.AbstractTest;
import org.testng.annotations.BeforeClass;

/**
 * Integration test to validate the Cloud Console Pages.
 *
 * @author Dmitry Yukhnovets
 * @since 1.7
 */
public class AbstractCloudConsoleTest extends AbstractTest
{
    public final String USERNAME = "automationteam";
    public final String PASSWORD = "wR5qiqNY";
    public static String consoleUrl = "";
    public CloudConsolePage cloudConsolePage;
    /**
    * Parse url for Cloud Console for dp and STAG envs
    */
    @BeforeClass(alwaysRun=true, groups = { "Cloud2" })
    public void prepare() throws Exception
    {
        cloudConsolePage = new CloudConsolePage(drone);
        consoleUrl=cloudConsolePage.getCloudConsoleUrl(shareUrl);

    }
}
