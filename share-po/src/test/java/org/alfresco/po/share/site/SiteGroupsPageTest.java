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
        siteUtil.createSite(driver, username, password, siteName, "description", "public");

        SiteDashboardPage site = siteActions.openSiteDashboard(driver, siteName);
        
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
