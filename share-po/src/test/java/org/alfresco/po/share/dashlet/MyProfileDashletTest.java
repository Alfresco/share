/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.user.MyProfilePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
public class MyProfileDashletTest extends AbstractDashletTest
{
    private DashBoardPage dashBoardPage;
    private MyProfilePage myProfilePage;
    private CustomiseUserDashboardPage customiseUserDashboardPage;
    
    private String userName;
    private String email;
    private MyProfileDashlet dashlet;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows a summary of your personal details. From here you can access your full user profile.";

    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        userName = "UserMeeting" + System.currentTimeMillis();
        email = userName + "@test.com";
        createEnterpriseUser(userName);
        shareUtil.loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
    }

    @Test
    public void instantiateMyProfileDashlet()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();

        customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard();
        customiseUserDashboardPage.render();

        dashBoard = customiseUserDashboardPage.addDashlet(Dashlets.MY_PROFILE, 1).render();

        dashlet = factoryPage.instantiatePage(driver, MyProfileDashlet.class);
    }

    @Test(dependsOnMethods = "instantiateMyProfileDashlet")
    public void verifyHelpIcon() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
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
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.getUserName().contains(userName), "User name isn't presented");
    }

    @Test(dependsOnMethods = "isUserNameDisplayed")
    public void isAvatarDisplayed() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.isAvatarDisplayed(), "Avatar isn't displayed");
    }

    @Test(dependsOnMethods = "isAvatarDisplayed")
    public void isEmailPresented() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertEquals(dashlet.getEmailName(), email);
    }

    @Test(dependsOnMethods = "isEmailPresented")
    public void clickOnUserName()throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        myProfilePage = dashlet.clickOnUserName().render();
        assertNotNull(myProfilePage);
    }

    @Test(dependsOnMethods = "clickOnUserName")
    public void clickViewFullProfileButton() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        dashlet = dashBoardPage.getDashlet("my-profile").render();
        assertTrue(dashlet.isViewFullProfileDisplayed(), "View Full Profile is absent");
        myProfilePage = dashlet.clickViewFullProfileButton().render();
        assertNotNull(myProfilePage);
    }

}
