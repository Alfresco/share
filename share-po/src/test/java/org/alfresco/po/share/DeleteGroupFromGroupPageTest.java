package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author maryia zaichanka
 */
@Listeners(FailedTestListener.class)
public class DeleteGroupFromGroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String groupName = "Test_Group";

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs("admin", "admin");

    }

    @Test(groups = "Enterprise-only")
    public void testClickButton() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage().render();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = resolvePage(driver).render();
        DeleteGroupFromGroupPage deleteGroup = groupsPage.deleteGroup(groupName).render();
        deleteGroup.clickButton(DeleteGroupFromGroupPage.Action.No);
        groupsPage = resolvePage(driver).render();
        groupsPage.clickBrowse();
        List<String> groups = groupsPage.getGroupList();
        Assert.assertTrue(groups.contains(groupName));

        deleteGroup = groupsPage.deleteGroup(groupName).render();
        deleteGroup.clickButton(DeleteGroupFromGroupPage.Action.Yes).render();
        groupsPage = resolvePage(driver).render();
        Assert.assertFalse(groupsPage.isGroupPresent(groupName));

    }

    @Test(groups = "Enterprise-only")
    public void testGetTitle() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage().render();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = resolvePage(driver).render();
        DeleteGroupFromGroupPage deleteGroup = groupsPage.deleteGroup(groupName).render();
        Assert.assertEquals(deleteGroup.getTitle(), "Delete Group");

    }

}
