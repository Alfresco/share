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

import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This enums used to describe the task status.
 * 
 * @author Sprasanna
 * @since v1.7.0
 */
public enum ContentType
{
    PLAINTEXT(By.cssSelector("a[href$='mimeType=text/plain']")), HTML(By.cssSelector("a[href$='mimeType=text/html']")), XML(By
            .cssSelector("a[href$='mimeType=text/xml']")), GOOGLEDOCS(By.cssSelector("span.document-file")), GOOGLESPREADSHEET(By
            .cssSelector("span.spreadsheet-file")), GOOGLEPRESENTATION(By.cssSelector("span.presentation-file"));

    private By contentLocator;

    public By getContentLocator()
    {
        return contentLocator;
    }

    ContentType(By contentType)
    {
        this.contentLocator = contentType;

    }

    /**
     * Returns the corresponding page object based on the enum.
     * 
     * @param drone WebDrone
     * @return SitePage
     */

    public Class<?> getContentCreationPage(WebDriver driver)
    {

        switch (this)
        {
            case PLAINTEXT:
            case XML:
                return CreatePlainTextContentPage.class;
            case HTML:
                return CreateHtmlContentPage.class;
            default:
                break;
        }
        throw new PageException("Content Type did not match to retrun a page object");
    }
}
