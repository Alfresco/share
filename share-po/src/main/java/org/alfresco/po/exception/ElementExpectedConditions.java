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