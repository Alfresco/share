
package org.alfresco.po.share.cmm;

import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test Class holds all tests to test NavigationCMMPage
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class NavigationCMMPageTest extends AbstractTestCMM
{
    private SharePage page;
    @Autowired CmmActions cmmActions;

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {
        // page = loginAs(username, password);
    }

    @AfterMethod
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    /**
     * Navigate to manage custom models from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void navigateToManageModelsAsAdmin() throws Exception
    {
        loginAs(username, password);
        ModelManagerPage cmmPage = (ModelManagerPage) cmmActions.navigateToModelManagerPage(driver);
        Assert.assertTrue(cmmPage.getTitle().endsWith("Model Manager"));
    }

    /**
     * Verify that the menu option to navigate to manage custom models is not available for normal User
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void navigateToManageModelsAsNewUser() throws Exception
    {

        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        userService.create(username, password, userinfo, userinfo, userinfo, userinfo, userinfo);
        loginAs(userinfo, userinfo);
        SharePage page = resolvePage(driver).render();

        try
        {
            page.getNav().selectManageCustomModelsPage().render();
            Assert.fail("This operation is not valid for the selected user");
        }
        catch (UnsupportedOperationException e)
        {

        }

    }

    /**
     * Navigate to manage custom models from the dashboard page by Non Admin User
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void navigateToManageModelsAsNonAdmin() throws Exception
    {
        String cmmGroupName = "ALFRESCO_MODEL_ADMINISTRATORS";
        String userinfo = "usercm" + System.currentTimeMillis() + "@test.com";
        page = shareUtil.loginWithPost(driver, shareUrl, username, password).render();
        UserSearchPage userPage = page.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, cmmGroupName);

        logout(driver);

        loginAs(userinfo, userinfo);

        ModelManagerPage cmmPage = (ModelManagerPage) cmmActions.navigateToModelManagerPage(driver);

        Assert.assertTrue(cmmPage.getTitle().endsWith("Model Manager"));
    }
}
