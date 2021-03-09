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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.UserRole;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to hold tests for Pending Invites page object
 * Pending Invites will be created for external users only
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class PendingInvitesPageTest extends AbstractTest
{

    private static Log logger = LogFactory.getLog(PendingInvitesPageTest.class);

    //InviteMembersPage membersPage;
    AddUsersToSitePage membersPage;
    SiteMembersPage siteMembersPage;
    PendingInvitesPage pendingInvitesPage;
    DashBoardPage dashBoard;
    String siteName;
    List<WebElement> inviteeList;
    String userName;
    String userNameTest;
    public static long refreshDuration = 15000;

    @BeforeClass
    public void instantiatePendingInvite() throws Exception
    {
        userName = "user" + System.currentTimeMillis();
        userNameTest = userName + "@test.com";
        siteName = "PendingInvitesTest" + System.currentTimeMillis();
        
        logger.info("SITE ******** " + siteName);
        logger.info("USER ******** " + userNameTest);
        
        
        dashBoard = loginAs(username, password);

        // Creating new user.
        /**
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
        **/
        // Creating a site.
        //TODO: Replace with SiteUtil code to create Site
        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();

        // Invite a user
        //List<String> searchUsers = null;
        //membersPage = site.getSiteNav().selectInvite().render();
 
        membersPage = site.getSiteNav().selectAddUser().render();
        membersPage.enterExternalUserFirstName(userNameTest);
        membersPage.enterExternalUserLastName(userNameTest);
        membersPage.enterExternalUserEmail(userNameTest);
        membersPage.selectExternalUser();
        membersPage.setAllRolesTo(UserRole.CONSUMER);
        membersPage.clickAddUsersButton();
        
        // TODO: Cleanup commented out code        
        /**
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            //searchUsers = membersPage.searchUser(userNameTest);
            searchUsers = membersPage.searchUser("user");
            try
            {
                if (searchUsers != null && searchUsers.size() > 0 && searchUsers.get(0).toString().contains(userNameTest))
                    
                {
                    membersPage.selectRole(searchUsers.get(0), UserRole.COLLABORATOR).render();
                    membersPage.clickInviteButton().render();
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
                membersPage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
        }
        **/
        siteMembersPage = site.getSiteNav().selectMembersPage().render();
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    // TODO: Remove dependsOn, add priority
    @Test
    public void navigateToPendingInvitesPage()
    {
        pendingInvitesPage = siteMembersPage.navigateToPendingInvites().render();
         assertNotNull(pendingInvitesPage);
    }

    @Test(dependsOnMethods = "navigateToPendingInvitesPage")
    public void checkSearch()
    {
        pendingInvitesPage.search(userNameTest);
        assertEquals(pendingInvitesPage.getInvitees().size(), 1);
    }

    @Test(dependsOnMethods = "checkSearch")
    public void cancelInvite()
    {
    	//TODO: Amend cancelInvitation to return html page 
        pendingInvitesPage.cancelInvitation(userName);
        assertNotNull(pendingInvitesPage);
        verifyInviteCancelled(userNameTest);
        assertTrue(verifyInviteCancelled(userNameTest), "The invite wasn't cancelled!");
    }

// TODO: Remove test class specific methods, add a method on the pendingInvitesPage instead to check if there's active invite for the specified user    
    private boolean verifyInviteCancelled(String userName)
    {
        boolean cancelled = false;
        inviteeList = pendingInvitesPage.getInvitees();
        if (inviteeList.size() == 0)
        {
            cancelled = true;
        }
        for (WebElement invitee : inviteeList)
        {
            if (invitee.getText().contains(userName))
            {
                cancelled = false;
            }
        }
        return cancelled;
    }
}
