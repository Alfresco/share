/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.search;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.search.FacetedSearchScopeMenu.ScopeMenuSelectedItemsMenu;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.UUID;

/**
 * Integration test to verify that highlight is working on Search Results page.
 *
 * @author CorinaZ
 */

@Listeners(FailedTestListener.class)
public class FacetedSearchHighlightedTermTest2 extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);
    protected String fileName1;
    protected String fileName2;
    protected String siteName;
    protected String wikiPage;
    protected String calendarName;
    protected String linkName;
    protected String blogName;
    protected String discussionName;
    protected String dataListName;
    protected String random;

    private DashBoardPage dashBoard;
    protected SharePage sharepage;

    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        try
        {
            dashBoard = loginAs(username, password);

            random = UUID.randomUUID().toString();

            // create 2 files, one calendar event, one wiki page, one link, one blog, one discussion, one data list to the site

            siteName = "mysite-" + random;
            fileName1 = "myfile1-" + random;
            fileName2 = "myfile2-" + random;
            calendarName = "calendarevent-" + random;
            wikiPage = "wikipage-" + random;
            linkName = "link-" + random;
            blogName = "blogpost-" + random;
            discussionName = "disscution-" + random;
            dataListName = "datalist-" + random;

            Date todayDate = new Date();
            todayDate.getTime();

            siteUtil.createSite(driver, username, password, siteName, "description", "Public");
            contentService.createDocument(username, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, fileName1);
            contentService.createDocument(username, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName2, fileName2);
            sitePagesService.addCalendarEvent(username, password, siteName, calendarName, calendarName, calendarName, todayDate, todayDate, null, null, false,
                    null);
            sitePagesService.createWiki(username, password, siteName, wikiPage, wikiPage, null);
            sitePagesService.createLink(username, password, siteName, linkName, "www.google.com", linkName, true, null);
            sitePagesService.createBlogPost(username, password, siteName, blogName, blogName, false, null);
            sitePagesService.createDiscussion(username, password, siteName, discussionName, discussionName, null);
            dataListPagesService.createDataList(username, password, siteName, DataListsService.DataList.CONTACT_LIST, dataListName, dataListName);

        }
        catch (Exception pe)
        {
            saveScreenShot("HighlightContentUpload");
            logger.error("Cannot create content to site ", pe);
        }

    }

    @AfterClass(groups = { "alfresco-one" })
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);

    }

    /**
     * Searching using disjunction ("OR"), the result is properly highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 1)
    public void testHighlightedDisjunction() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("myfile1 OR myfile2").render();
        FacetedSearchScopeMenu menu = new FacetedSearchScopeMenu(driver, factoryPage);
        resultPage = menu.selectScope(ScopeMenuSelectedItemsMenu.SPECIFIC_SITE).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem1 = (FacetedSearchResult) resultPage.getResultByName(fileName1);
        Assert.assertTrue(resultItem1.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem1.getHighlightedText(ItemHighlighted.NAME), "myfile1");
        FacetedSearchResult resultItem2 = (FacetedSearchResult) resultPage.getResultByName(fileName2);
        Assert.assertTrue(resultItem2.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem2.getHighlightedText(ItemHighlighted.NAME), "myfile2");
    }

    /**
     * Searching using conjunction ("AND"), the result is properly highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 2)
    public void testHighlightedConjunction() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("myfile AND myfile1").render();

        FacetedSearchResult resultItem1 = (FacetedSearchResult) resultPage.getResultByName(fileName1);
        Assert.assertTrue(resultItem1.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem1.getHighlightedText(ItemHighlighted.NAME), "myfile1");

        // verify that filename2 is not present on the results list
        Assert.assertFalse(false, String.valueOf(resultPage.isItemPresentInResultsList(SitePageType.DOCUMENT_LIBRARY, fileName2)));
    }

    /**
     * * Searching using negation ("NOT"), the result is properly highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 3)
    public void testHighlightedNegation1() throws Exception
    {
        // docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("myfile NOT myfile1").render();

        FacetedSearchResult resultItem1 = (FacetedSearchResult) resultPage.getResultByName(fileName2);
        Assert.assertTrue(resultItem1.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem1.getHighlightedText(ItemHighlighted.NAME), "myfile");

        // verify that filename2 is not present on the results list
        Assert.assertFalse(false, String.valueOf(resultPage.isItemPresentInResultsList(SitePageType.DOCUMENT_LIBRARY, fileName1)));
    }

    /**
     * * Searching using negation ("NOT"), the result is properly highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 4)
    public void testHighlightedNegation2() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("myfile !myfile1").render();

        FacetedSearchResult resultItem1 = (FacetedSearchResult) resultPage.getResultByName(fileName2);
        Assert.assertTrue(resultItem1.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem1.getHighlightedText(ItemHighlighted.NAME), "myfile");
        
        // verify that filename2 is not present on the results list
        Assert.assertFalse(false, String.valueOf(resultPage.isItemPresentInResultsList(SitePageType.DOCUMENT_LIBRARY, fileName1)));
    }

    /**
     * Searching file by Calendar Event name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 5)
    public void testHighlightedCalendarName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("calendarevent").render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResults().get(0);
        // FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName); -- why throw new
        // PageOperationException("Unable to get the name  : ");
        
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem.getHighlightedText(ItemHighlighted.NAME), "calendarevent");
    }

    /**
     * Searching by Wiki page name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 6)
    public void testHighlightedWikiName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("wikipage").render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(wikiPage);
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
        Assert.assertEquals(resultItem.getHighlightedText(ItemHighlighted.NAME), "wikipage");
    }

    /**
     * Searching file by Link name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 7)
    public void testHighlightedLinkName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(linkName).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(linkName);
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
    }

    /**
     * Searching file by Blog name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 8)
    public void testHighlightedBlogName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(blogName).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(blogName);
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
    }

    /**
     * Searching file by Discussion name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 9)
    public void testHighlightedDiscussionName() throws Exception
    {

        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(discussionName).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(discussionName);
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
    }

    /**
     * Searching file by Discussion name, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 10)
    public void testHighlightedDataListName() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(discussionName).render();
        Assert.assertTrue(resultPage.hasResults());

        FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(discussionName);
        Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
    }

}