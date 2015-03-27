/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Upload file page object, holds all element of the HTML page relating to
 * share's upload file page in site.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class UploadFilePage extends ShareDialogue
{
    private Log logger = LogFactory.getLog(this.getClass());
    private final By uploadField;
    private final By uploadFolderIcon;

    /**
     * Constructor.
     */
    public UploadFilePage(WebDrone drone)
    {
        super(drone);
        boolean isHtml5 = alfrescoVersion.isFileUploadHtml5();
        uploadFolderIcon = isHtml5 ? By.cssSelector("img.title-folder") : By.cssSelector("form[id$='_default-htmlupload-form']");
        uploadField = isHtml5 ? By.cssSelector("input.dnd-file-selection-button") : By.cssSelector("input[id$='default-filedata-file']");
    }

    @Override
    public UploadFilePage render(RenderTime timer)
    {
        RenderElement uploadForm = RenderElement.getVisibleRenderElement(uploadFolderIcon);
        elementRender(timer, uploadForm);
        return this;
    }

    @Override
    public UploadFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public UploadFilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Action that selects the submit upload button.
     * 
     * @return boolean true if submited.
     */
    private void submitUpload()
    {
        By selector;
        if (alfrescoVersion.isCloud())
        {
            selector = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-cancelOk-button-button");
        }
        else
        {
            selector = By.cssSelector("button[id*='html-upload']");
        }
        try
        {
            HtmlElement okButton = new HtmlElement(drone.find(selector), drone);
            String ready = okButton.click();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("operation completed in: %s", ready));
            }
            drone.waitUntilElementDisappears(selector, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException te)
        {
            logger.error(te);
        }
    }

    public boolean isUploadInputDisplayed()
    {
        try
        {
            return drone.isElementDisplayed(uploadField);
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     * 
     * @param filePath String file location to upload
     * @return {@link SharePage} DocumentLibrary or a RepositoryPage response
     */
    public HtmlPage uploadFile(final String filePath)
    {
        DocumentLibraryPage lib = upload(filePath).render();
        lib.setShouldHaveFiles(true);
        return lib;
    }

    public HtmlPage upload(final String filePath)
    {
        WebElement uploadFileInput = drone.find(uploadField);
        uploadFileInput.sendKeys(filePath);
        if (!alfrescoVersion.isFileUploadHtml5())
        {
            submitUpload();
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Upload button has been actioned");
        }
        return FactorySharePage.getPage(drone.getCurrentUrl(), drone);
    }

    /**
     * Clicks on the cancel link.
     */
    public void cancel()
    {
        List<WebElement> cancelButton = drone.findAll(By.cssSelector("button[id$='default-cancelOk-button-button']"));
        for (WebElement webElement : cancelButton)
        {
            if (webElement.isDisplayed() && webElement.isEnabled())
            {
                webElement.click();
            }
        }
    }
}
