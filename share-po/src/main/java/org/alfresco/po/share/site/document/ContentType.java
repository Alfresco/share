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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;

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
    private boolean isGoogleCreate = true;

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
     * @param
     * @return SitePage
     * @throws Exception
     */

    public HtmlPage getContentCreationPage(WebDrone drone)
    {

        switch (this)
        {
            case PLAINTEXT:
            case XML:
                return new CreatePlainTextContentPage(drone);
            case HTML:
                return new CreateHtmlContentPage(drone);
            case GOOGLEDOCS:
                return new GoogleDocsAuthorisation(drone, null, isGoogleCreate);
            case GOOGLESPREADSHEET:
                return new GoogleDocsAuthorisation(drone, null, isGoogleCreate);
            case GOOGLEPRESENTATION:
                return new GoogleDocsAuthorisation(drone, null, isGoogleCreate);
            default:
                break;
        }
        throw new PageException("Content Type did not match to retrun a page object");
    }
}
