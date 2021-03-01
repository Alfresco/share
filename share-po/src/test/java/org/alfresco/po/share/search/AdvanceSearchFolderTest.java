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
    @Test(groups={"Enterprise-only","bug"})
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
    
    @Test(groups={"Enterprise-only","bug"})
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
