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

import java.io.File;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class DocumentEditOfflinePage extends DocumentDetailsPage
{
    private final By cancelEditOffLineBtn = By.cssSelector("div#onActionCancelEditing>a.action-link");
    private static final String VIEW_ORIGINAL_DOCUMENT = "div.document-view-original>a";
    private static final String VIEW_WORKING_COPY = "div.document-view-working-copy>a";

    /**
     * Ensures that the 'checked out' message is visible.
     * 
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public DocumentEditOfflinePage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(200L);
                }
                catch (InterruptedException ite)
                {
                }
            }
            try
            {
                if (!loadingMessageDisplayed())
                {
                    if (driver.findElement(cancelEditOffLineBtn).isDisplayed())
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

    /**
     * Checks to see if the page is still entering off line
     * edit mode by checking the background message.
     * 
     * @return true is background message is visible.
     */
    private boolean loadingMessageDisplayed()
    {
        return isJSMessageDisplayed();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentEditOfflinePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public synchronized boolean isCheckedOut()
    {
        return true;
    }

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public HtmlPage selectEditOffLine(File file)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Select the cancel edit off line link.
     * 
     * @return {@link HtmlPage} edit off line page.
     */
    public HtmlPage selectCancelEditing()
    {
        try
        {
            WebElement link = driver.findElement(cancelEditOffLineBtn);
            link.click();
            canResume();
        }
        catch (NoSuchElementException nse)
        {
        }
        return getCurrentPage();
    }

    /**
     * Mimics the action of clicking on View Original Document link
     * 
     * @return DocumentDetailsPage
     */
    public HtmlPage selectViewOriginalDocument()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(VIEW_ORIGINAL_DOCUMENT));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to select View Original Document ", e);
        }
        return getCurrentPage();
    }

    /**
     * Mimics the action of clicking on View Working Copy link
     * 
     * @return DocumentEditOfflinePage
     */
    public HtmlPage selectViewWorkingCopy()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(VIEW_WORKING_COPY));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to select View Working Copy ", e);
        }
        return getCurrentPage();
    }

}
