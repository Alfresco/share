/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.usecases.pendingRequest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.AddUserGroupPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.po.share.NewGroupPage.ActionButton;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteFinderPage.ButtonType;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify pending requests is operating correctly.
 *
 * @author Charu
 */
@Listeners(FailedTestListener.class)
public class PendingRequestPageETETest extends AbstractDocumentTest
{
    PendingInvitesPage pendingRequestPage;
    private AddUsersToSitePage addUsersToSitePage;
    MyTasksPage myTasksPage;
    AddGroupsPage addGroupsPage;
    SiteGroupsPage siteGroupsPage;
    private static DashBoardPage dashBoard;
    private static SiteFinderPage siteFinder;
    private static SiteDashboardPage siteDashboardPage;
    private static SiteMembersPage siteMembersPage;
    private static EditTaskPage editTaskPage;
    String groupName = "GROUP1" + System.currentTimeMillis();
    String siteName;
    String siteName1;
    String modSiteName1;
    String modSiteName2;
    String modSiteName3;
    String modSiteName4;
    String modSiteName5;
    String modSiteName6;
    String modSiteName7;

    List<WebElement> perndingRequestList;
    private String userName1;
    private String userName2;
    private String userName3;
    private String userName4;

    @Autowired
    SiteActions siteActions;

    /**
     * Pre test setup to create users and moderated sites and add/request to join sites
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        userName1 = "user1" + System.currentTimeMillis();
        userName2 = "user2" + System.currentTimeMillis();
        userName3 = "user3" + System.currentTimeMillis();
        userName4 = "user4" + System.currentTimeMillis();

        modSiteName1 = "modSN1" + System.currentTimeMillis();
        modSiteName2 = "modSN2" + System.currentTimeMillis();
        modSiteName3 = "modSN3" + System.currentTimeMillis();
        modSiteName4 = "modSN4" + System.currentTimeMillis();
        modSiteName5 = "modSN5" + System.currentTimeMillis();
        modSiteName6 = "modSN6" + System.currentTimeMillis();
        modSiteName7 = "modSN7" + System.currentTimeMillis();

        createUser();

        loginAs(userName1, UNAME_PASSWORD);

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName1, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName2, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName3, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName4, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName5, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName6, "", "Moderated");

        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, modSiteName7, "", "Moderated");

        // Add user4 to modSiteName5 with Manager role
        siteDashboardPage = siteActions.openSiteDashboard(driver, modSiteName5).render();
        AddUsersToSitePage addUsersToSitePage = siteDashboardPage.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName4, UserRole.MANAGER);

        loginAs(userName2, UNAME_PASSWORD);

        siteActions.requestToJoinModSite(driver, modSiteName1);
        siteActions.requestToJoinModSite(driver, modSiteName2);
        siteActions.requestToJoinModSite(driver, modSiteName3);
        siteActions.requestToJoinModSite(driver, modSiteName4);
        siteActions.requestToJoinModSite(driver, modSiteName5);
        siteActions.requestToJoinModSite(driver, modSiteName6);
        siteActions.requestToJoinModSite(driver, modSiteName7);

    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUser() throws Exception
    {
        createEnterpriseUser(userName1);
        createEnterpriseUser(userName2);
        createEnterpriseUser(userName3);
        createEnterpriseUser(userName4);

    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    /**
     * This test is to check User can accept the pending request from
     * Manage pending Request Tab
     */
    @Test(groups = "alfresco-one", priority = 1)
    public void testAcceptPendingRequest() throws Exception
    {
        String taskName = "Request to join " + modSiteName1 + " site";

        // Login as userName1
        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is present in My Task Dashlet
        //TODO: Add Utility to Navigate to My tasks
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page on modSiteName1
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName1).render();

