/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.alfresco;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represents the tenant admin page found on alfresco.
 * @author Michael Suzuki
 * @since 5.0
 */
public class TenantAdminConsolePage extends AbstractAdminConsole
{

    private final static By INPUT_FIELD = By.name("tenant-cmd"); 
    public TenantAdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        while (true)
        {
            timer.start();
            try
            {
                if (drone.find(INPUT_FIELD).isDisplayed() && drone.find(SUBMIT_BUTTON).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse){ }
            finally
            {
                timer.end();
            }
        }
        return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render(long time)
    {
        return render(new RenderTime(time));
    }
    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render()
    {
        return render(maxPageLoadingTime);
    }
    /**
     * Populates the input with the create tenant command and
     * submits the form.
     * @param tenantName String tenant name 
     * @param password String tenant password
     */
    public HtmlPage createTenant(String tenantName, String password)
    {
        WebElement input = drone.find(INPUT_FIELD);
        input.clear();
        input.sendKeys(String.format("create %s %s", tenantName, password));
        drone.find(SUBMIT_BUTTON).click();
        return this;
    }
    /**
     * Method for sending string to command input field.
     *
     * @param command String to pass.
     */
    public void sendCommands(final String command)
    {
        WebDroneUtil.checkMandotaryParam("command input", command);
        WebElement input = drone.find(INPUT_FIELD);
        input.clear();
        input.sendKeys(String.format("%s", command));
        drone.find(SUBMIT_BUTTON).click();
    }
}
