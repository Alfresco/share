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
package org.alfresco.po.thirdparty.facebook;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class FacebookSingInPage extends Page
{
    private static final By EMAIL_INPUT = By.xpath("//input[@id='email']");
    private static final By PASSWORD_INPUT = By.xpath("//input[@type='password']");
    private static final By LOGIN_BUTTON = By.xpath("//input[@type='submit']");


    @SuppressWarnings("unchecked")
    @Override
    public FacebookSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(EMAIL_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(LOGIN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FacebookSingInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void login(String username, String password)
    {
        fillField(EMAIL_INPUT, username);
        fillField(PASSWORD_INPUT, password);
        WebElement loginButton = findAndWait(LOGIN_BUTTON);
        loginButton.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }

}
