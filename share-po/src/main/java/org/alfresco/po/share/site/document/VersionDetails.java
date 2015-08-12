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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Representation of Version Details.
 * 
 * @author Ranjith Manyam
 * @since 5.0.0
 */
public class VersionDetails
{

    private String versionNumber;
    private String fileName;
    private ShareLink userName;
    private String lastModified;
    private String comment;
    private String fullDetails;

    public VersionDetails(WebDriver driver, WebElement element, FactoryPage factoryPage)
    {
        try
        {
            versionNumber = element.findElement(By.cssSelector("div.version-panel-left>span.document-version")).getText();
            fileName = element.findElement(By.cssSelector("div.version-panel-right>h3")).getText();
            userName = new ShareLink(element.findElement(By.cssSelector("div.version-panel-right>div.version-details>div.version-details-right>a")), driver, factoryPage);
            lastModified = element.findElement(By.cssSelector("div.version-panel-right>div.version-details>div.version-details-right>span")).getText();
            comment = element.findElement(By.cssSelector("div.version-panel-right>div.version-details>div.version-details-right")).getText().split("\n")[1];
            fullDetails = element.findElement(By.cssSelector(".version-details-right")).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find elements", nse);
        }
    }

    public String getVersionNumber()
    {
        return versionNumber;
    }

    public String getFileName()
    {
        return fileName;
    }

    public ShareLink getUserName()
    {
        return userName;
    }

    public String getLastModified()
    {
        return lastModified;
    }

    public String getComment()
    {
        return comment;
    }

    public String getFullDetails()
    {
        return fullDetails;
    }
}
