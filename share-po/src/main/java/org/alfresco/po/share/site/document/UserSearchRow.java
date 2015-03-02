/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * This is object to carry Search Row.
 * 
 * @author nshah
 */
public class UserSearchRow implements SearchRow
{
    private String userName;
    private WebDrone drone;
    private WebElement element;
    private By BUTTON_ADD = By.cssSelector("span[class$='button'] span button");
    private By USER_NAME = By.cssSelector("td[class$='fullName'] div h3");

    UserSearchRow(WebDrone drone, WebElement element)
    {
        this.userName = element.findElement(USER_NAME).getText();
        this.drone = drone;
        this.element = element;
    }

    @Override
    public HtmlPage clickAdd()
    {
        try
        {
            element.findElement(BUTTON_ADD).click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("ADD button is not present in the element", nse);
        }

    }

    @Override
    public HtmlPage clickUser()
    {
        try
        {
            if ("EVERYONE".equals(this.userName))
            {
                throw new UnsupportedOperationException("EVERYONE doesnt have profiles");
            }
            else
            {
                element.findElement(USER_NAME).click();

            }
        }
        catch (UnsupportedOperationException use)
        {
            // catch and let go.
        }
        return FactorySharePage.resolvePage(drone);

    }

    @Override
    public String getUserName()
    {
        return this.userName;
    }

}
