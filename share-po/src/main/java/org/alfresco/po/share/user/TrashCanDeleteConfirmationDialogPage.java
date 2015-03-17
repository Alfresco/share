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
package org.alfresco.po.share.user;

import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * When the users Deletes an item in the trashcan they will be presented with confirmation Dialog.
 * This page is validate the confirmation dialog.
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */

public class TrashCanDeleteConfirmationDialogPage extends TrashCanPage
{
    protected static final By DELETE_CONFIRMATION_PROMPT = By.cssSelector("div[id='prompt']");
    protected static final By CONFIRMATION_BUTTON = By.cssSelector("div.ft>span button");

    public TrashCanDeleteConfirmationDialogPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Basic Render method
     */
    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmationDialogPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmationDialogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmationDialogPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Is confirmation Dialog displayed
     * 
     * @return - Boolean
     */
    public boolean isConfirmationDialogDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement prompt = drone.findAndWait(DELETE_CONFIRMATION_PROMPT);
            displayed = prompt.isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public TrashCanPage clickCancelButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = drone.findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("Cancel"))
                {
                    buttonElement.click();
                    return new TrashCanPage(drone);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cancel button is not visible", te);
        }
        return new TrashCanPage(drone);
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public TrashCanPage clickOkButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = drone.findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("OK"))
                {
                    buttonElement.click();
                    return new TrashCanPage(drone);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Ok button is not visible", te);
        }
        return new TrashCanPage(drone);
    }
}