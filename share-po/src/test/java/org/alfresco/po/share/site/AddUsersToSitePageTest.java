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
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Add Users to Site tests
 *
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class AddUsersToSitePageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AddUsersToSitePageTest.class);

    private SiteMembersPage siteMembersPage;
    private AddUsersToSitePage addUsersToSitePage;
    private DashBoardPage dashBoard;
    private SiteDashboardPage siteDashBoard;
    private SiteFinderPage siteFinderPage;
    private String siteName = "AddUserToSiteTest" + System.currentTimeMillis();
    public static long refreshDuration = 30000;

    @BeforeClass(groups = "Enterprise-only")
    public void createSite() throws Exception
    {    
        siteUtil.createSite(driver, username, password, siteName, "description", "Private");
    }

    /**
     * Adds user to the site
     *
     * @param userName
     * @param role
     * @throws Exception
     */
    private void addUserToSite(String userName, UserRole role) throws Exception
    {
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

        siteDashBoard = siteActions.openSiteDashboard(driver, siteName);

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName, role);
    }

    /**
     * Search for site members
     *
     * @param userName
     * @throws Exception
     */

    public List<String> searchForSiteMembers(String userName, boolean addUsersToSite) throws Exception
    {
        List<String> siteMembers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount + 8; searchCount++)
        {
            try
            {
                if (addUsersToSite)
                {
                    siteMembers = addUsersToSitePage.searchUser(userName);
                } else
                {
                    siteMembers = siteMembersPage.searchUser(userName);
                }
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (siteMembers != null && siteMembers.size() > 0 && siteUtil.hasUser(siteMembers, userName))
            {
                break;
            }
        }
        return siteMembers;
    }


    @Test(groups = "Enterprise-only")
    public void testAddManagerToSite() throws Exception
    {
        // add user to site with manager role
        String siteManagerUserName = "userManager" + System.currentTimeMillis() + "@test.com";

        logger.info("SITE MANAGER *** " + siteName);
        logger.info("USER MANAGER *** " + siteManagerUserName);

        addUserToSite(siteManagerUserName, UserRole.MANAGER);

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 1", count);

        // check the Add Users panel displays added user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
        String addedUserName = addedUserNames.get(0);
        Assert.assertEquals(addedUserName, siteManagerUserName + " " + siteManagerUserName);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole = addedUserRoles.get(0);
        Assert.assertEquals(addedUserRole, "Manager");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = siteMembersPage.searchUser(siteManagerUserName);
        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteManagerUserName) != -1);
        }

        logout(driver);

    }

    @Test(dependsOnMethods = "testAddManagerToSite", groups = "Enterprise-only")
    public void testAddCollaboratorToSite() throws Exception
    {
        // add user to site with manager role
        String siteCollaboratorUserName = "userCollaborator" + System.currentTimeMillis() + "@test.com";

        logger.info("SITE COLLABORATOR *** " + siteName);
        logger.info("USER COLLABORATOR *** " + siteCollaboratorUserName);

        addUserToSite(siteCollaboratorUserName, UserRole.COLLABORATOR);

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 1", count);

        // check the Add Users panel displays added user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
        String addedUserName = addedUserNames.get(0);
        Assert.assertEquals(addedUserName, siteCollaboratorUserName + " " + siteCollaboratorUserName);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole = addedUserRoles.get(0);
        Assert.assertEquals(addedUserRole, "Collaborator");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = siteMembersPage.searchUser(siteCollaboratorUserName);
        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteCollaboratorUserName) != -1);
        }

        logout(driver);

    }

    @Test(dependsOnMethods = "testAddCollaboratorToSite", groups = "Enterprise-only")
    public void testAddContributorToSite() throws Exception
    {
        // add user to site with manager role
        String siteContributorUserName = "userContributor" + System.currentTimeMillis() + "@test.com";


        logger.info("SITE CONTRIBUTOR *** " + siteName);
        logger.info("USER CONTRIBUTOR *** " + siteContributorUserName);

        addUserToSite(siteContributorUserName, UserRole.CONTRIBUTOR);

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 1", count);

        // check the Add Users panel displays added user user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
        String addedUserName = addedUserNames.get(0);
        Assert.assertEquals(addedUserName, siteContributorUserName + " " + siteContributorUserName);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole = addedUserRoles.get(0);
        Assert.assertEquals(addedUserRole, "Contributor");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = siteMembersPage.searchUser(siteContributorUserName);
        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteContributorUserName) != -1);
        }

        logout(driver);

    }

    @Test(dependsOnMethods = "testAddContributorToSite", groups = "Enterprise-only")
    public void testAddConsumerToSite() throws Exception
    {
        // add user to site with manager role
        String siteConsumerUserName = "userConsumer" + System.currentTimeMillis() + "@test.com";

        logger.info("SITE CONSUMER *** " + siteName);
        logger.info("USER CONTRIBUTOR *** " + siteConsumerUserName);

        addUserToSite(siteConsumerUserName, UserRole.CONSUMER);

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 1", count);

        // check the Add Users panel displays added user user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
        String addedUserName = addedUserNames.get(0);
        Assert.assertEquals(addedUserName, siteConsumerUserName + " " + siteConsumerUserName);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole = addedUserRoles.get(0);
        Assert.assertEquals(addedUserRole, "Consumer");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = siteMembersPage.searchUser(siteConsumerUserName);

        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteConsumerUserName) != -1);
        }

        logout(driver);

    }

    @Test(dependsOnMethods = "testAddConsumerToSite", groups = "Enterprise-only")
    public void testAddedUserNavigateToSiteDashboard() throws Exception
    {
        // add user to site with manager role
        String userNavigateToSiteDashboard = "userNavigateToSiteDashboard" + System.currentTimeMillis() + "@test.com";

        logger.info("SITE NAVIGATE TO DASHBOARD *** " + siteName);
        logger.info("USER NAVIGATE TO DASHBOARD *** " + userNavigateToSiteDashboard);

        addUserToSite(userNavigateToSiteDashboard, UserRole.CONSUMER);
        logout(driver);

        dashBoard = loginAs(userNavigateToSiteDashboard, userNavigateToSiteDashboard);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();

        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, siteName);
        siteDashBoard = siteFinderPage.selectSite(siteName).render();

        Assert.assertEquals(siteDashBoard.getPageTitle(), siteName);
        
        //check that added user cannot delete the site from the site configuration drop down
        Assert.assertFalse(siteDashBoard.getSiteNav().isDeleteSiteDisplayed());

        logout(driver);

    }

    @Test(dependsOnMethods = "testAddedUserNavigateToSiteDashboard", groups = "Enterprise-only")
    public void testAddMultipleUsersToSite() throws Exception
    {
        String userMultiple1 = "userMultipleUsersAdedToSite1" + System.currentTimeMillis();
        String userMultiple2 = "userMultipleUsersAdedToSite2" + System.currentTimeMillis();

        String userMultiple3 = "userMultipleUsersAdedToSite3" + System.currentTimeMillis();
        String userMultiple4 = "userMultipleUsersAdedToSite4" + System.currentTimeMillis();

        createEnterpriseUser(userMultiple1);
        createEnterpriseUser(userMultiple2);
        createEnterpriseUser(userMultiple3);
        createEnterpriseUser(userMultiple4);

        dashBoard = loginAs(username, password);
        siteFinderPage = dashBoard.getNav().selectSearchForSites().render();

        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, siteName);
        siteDashBoard = siteFinderPage.selectSite(siteName).render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        // search for user and select user from search results list
        List<String> searchUsers = searchForSiteMembers(userMultiple1, true);
        Assert.assertTrue(searchUsers.size() > 0 && siteUtil.hasUser(searchUsers, userMultiple1));
        addUsersToSitePage.clickSelectUser(userMultiple1).render();

        searchUsers = searchForSiteMembers(userMultiple2, true);
        Assert.assertTrue(searchUsers.size() > 0 && siteUtil.hasUser(searchUsers, userMultiple2));
        addUsersToSitePage.clickSelectUser(userMultiple2).render();

        addUsersToSitePage.setAllRolesTo(UserRole.COLLABORATOR);

        addUsersToSitePage.clickAddUsersButton();

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 2", count);

        // check the Add Users panel displays added user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();

        String addedUserName1 = addedUserNames.get(0);
        String addedUserName2 = addedUserNames.get(1);

        Assert.assertTrue(addedUserName1.indexOf(userMultiple1) != -1);
        Assert.assertTrue(addedUserName2.indexOf(userMultiple2) != -1);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole1 = addedUserRoles.get(0);
        String addedUserRole2 = addedUserRoles.get(1);
        Assert.assertEquals(addedUserRole1, "Collaborator");
        Assert.assertEquals(addedUserRole2, "Collaborator");

        searchUsers = searchForSiteMembers(userMultiple3, true);
        //Assert.assertTrue(searchUsers.size() > 0 && searchUsers.get(0).indexOf(userMultiple3) != -1);
        Assert.assertTrue(searchUsers.size() > 0 && siteUtil.hasUser(searchUsers, userMultiple3));
        addUsersToSitePage.clickSelectUser(userMultiple3).render();
        searchUsers = searchForSiteMembers(userMultiple4, true);
        //Assert.assertTrue(searchUsers.size() > 0 && searchUsers.get(0).indexOf(userMultiple4) != -1);
        Assert.assertTrue(searchUsers.size() > 0 && siteUtil.hasUser(searchUsers, userMultiple4));
        addUsersToSitePage.clickSelectUser(userMultiple4).render();
        addUsersToSitePage.setAllRolesTo(UserRole.COLLABORATOR);
        addUsersToSitePage.clickAddUsersButton();

        addedUserNames = addUsersToSitePage.getAddedUsersNames();

        String addedUserNameNew1 = addedUserNames.get(0);
        String addedUserNameNew2 = addedUserNames.get(1);
        String addedUserNameNew3 = addedUserNames.get(2);
        String addedUserNameNew4 = addedUserNames.get(3);
        Assert.assertTrue(addedUserNameNew1.indexOf(userMultiple3) != -1);
        Assert.assertTrue(addedUserNameNew2.indexOf(userMultiple4) != -1);
        Assert.assertTrue(addedUserNameNew3.indexOf(userMultiple1) != -1);
        Assert.assertTrue(addedUserNameNew4.indexOf(userMultiple2) != -1);

        addedUserRoles = addUsersToSitePage.getAddedUsersRoles();

        String addedUserRoleNew1 = addedUserRoles.get(0);
        String addedUserRoleNew2 = addedUserRoles.get(1);
        String addedUserRoleNew3 = addedUserRoles.get(2);
        String addedUserRoleNew4 = addedUserRoles.get(3);
        Assert.assertEquals(addedUserRoleNew1, "Collaborator");
        Assert.assertEquals(addedUserRoleNew2, "Collaborator");
        Assert.assertEquals(addedUserRoleNew3, "Collaborator");
        Assert.assertEquals(addedUserRoleNew4, "Collaborator");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();

        //List<String> siteMembers1 = siteMembersPage.searchUser(userMultiple1);
        List<String> siteMembers1 = searchForSiteMembers(userMultiple1, false);
        Assert.assertTrue(siteMembers1.get(0).indexOf(userMultiple1) != -1);

        //List<String> siteMembers2 = siteMembersPage.searchUser(userMultiple2);
        List<String> siteMembers2 = searchForSiteMembers(userMultiple2, false);
        Assert.assertTrue(siteMembers2.get(0).indexOf(userMultiple2) != -1);

        //List<String> siteMembers3 = siteMembersPage.searchUser(userMultiple3);
        List<String> siteMembers3 = searchForSiteMembers(userMultiple3, false);
        Assert.assertTrue(siteMembers3.get(0).indexOf(userMultiple3) != -1);

        //List<String> siteMembers4 = siteMembersPage.searchUser(userMultiple4);
        List<String> siteMembers4 = searchForSiteMembers(userMultiple4, false);
        Assert.assertTrue(siteMembers4.get(0).indexOf(userMultiple4) != -1);

        logout(driver);

    }

    // @Test(dependsOnMethods = "testAddMultipleUsersToSite", groups = "Enterprise-only")
    /**
     * public void testAddExternalUserToSite() throws Exception
     * {
     * // add user to site with manager role
     * String userExternalUserName = "userExternal" + System.currentTimeMillis() + "@test.com";
     * System.out.println("SITE EXTERNAL *** " + siteName);
     * System.out.println("USER EXTERNAL *** " + userExternalUserName);
     * dashBoard = loginAs(username, password);
     * userSitesPage = dashBoard.getNav().selectMySites().render();
     * siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
     * addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
     * addUsersToSitePage.enterExternalUserFirstName(userExternalUserName);
     * addUsersToSitePage.enterExternalUserLastName(userExternalUserName);
     * addUsersToSitePage.enterExternalUserEmail(userExternalUserName);
     * addUsersToSitePage.selectExternalUser();
     * addUsersToSitePage.setAllRolesTo(UserRole.CONSUMER);
     * addUsersToSitePage.clickAddUsersButton();
     * // check added users count
     * String count = addUsersToSitePage.getTotalAddedUsersCount();
     * Assert.assertEquals("Total users added 1", count);
     * // check the Add Users panel displays added user user name
     * List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
     * String addedUserName = addedUserNames.get(0);
     * Assert.assertEquals(addedUserName, userExternalUserName + " " + userExternalUserName);
     * // check the Add Users panel displays added user role
     * List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
     * String addedUserRole = addedUserRoles.get(0);
     * Assert.assertEquals(addedUserRole, "Consumer");
     * // check added user is displayed on SiteMembersPage
     * siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
     * List<String> siteMembers = siteMembersPage.searchUser(userExternalUserName);
     * // external user has to accept invite !
     * for (String siteMember : siteMembers)
     * {
     * Assert.assertTrue(siteMember.indexOf(userExternalUserName) != -1);
     * }
     * logout(driver);
     * }
     **/

    // @Test(dependsOnMethods = "testAddExternalUserToSite", groups = "Enterprise-only")
    @Test(dependsOnMethods = "testAddMultipleUsersToSite", groups = "Enterprise-only")
    public void testRemoveSelectedUser() throws Exception
    {
        // create user
        String userRemoveUserName = "userRemove" + System.currentTimeMillis();

        createEnterpriseUser(userRemoveUserName);


        dashBoard = loginAs(username, password);

        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        // search for user and select user from search results list
        List<String> searchUsers = searchForSiteMembers(userRemoveUserName, true);
        Assert.assertTrue(searchUsers.size() > 0 && siteUtil.hasUser(searchUsers, userRemoveUserName));
        addUsersToSitePage.clickSelectUser(userRemoveUserName);

        // remove user from the list of selected users
        addUsersToSitePage.removeSelectedUser(userRemoveUserName);

        List<String> selectedUserNames = addUsersToSitePage.getSelectedUserNames();
        Assert.assertFalse(selectedUserNames.contains(userRemoveUserName));

        logout(driver);

    }

    @Test(dependsOnMethods = "testRemoveSelectedUser", groups = "Enterprise-only")
    public void testNavigateToPendingInvitesPage() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToPendingInvitesPage();
        Assert.assertTrue(resolvePage(driver).render() instanceof PendingInvitesPage);

        logout(driver);

    }

    @Test(dependsOnMethods = "testNavigateToPendingInvitesPage", groups = "Enterprise-only")
    public void testNavigateToGroupsInvitePage() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToSiteGroupsPage();
        Assert.assertTrue(resolvePage(driver).render() instanceof SiteGroupsPage);

        logout(driver);

    }

    @Test(dependsOnMethods = "testNavigateToGroupsInvitePage", groups = "Enterprise-only")
    public void testNavigateToSiteMembersPage() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToMembersSitePage();
        Assert.assertTrue(resolvePage(driver).render() instanceof SiteMembersPage);

        logout(driver);

    }

    @Test(dependsOnMethods = "testNavigateToSiteMembersPage", groups = "Enterprise-only")
    public void testInfoTooltip() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteDashBoard = dashBoard.getNav().selectMostRecentSite().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage = addUsersToSitePage.clickOnInfoTooltip().render();
        Assert.assertTrue(addUsersToSitePage.isRoleInfoTooltipDisplayed());

        logout(driver);

    }

}
