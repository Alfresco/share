package org.alfresco.po.share.steps;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.steps.AdminActions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminActionsTest extends AbstractTest
{
    private DashBoardPage dashBoard;

    private AdminActions adminActions = new AdminActions();

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs("admin", "admin");
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testUnExpectedSharePageException() throws Exception
    {
        try
        {
            // Without navigating to groups page, this action should return UnexpectedSharePageException
            adminActions.browseGroups(drone);
        }
        catch(UnexpectedSharePageException e)
        {
            Assert.assertTrue(e.getMessage().contains("GroupsPage"));
        }
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testsnavigateToGroup() throws Exception
    {
        GroupsPage groupsPage = adminActions.navigateToGroup(drone);
        groupsPage = adminActions.browseGroups(drone);
        Assert.assertNotNull(groupsPage);
    }

    @Test(groups = "Enterprise-only", priority=3)
    public void testsBrowseGroup() throws Exception
    {
        GroupsPage groupsPage = adminActions.browseGroups(drone);

        Assert.assertTrue(groupsPage.isGroupPresent("ALFRESCO_ADMINISTRATORS"));
        
        Assert.assertTrue(adminActions.isUserGroupMember(drone, "Administrator", "admin", "ALFRESCO_ADMINISTRATORS"));
    }
    
    @Test(groups = "Enterprise-only", priority=4)
    public void testsCreateEntUserWithGroup() throws Exception
    {
        String userInfo = "usertest" + System.currentTimeMillis() + "@test.com";
        UserSearchPage userPage = adminActions.createEnterpriseUserWithGroup(drone, userInfo, userInfo, userInfo, userInfo, userInfo, "ALFRESCO_ADMINISTRATORS").render();
        Assert.assertNotNull(userPage);
    }
        
}
