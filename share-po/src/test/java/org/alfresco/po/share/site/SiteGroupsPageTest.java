package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.test.FailedTestListener;
import org.alfresco.po.exception.PageRenderTimeException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author nshah
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class SiteGroupsPageTest extends AbstractTest
{
    private String groupName = "testGrp" + System.currentTimeMillis();
    //InviteMembersPage membersPage;
    AddUsersToSitePage membersPage;
    SiteGroupsPage siteGroupsPage;
    AddGroupsPage addGroupsPage;
    String user;
    WebElement invitee;
    DashBoardPage dashBoard;
    String siteName;
    List<WebElement> inviteesList;
    String userNameTest;

    public static long refreshDuration = 15000;

    @BeforeClass
    public void instantiateMembers() throws Exception
    {
        userNameTest = "user" + System.currentTimeMillis() + "@test.com";
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);

        //create a group
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();

        //navigate to site groups page
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        //membersPage = site.getSiteNav().selectInvite().render();
        membersPage = site.getSiteNav().selectAddUser().render();
        siteGroupsPage = membersPage.navigateToSiteGroupsPage().render();

        //add a group to site
        addGroupsPage = siteGroupsPage.navigateToAddGroupsPage();
        addGroupsPage.addGroup(groupName, UserRole.COLLABORATOR);
    }

    @Test
    public void testNavigateToAddGroupsPage()
    {
        membersPage.navigateToSiteGroupsPage();
        Assert.assertTrue(siteGroupsPage.isSiteGroupsPage());

    }

    @Test(dependsOnMethods = "testNavigateToAddGroupsPage")
    public void testSearchGroup() throws Exception
    {
        List<String> searchGroups = null;
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            try
            {
                searchGroups = siteGroupsPage.searchGroup(groupName);
                siteGroupsPage.renderWithGroupSearchResults(refreshDuration);
            }
            catch (PageRenderTimeException exception)
            {
            }
            if (searchGroups != null && searchGroups.size() > 0)
            {
                break;
            }
        }
        Assert.assertTrue(searchGroups.size() > 0);
    }

    @Test(dependsOnMethods = "testSearchGroup")
    public void testAssignRole()
    {
        siteGroupsPage.assignRole(groupName, UserRole.MANAGER);
    }
}
