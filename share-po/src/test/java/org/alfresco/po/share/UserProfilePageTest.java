package org.alfresco.po.share;

import org.alfresco.po.AbstractTest;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify User profile page elements are in place.
 * 
 * @author Chiran
 * @since 1.7.1
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class UserProfilePageTest extends AbstractTest
{
    String userinfo = "puser" + System.currentTimeMillis() + "@test.com";
    private DashBoardPage dashBoard;
    private UserSearchPage page;
    UserSearchPage results;

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(userinfo);
        newPage.inputLastName(userinfo);
        newPage.inputEmail(userinfo);
        newPage.inputUsername(userinfo);
        newPage.inputPassword(userinfo);
        newPage.inputVerifyPassword(userinfo);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        page = userCreated.searchFor(userinfo).render();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void deleteNullUserMethod() throws Exception
    {
        page = dashBoard.getNav().getUsersPage().render();
        results = page.searchFor(userinfo).render();
        Assert.assertTrue(results.hasResults());
        results.clickOnUser(null);
        Assert.fail("IllegalArgumentException Expected");
    }

    @Test(dependsOnMethods = "deleteNullUserMethod")
    public void deleteUser() throws Exception
    {
        page = dashBoard.getNav().getUsersPage().render();
        Assert.assertTrue(page.isLogoPresent());
        Assert.assertTrue(page.isTitlePresent());
        results = page.searchFor(userinfo).render();

        Assert.assertTrue(results.hasResults());

        UserProfilePage userProfile = results.clickOnUser(userinfo).render();
        results = userProfile.deleteUser().render();
        results = page.searchFor(userinfo).render();

        Assert.assertFalse(results.hasResults());
    }
}
