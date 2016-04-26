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
package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
    @FindBy(css="input.dnd-file-selection-button") WebElement uploadFileInput;
    @RenderWebElement @FindBy(css="img.title-folder") WebElement uploadFolderIcon; 

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    public boolean isUploadInputDisplayed()
    {
       return uploadFileInput.isDisplayed();
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
        uploadFileInput.sendKeys(filePath);
        if (logger.isTraceEnabled())
        {
            logger.trace("Upload button has been actioned");
        }
        return getCurrentPage();
    }

    /**
     * Clicks on the cancel link.
     */
    public void cancel()
    {
        List<WebElement> cancelButton = driver.findElements(By.cssSelector("button[id$='default-cancelOk-button-button']"));
        for (WebElement webElement : cancelButton)
        {
            if (webElement.isDisplayed() && webElement.isEnabled())
            {
                webElement.click();
            }
        }
    }
}