        // Search for user2
        // TODO: Consider adding retrySearch utility if test runs incosistently
        pendingRequestPage = pendingRequestPage.searchRequest(userName2).render();

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Click Approve button
        pendingRequestPage = editTaskPage.selectApproveButton().render();
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        // Open modSiteName1 site dashboard
        siteDashboardPage = siteActions.openSiteDashboard(driver, modSiteName1).render();

        // Verify userName2 is a member of modSiteName1 with Consumer role
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage().render();
        Assert.assertTrue(siteMembersPage.isUserHasRole(userName2, UserRole.CONSUMER));

        // Verify task is removed from My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task is displayed");

    }

    /**
     * This test is to check User can Reject the pending request from
     * Edit Task Page
     */

    @Test(groups = "alfresco-one", priority = 2)
    public void testRejectPendingRequest() throws Exception
    {
        String taskName = "Request to join " + modSiteName2 + " site";

        // Navigate to my tasks page
        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName2).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Click Reject button in Edit Task Page
        pendingRequestPage = editTaskPage.selectRejectButton().render();
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        // Verify taskName is removed from My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Logout as userName1 and login as userName2 (who requested to join)
        logout(driver);
        loginAs(userName2, UNAME_PASSWORD);

        // Navigate to site finder page and verify the button label has 'Request to Join'
        // TODO: Avoid site finder: For indexing issues. Amend to use navigating to site dashboard instead
        siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName2).render();
        Assert.assertTrue(siteFinder.isButtonForSitePresent(modSiteName2, ButtonType.RequestToJoin));

    }

    /**
     * This test is to check User can add comments and save from
     * Edit Task Page
     */

    @Test(groups = "alfresco-one", priority = 3)
    public void testSavePendingRequest() throws Exception
    {
        String taskName = "Request to join " + modSiteName3 + " site";
        String comment = "test" + System.currentTimeMillis();

        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // TODO: Create Util to viewRequest and replace the code
        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName3).render();

        // Search for userName2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from userName2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Enter comment
        editTaskPage.enterComment(comment);

        // Click Approve button
        pendingRequestPage = editTaskPage.selectSaveButton().render();

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Verify added comment is displayed in the comment text area
        assertTrue(editTaskPage.readCommentFromCommentTextArea().contains(comment));

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        logout(driver);

    }

    /**
     * This test is to check User can add comments and cancel from
     * Edit Task Page
     */

    @Test(groups = "alfresco-one", priority = 4)
    public void testCancelEditTaskPendReqByApprover() throws Exception
    {
        String taskName = "Request to join " + modSiteName3 + " site";
        String comment = "test" + System.currentTimeMillis();

        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName3).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Enter comment
        editTaskPage.enterComment(comment);

        // Click Cancel button in Edit task page
        pendingRequestPage = editTaskPage.selectCancelButton().render();

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Verify added comment is not displayed in the comment text area
        assertFalse(editTaskPage.readCommentFromCommentTextArea().contains(comment));

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        logout(driver);

    }

    /**
     * This test is to check User can cancel his own request from
     * Site Finder Page
     */

    @Test(groups = "alfresco-one", priority = 5)
    public void testCancelPendReqByRequestedUser() throws Exception
    {
        String taskName = "Request to join " + modSiteName4 + " site";

        // Navigate to my tasks page
        dashBoard = loginAs(userName1, UNAME_PASSWORD);
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName4).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        logout(driver);
        dashBoard = loginAs(userName2, UNAME_PASSWORD);

        // Navigate to site Finder page, search modSiteName4 and verify button label has 'Cancel Request'
        // TODO: Check button using Site Dashboard
        siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName4).render();
        Assert.assertTrue(siteFinder.isButtonForSitePresent(modSiteName4, ButtonType.CancelRequset));

        // Click on 'Cancel Request' Button
        siteFinder.cancelRequestSite(modSiteName4);

        // Verify button label has 'Request To Join'
        Assert.assertTrue(siteFinder.isButtonForSitePresent(modSiteName4, ButtonType.RequestToJoin));

        logout(driver);
        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is not displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName4);

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify Request users list size is zero
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        logout(driver);

    }

    /**
     * This test is to check when any user claims a task then no other user with Manger role can
     * view the claimed pending request
     */
    @Test(groups = "alfresco-one", priority = 6)
    public void testTwoSiteManagersClaim() throws Exception
    {

        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // logout(driver);
        dashBoard = loginAs(userName4, UNAME_PASSWORD);

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        // TODO: Add util for Approve / Reject / Canceling the request
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();
        editTaskPage = editTaskPage.selectClaim().render();

        // Click Save button
        pendingRequestPage = editTaskPage.selectSaveButton().render();
        assertEquals(pendingRequestPage.getRequests().size(), 1);

        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify Request list size is zero
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        dashBoard = loginAs(userName4, UNAME_PASSWORD);
        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();
        editTaskPage.selectReleaseToPool().render();

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

    }

    /**
     * This test is to check all the users from a group can view the
     * pending request task when the group is added to site with Manager role
     */

    @Test(groups = "alfresco-one", priority = 7)
    public void testGroupSiteManagers() throws Exception
    {

        // Navigate to my tasks page
        dashBoard = loginAs(username, password);

        // Navigate to Groups Page
        // TODO: Replace all 'add user to group' code below by using adminActions.createEnterprise user with group method in prepare
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();

        // Create New group groupName
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();
        page.selectGroup(groupName).render();

        // Add userName3 to groupName
        AddUserGroupPage addUser = page.selectAddUser().render();
        addUser.searchUser(userName3);
        addUser.clickAddUserButton();

        // Add userName4 to groupName
        AddUserGroupPage addUser1 = page.selectAddUser().render();
        addUser1.searchUser(userName4);
        addUser1.clickAddUserButton();

        loginAs(userName1, UNAME_PASSWORD);
        siteDashboardPage = siteActions.openSiteDashboard(driver, modSiteName6).render();

        // Navigate to Add Users page
        addUsersToSitePage = siteDashboardPage.getSiteNav().selectAddUser().render();
        siteGroupsPage = addUsersToSitePage.navigateToSiteGroupsPage().render();

        // Add groupName to modSiteName6 with Manager Role
        addGroupsPage = siteGroupsPage.navigateToAddGroupsPage().render();
        addGroupsPage.addGroup(groupName, UserRole.MANAGER);
        Assert.assertTrue(addGroupsPage.isGroupAdded(groupName));

        loginAs(userName3, UNAME_PASSWORD);

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName6).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // logout(driver);
        dashBoard = loginAs(userName4, UNAME_PASSWORD);

        // Navigate to Pending request page on modSiteName6
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName6).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

    }

    /**
     * This test is to check User can accept the pending request from
     * Edit Task Page
     */
    @Test(groups = "alfresco-one", priority = 8)
    public void testAcceptPendingRequestInEditTaskPage() throws Exception
    {
        String taskName = "Request to join " + modSiteName7 + " site";

        // Navigate to my tasks page
        dashBoard = loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task is displayed");

        // Navigate to Pending request page
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName7).render();

        // Search for user2
        pendingRequestPage.searchRequest(userName2);

        // Verify pending request from user2 is displayed
        Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));

        // click on the view button on user2 request
        editTaskPage = pendingRequestPage.viewRequest(userName2).render();

        // Click Accept button in Edit Task Page
        pendingRequestPage = editTaskPage.selectApproveButton().render();
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        // Open modSiteName1 site dashboard
        siteDashboardPage = siteActions.openSiteDashboard(driver, modSiteName7).render();

        // Verify userName2 is a member of modSiteName1 with Consumer role
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage().render();
        Assert.assertTrue(siteMembersPage.isUserHasRole(userName2, UserRole.CONSUMER));

        // Verify task is removed from My Task Dashlet
        myTasksPage = dashBoard.getNav().selectMyTasks().render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task is displayed");

    }

}