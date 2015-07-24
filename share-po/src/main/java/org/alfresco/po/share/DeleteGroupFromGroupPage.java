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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;


@SuppressWarnings("unused")
public class DeleteGroupFromGroupPage extends SharePage
{
    private static final String CONFIRM_MESSAGE = "div[class='yui-module yui-overlay yui-panel' ]>div[class='bd']";
    private static final String DELETE_BUTTON = "button[id*='remove-button']";
    private static final String CANCEL_BUTTON = "div[id*='deletegroupdialog_c'] button[id*='cancel-button']";
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    public enum Action
    {
        Yes, No
    }

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public DeleteGroupFromGroupPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupFromGroupPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupFromGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupFromGroupPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Confirmation (or not) of deleting any group from Group page
     * 
     * @param groupButton Enum Action
     * @return html page
     */
    public HtmlPage clickButton(Action groupButton)
    {
        switch (groupButton)
        {
            case Yes:
                drone.findAndWait(By.cssSelector(DELETE_BUTTON)).click();
                canResume();
                return new GroupsPage(drone);

            case No:
                drone.findAndWait(By.cssSelector(CANCEL_BUTTON)).click();
                canResume();
                return new EditGroupPage(drone);

        }
        throw new PageException("Wrong Page");

    }

    /**
     * Get the Title in Delete group pop up window
     * 
     * @return - String
     */
    public String getTitle()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("div[id*='deletegroupdialog_h']")).getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Delete group window isn't pop up", toe);
        }

    }

}