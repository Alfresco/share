/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.user;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ChangePasswordPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Change Password page elements are in place.
 * Created by Olga Lokhach
 */
@Listeners(FailedTestListener.class)
public class ChangePasswordPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private MyProfilePage myProfilePage;
    private ChangePasswordPage changePasswordPage;
    private String userName;
    private String newPassword;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {
        userName = "User_" + System.currentTimeMillis();
        newPassword = UNAME_PASSWORD + "123";
        createEnterpriseUser(userName);
        ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
    }

    @Test(groups = { "Enterprise-only" })
    public void openChangePasswordPage()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        changePasswordPage = myProfilePage.getProfileNav().selectChangePassword().render();
        assertNotNull(changePasswordPage);
    }

    @Test(groups="Enterprise-only", dependsOnMethods = "openChangePasswordPage")
    public void isHeaderTitlePresent() throws Exception
    {
        String title = "Change User Password";
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        changePasswordPage = myProfilePage.getProfileNav().selectChangePassword().render();
        assertTrue(changePasswordPage.isTitlePresent(title), "Title is incorrect");
    }

    @Test(groups="Enterprise-only", dependsOnMethods = "isHeaderTitlePresent")
    public void changePassword() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        changePasswordPage = myProfilePage.getProfileNav().selectChangePassword().render();
        changePasswordPage.changePassword(UNAME_PASSWORD, newPassword);
        ShareUtil.logout(drone);
        SharePage resultPage = login(drone, shareUrl, userName, UNAME_PASSWORD).render();
        assertFalse(resultPage.isLoggedIn(), userName + " can login with old password");
        resultPage = ShareUtil.loginAs(drone, shareUrl, userName, newPassword).render();
        assertTrue(resultPage.isLoggedIn(), userName + " can't login with new password");
    }

    private HtmlPage login(WebDrone drone, String shareUrl ,String userName, String userPassword)
    {
        HtmlPage resultPage = null;

        try
        {
            resultPage = ShareUtil.loginAs(drone, shareUrl, userName, userPassword);
        }
        catch (SkipException se)
        {
        }
        return resultPage;
    }
}
