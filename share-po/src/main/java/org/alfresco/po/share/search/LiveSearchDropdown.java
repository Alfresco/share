/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.search;


import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
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
 */
public class LiveSearchDropdown extends SharePage
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdown.class);

    // Documents Title
    private static final String DOCUMENTS_TITLE = "div[data-dojo-attach-point='titleNodeDocs']";

    // Sites Title
    private static final String SITES_TITLE = "div[data-dojo-attach-point='titleNodeSites']";

    // People Title
    private static final String PEOPLE_TITLE = "div[data-dojo-attach-point='titleNodePeople']";

    // Close button
    private static final String CLOSE_DROPDOWN = ".alf-search-box-clear a";

    // Document results
    private static final String DOCUMENT_RESULTS = "div[data-dojo-attach-point='containerNodeDocs'] div.alf-livesearch-item";

    // Sites Results
    private static final String SITES_RESULTS = "div[data-dojo-attach-point='containerNodeSites'] div.alf-livesearch-item";

    // People Results
    private static final String PEOPLE_RESULTS = "div[data-dojo-attach-point='containerNodePeople'] div.alf-livesearch-item";

    // See more document results
    private static final String MORE_RESULTS = "a[title='More'] span";

    /**
     * Constructor
     * 
     * @param drone
     */
    public LiveSearchDropdown(WebDrone drone)
    {
        super(drone);

    }

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
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LiveSearchDropdown render(final long time)
    {
        return render(new RenderTime(time));
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
            
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(DOCUMENT_RESULTS));           
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchDocumentResult(element, drone));
                }
            }

        }
        catch (TimeoutException toe)
        {
            logger.error("No live search document results " + toe);
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
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(SITES_RESULTS));
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchSiteResult(element, drone));
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("No live search sites results " + toe);
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
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(PEOPLE_RESULTS));
            if (elements.size() > 0)
            {
                for (WebElement element : elements)
                {
                    results.add(new LiveSearchPeopleResult(element, drone));
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("No live search people results " + toe);
        }
        return results;
    }

    /**
     * clicks on close button
     */
    public void closeLiveSearchDropdown()
    {
        try
        {
            WebElement closeDropdown = drone.findAndWait(By.cssSelector(CLOSE_DROPDOWN));
            closeDropdown.click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Close live search dropdown button not present " + nse);
            throw new PageException("Unable to find close live search dropdown button.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Close live search dropdown button not present " + te);
            throw new PageException("Close live search dropdown button is not visible", te);
        }

    }

    /**
     * Checks if documents title is displayed
     * 
     * @return
     */
    public boolean isDocumentsTitleVisible()
    {
        try
        {
            WebElement documentTitle = drone.find(By.cssSelector(DOCUMENTS_TITLE));
            return documentTitle.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search documents title " + nse);
            throw new PageException("Unable to find live search documents title.", nse);
        }
    }

    /**
     * Checks if sites title is displayed
     * 
     * @return
     */
    public boolean isSitesTitleVisible()
    {
        try
        {
            WebElement sitesTitle = drone.find(By.cssSelector(SITES_TITLE));
            return sitesTitle.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search sites title " + nse);
            throw new PageException("Unable to find live search sites title.", nse);
        }
    }

    /**
     * Checks if people title is displayed
     * 
     * @return
     */
    public boolean isPeopleTitleVisible()
    {
        try
        {
            WebElement peopleTitle = drone.find(By.cssSelector(PEOPLE_TITLE));
            return peopleTitle.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search people title " + nse);
            throw new PageException("Unable to find live search people title.", nse);
        }
    }

    /**
     * Checks if more icon is displayed
     * 
     * @return
     */
    public boolean isMoreResultsVisible()
    {
        try
        {
            WebElement moreResults = drone.find(By.cssSelector(MORE_RESULTS));
            return moreResults.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No more results icon " + nse);
            throw new PageException("Unable to find more results icon.", nse);
        }
    }

    /**
     * Checks if live search returns document search results
     * 
     * @return
     */
    public boolean hasDocumentSearchResults()
    {
        boolean hasDocumentSearchResults = false;
        List<LiveSearchDocumentResult> liveSearchDocumentResults = getSearchDocumentResults();
        if (liveSearchDocumentResults.size() > 0)
        {
            hasDocumentSearchResults = true;
        }
        return hasDocumentSearchResults;
    }

    /**
     * Checks if live search returns sites search results
     * 
     * @return
     */
    public boolean hasSitesSearchResults()
    {
        boolean hasSitesSearchResults = false;
        List<LiveSearchSiteResult> liveSearchSiteResults = getSearchSitesResults();
        if (liveSearchSiteResults.size() > 0)
        {
            hasSitesSearchResults = true;
        }
        return hasSitesSearchResults;
    }

    /**
     * Checks if live search returns people search results
     * 
     * @return
     */
    public boolean hasPeopleSearchResults()
    {
        boolean hasPeopleSearchResults = false;
        List<LiveSearchPeopleResult> liveSearchPeopleResults = getSearchPeopleResults();
        if (liveSearchPeopleResults.size() > 0)
        {
            hasPeopleSearchResults = true;
        }
        return hasPeopleSearchResults;
    }

    /**
     * Clicks on see more results arrow
     * 
     * @return
     */
    public void clickToSeeMoreDocumentResults()
    {
        try
        {
            WebElement expandDocumentResults = drone.findAndWait(By.cssSelector(MORE_RESULTS));
            drone.mouseOver(expandDocumentResults);
            expandDocumentResults.click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No see more results icon " + nse);
            throw new PageException("Unable to find see more results icon.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find see more results icon. " + te);
            throw new PageException("Unable to find see more results icon. ", te);
        }

    }

    /**
     * Clicks on any link in live search results
     * 
     * @param liveSearchItem
     * @return
     */
    public HtmlPage selectLiveSearchItem(String liveSearchItem)
    {
        if (liveSearchItem == null || liveSearchItem.isEmpty())
        {
            throw new IllegalArgumentException("Live search item is required");
        }
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[text()='%s']", liveSearchItem))).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error(String.format("Live search result %s item not found ", liveSearchItem) + nse);
            throw new PageException(String.format("Live search result %s item not found", liveSearchItem), nse);
        }
        catch (TimeoutException te)
        {
            logger.error(String.format("Live search result %s item not found", liveSearchItem) + te);
            throw new PageException(String.format("Live search result %s item not found", liveSearchItem), te);
        }
    }

}
