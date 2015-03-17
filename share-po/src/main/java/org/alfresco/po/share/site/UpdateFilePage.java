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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
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
    private static final String PLANIN_HTML_IDENTIFIER = "html-upload";
    private static final String HTML5_IDENTIFIER = "dnd-upload";
    private static final String NON_HTML5_INPUT_FILE_FIELD = "input[id$='default-filedata-file']";
    private static final String INPUT_DND_FILE_SELECTION_BUTTON = "input.dnd-file-selection-button";
    private String textAreaCssLocation;
    private String minorVersionRadioButton;
    private String majorVersionRadioButton;
    private String submitButton;
    private String cancelButton;
    private String documentVersion;
    @SuppressWarnings("unused")
    private final boolean isEditOffLine;

    /**
     * Constructor.
     */
    public UpdateFilePage(WebDrone drone, final String documentVersion)
    {
        this(drone, documentVersion, false);
    }

    /**
     * Constructor.
     */
    public UpdateFilePage(WebDrone drone, final String documentVersion, final boolean editOffline)
    {
        super(drone);
        this.documentVersion = documentVersion;
        this.isEditOffLine = editOffline;

        // Check if supports HTML5 form input as cloud supports and enterprise doesnt.
        String prefix = alfrescoVersion.isFileUploadHtml5() ? HTML5_IDENTIFIER : PLANIN_HTML_IDENTIFIER;
        textAreaCssLocation = String.format(TEXT_AREA_CSS, prefix);
        minorVersionRadioButton = String.format(MINOR_BTN_CSS, prefix);
        majorVersionRadioButton = String.format(MAJOR_BTN_CSS, prefix);
        submitButton = String.format(SUBMIT_BTN_CSS, prefix);
        cancelButton = String.format(CANCEL_BTN_CSS, prefix);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized UpdateFilePage render(RenderTime timer)
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

    @SuppressWarnings("unchecked")
    @Override
    public UpdateFilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Select the minor version tick box.
     */
    public void selectMinorVersionChange()
    {
        drone.findAndWait(By.cssSelector(minorVersionRadioButton)).click();
    }

    /**
     * Select the major version tick box.
     */
    public void selectMajorVersionChange()
    {
        drone.findAndWait(By.cssSelector(majorVersionRadioButton)).click();
    }

    /**
     * Clicks on the submit upload button.
     */
    public HtmlPage submit()
    {
        // Get the expected version number
        String previousVersion = (String) drone.executeJavaScript("Alfresco.getFileUploadInstance(this).showConfig.updateVersion;");
        drone.findAndWait(By.cssSelector(submitButton)).click();
        drone.waitUntilNotVisible(By.cssSelector("div[style*='visible'] div.hd span"), "Update File", SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
        HtmlPage page = drone.getCurrentPage();
        if (page instanceof DocumentDetailsPage)
        {
            return new DocumentDetailsPage(drone, previousVersion);
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
        WebElement input;
        if (alfrescoVersion.isFileUploadHtml5())
        {
            input = drone.find(By.cssSelector(INPUT_DND_FILE_SELECTION_BUTTON));
        }
        else
        {
            input = drone.find(By.cssSelector(NON_HTML5_INPUT_FILE_FIELD));
        }
        input.sendKeys(filePath);
    }

    /**
     * Sets the comment in the comments field
     * 
     * @param comment String of user comment.
     */
    public void setComment(final String comment)
    {
        WebElement commentBox = drone.find(By.cssSelector(textAreaCssLocation));
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
            drone.findAndWait(By.cssSelector(cancelButton)).click();
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

}
