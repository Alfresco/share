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
package org.alfresco.po.share;

import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.search.SearchResultsPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests the {@link Pagination} class
 * @author Michael Suzuki
 * @since 1.2
 */
 /**
  * Pagination test has to be modified since pagination does not exist on basic search result page
  * and exist only on advance search result page.
  * Webdrone-704 has been raised to modify this test.  * 
  *    
  */
@Listeners(FailedTestListener.class)
@Test(groups={"Enterprise-only","TestBug"})
public class PaginationTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"Enterprise-only"})
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }
    
    @Test
    public void pagination() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        AllSitesResultsPage result = search.search("xyz").render();
        boolean hasPagination = result.paginationDisplayed();
        Assert.assertFalse(hasPagination);

        SearchResultsPage resultsPage = result;
        try
        {
            resultsPage = result.selectRepository().render();
            resultsPage = ((RepositoryResultsPage) resultsPage).search("email").render();
        }
        catch (NoSuchElementException e)
        {
            // The repository could not be selected since the "Search" config elements <repository-search>
            // hadn't been configured to "context" meaning the repo-link will no be on the page
            resultsPage = ((AllSitesResultsPage) resultsPage).search("email").render();
        }
        hasPagination = resultsPage.paginationDisplayed();
        if (!hasPagination)
        {
            saveScreenShot("PaginationTest.pagination.empty");
        }
        Assert.assertTrue(hasPagination);
        Assert.assertTrue(resultsPage.count() > 76);

        boolean next = Pagination.hasPaginationButton(drone, "a.yui-pg-next");
        Assert.assertTrue(next);
        resultsPage = Pagination.selectPaginationButton(drone, "a.yui-pg-next").render();
        int paginationPosition = resultsPage.getPaginationPosition();
        Assert.assertEquals(paginationPosition, 2);
    }
}
