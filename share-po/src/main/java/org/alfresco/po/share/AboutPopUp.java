package org.alfresco.po.share;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Object associated with PopUp message about alfresco creators, version and revision.
 *
 * @author Aliaksei Boole
 */
public class AboutPopUp extends PageElement
{
    private final static By FORM_XPATH = By.xpath("//div[@id='alfresco-AboutShare-instance-logo']");
    private final static By VERSIONS_DETAILS = By.cssSelector(".about>div:nth-child(2)");

    public AboutPopUp(WebDriver driver)
    {
        WebElement webElement = findAndWait(FORM_XPATH);
        setWrappedElement(webElement);
    }

    /**
     * Return About Logo Url
     *
     * @return String
     */
    public String getLogoUrl()
    {
        return getWrappedElement().getCssValue("background-image").replace("url(\"", "").replace("\")", "");
    }
    
    /**
     * Verify if the version details are displayed
     *
     * @return true if found
     */
    public boolean isVersionsDetailDisplayed()
    {
        return isElementDisplayed(VERSIONS_DETAILS);
    }
    
    /**
     * Get the versions detail
     * 
     * @return String
     */
    public String getVersionsDetail()
    {
        try
        {
            return findAndWait(VERSIONS_DETAILS).getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not able to find Version Details", e);
        }
        catch (TimeoutException te)
        {
            throw new PageException("Exceeded the time to find the Version Details", te);
        }
    }

}
