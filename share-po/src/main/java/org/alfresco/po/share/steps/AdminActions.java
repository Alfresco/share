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
package org.alfresco.po.share.steps;

import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.HtmlPage;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import org.alfresco.po.exception.PageOperationException;
@Component
public class AdminActions extends CommonActions
{
    /**
     * Navigate to Groups page
     * @param driver WebDriver Instance
     * @return Groups page
     */

    public GroupsPage navigateToGroup(WebDriver driver)
    {
        DashBoardPage dashBoard = openUserDashboard(driver);
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        return page;
    }
    
    /**
     * Navigate to Groups page
     * @param driver WebDriver Instance
     * @return Groups page
     */

    public UserSearchPage navigateToUserPage(WebDriver driver)
    {
        DashBoardPage dashBoard = openUserDashboard(driver);
        return dashBoard.getNav().getUsersPage().render();
    }
    
    /**
     * Returns GroupsPage
     * @param driver WebDriver Instance
     * @return GroupsPage 
     * @throws UnexpectedSharePageException if not a Groups page instance
     */
    private GroupsPage getGroupsPage(WebDriver driver) throws UnexpectedSharePageException
    {
        try
        {
            GroupsPage page = getSharePage(driver).render();
            return page;
        }
        catch(ClassCastException c)
        {
            throw new UnexpectedSharePageException(GroupsPage.class, c);
        }        
    }

    /**
     * Click on browse button in Groups page. 
     * This method only proceeds when the user is on groups page
     * 
     * @param driver WebDriver Instance        
     * @return Groups page
     */
    public GroupsPage browseGroups(WebDriver driver)
    {
            GroupsPage page = getGroupsPage(driver);
            page = page.clickBrowse().render();
            return page;       
    }
    
    /**
     * Verify user is a member of group
     * 
     * @param driver WebDriver Instance
     * @param fName - User's first name
     * @param uName - check whether this user is in group
     * @param groupName - Check whether user in this specific group Name
     * @return Boolean
     */

    public Boolean isUserGroupMember(WebDriver driver, String fName, String uName, String groupName)
    {
        GroupsPage page = browseGroups(driver);
        GroupsPage groupspage = page.selectGroup(groupName).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (fName.equals(userProfile.getfName()))
            {
                // Verify user is present in the members list
                if (userProfile.getUsername().contains(uName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public HtmlPage createEnterpriseUserWithGroup(WebDriver driver, String userName, String fName, String lName, String userEmail, String password,
            String groupName)
    {
        UserSearchPage userPage = navigateToUserPage(driver);
        NewUserPage newPage = userPage.selectNewUser().render();

        return newPage.createEnterpriseUserWithGroup(userName, fName, lName, userEmail, password, groupName).render();
    }
    
    public HtmlPage createEnterpriseUser(WebDriver driver, String userName, String fName, String lName, String userEmail, String password)
    {
        UserSearchPage userPage = navigateToUserPage(driver);
        NewUserPage newPage = userPage.selectNewUser().render();

        return newPage.createEnterpriseUser(userName, fName, lName, userEmail, password).render();
    }    
    
    /**
     * Open DashBoard > Repository > Data Dictionary
     * 
     * @param driver WebDriver
     * @return RepoPage
     */
    public HtmlPage openRepositoryDataDictionaryPage(WebDriver driver)
    {
        DashBoardPage dashBoard = openUserDashboard(driver);
        RepositoryPage repoPage = dashBoard.getNav().selectRepository().render();

        try
        {
            return repoPage.selectFolder("Data Dictionary").render();
        }
        catch (PageOperationException poe)
        {
            throw new PageOperationException("Data Dictionary Page can not be opened", poe);
        }
    }
    
    /**
     * Open Open DashBoard > Repository > Data Dictionary > Models Page
     * 
     * @param driver WebDriver
     * @return ModelsPage
     */
    public HtmlPage openRepositoryModelsPage(WebDriver driver)
    {        
        RepositoryPage repoPage = openRepositoryDataDictionaryPage(driver).render();
        try
        {            
            return repoPage.selectFolder("Models").render();
        }
        catch (PageOperationException poe)
        {
            throw new PageOperationException("Models Page can not be opened", poe);
        }
    }
    
    /**
     * Open Admin Tools > Sites Manager Page
     * 
     * @param driver WebDriver
     * @return ManageSitesPage
     */
    public HtmlPage openSitesManagerPage(WebDriver driver)
    {        
    	SharePage page = getSharePage(driver).render();
    	
		ManageSitesPage manageSitesPage = page.getNav().selectManageSitesPage().render();
		return manageSitesPage;
    }
}
