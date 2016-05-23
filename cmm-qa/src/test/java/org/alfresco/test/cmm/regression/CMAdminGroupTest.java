/*
 * #%L
 * Alfresco CMM Automation QA
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

package org.alfresco.test.cmm.regression;
/**
 * Test Class to test Model Manager Group, User
 * 
 * @author mbhave
 */

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.RemoveUserFromGroupPage;
import org.alfresco.po.share.RemoveUserFromGroupPage.Action;
import org.alfresco.po.share.SharePage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CMAdminGroupTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(CMAdminGroupTest.class);
    
    private String testName;
    
    private String emailContributors = "EMAIL_CONTRIBUTORS";

    public DashBoardPage dashBoardpage;

    public String title = "Remove User from Group";       

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * User logs out after test is executed
     * 
     * @throws Exception
     */
    @AfterMethod(alwaysRun=true)
    public void quit() throws Exception
    {
        logout(driver);
    }

    /**
     * Test:
     * <ul>
     * <li>Get the list of groups displayed in Groups page</li>
     * <li>Verify model_Administrator group is present in the group name list</li>
     * <li>Verify Repo_Admin is present in Model_Admin Group Members list</li>
     * </ul>
     */

    @AlfrescoTest(testlink="tobeadded1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testModelAdminGroupExists1() throws Exception
    {
        String admin = "Administrator";
        String uname = "admin";

        loginAs(driver, new String[] {username});

        // Navigate to Groups page
        GroupsPage page = adminActions.navigateToGroup(driver);

        // Click on browse button in Groups Page
        page = adminActions.browseGroups(driver).render();

        // Verify model_admin group name is present in the list of Groups
        Assert.assertTrue(page.isGroupPresent(modelAdmin), "Model Admin Group is present!!");

        // Verify RepoAdmin is the member of siteAdmin group
        Assert.assertTrue(adminActions.isUserGroupMember(driver, admin, uname, modelAdmin));

    }


    /**
     * Test:
     * <ul>
     * <li>Create new user and add to EMAIL_CONTRIBUTORS group</li>    
     * <li>Verify new user created and added to other group don't have access to Model Manager page</li>     
     * </ul>
     */ 
    @AlfrescoTest(testlink="tobeadded2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testNonGroupUsersCantManageModels2() throws Exception
    {        
        String testname = getUniqueTestName();        
        String userInfo = getUserNameFreeDomain(testname+ System.currentTimeMillis());  
        
        //Login as Admin
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, DEFAULT_PASSWORD, emailContributors );        
        
        //Logout as admin
        logout(driver);
        
        //Login as user1
        SharePage page = loginAs(driver, new String[] {userInfo, DEFAULT_PASSWORD});               
        
        //Verify Model Manager link is not displayed in the home page
        boolean check = page.getNav().hasManageModelsLink();
        Assert.assertFalse(check,"Manage Models link is not displayed in the Header Bar");    
               
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create new user and add to Model_admin group</li>
     * <li>Select the Model_Admin group from the list of Groups in Groups page</li>
     * <li>Remove created and added user from Model_admin group</li>
     * <li>Verify Confirm remove user pop up window is displayed</li>
     * <li>Confirm 'No'to remove user from pop up window </li>
     * <li>Verify Created user is present in the members list in Groups page</li>
     * <li>Verify Model Manager link is still displayed in the home page </li>
     * <li></li>
     * </ul>
     */    
    @AlfrescoTest(testlink="tobeadded3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testNewUserAddedToMMGroup3() throws Exception
    {
        String testname = getUniqueTestName();
        String userInfo = getUserNameFreeDomain(testname+ System.currentTimeMillis());       
        
        //Login as RepoAdmin
        loginAs(driver, new String[] {username});
        
        //Create User and add to Model Admin group
        adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, DEFAULT_PASSWORD, modelAdmin);                 
        
        //Navigate to Groups page
        adminActions.navigateToGroup(driver);
        
        //Click on browse button in Groups Page
        GroupsPage groupsPage = adminActions.browseGroups(driver).render();        
        
        //Select model_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(modelAdmin).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(userInfo).render();
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.No).render();
        
        //Verify created user is present in modelAdmin group
        Assert.assertTrue(adminActions.isUserGroupMember(driver,userInfo,userInfo, modelAdmin));       
                
        //Verify Model Admin user can access Model Manage page from home page      
        //Assert.assertTrue(cmmActions.isManageModelsLinkDisplayed(driver),"Manage Models link is not displayed in the Header Bar");                                 
    }       

    /**
     * Test:
     * <ul>
     * <li>Create new user and add to Model_admin group</li>
     * <li>Select the Model_admin group from the list of Groups in Groups page</li>
     * <li>Remove created and added user from model_admin group</li>
     * <li>Verify Confirm remove user pop up window is displayed</li>
     * <li>Confirm 'Yes'to remove user from pop up window </li>
     * <li>Verify created user is removed from members list in Groups page</li>
     * <li></li>
     * </ul>
     */   
    @AlfrescoTest(testlink="tobeadded4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testUserRemovedFromMMGroup4() throws Exception
    {
        String testname = getUniqueTestName();
        String userInfo = getUserNameFreeDomain(testname+ System.currentTimeMillis());           
        String groupName="ALFRESCO_Model_ADMINISTRATORS";
        
        //Login as RepoAdmin
        loginAs(driver, new String[] {username}); 
        
        //Create User and add to model Admin group
        adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, DEFAULT_PASSWORD, modelAdmin);
        
        //Navigate to Groups page
        adminActions.navigateToGroup(driver);
        
        //Click on browse button in Groups Page
        GroupsPage groupsPage = adminActions.browseGroups(driver).render();        
        
        //Select model_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(groupName).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(userInfo).render();
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.Yes).render();
        
        //Verify created user is not present in modelAdmin group
        Assert.assertFalse(adminActions.isUserGroupMember(driver,userInfo,userInfo, groupName));
        
        logout(driver);
        
        loginAs(driver, new String[] {userInfo, DEFAULT_PASSWORD});
        
        //Verify Model Admin user cannot access Model Manage page from home page
        SharePage page = loginAs(driver, new String[] {userInfo, DEFAULT_PASSWORD});  
        boolean check = page.getNav().hasManageModelsLink();
        Assert.assertFalse(check,"Manage Models link is not displayed in the Header Bar");                                   
    } 
}
