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
package org.alfresco.po.share.dashlet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
@FindBy(css="div[class='dashlet']")
/**
 * Page Object that hold all elements for web quick start dashlet page.
 * 
 * @author Cristina Axinte
 */

public class SiteWebQuickStartDashlet extends AbstractDashlet implements Dashlet
{
    private Log logger = LogFactory.getLog(SiteActivitiesDashlet.class);

    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div[class='dashlet']");
    private static final By LOAD_DATA_SELECTOR = By.cssSelector("select[id$='load-data-options']");
    private static final By IMPORT_BUTTON = By.cssSelector("button[id$='default-load-data-link']");
    private static final By WQS_HELP_LINK = By.cssSelector("div.detail-list-item.last-item>a");
    private static final By IMPORT_MESSAGE = By.xpath(".//span[contains(text(),'Website data import successful')]");
    @SuppressWarnings("unchecked")
    public SiteWebQuickStartDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER), getVisibleRenderElement(DASHLET_TITLE));
        return this;
    }

    /**
     * 
     */
    public void selectWebsiteDataOption(WebQuickStartOptions option)
    {
        if (option == null)
        {
            throw new UnsupportedOperationException("An option value is required");
        }
        try
        {
            Select dataLoadDropDown = new Select(findAndWait(LOAD_DATA_SELECTOR));
            dataLoadDropDown.selectByVisibleText(option.getDescription());
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and select the Website data dropdown.", e);
            }
        }
    }

    /**
     * 
     */
    public void clickImportButtton()
    {
        try
        {
            findAndWait(IMPORT_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Import Button.", e);
            }
        }
    }

    /**
     * Get the selected option applied for site wqs dashlet.
     * 
     * @return String
     */
    public String getSelectedWebsiteData()
    {
        try
        {
            Select websiteDataDropdown = new Select(driver.findElement(LOAD_DATA_SELECTOR));
            return websiteDataDropdown.getFirstSelectedOption().getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate filter elements from the dropdown", e);
        }
    }

    public boolean isWQSHelpLinkDisplayed()
    {
        try
        {
            return driver.findElement(WQS_HELP_LINK).isDisplayed();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Verify the import message
     * 
     * @return boolean
     */
    public boolean isImportMessageDisplayed()
    {
        try
        {

            waitForElement(By.xpath(".//span[contains(text(),'Website data import successful')]"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
            WebElement importMessage = driver.findElement(By.xpath(".//span[contains(text(),'Website data import successful')]"));
            if (importMessage != null)
                return true;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

        return false;
    }
    
    
    /**
     * Wait for message to be displayed and then wait to disappeared; wait for the import to be completed successfully
     * 
     */
    public void waitForImportMessage()
    {

        waitForElement(IMPORT_MESSAGE, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
        waitUntilElementDisappears(IMPORT_MESSAGE, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteWebQuickStartDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}