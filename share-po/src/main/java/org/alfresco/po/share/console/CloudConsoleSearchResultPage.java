package org.alfresco.po.share.console;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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
public class CloudConsoleSearchResultPage extends CloudConsolePage
{
    private static final By AUDIT_LOG_LINK = By.cssSelector("li>a[href*='audit']");
    private static final By PERSON_DETAILS_LINK = By.cssSelector("li>a[href*='accounts']");

    public CloudConsoleSearchResultPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleSearchResultPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            if (hasNoResult())
            {
                break;
            }
            else if (isVisibleResults())
            {
                break;
            }
            timer.end();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleSearchResultPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleSearchResultPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Checks if results table is displayed
     * 
     * @return true if visible
     */
    public boolean isVisibleResults()
    {
        try
        {
            if (drone.findDisplayedElements(AUDIT_LOG_LINK).size()>0)
            {
                return drone.find(AUDIT_LOG_LINK).isDisplayed();
            }
            else
            {
               return drone.find(PERSON_DETAILS_LINK).isDisplayed();
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    private boolean hasNoResult()
    {
        boolean noResults;
        try
        {
            // Search for no data message
            WebElement message = drone.find(By.cssSelector("div[class*='messages warning']"));
            noResults = message.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            noResults = false;
        }
        return noResults;
    }

}
