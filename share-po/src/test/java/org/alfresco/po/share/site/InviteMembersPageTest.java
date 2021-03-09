/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;

import org.alfresco.test.FailedTestListener;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Replaced by AddUsersToSitePageTest
 * 
 * @author Shan Nagarajan
 */
@Deprecated
@Listeners(FailedTestListener.class)
public class InviteMembersPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(InviteMembersPageTest.class);

    InviteMembersPage membersPage;
    SiteNavigation siteNavigation;
    String user;
    WebElement invitee;
    DashBoardPage dashBoard;
    String siteName;
    List<WebElement> inviteesList;
    String userNameTest;

    @BeforeClass(groups = "Enterprise-only")
    public void instantiateMembers() throws Exception
    {
        userNameTest = "user" + System.currentTimeMillis() + "@test.com";
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);

        // Creating new user.
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(userNameTest);
        newPage.inputLastName(userNameTest);
        newPage.inputEmail(userNameTest);
        newPage.inputUsername(userNameTest);
        newPage.inputPassword(userNameTest);
        newPage.inputVerifyPassword(userNameTest);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        userCreated.searchFor(userNameTest).render();
        Assert.assertTrue(userCreated.hasResults());
        /*
         * TODO: Cloud user creation needs to be implemented
         * Assert.assertTrue(UserUtil.createUser(userName, userName,
         * userName, userName, userName, alfrescoVersion.isCloud(),
         * shareUrl));
         */

        // TODO: Cloud user creation needs to be implemented
        // Assert.assertTrue(UserUtil.createUser(userName, userName,
        // userName, userName, userName, alfrescoVersion.isCloud(),
        // shareUrl));

        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        siteNavigation = site.getSiteNav();
        membersPage = siteNavigation.selectInvite().render();

    }

    @Test(groups = "Enterprise-only")
    public void testIsMembersPageTitlePresent()
    {
        Assert.assertTrue(membersPage.titlePresent());
    }

    @Test(groups = "Enterprise-only")
    public void testSearchUser() throws Exception
    {
        List<String> searchUsers = null;      
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            searchUsers = membersPage.searchUser(userNameTest);
            if (searchUsers != null && searchUsers.size() > 0 && searchUsers.get(0).toString().contains(userNameTest))
            {
                break;
            }
        }      
        user = searchUsers.get(0);
        Assert.assertNotNull(user);
        Assert.assertTrue(user.contains(userNameTest));
    }

    @Test(dependsOnMethods = "testAddingNullUser", groups = "Enterprise-only")
    public void testAddUser()
    {
        Assert.assertNotNull(membersPage.clickAddUser(user));
    }

    @Test(dependsOnMethods = "testSearchUser", groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testAddingNullUser()
    {
        Assert.assertNotNull(membersPage.clickAddUser(null));
    }

    @Test(dependsOnMethods = "testAddUser", groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testSelectInviteeAndAssignRoleToNull()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(user, null));
    }

    @Test(dependsOnMethods = "testSelectInviteeAndAssignRoleToNull", groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testSelectNullInviteeAndAssignRole()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(null, UserRole.COLLABORATOR));
    }

    @Test(dependsOnMethods = "testSelectNullInviteeAndAssignRole", groups = "Enterprise-only")
    public void testSelectInviteeAndAssignRole()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(user, UserRole.COLLABORATOR));
    }

    @Test(dependsOnMethods = "testSelectInviteeAndAssignRole", groups = "Enterprise-only")
    public void testStateMethods()
    {
        Assert.assertFalse(membersPage.isAddButtonEnabledFor(userNameTest));
        Assert.assertTrue(membersPage.isSelectRoleEnabledFor(userNameTest));
        Assert.assertTrue(membersPage.isRemoveIconEnabledFor(userNameTest));
    }

    @Test(dependsOnMethods = "testStateMethods", groups = "Enterprise-only")
    public void testSelectAllRole()
    {
        membersPage.selectRoleForAll(UserRole.MANAGER);
    }

    @Test(dependsOnMethods = "testSelectAllRole", groups = "Enterprise-only")
    public void testEnabledInviteButton()
    {
        Assert.assertTrue(membersPage.isInviteButtonEnabled());
        Assert.assertNotNull(membersPage.clickInviteButton());
    }

    @Test(dependsOnMethods = "testEnabledInviteButton", groups = "Enterprise-only")
    public void testNavigateToPendingInvitesPage()
    {
        membersPage.navigateToPendingInvitesPage();
        Assert.assertTrue(resolvePage(driver).render() instanceof PendingInvitesPage);
        membersPage = siteNavigation.selectInvite().render();
    }

    @Test(dependsOnMethods = "testNavigateToPendingInvitesPage", groups = "Enterprise-only")
    public void testNavigateToGroupsInvitePage()
    {
        membersPage.navigateToSiteGroupsPage();
        Assert.assertTrue(resolvePage(driver).render() instanceof SiteGroupsPage);
        membersPage = siteNavigation.selectInvite().render();
    }

    @Test(dependsOnMethods = "testNavigateToGroupsInvitePage", groups = "Enterprise-only")
    public void testSendExtInvite()
    {
        membersPage.invExternalUser("Gogi", "Gruzinidze", "gogi@vaikrasava.com", UserRole.CONSUMER);
    }

    @AfterClass(groups = "Enterprise-only")
    public void deleteSite() throws Exception
    {
        try
        {
            if (driver != null && !StringUtils.isEmpty(siteName))
            {
                siteUtil.deleteSite(username, password, siteName);
            }
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
}
