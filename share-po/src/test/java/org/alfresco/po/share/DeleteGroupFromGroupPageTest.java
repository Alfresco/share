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
package org.alfresco.po.share;

import java.util.List;

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
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = drone.getCurrentPage().render();
        DeleteGroupFromGroupPage deleteGroup = groupsPage.deleteGroup(groupName).render();
        deleteGroup.clickButton(DeleteGroupFromGroupPage.Action.No);
        groupsPage = drone.getCurrentPage().render();
        groupsPage.clickBrowse();
        List<String> groups = groupsPage.getGroupList();
        Assert.assertTrue(groups.contains(groupName));

        deleteGroup = groupsPage.deleteGroup(groupName).render();
        deleteGroup.clickButton(DeleteGroupFromGroupPage.Action.Yes).render();
        groupsPage = drone.getCurrentPage().render();
        Assert.assertFalse(groupsPage.isGroupPresent(groupName));

    }

    @Test(groups = "Enterprise-only")
    public void testGetTitle() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = drone.getCurrentPage().render();
        DeleteGroupFromGroupPage deleteGroup = groupsPage.deleteGroup(groupName).render();
        Assert.assertEquals(deleteGroup.getTitle(), "Delete Group");

    }

}