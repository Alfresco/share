package org.alfresco.po.share.search;



import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * PreView pop up page object 
 * 
 * @author Charu
 * @since 
 */
@SuppressWarnings("unchecked")
public class PreViewPopUpPage extends SharePage
{
	//Constants
	
	private static final By PREVIEW_POPUP = By.cssSelector(".alfresco-dialog-AlfDialog.dijitDialog");	
	private static final By IMG_ON_POPUP = By.cssSelector("div[class$='previewer Image']");	
	private static final By PREVIEW_TEXT = By.cssSelector(".textLayer>div");
	
	@RenderWebElement	
	private static final By PREVIEW_TITLE = By.cssSelector("div[class$='dijitDialogTitleBar']>span[class$=dijitDialogTitle]");	
	private static final By PREVIEW_CLOSE_BUTTON = By.cssSelector("div[class='footer']>span>span>span>span[class='dijitReset dijitInline dijitButtonText']");
	private static final Log logger = LogFactory.getLog(PreViewPopUpPage.class);	
	
	private WebElement closeButton;
	private WebElement previewTitle;
	private WebElement imgOnPopUp;
	private WebElement previewText;	
	
    public PreViewPopUpPage(WebDrone drone)
    {
        super(drone);
    }

    public PreViewPopUpPage render(RenderTime timer)
    {
    	webElementRender(timer);
    	List<WebElement> elements = drone.findAll(PREVIEW_TITLE);
    	for (WebElement webElement : elements) 
    	{
			System.out.println(webElement.isDisplayed());
		}
        return this;
    }      

    
    @Override
    public PreViewPopUpPage render(long time)
    {
        return render(new RenderTime(time));
    }

    
    @Override
    public PreViewPopUpPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if preview popup page is visible,     
     * 
     * @return true if displayed
     */
    public boolean isPreViewPopupPageVisible()
    {        
    	try
        {
            return drone.findFirstDisplayedElement(PREVIEW_POPUP).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
    } 
          
    /**
     * Verify is PreviewPageTitle displayed,
     */
    public boolean isTitlePresent(String name)
    {
        try
        {
        	previewTitle = drone.findFirstDisplayedElement(PREVIEW_TITLE);
        	if(previewTitle.isDisplayed())
        	{
        		if(previewTitle.getText().equals(name));
        		return true;        		       		 
        	}
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
		return false;
        
    }
       
        
    /**
     * Verify is PreviewImage displayed,
     */
    public boolean isPreViewDisplayed()
    {
        try
        {
        	imgOnPopUp = drone.findFirstDisplayedElement(IMG_ON_POPUP);
        	if(imgOnPopUp.isDisplayed())
        	{        		
        		return true;        		       		 
        	}
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
		return false;        
    }
    
    /**
     * Verify is PreviewText displayed,
     */
    public boolean isPreViewTextDisplayed()
    {
        try
        {
        	previewText = drone.findAndWait(PREVIEW_TEXT);
        	if(previewText.isDisplayed())
        	{        		
        		return true;        		       		 
        	}
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
		return false;        
    }
    
    /**
     * Select close button.
     * 
     * @return {@link FacetdSearchPage} page response
     */
    public FacetedSearchPage selectClose()
    {
       	try 
        {
        	closeButton = drone.findFirstDisplayedElement(PREVIEW_CLOSE_BUTTON);
        	if(closeButton.isDisplayed())
        	closeButton.click();
			
		}
        catch (NoSuchElementException nse)
        {
        	if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find close button");
            }
        }
        catch (StaleElementReferenceException e ) 
		{
		    e.printStackTrace();
		}
        
        return new FacetedSearchPage(drone);
    }  
    
    
}