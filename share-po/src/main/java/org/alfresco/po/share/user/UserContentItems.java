package org.alfresco.po.share.user;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.FactoryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserContentItems extends PageElement 
{

	private static final By CONTENT_NAME = By.cssSelector("p a");
	
	private static Log logger = LogFactory.getLog(UserContentItems.class);
	
	/**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param driver
     */
    public UserContentItems(WebElement element, WebDriver driver, FactoryPage factoryPage)
    {
        setWrappedElement(element);
        this.factoryPage = factoryPage;
    }
    
    /**
     * Get the content name as displayed on screen.
     * 
     * @return String
     */
    public String getContentName()
    {
        try
        {
            return findAndWait(CONTENT_NAME).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find content name: " + CONTENT_NAME, e);
        }

        throw new PageOperationException("Unable to find the content name: " + CONTENT_NAME);
    }
    
    /**
     * Click on the Content name.
     * 
     * @return Page
     */
    public HtmlPage clickOnContentName()
    {
        try
        {
            findAndWait(CONTENT_NAME).click();
            domEventCompleted();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find content name: " + CONTENT_NAME, e);
        }

        throw new PageOperationException("Unable to find the site name: " + CONTENT_NAME);
    }
    
}
