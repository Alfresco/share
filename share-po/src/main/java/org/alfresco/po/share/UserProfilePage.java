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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
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


    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        basicRender(timer);
        return this;
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
            return findAndWaitForElements(buttonGroup);
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
            findAndWait(deleteUser).click();

            List<WebElement> buttons = getButtons();

            for (WebElement button : buttons)
            {
                if (button.getText().equalsIgnoreCase("Delete"))
                {
                    button.click();
                    return getCurrentPage().render();
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
    public HtmlPage selectEditUser()
    {
        try
        {
            driver.findElement(editUser).click();
            return factoryPage.instantiatePage(driver, EditUserPage.class);
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
            findAndWait(goBackButton).click();
            return getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find the GoBack Button.");
    }

    @Override
    public <T extends HtmlPage> T render(RenderTime timer)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
