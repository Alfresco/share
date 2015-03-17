/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *//*
    * Copyright (C) 2005-2013 Alfresco Software Limited.
    * This file is part of Alfresco
    * Alfresco is free software: you can redistribute it and/or modify
    * it under the terms of the GNU Lesser General Public License as published by
    * the Free Software Foundation, either version 3 of the License, or
    * (at your option) any later version.
    * Alfresco is distributed in the hope that it will be useful,
    * but WITHOUT ANY WARRANTY; without even the implied warranty of
    * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    * GNU Lesser General Public License for more details.
    * You should have received a copy of the GNU Lesser General Public License
    * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
    */
package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author nshah
 *         To Add new Groups this page is used.
 */
public class AddGroupsPage extends SharePage
{

    public static final String SEARCH_INPUT_TEXT = "input[id$='-search-text']";
    public static final String SEARCH_RESULT_ROW = "tr[class^='yui-dt-rec']";
    public static final String SEARCH_BUTTON = "button[id$='group-search-button-button']";
    public static final String GROUP_DISPLAY_NAME = "td[class$='yui-dt-col-description']  div >h3.itemname";
    public static final String ADD_BUTTON = "td[class*='yui-dt-col-actions'] button";
    public static final String ADDED_GROUP = "td[class*='yui-dt-col-item'] div h3.itemname";
    public static final String ROW_ROLE_BUTTON = "td[class*='yui-dt-col-role'] button";
    public static final String ROW_ROLE_OPTION = "div[id$='inviteelist'] div > ul > li > a";
    public static final String ADD_GROUP = "button[id$='add-button-button']";
    private static final By SELECT_ROLE_FOR_ALL = By.cssSelector("button[id$='selectallroles-button-button']");

    private static final String ADD_BUTTON_FOR_USER_XPATH = "//div[contains(text(),'%s')]/../../..//button";
    private static final String SELECT_ROLE_BUTTON_FOR_USER_XPATH = "//span[contains(text(),'%s')]/../../../..//button";
    private static final String REMOVE_USER_ICON_FOR_USER_XPATH = "//h3[contains(text(),'%s')]/../../..//span[@class='removeIcon']/..";

