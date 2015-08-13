package org.alfresco.po.share;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
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
        driver.navigate().to(shareUrl);
        DashBoardPage dashBoard = loginAs(username, password);
        dashBoard.getNav().selectUserDropdown();
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
        MyProfilePage myprofilepage = userpage.selectMyProfile().render();
        Assert.assertTrue(myprofilepage.titlePresent());
        myprofilepage.getNav().selectUserDropdown();
    }

    /**
     * To verify the title of the user account Settings Page Title
     */
    @Test(groups = "Cloud-only", dependsOnMethods = "userProfilePageTitleCheck")
    public void selectAccountSettingsPageCheck()
    {
        Assert.assertTrue(userpage.isAccountSettingsLinkPresent());
        AccountSettingsPage accountSettingsPage = userpage.selectAccountSettingsPage().render();
        String title = "Account Settings";
        Assert.assertTrue(accountSettingsPage.isTitlePresent(title));
        accountSettingsPage.getNav().selectUserDropdown();
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
        siteUtil.createSite(driver, "admin","admin", setHomePageUserSiteName, "","Public");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();
        DocumentLibraryPage documentLibPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(setHomePageFolder, setHomePageFolder).render();
        
        documentLibPage.selectFolder(setHomePageFolder);

        //select folder page as a homepage
        documentLibPage.getNav().selectUserDropdown();
        documentLibPage = userpage.selectUseCurrentPage().render();

        //check that folder page is homepage
        documentLibPage.getNav().selectHome().render();
        Assert.assertEquals(setHomePageUserSiteName, documentLibPage.getPageTitle());
        Assert.assertTrue(driver.getCurrentUrl().indexOf(setHomePageFolder) != -1);
        
        //go to site dashboard
        SiteDashboardPage siteDashboardPage = documentLibPage.getSiteNav().selectSiteDashBoard().render();
       
        //set the dashboard as a homepage
        siteDashboardPage.getNav().selectUserDropdown();
        siteDashboardPage = userpage.selectUseCurrentPage().render();
        
        //delete site
        siteUtil.deleteSite("admin","admin", setHomePageUserSiteName);
        SiteFinderPage siteFinderPage = resolvePage(driver).render();
        String dashboardUrl = shareUrl + "/page/user/admin/dashboard";
        
        //click on Home
        siteDashboardPage =  (SiteDashboardPage) siteFinderPage.getNav().selectHome();
                
        //check that error page is displayed
        Assert.assertTrue(siteDashboardPage.getTitle().indexOf("System Error") != -1);

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
        
        //set user dashboard as home page
        DashBoardPage dashBoard = userpage.selectUseMyDashboardPage().render();

        //check that page is set as a home page
        dashBoard.getNav().selectHome().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
    }
    
    
    /**
     * To verify that click on User Dashboard displayes user dashboard
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useMyDashboardAsHomePageCheck")
    public void userDashboardCheck() throws Exception
    {
        DashBoardPage dashBoard = userpage.selectUserDashboard().render();
        Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
    }
    
}
