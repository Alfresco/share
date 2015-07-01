package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchView
 * This is still not completed since the development is in progress
 * @author Charu
 */
public class FacetedSearchView  
{
    /** Constants. */
    private static final By VIEW_TYPES = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP [data-dojo-attach-point$='textDirNode']");
    private static final By DETAILED_VIEW_RESULTS = By.cssSelector("tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell");
    private static final By GALLERY_VIEW_RESULTS = By.cssSelector("div[class='displayName']");
    private static final String DISPLAY_NAMES = ".displayName";
    private static final By GALLERY_VIEW_ICON = By.cssSelector(".alfresco-renderers-MoreInfo");
    private static Log logger = LogFactory.getLog(FacetedSearchView.class);
    
    private WebDrone drone;
    private String results;
    private WebElement sortOrderButton;
    private WebElement detailedViewResults;
    private WebElement galleryViewResults;    

    /**
     * Instantiates a new faceted search View.
     */
    public FacetedSearchView(WebDrone drone)
    {
        this.drone = drone;
    }

    /**
     * Gets the results.
     *
     * @return the results
     */
    public String getResults()
    {
        return results;
    }

    /**
     * Gets the sort order button.
     *
     * @return the sort order button
     */
    public WebElement getSortOrderButton()
    {
        return sortOrderButton;
    }


       
    /**
     * select view by index by the indexed item in the view menu
     *
     * @param i the index number of the item upon which to select
     * @return the html page
     */
    public HtmlPage selectViewByIndex(int i)
    {
        openMenu();
        boolean found = false;
        List<WebElement> menuElements = drone.findAll(VIEW_TYPES);
        if(menuElements != null && !menuElements.isEmpty())
        {
        	menuElements.get(i).click();
        	found = true;
        }
        
        if(!found)
        {
            cancelMenu();
        }
        return FactorySharePage.resolvePage(this.drone);
    }

    /**
     * Select by label.
     *
     * @param label the label to be sorted on
     * @return the html page
     */
    public HtmlPage selectViewByLabel(String label)
    {
        openMenu();
        boolean found = false;
        List<WebElement> menuElements = drone.findAll(VIEW_TYPES);
        for(WebElement option : menuElements)
        {
        	String value = StringUtils.trim(option.getText());
            if(label.equalsIgnoreCase(value))
            {
                StringUtils.trim(option.getText());
                option.click();
                found = true;
                break;
            }
        }
        if(!found)
        {
            cancelMenu();
        }
        return FactorySharePage.resolvePage(this.drone);
    }
    
    /**
     * Verify is results displayed in detailed view
     */
    public boolean isDetailedViewResultsDisplayed()
    {
        try
        {
        	detailedViewResults = drone.find(DETAILED_VIEW_RESULTS);
        	if(detailedViewResults.isDisplayed())
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
     * Verify is results displayed in Gallery view
     */
    public boolean isGalleryViewResultsDisplayed()
    {
        try
        {
        	galleryViewResults = drone.find(GALLERY_VIEW_RESULTS);
        	if(galleryViewResults.isDisplayed())
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
     * Open the sort menu.
     */
    private void openMenu()
    {
    	drone.find(By.tagName("body")).click();
    	WebElement element = drone.find(By.id("FCTSRCH_VIEWS_MENU"));
    	drone.mouseOver(element);
    	element.click();
    }

    /**
     * Cancel an open menu.
     */
    private void cancelMenu()
    {
    	drone.find(By.cssSelector("div.horizontal-widget")).click();
    }
    
    /**
     * Click on the GalleryIcon by name
     *
     * @param name String
     * @return GalleryViewPopupPage
     */
    public GalleryViewPopupPage clickGalleryIconByName(String name)
    {
        WebDroneUtil.checkMandotaryParam("Name", name);
        
    	try {
        	List<WebElement> displayNames = drone.findAll(By.cssSelector(DISPLAY_NAMES));
			{
			    for(WebElement results : displayNames)
			    {
			        if (results.getText().equalsIgnoreCase(name))
			        {			        	
			        	drone.mouseOver(results);
			            WebElement element = drone.findFirstDisplayedElement(GALLERY_VIEW_ICON);
			            drone.mouseOver(element);
			            element.click();
			        	return new GalleryViewPopupPage(drone);
			        }
			    }
			}
		} 
		catch (TimeoutException e)
        {
            logger.error("Unable to get the name : ", e);
        }

        throw new PageOperationException("Unable to get the name  : ");			
        
    }    
    

}

 