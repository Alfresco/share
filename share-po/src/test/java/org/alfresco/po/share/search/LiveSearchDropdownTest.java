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

import java.util.List;
import java.util.UUID;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.search.LiveSearchDropdown.ResultType;
import org.alfresco.po.share.search.LiveSearchDropdown.Scope;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests for live search dropdown
 *
 * @author jcule
 * @author adinap
 */
@Test(groups={"alfresco-one"})
@Listeners(FailedTestListener.class)
public class LiveSearchDropdownTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);

    protected String siteName;
    protected String siteName2;
    protected String fileName;
    protected String random;

    private DashBoardPage dashBoard;
    private DocumentLibraryPage docLib;

    @Autowired SiteActions siteActions;

    @BeforeClass
    public void prepare() throws Exception
    {
        try
        {
            dashBoard = loginAs(username, password);
            
            random = UUID.randomUUID().toString();
            
            siteName = random + "1";
            siteName2 = random + "2";
            fileName = random;
            
            siteUtil.createSite(driver, username, password, siteName, "description", "Public");
            siteUtil.createSite(driver, username, password, siteName2, "description", "Public");

            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            contentDetails.setTitle("House");
            contentDetails.setDescription("House");
            contentDetails.setContent("House");

            siteActions.navigateToDocumentLibrary(driver, siteName);
            siteActions.createContent(driver, contentDetails, ContentType.PLAINTEXT);

            siteActions.navigateToDocumentLibrary(driver, siteName2);
            siteActions.createContent(driver, contentDetails, ContentType.PLAINTEXT);
        }
        catch (Throwable pe)
        {
            saveScreenShot("liveSearchFileUpload");
            logger.error("Cannot upload file to site ", pe);
        }
    }

    @AfterClass
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);
        siteUtil.deleteSite(username, password, siteName2);
    }

    /**
     * Live search from User Dashboard: Returns no results
     */
    @Test(priority = 0)
    public void testCheckNoLiveSearchResults()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(random + "x@z").render();

        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isScopeRepositoryVisible());
        Assert.assertFalse(liveSearchResultPage.isScopeSiteVisible());
    }

    /**
     * Checks that when in site, the Live Search dropdown contains the scope options
     *
     */
    @Test(priority = 1)
    public void testCheckLiveSearchScopeOptionsSiteContext()
    {
    	docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        SearchBox search = docLib.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        Assert.assertTrue(liveSearchResultPage.isScopeRepositoryVisible());
        Assert.assertTrue(liveSearchResultPage.isScopeSiteVisible());
    }

    /**
     * Checks that when not in site context, the Live Search dropdown does not contain the scope options
     *
     */
    @Test(priority = 2)
    public void testCheckLiveSearchScopeOptionsRepoContext()
    {
    	dashBoard = siteActions.openUserDashboard(driver).render();
        SearchBox search = dashBoard.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        Assert.assertFalse(liveSearchResultPage.isScopeRepositoryVisible());
        Assert.assertFalse(liveSearchResultPage.isScopeSiteVisible());
    }

    /**
     * Checks the site name in Search Site scope option
     *
     */
    @Test(priority = 3)
    public void testCheckSiteScopeSiteName()
    {
    	docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        SearchBox search = docLib.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        String scopeSiteName = liveSearchResultPage.getScopeSiteName();
        Assert.assertEquals(scopeSiteName, siteName);
    }

    /**
     * Checks that the document search result contains document name, site name and user name
     *
     */
    @Test(priority = 4)
    public void testLiveSearchDocumentResult()
    {
    	LiveSearchResultItem liveSearchResult = new LiveSearchResultItem(ResultType.DOCUMENT, fileName);
    	liveSearchResult.setSiteName(siteName);
    	liveSearchResult.setUsername(username);
    	Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, fileName, Scope.DEFAULT, liveSearchResult, true, 3), "Live Search Results for Documents are not as expected");
    }

    /**
     * Expands document search results
     */
    @Test(priority = 5)
    public void testExpandLiveSearchDocumentResult()
    {
    	siteActions.openUserDashboard(driver).render();
    	
    	LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, "jpg", Scope.DEFAULT).render();
    	
    	liveSearchResultPage = liveSearchResultPage.clickToSeeMoreDocumentResults().render();

        List<LiveSearchDocumentResult> liveSearchResultsPage = liveSearchResultPage.getSearchDocumentResults();
        Assert.assertTrue(liveSearchResultsPage.size() > 0);

        liveSearchResultPage.closeLiveSearchDropdown().render();

        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());
    }

    /**
     * Clicks on the document name in the document search result and checks that
     * the document's details page is displayed
     */
    @Test(priority = 6)
    public void testClickOnDocumentTitle()
    {
    	dashBoard = siteActions.openUserDashboard(driver).render();
        SearchBox search = dashBoard.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        List<LiveSearchDocumentResult> documentResultList = liveSearchResultPage.getSearchDocumentResults();
        Assert.assertTrue(documentResultList.size() > 0);

        DocumentDetailsPage documentDetailsPage = documentResultList.get(0).clickOnDocumentTitle().render();
        Assert.assertEquals(documentDetailsPage.getDocumentTitle(), fileName);
    }

    /**
     * Clicks on document site name in the document search result and checks
     * that document site library page is displayed
     */
    @Test(priority = 7)
    public void testClickOnDocumentSiteName()
    {
    	dashBoard = siteActions.openUserDashboard(driver).render();
        SearchBox search = dashBoard.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        List<LiveSearchDocumentResult> documentResultList = liveSearchResultPage.getSearchDocumentResults();
        Assert.assertTrue(documentResultList.size() > 0);

        DocumentLibraryPage documentLibraryPage = documentResultList.get(0).clickOnDocumentSiteTitle().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));
    }

    /**
     * Clicks on document user name in document search result and checks
     * that user profile page is displayed
     */
    @Test(priority = 8)
    public void testClickOnDocumentUserName()
    {
        dashBoard = siteActions.openUserDashboard(driver).render();
        SearchBox search = dashBoard.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        List<LiveSearchDocumentResult> documentResultList = liveSearchResultPage.getSearchDocumentResults();
        Assert.assertTrue(documentResultList.size() > 0);

        MyProfilePage myProfilePage = documentResultList.get(0).clickOnDocumentUserName().render();
        Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
    }

    /**
     * Searches for site and checks that site name is displayed in site results
     */
    @Test(priority = 9)
    public void testLiveSearchSitesResult()
    {
        LiveSearchResultItem liveSearchResult = new LiveSearchResultItem(ResultType.SITE, siteName);
        liveSearchResult.setSiteName(siteName);

        Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, siteName, Scope.DEFAULT, liveSearchResult, true, 3), "Live Search Results for Site are not as expected: " + siteName);

        LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, siteName, Scope.DEFAULT).render();

        List<LiveSearchSiteResult> sitesResultList = liveSearchResultPage.getSearchSitesResults();
        Assert.assertTrue(sitesResultList.size() > 0, "Live search results not found.");

        for(LiveSearchSiteResult result : sitesResultList)
        {
            Assert.assertTrue(result.getSiteName().getDescription().contains(siteName));
        }
    }

    /**
     * Searches for username and checks that it is displayed in people search results
     */
    @Test(priority = 10)
    public void testLiveSearchPeopleResult()
    {
        LiveSearchResultItem liveSearchResult = new LiveSearchResultItem(ResultType.PEOPLE, username);
        liveSearchResult.setUsername(username);

        Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, username, Scope.DEFAULT, liveSearchResult, true, 3), "Live Search Results for People are not as expected: " + username);

        LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, username, Scope.DEFAULT).render();

        List<LiveSearchPeopleResult> peopleResultList = liveSearchResultPage.getSearchPeopleResults();
        Assert.assertTrue(peopleResultList.size() > 0, "Live search results not found.");

        for(LiveSearchPeopleResult result : peopleResultList)
        {
            Assert.assertTrue(result.getUserName().getDescription().contains(username));
        }
    }

    /**
     * Clicks on the site name in sites search results and checks
     * that the site dashboard page is displayed
     */
    @Test(priority = 11)
    public void testClickOnSiteResult()
    {
        LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, siteName, Scope.DEFAULT).render();

        List<LiveSearchSiteResult> siteResultList = liveSearchResultPage.getSearchSitesResults();
        Assert.assertTrue(siteResultList.size() > 0, "Live search results not found.");

        SiteDashboardPage siteDashboardPage = siteResultList.get(0).clickOnSiteTitle().render();
        Assert.assertTrue(siteDashboardPage.isSiteTitle(siteName));
    }

    /**
     * Clicks on username in people search result and checks that
     * user profile page is displayed
     */
    @Test(priority = 12)
    public void testClickOnPeopleResult()
    {
        LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, username, Scope.DEFAULT).render();

        List<LiveSearchPeopleResult> peopleResultList = liveSearchResultPage.getSearchPeopleResults();
        Assert.assertTrue(peopleResultList.size() > 0, "Live search results not found.");

        MyProfilePage myProfilePage = peopleResultList.get(0).clickOnUserName().render();
        Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
    }

    /**
     * When clicking on Search Site scope, Document results are from the current site
     */
    @Test(priority = 13)
    public void testLiveSearchInSiteResults()
    {
    	siteActions.navigateToDocumentLibrary(driver, siteName).render();
    	
    	LiveSearchResultItem liveSearchResult = new LiveSearchResultItem(ResultType.DOCUMENT, fileName);
    	liveSearchResult.setSiteName(siteName);
    	Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, fileName, Scope.SITE, liveSearchResult, true, 3), "Live Search Results for Documents are not as expected for: " + siteName);

    	LiveSearchDropdown liveSearchResults = siteActions.liveSearch(driver, fileName, Scope.SITE).render();

    	Assert.assertTrue(liveSearchResults.areAllResultsFromSite(siteName), "Results from other sites are found when Search Scope = Site");
    }

    /**
     * When clicking on Search Repository scope, Document results are from the entire repository
     */
    @Test(priority = 14)
    public void testLiveSearchInRepositoryResults()
    {
    	siteActions.navigateToDocumentLibrary(driver, siteName).render();
    	
    	LiveSearchResultItem liveSearchResult = new LiveSearchResultItem(ResultType.DOCUMENT, fileName);
    	liveSearchResult.setSiteName(siteName);
    	Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, fileName, Scope.REPO, liveSearchResult, true, 3), "Live Search Results for Documents are not as expected for: " + siteName);
    	
    	liveSearchResult = new LiveSearchResultItem(ResultType.DOCUMENT, fileName);
    	liveSearchResult.setSiteName(siteName2);
    	Assert.assertTrue(siteActions.checkLiveSearchResultsWithRetry(driver, fileName, Scope.REPO, liveSearchResult, true, 3), "Live Search Results for Documents are not as expected: " + siteName2);
    }

    /**
     * Checks that when pressing Enter in liveSearch with Search site scope, the scope in Search Results page is
     * set to current site
     *
     */
    @Test(priority = 15)
    public void testLiveSearchToFacetedInSiteScope()
    {
    	docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        SearchBox search = docLib.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        liveSearchResultPage = liveSearchResultPage.selectScope(Scope.SITE).render();

        String scopeSiteName = liveSearchResultPage.getScopeSiteName();

        FacetedSearchPage facetedResultsPage = liveSearchResultPage.getSearch().search(fileName + Keys.RETURN).render();
        Assert.assertEquals(facetedResultsPage.getScopeMenu().getCurrentSelection(), scopeSiteName);
        
        // TODO: Check results are returned accordingly
    }

    /**
     * Checks that when pressing Enter in liveSearch with Search Alfresco scope, the scope in Search Results page is
     * set to Repository
     *
     */
    @Test(priority = 16)
    public void testLiveSearchToFacetedInRepositoryScope()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        SearchBox search = docLib.getSearch().render();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        liveSearchResultPage.selectScope(Scope.REPO);

        FacetedSearchPage facetedResultsPage = liveSearchResultPage.getSearch().search(fileName + Keys.RETURN).render();
        Assert.assertEquals(facetedResultsPage.getScopeMenu().getCurrentSelection(), "Repository");
        
        // TODO: Check results are returned accordingly
    }

    /**
     * Checks that when in a site context, if there are no results in the site but there are results in the Repository,
     * the scope options are visible
     *
     */
    @Test(priority = 17)
    public void testLiveSearchNoResultsInSite()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        LiveSearchDropdown liveSearchResultPage = siteActions.liveSearch(driver, "jpg", Scope.SITE).render();

        Assert.assertTrue(liveSearchResultPage.isScopeRepositoryVisible());
        Assert.assertTrue(liveSearchResultPage.isScopeSiteVisible());

        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());
    }

}
