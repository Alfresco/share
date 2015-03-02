/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author
 */

@SuppressWarnings("unused")
public class AddUserGroupPage extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    private static final String SEARCH_BUTTON = "button[id*='search-peoplefinder']";
    private static final String SEARCH_INPUT = "input[id*='search-peoplefinder-search-text']";
    private static final String SEARCH_RESULT_ROW = "tr[class^='yui-dt-rec']";
    private static final String ADD_BUTTON = "td[class*='yui-dt-col-actions'] button";
    private static final String CLOSE_ICON = "div[class*='people-picker'] a";

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public AddUserGroupPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddUserGroupPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddUserGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddUserGroupPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Checks if search button is present and enabled
     * 
     * @return
     */
    public boolean isSearchButtonEnabled()
    {
        try
        {
            WebElement searchButton = drone.find(By.cssSelector(SEARCH_BUTTON));
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
     * @return
     */
    public boolean isSearchFieldEnabled()
    {
        try
        {
            WebElement searchField = drone.find(By.cssSelector(SEARCH_INPUT));
            return searchField.isDisplayed() && searchField.isEnabled();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element:" + SEARCH_INPUT, e);
        }
    }

    /**
     * Click on Add button at Add User window
     */
    public void clickAddUserButton()
    {
        try
        {

            WebElement addUserButton = drone.findAndWait(By.cssSelector(ADD_BUTTON));
            if (addUserButton.isEnabled())
            {
                addUserButton.click();
                waitUntilAlert();
            }
            else
            {
                throw new PageOperationException("Add User button is disabled");
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not found element is : " + ADD_BUTTON, e);
        }
    }

    /**
     * Serch for user to add to a group
     * 
     * @param userName
     * @return
     */
    public AddUserGroupPage searchUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            WebElement input = drone.findAndWait(By.cssSelector(SEARCH_INPUT));
            input.clear();
            input.sendKeys(userName);
            WebElement searchButton = drone.findAndWait(By.cssSelector(SEARCH_BUTTON));
            searchButton.click();
            drone.findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            return new AddUserGroupPage(drone).render();

        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_INPUT, nse);
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
            WebElement addUserButton = drone.findAndWait(By.cssSelector(CLOSE_ICON));
            addUserButton.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not found element is : " + ADD_BUTTON, e);
        }

        return FactorySharePage.resolvePage(drone);

    }
}