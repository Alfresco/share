package org.alfresco.po.share.user;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.LoginPage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = { "Cloud-only" })
public class CloudForgotPasswordPageTest extends AbstractTest
{
    CloudForgotPasswordPage forgotPassPage;
    LoginPage login;

    /**
     * Checks the forgot password functionality
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Cloud-only" })
    public void testSelectFogotPassordLink() throws Throwable
    {
        drone.navigateTo(shareUrl);
        login = new LoginPage(drone);
        forgotPassPage = login.selectFogotPassordLink().render();

        assertTrue(drone.getTitle().contains("Forgot Password"), "Forgot Password page didn't opened");
    }

    @Test(dependsOnMethods = "testSelectFogotPassordLink", timeOut = 60000)
    public void testClickCancel() throws Throwable
    {
        login = new LoginPage(drone);
        login = forgotPassPage.clickCancel().render();
        assertTrue(drone.getTitle().contains("Login"), "Login page didn't opened");
    }

    @Test(dependsOnMethods = "testClickCancel", timeOut = 60000)
    public void testSelectSendInstructions() throws Throwable
    {
        forgotPassPage = login.selectFogotPassordLink().render();
        forgotPassPage.clickSendInstructions(cloudUserName);

        assertTrue(forgotPassPage.isConfirmationResetPassword());

    }
}
