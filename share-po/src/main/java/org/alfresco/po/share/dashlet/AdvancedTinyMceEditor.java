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
package org.alfresco.po.share.dashlet;

import java.util.Set;

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Advanced TinyMce Editor page object, holds the extra features edit and format the text.
 * 
 * @author Chiran
 */
public class AdvancedTinyMceEditor extends TinyMceEditor
{
    private Log logger = LogFactory.getLog(AdvancedTinyMceEditor.class);

    private static final By LINK_CSS = By.cssSelector("i.mce-i-link");
    private static final By UNLINK_CSS = By.cssSelector("span.mceIcon.mce_unlink");
    private static final By ANCHOR_CSS = By.cssSelector("i.mce-i-anchor");
    private static final By IMAGE_LINK_CSS = By.cssSelector("i.mce-i-image");
    private static final By HTML_CODE_CSS = By.cssSelector("i.mce-i-code");

    /**
     * This method does the clicking on Insert/Edit Link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditLinkPage
     */
    public InsertOrEditLinkPage clickInsertOrEditLink()
    {
        try
        {
            driver.findElement(LINK_CSS).click();
            return new InsertOrEditLinkPage(driver, findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    /**
     * This method does the clicking on UnLink present on Site Notice Configure Tiny mce editor.
     * 
     */
    public void clickUnLink()
    {
        try
        {
            driver.findElement(UNLINK_CSS).click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit UnLink css on SiteNoticeTinyMce:", te);
        }
    }

    /**
     * This method does the clicking on Insert/Edit Anchor link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditAnchorPage
     */
    public InsertOrEditAnchorPage selectInsertOrEditAnchor()
    {
        try
        {
            driver.findElement(ANCHOR_CSS).click();
            return new InsertOrEditAnchorPage(driver, findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Anchor Link css on SiteNoticeTinyMce:", te);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Anchor dialog", nse);
        }
    }
    
    /**
     * This method does the clicking on Insert/Edit Image link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditImagePage
     */
    public InsertOrEditImagePage selectInsertOrEditImage()
    {
        try
        {
            driver.findElement(IMAGE_LINK_CSS).click();
            return new InsertOrEditImagePage(driver, findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Image Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    /**
     * This method does the clicking on Html Editor link present on Site Notice Configure Tiny mce editor.
     * 
     * @return HtmlSourceEditorPage
     */
    public HtmlSourceEditorPage selectHtmlSourceEditor()
    {
        try
        {
            driver.findElement(HTML_CODE_CSS).click();
            return new HtmlSourceEditorPage(driver, findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the HtmlEditor Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    @SuppressWarnings("unused")
    private void switchDroneToNewWindowOfListOfWindows(String windowName)
    {
        Set<String> windowHandles = driver.getWindowHandles();

        for (String windowHandle : windowHandles)
        {
            driver.switchTo().window(windowHandle);
            logger.info(driver.getTitle());
            if (driver.getTitle().equals(windowName))
            {
                return;
            }
        }

        throw new PageOperationException("Unable to find the given window name.");
    }
}
