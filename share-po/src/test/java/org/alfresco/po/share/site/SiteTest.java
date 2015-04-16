/*
# * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;



/**
 * Site CRUD integration test.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups="alfresco-one")
public class SiteTest extends AbstractTest
{
    private String siteName;
    private String privateSiteName;
    private String moderateSiteName;
    private String privateModSiteName;
    DashBoardPage dashBoard;
    String testuser = "testuser" + System.currentTimeMillis();

    @BeforeTest(groups="alfresco-one")
    public void setup()
    {
        siteName = String.format("test-%d-site-crud",System.currentTimeMillis());
        privateSiteName = "private-" + siteName;
        moderateSiteName = "mod-" + siteName;
        privateModSiteName = "privateMod-" + siteName;
        // user joining the above sites
    }
    
    @BeforeClass(groups="alfresco-one")
    public void loginPrep() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            createEnterpriseUser(testuser);
        }
        dashBoard = loginAs(username, password);
    }
    
    @AfterClass(groups="alfresco-one")
    public void teardown() throws Exception
    {
        SiteUtil.deleteSite(drone, siteName);
        SiteUtil.deleteSite(drone, privateSiteName);
        SiteUtil.deleteSite(drone, moderateSiteName);
        SiteUtil.deleteSite(drone, privateModSiteName);
    }
    
    @BeforeMethod
    public void navigateToDash()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
    }
    
    /**
     * Test Site creation.
     * 
     * @throws Exception if error
     */
    @Test 
    public void createSite() throws Exception
    {
        // TODO: Create site option is not available for admin, admin user in Cloud. Pl run tests with other user, i.e. user1@freenet.test
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createNewSite(siteName).render();
        
        Assert.assertTrue(FactorySharePage.getPage(drone.getCurrentUrl(), drone) instanceof SiteDashboardPage);
        
        Assert.assertTrue(siteName.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getSiteNav().isDashboardActive());
        Assert.assertFalse(site.getSiteNav().isDocumentLibraryActive());
        Assert.assertTrue(site.getSiteNav().isDashboardDisplayed());
        Assert.assertTrue(site.getSiteNav().isSelectSiteMembersDisplayed());
    }
    
    @Test(dependsOnMethods="createSite")
    public void createDuplicateSite() throws Exception
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SharePopup errorPopup = createSite.createNewSite(siteName).render();
        try
        {
        errorPopup.handleMessage();
        Assert.fail("This is exception line");
        }
        catch(ShareException se)
        {
        }
        createSite.cancel();
    }
    
    @Test(dependsOnMethods = "createDuplicateSite")
    public void checkSiteNavigation()
    {
        SharePage sharePage = drone.getCurrentPage().render();
        SiteFinderPage siteFinder = sharePage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        
        Assert.assertFalse(FactorySharePage.getPage(drone.getCurrentUrl(), drone) instanceof SiteDashboardPage);
        
        Assert.assertFalse(docPage.getSiteNav().isDashboardActive());
        Assert.assertTrue(docPage.getSiteNav().isDocumentLibraryActive());
        siteDash = docPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(docPage.getSiteNav().isDashboardActive());
        Assert.assertFalse(docPage.getSiteNav().isDocumentLibraryActive());
    }
    
    @Test(dependsOnMethods = "checkSiteNavigation")
    public void searchForSiteThatDoesntExists()
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite("xyz").render();
        Assert.assertFalse(siteFinder.hasResults());
    }
    
//    /**
//     * Test public site joining.
//     * 
//     * @throws Exception
//     */
//    @Test(dependsOnMethods = "createSite", groups = "nonCloud")
//    public void joinPublicSite() throws Exception
//    {
//        logout(drone);
//        loginAs(testuser, "password");
//        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
//        siteFinder = siteFinder.searchForSite(siteName).render();
//        boolean hasResults = siteFinder.hasResults();
//        Assert.assertTrue(hasResults);
//        SharePage returnedSiteFinder = siteFinder.joinSite(siteName).render();
//        assertTrue("Should be an instance of SiteFinderPage", returnedSiteFinder instanceof SiteFinderPage);
//    }

