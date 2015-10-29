/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.thirdparty.wordpress;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressSignInPage extends Page
{
    private static final By USER_LOGIN = By.cssSelector("#user_login");
    private static final By USER_PASS = By.cssSelector("#user_pass");
    private static final By SUBMIT_BTN = By.cssSelector("#wp-submit");

    @SuppressWarnings("unchecked")
    @Override
    public WordPressSignInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USER_LOGIN),
            getVisibleRenderElement(USER_PASS),
            getVisibleRenderElement(SUBMIT_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressSignInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public WordPressUserPage login(String userName, String password)
    {

        fillField(USER_LOGIN, userName);
        fillField(USER_PASS, password);
        click(SUBMIT_BTN);
        waitUntilElementPresent(By.cssSelector(".reblog"), 5);
        return  factoryPage.instantiatePage(driver, WordPressUserPage.class).render();
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }
}
