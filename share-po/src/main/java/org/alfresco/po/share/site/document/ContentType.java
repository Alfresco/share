
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
