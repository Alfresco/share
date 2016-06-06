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
package org.alfresco.po.alfresco;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represents the repository admin console page found on alfresco.
 * @author Michael Suzuki
 * @since 5.0
 */
public class RepositoryAdminConsolePage extends AbstractAdminConsole
{

    private final static By INPUT_FIELD = By.name("repo-cmd"); 

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        while (true)
        {
            timer.start();
            try
            {
                if (driver.findElement(INPUT_FIELD).isDisplayed() && driver.findElement(SUBMIT_BUTTON).isDisplayed())
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
    public RepositoryAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Populates the input with command and
     * submits the form.
     * @param command String command
     */
    public void sendCommands(final String command)
    {
        WebElement input = driver.findElement(INPUT_FIELD);
        input.clear();
        input.sendKeys(command);
        driver.findElement(SUBMIT_BUTTON).click();
    }
}
