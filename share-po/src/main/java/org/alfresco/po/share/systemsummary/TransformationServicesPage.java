/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */

package org.alfresco.po.share.systemsummary;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by olga.lokhach
 */
public class TransformationServicesPage extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CANCEL_BUTTON = By.xpath("//input[@value='Cancel']");

    //Office Transform - JODConverter
    @RenderWebElement
    private final static By JODCONVERTER_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='jodconverter.enabled']");
    @RenderWebElement
    private final static By JODCONVERTER_PORT_INPUT = By.cssSelector("input[name$='jodconverter.portNumbers']");

    public TransformationServicesPage (WebDrone drone)
    {
        super(drone);
    }
    @SuppressWarnings("unchecked")
    @Override
    public synchronized TransformationServicesPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TransformationServicesPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TransformationServicesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }
    /**
     * Method to enable or disable the FTP server
     */

    public TransformationServicesPage selectJODConverterEnabledCheckbox()
    {
        drone.findAndWait(JODCONVERTER_ENABLED_CHECKBOX).click();
        click(SAVE_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Is check box selected
     *
     * @return - Boolean
     */
    public boolean isJODConverterEnabledSelected()
    {
        try
        {
            return (drone.find(JODCONVERTER_ENABLED_CHECKBOX).isSelected());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}
