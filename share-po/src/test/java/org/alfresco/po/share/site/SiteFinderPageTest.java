/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.site;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;

import org.alfresco.po.share.site.SiteFinderPage.ButtonType;

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

        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        siteUtil.createSite(driver, username, password, siteNamePublic, "description", "Public");
        siteUtil.createSite(driver, username, password, siteNameModerated, "description", "Moderated");
        siteUtil.createSite(driver, username, password, siteNamePrivate, "description", "Private");
    }
    
    @BeforeMethod
    public void getSiteFinder()
    {
        siteFinder = getSiteFinderPage();
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteNamePublic);
        siteUtil.deleteSite(username, password, siteNameModerated);
        siteUtil.deleteSite(username, password, siteNamePrivate);
    }
    
    private SiteFinderPage getSiteFinderPage()
    {
        // Navigate to Search For Site
        SharePage page = resolvePage(driver).render(); 
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
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
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
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteNameModerated); 
        Assert.assertEquals(siteFinder.getSiteList().size(), 1);
    }
    
    @Test(priority=7)
    public void test105SearchForSitesPrivate()
    {
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteNamePrivate);
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
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteNameModerated);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNameModerated).render();
        Assert.assertEquals(siteDash.render().isSite(siteNameModerated), true);
    }    
    
    @Test(priority=10)
    public void test108SelectSitePrivate()
    {
        siteFinder = siteFinder.searchForSite(siteNamePrivate).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNamePrivate).render();
        Assert.assertEquals(siteDash.render().isSite(siteNamePrivate), true);
    }
    
    @Test(priority=11)
    public void test109SelectSitePublic()
    {
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteNamePublic).render();
        Assert.assertEquals(siteDash.render().isSite(siteNamePublic), true);
    }
    
    @Test(priority=12)
    public void test110SelectSiteByIndex()
    {
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSiteByIndex(0).render();
        Assert.assertEquals(siteDash.render().isSite(siteName), true);
    }
    
    @Test(expectedExceptions = UnsupportedOperationException.class, priority=13)
    public void test111SelectSiteNull()
    {
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(null).render();
        Assert.assertEquals(siteDash.render().isSite(siteName), true);
    }
    
    @Test(priority=14)
    public void test112DeleteSite()
    {
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        List<String> sitesFound = siteFinder.getSiteList(); 
        siteFinder.deleteSite(siteName);
        
        siteFinder = getSiteFinderPage();
        siteFinder = siteFinder.searchForSite(siteName).render();
        sitesFound = siteFinder.getSiteList();
        Assert.assertFalse(sitesFound.contains(siteName));
        
    }
}
