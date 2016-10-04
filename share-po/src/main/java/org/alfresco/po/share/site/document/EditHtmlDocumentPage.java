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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.NoSuchElementException;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 *         version 1.7
 */
public class EditHtmlDocumentPage extends InlineEditPage
{
    private Log logger = LogFactory.getLog(DetailsPage.class);
    private static final String IFRAME_ID = "template_x002e_inline-edit_x002e_inline-edit_x0023_default_prop_cm_content_ifr";
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='default-form-submit-button']");


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

    /**
     * @return boolean
     */
    public boolean isEditHtmlDocumentPage()
    {
        try
        {
            driver.switchTo().frame(IFRAME_ID);
            boolean isDesiredPage = driver.findElement(By.cssSelector("#tinymce")).isDisplayed() ? true : false;
            driver.switchTo().defaultContent();
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
     * @return int
     */
    public int countOfTxtsFromEditor()
    {
        waitForElement(By.cssSelector("button[id$='default-form-cancel-button']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        driver.switchTo().frame(IFRAME_ID);
        int noOfElements = findAndWaitForElements(By.cssSelector("#tinymce>p")).size();
        driver.switchTo().defaultContent();
        return noOfElements;
    }

    /**
     * Edit the editor, enter new text line, count the lines and save it.
     * 
     * @param txtLine String
     */
    public void editText(String txtLine)
    {
        try
        {
            driver.switchTo().frame(IFRAME_ID);
            WebElement element = findAndWait(By.cssSelector("#tinymce"));
            element.sendKeys(txtLine);
            element.sendKeys(Keys.chord(Keys.ENTER));
            driver.switchTo().defaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Tinymce Editor is not found", toe);
        }
    }

    /**
     * Edit the editor, enter new text line, count the lines and save it.
     *
     * @param txtLine String
     */
    public void addTextToTinyMCE(String txtLine)
    {
        try
        {
            driver.switchTo().defaultContent();
            TinyMceEditor tinyMceEditor = new TinyMceEditor();
            tinyMceEditor.setTinyMce();
            String oldText = tinyMceEditor.getContent();
            tinyMceEditor.setText(oldText + txtLine);
            driver.switchTo().frame(IFRAME_ID);
            WebElement element = findAndWait(By.cssSelector("#tinymce"));
            element.sendKeys(Keys.chord(Keys.ENTER));
            driver.switchTo().defaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Tinymce Editor is not found", toe);
        }
    }
    /**
     * Edit the editor, enter new text line, count the lines and save it.
     * @return HtmlPage
     */
    public HtmlPage saveText()
    {
        try
        {
            findAndWait(SUBMIT_BUTTON).click();
            waitUntilElementDisappears(SUBMIT_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (TimeoutException toe)
        {
            logger.error("Submit button is not present", toe);
        }
        throw new PageOperationException();
    }

}
