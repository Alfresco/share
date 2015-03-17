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

package org.alfresco.po.share.search;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Advance Folder Search page object, holds all element of the folder search
 * page. The user can search with the following elements (keyword, Name,
 * title, Description).
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */

public class AdvanceSearchFolderPage extends AdvanceSearchPage
{
    protected static final By FOLDER_SEARCH_FORM = By.cssSelector("button[id$='selected-form-button-button']");

    /**
     * Constructor.
     */
    public AdvanceSearchFolderPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Page rendering.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchFolderPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchFolderPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException ite)
                {
                }
            }
            try
            {
                if (isFolderSearchPageDisplayed())
                {
                    if (isSearchButtonDisplayed())
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
                // Keep waiting for it
            }
            timer.end();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchFolderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Validate whether folder Search is displayed by validating the look for
     * field of search Page.
     * 
     * @return true Folder is present.
     */
    protected boolean isFolderSearchPageDisplayed()
    {
        Boolean displayed = false;
        try
        {
            WebElement folderSearchForm = drone.find(FOLDER_SEARCH_FORM);
            if (folderSearchForm != null && folderSearchForm.getText().contains("Folders"))
            {
                displayed = true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return displayed;
    }
}
