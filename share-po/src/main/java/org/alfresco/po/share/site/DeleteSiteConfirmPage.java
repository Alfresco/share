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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Delete site popup - confirmation page comes when you delete button on {@link DeleteSitePage}
 * 
 * @author Bogdan Bocancea
 */

public class DeleteSiteConfirmPage extends SharePage
{

    //private static final String YES_BUTTON = "//button[text()='Yes']";
    private static final String YES_BUTTON = "div#prompt div.ft span span button";
    private static final String NO_BUTTON = "//button[text()='No']";
    private static final String MESSAGE_LABEL = "//div[@id='prompt']/div[@class='bd']";


    @SuppressWarnings("unchecked")
    @Override
    public DeleteSiteConfirmPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSiteConfirmPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Helper method to click on the Delete button
     */
    public HtmlPage clickYes()
    {
        try
        {
            findAndWait(By.cssSelector(YES_BUTTON)).click();
            waitUntilVisible(By.xpath(".//*[@id='message']/div/span"), "Site was deleted", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            waitUntilNotVisibleWithParitalText(By.xpath(".//*[@id='message']/div/span"), "Site was deleted", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Yes button", e);
        }
        return getCurrentPage();
    }

    /**
     * Helper method to click on the Delete button
     */
    public HtmlPage clickNo()
    {
        try
        {
            findAndWait(By.xpath(NO_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the No button", e);
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
