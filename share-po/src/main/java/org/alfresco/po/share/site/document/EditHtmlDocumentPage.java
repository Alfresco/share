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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author nshah
 *         version 1.7
 */
public class EditHtmlDocumentPage extends InlineEditPage
{
    private Log logger = LogFactory.getLog(DetailsPage.class);
    private static final String IFRAME_ID = "template_x002e_inline-edit_x002e_inline-edit_x0023_default_prop_cm_content_ifr";
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='default-form-submit-button']");

    public EditHtmlDocumentPage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    @Override
    public EditHtmlDocumentPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION),  getVisibleRenderElement(SUBMIT_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditHtmlDocumentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditHtmlDocumentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @return
     */
    public boolean isEditHtmlDocumentPage()
    {
        try
        {
            drone.switchToFrame(IFRAME_ID);
            boolean isDesiredPage = drone.find(By.cssSelector("#tinymce")).isDisplayed() ? true : false;
            drone.switchToDefaultContent();
            return isDesiredPage;

        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * return the count of text lines entered in editor.
     * 
     * @return
     */
    public int countOfTxtsFromEditor()
    {
        drone.waitForElement(By.cssSelector("button[id$='default-form-cancel-button']"), maxPageLoadingTime);
        drone.switchToFrame(IFRAME_ID);
        int noOfElements = drone.findAndWaitForElements(By.cssSelector("#tinymce>p")).size();
        drone.switchToDefaultContent();
        return noOfElements;
    }

    /**
     * Edit the editor, enter new text line, count the lines and save it.
     * 
     * @param txtLine
     * @return
     */
    public void editText(String txtLine)
    {
        try
        {
            drone.switchToFrame(IFRAME_ID);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
            element.sendKeys(txtLine);
            element.sendKeys(Keys.chord(Keys.ENTER));
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Tinymce Editor is not found", toe);
        }
    }

    /**
     * Edit the editor, enter new text line, count the lines and save it.
     *
     * @param txtLine
     * @return
     */
    public void addTextToTinyMCE(String txtLine)
    {
        try
        {
            drone.switchToDefaultContent();
            TinyMceEditor tinyMceEditor = new TinyMceEditor(drone);
            tinyMceEditor.setTinyMce(IFRAME_ID);
            String oldText = tinyMceEditor.getContent();
            tinyMceEditor.setText(oldText + txtLine);
            drone.switchToFrame(IFRAME_ID);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
            element.sendKeys(Keys.chord(Keys.ENTER));
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Tinymce Editor is not found", toe);
        }
    }
    /**
     * Edit the editor, enter new text line, count the lines and save it.
     * @return
     */
    public HtmlPage saveText()
    {
        try
        {
            drone.findAndWait(SUBMIT_BUTTON).click();
            drone.waitUntilElementDisappears(SUBMIT_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException toe)
        {
            logger.error("Submit button is not present", toe);
        }
        throw new PageOperationException();
    }

}
