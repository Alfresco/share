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
package org.alfresco.po.share.adminconsole;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Part of Tag Manager page. Showing when you edit tag on the page.
 * @author Olga Antonik
 */
public class EditTagForm extends PageElement 
{

    private final static By FORM = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-dialog");
    private final static By RENAME_TAG_INPUT = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-name");
    private final static By OK = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-ok-button");
    private final static By CANCEL = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-cancel-button");

    /**
     * Fill field with new tag name.
     *
     * @param text new tag name
     */
    public void fillTagField(String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(RENAME_TAG_INPUT);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    /**
     * Mimic click on OK button.
     */
    public void clickOk()
    {
        click(OK);
    }

    /**
     * Mimic click on Cancel button.
     */
    public void clickCancel()
    {
        click(CANCEL);
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    /**
     * Check that form display on page.
     *
     * @return true - if form displayed.
     */
    public boolean isDisplay()
    {
        try
        {
            return driver.findElement(FORM).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

}
