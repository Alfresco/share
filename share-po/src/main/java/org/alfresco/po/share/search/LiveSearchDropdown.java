/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.po.share.search;


import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Interactions with live search dropdown
 * 
 * @author jcule
 * @author adinap
 * @author mbhave
 */
public class LiveSearchDropdown extends SharePage
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdown.class);

    // Live Search drop down
    private static final String LIVE_SEARCH_DROPDOWN = ".alf-livesearch[style='display: block;']";

    // Search scope Repository
    private static final String SCOPE_REPOSITORY = ".alf-livesearch-context__repo";

    // Search scope Site
    private static final String SCOPE_SITE = ".alf-livesearch-context__site";
    
    // Site Scope: Visibility
    private static final String SITE_VISIBILITY = "#HEADER_TITLE_VISIBILITY";

    // Documents Title
    private static final String DOCUMENTS_TITLE = ".alf-live-search-documents-title[style='display: block;']";

    // Sites Title
    private static final String SITES_TITLE = ".alf-live-search-sites-title[style='display: block;']";

    // People Title
    private static final String PEOPLE_TITLE = ".alf-live-search-people-title[style='display: block;']";

    // Close button
    private static final String CLOSE_DROPDOWN = ".alfresco-header-SearchBox-clear a";

    // Document results
    private static final String DOCUMENT_RESULTS = "div[data-dojo-attach-point='containerNodeDocs'] div.alf-livesearch-item";

    // Sites Results
    private static final String SITES_RESULTS = "div[data-dojo-attach-point='containerNodeSites'] div.alf-livesearch-item";

    // People Results
    private static final String PEOPLE_RESULTS = "div[data-dojo-attach-point='containerNodePeople'] div.alf-livesearch-item";

    // See more document results
    private static final String MORE_RESULTS = "a[title='More'] span";
    
    public enum Scope {SITE, REPO, DEFAULT};
    
    public enum ResultType {PEOPLE, SITE, DOCUMENT};
     

    @SuppressWarnings("unchecked")
    @Override
    public LiveSearchDropdown render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LiveSearchDropdown render(RenderTime timer)
    {
    	if (isUserWithinSiteContext() && isLiveSearchDropdownVisible())
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(By.cssSelector(SCOPE_REPOSITORY)),
    				 RenderElement.getVisibleRenderElement(By.cssSelector(SCOPE_SITE)));
    	    return this;
        }
    	else
    	{
    		elementRender(timer, new RenderElement(By.cssSelector(SCOPE_REPOSITORY), ElementState.PRESENT), 
    				new RenderElement(By.cssSelector(SCOPE_SITE), ElementState.PRESENT));
    	}
    	return this;    	
    }
    
    /**
     * Check is User is within Site Context, default is false, when repo context
     *
     * @return if displayed
     */
    public boolean isUserWithinSiteContext()
    {
        try
        {
            boolean displayed = driver.findElement(By.cssSelector(SITE_VISIBILITY)).isDisplayed();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("** Site Scope: %s", displayed));
            }
            return displayed;
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if live search dropdown is displayed
     *
     * @return boolean
     */
    public boolean isLiveSearchDropdownVisible()
    {
        try
        {
            WebElement liveSearchDropDown = driver.findElement(By.cssSelector(LIVE_SEARCH_DROPDOWN));
            boolean displayed = liveSearchDropDown.isDisplayed();
            
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("** Live Search dropdown: %s", displayed));
            }
            return displayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search dropdown ", nse);
        }
        return false;
    }

    /**
     * Gets the Search Site scope option name
     *
     * @return String
     */
    public String getScopeSiteName()
    {
        try
        {
            String siteName = driver.findElement(By.cssSelector(SCOPE_SITE)).getText();
            if (siteName.lastIndexOf(' ') >= 0)
            {
                siteName = siteName.substring(siteName.lastIndexOf(' ') + 1).replaceAll("\'", "");
            }
            return siteName;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Search Site scope option", nse);
            throw new PageException("Unable to find live search documents title.", nse);
        }
    }

    /**
     * Gets the search results as a collection of LiveSearchDocumentResult.
     * 
     * @return Collections of search result
     */
    public List<LiveSearchDocumentResult> getSearchDocumentResults()
    {
        
        List<LiveSearchDocumentResult> results = new ArrayList<LiveSearchDocumentResult>();
        try
        {

            List<WebElement> elements = findAndWaitForElements(By.cssSelector(DOCUMENT_RESULTS));           
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchDocumentResult(element, driver, factoryPage));
                }
            }

        }
        catch (TimeoutException toe)
        {
            logger.error("No live search document results ", toe);
        }
        return results;

    }

    /**
     * Gets the sites search results as a collection of LiveSearchSiteResult.
     * 
     * @return Collections of search result
     */
    public List<LiveSearchSiteResult> getSearchSitesResults()
    {
        List<LiveSearchSiteResult> results = new ArrayList<LiveSearchSiteResult>();
        try
        {
            List<WebElement> elements = findAndWaitForElements(By.cssSelector(SITES_RESULTS));
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchSiteResult(element, driver, factoryPage));
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("No live search sites results ", toe);
        }
        return results;
    }

    /**
     * Gets the people search results as a collection of .
     * 
     * @return Collections of search result
     */
    public List<LiveSearchPeopleResult> getSearchPeopleResults()
    {
        List<LiveSearchPeopleResult> results = new ArrayList<LiveSearchPeopleResult>();
        try
        {
            List<WebElement> elements = findAndWaitForElements(By.cssSelector(PEOPLE_RESULTS));
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchPeopleResult(element, driver, factoryPage));
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("No live search people results ", toe);
        }
        return results;
    }

    /**
     * clicks on close button
     */
    public HtmlPage closeLiveSearchDropdown()
    {
        try
        {
            WebElement closeDropdown = findAndWait(By.cssSelector(CLOSE_DROPDOWN));
            closeDropdown.click();
            return factoryPage.getPage(driver);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Close live search dropdown button not present ", nse);
            throw new PageException("Unable to find close live search dropdown button.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Close live search dropdown button not present ", te);
            throw new PageException("Close live search dropdown button is not visible", te);
        }

    }

    /**
     * Checks if documents title is displayed
     * 
     * @return boolean
     */
    public boolean isDocumentsTitleVisible()
    {
        try
        {
            WebElement title = driver.findElement(By.cssSelector(DOCUMENTS_TITLE));
            return title.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search documents title ", nse);
            return false;
        }
    }

    /**
     * Checks if sites title is displayed
     * 
     * @return boolean
     */
    public boolean isSitesTitleVisible()
    {
        try
        {
            WebElement title = driver.findElement(By.cssSelector(SITES_TITLE));
            return title.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search sites title ", nse);
        }
        return false;
    }

    /**
     * Checks if people title is displayed
     * 
     * @return boolean
     */
    public boolean isPeopleTitleVisible()
    {
        try
        {
            WebElement title = driver.findElement(By.cssSelector(PEOPLE_TITLE));
            return title.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search people title ", nse);
            return false;
        }
    }

    /**
     * Checks if more icon is displayed
     * 
     * @return boolean
     */
    public boolean isMoreResultsVisible()
    {
        try
        {
            WebElement more = driver.findElement(By.cssSelector(MORE_RESULTS));
            return more.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No more results icon ", nse);
            return false;
        }
    }

    /**
     * Checks if scope Repository is displayed
     *
     * @return boolean
     */
    public boolean isScopeRepositoryVisible()
    {
        try
        {
            WebElement scopeRepo = driver.findElement(By.cssSelector(SCOPE_REPOSITORY));
            return scopeRepo.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search scope repository option ", nse);
            return false;
        }
    }

    /**
     * Checks if scope Site is displayed
     *
     * @return boolean
     */
    public boolean isScopeSiteVisible()
    {
        try
        {
            WebElement scopeSite = driver.findElement(By.cssSelector(SCOPE_SITE));
            return scopeSite.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search scope site option ", nse);
            return false;
        }
    }

    /**
     * Clicks on see more results arrow
     * 
     */
    public HtmlPage clickToSeeMoreDocumentResults()
    {
        try
        {
            WebElement expandDocumentResults = findAndWait(By.cssSelector(MORE_RESULTS));
            mouseOver(expandDocumentResults);
            expandDocumentResults.click();
            return this.render();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No see more results icon ", nse);
            throw new PageException("Unable to find see more results icon.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find see more results icon. ", te);
            throw new PageException("Unable to find see more results icon. ", te);
        }

    }

    /**
     * Click on Search scope option
     *
     * @param searchScope Scope
     * @return LiveSearchDropdown
     */
    public LiveSearchDropdown selectScope(Scope searchScope)
    { 
    	String scopeSelector = SCOPE_REPOSITORY;
    	
    	if (searchScope == Scope.SITE)
    	{
    		scopeSelector = SCOPE_SITE;
    	}
    	
    	if (searchScope != Scope.DEFAULT)
    	{
	        try
	        {
	            WebElement scopeRepository = driver.findElement(By.cssSelector(scopeSelector));
	            mouseOver(scopeRepository);
	            scopeRepository.click();   
	        }
	        catch (NoSuchElementException nse)
	        {
	            logger.error("Required scope could not be selected", nse);
	            throw new PageException("Unable to find Search Alfresco option.", nse);
	        }
    	}
    	return this.render();
    }


    /**
    * method to find out if the expected liveSearchItem is listed on the LiveSearchDropDown Page
    * @param liveSearchItem LiveSearchResultItem
    * @return true if item is found
    */
    public boolean isItemListed(LiveSearchResultItem liveSearchItem)
    {
        boolean nodeFound = false;
        
        if (liveSearchItem.getResultType() == ResultType.DOCUMENT)
        {
        	List<LiveSearchDocumentResult> liveSearchResultsDocs = getSearchDocumentResults();
        	
        	for (LiveSearchDocumentResult result : liveSearchResultsDocs)
	        {
	            if(result.getTitle().getDescription().contains(liveSearchItem.getResultItemName()))
	            {
	            	// Check if SiteName matches
	            	if (liveSearchItem.getSiteName().isEmpty() || result.getSiteName().getDescription().equalsIgnoreCase(liveSearchItem.getSiteName()))
	            	{            	
		            	// Check if UserName matches
		            	if (liveSearchItem.getUsername().isEmpty() || result.getUserName().getDescription().equalsIgnoreCase(liveSearchItem.getUsername()))
		            	{
			            	nodeFound = true;
			            	break;
		            	}
	            	}
	            }
	        }
        }

        else if (liveSearchItem.getResultType() == ResultType.SITE)
        {
        	List<LiveSearchSiteResult> liveSearchResultsSites = getSearchSitesResults();
        	
	        for (LiveSearchSiteResult result : liveSearchResultsSites)
	        {
	            if(result.getSiteName().getDescription().contains(liveSearchItem.getResultItemName()))
	            {
	            	nodeFound = true;
	            	break;
	            }
	        }
        }
        
        else
        {
        	List<LiveSearchPeopleResult> liveSearchResultsPeople = getSearchPeopleResults();
        	
        	for (LiveSearchPeopleResult result : liveSearchResultsPeople)
	        {
	            if(result.getUserName().getDescription().contains(liveSearchItem.getResultItemName()))
	            {
	            	nodeFound = true;
	            	break;
	            }
	        }
        }
     
        return nodeFound;
    }
    
    /**
     * Utility to return true if all Live Search results are from specified site
     *
     * @param siteName String
     * @return false if content found is from site other than specified
     */
    public boolean areAllResultsFromSite(String siteName)
    {
    	boolean allResultScoped = true;
    	
    	List<LiveSearchDocumentResult> liveSearchResultsDocs = getSearchDocumentResults();
    	
    	if (liveSearchResultsDocs.size() == 0) 
    	{
    		allResultScoped = true;
    	}
    	
    	for (LiveSearchDocumentResult result : liveSearchResultsDocs)
        {
    		logger.info("Search Results found within Site: " + result.getTitle().getDescription());
    		
    		if(result.getSiteName().getDescription().equals(siteName))
            {
            	allResultScoped = allResultScoped && true;
            }
            else
            {
            	allResultScoped = allResultScoped && false;
            }
        }
    	
    	if (allResultScoped)
    	{
    		logger.info("No out of scope Search Results found");
    	}
    	
    	return allResultScoped;
    }

}
