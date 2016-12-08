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
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Integration test to verify that highlight is working on Search Results page.
 *
 * @author CorinaZ
 */

@Listeners(FailedTestListener.class) public class FacetedSearchHighlightedTermTest1 extends AbstractTest
{
        private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);
        protected String siteName;
        protected String fileName;
        protected String folderName;
        protected String random;
        protected SharePage sharepage;
        private DashBoardPage dashBoard;
        private DocumentLibraryPage docLib;
        private AdvanceSearchPage contentSearchPage;

        @BeforeClass(groups = { "alfresco-one" })
        public void prepare() throws Exception
        {
                try
        {
                dashBoard = loginAs(username, password);
                random = UUID.randomUUID().toString();

                //create one file to the repository with the same name, title, description and content
                siteName = "mycontent-" + random;
                fileName = "mycontent" + System.currentTimeMillis();
                folderName = "mycontentf-" + random;

                //create one site
                siteUtil.createSite(driver, username, password, siteName, "description", "Public");

                //create one file in the site with the same name, title, description and content
                siteActions.navigateToDocumentLibrary(driver, siteName);

                // create one folder the same name, title and description
                siteActions.createFolder(driver, folderName, folderName, folderName);

                //create one file in the folder with the same name, title, description and content
                siteActions.navigateToFolder(driver, folderName);
                ContentDetails contentDetails1 = new ContentDetails();
                contentDetails1.setName(fileName);
                contentDetails1.setTitle(fileName);
                contentDetails1.setDescription(fileName);
                contentDetails1.setContent(fileName);
                siteActions.createContent(driver, contentDetails1, ContentType.PLAINTEXT);

        }
                catch (Throwable pe)
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
         * Searching file by name, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = {"alfresco-one" }, priority = 1)
        public void testHighlightedSearchFileByName() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search(fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.NAME), fileName);
        }

        /**
         * Searching file by name, the result is highlighted
         * Search is performed from Advanced Search
         */
        @Test(groups = {"alfresco-one"}, priority = 2)
        public void testHighlightedSearchFileByNameAdvancedSearch() throws Exception
        {
                contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
                contentSearchPage.inputName(fileName);
                FacetedSearchPage resultPage = contentSearchPage.clickSearch().render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.NAME),fileName);
        }

        /**
         * Searching folder by name, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = {"alfresco-one"}, priority = 3)
        public void testHighlightedSearchFolderByName() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                String SEARCH_TERM = "mycontentf";
                FacetedSearchPage resultPage = search.search(SEARCH_TERM).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(folderName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.NAME), SEARCH_TERM);
        }

        /**
         * Searching file by title, the result is highlighted
         * Search is performed from Advanced Search
         */
        @Test(groups = {"alfresco-one"}, priority = 4)
        public void testHighlightedSearchFileByTitle() throws Exception
        {
                contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
                contentSearchPage.inputTitle(fileName);
                FacetedSearchPage resultPage = contentSearchPage.clickSearch().render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.TITLE),fileName);

        }

        /**
         * Searching folder by title, the result is highlighted
         * Search is performed from Advanced Search
         */
        @Test(groups = {"alfresco-one"}, priority = 5)
        public void testHighlightedSearchFolderByTitle() throws Exception
        {
                contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
                contentSearchPage.searchLink("Folders").render();
                contentSearchPage.inputTitle(folderName);
                SearchResultPage resultPage = contentSearchPage.clickSearch().render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResults().get(0);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
        }

        /**
         * Searching file by description, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = {"alfresco-one" }, priority = 6)
        public void testHighlightedSearchFileByDescription() throws Exception
        {
                docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
                SearchBox search = docLib.getSearch();
                FacetedSearchPage resultPage = search.search(fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.DESCRIPTION), fileName);

        }

        /**
         * Searching file by description, the result is highlighted
         * Search is performed from Advanced Search
         */
        @Test(groups = {"alfresco-one" }, priority = 7)
        public void testHighlightedSearchFileByDescriptionAdvancedSearch() throws Exception
        {
                contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
                contentSearchPage.inputTitle(fileName);
                FacetedSearchPage resultPage = contentSearchPage.clickSearch().render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.DESCRIPTION),fileName);
        }

        /**
         * Searching file by description, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 8)
        public void testHighlightedSearchFolderByDescription() throws Exception
        {
                SearchBox search = docLib.getSearch();
                FacetedSearchPage resultPage = search.search(folderName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(folderName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
        }

        /**
         * Searching file by content, the result is highlighted
         * Search is performed from Advanced Search
         */
        @Test(groups = { "alfresco-one" }, priority = 9)
        public void testHighlightedSearchFileByContent() throws Exception
        {
                contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
                contentSearchPage.inputKeyword(fileName);
                FacetedSearchPage resultPage = contentSearchPage.clickSearch().render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.CONTENT));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.CONTENT), fileName);
        }

        /**
         * Searching file by "name:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 10)
        public void testHighlightedSearchByPropertyName() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                String SEARCH_TERM = "mycontent";
                FacetedSearchPage resultPage = search.search("name:" + SEARCH_TERM).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.NAME), SEARCH_TERM);
        }

        /**
         * Searching file by "title:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 11)
        public void testHighlightedSearchByPropertyTitle() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("title:" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.TITLE), fileName);

        }

        /**
         * Searching file by "description:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 12)
        public void testHighlightedSearchByPropertyDescription() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("description:" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.DESCRIPTION), fileName);

        }

        /**
         * Searching file by "content:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 13)
        public void testHighlightedSearchByPropertyContent() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("content:" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.CONTENT));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.CONTENT), fileName);
        }

        /**
         * Searching file by "cm_name:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 14)
        public void testHighlightedSearchByPropertyCm_Name() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("cm_name:" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertEquals(resultItem.getTextItemHighlighted(ItemHighlighted.NAME), fileName);
        }

        /**
         * Searching file by "TEXT:" property, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 15)
        public void testHighlightedSearchByPropertyTEXT() throws Exception
        {
                docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
                SearchBox search = docLib.getSearch();
                FacetedSearchPage resultPage = search.search("TEXT:" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));

        }

        /**
         * Searching file by "*" wildcard, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 16)
        public void testHighlightedSearchByWildcardAsterisk() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("mycontent*").render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.CONTENT));
        }

        /**
         * Searching file by "=" wildcard, the result is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 17)
        public void testHighlightedSearchByWildcardsEqual() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("=" + fileName).render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.CONTENT));
        }

        /**
         * Searching file by "?" wildcard, the result is highlighted
         * Search is performed from Live Search
         * <p>
         * For SOLR6, the test will fail due to https://issues.alfresco.com/jira/browse/SHA-1934
         */
        @Test(groups = { "alfresco-one" }, priority = 18)
        public void testHighlightedSearchByWildcardQuestionMark() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("??content").render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.NAME));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.TITLE));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.DESCRIPTION));
                Assert.assertTrue(resultItem.isItemHighlighted(ItemHighlighted.CONTENT));
        }

        /**
         * Searching by filename when is present elsewhere than name/title/description/content - location, user name, site name
         * nothing is highlighted
         * Search is performed from Live Search
         */
        @Test(groups = { "alfresco-one" }, priority = 19)
        public void testItemsNotHighlighted() throws Exception
        {
                SearchBox search = dashBoard.getSearch();
                FacetedSearchPage resultPage = search.search("mycontent").render();
                Assert.assertTrue(resultPage.hasResults());

                FacetedSearchResult resultItem = (FacetedSearchResult) resultPage.getResultByName(fileName);
                Assert.assertFalse(resultItem.isSiteHighlighted());
                Assert.assertFalse(resultItem.isLocationHighlighted());
                Assert.assertFalse(resultItem.isUserNameHighlighted());
        }

        /**
         * Searching by a term or query of a file that is not the name, title, description or content
         * the search term highlighting is not used.
         */
        @Test(groups = { "alfresco-one" }, priority = 20)
        public void testNoItemIsHighlightedSearchByCreatedProperty() throws Exception
        {
                docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
                SearchBox search = dashBoard.getSearch();
                String SEARCH_TERM = "created:today";
                FacetedSearchPage resultPage = search.search(SEARCH_TERM).render();
                Assert.assertTrue(resultPage.hasResults());

                List<SearchResult> facetedSearchResult = resultPage.getResults();
                for (SearchResult result : facetedSearchResult)
                {
                        FacetedSearchResult facetedResult = (FacetedSearchResult) result;
                        Assert.assertFalse(facetedResult.isAnyItemHighlighted(),
                                "Test failed when item is highlighted - no item should be highlighted!!");
                }
        }

        /**
         * Searching by a term or query of a file that is not the name, title, description or content
         * the search term highlighting is not used.
         */
        @Test(groups = { "alfresco-one" }, priority = 21)
        public void testNoItemIsHighlightedSearchByAsterisk() throws Exception
        {
                docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
                SearchBox search = docLib.getSearch();
                FacetedSearchPage resultPage = search.search("*").render();
                Assert.assertNotNull(resultPage);
                FacetedSearchScopeMenu menu = new FacetedSearchScopeMenu(driver, factoryPage);
                resultPage = menu.navigateMenu(FacetedSearchScopeMenu.ScopeMenuSelectedItemsMenu.SPECIFIC_SITE).render();
                List<SearchResult> facetedSearchResult = resultPage.getResults();
                Assert.assertTrue(resultPage.hasResults());

                for (SearchResult result : facetedSearchResult)
                {
                        FacetedSearchResult facetedResult = (FacetedSearchResult) result;
                        Assert.assertFalse(facetedResult.isAnyItemHighlighted(),
                                "Test failed when item is highlighted - no item should be highlighted!!");
                }
        }

        /**
         * Searching by a term or query of a file that is not the name, title, description or content
         * the search term highlighting is not used.
         */
        @Test(groups = {
                "alfresco-one" }, priority = 22)
        public void testGalleryViewIsNotHighlighted() throws Exception
        {
                docLib = siteActions.navigateToDocumentLibrary(driver, siteName).render();
                SearchBox search = docLib.getSearch();
                FacetedSearchPage resultPage = search.search("mycontent").render();
                Assert.assertTrue(resultPage.hasResults());

                resultPage.getView().selectViewByLabel("Gallery View");
                Assert.assertFalse(resultPage.getView().isAnyItemHighlightedGalleryView(),
                        "Test failed when item is highlighted - no item should be highlighted!!");
                resultPage.getView().selectViewByLabel("Detailed View");
        }
}




