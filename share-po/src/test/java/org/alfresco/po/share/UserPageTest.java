package org.alfresco.po.share;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
    private String setHomePageUser = "setHomePageUser" + System.currentTimeMillis();
    
    
    @BeforeClass
    public void prepare() throws Exception
    { 
        createEnterpriseUser(setHomePageUser);
        loginAs(setHomePageUser, UNAME_PASSWORD);
        SiteUtil.createSite(drone, setHomePageUserSiteName, "description", "Public");
        
        SiteDashboardPage siteDashBoard = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = siteDashBoard.getSiteNav().selectSiteDocumentLibrary().render();
        
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(setHomePageFolder, setHomePageFolder).render();
        ShareUtil.logout(drone);
        
    }
    
    
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
        ShareUtil.logout(drone);
    }
    
    /**
     * To verify that click on use Current page sets current page as home page
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "changePassWordPageCheck")
    public void useCurrentPageAsHomePageCheck() throws Exception
    {
        DashBoardPage dashBoard = loginAs(setHomePageUser, UNAME_PASSWORD);
 
        UserSitesPage userSitesPage = dashBoard.getNav().selectMySites().render();     
        SiteDashboardPage siteDashBoard = userSitesPage.getSite(setHomePageUserSiteName).clickOnSiteName().render();
        DocumentLibraryPage documentLibPage = siteDashBoard.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(setHomePageFolder);

        userpage = documentLibPage.getNav().selectUserDropdown().render();
        //set Change Password Page as a home page   
        userpage.selectUseCurrentPage();
        //log out, log in again and check that Change Password Page is set as a home page - navigate to user dashboard
        ShareUtil.logout(drone);
        documentLibPage = ShareUtil.loginAs(drone, shareUrl, setHomePageUser, UNAME_PASSWORD).render();
        Assert.assertEquals(setHomePageUserSiteName, documentLibPage.getPageTitle());
        Assert.assertTrue(drone.getCurrentUrl().indexOf(setHomePageFolder) != -1);
        userpage = documentLibPage.getNav().selectUserDropdown().render();
    }
    
    /**
     * To verify that click on Use My Dashboard sets user dashboard page as home page
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "useCurrentPageAsHomePageCheck")
    public void useMyDashboardAsHomePageCheck() throws Exception
    {
        Assert.assertTrue(userpage.isUseMyDashboardPresent());
        
        //set user dashboard as home page
        userpage.selectUseMyDashboardPage();
        //check that page is set as a home page
        ShareUtil.logout(drone);
        DashBoardPage dashBoard = loginAs(drone, shareUrl, setHomePageUser, UNAME_PASSWORD);
        Assert.assertTrue(dashBoard.getPageTitle().indexOf(setHomePageUser + "@test.com" + " Dashboard") != -1);
        userpage = dashBoard.getNav().selectUserDropdown().render();
    }
    
    /**
     * To verify the right page is opened when selecting Help
     */
    //@Test(groups = "Enterprise-only", dependsOnMethods = "changePassWordPageCheck")
    @Test(groups = "Enterprise-only", dependsOnMethods = "useMyDashboardAsHomePageCheck")
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
