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
package org.alfresco.po;

import static org.alfresco.po.ElementState.INVISIBLE_WITH_TEXT;
import static org.alfresco.po.ElementState.VISIBLE;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Renderable element with Locator and Element State with optional text.
 * @author Shan Nagarajan
 * @since  1.7.0
 */
public class RenderElement
{
    private static final String LOCATOR_REQUIRED_ERR_MSG = "A locator is required";
    private By locator;
    private ElementState elementState;
    
    public RenderElement(By locator, ElementState elementState)
    {
        if(null == locator)
        {
            throw new IllegalArgumentException("Locator can't be null");
        }
        if(null == elementState)
        {
            throw new IllegalArgumentException("Element State can't be null");
        }
        if(INVISIBLE_WITH_TEXT.equals(elementState))
        {
           throw new IllegalArgumentException("Please use other constructor with text"); 
        }
        this.locator = locator;
        this.elementState = elementState;
    }
    
    /**
     * Render the element based the {@link ElementState}.
     * @param driver - {@link Webdriver}
     * @param timeOutInSeconds duration
     */
    public void render(WebDriver driver, long timeOutInSeconds)
    {
        switch (elementState)
        {
            case VISIBLE:
                waitForElement(driver, locator, timeOutInSeconds);
                break;
            case CLICKABLE:
                waitUntilElementClickable(driver, locator, timeOutInSeconds);
                break;
            case INVISIBLE:
                waitUntilElementDisappears(driver, locator, timeOutInSeconds);
                break;
            case PRESENT:
                waitUntilElementPresent(driver, locator, timeOutInSeconds);
                break;
            default:
                throw new IllegalArgumentException(elementState + "is not defined in the Render Element, please add supported opertaion.");
        }
    }

    public By getLocator()
    {
        return locator;
    }


    public ElementState getElementState()
    {
        return elementState;
    }
    
    /**
     * Returns the Visible {@link RenderElement}.
     * @param locator {@link By} locator type
     * @return {@link RenderElement} element render check
     */
    public static RenderElement getVisibleRenderElement(By locator)
    {
        return new RenderElement(locator, VISIBLE);
    }
    /**
     * Wait until the element is visible for the specified amount of time.
     * @param driver WebDriver
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitForElement(WebDriver driver, By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    /**
     * Wait until the Clickable of given Element for given seconds.
     * 
     * @param driver WebDriver
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementClickable(WebDriver driver, By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    /**
     * Wait until the invisibility of given Element for given seconds.
     * @param driver WebDriver
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementDisappears(WebDriver driver, By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    /**
     * Wait until the invisibility of given Element for given seconds.
     * 
     * @param driver WebDriver
     * @param locator CSS Locator
     * @param text - The Text to find in the Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilNotVisible(WebDriver driver, By locator, String text, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        if(text == null || text.isEmpty())
        {
            throw new IllegalArgumentException("Text value is required");
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }
    /**
     * Wait until the visibility of given Element for given seconds.
     * 
     * @param driver WebDriver
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementPresent(WebDriver driver, By locator, long timeOutInSeconds)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
}
