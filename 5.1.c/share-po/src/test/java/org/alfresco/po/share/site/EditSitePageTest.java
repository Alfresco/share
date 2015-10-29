package org.alfresco.po.share.site;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;

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
        siteUtil.createSite(driver, username, password, siteName,"", "Public");
        
    }

    @Test(groups="unit")
    public void testPage()
    {
        EditSitePage page = new EditSitePage();
        Assert.assertNotNull(page);
    }

    @Test(groups = "alfresco-one")
    public void testSelectSiteVisibility()
    {
        SiteFinderPage siteFinder = siteUtil.searchSite(driver, siteName);
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        EditSitePage siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(!siteDetails.isPrivate());
        siteDetails.selectSiteVisibility(true, false);
        siteDetails.selectOk();
        siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
    }
}
