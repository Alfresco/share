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
                //searchUsers = addUsersToSitePage.searchUser(userName);
                searchUsers = addUsersToSitePage.searchUser("user");
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
        /**
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("1", count);
        **/

        // check the Add Users panel displays added user user name
        /**
        String addedUserName = addUsersToSitePage.getAddedUserName(siteManagerUserName);
        Assert.assertEquals(addedUserName, siteManagerUserName);
        **/

        // check the Add Users panel displays added user role
        /**
        String addedUserRole = addUsersToSitePage.getAddedUserRole(siteManagerUserName);
        Assert.assertEquals(addedUserRole, "Manager");
        **/

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = searchForSiteMembers(siteManagerUserName);
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
        /**
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("1", count);
        **/

        // check the Add Users panel displays added user user name
        /**
        String addedUserName = addUsersToSitePage.getAddedUserName(siteCollaboratorUserName);
        Assert.assertEquals(addedUserName, siteCollaboratorUserName);
        **/

        // check the Add Users panel displays added user role
        /**
        String addedUserRole = addUsersToSitePage.getAddedUserRole(siteCollaboratorUserName);
        Assert.assertEquals(addedUserRole, "Collaborator");
        **/

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = searchForSiteMembers(siteCollaboratorUserName);
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
        /**
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("1", count);
        **/

        // check the Add Users panel displays added user user name
        /**
        String addedUserName = addUsersToSitePage.getAddedUserName(siteContributorUserName);
        Assert.assertEquals(addedUserName, siteContributorUserName);
        **/

        // check the Add Users panel displays added user role
        /**
        String addedUserRole = addUsersToSitePage.getAddedUserRole(siteContributorUserName);
        Assert.assertEquals(addedUserRole, "Contributor");
        **/

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = searchForSiteMembers(siteContributorUserName);
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
        /**
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("1", count);
        **/

        // check the Add Users panel displays added user user name
        /**
        String addedUserName = addUsersToSitePage.getAddedUserName(siteConsumerUserName);
        Assert.assertEquals(addedUserName, siteConsumerUserName);
        **/
        // check the Add Users panel displays added user role
        /**
        String addedUserRole = addUsersToSitePage.getAddedUserRole(siteConsumerUserName);
        Assert.assertEquals(addedUserRole, "Consumer");
        **/

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = searchForSiteMembers(siteConsumerUserName);
        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteConsumerUserName) != -1);
        }

        ShareUtil.logout(drone);

    }

    @Test(dependsOnMethods = "testAddConsumerToSite", groups = "Enterprise-only")
    public void testAddExternalUserToSite() throws Exception
    {
        // add user to site with manager role
        String siteExternalUserName = "userExternal" + System.currentTimeMillis() + "@test.com";
               
        System.out.println("SITE EXTERNAL *** " + siteName);
        System.out.println("USER EXTERNAL *** " + siteExternalUserName);
        
        dashBoard = loginAs(username, password);
        userSitesPage = dashBoard.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
        
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        
        addUsersToSitePage.enterExternalUserFirstName(siteExternalUserName);
        addUsersToSitePage.enterExternalUserLastName(siteExternalUserName);
        addUsersToSitePage.enterExternalUserEmail(siteExternalUserName);
        addUsersToSitePage.selectExternalUser();
        addUsersToSitePage.setAllRolesTo(UserRole.CONSUMER);
        addUsersToSitePage.clickAddUsersButton();

        // check added users count
        /**
        String count = addUsersToSitePage.getTotalAddedUsersCount();
        Assert.assertEquals("1", count);
        **/

        // check the Add Users panel displays added user user name
        /**
        String addedUserName = addUsersToSitePage.getAddedUserName(siteExternalUserName);
        Assert.assertEquals(addedUserName, siteExternalUserName);
        **/

        // check the Add Users panel displays added user role
        /**
        String addedUserRole = addUsersToSitePage.getAddedUserRole(siteExternalUserName);
        Assert.assertEquals(addedUserRole, "Consumer");
        **/

        // check added user is displayed on SiteMembersPage
        siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        List<String> siteMembers = searchForSiteMembers(siteExternalUserName);
        
        //external user has to accept invite !
        
        for (String siteMember : siteMembers)
        {
            Assert.assertTrue(siteMember.indexOf(siteExternalUserName) != -1);
        }
        
        ShareUtil.logout(drone);
    }

    @Test(dependsOnMethods = "testAddExternalUserToSite", groups = "Enterprise-only")
    public void testRemoveSelectedUser() throws Exception
    {
        // create user
        String siteRemoveUserName = "userRemove" + System.currentTimeMillis();
        createEnterpriseUser(siteRemoveUserName);
        dashBoard = loginAs(username, password);
        SharePage page = drone.getCurrentPage().render();
        UserSitesPage userSitesPage = page.getNav().selectMySites().render();
        siteDashBoard = userSitesPage.getSite(siteName).clickOnSiteName().render();
        addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();

        // search for user and select user from search results list
        List<String> searchUsers = searchForSiteMembers("user");
      
        Assert.assertTrue(searchUsers.size() > 0);
        addUsersToSitePage.clickSelectUser(siteRemoveUserName);
 
        // remove user from the list of selected users
        addUsersToSitePage.removeSelectedUser(siteRemoveUserName);
        List<String> selectedUserNames = addUsersToSitePage.getSelectedUserNames();
        Assert.assertFalse(selectedUserNames.contains(siteRemoveUserName));
        
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

}
