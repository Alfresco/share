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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author
 */

@SuppressWarnings("unchecked")
public class AddUserGroupPage extends ShareDialogue
{
    private static final String SEARCH_BUTTON = "button[id$='default-search-peoplefinder-search-button-button']";
    @RenderWebElement @FindBy(css="input[id$='peoplefinder-search-text']") TextInput search;
    private static final String SEARCH_RESULT_ROW = "tr[class^='yui-dt-rec']";
    @FindBy(css="td[class*='yui-dt-col-actions'] button") Button add;
    private static final String CLOSE_ICON = "div[class*='people-picker'] a";

    public AddUserGroupPage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        basicRender(timer);
        webElementRender(timer);
        return this;
    }
    /**
     * Checks if search button is present and enabled
     * 
     * @return boolean
     */
    public boolean isSearchButtonEnabled()
    {
        try
        {
            WebElement searchButton = driver.findElement(By.cssSelector(SEARCH_BUTTON));
            return searchButton.isDisplayed() && searchButton.isEnabled();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element:" + SEARCH_BUTTON, e);
        }
    }

    /**
     * Checks if search field is present and enabled
     * 
     * @return boolean
     */
    public boolean isSearchFieldEnabled()
    {
        try
        {
            return search.isDisplayed() && search.isEnabled();
        }
        catch (Exception e)
        {
            throw new PageException("Search input not found Element:", e);
        }
    }

    /**
     * Click on Add button at Add User window
     */
    public void clickAddUserButton()
    {
        try
        {

            
            if (add.isEnabled())
            {
                add.click();
                waitUntilAlert();
            }
            else
            {
                throw new PageOperationException("Add User button is disabled");
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to click on add button: ", e);
        }
    }
    /**
     * Serch for user to add to a group
     * 
     * @param userName String
     * @return AddUserGroupPage
     */
    public HtmlPage searchUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            search.clear();
            search.sendKeys(userName);
            WebElement searchButton = findAndWait(By.cssSelector(SEARCH_BUTTON));
            searchButton.click();
            findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW), getDefaultWaitTime());
            return this;

        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element search input:", nse);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_BUTTON, toe);
        }

    }

    public HtmlPage clickClose()
    {
        try
        {
            WebElement addUserButton = findAndWait(By.cssSelector(CLOSE_ICON));
            addUserButton.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not found element is : " + CLOSE_ICON, e);
        }

        return getCurrentPage();
    }
    /**
     * Checkes if add button on page is visible.
     * @return true if visible
     */
    public boolean isAddButtonDisplayed()
    {
        return add.isDisplayed();
    }
}