//    /**
//     * Test public-moderated site joining.
//     * 
//     * @throws IOException
//     */
//    @Test(dependsOnMethods = { "createPublicModerateSite", "joinPublicSite" }, groups = "nonCloud")
//    public void joinPublicModSite()
//    {
//        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
//        siteFinder = siteFinder.searchForSite(moderateSiteName).render();
//        boolean hasResults = siteFinder.hasResults();
//        Assert.assertTrue(hasResults);
//        SharePage returnedSiteFinder = siteFinder.joinSite(moderateSiteName).render();
//        assertTrue("Should be an instance of SiteFinderPage", returnedSiteFinder instanceof SiteFinderPage);
//    }
//
//    /**
//     * Test public site joining.
//     * 
//     * @throws Exception
//     */
//    @Test(dependsOnMethods = "joinPublicSite", groups = "nonCloud")
//    public void leaveSite() throws Exception
//    {
//    	SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
//    	siteFinder = siteFinder.searchForSite(siteName).render();
//    	boolean hasResults = siteFinder.hasResults();
//    	Assert.assertTrue(hasResults);
//    	SharePage returnedSiteFinder = siteFinder.leaveSite(siteName).render();
//    	assertTrue("Should be an instance of SiteFinderPage", returnedSiteFinder instanceof SiteFinderPage);
//    }
//    
//
//    /**
//     * Test public-moderated site joining.
//     * @throws Exception 
//     */
//    @Test(expectedExceptions = PageException.class,
//          dependsOnMethods = { "createPublicModerateSite", "joinPublicModSite" }, 
//          groups = "nonCloud")
//    public void joinNonExistingSite()
//    {
//        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
//        siteFinder = siteFinder.searchForSite(moderateSiteName).render();
//        boolean hasResults = siteFinder.hasResults();
//        Assert.assertTrue(hasResults);
//        siteFinder = siteFinder.joinSite("ertwertwe").render();
//    }

    /**
     * Test site deletion.
     * @throws IOException 
     * @throws Exception if error found
     */
    
    @Test(dependsOnMethods = { "createPrivateModerateSiteShouldYeildPrivateSite"})
    public void deleteSite() throws Exception 
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
        boolean hasResults = siteFinder.hasResults();
        Assert.assertTrue(hasResults);
        siteFinder = siteFinder.deleteSite(siteName).render();
        hasResults = siteFinder.hasResults();
        List<String> sites = siteFinder.getSiteList();
        Assert.assertFalse(sites.contains(siteName));
    }
    
    @Test(dependsOnMethods = "searchForSiteThatDoesntExists")
    public void createPrivateSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createPrivateSite(privateSiteName).render();
        Assert.assertTrue(privateSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
        Assert.assertFalse(siteDetails.isModerate());
        siteDetails.cancel();
    }
    
    @Test(dependsOnMethods = "createPrivateSite")
    public void createPublicModerateSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createModerateSite(moderateSiteName).render();
        Assert.assertTrue(moderateSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();
        Assert.assertFalse(siteDetails.isPrivate());
        Assert.assertTrue(siteDetails.isModerate());
        siteDetails.cancel();
    }
    
    @Test(dependsOnMethods = "createPublicModerateSite")
    public void createPrivateModerateSiteShouldYeildPrivateSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createNewSite(privateModSiteName, null, true, true).render();
        Assert.assertTrue(privateModSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
        Assert.assertFalse(siteDetails.isModerate());
        siteDetails.cancel();
    }
    
    @Test(dependsOnMethods = "createPrivateModerateSiteShouldYeildPrivateSite")
    public void isEditingEnabled()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        Assert.assertFalse(createSite.isNameEditingDisaabled(), "Name Should be enabled for editing.");
        Assert.assertFalse(createSite.isUrlNameEditingDisaabled(), "URL Name should be enabled for editing");
        createSite.cancel();
    }
    
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void checkSiteNaveActiveLinkWithNull()
    {
        SiteNavigation nav = new SiteNavigation(drone);
        nav.isLinkActive(null);
    }

    @Test(dependsOnMethods = "deleteSite")
    public void checkSetSiteName()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        createSite.setSiteName(siteName);
        Assert.assertEquals(createSite.getSiteName(), siteName);
        createSite.clickCancel().render();
    }

    @Test(dependsOnMethods = "checkSetSiteName")
    public void checkSetSiteURL()
    {
        String siteURL = siteName + "URL";
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        createSite.setSiteName(siteURL);
        Assert.assertEquals(createSite.getSiteUrl(), siteURL.toLowerCase());
        createSite.clickCancel().render();

    }

//    /**
//     * A 4.2 bug  ALF-18320
//     * https://issues.alfresco.com/jira/browse/ALF-18320
//     * Tests SiteResultsPage by searching from a site page.
//     * @throws IOException 
//     */
//    @Test(dependsOnMethods = "searchForSiteThatDoesntExists")
//    public void searchInSite() throws Exception
//    {
//        try
//        {
//            SitePage site = getSiteDashboard(siteName);
//            SiteResultsPage serchResults = site.getSearch().search("*e*").render();
//            Assert.assertNotNull(serchResults);
//            Assert.assertTrue(serchResults.getResults().isEmpty());
//        }
//        catch (Exception e)
//        {
//            saveScreenShot(drone, "SiteTest.searchInSite");
//            throw new Exception("Unable to search in site", e);
//        }
//    }
}
