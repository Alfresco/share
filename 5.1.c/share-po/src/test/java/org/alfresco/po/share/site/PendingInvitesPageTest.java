package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;

import org.alfresco.test.FailedTestListener;
import org.alfresco.po.exception.PageRenderTimeException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
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
        
        System.out.println("SITE ******** " + siteName);
        System.out.println("USER ******** " + userNameTest);
        
        
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
        pendingInvitesPage.cancelInvitation(userName);
        assertNotNull(pendingInvitesPage);
        verifyInviteCancelled(userNameTest);
        assertTrue(verifyInviteCancelled(userNameTest), "The invite wasn't cancelled!");
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
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
