/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.po.wqs;

import org.alfresco.po.share.util.FileDownloader;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsPublicationPage extends WcmqsAbstractPage
{

    public static final List<String> PUBLICATION_PAGES = Arrays.asList(
            "Alfresco WCM", "Alfresco Share", "Alfresco Datasheet - Social Computing",
            "Datasheet - Enterprise Network", "Content Platform", "OEM Datasheet", "Alfresco Office");
    private final static Object waitObject = new Object();
    @RenderWebElement
    private final By PUBLICATION_NAME = By.cssSelector(".interior-content>h2");
    private final By PUBLICATION_DATE = By.cssSelector(".blog-list-misc>span");
    private final By PUBLICATION_PREVIEW = By.id("web-preview-container");
    private final By PUBLICATION_TAGS_SECTION = By.cssSelector(".interior-content>h3");
    private final By PUBLICATION_DETAILS_SECTION = By.cssSelector(".address-box");
    private final By PUBLICATION_DESCRIPTION = By.cssSelector(".address-box>p");
    private final By PUBLICATION_AUTHOR = By.xpath("//div[@class='address-box']//strong[contains(text(),'Author')]/..");
    private final By PUBLICATION_PUBLISH_DATE = By.xpath("//div[@class='address-box']//strong[contains(text(),'Published')]/..");
    private final By PUBLICATION_SIZE = By.xpath("//div[@class='address-box']//strong[contains(text(),'Size')]/..");
    private final By PUBLICATION_MIME_TYPE = By.xpath("//div[@class='address-box']//strong[contains(text(),'Mime Type')]/..");
    private final By PUBLICATION_DOWNLOAD = By.xpath("//div[@class='address-box']//strong[contains(text(),'Download')]/..");
    private final By PUBLICATION_DOWNLOAD_LINK = By.cssSelector(".address-box>ul>li>a");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public WcmqsPublicationPage(WebDrone drone)
    {
        super(drone);
    }

    public static void fileDownloaded(File file) throws InterruptedException
    {
        synchronized (waitObject)
        {
            if (file.exists())
            {
                waitObject.notifyAll();
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsPublicationPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(PUBLICATION_NAME));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsPublicationPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsPublicationPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public boolean isPublicationNameDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_NAME).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }

        return false;

    }

    public boolean isPublicationDetailsDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_DETAILS_SECTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationDateDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_DATE).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationPreviewDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_PREVIEW).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    public boolean isPublicationTagsDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_TAGS_SECTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationDescriptionDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_DESCRIPTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    public boolean isPublicationAuthorDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_AUTHOR).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationPublishDateDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_PUBLISH_DATE).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    public boolean isPublicationSizeDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_SIZE).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationMimeDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_MIME_TYPE).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    public boolean isPublicationDownloadDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_DOWNLOAD).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    /**
     * Finds and clicks on the file download link.
     *
     * @return filePath String file url
     */
    private String downloadFile()
    {
        String filePath = "";
        try
        {
            WebElement link = drone.find(PUBLICATION_DOWNLOAD_LINK);

            link.click();
            String fileUrl = link.getAttribute("href");
            if (fileUrl != null && !fileUrl.isEmpty())
            {
                filePath = fileUrl.replace("?a=true", "");
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return filePath;
    }

    /**
     * Finds and clicks on the file download link.
     *
     * @return filePath String file url
     */
    public File downloadFiles() throws Exception
    {
        FileDownloader downloader = new FileDownloader(drone);
        File downloadedFile = new File(downloader.getLocalDownloadPath() + "testfile" + System.currentTimeMillis());
        downloader.download(downloadFile(), downloadedFile);
        synchronized (waitObject)
        {
            while (!downloadedFile.exists())
            {
                waitObject.wait();
            }
        }
        fileDownloaded(downloadedFile);

        return downloadedFile;

    }

    /**
     * Method to click on a tag
     *
     * @param tagName
     */
    public void clickDocumentTag(String tagName)
    {
        try
        {
            drone.findAndWait(By.cssSelector(String.format("a[href$='%s']", tagName))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find tag. " + e.toString());
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Exceeded time to find tag. " + e.toString());
        }

    }

}
