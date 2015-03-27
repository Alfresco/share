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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Abhijeet Bharade
 */
public class FilmStripViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    private static Log logger = LogFactory.getLog(FilmStripViewFileDirectoryInfo.class);

    /**
     * @param nodeRef
     * @param webElement
     * @param drone
     */
    public FilmStripViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);

        THUMBNAIL_TYPE = String.format(".//div[@class='alf-filmstrip-nav-item-thumbnail']//img[@id='%s']", nodeRef);
        rowElementXPath = "../../../..";
        FILE_DESC_IDENTIFIER = "div.detail:first-of-type span.item";
        THUMBNAIL = THUMBNAIL_TYPE + "/../..";
        DETAIL_WINDOW = By.xpath("//div[@class='alf-actions']/../../..");
        resolveStaleness();
        TAG_ICON = "//h3[@class='filename']/span/a[text()='%s']/../../../div/span[@class='insitu-edit']";
    }

    /**
     * @return WebElement
     */
    @Override
    protected WebElement getInfoIcon()
    {
        try
        {
            if (drone.findAndWait(By.xpath(THUMBNAIL)).isDisplayed())
            {
                selectThumbnail();
            }
            else
            {
                throw new PageException("Thumbnail not visible");
            }
            return findAndWait(By.cssSelector("a.alf-show-detail"));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css.", e);
        }

        throw new PageException("File directory info with title was not found");
    }

    @Override
    public String getName()
    {
        return drone.findAndWait(By.xpath(THUMBNAIL)).getText();
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    @Override
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = drone.findAndWait(By.xpath(THUMBNAIL_TYPE));
            if (logger.isTraceEnabled())
            {
                logger.trace("thumbnailType - " + thumbnailType.getAttribute("src"));
            }
            return thumbnailType.getAttribute("src").contains("folder");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        drone.findAndWait(By.xpath(THUMBNAIL)).click();
        domEventCompleted();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Gets the Title of the file or directory, if none then empty string is returned.
     * 
     * @return String Content description
     */
    @Override
    public String getTitle()
    {
        clickInfoIcon();
        return super.getTitle();
    }

    @Override
    public void selectCheckbox()
    {
        clickInfoIcon();
        super.selectCheckbox();
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTitle()
     */
    @Override
    public HtmlPage clickOnTitle()
    {
        clickInfoIcon();
        return super.clickOnTitle();
    }
}