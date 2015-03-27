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
package org.alfresco.po.share.task;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of WorkFlow Item on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class TaskItem
{
    private String itemName;
    private ShareLink itemNameLink;
    private String description;
    private DateTime dateModified;
    private ShareLink viewMoreActionsLink;

    private static final By ITEM_NAME = By.cssSelector("h3.name");
    private static final By ITEM_NAME_LINK = By.cssSelector("h3.name a");
    private static final By ITEM_DESCRIPTION = By.cssSelector("div.description");
    private static final By ITEM_DATE_MODIFIED = By.cssSelector("div.viewmode-label");
    private static final By VIEW_MORE_ACTIONS_LINK = By.cssSelector("a.view_more_actions");

    public TaskItem(WebElement element, WebDrone drone, boolean isViewMoreActionDisplayed)
    {
        itemName = element.findElement(ITEM_NAME).getText();
        description = element.findElement(ITEM_DESCRIPTION).getText().split("Description: ")[1];
        dateModified = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(element.findElement(ITEM_DATE_MODIFIED).getText().split("on:")[1].trim());
        itemNameLink = new ShareLink(element.findElement(ITEM_NAME_LINK), drone);

        if (isViewMoreActionDisplayed)
        {
            viewMoreActionsLink = new ShareLink(element.findElement(VIEW_MORE_ACTIONS_LINK), drone);
        }
        else
        {
            viewMoreActionsLink = null;
        }
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getDescription()
    {
        return description;
    }

    public DateTime getDateModified()
    {
        return dateModified;
    }

    public ShareLink getItemNameLink()
    {
        return itemNameLink;
    }

    public ShareLink getViewMoreActionsLink()
    {
        if (viewMoreActionsLink != null)
        {
            return viewMoreActionsLink;
        }
        else
        {
            throw new PageOperationException("View More Actions Link is not available on this page");
        }
    }
}
