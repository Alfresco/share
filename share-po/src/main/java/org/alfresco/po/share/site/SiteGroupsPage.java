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

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 *         To add Groups with a Site.
 */
public class SiteGroupsPage extends SharePage
{
    private static Log logger = LogFactory.getLog(SiteMembersPage.class);
    public static final String ADD_GROUPS = "a[id$='-addGroups-button']";
    private static final By SEARCH_GROUP_ROLE_TEXT = By.cssSelector("input.search-term");
    private static final By SEARCH_GROUP_ROLE_BUTTON = By.cssSelector("button[id$='default-button-button']");
    private static final By LIST_OF_GROUPS = By.cssSelector("tbody.yui-dt-data>tr");
    private static final By GROUP_NAME_FROM_LIST = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private static final String ROLES_DROP_DOWN_VALUES_CSS_PART_1 = "span[id$='";
    private static final String ROLES_DROP_DOWN_VALUES_CSS_PART_2 = "']>div[class*='yui-menu-button-menu']>div>ul>li";
    private static final String ROLES_DROP_DOWN_BUTTON_CSS_PART_1 = "span[id$='";
    private static final String ROLES_DROP_DOWN_BUTTON_CSS_PART_2 = "']>span[class$='menu-button']>span>button";

    @Override
    @SuppressWarnings("unchecked")
    public SiteGroupsPage render(RenderTime timer)
    {

        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(ADD_GROUPS)));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteGroupsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public SiteGroupsPage renderWithGroupSearchResults(final long time)
    {
        return renderWithGroupSearchResults(new RenderTime(time));
    }

    public SiteGroupsPage renderWithGroupSearchResults()
    {
        return renderWithGroupSearchResults(new RenderTime(maxPageLoadingTime));
    }

    public SiteGroupsPage renderWithGroupSearchResults(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LIST_OF_GROUPS));
        return this;
    }

    /**
     * Navigate to Add Groups Page.
     *
     * @return AddGroupsPage
     */
    public AddGroupsPage navigateToAddGroupsPage()
    {
        try
        {
            driver.findElement(By.cssSelector(ADD_GROUPS)).click();
            return factoryPage.instantiatePage(driver, AddGroupsPage.class);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Element:" + ADD_GROUPS + " not found", nse);
        }
    }

    /**
     * @return boolean
     */
    public boolean isSiteGroupsPage()
    {
        try
        {
            if (driver.findElement(By.cssSelector(ADD_GROUPS)).isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Element:" + ADD_GROUPS + " not found", nse);
        }
        throw new PageOperationException("Not a SiteGroups Page");
    }

    /**
     * Method to verify Add Groups button is available
     *
     * @return boolean
     */
    public boolean isAddGroupDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector(ADD_GROUPS)).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * This method search for the given groupName and returns the list of groups.
     *
     * @param groupName String
     * @return List<String>
     */
    public List<String> searchGroup(String groupName)
    {
        if (groupName == null)
        {
            throw new UnsupportedOperationException("GroupName value is required");
        }
        if (logger.isTraceEnabled())
        {
            logger.trace("Groups page: searchGroup :" + groupName);
        }
        try
        {
            WebElement groupRoleSearchTextBox = findAndWait(SEARCH_GROUP_ROLE_TEXT);
            groupRoleSearchTextBox.clear();
            groupRoleSearchTextBox.sendKeys(groupName);

            WebElement searchButton = findAndWait(SEARCH_GROUP_ROLE_BUTTON);
            searchButton.click();
            List<WebElement> list = findAndWaitForElements(LIST_OF_GROUPS, getDefaultWaitTime());
            List<String> groupNamesList = new ArrayList<String>();
            for (WebElement group : list)
            {
                WebElement groupNameElement = group.findElement(GROUP_NAME_FROM_LIST);
                if (groupNameElement != null && groupNameElement.getText() != null)
                {
                    groupNamesList.add(groupNameElement.getText());
                }
            }
            return groupNamesList;
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the group list css.", e);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the group list css.", e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * The filters of the Site content those are diplayed in filters dropdown.
     *
     * @param groupName String
     * @return <List<WebElement>>
     */
    private List<WebElement> getRoles(String groupName)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Members Page: Returning the roles list");
        }
        List<WebElement> listOfRoles = new ArrayList<WebElement>();
        String name = groupName.trim();
        try
        {
            findAndWait(By.cssSelector(ROLES_DROP_DOWN_BUTTON_CSS_PART_1 + name + ROLES_DROP_DOWN_BUTTON_CSS_PART_2)).click();
            listOfRoles = findAndWaitForElements(By.cssSelector(ROLES_DROP_DOWN_VALUES_CSS_PART_1 + name + ROLES_DROP_DOWN_VALUES_CSS_PART_2));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the list of roles.", e);
        }

        return listOfRoles;
    }

    /**
     * This method assigns role from drop down values.
     *
     * @param groupName String
     * @param userRole UserRole
     * @return {@link SiteGroupsPage}
     */
    public SiteGroupsPage assignRole(String groupName, UserRole userRole)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Groups page: Assign role");
        }
        if (groupName == null || groupName.isEmpty())
        {
            throw new UnsupportedOperationException("usreName  is required.");
        }
        if (userRole == null)
        {
            throw new UnsupportedOperationException("userRole is required.");
        }
        for (WebElement role : getRoles(groupName))
        {
            String roleText = role.getText().trim();
            if (roleText != null && userRole.getRoleName().equalsIgnoreCase(roleText))
            {
                role.click();
                return factoryPage.instantiatePage(driver, SiteGroupsPage.class);
            }
        }
        throw new PageException("Unable to find the rolename.");
    }

    /**
     * Method to verify whether assign role drop down is available
     *
     * @param groupName String
     * @return true if displayed
     */
    public boolean isAssignRolePresent(String groupName)
    {
        String name = groupName.trim();
        try
        {
            return isElementDisplayed(By.cssSelector(ROLES_DROP_DOWN_BUTTON_CSS_PART_1 + name + ROLES_DROP_DOWN_BUTTON_CSS_PART_2));
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
        catch (StaleElementReferenceException e)
        {
            return isAssignRolePresent(groupName);
        }
    }

    /**
     * Method to verify whether remove button is present
     *
     * @param groupName String
     * @return true if displayed
     */
    public boolean isRemoveButtonPresent(String groupName)
    {
        String name = groupName.trim();
        try
        {
            return isElementDisplayed(By.cssSelector(String.format("span[id$='button-GROUP_%s']>span>span>button", name)));
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * Method to remove given group from Site.
     *
     * @param groupName String
     */
    public SiteGroupsPage removeGroup(String groupName)
    {
        if (StringUtils.isEmpty(groupName))
        {
            throw new IllegalArgumentException("User Name is required.");
        }
        try
        {
            WebElement element = findAndWait(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
            String id = element.getAttribute("id");
            element.click();
            waitUntilElementDeletedFromDom(By.id(id), maxPageLoadingTime);
            return factoryPage.instantiatePage(driver, SiteGroupsPage.class);
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Group: \"" + groupName + "\" can not be found in groups list.", e);
            }
        }
        throw new PageException("Group: \"" + groupName + "\" can not be found in groups list.");
    }
}
