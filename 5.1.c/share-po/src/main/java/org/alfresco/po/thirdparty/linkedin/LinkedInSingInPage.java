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
package org.alfresco.po.thirdparty.linkedin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class LinkedInSingInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='session_key-oauthAuthorizeForm']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='session_password-oauthAuthorizeForm']");
    private final static By ALLOW_ACCESS_BUTTON = By.xpath("//input[@value='Allow access']");

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USERNAME_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(ALLOW_ACCESS_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void login(String userName, String password)
    {

        fillField(USERNAME_INPUT, userName);
        fillField(PASSWORD_INPUT, password);
        click(ALLOW_ACCESS_BUTTON);
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
