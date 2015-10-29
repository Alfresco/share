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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Object associated with Hide Get Started Panel popup.
 * 
 * @author jcule
 */
public class HideGetStartedPanel extends SharePage
{

    private static Log logger = LogFactory.getLog(HideGetStartedPanel.class);

    // Hide Get Started Panel Title
    public static final By HIDE_GET_STARTED_PANEL_TITLE = By.cssSelector("#prompt_h");

    // Hide Get Started Panel popup Text
    public static final String HIDE_GET_STARTED_PANEL_TEXT = "You can always show it again later by using the";

    // Hide Get Started Panel popup OK button
    public static final String HIDE_GET_STARTED_OK_BUTTON = "//button[text()='OK']";

    // Hide Get Started Panel popup Cancel button
    @FindBy(xpath = "//button[text()='Cancel']")
    Button cancel;

    /**
     * Get the Hide Get Started Panel text
     * 
     * @return String
     */
    public String getHideGetStatedPanelText()
    {
        try
        {
            // FIXME jelena to fix to use correct css and not text.
            return driver.findElement(By.cssSelector(HIDE_GET_STARTED_PANEL_TEXT)).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able to find Hide Get Started Panel text ", nse);
        }

    }

    /**
     * Clicks on OK button on Hide Get Started Panel popup
     * 
     * @return
     */
    public HtmlPage clickOnHideGetStartedPanelOkButton()
    {
        try
        {
            findAndWait(By.xpath(HIDE_GET_STARTED_OK_BUTTON)).click();
            waitUntilAlert();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find OK button on Hide Get Started Panel.", toe);
            }
        }

        return getCurrentPage();
    }

    /**
     * Clicks on Cancel button on Hide Get Started Panel popup
     * 
     * @return
     */
    public HtmlPage clickOnHideGetStartedPanelCancelButton()
    {
        try
        {
            cancel.click();
            waitUntilAlert();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find OK button on Hide Get Started Panel.", toe);
            }
        }
        return getCurrentPage();
    }

}
