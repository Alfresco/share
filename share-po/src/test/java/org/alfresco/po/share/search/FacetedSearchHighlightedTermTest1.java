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

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.search.FacetedSearchScopeMenu.ScopeMenuSelectedItemsMenu;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.steps.CommonActions;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration test to verify that highlight is working on Search Results page.
 *
 * @author CorinaZ
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class FacetedSearchHighlightedTermTest1 extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);
    protected String siteName;
    protected String fileName;
    protected String folderName;
    protected SharePage sharepage;

    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        try
        {
            loginAs(username, password);

            // create one file to the repository with the same name, title, description and content
            siteName = "mycontent-" + System.currentTimeMillis();
            fileName = "mycontent" + System.currentTimeMillis();
            folderName = "mycontentf" + System.currentTimeMillis();

            // create one site
            siteUtil.createSite(driver, username, password, siteName, "description", "Public");

            // create one file in the site with the same name, title, description and content
            siteActions.navigateToDocumentLibrary(driver, siteName);

            // create one folder the same name, title and description
            siteActions.createFolder(driver, folderName, folderName, folderName);

            // create one file in the folder with the same name, title, description and content
            siteActions.navigateToFolder(driver, folderName);

            ContentDetails contentDetails1 = new ContentDetails();
            contentDetails1.setName(fileName);
            contentDetails1.setTitle(fileName);
            contentDetails1.setDescription(fileName);
            contentDetails1.setContent(fileName);

            siteActions.createContent(driver, contentDetails1, ContentType.PLAINTEXT);

        }
        catch (Exception e)
        {
            saveScreenShot("HighlightContentUpload");
            logger.error("Cannot create content to site ", e);
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
    @Test(groups = { "alfresco-one" }, priority = 1)
    public void testHighlightedSearchFileByName() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, fileName, fileName, true, 3));
        
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true),
                "Highlighting results do not match");

    }

    /**
     * Searching file by name, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 2)
    public void testHighlightedSearchFileByNameAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.NAME.getSearchKeys(), fileName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, fileName, CommonActions.SelectItem.CONTENT, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true),
                "Highlighting results do not match");

    }

    /**
     * Searching folder by name, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 3)
    public void testHighlightedSearchFolderByNameAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.NAME.getSearchKeys(), folderName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, folderName, CommonActions.SelectItem.FOLDER, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, folderName, ItemHighlighted.NAME, folderName, true),
                "Highlighting results do not match");

    }

    /**
     * Searching file by title, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 4)
    public void testHighlightedSearchFileByTitleAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.TITLE.getSearchKeys(), fileName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, fileName, CommonActions.SelectItem.CONTENT, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.TITLE, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching folder by title, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 5)
    public void testHighlightedSearchFolderByTitleAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.TITLE.getSearchKeys(), folderName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, folderName, CommonActions.SelectItem.FOLDER, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, folderName, ItemHighlighted.TITLE, folderName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by description, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 6)
    public void testHighlightedSearchFileByDescription() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by description, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 7)
    public void testHighlightedSearchFileByDescriptionAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.DESCRIPTION.getSearchKeys(), fileName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, fileName, CommonActions.SelectItem.CONTENT, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by description, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 8)
    public void testHighlightedSearchFolderByDescriptionAdvancedSearch() throws Exception
    {

        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.DESCRIPTION.getSearchKeys(), folderName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, folderName, CommonActions.SelectItem.FOLDER, searchInfo, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, folderName, ItemHighlighted.DESCRIPTION, folderName, true),
                "Highlighting results do not match");

    }

    /**
     * Searching file by content, the result is highlighted
     * Search is performed from Advanced Search
     */
    @Test(groups = { "alfresco-one" }, priority = 9)
    public void testHighlightedSearchFileByContentAdvancedSearch() throws Exception
    {
        Map<String, String> searchInfo = new HashMap<>();
        {
            searchInfo.put(SearchKeys.KEYWORD.getSearchKeys(), fileName);
        }

        Assert.assertTrue(siteActions.checkAdvancedSearchResultsWithRetry(driver, fileName, CommonActions.SelectItem.CONTENT, searchInfo, true, 3));
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.CONTENT, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "name:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 10)
    public void testHighlightedSearchByPropertyName() throws Exception
    {
        String SEARCH_TERM = "mycontent";
        
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "name:" + SEARCH_TERM, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, SEARCH_TERM, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "title:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 11)
    public void testHighlightedSearchByPropertyTitle() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "title:" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.TITLE, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "description:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 12)
    public void testHighlightedSearchByPropertyDescription() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "title:" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "content:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 13)
    public void testHighlightedSearchByPropertyContent() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "content:" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.CONTENT, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "cm_name:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 14)
    public void testHighlightedSearchByPropertyCm_Name() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "cm_name:" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true),
                "Highlighting results do not match");
    }

    /**
     * Searching file by "TEXT:" property, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 15)
    public void testHighlightedSearchByPropertyTEXT() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "TEXT:" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true),
                "Highlighting results do not match");

    }

    /**
     * Searching file by "*" wildcard, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 16)
    public void testHighlightedSearchByWildcardAsterisk() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "mycontent*", fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.TITLE, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.CONTENT, fileName, true), "Highlighting results do not match");
    }

    /**
     * Searching file by "=" wildcard, the result is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 17)
    public void testHighlightedSearchByWildcardsEqual() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "=" + fileName, fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.TITLE, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, fileName, true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.CONTENT, fileName, true), "Highlighting results do not match");
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
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "??content", fileName, true, 3));

        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.NAME, "mycontent", true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.TITLE, "mycontent", true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.DESCRIPTION, "mycontent", true), "Highlighting results do not match");
        Assert.assertTrue(siteActions.checkSearchResultHighlighting(driver, fileName, ItemHighlighted.CONTENT, "mycontent", true), "Highlighting results do not match");
    }

    /**
     * Searching by filename when is present elsewhere than name/title/description/content - location, user name, site name
     * nothing is highlighted
     * Search is performed from Live Search
     */
    @Test(groups = { "alfresco-one" }, priority = 19)
    public void testItemsNotHighlighted() throws Exception
    {
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "mycontent", fileName, true, 3));

        FacetedSearchResult resultItem = siteActions.getFacetedSearchResult(driver, fileName);

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
        siteActions.navigateToDocumentLibrary(driver, siteName).render();

        FacetedSearchPage resultPage = siteActions.search(driver, "created:today").render();

        Assert.assertTrue(resultPage.hasResults());

        List<SearchResult> facetedSearchResult = resultPage.getResults();
        for (SearchResult result : facetedSearchResult)
        {
            FacetedSearchResult facetedResult = (FacetedSearchResult) result;
            Assert.assertFalse(facetedResult.isAnyItemHighlighted(), "Test failed when item is highlighted - no item should be highlighted!!");
        }
    }

    /**
     * Searching by a term or query of a file that is not the name, title, description or content
     * the search term highlighting is not used.
     */
    @Test(groups = { "alfresco-one" }, priority = 21)
    public void testNoItemIsHighlightedSearchByAsterisk() throws Exception
    {
        siteActions.navigateToDocumentLibrary(driver, siteName).render();

        FacetedSearchPage resultPage = siteActions.search(driver, "*").render();

        // Select Scope = SITE
        resultPage = resultPage.selectSearchScope(ScopeMenuSelectedItemsMenu.SPECIFIC_SITE).render();
        List<SearchResult> facetedSearchResult = resultPage.getResults();
        Assert.assertTrue(resultPage.hasResults());

        for (SearchResult result : facetedSearchResult)
        {
            FacetedSearchResult facetedResult = (FacetedSearchResult) result;
            Assert.assertFalse(facetedResult.isAnyItemHighlighted(), "Test failed when item is highlighted - no item should be highlighted!!");
        }
    }

    /**
     * Searching by a term or query of a file that is not the name, title, description or content
     * the search term highlighting is not used.
     */
    @Test(groups = { "alfresco-one" }, priority = 22)
    public void testGalleryViewIsNotHighlighted() throws Exception
    {
        siteActions.navigateToDocumentLibrary(driver, siteName).render();

        FacetedSearchPage resultPage = siteActions.search(driver, "mycontent").render();
        Assert.assertTrue(resultPage.hasResults());

        resultPage = resultPage.getView().selectViewByLabel("Gallery View").render();
        Assert.assertFalse(resultPage.getView().isAnyItemHighlightedGalleryView(), "Test failed when item is highlighted - no item should be highlighted!!");

        resultPage = resultPage.getView().selectViewByLabel("Detailed View").render();
        Assert.assertTrue(resultPage.hasResults());
    }
}
