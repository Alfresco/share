/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.search;


import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * Integration test to verify search page elements are in place.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */

@Listeners(FailedTestListener.class)
public class FacetedSearchResultsPageTest extends AbstractTest
{
    private static final String SEARCH_TERM = "ipsum";
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password);
    }
    
    @BeforeMethod(groups={"alfresco-one"})
    public void reset()
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().selectMyDashBoard();
    }
    
    @Test(groups="alfresco-one")
    public void searchEmptyResult()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("y@z").render();
        Assert.assertNotNull(resultPage);
        Assert.assertFalse(resultPage.hasResults());
        
    }   
        
    @Test(groups = {"Enterprise-only", "TestBug"},dependsOnMethods="searchEmptyResult")
    public void selectNthSearchResult() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(SEARCH_TERM).render();
        Assert.assertTrue(resultPage.hasResults(),"QA-915");
        String name = resultPage.getResults().get(2).getName();
        Assert.assertNotNull(name);
        DocumentDetailsPage itemPage = resultPage.selectItem(2).render();
        Assert.assertTrue(name.equalsIgnoreCase(itemPage.getDocumentTitle()));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"},dependsOnMethods="selectNthSearchResult")
    public void selectSearchResultByName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(SEARCH_TERM).render();
        Assert.assertTrue(resultPage.hasResults());
        String name = resultPage.getResults().get(2).getName();
        Assert.assertNotNull(name);
        DocumentDetailsPage itemPage = resultPage.selectItem(name).render();
        Assert.assertTrue(name.equalsIgnoreCase(itemPage.getDocumentTitle()));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"},dependsOnMethods="selectSearchResultByName")
    public void selectFirstSearchResult() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(SEARCH_TERM).render();
        Assert.assertNotNull(resultPage);
        
        DocumentDetailsPage itemPage = resultPage.getResults().get(0).clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Document Details"));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"}, dependsOnMethods="selectFirstSearchResult")
    public void selectSearchResultOfTypeFolder() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Images").render();
        Assert.assertNotNull(resultPage);
        
        DocumentLibraryPage itemPage = resultPage.getResultByName("Images").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Library"));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"}, dependsOnMethods="selectSearchResultOfTypeFolder")
    public void selectSearchResultOfTypeWiki() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Project").render();
        Assert.assertNotNull(resultPage);
        
        WikiPage itemPage = resultPage.getResultByName("Main Page").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Wiki"));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"}, dependsOnMethods="selectSearchResultOfTypeWiki")
    public void selectSearchResultOfTypeDataList() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Issue Log").render();
        Assert.assertNotNull(resultPage);
        
        DataListPage itemPage = resultPage.getResultByName("Issue Log").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Project Lists"));
    }
    
    @Test(groups = {"Enterprise-only", "TestBug"}, dependsOnMethods="selectSearchResultOfTypeDataList")
    public void pagination() throws Exception
    {
        int expectedResultLength = 10;
        
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("a").render();        

        // Check the results
        int resultsCount = facetedSearchPage.getResults().size();
        Assert.assertTrue(resultsCount > 0, "After searching for the letter 'a' there should be some search results");

        // If the number of results equals the expectedResultCount - pagination is probably available
        if(resultsCount > expectedResultLength)
        {
            // Force a pagination
            // We do a short scroll first to get past the exclusion of the first scroll event (required for some browsers)
            facetedSearchPage.scrollSome(50);
            facetedSearchPage.scrollToPageBottom();          
              
            // Check the results
            int paginatedResultsCount = facetedSearchPage.getResults().size();
            Assert.assertTrue(paginatedResultsCount > 0, "After searching for the letter 'a' and paginating there should be some search results");
            Assert.assertTrue(paginatedResultsCount >= resultsCount, "After searching for the letter 'a' and paginating there should be the same or more search results");
        }
        facetedSearchPage.getSearchForm().clearSearchTerm();
    }
    
    /**
     * This test is validate the list of sort descriptions are same as what is
     * displayed.
     * 
     * @author Charu
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only", "TestBug" }, dependsOnMethods="pagination")
    public void searchSortDescTest() throws Exception
    {
        String selectedSort;
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search(SEARCH_TERM).render();
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getSort().sortByLabel("Name");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Name", "sort description is not matching");        
        facetedSearchPage.getSort().toggleSortOrder().render();
        Assert.assertNotNull(facetedSearchPage);
    }

    /**
     * This test is to validate whether when we pass the sort type we get
     * correct results back.
     * 
     * @author Charu
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only", "TestBug" }, dependsOnMethods="searchSortDescTest")
    public void searchSortTest() throws Exception
    {
        FacetedSearchPage resultPage;
        SearchBox search = dashBoard.getSearch();
        resultPage = search.search(SEARCH_TERM).render();
        Assert.assertNotNull(resultPage);
        resultPage = resultPage.getSort().sortByLabel("NAME").render();
        List<SearchResult> facetedSearchResult = resultPage.getResults();
        if (facetedSearchResult.isEmpty() || facetedSearchResult == null)
        {
            Assert.fail("serach results is empty");
        }
        Assert.assertTrue(facetedSearchResult.get(0).getName().contains("Meeting"),"The results are sorted by name");
        for (SearchResult results : facetedSearchResult)
        {
            if (results.getTitle().contains("Meeting"))
            {
                Assert.assertTrue(true, "Test passed");
            }
        }
    }

    /**
     * This test is validate the sort with invalid data and to verify the sort order is set to default.
     * To toggle the sort order and verify there are some search results 
     * in sort filter list     * 
     * @author Charu
     * 
     */
    @Test(groups = { "Enterprise-only", "TestBug" }, dependsOnMethods="searchSortTest")
    public void searchSortExceptionTest() throws Exception
    {
        String selectedSort;
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search(SEARCH_TERM).render();
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getSort().sortByLabel("N");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Relevance", "sort description is not matching");        
        facetedSearchPage.getSort().sortByLabel("Title");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Title", "sort description is not matching");
        facetedSearchPage.getSort().toggleSortOrder().render();
        Assert.assertNotNull(facetedSearchPage);
     }
    
    @Test(groups = { "Enterprise-only", "TestBug"}, dependsOnMethods="searchSortExceptionTest")
    public void getResultCount()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search(SEARCH_TERM).render();
        Assert.assertEquals(facetedSearchPage.getResultCount(),6);
        facetedSearchPage = facetedSearchPage.getSearch().search("yyyxxxxz").render();
        Assert.assertEquals(facetedSearchPage.getResultCount(),0);
    }
    
    @Test(groups = { "Enterprise-only", "TestBug"}, dependsOnMethods="getResultCount")
    public void selectFacet()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search(SEARCH_TERM).render();
        FacetedSearchPage filteredResults = facetedSearchPage.selectFacet("Microsoft Word").render();
        Assert.assertEquals(filteredResults.getResultCount(), 3);
    }
    
    
    
    /**
     * This test is validate the sort with invalid data and to verify the sort order is set to default.
     * To toggle the sort order and verify there are some search results 
     * in sort filter list      
     * @author Charu
     * 
     */
    @Test(groups = { "Enterprise-only", "TestBug" })
    public void searchSelectViewTest() throws Exception
    {       
    	SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("ipsum").render();
        Assert.assertNotNull(facetedSearchPage); 
        facetedSearchPage.getView().selectViewByLabel("Detailed View");
        Assert.assertTrue(facetedSearchPage.getView().isDetailedViewResultsDisplayed(),"Detailed view option is matching");
        facetedSearchPage.getView().selectViewByLabel("Gallery View");
        Assert.assertNotNull(facetedSearchPage);
        Assert.assertTrue(facetedSearchPage.getView().isGalleryViewResultsDisplayed(),"Gallery view option is matching");
        facetedSearchPage.getView().selectViewByLabel("Detailed View");
        Assert.assertTrue(facetedSearchPage.getView().isDetailedViewResultsDisplayed(),"Detailed view option is matching");
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getView().selectViewByLabel("Gallery View");
        Assert.assertNotNull(facetedSearchPage);
        GalleryViewPopupPage galleryViewPopupPage = facetedSearchPage.getView().clickGalleryIconByName("Project Overview.ppt");        
        Assert.assertTrue(galleryViewPopupPage.isTitlePresent("Project Overview.ppt"));
        galleryViewPopupPage.selectClose().render();
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getView().selectViewByLabel("Detailed View");
        Assert.assertTrue(facetedSearchPage.getView().isDetailedViewResultsDisplayed(),"Detailed view option is matching");      
        
     }
    
    @Test(groups = { "Enterprise-only", "TestBug" },dependsOnMethods="searchSelectViewTest")
    public void clickImagePreviewTest() throws Exception
    {  	      
       
        SearchBox search = dashBoard.getSearch();        
        FacetedSearchPage facetedSearchPage = search.search("jpg").render();
        Assert.assertNotNull(facetedSearchPage);        
        PreViewPopUpImagePage preViewPopUpImagePage = facetedSearchPage.getResultByName("grass.jpg").clickImageLinkToPicture().render();
        Assert.assertTrue(preViewPopUpImagePage.isPreViewPopupPageVisible(),"Preview popup page");
        preViewPopUpImagePage.selectClose().render();
        Assert.assertTrue(facetedSearchPage.isTitlePresent("Search"));
        SearchBox searchTerm = dashBoard.getSearch();    
        FacetedSearchPage facetedsearchPage = searchTerm.search(SEARCH_TERM).render();
        Assert.assertNotNull(facetedsearchPage);        
        PreViewPopUpPage previewPopUpPage = facetedsearchPage.getResults().get(0).clickImageLink().render();
        //Assert.assertTrue(preViewPopUpPage.isPreViewPopupPageVisible(),"Preview popup page");
        Assert.assertTrue(previewPopUpPage.isTitlePresent("Project Overview.ppt"),"Title is present");
        Assert.assertTrue(previewPopUpPage.isPreViewTextDisplayed(), "Preview text displayed");
        facetedsearchPage = previewPopUpPage.selectClose().render();
        Assert.assertTrue(facetedsearchPage.isTitlePresent("Search"));
       
     }
    
    @Test(groups = { "Enterprise-only", "TestBug" },dependsOnMethods="clickImagePreviewTest")
    public void clickConfigureSearchTest() throws Exception
    {
        String groupName = "ALFRESCO_SEARCH_ADMINISTRATORS";    
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, groupName);
        logout(drone);
        loginAs(userinfo, userinfo);
        FacetedSearchPage facetedSearchPage = dashBoard.getNav().getFacetedSearchPage().render();
        Assert.assertTrue(facetedSearchPage.isConfigureSearchDisplayed(drone));
        FacetedSearchConfigPage facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();
        Assert.assertTrue(facetedSearchConfigPage.getTitle().equals("Search Manager"));
    }
    
    @Test(groups = { "Enterprise-only", "TestBug" },dependsOnMethods="clickConfigureSearchTest")
    public void clickLinksOnSearchItemTest() throws Exception
    { 
        SearchBox search = dashBoard.getSearch();        
        FacetedSearchPage facetedSearchPage = search.search(SEARCH_TERM).render();
        Assert.assertNotNull(facetedSearchPage); 
        String url = drone.getCurrentUrl();
        facetedSearchPage.getResults().get(1).clickDateLink().render();     
        String newUrl = drone.getCurrentUrl();
        // We should no longer be on the faceted search page
        Assert.assertNotEquals(url, newUrl, "After searching for the letter 'a' and clicking the site link of result 1, the url should have changed");     
        facetedSearchPage = dashBoard.getNav().getFacetedSearchPage().render();
        FacetedSearchPage facetedsearchPage = search.search(SEARCH_TERM).render();
        facetedsearchPage.getResults().get(1).clickSiteLink().render(); 
        String newurl = drone.getCurrentUrl();
        Assert.assertNotEquals(url, newurl, "After searching for the letter 'a' and clicking the site link of result 1, the url should have changed");
        facetedSearchPage = dashBoard.getNav().getFacetedSearchPage().render();
        FacetedSearchPage facetedsearchpage = search.search(SEARCH_TERM).render();
        facetedsearchpage.getResults().get(1).clickLink().render(); 
        String newurl1 = drone.getCurrentUrl();
        Assert.assertNotEquals(url, newurl1, "After searching for the letter 'a' and clicking the site link of result 1, the url should have changed");
     }
    
    
}
