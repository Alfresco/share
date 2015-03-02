package org.alfresco.po.share.user;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class UserContentItems extends HtmlElement {

	private static final By CONTENT_NAME = By.cssSelector("p a");
	
	private static Log logger = LogFactory.getLog(UserContentItems.class);
	
	/**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param drone
     */
    public UserContentItems(WebElement element, WebDrone drone)
    {
        super(element, drone);
    }
    
    /**
     * Get the content name as displayed on screen.
     * 
     * @return
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
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find content name: " + CONTENT_NAME, e);
        }

        throw new PageOperationException("Unable to find the site name: " + CONTENT_NAME);
    }
    
}
