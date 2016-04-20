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
package org.alfresco.po.share.site;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Delete site popup comes when you click delete button on {@link SiteFinderPage}.
 * 
 * @author Bogdan Bocancea
 */

public class DeleteSitePage extends SharePage
{

    //private static final String DELETE_BUTTON = "//button[text()='Delete']";
    private static final String DELETE_BUTTON = "div#prompt div.ft span span button";
    private static final String CANCEL_BUTTON = "//button[text()='Cancel']";
    private static final String MESSAGE_LABEL = "//div[@id='prompt']/div[@class='bd']";


    @SuppressWarnings("unchecked")
    @Override
    public DeleteSitePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSitePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Helper method to click on the Delete button
     * 
     * @return HtmlPage
     */
    public HtmlPage clickDelete()
    {
        try
        {
            findAndWait(By.cssSelector(DELETE_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Delete button", e);
        }
        return factoryPage.instantiatePage(driver, DeleteSiteConfirmPage.class);
    }

    /**
     * Helper method to click on the Cancel button
     */
    public HtmlPage clickCancel()
    {
        try
        {
            findAndWait(By.xpath(CANCEL_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Cancel button", e);
        }
        return getCurrentPage();
    }

    /**
     * Get the message from the popup
     * 
     * @return String message
     */
    public String getMessage()
    {
        String message = "";
        try
        {
            message = findAndWait(By.xpath(MESSAGE_LABEL)).getText();
        }
        catch (NoSuchElementException e)
        {
        }
        return message;
    }
}
