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
package org.alfresco.po.exception;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Canned {@link ExpectedCondition}s which are generally useful within webdrone test.
 *
 * @author Shan Nagarajan
 * @since  1.7.1
 */
public final class ElementExpectedConditions
{
    private ElementExpectedConditions() {}
    /**
     * An expectation for checking that an element with text is either invisible
     * or not present on the DOM.
     * 
     * @param driver {@link WebDriver}'s object
     * @param locator used to find the element
     * @param text of the element
     * @return {@link ExpectedCondition} true if met
     */
    public static ExpectedCondition<Boolean> invisibilityOfElementWithPartialText(WebDriver driver, final By locator, final String text)
    {
        if(driver == null) 
        {
            throw new IllegalArgumentException("WebDriver is required");
        }
        if(locator == null)
        { 
            throw new IllegalArgumentException("By locator is required");
        }
        if(text == null)
        {
            throw new IllegalArgumentException("Text value is required");
        } 
        return new ExpectedCondition<Boolean>()
        {
            @Override
            public Boolean apply(WebDriver driver)
            {
                if(driver == null) { throw new IllegalArgumentException("Webdrive is required");}
                try
                {
                    return !driver.findElement(locator).getText().contains(text);
                }
                // Returns true because the element is not present in DOM.
                catch (NoSuchElementException e){ return true; }
                // Returns true because stale element reference implies that element  is no longer visible.
                catch (StaleElementReferenceException e){ return true; }
            }
            @Override
            public String toString(){ return String.format("element containing '%s' to no longer be visible: %s", text, locator);}
        };
    }
}