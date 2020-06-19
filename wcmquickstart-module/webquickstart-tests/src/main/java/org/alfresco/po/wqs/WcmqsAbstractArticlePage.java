/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.po.wqs;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public abstract class WcmqsAbstractArticlePage extends WcmqsAbstractPage
{
    protected final By EDIT_LINK = By.cssSelector("a.alfresco-content-edit");
    protected final By CREATE_LINK = By.cssSelector("a.alfresco-content-new");
    protected final By DELETE_LINK = By.cssSelector("a.alfresco-content-delete");

    public WcmqsAbstractArticlePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractArticlePage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(EDIT_LINK), getVisibleRenderElement(CREATE_LINK), getVisibleRenderElement(DELETE_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractArticlePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractArticlePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public WcmqsEditPage clickEditButton()
    {
        try
        {
            drone.findAndWait(EDIT_LINK).click();
            return new WcmqsEditPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find edit button. ", e);
        }
    }

    public WcmqsEditPage clickCreateButton()
    {
        try
        {
            drone.findAndWait(CREATE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find edit button. ", e);
        }
        return new WcmqsEditPage(drone);
    }

    public void clickDeleteButton()
    {
        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find edit button. ", e);
        }
    }

    public boolean isEditButtonDisplayed()
    {
        try
        {
            return drone.find(EDIT_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isCreateButtonDisplayed()
    {
        try
        {
            return drone.find(CREATE_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isDeleteButtonDisplayed()
    {
        try
        {
            return drone.find(DELETE_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }
}
