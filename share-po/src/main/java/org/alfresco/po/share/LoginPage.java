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
import org.alfresco.po.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Login Page object that holds all information and methods that can be found on
 * the login page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class LoginPage extends SharePage
{
    @RenderWebElement 
        @FindBy(css="[id$='default-username']") 
            private TextInput usernameInput;
    @RenderWebElement
        @FindBy(css="[id$='default-password']") 
            private TextInput passwordInput;
    @RenderWebElement
        @FindBy(css="[id$='default-submit-button']") 
            private Button submit;


    /**
     * Login action, completes the login form by submitting user name and password.
     * @param username String user identifier
     * @param password String user password
     * @return Alfresco share page, Dashboard page.
     * @throws Exception if error
     */
    public HtmlPage loginAs(String username, String password) throws Exception
    {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        submit.click();
        return getCurrentPage();
    }

    /**
     * Verify if error message is displayed.
     *
     * @return true if div.bd is displayed
     */
    public boolean hasErrorMessage()
    {
        return isDisplayed(By.cssSelector("div.error"));
    }

    public String getErrorMessage()
    {
        return driver.findElement(By.cssSelector("div.error")).getText();
    }
}
