package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test web quick start dashlet page elements.
 * 
 * @author Cristina Axinte
 * 
 */
@Test(groups = { "check", "alfresco-one" })
@Listeners(FailedTestListener.class)
public class SiteWebQuickStartDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SiteWebQuickStartDashlet wqsDashlet = null;

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");

    }

    @AfterClass
    public void deleteSite()
    {
        // SiteUtil.deleteSite(drone, siteName);
    }
    
    @BeforeMethod
    public void siteAndWQSDashletSetup()
    {
         siteName= this.getClass().getSimpleName()+ System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
         SitePage site = drone.getCurrentPage().render();
         customiseSiteDashBoard = site.getSiteNav().selectCustomizeDashboard().render();
         siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.WEB_QUICK_START, 1).render();
         wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
    }

    @Test
    public void instantiateDashlet()
    {
        SiteWebQuickStartDashlet wqsDashlet = new SiteWebQuickStartDashlet(drone);
        Assert.assertNotNull(wqsDashlet);
    }
    
    @Test
    public void clickImportButttonPositive()
    {
        drone.getCurrentPage().render();
        wqsDashlet.clickImportButtton();
        assertNotNull(wqsDashlet);

    }

    @Test
    public void verifyIsWQSHelpLinkDisplayedPositive()
    {
        SitePage site = drone.getCurrentPage().render();
        wqsDashlet.clickImportButtton();
        site = drone.getCurrentPage().render();
        site.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(wqsDashlet.isWQSHelpLinkDisplayed());
    }

    @Test
    public void getSelectedWebsiteDataPositive()
    {
        drone.getCurrentPage().render();
        assertEquals(wqsDashlet.getSelectedWebsiteData(), "Finance");
    }

    @Test
    public void selectWebsiteDataOptionPositive() throws Exception
    {
        drone.getCurrentPage().render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.GOVERNMENT);
        wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        assertEquals(wqsDashlet.getSelectedWebsiteData(), "Government");
    }

    @Test
    public void verifyIsWQSHelpLinkDisplayedNegative()
    {
        drone.getCurrentPage().render();
        assertFalse(wqsDashlet.isWQSHelpLinkDisplayed());
    }

}
