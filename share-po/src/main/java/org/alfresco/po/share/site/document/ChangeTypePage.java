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
package org.alfresco.po.share.site.document;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * This is the pop up dialog to select the folder type.
 * 
 * @author Abhijeet Bharade
 * @version 1.7.0
 */

public class ChangeTypePage extends SharePage
{

    private static final By TYPE_DROPDOWN = By.cssSelector("div[style^='visibility: visible;'] form select");
    private static final By CANCEL_BUTTON = By.cssSelector("div[style^='visibility: visible;'] form button[id$='-cancel-button']");
    protected static final By OK_BUTTON = By.cssSelector("div[style^='visibility: visible;'] form button[id$='-ok-button']");

    protected ChangeTypePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangeTypePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangeTypePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangeTypePage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(TYPE_DROPDOWN));
        }
        catch (NoSuchElementException e)
        {
        }
        return this;
    }

    /**
     * Checks whether the page/dialog is displayed.
     * 
     * @return
     */
    public boolean isChangeTypeDisplayed()
    {
        try
        {
            return drone.find(TYPE_DROPDOWN).isDisplayed();
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
        List<WebElement> typeOptions = drone.findAll(By.cssSelector("div[style^='visibility: visible;'] form select option"));
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
        drone.find(CANCEL_BUTTON).click();
        return new FolderDetailsPage(drone);
    }

    public void selectChangeType(final String changeType)
    {
        if (changeType == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement dropDown = drone.find(TYPE_DROPDOWN);
        Select select = new Select(dropDown);
        select.selectByVisibleText(changeType);
    }

    /**
     * Clicks on save button to close the dlg.
     * 
     * @return {@link FolderDetailsPage}
     */
    public HtmlPage selectSave()
    {
        drone.find(OK_BUTTON).click();
        return new FolderDetailsPage(drone);
    }

}
