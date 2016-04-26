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
import java.util.UUID;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.MyProfilePage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests for live search dropdown
 *
 * @author jcule
 */
@Test(groups={"alfresco-one"})
@Listeners(FailedTestListener.class)
public class LiveSearchDropdownTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);
    protected String siteName;
    protected String fileName;

    private DashBoardPage dashBoard;

    @BeforeClass
    public void prepare() throws Exception
    {
        try
        {

            dashBoard = loginAs(username, password);
            String random = UUID.randomUUID().toString();
            siteName = random;
            fileName = random;
            siteUtil.createSite(driver, username, password, siteName, "description", "Public");
            SitePage site = resolvePage(driver).render();
            DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
            CreatePlainTextContentPage contentPage = docPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            contentDetails.setTitle("House");
            contentDetails.setDescription("House");
            contentDetails.setContent("House");
            contentPage.create(contentDetails).render();
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
    }

    @Test
    public void checkNoLiveSearchResults()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch("x@z").render();
        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());

    }


    /**
     * Checks that the document search result contains document name,
     * site name and user name
     *
     * @throws InterruptedException
     */
    @Test(dependsOnMethods = "checkNoLiveSearchResults")
    public void liveSearchDocumentResult() throws Exception
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchDocumentsRetry();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            Assert.assertEquals(liveSearchDocumentResult.getTitle().getDescription(), fileName);
            Assert.assertEquals(liveSearchDocumentResult.getSiteName().getDescription(), siteName);
            Assert.assertEquals(liveSearchDocumentResult.getUserName().getDescription(), username.toLowerCase());
        }
    }

    /**
     * Expands document search results
     */

    @Test(dependsOnMethods = "liveSearchDocumentResult")
    public void expandLiveSearchDocumentResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch("jpg").render();
        Assert.assertNotNull(liveSearchResultPage);

        liveSearchResultPage.clickToSeeMoreDocumentResults();
        List<LiveSearchDocumentResult> liveSearchResultsPage = liveSearchResultPage.getSearchDocumentResults();

        Assert.assertTrue(liveSearchResultsPage.size() > 0);

        liveSearchResultPage.closeLiveSearchDropdown();
        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());
    }


    /**
     * Clicks on the document name in the document search result and checks that
     * the documents details page is displayed
     */
    @Test(dependsOnMethods = "expandLiveSearchDocumentResult")
    public void clickOnDocumentTitle() throws Exception
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchDocumentsRetry();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            DocumentDetailsPage documentDetailsPage = liveSearchDocumentResult.clickOnDocumentTitle().render();
            Assert.assertEquals(documentDetailsPage.getDocumentTitle(), fileName);
        }

    }


    /**
     * Clicks on the site name in the document search result and checks
     * that document site library page is displayed
     */
    @Test(dependsOnMethods = "clickOnDocumentTitle")
    public void clickOnDocumentSiteName() throws Exception
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchDocumentsRetry();
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            DocumentLibraryPage documentLibraryPage = liveSearchDocumentResult.clickOnDocumentSiteTitle().render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));
        }

    }

    /**
     * Clicks on document user name in document search result and checks
     * that user profile page is displayed
     */
    @Test(dependsOnMethods = "clickOnDocumentSiteName")
    public void clickOnDocumentUserName() throws Exception
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchDocumentsRetry();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            MyProfilePage myProfilePage = liveSearchDocumentResult.clickOnDocumentUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }

    }


    /**
     * Searches for site and checks that site name is displayed in site results
     */
    @Test(dependsOnMethods = "clickOnDocumentUserName")
    public void liveSearchSitesResult() throws Exception
    {
        List<LiveSearchSiteResult> liveSearchSitesResults = liveSearchSitesRetry();
        for (LiveSearchSiteResult liveSearchSiteResult : liveSearchSitesResults)
        {
            Assert.assertEquals(liveSearchSiteResult.getSiteName().getDescription(), siteName);
        }

    }



    /**
     * Searches for username and checks that it is displayed in people search results
     */
    @Test(dependsOnMethods = "liveSearchSitesResult")
    public void liveSearchPeopleResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(username).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchPeopleResult> liveSearchPeopleResults = liveSearchResultPage.getSearchPeopleResults();
        Assert.assertTrue(liveSearchPeopleResults.size() > 0);

        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            Assert.assertTrue(liveSearchPeopleResult.getUserName().getDescription().indexOf(username) != -1);
        }

    }


    /**
     * Clicks on the site name in sites search results and checks
     * that the site dashboard page is displayed
     */
    @Test(dependsOnMethods = "liveSearchPeopleResult")
    public void clickOnSiteResult() throws Exception
    {
        List<LiveSearchSiteResult> liveSearchSiteResults = liveSearchSitesRetry();
        Assert.assertTrue(liveSearchSiteResults.size() > 0);
        for (LiveSearchSiteResult liveSearchSitesResult : liveSearchSiteResults)
        {
            SiteDashboardPage siteDashboardPage = liveSearchSitesResult.clickOnSiteTitle().render();
            Assert.assertTrue(siteDashboardPage.isSiteTitle(siteName));
        }
    }

    /**
     * Clicks on username in people search result and checks that
     * user profile page is displayed
     */
    @Test(dependsOnMethods = "clickOnSiteResult")
    public void clickOnPeopleResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(username).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchPeopleResult> liveSearchPeopleResults = liveSearchResultPage.getSearchPeopleResults();
        Assert.assertTrue(liveSearchPeopleResults.size() > 0);
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            MyProfilePage myProfilePage = liveSearchPeopleResult.clickOnUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }
    }

     /**
     * Retries search for a document result
     * @return List<LiveSearchDocumentResult>
     * @throws Exception
     */
    public List<LiveSearchDocumentResult> liveSearchDocumentsRetry() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = null;
        List<LiveSearchDocumentResult> liveSearchDocumentResults = new ArrayList<LiveSearchDocumentResult>();
        int counter = 0;
        int waitInMilliSeconds = 8000;
        while (counter < 3)
        {
            liveSearchResultPage = search.liveSearch(fileName).render();
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
            liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
            if ((liveSearchDocumentResults.size() == 1) && liveSearchDocumentResults.get(0).getTitle().getDescription().equalsIgnoreCase(fileName))
            {
                return liveSearchDocumentResults;

            }
            else
            {
                counter++;
                dashBoard = dashBoard.getNav().selectMyDashBoard().render();

            }
            // double wait time to not overdo solr search
            waitInMilliSeconds = (waitInMilliSeconds * 2);

        }
        throw new Exception("livesearch failed");
    }

    /**
     * Retries search for a site result
     * @return List<LiveSearchSiteResult>
     * @throws Exception
     */
    public List<LiveSearchSiteResult> liveSearchSitesRetry() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = null;
        List<LiveSearchSiteResult> liveSearchSiteResults = new ArrayList<LiveSearchSiteResult>();
        int counter = 0;
        int waitInMilliSeconds = 8000;
        while (counter < 3)
        {
            liveSearchResultPage = search.liveSearch(siteName).render();
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
            liveSearchSiteResults = liveSearchResultPage.getSearchSitesResults();
            if ((liveSearchSiteResults.size() == 1) && liveSearchSiteResults.get(0).getSiteName().getDescription().equalsIgnoreCase(siteName))
            {
                return liveSearchSiteResults;
            }
            else
            {
                counter++;
                dashBoard = dashBoard.getNav().selectMyDashBoard().render();
            }
            // double wait time to not overdo solr search
            waitInMilliSeconds = (waitInMilliSeconds * 2);

        }
        throw new Exception("livesearch failed");
    }
}
