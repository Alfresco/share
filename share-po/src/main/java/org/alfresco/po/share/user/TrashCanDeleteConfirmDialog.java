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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * When the users selects Delete of many items in the trashcan they will be
 * presented with confirmation about the list of items Delete. This page is
 * validate the confirmation dialog.
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */

public class TrashCanDeleteConfirmDialog extends TrashCanPage
{
    protected static final By DELETE_OK_BUTTON = By.cssSelector("div.ft button");

    public TrashCanDeleteConfirmDialog(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public TrashCanDeleteConfirmDialog render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(DELETE_OK_BUTTON));
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
    public TrashCanDeleteConfirmDialog render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmDialog render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method helps to click on OK button
     */
    public TrashCanPage clickDeleteOK()
    {
        drone.find(DELETE_OK_BUTTON).click();
        return new TrashCanPage(drone);
    }
}
