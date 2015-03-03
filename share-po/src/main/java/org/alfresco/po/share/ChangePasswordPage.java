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
package org.alfresco.po.share;

import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Change password page object, holds all element of the html page relating to
 * share's my profile change password page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class ChangePasswordPage extends SharePage
{
    private static final String CHANGE_PASSWORD_FORM_ID = "form.change.password.id";
    private final static By OLD_PASSWORD = By.cssSelector ("input[id$='-oldpassword']");
    private final static By NEW_PASSWORD = By.cssSelector ("input[id$='-newpassword1']");
    private final static By CONFIRM_NEW_PASSWORD = By.cssSelector ("input[id$='-newpassword2']");
    private final static By OK_BUTTON = By.cssSelector ("button[id$='-button-ok-button']");
    private final static By CANCEL_BUTTON = By.cssSelector ("button[id$='-button-cancel-button']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public ChangePasswordPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public boolean formPresent()
    {
        boolean isPrsent = false;
        try
        {
            WebElement form = drone.findAndWaitById(CHANGE_PASSWORD_FORM_ID);
            isPrsent = form.isDisplayed();
        }
        catch (NoSuchElementException ex)
        {

        }
        return isPrsent;
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    public MyProfilePage changePassword (String oldPassword, String newPassword)
    {
        fillField(OLD_PASSWORD, oldPassword);
        fillField(NEW_PASSWORD, newPassword);
        fillField(CONFIRM_NEW_PASSWORD, newPassword);
        click(OK_BUTTON);
        return drone.getCurrentPage().render();
    }
}
