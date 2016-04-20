package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.FactoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This is object to carry Search Row.
 * 
 * @author nshah
 */
public class UserSearchRow extends PageElement implements SearchRow
{
    private String userName;
    private WebElement element;
    private By BUTTON_ADD = By.cssSelector("span[class$='button'] span button");
    private By USER_NAME = By.cssSelector("td[class$='fullName'] div h3");

    UserSearchRow(WebDriver driver, WebElement element, FactoryPage factoryPage)
    {
        this.userName = element.findElement(USER_NAME).getText();
        this.driver = driver;
        this.element = element;
        this.factoryPage = factoryPage;
    }

    @Override
    public HtmlPage clickAdd()
    {
        try
        {
            element.findElement(BUTTON_ADD).click();
            return getCurrentPage();

        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("ADD button is not present in the element", nse);
        }

    }

    @Override
    public HtmlPage clickUser()
    {
        try
        {
            if ("EVERYONE".equals(this.userName))
            {
                throw new UnsupportedOperationException("EVERYONE doesnt have profiles");
            }
            else
            {
                element.findElement(USER_NAME).click();

            }
        }
        catch (UnsupportedOperationException use)
        {
            // catch and let go.
        }
        return getCurrentPage();

    }

    @Override
    public String getUserName()
    {
        return this.userName;
    }

}
