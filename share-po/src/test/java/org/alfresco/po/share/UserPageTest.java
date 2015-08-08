package org.alfresco.po.share;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * User Page Test 
 * @author hamara
 */
@Listeners(FailedTestListener.class)
public class UserPageTest extends AbstractTest
{
    UserPage userpage;
    private String setHomePageUserSiteName = "setHomePageUserSite" + System.currentTimeMillis(); 
    private String setHomePageFolder = "setHomePageFolder" + System.currentTimeMillis();
    
    @Test(groups = "alfresco-one")
    public void userPageLinks() throws Exception
    {
        drone.navigateTo(shareUrl);
        DashBoardPage dashBoard = loginAs(username, password);
        userpage = dashBoard.getNav().selectUserDropdown().render();
        Assert.assertTrue(userpage.isSetStausLinkPresent());
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
        MyProfilePage myprofilepage = userpage.selectMyProfile();
        Assert.assertTrue(myprofilepage.titlePresent());
        userpage = myprofilepage.getNav().selectUserDropdown().render();
    }

    /**
     * To verify the title of the user account Settings Page Title
     */
    @Test(groups = "Cloud-only", dependsOnMethods = "userProfilePageTitleCheck")
    public void selectAccountSettingsPageCheck()
    {
        Assert.assertTrue(userpage.isAccountSettingsLinkPresent());
        AccountSettingsPage accountSettingsPage = userpage.selectAccountSettingsPage();
        String title = "Account Settings";
        Assert.assertTrue(accountSettingsPage.isTitlePresent(title));
        userpage = accountSettingsPage.getNav().selectUserDropdown().render();
    }
    
    /**
     * To verify the title of the user account Settings Page Title
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "userProfilePageTitleCheck", expectedExceptions = UnsupportedOperationException.class)
    public void selectAccountSettingsPageCheckForEnt()
    {
        Assert.assertFalse(userpage.isAccountSettingsLinkPresent());
        userpage.selectAccountSettingsPage();
    }

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
        SiteUtil.createSite(drone, setHomePageUserSiteName, "description", "Public");
        SiteDashboardPage siteDashBoard = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = siteDashBoard.getSiteNav().selectSiteDocumentLibrary().render();
        
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(setHomePageFolder, setHomePageFolder).render();
        
        documentLibPage.selectFolder(setHomePageFolder);

        //select folder page as a homepage
        userpage = documentLibPage.getNav().selectUserDropdown();
        documentLibPage = userpage.selectUseCurrentPage().render();

        //check that folder page is homepage
        documentLibPage.getNav().selectHome().render();
        Assert.assertEquals(setHomePageUserSiteName, documentLibPage.getPageTitle());
        Assert.assertTrue(drone.getCurrentUrl().indexOf(setHomePageFolder) != -1);
        
        //go to site dashboard
        SiteDashboardPage siteDashboardPage = documentLibPage.getSiteNav().selectSiteDashBoard().render();
       
        //set the dashboard as a homepage
        userpage = siteDashboardPage.getNav().selectUserDropdown().render();
        siteDashboardPage = userpage.selectUseCurrentPage().render();
        
        //delete site
        SiteUtil.deleteSite(drone, setHomePageUserSiteName);
        SiteFinderPage siteFinderPage = drone.getCurrentPage().render();
 
        String dashboardUrl = shareUrl + "/page/user/admin/dashboard";
        
        //click on Home
        siteDashboardPage =  (SiteDashboardPage) siteFinderPage.getNav().selectHome();
                
        //check that error page is displayed
        Assert.assertTrue(siteDashboardPage.getTitle().indexOf("System Error") != -1);

        drone.navigateTo(dashboardUrl);
        DashBoardPage dashBoard = drone.getCurrentPage().render();        
        userpage = dashBoard.getNav().selectUserDropdown().render();
    }
    
    /**
     * To verify that click on Use My Dashboard sets user dashboard page as home page
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useCurrentPageAsHomePageCheck")
    public void useMyDashboardAsHomePageCheck() throws Exception
    {
        Assert.assertTrue(userpage.isUseMyDashboardPresent());
        
        //set user dashboard as home page
        DashBoardPage dashBoard = userpage.selectUseMyDashboardPage().render();

        //check that page is set as a home page
        dashBoard.getNav().selectHome().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
        userpage = dashBoard.getNav().selectUserDropdown().render();
    }
    
    
    /**
     * To verify that click on User Dashboard displayes user dashboard
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useMyDashboardAsHomePageCheck")
    public void userDashboardCheck() throws Exception
    {
        DashBoardPage dashBoard = userpage.selectUserDashboard().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
        userpage = dashBoard.getNav().selectUserDropdown().render();
    }
    
    
    
    /**
     * To verify the right page is opened when selecting Help
     */
    //@Test(groups = "Enterprise-only", dependsOnMethods = "changePassWordPageCheck")
    @Test(groups = "Enterprise-only", dependsOnMethods = "userDashboardCheck")
    public void selectHelp()
    {
        String mainWindow = drone.getWindowHandle();
        userpage.clickHelp();
        waitInSeconds(3);
        Assert.assertTrue(isWindowOpened("Using Alfresco One"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
    }
}
