

package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;



/**
 * 
 * Holds details of the site search result in the sites live search dropdown
 * @author jcule
 *
 */
public class LiveSearchSiteResult extends PageElement
{
    private static Log logger = LogFactory.getLog(LiveSearchSiteResult.class);
    private static final String DOCUMENT_SITE_TITLE = "a";
    private WebElement webElement;
    private ShareLink siteName;
    
    
    /**
     * Constructor
     * @param element {@link WebElement} 
     * @param drone WebDrone
     */
    public LiveSearchSiteResult(WebElement element, WebDriver driver, FactoryPage factoryPage)
    {
        webElement = element;
        this.driver = driver;
        this.factoryPage = factoryPage;
    }

    /**
     * Title of search result document item.
     * @return String title
     */
    public ShareLink getSiteName()
    {
        if(siteName == null)
        {
            try
            {
                WebElement siteTitleElement = webElement.findElement(By.cssSelector(DOCUMENT_SITE_TITLE));
                siteName = new ShareLink(siteTitleElement, driver, factoryPage);
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search site result title", e);
            }
        }
        return siteName;
    }
    
  
 
    /**
     * Clicks on document site title on site search result
     */
    public HtmlPage clickOnSiteTitle()
    {
        try
        {
            webElement.findElement(By.cssSelector(DOCUMENT_SITE_TITLE)).click();
            return getCurrentPage();
         
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Document site title element not visible. " + nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find document site title element. " + te);
        }
        throw new PageException("Unable to find document site title element.");
    }
    
 }
