package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.po.share.NewGroupPage.ActionButton;
import org.alfresco.po.share.enums.UserRole;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author nshah
 *
 */
public class AddGroupsPageTest extends AbstractTest
{
    //InviteMembersPage membersPage;
    AddUsersToSitePage membersPage;
    AddGroupsPage addGroupsPage;
    String user;
    WebElement invitee;
    DashBoardPage dashBoard;
    String siteName;
    List<WebElement> inviteesList;
    String userNameTest;
    String groupName ="TEST_GROUP"+System.currentTimeMillis();

    @BeforeClass(groups="Enterprise-only")
    public void instantiateMembers() throws Exception
    {
        userNameTest = "user" + System.currentTimeMillis() + "@test.com";
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();

        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        //membersPage = site.getSiteNav().selectInvite().render();
        membersPage = site.getSiteNav().selectAddUser().render();
        SiteGroupsPage siteGroups = membersPage.navigateToSiteGroupsPage().render();
        addGroupsPage = siteGroups.navigateToAddGroupsPage().render();
    }

    @Test(groups="Enterprise-only")
    public void testNavigateToAddGroupsPage()
    {
        addGroupsPage = addGroupsPage.addGroup(groupName, UserRole.CONSUMER).render();
        Assert.assertTrue(addGroupsPage.isGroupAdded(groupName));
    }
}
