/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class ConfirmDeletePage extends SharePage
{
    public enum Action
    {
        Delete, Cancel
    }

    protected ConfirmDeletePage(WebDrone drone)
    {
        super(drone);

    }

    private final Log logger = LogFactory.getLog(ConfirmDeletePage.class);

    private static final By BUTTON_GROUP = By.cssSelector(".button-group");
    private static final By PROMPT = By.cssSelector("div[id$='prompt']");

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeletePage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(BUTTON_GROUP), getVisibleRenderElement(PROMPT));
        }
        catch (NoSuchElementException e)
        {
            logger.error(BUTTON_GROUP + "or" + PROMPT + " not found!", e);

        }
        catch (TimeoutException e)
        {
            logger.error(BUTTON_GROUP + "or" + PROMPT + " not found!", e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeletePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeletePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Select Action "Delete" or "Cancel" to perform.
     * 
     * @param action
     * @return - HtmlPage
     */
    public HtmlPage selectAction(Action action)
    {

        try
        {
            By buttonSelector = By.cssSelector(".button-group span span button");
            List<WebElement> buttons = drone.findAll(buttonSelector);
            long elementWaitTime = SECONDS.convert(maxPageLoadingTime, MILLISECONDS);
            for (WebElement button : buttons)
            {
                if (action.name().equals(button.getText()))
                {
                    button.click();
                    drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div#prompt>div.hd"), "Delete", elementWaitTime);
                    if (Action.Delete.equals(action))
                    {
                        By deleteMessageSelector = By.cssSelector("div.bd>span.message");
                        String deleteMessage = "deleted";
                        drone.waitUntilVisible(deleteMessageSelector, deleteMessage, elementWaitTime);
                        drone.waitUntilNotVisibleWithParitalText(deleteMessageSelector, deleteMessage, elementWaitTime);
                    }
                    return drone.getCurrentPage();
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error(BUTTON_GROUP + "not present in this page", nse);

        }
        throw new PageOperationException(BUTTON_GROUP + "not present in this page");
    }

}
