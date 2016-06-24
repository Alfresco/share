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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * Edit document properties pop up page object, holds all element of the HTML page
 * relating to share's edit document properties pop up page.
 * 
 * @author Michael Suzuki
 * @since 1.3.1
 */
@Deprecated
public class EditDocumentPropertiesPopup extends AbstractEditProperties
{

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPopup render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (isEditPropertiesPopupVisible())
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPopup render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isEditPropertiesPopupVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector("form.bd")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
    }

    /**
     * Clicks on save button from the popup view on document
     * library page.
     * 
     * @return {@link DocumentLibraryPage} page response
     */
    public HtmlPage selectSave()
    {
        clickSave();
        canResume();
        return getCurrentPage();
    }

    /**
     * Select cancel button.
     * 
     * @return {@link DocumentLibraryPage} page response
     */
    public HtmlPage selectCancel()
    {
        clickOnCancel();
        return getCurrentPage();
    }
}
