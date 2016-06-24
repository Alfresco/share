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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Pagination object, holds common methods applied to pagination
 * in Alfresco share.
 * 
 * @author Michael Suzuki
 */
public class Pagination extends PageElement
{
    private static final String PAGINATOR = "div[id*='paginator']";

    /**
     * Checks for next or previous pagination button.
     * and verify if click able.
     * 
     * @param driver {@link WebDriver}
     * @param css the css to find the button
     * @return true if clickable.
     */
    public static boolean hasPaginationButton(WebDriver driver, final String css)
    {
        try
        {
            WebElement element = driver.findElement(By.cssSelector(PAGINATOR));
            WebElement nextPage = element.findElement(By.cssSelector(css));
            return nextPage.isDisplayed();
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
     * @param driver {@link WebDriver}
     * @param css that identifies which button to select
     * @return Search result page
     */
    public HtmlPage selectPaginationButton(WebDriver driver, final String css)
    {
        try
        {
            WebElement paginator = driver.findElement(By.cssSelector(PAGINATOR));
            WebElement button = paginator.findElement(By.cssSelector(css));
            button.click();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException te)
        {
        }
        return getCurrentPage();
    }
}
