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
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageRenderTimeException;
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
    InviteMembersPage membersPage;
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
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();

        //navigate to site groups page
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();
        membersPage = site.getSiteNav().selectInvite().render();
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