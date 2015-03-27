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
 */
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * User Profile page object
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class UserProfilePage extends SharePage
{
    @RenderWebElement
    private final By goBackButton = By.cssSelector("button[id$='default-goback-button-button']");
    @RenderWebElement
    private final By editUser = By.cssSelector("button[id$='default-edituser-button-button']");
    @RenderWebElement
    private final By deleteUser = By.cssSelector("button[id$='default-deleteuser-button-button']");
    private final By buttonGroup = By.cssSelector("span.button-group>span>span>button");

    /**
     * Constructor.
     * 
     * @param drone
     */
    public UserProfilePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render(RenderTime timer)
    {
        try
        {
            webElementRender(timer);
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(100L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    if (!isJSMessageDisplayed())
                    {
                        break;
                    }
                }
                catch (NoSuchElementException nse)
                {
                }
                timer.end();
            }
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
    public UserProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Gets the buttons present on Delete confirmation window.
     * 
     * @return List<WebElement>
     */
    private List<WebElement> getButtons()
    {
        try
        {
            return drone.findAndWaitForElements(buttonGroup);
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find the Button group.");
    }

    /**
     * This method deletes the User through clicking on deleteUser button.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage deleteUser()
    {
        try
        {
            drone.findAndWait(deleteUser).click();

            List<WebElement> buttons = getButtons();

            for (WebElement button : buttons)
            {
                if (button.getText().equalsIgnoreCase("Delete"))
                {
                    button.click();
                    return new UserSearchPage(drone);
                }
            }
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find the Delete User Button.");
    }

    /**
     * This method clicks on Edit User.
     * 
     * @return NewUserPage
     */
    public EditUserPage selectEditUser()
    {
        try
        {
            drone.findAndWait(editUser).click();
            return new EditUserPage(drone);
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find the Edit User Button.");
    }

    /**
     * This method clicks on GoBack.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage selectGoBack()
    {
        try
        {
            drone.findAndWait(goBackButton).click();
            return new UserSearchPage(drone);
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find the GoBack Button.");
    }
}