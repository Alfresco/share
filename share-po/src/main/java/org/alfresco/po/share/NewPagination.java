/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Pagination object, holds common methods applied to pagination
 * in Alfresco share.
 * 
 * @author Subashni Prasanna
 */
public class NewPagination
{
    private static final String PAGINATOR = "div[id*='paginator']";

    private NewPagination()
    {
    };

    /**
     * Checks for next or previous pagination button.
     * and verify if click able.
     * 
     * @param drone {@link WebDrone}
     * @param css the css to find the button
     * @return true if clickable.
     */
    public static boolean hasPaginationButton(WebDrone drone, final String css)
    {
        try
        {
            WebElement element = drone.find(By.cssSelector(PAGINATOR));
            WebElement nextPage = element.findElement(By.cssSelector(css));
            String nextPageIsPresent = nextPage.getAttribute("disabled");
            if (nextPageIsPresent == null || nextPageIsPresent.isEmpty())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Selects the next or previous button on the pagination
     * bar based on the action required.
     * 
     * @param drone {@link WebDrone}
     * @param css that identifies which button to select
     * @return Search result page
     * @throws PageException
     */
    public static HtmlPage selectPaginationButton(WebDrone drone, final String css) throws PageException
    {
        try
        {
            WebElement paginator = drone.find(By.cssSelector(PAGINATOR));
            WebElement button = paginator.findElement(By.cssSelector(css));
            String nextPageIsPresent = button.getAttribute("disabled");
            if (nextPageIsPresent == null || nextPageIsPresent.isEmpty())
            {
                button.click();
            }
            else
            {
                throw new PageException("The pagination buttons are disabled");
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException te)
        {
        }
        return FactorySharePage.resolvePage(drone);
    }
}
