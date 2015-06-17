package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class AddUsersToSitePageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AddUsersToSitePageTest.class);

    private SiteMembersPage siteMembersPage;
    private AddUsersToSitePage addUsersToSitePage;
    private DashBoardPage dashBoard;
    private SiteDashboardPage siteDashBoard;
    private UserSitesPage userSitesPage;
    private String siteName = "AddUserToSiteTest" + System.currentTimeMillis();
    private static long refreshDuration = 15000;

    @BeforeClass(groups = "Enterprise-only")
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        createSitePage.createNewSite(siteName).render();
        ShareUtil.logout(drone);
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
        userSitesPage = page.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
        if (!alfrescoVersion.isCloud())
        {
            List<String> searchUsers = null;
            addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
            for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
            {
                searchUsers = addUsersToSitePage.searchUser(userName);
                try
                {
                    if (searchUsers != null && searchUsers.size() > 0)
                    {
                        addUsersToSitePage.clickSelectUser(userName);
                        addUsersToSitePage.setUserRoles(userName, role);
                        addUsersToSitePage.clickAddUsersButton();
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
                    addUsersToSitePage.renderWithUserSearchResults(refreshDuration);
                }
                catch (PageRenderTimeException exception)
                {
                }
            }

        }
        else
        {
            // TODO: In Cloud environemnt, need to implement the inviting and
            // accepting the invitation to join on another user site page.
        }
    }

    /**
     * Search for site members
     * 
     * @param userName
     * @throws Exception
     */

    public List<String> searchForSiteMembers(String userName) throws Exception
    {
        List<String> siteMembers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            try
            {
                siteMembers = addUsersToSitePage.searchUser(userName);
                addUsersToSitePage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (siteMembers != null && siteMembers.size() > 0)
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

        System.out.println("SITE MANAGER *** " + siteName);
        System.out.println("USER MANAGER *** " + siteManagerUserName);

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

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddManagerToSite", groups = "Enterprise-only")
    public void testAddCollaboratorToSite() throws Exception
    {
        // add user to site with manager role
        String siteCollaboratorUserName = "userCollaborator" + System.currentTimeMillis() + "@test.com";

        System.out.println("SITE COLLABORATOR *** " + siteName);
        System.out.println("USER COLLABORATOR *** " + siteCollaboratorUserName);

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

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddCollaboratorToSite", groups = "Enterprise-only")
    public void testAddContributorToSite() throws Exception
    {
        // add user to site with manager role
        String siteContributorUserName = "userContributor" + System.currentTimeMillis() + "@test.com";

        System.out.println("SITE CONTRIBUTOR *** " + siteName);
        System.out.println("USER CONTRIBUTOR *** " + siteContributorUserName);

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

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddContributorToSite", groups = "Enterprise-only")
    public void testAddConsumerToSite() throws Exception
    {
        // add user to site with manager role
        String siteConsumerUserName = "userConsumer" + System.currentTimeMillis() + "@test.com";

        System.out.println("SITE CONSUMER *** " + siteName);
        System.out.println("USER CONTRIBUTOR *** " + siteConsumerUserName);

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

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddConsumerToSite", groups = "Enterprise-only")
    public void testAddedUserNavigateToSiteDashboard() throws Exception
    {
        // add user to site with manager role
        String userNavigateToSiteDashboard = "userNavigateToSiteDashboard" + System.currentTimeMillis() + "@test.com";

        System.out.println("SITE NAVIGATE TO DASHBOARD *** " + siteName);
        System.out.println("USER NAVIGATE TO DASHBOARD *** " + userNavigateToSiteDashboard);

        addUserToSite(userNavigateToSiteDashboard, UserRole.CONSUMER);

        ShareUtil.logout(drone);

        dashBoard = loginAs(userNavigateToSiteDashboard, userNavigateToSiteDashboard);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        Assert.assertEquals(siteDashBoard.getPageTitle(), siteName);

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddedUserNavigateToSiteDashboard", groups = "Enterprise-only")
    public void testAddMultipleUsersToSite() throws Exception
    {
        String userMultiple1 = "userMultipleUsersAdedToSite1" + System.currentTimeMillis();
        String userMultiple2 = "userMultipleUsersAdedToSite2" + System.currentTimeMillis();

        System.out.println("SITE MULTIPLE USERS *** " + siteName);
        System.out.println("USER MULTIPLE USERS1 *** " + userMultiple1);
        System.out.println("USER MULTIPLE USERS2 *** " + userMultiple2);

        createEnterpriseUser(userMultiple1);
        createEnterpriseUser(userMultiple2);

        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        // search for user and select user from search results list
        List<String> searchUsers = searchForSiteMembers(userMultiple1);
        Assert.assertTrue(searchUsers.size() > 0);

        addUsersToSitePage.clickSelectUser(userMultiple1).render();

        searchUsers = searchForSiteMembers(userMultiple2);
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

        Assert.assertTrue(addedUserName2.indexOf(userMultiple1) != -1);
        Assert.assertTrue(addedUserName1.indexOf(userMultiple2) != -1);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole1 = addedUserRoles.get(0);
        String addedUserRole2 = addedUserRoles.get(1);
        Assert.assertEquals(addedUserRole1, "Collaborator");
        Assert.assertEquals(addedUserRole2, "Collaborator");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers1 = siteMembersPage.searchUser(userMultiple1);
        for (String siteMember : siteMembers1)
        {
            Assert.assertTrue(siteMember.indexOf(userMultiple1) != -1);
        }
        List<String> siteMembers2 = siteMembersPage.searchUser(userMultiple2);
        for (String siteMember : siteMembers2)
        {
            Assert.assertTrue(siteMember.indexOf(userMultiple2) != -1);
        }

        ShareUtil.logout(drone);

    }

    //@Test(dependsOnMethods = "testAddMultipleUsersToSite", groups = "Enterprise-only")
    /**
    public void testAddExternalUserToSite() throws Exception
    {
        // add user to site with manager role
        String userExternalUserName = "userExternal" + System.currentTimeMillis() + "@test.com";

        System.out.println("SITE EXTERNAL *** " + siteName);
        System.out.println("USER EXTERNAL *** " + userExternalUserName);

        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.enterExternalUserFirstName(userExternalUserName);
        addUsersToSitePage.enterExternalUserLastName(userExternalUserName);
        addUsersToSitePage.enterExternalUserEmail(userExternalUserName);
        addUsersToSitePage.selectExternalUser();
        addUsersToSitePage.setAllRolesTo(UserRole.CONSUMER);
        addUsersToSitePage.clickAddUsersButton();

        // check added users count
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("Total users added 1", count);

        // check the Add Users panel displays added user user name
        List<String> addedUserNames = addUsersToSitePage.getAddedUsersNames();
        String addedUserName = addedUserNames.get(0);
        Assert.assertEquals(addedUserName, userExternalUserName + " " + userExternalUserName);

        // check the Add Users panel displays added user role
        List<String> addedUserRoles = addUsersToSitePage.getAddedUsersRoles();
        String addedUserRole = addedUserRoles.get(0);
        Assert.assertEquals(addedUserRole, "Consumer");

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = siteMembersPage.searchUser(userExternalUserName);

        // external user has to accept invite !

        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(userExternalUserName) != -1);
        }

        ShareUtil.logout(drone);
    }
    **/

    //@Test(dependsOnMethods = "testAddExternalUserToSite", groups = "Enterprise-only")
    @Test(dependsOnMethods = "testAddMultipleUsersToSite", groups = "Enterprise-only")
    public void testRemoveSelectedUser() throws Exception
    {
        // create user
        String userRemoveUserName = "userRemove" + System.currentTimeMillis();
        createEnterpriseUser(userRemoveUserName);
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        // search for user and select user from search results list
        List<String> searchUsers = searchForSiteMembers(userRemoveUserName + "@test.com");
        Assert.assertTrue(searchUsers.size() > 0);
        addUsersToSitePage.clickSelectUser(userRemoveUserName);

        // remove user from the list of selected users
        addUsersToSitePage.removeSelectedUser(userRemoveUserName);

        List<String> selectedUserNames = addUsersToSitePage.getSelectedUserNames();
        Assert.assertFalse(selectedUserNames.contains(userRemoveUserName));

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testRemoveSelectedUser", groups = "Enterprise-only")
    public void testNavigateToPendingInvitesPage() throws Exception
    {
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToPendingInvitesPage();
        Assert.assertTrue(drone.getCurrentPage().render() instanceof PendingInvitesPage);

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testNavigateToPendingInvitesPage", groups = "Enterprise-only")
    public void testNavigateToGroupsInvatePage() throws Exception
    {
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToSiteGroupsPage();
        Assert.assertTrue(drone.getCurrentPage().render() instanceof SiteGroupsPage);

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testNavigateToGroupsInvatePage", groups = "Enterprise-only")
    public void testNavigateToSiteMembersPage() throws Exception
    {
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.navigateToMembersSitePage();
        Assert.assertTrue(drone.getCurrentPage().render() instanceof SiteMembersPage);

        ShareUtil.logout(drone);

    }


    @Test(dependsOnMethods = "testNavigateToSiteMembersPage", groups = "Enterprise-only")
    public void testInfoTooltip() throws Exception
    {
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();

        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        addUsersToSitePage.clickOnInfoTooltip();
        Assert.assertNotNull(addUsersToSitePage.getTooltipHeader());

        ShareUtil.logout(drone);

    }




}
