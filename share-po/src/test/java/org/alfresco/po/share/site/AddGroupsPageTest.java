/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
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
    InviteMembersPage membersPage;
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
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();
        Assert.assertTrue(page.getGroupList().contains(groupName), "Group is not created!!");

        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        membersPage = site.getSiteNav().selectInvite().render();
        SiteGroupsPage siteGroups = membersPage.navigateToSiteGroupsPage().render();
        addGroupsPage = siteGroups.navigateToAddGroupsPage().render();       
    }

    @Test(groups="Enterprise-only")
    public void testNavigateToAddGroupsPage()
    {
        addGroupsPage =addGroupsPage.addGroup(groupName, UserRole.CONSUMER).render();
        Assert.assertTrue(addGroupsPage.isGroupAdded(groupName));
    }
}
