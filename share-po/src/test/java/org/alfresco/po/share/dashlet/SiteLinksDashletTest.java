package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Site Links dashlet web elements
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class SiteLinksDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_LINKS_DASHLET = "site-links";
    private SiteLinksDashlet siteLinksDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows links relevant to this site. The list is compiled by site members. Clicking a link opens it in a new window.";
    LinksDetailsPage linksDetailsPage = null;

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "siteLinksDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_LINKS, 1).render();
        siteLinksDashlet = siteDashBoard.getDashlet(SITE_LINKS_DASHLET).render();
        assertNotNull(siteLinksDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon()
    {
        siteLinksDashlet.clickOnHelpIcon();
        assertTrue(siteLinksDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = siteLinksDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        siteLinksDashlet.closeHelpBallon();
        assertFalse(siteLinksDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyLinksCount()
    {
        assertEquals(siteLinksDashlet.getLinksCount(), 0);
    }

    @Test(dependsOnMethods = "verifyLinksCount")
    public void verifyIsLinkDisplayed()
    {
        assertFalse(siteLinksDashlet.isLinkDisplayed("no link azazaza!"));
    }

    @Test(dependsOnMethods = "verifyIsLinkDisplayed")
    public void createLinkFromDashlet()
    {
        linksDetailsPage = siteLinksDashlet.createLink("name", "google.com");
        assertNotNull(linksDetailsPage);
    }

    @Test(dependsOnMethods = "createLinkFromDashlet")
    public void secondVerify()
    {
        navigateToSiteDashboard();
        assertEquals(siteLinksDashlet.getLinksCount(), 1);
        assertTrue(siteLinksDashlet.isLinkDisplayed("name"));
    }

}
