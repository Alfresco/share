package org.alfresco.po.share;

import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * User Page Test 
 * @author hamara
 */
public class UserPageTest extends AbstractTest
{
    UserPage userpage;
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
        userpage = changepasswordpage.getNav().selectUserDropdown().render();
    }
    
    /**
     * To verify the right page is opened when selecting Help
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "changePassWordPageCheck")
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
