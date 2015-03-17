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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * The Object the represent Site search result item on Site Search Dashlet.
 * 
 * @author snagarajan
 */
public class SiteSearchItem
{

    private ShareLink thumbnail;
    private ShareLink itemName;
    private ShareLink path;

    public SiteSearchItem(WebElement searchItem, WebDrone drone)
    {
        this.thumbnail = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-site'] a")), drone);
        this.itemName = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-path'] div>h3>a")), drone);
        try
        {
            this.path = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-path'] .details>a")), drone);
        }
        catch (NoSuchElementException nse)
        {
        }
    }

    public ShareLink getThumbnail()
    {
        return thumbnail;
    }

    public ShareLink getItemName()
    {
        return itemName;
    }

    public ShareLink getPath()
    {
        return path;
    }

}
