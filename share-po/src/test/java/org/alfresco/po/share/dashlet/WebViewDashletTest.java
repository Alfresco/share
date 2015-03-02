package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Web View dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class WebViewDashletTest extends AbstractSiteDashletTest
{
    private static final String WEB_VIEW_DASHLET = "web-view";
    private WebViewDashlet webViewDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private ConfigureWebViewDashletBoxPage configureWebViewDashletBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the website of your choice. Click the edit icon on the dashlet to change the web address.\n"
            + "Clicking the dashlet title opens the website in a separate window.";
    private static final String EXP_DEFAULT_DASHLET_MSG = "No web page to display.";
    private static final String EXTERNAL_SITE_URL = "http://electrictower.ru/";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "webViewDashletTest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test(groups = "Enterprise-only")
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.WEB_VIEW, 1).render();
        webViewDashlet = siteDashBoard.getDashlet(WEB_VIEW_DASHLET).render();
        assertNotNull(webViewDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "instantiateDashlet")
    public void verifyDefaultMessage()
    {
        String defaultMessage = webViewDashlet.getDefaultMessage();
        assertEquals(defaultMessage, EXP_DEFAULT_DASHLET_MSG);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyDefaultMessage")
    public void verifyHelpIcon()
    {
        webViewDashlet.clickOnHelpIcon();
        assertTrue(webViewDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = webViewDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        webViewDashlet.closeHelpBallon();
        assertFalse(webViewDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyHelpIcon")
    public void checkConfigureIcon()
    {
        configureWebViewDashletBoxPage = webViewDashlet.clickConfigure();
        assertNotNull(configureWebViewDashletBoxPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkConfigureIcon")
    public void configExternalSite()
    {
        configureWebViewDashletBoxPage.config(EXTERNAL_SITE_URL, EXTERNAL_SITE_URL);
        int i = 0;
        for (; i < 20; i++)
        {
            try
            {
                webViewDashlet.getDefaultMessage();
            }
            catch (Exception e)
            {
                break;
            }
        }
        assertTrue(i == 20);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "configExternalSite")
    public void verifyIsFrameShow()
    {
        assertTrue(webViewDashlet.isFrameShow(EXTERNAL_SITE_URL));
    }

}
