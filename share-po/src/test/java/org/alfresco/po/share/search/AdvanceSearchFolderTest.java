
package org.alfresco.po.share.search;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the advance Search Folder Search Page.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
@Listeners(FailedTestListener.class)
public class AdvanceSearchFolderTest extends AbstractTest
{
    @SuppressWarnings("unused")
    private String siteName;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    AdvanceSearchFolderPage folderSearchPage;
    

    /**
     * Pre test setup for Folder Search.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    /**
     * This Test case is to Test content search with all the fields entered.
     * 
     * @throws Exception
     */
    @Test(groups={"Enterprise-only","Enterprise4.2Bug"})
    public void folderSearchTest() throws Exception
    {
    	AdvanceSearchContentPage contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
    	folderSearchPage = contentSearchPage.searchLink("Folders").render(); 	
        contentSearchPage.inputName("Contracts");
        contentSearchPage.inputDescription("This folder holds the agency contracts");        
        FacetedSearchPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());        
    }

    /**
     * This method is keep searching the search until we get results.
     * 
     * @return SearchResultsPage
     * @throws Exception
     */
    public SearchResultsPage searchRetry() throws Exception
    {
        int counter = 0;
        int waitInMilliSeconds = 3000;
        while (counter < 5)
        {
            synchronized (this)
            {
                try{ this.wait(waitInMilliSeconds); } catch (InterruptedException e) {}
            }
            waitInMilliSeconds += 3000;
            SearchResultsPage searchResults = folderSearchPage.clickSearch().render();
            if (searchResults.hasResults())
            {
                return searchResults;
            }
            else
            {
                counter++;
                searchResults.goBackToAdvanceSearch().render();            }
            }
        throw new Exception("search failed");
    }
    /**
     * New test case to Test Keyword Search field input and get.
     * @throws Exception
     * 
     */
    
    @Test(groups={"Enterprise-only","Enterprise4.2Bug"})
    public void folderKeywordSearchTest() throws Exception
    {
        AdvanceSearchContentPage contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        folderSearchPage = contentSearchPage.searchLink("Folders").render();
        folderSearchPage.inputKeyword("Contracts");
        FacetedSearchPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        //folderSearchPage = searchResults.goBackToAdvanceSearch().render();
        //Assert.assertEquals("Contracts", contentSearchPage.getKeyword());
    }
    
    /**
     * This test is to test whether the first result item is folder or not.
     * @throws Exception
     */
    @Test(dependsOnMethods="folderSearchTest")
    public void testIsFolder() throws Exception
    {
        AdvanceSearchContentPage contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        folderSearchPage = contentSearchPage.searchLink("Folders").render();
        folderSearchPage.inputName("Contracts");
        FacetedSearchPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        Assert.assertTrue(searchResults.getResults().get(0).isFolder());
    }
}
