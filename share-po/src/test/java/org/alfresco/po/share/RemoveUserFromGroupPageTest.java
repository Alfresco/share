package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.AbstractTest;
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
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
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
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        page = page.clickBrowse().render();
        GroupsPage groupspage = page.selectGroup(groupName).render();
        RemoveUserFromGroupPage removeUserFromGroupPage = groupspage.removeUser(userinfo).render();
        Assert.assertEquals(removeUserFromGroupPage.getTitle(),"Remove User from Group");
    }      


}