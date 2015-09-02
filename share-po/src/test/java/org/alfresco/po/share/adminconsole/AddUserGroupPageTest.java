/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.adminconsole;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.AddUserGroupPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author maryia zaichanka
 */
@Listeners(FailedTestListener.class)
public class AddUserGroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String groupName = "Add_Group";
    private String user = "user" + System.currentTimeMillis();

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
    	userService.create("admin", "admin", user, user, user+"@test.com", user, user);
    	dashBoard = loginAs("admin", "admin");
    	dashBoard.getNav().getUsersPage().render();

    }

    @Test(groups = "Enterprise-only")
    public void testsSearchUser() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage().render();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = resolvePage(driver).render();
        groupsPage.selectGroup(groupName);
        AddUserGroupPage addUser = groupsPage.selectAddUser().render();
        addUser.searchUser(user).render();
        Assert.assertTrue(addUser.isAddButtonDisplayed());
        addUser.clickClose();

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testsSearchUser")
    public void testClickAddUserButton() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage().render();
        groupsPage.clickBrowse();
        resolvePage(driver).render();
        groupsPage.selectGroup(groupName);
        AddUserGroupPage addUser = groupsPage.selectAddUser().render();
        addUser.searchUser(user);
        addUser.clickAddUserButton();
        groupsPage = resolvePage(driver).render();
        groupsPage.clickBrowse();
        groupsPage.selectGroup(groupName).render();
        resolvePage(driver).render();
        List<String> users = groupsPage.getUserList();
        Assert.assertTrue(users.contains(user + " " + user + " (" + user + ")"), "Added user isn't displayed in a group");

    }

}
