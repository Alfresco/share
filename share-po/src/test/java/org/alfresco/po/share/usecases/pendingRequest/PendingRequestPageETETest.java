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

import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.po.share.NewGroupPage.ActionButton;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.steps.AdminActions;
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
    MyTasksPage myTasksPage;
    private static EditTaskPage editTaskPage;    
    private static SiteDashboardPage siteDashboardPage;    
    private static GroupsPage groupsPage;
   
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

    @Autowired SiteActions siteActions;
    @Autowired AdminActions adminActions;
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
        userName3 = "user3" + System.currentTimeMillis() + "@test.com";
        userName4 = "user4" + System.currentTimeMillis() + "@test.com";

        modSiteName1 = "modSN1" + System.currentTimeMillis();
        modSiteName2 = "modSN2" + System.currentTimeMillis();
        modSiteName3 = "modSN3" + System.currentTimeMillis();
        modSiteName4 = "modSN4" + System.currentTimeMillis();
        modSiteName5 = "modSN5" + System.currentTimeMillis();
        modSiteName6 = "modSN6" + System.currentTimeMillis();
        modSiteName7 = "modSN7" + System.currentTimeMillis();

        loginAs(username, password);   
        
        //Create new group
        adminActions.navigateToGroup(driver);
        groupsPage = adminActions.browseGroups(driver);
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();       
                
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
        adminActions.createEnterpriseUserWithGroup(driver, userName3, userName3, userName3, userName3, UNAME_PASSWORD, groupName);
        adminActions.createEnterpriseUserWithGroup(driver, userName4, userName4, userName4, userName4, UNAME_PASSWORD, groupName);      

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
        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is present in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, true);

        // Navigate to Pending request page on modSiteName1
        pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName1).render();

        //Approve request from user2
        pendingRequestPage = pendingRequestPage.approveRequest(userName2).render();
        assertEquals(pendingRequestPage.getRequests().size(), 0);

        //Verify user has consumer role
        siteActions.isUserHasRole(driver,userName2, modSiteName1, UserRole.CONSUMER,true);

        // Verify task is removed from My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, false);
        
        logout(driver);

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
        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, true);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName2).render();
        
        //Verify user2 displayed in the pending list
        siteActions.isUserDisplayedInList(driver,userName2, true);   
            
        //Reject request from user2
        siteActions.rejectRequest(driver,userName2).render();
        siteActions.isUserDisplayedInList(driver,userName2, false);
           
        // Verify taskName is removed from My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, false);
        
        loginAs(userName2, UNAME_PASSWORD);

        // Navigate to site dashboard page and verify the button label has 'Request to Join'       
        siteDashboardPage  = siteActions.openSiteDashboard(driver, modSiteName2).render();
        Assert.assertTrue(siteDashboardPage.isJoinSiteLinkPresent());
        
        logout(driver);
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

        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, true);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName3).render();

        // Verify pending request from user2 is displayed
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        // click on the view button on user2 request
        editTaskPage = siteActions.viewRequest(driver,userName2).render();              

        // Enter comment
        editTaskPage.enterComment(comment);

        // Click Approve button
        pendingRequestPage = editTaskPage.selectSaveButton().render();

        // Verify pending request from user2 is displayed
        siteActions.isUserDisplayedInList(driver,userName2, true);

        // click on the view button on user2 request       
        editTaskPage = siteActions.viewRequest(driver,userName2).render();

        // Verify added comment is displayed in the comment text area
        assertTrue(editTaskPage.readCommentFromCommentTextArea().contains(comment));

        // Verify taskName is displayed in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, true);

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

        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, true);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName3).render();

        //Verify user2 displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, true);

        //View user2 request
        editTaskPage = siteActions.viewRequest(driver,userName2).render();
        
        // Enter comment
        editTaskPage.enterComment(comment);

        // Click Cancel button in Edit task page
        pendingRequestPage = editTaskPage.selectCancelButton().render();

        // Verify pending request from user2 is displayed       
        siteActions.isUserDisplayedInList(driver,userName2, true);

        // click on the view button on user2 request
        editTaskPage = siteActions.viewRequest(driver,userName2).render();

        // Verify added comment is not displayed in the comment text area
        assertFalse(editTaskPage.readCommentFromCommentTextArea().contains(comment));

        // Verify pending request from user2 is displayed       
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        // Verify taskName is displayed in My Task Dashlet       
        siteActions.isMyTaskDisplayed(driver, taskName, true);

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
        
        loginAs(userName1, UNAME_PASSWORD);
        
        //Verify my task displayed in my task dashlet
        siteActions.isMyTaskDisplayed(driver, taskName, true);
        
        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName4).render();

        //Verify user2 displayed in the pending list
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        loginAs(userName2, UNAME_PASSWORD);

        // Navigate to site Dash board page, verify button label has 'Cancel Request'      
        siteDashboardPage  = siteActions.openSiteDashboard(driver, modSiteName4).render();
        Assert.assertTrue(siteDashboardPage.isCancelSiteRequestLinkPresent());        
        
        //Cancel Request to join modSiteName4 from site dash board page
        siteDashboardPage = siteActions.cancelRequestToJoinModSite(driver, modSiteName4).render();
        Assert.assertTrue(siteDashboardPage.isJoinSiteLinkPresent());
        
        //logout(driver);
        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is not displayed in My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, false);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName4).render();

        //Verify user2 not displayed in the pending list
        siteActions.isUserDisplayedInList(driver,userName2, false);

        logout(driver);

    }

   /**
     * This test is to check when any user claims a task then no other user with Manger role can
     * view the claimed pending request
     */
    @Test(groups = "alfresco-one", priority = 6)

    public void testTwoSiteManagersClaim() throws Exception
    {

        loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();
        
        // Verify pending request from user2 is displayed      
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        // logout(driver);
        loginAs(userName4, UNAME_PASSWORD);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();       

        // Verify pending request from user2 is displayed       
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        // click on the view button on user2 request      
        editTaskPage = siteActions.viewRequest(driver,userName2).render();
        editTaskPage = editTaskPage.selectClaim().render();

        // Click Save button
        pendingRequestPage = editTaskPage.selectSaveButton().render();       
        siteActions.isUserDisplayedInList(driver,userName2, true);

        loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        //Verify user2 not displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, false);

        loginAs(userName4, UNAME_PASSWORD);
        
        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();
        
        //Verify user2 not displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, true);

        // click on the view button on user2 request
        editTaskPage = siteActions.viewRequest(driver,userName2).render();
        editTaskPage.selectReleaseToPool().render();

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        //verify user2 displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, true);

        loginAs(userName1, UNAME_PASSWORD);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName5).render();

        //verify user2 displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, true);        
        

    }

    /**
     * This test is to check all the users from a group can view the
     * pending request task when the group is added to site with Manager role
     */

    @Test(groups = "alfresco-one", priority = 7)
    public void testGroupSiteManagers() throws Exception
    {
    	      
        loginAs(userName1, UNAME_PASSWORD);
      
        //Add group to site
        siteActions.addGroupToSite(driver, modSiteName6, groupName, UserRole.MANAGER);
        
        loginAs(userName3, UNAME_PASSWORD);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName6).render();
       
        // Verify pending request from user2 is displayed       
        siteActions.isUserDisplayedInList(driver,userName2, true);

        loginAs(userName4, UNAME_PASSWORD);

        // Navigate to Pending request page on modSiteName6
        siteActions.navigateToPendingRequestPage(driver, modSiteName6).render();
        
        // Verify pending request from user2 is displayed       
        siteActions.isUserDisplayedInList(driver,userName2, true);

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
        loginAs(userName1, UNAME_PASSWORD);

        // Verify taskName is displayed in My Task Dashlet       
        siteActions.isMyTaskDisplayed(driver, taskName, true);

        // Navigate to Pending request page
        siteActions.navigateToPendingRequestPage(driver, modSiteName7).render();

        //Verify user2 displayed in the list
        siteActions.isUserDisplayedInList(driver,userName2, true);
        
        // Click Approve button in Edit Task Page       
        siteActions.approveRequest(driver,userName2).render();      
        siteActions.isUserDisplayedInList(driver,userName2, false);

        //Verify user has consumer role to site
        siteActions.isUserHasRole(driver,userName2, modSiteName7, UserRole.CONSUMER,true);

        // Verify task is removed from My Task Dashlet        
        siteActions.isMyTaskDisplayed(driver, taskName, false);

    }

}