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
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
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
            List<WebElement> buttons = driver.findElements(buttonSelector);
            long elementWaitTime = SECONDS.convert(maxPageLoadingTime, MILLISECONDS);
            for (WebElement button : buttons)
            {
                if (action.name().equals(button.getText()))
                {
                    button.click();
                    waitUntilNotVisibleWithParitalText(By.cssSelector("div#prompt>div.hd"), "Delete", elementWaitTime);
                    if (Action.Delete.equals(action))
                    {
                        By deleteMessageSelector = By.cssSelector("div.bd>span.message");
                        String deleteMessage = "deleted";
                        waitUntilVisible(deleteMessageSelector, deleteMessage, elementWaitTime);
                        waitUntilNotVisibleWithParitalText(deleteMessageSelector, deleteMessage, elementWaitTime);
                    }
                    return getCurrentPage();
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
