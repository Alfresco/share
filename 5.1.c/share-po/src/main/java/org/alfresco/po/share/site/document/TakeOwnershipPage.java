/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Object associated with Take Ownership popup.
 * 
 * @author jcule
 */
public class TakeOwnershipPage extends SharePage
{
    private static Log logger = LogFactory.getLog(TakeOwnershipPage.class);

    // Take ownership popup title
    public static final By TAKE_OWNERSHIP_POPUP_TITLE = By.cssSelector("#prompt_h");

    // Take ownership popup Take Ownership button
    public static final String TAKE_OWNERSHIP_BUTTON = "//button[text()='OK']";

    // Take Ownership popup Cancel button
    public static final String TAKE_OWNERSHIP_CANCEL_BUTTON = "//button[text()='Cancel']";


    @SuppressWarnings("unchecked")
	@Override
    public TakeOwnershipPage render()
    {
    	RenderTime timer = new RenderTime(maxPageLoadingTime);
        elementRender(timer, RenderElement.getVisibleRenderElement(TAKE_OWNERSHIP_POPUP_TITLE));
        return this;
    }


    /**
     * Get the Take Ownership popup title
     * 
     * @return String
     */
    public String getTakeOwnershipPopupTitle()
    {
        try
        {
            return driver.findElement(TAKE_OWNERSHIP_POPUP_TITLE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able to find Take Ownership popup title ", nse);
        }

    }

    /**
     * Clicks on Take Ownership button on Take Ownership popup
     * 
     * @return
     */
    public HtmlPage clickOnTakeOwnershipButton()
    {
        try
        {
            findAndWait(By.xpath(TAKE_OWNERSHIP_BUTTON)).click();
            waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Take Ownership button on Take Ownership popup.", toe);
            }
        }

        return getCurrentPage();
    }

    /**
     * Clicks on Cancel button on Take Ownership popup
     * 
     * @return
     */
    public HtmlPage clickOnTakeOwnershipCancelButton()
    {
        try
        {
        	findAndWait(By.xpath(TAKE_OWNERSHIP_CANCEL_BUTTON)).click();
            waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Cancel button on Take Ownership popup.", toe);
            }
        }
        return getCurrentPage();
    }

}
