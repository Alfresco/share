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

package org.alfresco.po.share.search;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Advance search Content Search page object, holds all element of the content
 * Search page.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
public class AdvanceSearchContentPage extends AdvanceSearchPage
{

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchContentPage render(RenderTime timer)
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
                if (isContentSearchDisplayed())
                {
                    if (isSearchButtonDisplayed() && isDateModifierFromDisplayed())
                    {
                        // It's there and visible
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
    public AdvanceSearchContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Check whether content search form is loaded correctly
     * 
     * @return true if page is displayed correctly
     */
    protected boolean isContentSearchDisplayed()
    {
        Boolean displayed = false;
        try
        {
            WebElement contentSearchForm = driver.findElement(CONTENT_SEARCH_FORM_DROPDOWN);
            if (contentSearchForm != null && contentSearchForm.getText().contains("Content"))
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
