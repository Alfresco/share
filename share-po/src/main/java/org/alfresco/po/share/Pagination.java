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
