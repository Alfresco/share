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

package org.alfresco.po.share.steps;

/**
 * Class contains Common user steps / actions / utils for regression tests
 * 
 *  @author mbhave
 *  @author adinap
 */

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.ActivityShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.search.*;
import org.alfresco.po.share.search.LiveSearchDropdown.Scope;
import org.alfresco.po.share.site.SitePageType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommonActions
{
	private static final Log logger = LogFactory.getLog(CommonActions.class);
	public static long refreshDuration = 20000;
    protected static final String MY_DASHBOARD = " Dashboard";
    public final static String DOCLIB = "DocumentLibrary";
    @Autowired protected FactoryPage factoryPage;
    
    public int retrySearchCount = 3;
    

    /**
     * Checks if driver is null, throws UnsupportedOperationException if so.
     *
     * @param driver WebDriver Instance
     * @throws UnsupportedOperationException if driver is null
     */
    public void checkIfDriverIsNull(WebDriver driver)
    {
        if (driver == null)
        {
            throw new UnsupportedOperationException("WebDriver is required");
        }
    }
    
    /**
     * Checks if the current page is share page, throws PageException if not.
     *
     * @param driver WebDriver Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
     */
    public SharePage getSharePage(WebDriver driver)
    {
        checkIfDriverIsNull(driver);
        try
        {
            HtmlPage generalPage = factoryPage.getPage(driver);
            return (SharePage) generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Can not cast to SharePage: Current URL: " + driver.getCurrentUrl());
        }
    }
    
    /**
     * Refreshes and returns the current page: throws PageException if not a share page.
     * 
     * @param driver WebDriver Instance
     * @return HtmlPage
     * */
    public HtmlPage refreshSharePage(WebDriver driver)
    {
        checkIfDriverIsNull(driver);
        driver.navigate().refresh();
        return getSharePage(driver);
    }

    /**
     * Common method to wait for the next solr indexing cycle.
     * 
     * @param driver WebDriver Instance
     * @param waitMiliSec Wait duration in milliseconds
     */
    public HtmlPage webDriverWait(WebDriver driver, long waitMiliSec)
    {
        checkIfDriverIsNull(driver);

        synchronized (this)
        {
            try
            {
                this.wait(waitMiliSec);
            }
            catch (InterruptedException e)
            {
                // Discussed not to throw any exception
            }
        }
        return getSharePage(driver);
    }
    /**
     * Navigate to User DashBoard page and waits for the page render to
     * complete. Assumes User is logged in
     * 
     * @param driver WebDriver Instance
     * @return DashBoardPage
     */
    public DashBoardPage refreshUserDashboard(WebDriver driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);
        return page.getNav().selectMyDashBoard().render();
    }

    /**
     * Navigate to User DashBoard and waits for the page render to complete.
     * Assumes User is logged in
     * 
     * @param driver WebDriver Instance
     * @return DashBoardPage
     */
    public DashBoardPage openUserDashboard(WebDriver driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);
        if (page.getPageTitle().contains(MY_DASHBOARD))
        {
            return (DashBoardPage) page;
        }

        return refreshUserDashboard(driver);
    }
    
    /**
     * Helper to search for an Element in the list of <ShareLinks>.
     * 
     * @param driver WebDriver Instance
     * @param dashlet String Name of the dashlet
     * @return List<ShareLink>: List of Share Links available in the dashlet
     */
    protected List<ShareLink> getDashletEntries(WebDriver driver, Dashlets dashlet)
    {
        List<ShareLink> entries = null;
        List<ActivityShareLink> activityEntries = null;

        DashBoardPage dashBoard = getSharePage(driver).render();
        if (dashlet == null)
        {
            dashlet = Dashlets.MY_DOCUMENTS;
        }

        if (dashlet.equals(Dashlets.MY_DOCUMENTS))
        {
            MyDocumentsDashlet myDocumentsDashlet = dashBoard.getDashlet(dashlet.getDashletName()).render();
            entries = myDocumentsDashlet.getDocuments();
        }
        else if (dashlet.equals(Dashlets.MY_ACTIVITIES))
        {
        	MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet(dashlet.getDashletName()).render();
            activityEntries = activitiesDashlet.getActivities();   
        }

        return entries;
    }
    
    /**
     * Helper to search for an Element in the list of <ActivityShareLink>.
     * 
     * @param driver WebDriver Instance
     * @return List<ActivityShareLink>: List of Share Links available in the dashlet
     */
    protected List<ActivityShareLink> getMyActivitiesDashletEntries(WebDriver driver)
    {
        List<ActivityShareLink> activityEntries = null;

        DashBoardPage dashBoard = getSharePage(driver).render();
        Dashlets dashlet = Dashlets.MY_ACTIVITIES;

        	MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet(dashlet.getDashletName()).render();
            activityEntries = activitiesDashlet.getActivities();

        return activityEntries;
    }
    
    /**
     * Helper to search for an Element in the list of <ShareLinks>.
     * 
     * @param entryList <ShareLinks>
     * @param entry String entry to be found in the ShareLinks' list
     * @return Boolean true if entry is found, false if not
     */
    protected Boolean findInList(List<ShareLink> entryList, String entry)
    {
        for (ShareLink link : entryList)
        {
            if (entry.equalsIgnoreCase(link.getDescription()))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Helper to search for an Element in the list of <MyActivities>.
     * 
     * @param entryList <ActivityShareLink>
     * @param entry String entry to be found in the ShareLinks' list
     * @return Boolean true if entry is found, false if not
     */
    protected Boolean findInMyActivitiesList(List<ActivityShareLink> entryList, String entry)
    {
        for (ActivityShareLink link : entryList)
        {
            if (entry.equalsIgnoreCase(link.getDescription()))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Helper to search for an Activity Entry on the Site Dashboard Page, with configurable retry search option.
     * 
     * @param driver <WebDriver> instance
     * @param dashlet <String> Name of the Dashlet such as: activities,content,myDocuments etc
     * @param entry <String> Entry to look for within the Dashlet
     * @param entryPresent <String> Parameter to indicate should the entry be visible within the dashlet
     * @return <Boolean>
     */
    public Boolean searchUserDashBoardWithRetry(WebDriver driver, Dashlets dashlet, String entry, Boolean entryPresent)
    {
        Boolean found = !entryPresent;
        Boolean resultAsExpected = false;

        List<ShareLink> shareLinkEntries = new ArrayList<ShareLink>();
        List<ActivityShareLink> entriesFound = new ArrayList<ActivityShareLink>();

        // Open Site DashBoard: Assumes User is logged in
        DashBoardPage dashBoard = openUserDashboard(driver);

        // Repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                // This below code is needed to wait for the solr indexing.
                webDriverWait(driver, refreshDuration);

                dashBoard = refreshUserDashboard(driver);
            }

            if (dashlet.equals(Dashlets.MY_ACTIVITIES))
            {
                MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet(Dashlets.MY_ACTIVITIES.getDashletName()).render();
                entriesFound = activitiesDashlet.getActivities();
                
                if (!entriesFound.isEmpty())
                {
                    found = findInMyActivitiesList(entriesFound, entry);
                }
                
            }
            else
            {
                shareLinkEntries = getDashletEntries(driver, dashlet);

                if (!shareLinkEntries.isEmpty())
                {
                    found = findInList(shareLinkEntries, entry);
                }
            }

            // Loop again if result is not as expected: To cater for solr lag: eventual consistency
            resultAsExpected = (entryPresent.equals(found));
            if (resultAsExpected)
            {
                break;
            }
        }

        return resultAsExpected;
    }

    /**
     * Util to perform search using the given search string and value
     * 
     * @param driver WebDriver
     * @param searchString String
     * @return FacetedSearchPage
     */
    public HtmlPage search(WebDriver driver, String searchString)
    {
        SearchBox search = getSharePage(driver).getSearch();
        FacetedSearchPage resultPage = search.search(searchString).render();
        return resultPage;
    }
    
    /**
     * Util to perform search using the given search string and value
     * 
     * @param driver WebDriver
     * @param searchString String
     * @return LiveSearchDropdown
     */
    public LiveSearchDropdown liveSearch(WebDriver driver, String searchString, Scope scope)
    {
    	
    	SearchBox search = getSharePage(driver).getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(searchString).render();
        return liveSearchResultPage.selectScope(scope).render();
    }
    
    /**
     * Util to perform search and check if search results are as expected
     * 
     * @param driver WebDriver
     * @param searchString String
     * @param nodeNameToLookFor String
     * @param expectedInResults boolean
     * @return true if search results are as expected
     */

    public boolean checkSearchResults(WebDriver driver, String searchString, String nodeNameToLookFor, boolean expectedInResults)
    {
        FacetedSearchPage resultPage = search(driver, searchString).render();
        if (resultPage.hasResults())
        {
            return expectedInResults == resultPage.isItemPresentInResultsList(SitePageType.DOCUMENT_LIBRARY, nodeNameToLookFor);
        }
        else
        {
            return expectedInResults == false;
        }
    }
    
    /**
     * Util to perform search and retry waiting for solr indexing : check if search results are as expected
     * 
     * @param driver WebDriver
     * @param searchString String
     * @param nodeNameToLookFor String
     * @param expectedInResults boolean
     * @param retrySearchCount int
     * @return true if search results are as expected
     */
    public boolean checkSearchResultsWithRetry(WebDriver driver, String searchString, String nodeNameToLookFor, boolean expectedInResults, int retrySearchCount)
    {
        boolean resultOk = false;
        
        for (int searchCount = 1; searchCount < retrySearchCount; searchCount++)
        {
        	resultOk = checkSearchResults(driver, searchString, nodeNameToLookFor, expectedInResults);
        	
        	// ResultOk?
        	if (resultOk)
        	{
        		return resultOk;
        	}
            else
            {
            	// Retry: Wait for Solr Indexing
            	logger.info("Waiting for the solr indexing to catchup for Node: " + nodeNameToLookFor);
				webDriverWait(driver, refreshDuration);
				refreshSharePage(driver).render();
            }
         }
        return checkSearchResults(driver, searchString, nodeNameToLookFor, expectedInResults);
    }
   
    /**
    * Util to perform search and check if search results are as expected
    * 
    * @param driver WebDriver
    * @param searchString String
    * @param searchScope Scope
    * @param liveSearchItem LiveSearchResultItem
    * @param expectedInResults boolean
    * @return true if search results are as expected
    */
    public boolean checkLiveSearchResults(WebDriver driver, String searchString, Scope searchScope, LiveSearchResultItem liveSearchItem, boolean expectedInResults)
    {
        LiveSearchDropdown liveSearchResults = liveSearch(driver, searchString, searchScope).render();
        return expectedInResults == liveSearchResults.isItemListed(liveSearchItem);
    }
   
    /**
    * 
    * @param driver WebDriver
    * @param searchString String
    * @param searchScope Scope
    * @param siteName String
    * @param expectedInResults boolean
    * @return boolean
    */
   public boolean checkAllLiveSearchResultsAreScoped(WebDriver driver, String searchString, Scope searchScope, String siteName, boolean expectedInResults)
   {
       LiveSearchDropdown liveSearchResults = liveSearch(driver, searchString, searchScope).render();
       
       return expectedInResults == liveSearchResults.areAllResultsFromSite(siteName);
   }
   
   /**
    * Util to perform search and retry waiting for solr indexing : check if search results are as expected
    * 
    * @param driver WebDriver
    * @param searchString String
    * @param searchScope Scope
    * @param liveSearchItem LiveSearchResultItem
    * @param expectedInResults boolean
    * @param retrySearchCount int
    * @return true if search results are as expected
    */
    public boolean checkLiveSearchResultsWithRetry(WebDriver driver, String searchString, Scope searchScope, LiveSearchResultItem liveSearchItem, boolean expectedInResults, int retrySearchCount)
    {
        boolean resultOk = false;

        for (int searchCount = 1; searchCount < retrySearchCount; searchCount++)
        {
       	    resultOk = checkLiveSearchResults(driver, searchString, searchScope, liveSearchItem, expectedInResults);
       	
       	    // ResultOk?
       	    if (resultOk)
       	    {
       		    return resultOk;
       	    }
            else
            {
           	    // Retry: Wait for Solr Indexing
           	    logger.info("Waiting for the solr indexing to catchup for Node: " + liveSearchItem.getResultItemName());
				webDriverWait(driver, refreshDuration);
            }
        }
        return checkLiveSearchResults(driver, searchString, searchScope, liveSearchItem, expectedInResults);
    }

}
