/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class SiteMembersPageTest extends AbstractTest
{
    SiteMembersPage siteMembersPage;
    InviteMembersPage inviteMembersPage;
    WebElement user;
    DashBoardPage dashBoard;
    String siteName;

    // user should be created.
    String userName = "user" + System.currentTimeMillis() + "@test.com";
    public static long refreshDuration = 15000;

    @BeforeClass(groups = "Enterprise-only")
    public void instantiateMembers() throws Exception
    {
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);
        if (!alfrescoVersion.isCloud())
        {
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
        }
        else
        {
            // TODO: Cloud user creation needs to be implemented
            // Assert.assertTrue(UserUtil.createUser(userName, userName,
            // userName, userName, userName, alfrescoVersion.isCloud(),
            // shareUrl));
        }
        SharePage page = drone.getCurrentPage().render();
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        if (!alfrescoVersion.isCloud())
        {
            List<String> searchUsers = null;
            inviteMembersPage = site.getSiteNav().selectInvite().render();
            for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
            {
                searchUsers = inviteMembersPage.searchUser(userName);
                waitInSeconds(1);
                try
                {
                    if (searchUsers != null && searchUsers.size() > 0 && searchUsers.get(0).toString().contains(userName))
                    {
                        inviteMembersPage.selectRole(searchUsers.get(0), UserRole.COLLABORATOR).render();
                        inviteMembersPage.clickInviteButton().render();
                        break;
                    }
                }
                catch (Exception e)
                {
                    saveScreenShot("SiteTest.instantiateMembers-error");
                    throw new Exception("Waiting for object to load", e);
                }
                try
                {
                    inviteMembersPage.renderWithUserSearchResults(refreshDuration);
                }
                catch (PageRenderTimeException exception)
                {
                }
            }
           
            ShareUtil.logout(drone);
            DashBoardPage userDashBoardPage = loginAs(userName, userName).render();
            MyTasksDashlet task = userDashBoardPage.getDashlet("tasks").render();
            EditTaskPage editTaskPage = task.clickOnTask(siteName).render();
            userDashBoardPage = editTaskPage.selectAcceptButton().render();
            ShareUtil.logout(drone);
            dashBoard = loginAs(username, password);
            drone.navigateTo(String.format("%s/page/site/%s/dashboard", shareUrl, siteName));
            site = drone.getCurrentPage().render();
        }
        else
        {
            // TODO: In Cloud environemnt, need to implement the inviting and
            // accepting the invitation to join on another user site page.
        }
        siteMembersPage = site.getSiteNav().selectMembers().render();
    }

    @Test(groups = "Enterprise-only")
    public void testSearchUser() throws Exception
    {
        List<String> searchUsers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            try
            {
                searchUsers = siteMembersPage.searchUser(userName);
                siteMembersPage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (searchUsers != null && searchUsers.size() > 0)
            {
                break;
            }
        }
        Assert.assertTrue(searchUsers.size() > 0);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testSearchUser")
    public void testAssignRole() throws Exception
    {
        Assert.assertNotNull(siteMembersPage.assignRole(userName, UserRole.COLLABORATOR));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testAssignRole")
    public void testRemoveUser() throws Exception
    {
        siteMembersPage = siteMembersPage.removeUser(userName);
        Assert.assertNotNull(siteMembersPage);

        List<String> searchUsers = siteMembersPage.searchUser(userName);

        Assert.assertTrue(searchUsers.size() == 0);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testAssignRole", expectedExceptions = { UnsupportedOperationException.class })
    public void testAssignRoleToNullUser()
    {
        Assert.assertNotNull(siteMembersPage.assignRole(null, UserRole.COLLABORATOR));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testAssignRoleToNullUser", expectedExceptions = { UnsupportedOperationException.class })
    public void testAssignNullRole()
    {
        Assert.assertNotNull(siteMembersPage.assignRole(userName, null));
    }

    @AfterClass(groups = "Enterprise-only")
    public void deleteSite() throws Exception
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = siteFinder.deleteSite(siteName).render();
    }
}