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

package org.alfresco.po.share.dashlet.mydiscussions;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Class that represents Delete Topic Dialog Popup
 *
 * @author jcule
 */
public class DeleteTopicDialogPage extends SharePage
{
    private static Log logger = LogFactory.getLog(DeleteTopicDialogPage.class);

    private static final String DELETE_TOPIC_TITLE = "//div[text()='Delete Topic']";
    private static final String DELETE_BUTTON = "//button[text()='Delete']";
    private static final String CANCEL_BUTTON = "//button[text()='Cancel']";

    @SuppressWarnings("unchecked")
    @Override
    public DeleteTopicDialogPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.xpath(DELETE_TOPIC_TITLE)), getVisibleRenderElement(By.xpath(DELETE_BUTTON)),
                getVisibleRenderElement(By.xpath(CANCEL_BUTTON)));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteTopicDialogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Clicks on topic delete button
     */
    public HtmlPage clickOnDeleteButton()
    {
        try
        {
            WebElement deleteButton = findAndWait(By.xpath(DELETE_BUTTON));
            deleteButton.click();
            waitUntilAlert();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Exceeded the time to find xpath for topic delete button on Delete Topic Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic delete button on Delete Topic Page.");
    }

    /**
     * Clicks on cancel button
     */
    public void clickOnCancelButton()
    {
        try
        {
            WebElement deleteButton = findAndWait(By.xpath(CANCEL_BUTTON));
            deleteButton.click();
        }
        catch (TimeoutException te)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Exceeded the time to find xpath for cancel topic delete button on Delete Topic Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic cancel delete button on Delete Topic Page.");
    }

}
