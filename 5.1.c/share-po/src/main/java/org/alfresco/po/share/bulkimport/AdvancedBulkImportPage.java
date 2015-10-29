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
package org.alfresco.po.share.bulkimport;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author Sergey Kardash
 */

public abstract class AdvancedBulkImportPage extends SharePage
{

    // Button
    protected static final By IDLE_CURRENT_STATUS = By.xpath("//td[contains(text(),'Current status')]/following-sibling::td/span[text()='Idle']");
    protected static final By IN_PROGRESS_CURRENT_STATUS = By.xpath("//td[contains(text(),'Current status')]/following-sibling::td/span[text()='In progress']");

    // CheckBoxes
    protected static final By CHECK_BOX_DISABLE_RULES = By.cssSelector("input[id='disableRules']");

    // Button
    protected static final By INITIATE_BULK_IMPORT_BUTTON = By.cssSelector("input[type='submit']");

    // Input fields
    protected static final By IMPORT_DIRECTORY = By.cssSelector("input[name$='sourceDirectory']");
    protected static final By TARGET_SPACE_PATH = By.cssSelector("input[name$='targetPath']");

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedBulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedBulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to set disable rules check box
     */
    public void setDisableRulesCheckbox()
    {
        try
        {
            driver.findElement(CHECK_BOX_DISABLE_RULES).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find disable rules checkbox");
        }
    }

    /**
     * Method for clicking 'Initiate Bulk Import' button
     */
    public void clickImport()
    {
        WebElement importButton = findAndWait(INITIATE_BULK_IMPORT_BUTTON);
        try
        {
            importButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find 'Initiate Bulk Import' button");
        }
    }

    /**
     * Method to set String input in the field
     * 
     * @param input WebElement
     * @param value String
     */
    public void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find the element");
        }
    }

    /**
     * Method to set import directory field
     * 
     * @param importDirectory String
     */
    public void setImportDirectoryField(final String importDirectory)
    {
        setInput(findAndWait(IMPORT_DIRECTORY), importDirectory);
    }

    /**
     * Method to set target space path field
     * 
     * @param path String
     */
    public void setTargetPathField(final String path)
    {
        setInput(findAndWait(TARGET_SPACE_PATH), path);
    }
}
