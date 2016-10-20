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
package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.test.FailedTestListener;
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
    AddUsersToSitePage addUsersToSitePage;
    WebElement user;
    DashBoardPage dashBoard;
    String siteName;

    // user should be created.
    String userName = "user" + System.currentTimeMillis() + "@test.com";
    public static long refreshDuration = 30000;

    @BeforeClass(groups = "Enterprise-only")
    public void instantiateMembers() throws Exception
    {
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);
        UserSearchPage userSearchPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userSearchPage.selectNewUser().render();
        newPage.inputFirstName(userName);
        newPage.inputLastName(userName);
        newPage.inputEmail(userName);
        newPage.inputUsername(userName);
        newPage.inputPassword(userName);
        newPage.inputVerifyPassword(userName);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        userCreated.searchFor(userName).render();
        
        siteUtil.createSite(driver, username, password, siteName, "description", "public");
        
        SiteDashboardPage site = siteActions.openSiteDashboard(driver, siteName);
        
        addUsersToSitePage = site.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName, UserRole.COLLABORATOR);
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
    }

     
    @Test(groups = "Enterprise-only")
    public void testSearchUser() throws Exception
    {
        List<String> searchUsers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount + 8; searchCount++)
        {
            try
            {
                searchUsers = siteMembersPage.searchUser(userName);
                siteMembersPage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (searchUsers != null && searchUsers.size() > 0 && searchUsers.get(0).indexOf(userName) != -1)
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