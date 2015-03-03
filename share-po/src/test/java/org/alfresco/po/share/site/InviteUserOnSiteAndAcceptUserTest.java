/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.po.share.site;

import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify site dashboard page elements are in place.
 * 
 * @author CBairaajoni
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class InviteUserOnSiteAndAcceptUserTest extends AbstractTest
{
    DashBoardPage dashBoard;
    String siteName;
    SiteDashboardPage site;
    InviteMembersPage membersPage;
    String userName = "userz" + System.currentTimeMillis() + "@test.com";

    @BeforeClass(groups = "Enterprise-only")
    public void instantiateSiteMembers() throws Exception
    {
        siteName = "MembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);

        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(userName);
        newPage.inputLastName(userName);
        newPage.inputEmail(userName);
        newPage.inputUsername(userName);
        newPage.inputPassword(userName);
        newPage.inputVerifyPassword(userName);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        userCreated.searchFor(userName).render();
        Assert.assertTrue(userCreated.hasResults());

        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        site = createSite.createNewSite(siteName).render();
    }
    
    @AfterClass(groups = "Enterprise-only")
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
        logout(drone);
    }

    /**
     * Test process of changing the user role.
     * 
     * @throws Exception
     */
    @Test(groups = "Enterprise-only")
    public void testInviteUserAndChangeUserRole() throws Exception
    {
        List<String> searchUsers = null;
        membersPage = site.getSiteNav().selectInvite().render();
        searchUsers = membersPage.searchUser(userName);
        assertNotNull(searchUsers);
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchUsers != null && searchUsers.size() > 0)
            {

                membersPage = membersPage.selectRole(searchUsers.get(0), UserRole.COLLABORATOR);
                assertNotNull(membersPage);
                membersPage = membersPage.clickInviteButton().render();
                assertNotNull(membersPage);
                break;
            }
        }
    }
    
    //TODO shan FIXME !
//    @Test(dependsOnMethods = "testInviteUserAndChangeUserRole")
//    public void testAcceptInvitation() throws Exception
//    {
//        try
//        {
//            DashBoardPage userDashBoardPage = loginAs(userName, userName);
//            assertNotNull(userDashBoardPage);
//
//            MyTasksDashlet task = userDashBoardPage.getDashlet("tasks").render();
//            assertNotNull(task);
//
//            task = task.clickOnTask(siteName).render();
//            assertNotNull(task);
//
//            task.acceptInvitaton();
//            assertNotNull(task);
//
//            logout(drone);
//
//        }
//        catch (Throwable e)
//        {
//            saveScreenShot(drone, "SiteTest.testAcceptInvitation-error");
//            throw new Exception("Unable to Accept Invitation", e);
//        }
//    }
//
//    @Test(dependsOnMethods = "testAcceptInvitation", expectedExceptions = UnsupportedOperationException.class)
//    public void clickOnTaskNullTaskName() throws Exception
//    {
//        MyTasksDashlet task = null;
//        try
//        {
//            DashBoardPage userDashBoardPage = loginAs(userName, userName);
//            assertNotNull(userDashBoardPage);
//            task = userDashBoardPage.getDashlet("tasks").render();
//        }
//        catch (Throwable e)
//        {
//            saveScreenShot(drone, "SiteTest.testAcceptInvitation-error");
//            throw new Exception("Unable to Accept Invitation", e);
//        }
//        task = task.clickOnTask(null);
//
//    }

}
