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
package org.alfresco.po.share;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.EditSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * User Page Test
 * 
 * @author hamara
 */
@Listeners(FailedTestListener.class)
public class UserPageTest extends AbstractTest
{
    UserPage userpage;
    private String setHomePageUserSiteName = "setHomePageUserSite" + System.currentTimeMillis();
    private String setHomePageFolder = "setHomePageFolder" + System.currentTimeMillis();
    private String privateSiteName = "privateSiteName" + System.currentTimeMillis();
    private String publicSiteName = "publicSiteName" + System.currentTimeMillis();
    private String siteUserName = "siteUserName" + System.currentTimeMillis();
    
    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception
    {
        siteUtil.createSite(driver, username, password, publicSiteName,"", "Public");
        siteUtil.createSite(driver, username, password, privateSiteName,"", "Private");
        createEnterpriseUser(siteUserName);
        driver.navigate().to(shareUrl);
    }
    
    
    @Test(groups = "alfresco-one")
    public void userPageLinks() throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        userpage = dashBoard.getNav().selectUserDropdown().render();
        Assert.assertTrue(userpage.isHelpLinkPresent());
        Assert.assertTrue(userpage.isLogoutLinkPresent());
    }

    /**
     * To verify the title of the user Profile Page Title
     */
    @Test(groups = "alfresco-one", dependsOnMethods = "userPageLinks")
    public void userProfilePageTitleCheck()
    {
        Assert.assertTrue(userpage.isMyProfileLinkPresent());
        MyProfilePage myprofilepage = userpage.selectMyProfile().render();
        Assert.assertTrue(myprofilepage.titlePresent());
        userpage = myprofilepage.getNav().selectUserDropdown();
    }

    /**
     * To verify the title of the user account Settings Page Title
     */
    // @Test(groups = "Cloud-only", dependsOnMethods = "userProfilePageTitleCheck")
    /**
     * public void selectAccountSettingsPageCheck()
     * {
     * Assert.assertTrue(userpage.isAccountSettingsLinkPresent());
     * AccountSettingsPage accountSettingsPage = userpage.selectAccountSettingsPage().render();
     * String title = "Account Settings";
     * Assert.assertTrue(accountSettingsPage.isTitlePresent(title));
     * accountSettingsPage.getNav().selectUserDropdown();
     * }
     **/

    /**
     * To verify the title of the user account Settings Page Title
     */
    // @Test(groups = "Enterprise-only", dependsOnMethods = "userProfilePageTitleCheck", expectedExceptions = UnsupportedOperationException.class)
    /**
     * public void selectAccountSettingsPageCheckForEnt()
     * {
     * Assert.assertFalse(userpage.isAccountSettingsLinkPresent());
     * userpage.selectAccountSettingsPage();
     * }
     **/

    /**
     * To verify the title of the change Password Page Title
     */
    @Test(groups = "alfresco-one", dependsOnMethods = "userProfilePageTitleCheck")
    public void changePassWordPageCheck()
    {
        Assert.assertTrue(userpage.isChangePassWordLinkPresent());
        ChangePasswordPage changepasswordpage = userpage.selectChangePassword();
        Assert.assertTrue(changepasswordpage.formPresent());
    }

    /**
     * To verify that click on use Current page sets current page as home page
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "changePassWordPageCheck")
    public void useCurrentPageAsHomePageCheck() throws Exception
    {
        siteUtil.createSite(driver, "admin", "admin", setHomePageUserSiteName, "", "Public");

        SiteDashboardPage siteDashBoard = resolvePage(driver).render();

        DocumentLibraryPage documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(setHomePageFolder, setHomePageFolder).render();

        documentLibPage.selectFolder(setHomePageFolder);

        // select folder page as a homepage
        documentLibPage.getNav().selectUserDropdown();
        documentLibPage = userpage.selectUseCurrentPage().render();

        // check that folder page is homepage
        documentLibPage.getNav().selectMyDashBoard().render();
        Assert.assertEquals(setHomePageUserSiteName, documentLibPage.getPageTitle());
        Assert.assertTrue(driver.getCurrentUrl().indexOf(setHomePageFolder) != -1);

        // go to site dashboard
        SiteDashboardPage siteDashboardPage = documentLibPage.getSiteNav().selectSiteDashBoard().render();

        // set the dashboard as a homepage
        siteDashboardPage.getNav().selectUserDropdown();
        siteDashboardPage = userpage.selectUseCurrentPage().render();

        // delete site
        siteUtil.deleteSite(driver, setHomePageUserSiteName);

        SiteFinderPage siteFinderPage = resolvePage(driver).render();
        String dashboardUrl = shareUrl + "/page/user/admin/dashboard";

        // click on Home
        SiteDashboardErrorPage siteDashboardError = siteFinderPage.getNav().selectMyDashBoard().render();

        // check that error page is displayed
        Assert.assertNotNull(siteDashboardError, "Site Dashboard Error Page should be displayed after Site's been deleted");

        driver.navigate().to(dashboardUrl);
        DashBoardPage dashBoard = resolvePage(driver).render();
        dashBoard.getNav().selectUserDropdown();

    }

    /**
     * To verify that click on Use My Dashboard sets user dashboard page as home page
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useCurrentPageAsHomePageCheck")
    public void useMyDashboardAsHomePageCheck() throws Exception
    {
        Assert.assertTrue(userpage.isUseMyDashboardPresent());

        // set user dashboard as home page
        DashBoardPage dashBoard = userpage.selectUseMyDashboardPage().render();

        // check that page is set as a home page
        dashBoard.getNav().selectMyDashBoard().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
    }

    /**
     * To verify that click on User Dashboard displayes user dashboard
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useMyDashboardAsHomePageCheck")
    public void userDashboardCheck() throws Exception
    {
        DashBoardPage dashBoard = resolvePage(driver).render();
        userpage = dashBoard.getNav().selectUserDropdown();
        dashBoard = userpage.selectUserDashboard().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
        logout(driver);
    }
    
    /**
     * Checks that error page is displayed when user sets the home page to be the site's dashboard
     * and the site's visibility changed from Public to Private
     * 
     * @throws Exception
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "userDashboardCheck")
    public void useSiteDashboardAsHomePageAndChangeVisibility() throws Exception
    {
        //user sets public site's dashboard as a home page
        DashBoardPage dashBoard = loginAs(siteUserName, "password");
        SiteFinderPage siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(publicSiteName).render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, publicSiteName);
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(publicSiteName).render();
        userpage = siteDashboardPage.getNav().selectUserDropdown().render();
        siteDashboardPage = userpage.selectUseCurrentPage().render();
        logout(driver);
        
        //public site becomes private
        dashBoard = loginAs(username, password);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(publicSiteName).render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, publicSiteName);
        siteDashboardPage = siteFinderPage.selectSite(publicSiteName).render();
        EditSitePage editSitePage = siteDashboardPage.getSiteNav().selectEditSite().render();
        editSitePage.selectSiteVisibility(true, false);
        siteDashboardPage = editSitePage.selectOk().render();
        logout(driver);
            
        //error page is displayed as home page 
        driver.navigate().to(shareUrl);
        LoginPage lp = factoryPage.getPage(driver).render();
        lp.loginAs(siteUserName, "password");
        Assert.assertTrue(resolvePage(driver).getTitle().indexOf("System Error") != -1);
        
        String dashboardUrl = shareUrl + "/page/user/" + siteUserName + "/dashboard";
        driver.navigate().to(dashboardUrl);
        logout(driver);
        
    }
    
    /**
     * Checks that error page is displayed when member of the private site sets the home page to be the site 
     * dashboard and leaves the site 
     * 
     * @throws Exception
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useSiteDashboardAsHomePageAndChangeVisibility")
    public void useSiteDashboardAsHomePageAndLeaveSite() throws Exception
    {
        //user added to the private site
        DashBoardPage dashBoard = loginAs(username, password);
        SiteFinderPage siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(privateSiteName).render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, privateSiteName);
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(privateSiteName).render();
        AddUsersToSitePage addUsersToSitePage = siteDashboardPage.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, siteUserName, UserRole.COLLABORATOR);
        logout(driver);

        //user sets private site's dashboard as a home page
        dashBoard = loginAs(siteUserName, "password");
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, privateSiteName);
        siteDashboardPage = siteFinderPage.selectSite(privateSiteName).render();
        userpage = siteDashboardPage.getNav().selectUserDropdown().render();
        siteDashboardPage = userpage.selectUseCurrentPage().render();
        
        //user leaves the site
        dashBoard = siteDashboardPage.getSiteNav().leaveSite().render();   

        //error page is displayed as home page
        dashBoard.getNav().selectMyDashBoard();
        Assert.assertTrue(resolvePage(driver).getTitle().indexOf("System Error") != -1);
    }
    
    
    

}
