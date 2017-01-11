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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Upload file page object, holds all element of the html page relating to
 * share's upload file page in site.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class UpdateFilePage extends SharePage
{
    private static Log logger = LogFactory.getLog(UpdateFilePage.class);
    private static final String CANCEL_BTN_CSS = "span[id*='%s'] button[id$='default-cancelOk-button-button']";
    private static final String SUBMIT_BTN_CSS = "span[id*='%s'] button[id$='default-upload-button-button']";
    private static final String MAJOR_BTN_CSS = "div[id*='%s'] input[id$='majorVersion-radioButton']";
    private static final String MINOR_BTN_CSS = "div[id*='%s'] input[id$='minorVersion-radioButton']";
    private static final String TEXT_AREA_CSS = "div[id*='%s'] textarea[id$='-description-textarea']";
    private static final String HTML5_IDENTIFIER = "dnd-upload";
    private static final String INPUT_DND_FILE_SELECTION_BUTTON = "input.dnd-file-selection-button";
    // Check if supports HTML5 form input as cloud supports and enterprise doesnt.
    
    private String textAreaCssLocation = String.format(TEXT_AREA_CSS, HTML5_IDENTIFIER);
    private String minorVersionRadioButton = String.format(MINOR_BTN_CSS, HTML5_IDENTIFIER);
    private String majorVersionRadioButton = String.format(MAJOR_BTN_CSS, HTML5_IDENTIFIER);
    private String submitButton = String.format(SUBMIT_BTN_CSS, HTML5_IDENTIFIER);
    private String cancelButton = String.format(CANCEL_BTN_CSS, HTML5_IDENTIFIER);
    private String documentVersion;
    private  boolean isEditOffLine;

    @SuppressWarnings("unchecked")
    @Override
    public UpdateFilePage render(RenderTime timer)
    {
        RenderElement textArea = RenderElement.getVisibleRenderElement(By.cssSelector(textAreaCssLocation));
        RenderElement minorRadioButton = RenderElement.getVisibleRenderElement(By.cssSelector(minorVersionRadioButton));
        RenderElement majorRadioButton = RenderElement.getVisibleRenderElement(By.cssSelector(majorVersionRadioButton));
        elementRender(timer, textArea, minorRadioButton, majorRadioButton);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UpdateFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * Select the minor version tick box.
     */
    public void selectMinorVersionChange()
    {
        findAndWait(By.cssSelector(minorVersionRadioButton)).click();
    }

    /**
     * Select the major version tick box.
     */
    public void selectMajorVersionChange()
    {
        findAndWait(By.cssSelector(majorVersionRadioButton)).click();
    }

    /**
     * Clicks on the submit upload button.
     */
    public HtmlPage submitUpload()
    {
        // Get the expected version number
        String previousVersion = (String) executeJavaScript("Alfresco.getFileUploadInstance(this).showConfig.updateVersion;");
        findAndWait(By.cssSelector(submitButton)).click();
        waitUntilNotVisible(By.cssSelector("div[style*='visible'] div.hd span"), "Update File", SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
        waitUntilAlert();
        HtmlPage page = getCurrentPage();
        if (page instanceof DocumentDetailsPage)
        {
            DocumentDetailsPage ddp = factoryPage.instantiatePage(driver,DocumentDetailsPage.class);
            ddp.setPreviousVersion(previousVersion);
            return ddp;
        }
        return page;
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     * 
     * @param filePath String file location to upload
     */
    public void uploadFile(final String filePath)
    {
        WebElement input = driver.findElement(By.cssSelector(INPUT_DND_FILE_SELECTION_BUTTON));
        input.sendKeys(filePath);
        domEventCompleted();
    }

    /**
     * Sets the comment in the comments field
     * 
     * @param comment String of user comment.
     */
    public void setComment(final String comment)
    {
        WebElement commentBox = driver.findElement(By.cssSelector(textAreaCssLocation));
        commentBox.click();
        commentBox.sendKeys(comment);
    }

    /**
     * Clicks on the submit upload button.
     */
    public void selectCancel()
    {
        try
        {
            findAndWait(By.cssSelector(cancelButton)).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the cancel button.", e);
            throw new PageException("Unable to find the cancel button css : " + cancelButton);
        }
    }

    protected void setTextAreaCssLocation(String textAreaCssLocation)
    {
        this.textAreaCssLocation = textAreaCssLocation;
    }

    protected void setMinorVersionRadioButton(String minorVersionRadioButton)
    {
        this.minorVersionRadioButton = minorVersionRadioButton;
    }

    protected void setMajorVersionRadioButton(String majorVersionRadioButton)
    {
        this.majorVersionRadioButton = majorVersionRadioButton;
    }

    protected void setSubmitButton(String submitButton)
    {
        this.submitButton = submitButton;
    }

    protected void setCancelButton(String cancelButton)
    {
        this.cancelButton = cancelButton;
    }

    protected String getMinorVersionRadioButton()
    {
        return minorVersionRadioButton;
    }

    protected String getMajorVersionRadioButton()
    {
        return majorVersionRadioButton;
    }

    protected String getSubmitButton()
    {
        return submitButton;
    }

    protected String getDocumentVersion()
    {
        return documentVersion;
    }

    public boolean isEditOffLine()
    {
        return isEditOffLine;
    }

    public void setDocumentVersion(String documentVersion)
    {
        this.documentVersion = documentVersion;
    }
    public void setEditOffline(boolean isEditOffline)
    {
        this.isEditOffLine = isEditOffline;
    }

}
