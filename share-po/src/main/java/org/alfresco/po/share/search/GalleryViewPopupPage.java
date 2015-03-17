package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * GalleryViewPopupPage object 
 * 
 * @author Charu
 * @since 
 */
@SuppressWarnings("unchecked")
public class GalleryViewPopupPage extends SharePage
{
	private static final By GALLERY_POPUP_TITLE = By.cssSelector("div[class='dijitDialogTitleBar']>span[class='dijitDialogTitle']");
	private static final By GALLERY_VIEW_CLOSE_BUTTON = By.cssSelector("div[class='dijitDialogTitleBar']>span[class='dijitDialogCloseIcon']");
	private WebElement galleryPopupTitle;
	private WebElement closeButton;
	private static final Log logger = LogFactory.getLog(GalleryViewPopupPage.class);	
	
	public GalleryViewPopupPage(WebDrone drone)
    {
        super(drone);
    }

    public GalleryViewPopupPage render(RenderTime timer)
    {
    	webElementRender(timer);
    	List<WebElement> elements = drone.findAll(GALLERY_POPUP_TITLE);
    	for (WebElement webElement : elements) 
    	{
			System.out.println(webElement.isDisplayed());
		}
        return this;
    }      

    
    @Override
    public GalleryViewPopupPage render(long time)
    {
        return render(new RenderTime(time));
    }

    
    @Override
    public GalleryViewPopupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
	/**
     * Verify is GalleryViewPopupPageTitle displayed,
     */
    public boolean isTitlePresent(String name)
    {
        try
        {
        	galleryPopupTitle = drone.findAndWait(GALLERY_POPUP_TITLE);
        	if(galleryPopupTitle.isDisplayed())
        	{
        		if(galleryPopupTitle.getText().contains(name));
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
        	closeButton = drone.findFirstDisplayedElement(GALLERY_VIEW_CLOSE_BUTTON);
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
