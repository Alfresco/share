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

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * LoginPage process integration test
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class LoginPageTest extends AbstractTest
{
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Log a user into Alfresco with valid credentials
     * and then logout
     * @throws Exception if error
     */ 
    @Test
    public void loginAndLogout() throws Exception 
    {
        drone.navigateTo(shareUrl);
        LoginPage page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());

        DashBoardPage dashboardPage = (DashBoardPage) ShareUtil.loginAs(drone, shareUrl, username, password);
        dashboardPage.render();
        Assert.assertFalse(page.isBrowserTitle("login"));
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        SharePage pageResponse = dashboardPage.getNav().logout().render();
        Assert.assertTrue(pageResponse.isBrowserTitle("login"));
    }
    
    /**
     * Test login panel is displayed when a user 
     * tries to access an Alfresco share page.
     */
    @Test
    public void pageShouldDisplayLoginPanel()
    {
        SharePage page;
        try
        {
            drone.navigateTo(shareUrl);
            page = drone.getCurrentPage().render();
            Assert.assertTrue(page.isBrowserTitle("login"));
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            page = null;
        }
    }
    
    /**
     * Verify that a logging in with a fake
     * user will not grant the user access
     * and redisplay the login panel.
     * @throws Exception if error
     */
    @Test
    public void loginWithFakeCredentials() throws Exception
    {
        drone.navigateTo(shareUrl);
        LoginPage page = (LoginPage) ShareUtil.loginAs(drone, shareUrl,"fake-admin", "fake-password").render(); 
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertTrue(page.hasErrorMessage());
        Assert.assertTrue(page.getErrorMessage().length() > 1);
    }
    
    @Test(dependsOnMethods = "loginWithFakeCredentials")
    public void checkCSRFToken() throws Exception
    {
        drone.navigateTo(shareUrl);
        ShareUtil.loginAs(drone, shareUrl, username, password);
        String csrfToken1 = getCookieValue();
        Assert.assertNotNull(csrfToken1);
        drone.refresh();
        DashBoardPage dPage = (DashBoardPage)drone.getCurrentPage().render();
        dPage.render();
        String csrfToken2 = getCookieValue();
        Assert.assertNotNull(csrfToken2);   
        if (alfrescoVersion.isCloud())
        {
            Assert.assertFalse(csrfToken1.equals(csrfToken2));
        }
    }
    
    /**
     * Helper method to extract cookie value
     * of Alfresco-CSRFToken
     * @return String token value
     */
    private String getCookieValue()
    {
        Cookie cookie = drone.getCookie("Alfresco-CSRFToken");
        if(cookie != null)
        {
            return cookie.getValue();
        }
        return "";
    }
}
