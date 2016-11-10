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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.ShareDialogue;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * This is the pop up dialog to select the folder type.
 * 
 * @author Abhijeet Bharade
 * @version 1.7.0
 */

public class ChangeTypePage extends ShareDialogue
{
    private static final By TYPE_DROPDOWN = By.cssSelector("div[style^='visibility: visible;'] form select");
    private static final By CANCEL_BUTTON = By.cssSelector("div[style^='visibility: visible;'] form button[id$='-cancel-button']");
    protected static final By OK_BUTTON = By.cssSelector("div[style^='visibility: visible;'] form button[id$='-ok-button']");

    @SuppressWarnings("unchecked")
    @Override
    public ChangeTypePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangeTypePage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(TYPE_DROPDOWN), RenderElement.getVisibleRenderElement(OK_BUTTON));
        }
        catch (NoSuchElementException e)
        {
        }
        return this;
    }

    /**
     * Checks whether the page/dialog is displayed.
     * 
     * @return boolean
     */
    public boolean isChangeTypeDisplayed()
    {
        try
        {
            return driver.findElement(TYPE_DROPDOWN).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Gets the types from dropdown box
     */
    public List<String> getTypes()
    {
        List<WebElement> typeOptions = driver.findElements(By.cssSelector("div[style^='visibility: visible;'] form select option"));
        List<String> typesList = new ArrayList<String>();
        for (WebElement webElement : typeOptions)
        {
            typesList.add(webElement.getText());
        }
        return typesList;
    }

    /**
     * Clicks on cancel button to close the dlg.
     * 
     * @return {@link FolderDetailsPage}
     */
    public HtmlPage selectCancel()
    {
        driver.findElement(CANCEL_BUTTON).click();
        return getCurrentPage();
    }

    public void selectChangeType(final String changeType)
    {
        if (changeType == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement dropDown = findAndWait(TYPE_DROPDOWN);
        Select select = new Select(dropDown); 
        select.selectByVisibleText(changeType);
        select.getFirstSelectedOption().click();
    }
    
    public void selectChangeTypeByIndex(final int index)
    {
        WebElement dropDown = findAndWait(TYPE_DROPDOWN);
        Select select = new Select(dropDown); 
        select.selectByIndex(index);
    }

    /**
     * Clicks on save button to close the dlg.
     * 
     * @return {@link DocumentDetailsPage or FolderDetailsPage}
     */
    public HtmlPage selectSave()
    {
        WebElement okButton = driver.findElement(OK_BUTTON);
        okButton.click();
        return getCurrentPage();
    }

}
