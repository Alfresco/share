/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteFinderPage.ButtonType;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Meenal Bhave
 * @since 1.7.Dev
 */
@Listeners(FailedTestListener.class)
@Test(groups="alfresco-one")
public class SiteFinderPageTest extends AbstractTest
{
    private static String siteName, siteNamePublic, siteNameModerated, siteNamePrivate;
    private static SiteFinderPage siteFinder;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        siteNamePublic = siteName + "-2";
        siteNameModerated = siteName + "mod";
        siteNamePrivate = siteName + "private";

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SiteUtil.createSite(drone, siteNamePublic, "description", "Public");
        SiteUtil.createSite(drone, siteNameModerated, "description", "Moderated");
        SiteUtil.createSite(drone, siteNamePrivate, "description", "Private");
    }
    
    @BeforeMethod
    public void getSiteFinder()
    {
        siteFinder = getSiteFinderPage();
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteNamePublic);
        SiteUtil.deleteSite(drone, siteNameModerated);
        SiteUtil.deleteSite(drone, siteNamePrivate);
    }
    
    private SiteFinderPage getSiteFinderPage()
    {
        // Navigate to Search For Site
        SharePage page = drone.getCurrentPage().render(); 
        siteFinder = page.getNav().selectSearchForSites().render(); 
        return siteFinder;        
    }
    
    @Test(priority=1)
    public void test100zeroResults() throws IOException
    {
        Assert.assertEquals(siteFinder.hasResults(), false);
        siteFinder = siteFinder.searchForSite(siteName + System.currentTimeMillis()).render();
        Assert.assertEquals(siteFinder.hasResults(), false);
        Assert.assertEquals(siteFinder.getSiteList().size(), 0);
        this.saveScreenShot("Prepare");
    }
    
    @Test(priority=2)
    public void test101nonZeroResults()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName).render();
        Assert.assertEquals(siteFinder.hasResults(), true); 
        Assert.assertTrue(siteFinder.getSiteList().size() >= 1);
        Assert.assertTrue(siteFinder.isButtonForSitePresent(siteName, ButtonType.Leave));
    }
    
    @Test(priority=3, expectedExceptions={IllegalArgumentException.class})
    public void test102SearchForSitesWithNull()
    {
        siteFinder.searchForSite(null);
    }

    
    @Test(priority=4)
    public void test102SearchForSitesWithEmpty()
    {
        siteFinder = siteFinder.searchForSite("").render();
        Assert.assertEquals(siteFinder.hasResults(), true);
    }
    
    @Test(priority=5)
    public void test103SearchForSitesWithEmpty()
    {
        siteFinder = siteFinder.searchForSite("").render();
        Assert.assertEquals(siteFinder.hasResults(), true);
    }
    
    @Test(priority=6)
    public void test104SearchForSitesModerated()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteNameModerated).render(); 
        Assert.assertEquals(siteFinder.getSiteList().size(), 1);
    }
    
    @Test(priority=7)
    public void test105SearchForSitesPrivate()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteNamePrivate).render();
        Assert.assertEquals(siteFinder.getSiteList().size(), 1);
        Assert.assertEquals(siteFinder.getSiteList().get(0), siteNamePrivate);
    }
    
    @Test(priority=8)
    public void test106SearchForSitesNonExistent()
    {
        siteFinder =  siteFinder.searchForSite("zzzz"+System.currentTimeMillis()).render();
        Assert.assertEquals(siteFinder.getSiteList().size(), 0);
    }
    
    @Test(priority=9)
    public void test107SelectSiteModerated()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteNameModerated).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNameModerated);
        Assert.assertEquals(siteDash.render().isSite(siteNameModerated), true);
    }    
    
    @Test(priority=10)
    public void test108SelectSitePrivate()
    {
        siteFinder = siteFinder.searchForSite(siteNamePrivate).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNamePrivate);
        Assert.assertEquals(siteDash.render().isSite(siteNamePrivate), true);
    }
    
    @Test(priority=11)
    public void test109SelectSitePublic()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNamePublic);
        Assert.assertEquals(siteDash.render().isSite(siteNamePublic), true);
    }
    
    @Test(priority=12)
    public void test110SelectSiteByIndex()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSiteByIndex(0);
        Assert.assertEquals(siteDash.render().isSite(siteName), true);
    }
    
    @Test(expectedExceptions = UnsupportedOperationException.class, priority=13)
    public void test111SelectSiteNull()
    {
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(null);
        Assert.assertEquals(siteDash.render().isSite(siteName), true);
    }
    
    @Test(priority=14)
    public void test112DeleteSite()
    {
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName).render();
        List<String> sitesFound = siteFinder.getSiteList(); 
        siteFinder.deleteSite(siteName);
        
        siteFinder = getSiteFinderPage();
        siteFinder = siteFinder.searchForSite(siteName).render();
        sitesFound = siteFinder.getSiteList();
        Assert.assertFalse(sitesFound.contains(siteName));
        
    }
}
