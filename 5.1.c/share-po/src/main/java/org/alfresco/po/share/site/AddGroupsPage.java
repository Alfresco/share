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

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

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
    public AddGroupsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @param groupDisplayName String
     * @return AddGroupsPage
     */
    public AddGroupsPage addGroupToSite(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            List<WebElement> searchResultRows = driver.findElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = row.findElement(By.cssSelector(ADD_BUTTON));
                    if (addButton.isEnabled())
                    {
                        addButton.click();
                        return factoryPage.instantiatePage(driver, AddGroupsPage.class);
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
     * @param groupDisplayName String
     * @return AddGroupsPage
     */
    public AddGroupsPage searchGroup(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            WebElement input = findAndWait(By.cssSelector(SEARCH_INPUT_TEXT));
            input.clear();
            input.sendKeys(groupDisplayName);
            findAndWait(By.cssSelector(SEARCH_BUTTON)).click();
            waitForElement(By.cssSelector(SEARCH_RESULT_ROW), 2);
            return factoryPage.instantiatePage(driver, AddGroupsPage.class);
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
     * @param groupDisplayName String
     * @param roleToAssign UserRole
     * @return AddGroupsPage
     */
    public AddGroupsPage assignRoleToGroup(String groupDisplayName, UserRole roleToAssign)
    {
        try
        {
            if (driver.findElement(By.cssSelector(ADDED_GROUP)).isDisplayed())
            {
                driver.findElement(By.cssSelector(ROW_ROLE_BUTTON)).click();

                List<WebElement> listOfRoles = driver.findElements(By.cssSelector("div[id$='inviteelist'] div > ul > li > a"));

                for (WebElement role : listOfRoles)
                {
                    if (roleToAssign.toString().equalsIgnoreCase(role.getText()))
                    {
                        role.click();
                        return factoryPage.instantiatePage(driver, AddGroupsPage.class);
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
     * @param groupDisplayName String
     * @param roleToAssign UserRole
     * @return AddGroupsPage
     */
    public AddGroupsPage addGroup(String groupDisplayName, UserRole roleToAssign)
    {
        searchGroup(groupDisplayName).render();
        addGroupToSite(groupDisplayName).render();
        assignRoleToGroup(groupDisplayName, roleToAssign).render();
        clickAddGroupsButton();
        return factoryPage.instantiatePage(driver, AddGroupsPage.class);
    }

    public void clickAddGroupsButton()
    {
        try
        {

            WebElement addGroupsButton = findAndWait(By.cssSelector(ADD_GROUP));
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
     * @param groupDisplayName String
     * @return boolean
     */
    public boolean isGroupAdded(String groupDisplayName)
    {
        try
        {
            searchGroup(groupDisplayName);
            List<WebElement> searchResultRows = findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = driver.findElement(By.cssSelector(ADD_BUTTON));
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
        findAndWait(removeUsers).click();
    }

    /**
     * @param role UserRole
     */
    public void selectRoleForAll(UserRole role)
    {
        findAndWait(SELECT_ROLE_FOR_ALL).click();
        findAndWait(By.xpath(String.format("//a[contains(text(),'%s')]", role.getRoleName()))).click();
    }

    /**
     * true if 'Add' button displayed and enabled for user.
     *
     * @param groupName String
     * @return boolean
     */
    public boolean isAddButtonEnabledFor(String groupName)
    {
        return isSmThEnabledFor(groupName, ADD_BUTTON_FOR_USER_XPATH);
    }

    /**
     * @return boolean
     */
    public boolean isAddGroupsButtonEnabled()
    {
        try
        {
            WebElement inviteButton = driver.findElement(By.cssSelector(ADD_GROUP));
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
     * @param groupName String
     * @return boolean
     */
    public boolean isSelectRoleEnabledFor(String groupName)
    {
        return isSmThEnabledFor(groupName, SELECT_ROLE_BUTTON_FOR_USER_XPATH);
    }

    /**
     * true if 'Remove Icon' image displayed and enabled for user.
     *
     * @param groupName String
     * @return boolean
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
            WebElement addButton = findAndWait(smthElement, 2000);
            return addButton.isDisplayed() && addButton.isEnabled();
        }
        catch (TimeoutException e)
        {
            throw new PageException("Not found Element:" + smthElement, e);
        }
    }
}
