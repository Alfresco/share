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
package org.alfresco.po.share.site.wiki;

import org.alfresco.po.PageElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * @author Marina.Nenadovets
 */
public class WikiPageDirectoryInfo extends PageElement
{


    @FindBy(css=".actionPanel>div.editPage>a") Link edit;
    public void clickEdit()
    {
        edit.click();
    }

    @FindBy(css=".detailsPage>a") Link details;
    /**
     * Method to click Details link
     *
     * @return Wiki page
     */
    public void clickDetails()
    {
        details.click();
    }
    @FindBy(css=".deletePage>a")Link delete;
    /**
     * Method to click Delete
     *
     * @return Wiki page
     */
    public void clickDelete()
    {
        delete.click();
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isEditLinkPresent()
    {
        try
        {
            return edit.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isDeleteLinkPresent()
    {
        try
        {
            return delete.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
}
