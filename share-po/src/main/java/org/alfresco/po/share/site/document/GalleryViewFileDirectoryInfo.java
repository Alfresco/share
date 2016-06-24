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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the Gallery View specific File Directory Info implementations.
 * 
 * @author cbairaajoni
 */
public class GalleryViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    private static Log logger = LogFactory.getLog(GalleryViewFileDirectoryInfo.class);

    private WebElement fileDirectoryInfo;
//
//    public GalleryViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDriver driver)
//    {
//        super(nodeRef, webElement, driver);
//
//        FILENAME_IDENTIFIER = "div.alf-label>a";
//        THUMBNAIL_TYPE = "div.alf-gallery-item-thumbnail>span";
//        rowElementXPath = "../../../..";
//        FILE_DESC_IDENTIFIER = "h3.filename+div.detail+div.detail>span";
//        TAG_LINK_LOCATOR = By.cssSelector("div>div>span>span>a.tag-link");
//        DETAIL_WINDOW = By.xpath("//div[@class='alf-detail-thumbnail']/../../..");
//        fileDirectoryInfo = webElement;
//        resolveStaleness();
//
//        if (isFolder())
//        {
//            THUMBNAIL = "div.alf-gallery-item-thumbnail span a";
//        }
//        else
//        {
//            THUMBNAIL = "div.alf-gallery-item-thumbnail>div+div+a>img";
//        }
//    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        mouseOver(findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']", getName()))));
        return super.selectThumbnail();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#getFileOrFolderHeight()
     */
    public double getFileOrFolderHeight()
    {
        try
        {
            String style = fileDirectoryInfo.getAttribute("style");
            return Double.valueOf(style.substring(style.indexOf(" ") + 1, style.indexOf("p")));
        }
        catch (NumberFormatException e)
        {
            logger.error("Unable to convert String into int:", e);
        }

        throw new PageOperationException("Error in finding the file size.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCheckbox()
     */
    @Override
    public void selectCheckbox()
    {
        mouseOver(findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']", getName()))));
        super.selectCheckbox();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getThumbnailURL()
     */
    @Override
    public String getThumbnailURL()
    {
        try
        {
            String xPath;

            if (isFolder())
            {
                xPath = ".//div[@class='alf-label']/a[text()='%s']/../../span/a/img";
            }
            else
            {
                xPath = ".//div[@class='alf-label']/a[text()='%s']/../../a/img";
            }

            WebElement img = findAndWait(By.xpath(String.format(xPath, getName())));

            return img.getAttribute("src");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Error in finding the file size.", e);
        }
    }
}