    public AddGroupsPage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AddGroupsPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(ADD_GROUP)), getVisibleRenderElement(By.cssSelector(SEARCH_INPUT_TEXT)));
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
    public AddGroupsPage render(long time)
    {

        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddGroupsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @param groupDisplayName
     * @return
     */
    public AddGroupsPage addGroupToSite(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            List<WebElement> searchResultRows = drone.findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = row.findElement(By.cssSelector(ADD_BUTTON));
                    if (addButton.isEnabled())
                    {
                        addButton.click();
                        return new AddGroupsPage(drone);
                    }
                    else
                    {
                        throw new PageException("Group is already added");
                    }
                }
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_RESULT_ROW, toe);
        }
        throw new PageException("Group does not exist!!");
    }

    /**
     * @param groupDisplayName
     * @return
     */
    public AddGroupsPage searchGroup(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            WebElement input = drone.findAndWait(By.cssSelector(SEARCH_INPUT_TEXT));
            input.clear();
            input.sendKeys(groupDisplayName);
            drone.findAndWait(By.cssSelector(SEARCH_BUTTON)).click();
            drone.waitForElement(By.cssSelector(SEARCH_RESULT_ROW), 2);
            return new AddGroupsPage(drone).render();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_INPUT_TEXT, nse);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_BUTTON, toe);
        }

    }

    /**
     * Assign role to group.
     *
     * @param groupDisplayName
     * @param roleToAssign
     * @return
     */
    public AddGroupsPage assignRoleToGroup(String groupDisplayName, UserRole roleToAssign)
    {
        try
        {
            if (drone.find(By.cssSelector(ADDED_GROUP)).isDisplayed())
            {
                drone.find(By.cssSelector(ROW_ROLE_BUTTON)).click();

                List<WebElement> listOfRoles = drone.findAll(By.cssSelector("div[id$='inviteelist'] div > ul > li > a"));

                for (WebElement role : listOfRoles)
                {
                    if (roleToAssign.toString().equalsIgnoreCase(role.getText()))
                    {
                        role.click();
                        return new AddGroupsPage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Group does not exist", nse);
        }
        throw new PageException("Group does not exist!!");
    }

    /**
     * @param groupDisplayName
     * @param roleToAssign
     * @return
     */
    public AddGroupsPage addGroup(String groupDisplayName, UserRole roleToAssign)
    {
        searchGroup(groupDisplayName).render();
        addGroupToSite(groupDisplayName).render();
        assignRoleToGroup(groupDisplayName, roleToAssign).render();
        clickAddGroupsButton();
        return new AddGroupsPage(drone);
    }

    public void clickAddGroupsButton()
    {
        try
        {

            WebElement addGroupsButton = drone.findAndWait(By.cssSelector(ADD_GROUP));
            if (addGroupsButton.isEnabled())
            {
                addGroupsButton.click();
                waitUntilAlert();
            }
            else
            {
                throw new PageOperationException("Add Group is disabled!!");
            }
        }
        catch (TimeoutException nse)
        {
            throw new PageOperationException("Not found element is : " + ADD_GROUP, nse);
        }
    }

    /**
     * @param groupDisplayName
     * @return
     */
    public boolean isGroupAdded(String groupDisplayName)
    {
        try
        {
            searchGroup(groupDisplayName);
            List<WebElement> searchResultRows = drone.findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = drone.find(By.cssSelector(ADD_BUTTON));
                    if (!addButton.isEnabled())
                    {
                        return true;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Group not found", nse);
        }
        return false;
    }


    public void removeGroupFromAdd(String groupName)
    {
        By removeUsers = By.xpath(String.format(REMOVE_USER_ICON_FOR_USER_XPATH, groupName));
        drone.findAndWait(removeUsers).click();
    }

    /**
     * @param role
     */
    public void selectRoleForAll(UserRole role)
    {
        drone.findAndWait(SELECT_ROLE_FOR_ALL).click();
        drone.findAndWait(By.xpath(String.format("//a[contains(text(),'%s')]", role.getRoleName()))).click();
    }

    /**
     * true if 'Add' button displayed and enabled for user.
     *
     * @param groupName
     * @return
     */
    public boolean isAddButtonEnabledFor(String groupName)
    {
        return isSmThEnabledFor(groupName, ADD_BUTTON_FOR_USER_XPATH);
    }

    /**
     * @return
     */
    public boolean isAddGroupsButtonEnabled()
    {
        try
        {
            WebElement inviteButton = drone.find(By.cssSelector(ADD_GROUP));
            return inviteButton.isDisplayed() && inviteButton.isEnabled();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element:" + ADD_GROUP, e);
        }
    }

    /**
     * true if 'SelectRole' button displayed and enabled for user.
     *
     * @param groupName
     * @return
     */
    public boolean isSelectRoleEnabledFor(String groupName)
    {
        return isSmThEnabledFor(groupName, SELECT_ROLE_BUTTON_FOR_USER_XPATH);
    }

    /**
     * true if 'Remove Icon' image displayed and enabled for user.
     *
     * @param groupName
     * @return
     */
    public boolean isRemoveIconEnabledFor(String groupName)
    {
        return isSmThEnabledFor(groupName, REMOVE_USER_ICON_FOR_USER_XPATH);
    }

    private boolean isSmThEnabledFor(String userName, String smthXpath)
    {
        By smthElement = By.xpath(String.format(smthXpath, userName));
        try
        {
            WebElement addButton = drone.findAndWait(smthElement, 2000);
            return addButton.isDisplayed() && addButton.isEnabled();
        }
        catch (TimeoutException e)
        {
            throw new PageException("Not found Element:" + smthElement, e);
        }
    }
}
