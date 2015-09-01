/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.po.share.cmm.admin;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogueAikau;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The Class ImportModelPopUp
 * 
 * @author Meenal Bhave
 */
@SuppressWarnings("unchecked")
public class ImportModelPopUp extends ShareDialogueAikau
{

    /** The logger */
    private static final Log LOGGER = LogFactory.getLog(CreateNewPropertyGroupPopUp.class);

    /** The selectors */
    private static final By SHARE_DIALOGUE_HEADER = By.id("CMM_IMPORT_DIALOG_title");

    private static final String IMPORT_BUTTON = ".footer .alfresco-buttons-AlfButton:first-of-type>span";

    private static final String BUTTON_CANCEL = ".footer .alfresco-buttons-AlfButton:last-of-type";

    private final By browseField = By.cssSelector("input.alfresco-html-FileInput");

    @Override
    public ImportModelPopUp render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(SHARE_DIALOGUE_HEADER));

        elementRender(timer, getVisibleRenderElement(By.cssSelector(IMPORT_BUTTON)), getVisibleRenderElement(By.cssSelector(BUTTON_CANCEL + ">span")));

        return this;
    }

    /**
     * Checks if the create button is enabled.
     * 
     * @return true, if the create button is enabled
     */
    public boolean isImportButtonEnabled()
    {
        try
        {
            WebElement button = findFirstDisplayedElement(By.cssSelector(IMPORT_BUTTON));
            if (button.getAttribute("class").contains("dijitDisabled"))
            {
                return false;
            }
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOGGER.info("Import button not displayed: ", e);
            return false;
        }
        return true;
    }

    public boolean isImportInputDisplayed()
    {
        try
        {
            return isElementDisplayed(browseField);
        }
        catch (Exception e)
        {
            LOGGER.info("Exception while checking if Import Input is displayed", e);
        }
        return false;
    }

    public HtmlPage importModel(final String filePath)
    {
        WebElement browseFileInput = driver.findElement(browseField);
        browseFileInput.sendKeys(filePath);

        clickImportButton();

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Import button has been actioned");
        }
        return factoryPage.getPage(driver);
    }

    /**
     * Action that selects the Import button.
     * 
     * @return void
     */
    private void clickImportButton()
    {
        By selector = By.cssSelector(IMPORT_BUTTON);

        try
        {
            WebElement button = driver.findElement(selector);
            button.click();

            waitUntilElementDisappears(selector, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOGGER.error("Unable to select the Import button ", e);
            throw new PageOperationException("Unable to select the Import button", e);
        }
    }

}
