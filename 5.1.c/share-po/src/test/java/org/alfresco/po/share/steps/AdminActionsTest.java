package org.alfresco.po.share.steps;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminActionsTest extends AbstractTest
{

    @Autowired AdminActions adminActions;

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs("admin", "admin");
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testUnExpectedSharePageException() throws Exception
    {
        try
        {
            // Without navigating to groups page, this action should return UnexpectedSharePageException
            adminActions.browseGroups(driver);
        }
        catch(UnexpectedSharePageException e)
        {
            Assert.assertTrue(e.getMessage().contains("GroupsPage"));
        }
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testsnavigateToGroup() throws Exception
    {
        GroupsPage groupsPage = adminActions.navigateToGroup(driver);
        groupsPage = adminActions.browseGroups(driver);
        Assert.assertNotNull(groupsPage);
    }

    @Test(groups = "Enterprise-only", priority=3)
    public void testsBrowseGroup() throws Exception
    {
        GroupsPage groupsPage = adminActions.browseGroups(driver);

        Assert.assertTrue(groupsPage.isGroupPresent("ALFRESCO_ADMINISTRATORS"));
        
        Assert.assertTrue(adminActions.isUserGroupMember(driver, "Administrator", "admin", "ALFRESCO_ADMINISTRATORS"));
    }
    
    @Test(groups = "Enterprise-only", priority=4)
    public void testsCreateEntUserWithGroup() throws Exception
    {
        String userInfo = "usertest" + System.currentTimeMillis() + "@test.com";
        UserSearchPage userPage = adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, userInfo, "ALFRESCO_ADMINISTRATORS").render();
        Assert.assertNotNull(userPage);
    }
        
}
