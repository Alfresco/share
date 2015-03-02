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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.*;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.user.MyProfilePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
public class MyProfileDashletTest extends AbstractDashletTest
{
    private DashBoardPage dashBoardPage;
    private MyProfilePage myProfilePage;
    private CustomiseUserDashboardPage customiseUserDashboardPage;
    private AlfrescoVersion version;
    private String userName;
    private String email;
    private MyProfileDashlet dashlet;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows a summary of your personal details. From here you can access your full user profile.";

    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        userName = "UserMeeting" + System.currentTimeMillis();
        email = userName + "@test.com";

        version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);
            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }
    }

    @Test
    public void instantiateMyProfileDashlet()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard();
        customiseUserDashboardPage.render();

        dashBoard = customiseUserDashboardPage.addDashlet(Dashlets.MY_PROFILE, 1).render();

        dashlet = new MyProfileDashlet(drone).render();
    }

    @Test(dependsOnMethods = "instantiateMyProfileDashlet")
    public void verifyHelpIcon() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.isHelpIconPresent(), "Help icon isn't displayed");
        dashlet.clickOnHelpIcon();
        assertTrue(dashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        String actualHelpBalloonMsg = dashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        dashlet.closeHelpBallon();
        assertFalse(dashlet.isBalloonDisplayed(), "Baloon popup is displayed");
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void isUserNameDisplayed() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.getUserName().contains(userName), "User name isn't presented");
    }

    @Test(dependsOnMethods = "isUserNameDisplayed")
    public void isAvatarDisplayed() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.isAvatarDisplayed(), "Avatar isn't displayed");
    }

    @Test(dependsOnMethods = "isAvatarDisplayed")
    public void isEmailPresented() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertEquals(dashlet.getEmailName(), email);
    }

    @Test(dependsOnMethods = "isEmailPresented")
    public void clickOnUserName()throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        myProfilePage = dashlet.clickOnUserName().render();
        assertNotNull(myProfilePage);
    }

    @Test(dependsOnMethods = "clickOnUserName")
    public void clickViewFullProfileButton() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoardPage = page.getNav().selectMyDashBoard();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.isViewFullProfileDisplayed(), "View Full Profile is absent");
        myProfilePage = dashlet.clickViewFullProfileButton().render();
        assertNotNull(myProfilePage);
    }

}
