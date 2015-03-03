/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Alfresco Share navigation bar integration test.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@SuppressWarnings("unused")
public class NavigationBarTest extends AbstractTest
{
    private SharePage page;    
    private String siteName;

    private static final String pentahoBusinessAnalystGroup = "ANALYTICS_BUSINESS_ANALYSTS";
    private String businessAnalystsUserName = "BusinessAnalystUser_" + System.currentTimeMillis();
    
    
    @BeforeClass(groups={"alfresco-one"}, alwaysRun=true)
    public void setup() throws Exception
    {
        siteName = String.format("test-%d-site-crud",System.currentTimeMillis());
        page = loginAs(username, password);
    }
    
    /**
     * Selects My Sites from Sites menu and checks that User Sites List is Displayed
     * @throws Exception
     */
    @Test(groups={"alfresco-one"}, priority=1)
    public void navigateToMySites() throws Exception
    {
        UserSitesPage userSitesPage = page.getNav().selectMySites().render();
        assertEquals(userSitesPage.getPageTitle(), "User Sites List");
    }  
    
   
    /**
     * Navigate to people finder from the dashboard page
     * and back to dash board page by selecting the 
     * navigation icons.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToMySites",groups={"alfresco-one"}, priority=2)
    public void navigateToPeopleFinder() throws Exception
    {
        PeopleFinderPage peoplePage = page.getNav().selectPeople().render();
        assertEquals(peoplePage.getPageTitle(), "People Finder");
    }
    
    /**
     * Test navigating to site finder page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToPeopleFinder",groups={"alfresco-one"}, priority=3)
    public void navigateToSearchForSites() throws Exception
    {
        page = page.getNav().selectSearchForSites().render();
        assertEquals(page.getPageTitle(), "Site Finder");
    }
    
    /**
     * Test navigating to create site page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToSearchForSites",groups={"alfresco-one"}, priority=4)
    public void navigateToCreateSite() throws Exception
    {
        assertTrue(page.getNav().isCreateSitePresent());
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        assertTrue(createSitePage.isCreateSiteDialogDisplayed());
        createSitePage.cancel();
    }
    
    /**
     * Test navigating to my profile page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToCreateSite" ,groups={"alfresco-one"}, priority=5)
    public void navigateToMyProfile() throws Exception
    {
        MyProfilePage myProfilePage = page.getNav().selectMyProfile().render();
        assertTrue(myProfilePage.titlePresent());
    }
    
    /**
     * Test navigating to change password page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToMyProfile",groups={"alfresco-one"}, priority=6)
    public void navigateChangePassword() throws Exception
    {
        ChangePasswordPage changePasswordPage = page.getNav().selectChangePassword().render();
        assertTrue(changePasswordPage.formPresent());
    }
    
     /**
     * Test navigating to change password page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateChangePassword",groups={"alfresco-one"}, priority=7)
    public void navigateDashBoard() throws Exception
    {
        DashBoardPage dash = page.getNav().selectMyDashBoard().render();
        assertTrue(dash.titlePresent());
        dash.getTitle().contains("Dashboard");
        assertTrue(dash.getTitle().contains("Dashboard"));
    }
    
    /**
     * Test repository link, note that this is for non cloud product.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateDashBoard",groups = "Enterprise-only", priority=8)
    public void navigateToRepository() throws Exception
    {
        AlfrescoVersion version = drone.getProperties().getVersion();
        if(version.isCloud())
        {
            throw new SkipException("This feature is not supported in cloud so skip it");
        }
        RepositoryPage repoPage = page.getNav().selectRepository().render();
        assertTrue(repoPage.isBrowserTitle("Repository"));
    }
    
    /**
     * Test advance search link.
     * Note supported in cloud.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToRepository",groups= "Enterprise-only", priority=9)
    public void advanceSearch() throws Exception
    {
    	AlfrescoVersion version = drone.getProperties().getVersion();
        if(version.isCloud())
        {
            throw new SkipException("This feature is not supported in cloud so skip it");
        }
        AdvanceSearchContentPage searchPage = page.getNav().selectAdvanceSearch().render();
        assertEquals(searchPage.getPageTitle(), "Advanced Search");
    }

    @Test(dependsOnMethods = "advanceSearch", groups = {"Enterprise-only"}, expectedExceptions = UnsupportedOperationException.class, priority=10)
    public void testSelectNetworkDropdownInEnterprise() throws Exception
    {
        page.getNav().selectNetworkDropdown();
    }
    
    @Test(dependsOnMethods = "testSelectNetworkDropdownInEnterprise", groups = {"Enterprise-only"}, expectedExceptions = UnsupportedOperationException.class, priority=11)
    public void testSelectNetworkInEnterprise() throws Exception
    {
        String strInvitedUser = username.substring(username.lastIndexOf("@") + 1, username.length());
       page.getNav().selectNetwork(strInvitedUser);
    }
    
    @Test(groups ="Cloud-only", priority=12)
    public void testNetworkDropdown()
    {
        Assert.assertNotNull(page.getNav().selectNetworkDropdown());
    }

    @Test(dependsOnMethods = "testNetworkDropdown", groups = "Cloud-only", priority=13)
    public void testSelectNetwork()
    {
        page = drone.getCurrentPage().render();
        String strInvitedUser = username.substring(username.lastIndexOf("@") + 1, username.length());
        Assert.assertNotNull(page.getNav().selectNetwork(strInvitedUser).render());
    }

    @Test(dependsOnMethods = "testSelectNetwork", groups = "Cloud-only", expectedExceptions = IllegalArgumentException.class, priority=14)
    public void testSelectNetworkWithNull()
    {
        page.getNav().selectNetwork(null);
    }

    @Test(dependsOnMethods = "testSelectNetworkWithNull", groups = "Cloud-only", expectedExceptions = IllegalArgumentException.class, priority=15)
    public void testSelectNetworkWithEmpty()
    {
        page.getNav().selectNetwork("");
    }

    @Test(dependsOnMethods = "testSelectNetworkWithEmpty", groups = "Cloud-only", priority=16)
    public void testgetNetworks()
    {
        List<String> userNetworks = page.getNav().getUserNetworks();
        assertTrue(userNetworks.size() > 0);
    }
    
    /**
     * Test navigating to Account Settings Page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "testgetNetworks",groups={"Cloud-only"}, priority=17)
    public void navigateAccountSettings() throws Exception
    {
        AccountSettingsPage accountSettingsPage = page.getNav().selectAccountSettingsPage().render();
        assertEquals(accountSettingsPage.getPageTitle(), "Account Settings");
    }
    
    /**
     * Navigate to admin tools from the dashboard page.
     * 
     * @throws Exception if error
     */
    @Test(groups = {"Enterprise-only"}, priority=18)
    public void navigateToAdminTools() throws Exception
    {
        AdminConsolePage adminConsolePage = page.getNav().selectAdminTools().render();
        assertEquals(adminConsolePage.getPageTitle(), "Admin Tools");
    }

