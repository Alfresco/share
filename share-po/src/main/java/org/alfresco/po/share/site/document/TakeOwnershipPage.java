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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
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
    public static final String TAKE_OWNERSHIP_BUTTON = "//button[text()='Take Ownership']";

    // Take Ownership popup Cancel button
    public static final String TAKE_OWNERSHIP_CANCEL_BUTTON = "//button[text()='Cancel']";

    public TakeOwnershipPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TakeOwnershipPage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(TAKE_OWNERSHIP_POPUP_TITLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TakeOwnershipPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TakeOwnershipPage render(final long time)
    {
        return render(new RenderTime(time));
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
            return drone.find(TAKE_OWNERSHIP_POPUP_TITLE).getText();
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
            drone.findAndWait(By.xpath(TAKE_OWNERSHIP_BUTTON)).click();
            drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Take Ownership button on Take Ownership popup.", toe);
            }
        }

        return FactorySharePage.resolvePage(drone);
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
            drone.findAndWait(By.xpath(TAKE_OWNERSHIP_CANCEL_BUTTON)).click();
            drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Cancel button on Take Ownership popup.", toe);
            }
        }
        return FactorySharePage.resolvePage(drone);
    }

}
