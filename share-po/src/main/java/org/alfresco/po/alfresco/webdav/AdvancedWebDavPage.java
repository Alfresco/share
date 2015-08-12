/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * @author Sergey Kardash
 */
public class AdvancedWebDavPage extends SharePage
{

    private final By DIRECTORY_LISTING = By.cssSelector("td[class='textLocation']");
    private final String DIRECTORY_LINK = "//td[@class='textData']/a[text()='%s']";
    private final By UP_TO_LEVEL = By.xpath("//td[@class='textData']/a[text()='[Up a level]']");

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedWebDavPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedWebDavPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to verify webdav is opened
     *
     * @return boolean
     */
    public boolean isOpened()
    {
        try
        {
            return driver.findElement(DIRECTORY_LISTING).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;

    }

    /**
     * Method to verify webdav is opened
     *
     * @return String
     */
    public String getDirectoryText()
    {
        return findAndWait(DIRECTORY_LISTING).getText();
    }

    /**
     * Method to click directory for webdav page
     */
    public void clickDirectory(String directoryName)
    {
        findAndWait(By.xpath(String.format(DIRECTORY_LINK, directoryName))).click();
    }

    /**
     * Method to check directory is displayed for webdav page
     *
     * @return boolean
     */
    public boolean checkDirectoryDisplayed(String directoryName)
    {
        try
        {
            return driver.findElement(By.xpath(String.format(DIRECTORY_LINK, directoryName))).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Method to click directory for webdav page
     */
    public void clickUpToLevel()
    {
        findAndWait(UP_TO_LEVEL).click();
    }

    /**
     * Method to check directory is displayed for webdav page
     *
     * @return boolean
     */
    public boolean checkUpToLevelDisplayed()
    {
        try
        {
            return driver.findElement(UP_TO_LEVEL).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

}
