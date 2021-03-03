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
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.NewGroupPage.ActionButton;
import org.alfresco.po.share.RemoveUserFromGroupPage.Action;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author Charu
 */
@Listeners(FailedTestListener.class)
public class GroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    GroupsPage page;
    private String groupName = "testGrp" + System.currentTimeMillis();
    private String siteAdmin = "SITE_ADMINISTRATORS";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    @BeforeMethod
    public void browseGroups()
    {
        page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
    }

    @Test(groups = "Enterprise-only", enabled = false)
    public void testNewGroup() throws Exception
    {
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();
        Assert.assertTrue(page.getGroupList().contains(groupName), String.format("Group: %s can not be found", groupName));
    }

    @Test(groups = "Enterprise-only")
    public void testSelectGroup() throws Exception
    {
        Assert.assertTrue(page.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");
        page = page.selectGroup(siteAdmin).render();
        Assert.assertTrue(page.hasMembers(), "Group members are  present");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testSelectGroup")
    public void testisGroupPresent() throws Exception
    {
        Assert.assertTrue(page.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testisGroupPresent")
    public void testgetMembersList() throws Exception
    {
        // String Admin = "Administrator";

        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, siteAdmin);
        page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
        page = page.selectGroup(siteAdmin).render();
        Assert.assertTrue(page.hasMembers(), "Group members are present");
        List<UserProfile> userProfiles = page.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (userinfo.equals(userProfile.getfName()))
            {
                Assert.assertTrue(userProfile.getlName().contains(userinfo));
                Assert.assertTrue(userProfile.getUsername().contains(userinfo));
                break;
            }

        }

    }

    /*
     * @Test(groups = "Enterprise-only", dependsOnMethods = "testgetMembersList")
     * public void testclickAddUser() throws Exception
     * {
     * GroupsPage page = dashBoard.getNav().getGroupsPage();
     * page = page.navigateToAddAndEditGroups().render();
     * page.selectGroup(siteAdmin).render();
     * AddUserPage addUserPage = page.clickAddUser().render();
     * Assert.assertTrue(addUserPage.getTitle().contains("Add User"),"Add User page is displayed");
     * }
     * @Test(groups = "Enterprise-only", dependsOnMethods = "testclickAddUser")
     * public void testclickAdGroup() throws Exception
     * {
     * GroupsPage page = dashBoard.getNav().getGroupsPage();
     * page = page.navigateToAddAndEditGroups().render();
     * page.selectGroup(siteAdmin).render();
     * AddGroupPage addGroupPage = page.clickAddGroup().render();
     * Assert.assertTrue(addGroupPage.getTitle().contains("Add Group"),"Add User page is displayed");
     * }
     */

    @Test(groups = "Enterprise-only", dependsOnMethods = "testgetMembersList")
    public void testRemoveUser() throws Exception
    {
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, siteAdmin);
        page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
        page = page.selectGroup(siteAdmin).render();
        RemoveUserFromGroupPage removeUserFromGroupPage = page.removeUser(userinfo).render();
        removeUserFromGroupPage.selectAction(Action.Yes).render();
        List<UserProfile> userProfiles = page.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (userinfo.equals(userProfile.getfName()))
            {
                Assert.assertFalse(userProfile.getUsername().contains(userinfo));
                break;
            }

        }

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testRemoveUser")
    public void testSelectAddUser() throws Exception
    {
        page.selectGroup(siteAdmin).render();
        AddUserGroupPage addUserPage = page.selectAddUser().render();
        Assert.assertTrue(addUserPage.getDialogueTitle().equals("Add User"), "Add User page isn't displayed");
        addUserPage.clickClose();

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testSelectAddUser")
    public void testSelectAddGroup() throws Exception
    {
        EditGroupPage editGroupPage = page.selectEditGroup(siteAdmin);
        editGroupPage.isDisplayNameInputPresent();
        editGroupPage.isSaveButtonEnabled();
        editGroupPage.clickButton(EditGroupPage.ActionButton.CANCEL);

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testSelectAddGroup")
    public void testDeleteGroup() throws Exception
    {
        DeleteGroupFromGroupPage deleteGroup = page.deleteGroup(siteAdmin);
        Assert.assertTrue(deleteGroup.getTitle().equals("Delete Group"));
    }
}
