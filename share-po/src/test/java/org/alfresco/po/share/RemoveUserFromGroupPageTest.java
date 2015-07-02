/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.RemoveUserFromGroupPage.Action;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
/**
 *
 * 
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
public class RemoveUserFromGroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard; 
    private String groupName = "SITE_ADMINISTRATORS";    

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs("admin","admin");     
        
    }
    
    @Test(groups = "Enterprise-only")
    public void testselectAction() throws Exception
    {         
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, groupName);
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        GroupsPage groupspage = page.selectGroup(groupName).render();
        RemoveUserFromGroupPage removeUserFromGroupPage = groupspage.removeUser(userinfo).render();
        removeUserFromGroupPage.selectAction(Action.No).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (userinfo.equals(userProfile.getfName()))
            {
                Assert.assertTrue(userProfile.getUsername().contains(userinfo));
                break;
            }

        }

        RemoveUserFromGroupPage removeuserFromGroupPage = groupspage.removeUser(userinfo).render();
        removeuserFromGroupPage.selectAction(Action.Yes).render();
        List<UserProfile> userprofiles = groupspage.getMembersList();

        for (UserProfile userprofile : userprofiles)
        {
            if (userinfo.equals(userprofile.getfName()))
            {
                Assert.assertFalse(userprofile.getUsername().contains(userinfo));
                break;
            }

        }        
    }
    
    @Test(groups = "Enterprise-only")
    public void testgetTitle() throws Exception
    {         
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, groupName);
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        GroupsPage groupspage = page.selectGroup(groupName).render();
        RemoveUserFromGroupPage removeUserFromGroupPage = groupspage.removeUser(userinfo).render();
        Assert.assertEquals(removeUserFromGroupPage.getTitle(),"Remove User from Group");
    }      


}