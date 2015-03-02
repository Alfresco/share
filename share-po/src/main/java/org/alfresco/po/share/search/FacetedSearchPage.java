package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * The Class FacetedSearchPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchPage extends SharePage implements SearchResultPage
{

    /** Constants */
    private static final By SEARCH_MENU_BAR = By.id("FCTSRCH_TOP_MENU_BAR");
    private static final By RESULT = By.cssSelector("tr.alfresco-search-AlfSearchResult");
    private static final By CONFIGURE_SEARCH = By.cssSelector("div[id=FCTSRCH_CONFIG_PAGE_LINK]");    
    private static final Log logger = LogFactory.getLog(FacetedSearchPage.class);
    private static final String goToAdvancedSearch = "span#HEADER_ADVANCED_SEARCH_text";
    private static final By SEARCH_RESULTS_LIST = By.cssSelector("div[id='FCTSRCH_SEARCH_RESULTS_LIST']");

    /**
     * Instantiates a new faceted search page.
     * 
     * @param drone WebDriver browser client
     */
    public FacetedSearchPage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public FacetedSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public FacetedSearchPage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public FacetedSearchPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                
                if (drone.find(SEARCH_MENU_BAR).isDisplayed() && drone.find(SEARCH_RESULTS_LIST).isDisplayed())
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    } 
       

    /**
     * Gets the search form.
     * 
     * @return {@link FacetedSearchForm}
     */
    public FacetedSearchForm getSearchForm()
    {
        return new FacetedSearchForm(drone);
    }

    /**
     * Gets the sort.
     * 
     * @return {@link FacetedSearchSort}
     */
    public FacetedSearchSort getSort()
    {
        return new FacetedSearchSort(drone);
    }


    /**
     * Gets the results.
     * 
     * @return List<{@link FacetedSearchResult}>
     */
    public List<SearchResult> getResults()
    {
    	List<SearchResult> results = new ArrayList<SearchResult>();
        List<WebElement> response = drone.findAll(RESULT);
        for (WebElement result : response)
        {
        	results.add(new FacetedSearchResult(drone, result));
        }
        return results;
    }

    /**
     * Gets a result by its title if it exists.
     *
     * @param title
     * @return the result
     */
	public SearchResult getResultByTitle(String title) 
	{
		try
		{
			for (SearchResult facetedSearchResult : getResults())
			{
				if (facetedSearchResult.getTitle().equals(title)) 
				{
					return facetedSearchResult;
				}
			}
		} 
		catch (TimeoutException e)
		{
			logger.error("Unable to get the title : ", e);
		}

		throw new PageOperationException("Unable to get the title  : ");

	}
	/**
	 * Gets the view.
	 * ^M
	 * @return {@link FacetedSearchView}
	 */
	public FacetedSearchView getView()
	{
		return new FacetedSearchView(drone);
	}
    /**
     * Gets a result by its name if it exists.
     *
     * @param name
     * @return the result
     */
    public SearchResult getResultByName(String name)
    {
        try {
			for(SearchResult facetedSearchResult : getResults())
			{
			    if(facetedSearchResult.getName().equals(name))
			    {
			        return facetedSearchResult;
			    }
			}
		} 
		catch (TimeoutException e)
        {
            logger.error("Unable to get the name : ", e);
        }

        throw new PageOperationException("Unable to get the name  : ");			
        
    }

    /**
     * Scroll to page bottom.
     */
    public void scrollSome(int distance)
    {
        this.drone.executeJavaScript("window.scrollTo(0," + distance + ");", "");
    }

    /**
     * Scroll to page bottom.
     */
    public void scrollToPageBottom()
    {
        this.drone.executeJavaScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
    }

    /**
     * Gets the current url hash.
     *
     * @return the url hash
     */
    public String getUrlHash()
    {
        String url = this.drone.getCurrentUrl();

        // Empty url or no #
        if(StringUtils.isEmpty(url) || !StringUtils.contains(url, "#"))
        {
            return null;
        }

        return StringUtils.substringAfter(url, "#");
    }

    
    /**
     * Get the numeric value display on top of search results.
     * The number indicates the total count found for given search.
     * @return
     */
    public int getResultCount()
    {
        String val = drone.find(By.cssSelector("#FCTSRCH_RESULTS_MENU_BAR>div>div>div>span>b")).getText();
        return Integer.valueOf(val).intValue();
    }
    /**
     * Select the facet that matches the title from the facet grouping. 
     * @param title facet name, eg Microsoft Word
     * @return FacetedSearchPage with filtered results
     */
    public FacetedSearchPage selectFacet(final String title)
    {
        WebDroneUtil.checkMandotaryParam("Facet title", title);
        WebElement facet = drone.find(By.xpath(String.format("//span[@class = 'filterLabel'][contains(., '%s')]",title)));
        facet.click();
        return this;
    }
    
    /**
     * Click on configure search Link.     *
     * @return the FacetedSearchConfigpage
     */
    public FacetedSearchConfigPage clickConfigureSearchLink()
    {
        try {
			WebElement configure_search = drone.find(CONFIGURE_SEARCH);
			if (configure_search.isDisplayed())
			{
			    configure_search.click();        
			}
			return new FacetedSearchConfigPage(drone);
		} catch (TimeoutException e)
        {
            logger.error("Unable to find the link : " + e.getMessage());
        }

        throw new PageException("Unable to find the link : " );
    }   
         
    /**
     * verify configureSearchlink is displayed
     * @param driver
     * @return Boolean
     */
    public Boolean isConfigureSearchDisplayed(WebDrone driver)
    {
        try
        {
            WebElement configure_search = drone.find(CONFIGURE_SEARCH);
            if (configure_search.isDisplayed())
            {
                return true;
            }

        }

        catch (NoSuchElementException te)
        {
        	if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find configure search link");
            }
        }
        return false;
    }
    
    @Override
    public boolean hasResults()
    {
        return !getResults().isEmpty();
    }

    @Override
    public HtmlPage selectItem(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Search row name is required");
        }
        try
        {
            String selector = String.format("//span[@class = 'value'][contains(., '%s')]", name);
            drone.find(By.xpath(selector)).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %s item not found", name), e);
        }
        
        return FactorySharePage.getUnknownPage(drone);
    }

    @Override
    public HtmlPage selectItem(int number)
    {
        if (number < 0)
        {
            throw new IllegalArgumentException("Value can not be negative");
        }
        number += 1;
        try
        {
            String selector = String.format("tr.alfresco-search-AlfSearchResult:nth-of-type(%d) a", number);
            WebElement row = drone.find(By.cssSelector(selector));
            row.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %d item not found", number), e);
        }

        return FactorySharePage.getUnknownPage(drone);
    }
    
    public boolean isItemPresentInResultsList(SitePageType pageType, String itemName)
    {
        String thumbnailImg;
        boolean isPresent = false;
        List<SearchResult> searchResults = getResults();

        for (SearchResult result : searchResults)
        {
            if(result.getName().equals(itemName))
            {
                switch (pageType)
                {
                    case WIKI:
                        thumbnailImg = "wiki-page.png";
                        break;
                    case BLOG:
                        thumbnailImg = "topic-post.png";
                        break;
                    case CALENDER:
                        thumbnailImg = "calendar-event.png";
                        break;
                    case DATA_LISTS:
                        thumbnailImg = "datalist.png";
                        break;
                    case DISCUSSIONS:
                        thumbnailImg = "topic-post.png";
                        break;
                    case LINKS:
                        thumbnailImg = "link.png";
                        break;
                    default:
                        thumbnailImg = "doclib";
                        break;
                }

                isPresent = result.getThumbnail().contains(thumbnailImg);
                if(isPresent)
                    break;
            }
        }
        return isPresent;
    }

    public boolean isPageCorrect()
    {
        boolean isCorrect;
        isCorrect = drone.isElementDisplayed(getSearchForm().SEARCH_FIELD) && drone.isElementDisplayed(getSearchForm().SEARCH_BUTTON) && drone.isElementDisplayed(RESULT)
                && getSort().isSortCorrect();
        return isCorrect;
    }

    public boolean isGoToAdvancedSearchPresent()
    {
        return drone.isElementDisplayed(By.cssSelector(goToAdvancedSearch));
    }

	public List<FacetedSearchFacetGroup> getFacetGroups() 
	{
		List<WebElement> facetGroups = drone.findAll(By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters:not(.hidden)"));
		List<FacetedSearchFacetGroup>filters = new ArrayList<FacetedSearchFacetGroup>();
		for (WebElement facetGroup : facetGroups)
		{
        	filters.add(new FacetedSearchFacetGroup(drone, facetGroup));
        }
		return filters;
	}

	public FacetedSearchScopeMenu getScopeMenu() 
	{
		return new FacetedSearchScopeMenu(drone);
	}


}