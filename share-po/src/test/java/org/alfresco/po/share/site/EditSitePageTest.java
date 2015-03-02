package org.alfresco.po.share.site;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = "alfresco-one")
public class EditSitePageTest extends AbstractTest
{
    private String siteName;

    @BeforeClass(groups = "alfresco-one")
    public void setup() throws Exception
    {
        siteName = String.format("test-%d-editSite", System.currentTimeMillis());

        DashBoardPage dashBoard = loginAs(username, password);
        dashBoard.render();
        SiteUtil.createSite(drone, siteName, "Public");
        
    }

    @Test(groups="unit")
    public void testPage()
    {
        EditSitePage page = new EditSitePage(drone);
        Assert.assertNotNull(page);
    }

    @Test(groups = "alfresco-one")
    public void testSelectSiteVisibility()
    {
        SiteFinderPage siteFinder = SiteUtil.searchSite(drone, siteName);
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        EditSitePage siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(!siteDetails.isPrivate());
        siteDetails.selectSiteVisibility(true, false);
        siteDetails.selectOk();
        siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
    }
}