    /**
     * Navigate to manage sites from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority=19)
    public void navigateToManageSites() throws Exception
    {
        ManageSitesPage manageSitesPage = page.getNav().selectManageSitesPage().render();
        assertEquals(manageSitesPage.getPageTitle(), "Sites Manager");
    }

    /**
     * Navigate to manage sites from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */
    @Test (groups = { "Enterprise-only" }, priority=20)
    public void navigateToManageSitesSiteAdmin() throws Exception
    {
        String siteAdmin = "SITE_ADMINISTRATORS";
        UserSearchPage userPage = page.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, siteAdmin);
        ShareUtil.logout(drone);
        ShareUtil.loginAs(drone, shareUrl, userinfo, userinfo);
        ManageSitesPage manageSitesPage = page.getNav().selectManageSitesSiteAdmin().render();
        assertEquals(manageSitesPage.getPageTitle(), "Sites Manager");

    }
    
    @Test(groups= "Enterprise-only", priority=21)
    public void noRecentSites() throws Exception
    {   
        try
        {
            page.getNav().getRecentSitesPresent();
            fail("PageOperationException Should been thrown by above line.");
        }
        catch (PageOperationException e)
        {
            String patternString = "No Recent Site(s) Available";
            assertTrue(e.getMessage().startsWith(patternString), "Exception Message should Start with " + patternString);
        }
    }
    
    /**
     * Test newly created site from favourite..
     * @throws Exception if error
     */
    @Test(groups= "Enterprise-only", priority=22)
    public void removeAndAddSiteFromFavourite() throws Exception
    {        
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSitePage.createNewSite(siteName).render();
        assertTrue(site.getNav().getRecentSitesPresent().size() > 0 );   
        assertTrue(site.getNav().doesAnyFavouriteSiteExist());
        assertTrue(site.getNav().isSiteFavourtie());       
        site.getNav().removeFavourite();
        assertFalse(site.getNav().doesAnyFavouriteSiteExist());
        assertFalse(site.getNav().isSiteFavourtie());
        site.getNav().setSiteAsFavourite();
        assertTrue(site.getNav().isSiteFavourtie());  
        assertTrue(site.getNav().getFavouriteSites().contains(siteName));
        drone.refresh();
    }
    @Test(groups= "Enterprise-only", enabled=false, expectedExceptions={UnsupportedOperationException.class}, priority=23)
    public void removeAndSiteFromFavouriteInDashBoardPage() throws Exception
    {   
        CustomiseUserDashboardPage usereDashBoradPage = page.getNav().selectCustomizeUserDashboard().render();
        usereDashBoradPage.getNav().isSiteFavourtie();
    }
 
 }
