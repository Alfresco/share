/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.test.wqs.web.search;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.WcmqsBlogPostPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.test.wqs.web.publications.PublicationActions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cristina Axinte on 12/22/2014.
 */

@Listeners(FailedTestListener.class)
public class SearchTests extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(SearchTests.class);
    private String testName;
    private String siteName;
    private String ipAddress;

    private String blogHouse5710;
    private String blogTechno5710;
    private String blogTrance5710;
    private String newsHouse5710;
    private String newsTechno5710;
    private String newsTrance5710;
    private String publicationHouse5710;
    private String publicationTechno5710;
    private String publicationTrance5710;

    private String blogHouse5711;
    private String blogTechno5711;
    private String blogTrance5711;
    private String newsHouse5711;
    private String newsTechno5711;
    private String newsTrance5711;
    private String publicationHouse5711;
    private String publicationTechno5711;
    private String publicationTrance5711;

    private String tagHouse = "House2";
    private String tagTechno = "techno2";
    private String tagTrance = "trance2";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        ipAddress = getIpAddress();
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        blogHouse5710 = "Blog House5710";
        blogTechno5710 = "Blog Techno5710";
        blogTrance5710 = "Blog Trance5710";
        newsHouse5710 = "News House5710";
        newsTechno5710 = "News Techno5710";
        newsTrance5710 = "News Trance5710";
        publicationHouse5710 = "Publication House5710";
        publicationTechno5710 = "Publication Techno5710";
        publicationTrance5710 = "Publication Trance5710";

        blogHouse5711 = "Blog house1 content 5711";
        blogTechno5711 = "Blog techno1 content 5711";
        blogTrance5711 = "Blog trance1 content 5711";
        newsHouse5711 = "News House1 content 5711";
        newsTechno5711 = "News Techno1 content 5711";
        newsTrance5711 = "News Trance1 content 5711";
        publicationHouse5711 = "Publication House1 content 5711";
        publicationTechno5711 = "Publication Techno1 content 5711";
        publicationTrance5711 = "Publication Trance1 content 5711";

        // User login
        // ---- Step 1 ----
        // ---- Step Action -----
        // WCM Quick Start is installed; - is not required to be executed automatically
        ShareUtil.loginAs(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Site "My Web Site" is created in Alfresco Share;
        siteService.create(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName, "", Site.Visibility.PUBLIC);

        // ---- Step 3 ----
        // ---- Step Action -----
        // WCM Quick Start Site Data is imported;
        SiteDashboardPage siteDashboardPage = (SiteDashboardPage) siteActions.openSiteDashboard(drone, siteName);
        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo(QUICK_START_EDITORIAL).selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname("localhost");
        documentPropertiesPage.clickSave();

        documentLibPage.render();
        documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        createArticles5710();
        waitForDocumentsToIndex();
        createArticles5711();
        waitForDocumentsToIndex();
        createArticles5712();
        waitForDocumentsToIndex();
        createArticlesSameContent();
        waitForDocumentsToIndex();

        ShareUtil.logout(drone);
        waitForWcmqsToLoad();
        loginToWqsFromHomePage();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAfterClass()
    {
        logger.info("Delete the site after all tests where run.");
        siteService.delete(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName);
        super.tearDown();

    }

    /*
     * AONE-5708: Search
     */
    @AlfrescoTest(testlink = "AONE-5708")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifySearch() throws Exception
    {
        String searchedText = "company";
        int expectedNumberOfSearchedItems = 11;
        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;
        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with company term and click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // Search Results list with the list of found items
        // Latest Blog Articles list
        // Pagination (Next page, Previous page, Page # of #)
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsSearchPage wcmqsSearchPage = wcmqsHomePage.searchText(searchedText).render();

        Assert.assertTrue(wcmqsSearchPage.verifyNumberOfSearchResultsHeader(10, expectedNumberOfSearchedItems, searchedText), "The header is not: Showing "
                + "10" + " of " + expectedNumberOfSearchedItems + " results for \"" + searchedText + "\" within the website...");
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty.");
        Assert.assertEquals(wcmqsSearchPage.getWcmqsSearchPagePagination(), "Page 1 of 2", "The pagination form is not (Page 1 of 2)");
    }

    /*
     * AONE-5709 Opening blog posts from Search page
     */
    @AlfrescoTest(testlink = "AONE-5709")
    @Test(groups = {"WQS", "EnterpriseOnly", "ProductBug"})
    public void openBlogPostFromSearch() throws Exception
    {
        //The left side of search page does not display Latest Blog Articles - bug or expected functionality

        String searchedText = "company";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;
        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with company term and click Search button near Search field;
        // ---- Expected results ----
        // Search page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsSearchPage wcmqsSearchPage = wcmqsHomePage.searchText(searchedText).render();

        Assert.assertNotEquals(wcmqsSearchPage.getTagSearchResults().size(), 0, "The Search Results list is empty");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Ethical funds blog post name in Latest Blog Articles section; - Click the first blog post name in Latest Blog Articles section (since many
        // blogs are added as precondition)
        // ---- Expected results ----
        // Blog post is opened successfully;
        String firstLatestBlog = wcmqsSearchPage.getLatestBlogArticles().get(0);
        wcmqsSearchPage.clickLatestBlogArticle(firstLatestBlog);
        WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone);
        blogPostPage.render();
        Assert.assertEquals(blogPostPage.getTitle(), firstLatestBlog);

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to Search page and click Company organises workshop blog post name in Latest Blog Articles section; - Click the second blog post name in
        // Latest Blog Articles section (since many blogs are added as precondition)
        // ---- Expected results ----
        // Blog post is opened successfully;
        blogPostPage.getDrone().navigateTo(drone.getPreviousUrl());
        String secondLatestBlog = wcmqsSearchPage.getLatestBlogArticles().get(1);
        wcmqsSearchPage.render();
        wcmqsSearchPage.clickLatestBlogArticle(secondLatestBlog);
        blogPostPage = new WcmqsBlogPostPage(drone);
        blogPostPage.render();
        Assert.assertEquals(blogPostPage.getTitle(), secondLatestBlog);

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to Search page and click Our top analyst's latest thoughts blog post name in Latest Blog Articles section; - Click the third blog post name in
        // Latest Blog Articles section (since many blogs are added as precondition)
        // ---- Expected results ----
        // Blog post is opened successfully;
        blogPostPage.getDrone().navigateTo(drone.getPreviousUrl());
        String thirdLatestBlog = wcmqsSearchPage.getLatestBlogArticles().get(2);
        wcmqsSearchPage.render();
        wcmqsSearchPage.clickLatestBlogArticle(thirdLatestBlog);
        blogPostPage = new WcmqsBlogPostPage(drone);
        blogPostPage.render();
        Assert.assertEquals(blogPostPage.getTitle(), thirdLatestBlog);
    }

    /*
     * AONE-5710 Searching items by name
     */
    @AlfrescoTest(testlink = "AONE-5710")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void searchItemsByName() throws Exception
    {
        String searchedText = "House";
        String searchedText2 = "techno";
        String searchedText3 = "trance";
        String newsTitle = "House prices face rollercoaster ride";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs;
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with House term;
        // ---- Expected results ----
        // Data is entered successfully;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blogs House
        // news article House
        // publication article House
        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(blogHouse5710), "Search Results list does not contain title: "
                + blogHouse5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(blogTechno5710), "Search Results list contains title: " + blogTechno5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(newsTitle), "Search Results list does not contain title: " + newsTitle);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(newsHouse5710), "Search Results list does not contain title: "
                + newsHouse5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(newsTechno5710), "Search Results list contains title: " + newsTechno5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationHouse5710), "Search Results list does not contain title: "
                + publicationHouse5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationTechno5710), "Search Results list contains title: "
                + publicationTechno5710);

        // ---- Step 4 ----
        // ---- Step action ----
        // Fill in Search field with techno
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText2);

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blog Techno
        // news article Techno
        // publication article Techno

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(blogTechno5710), "Search Results list does not contain title: "
                + blogTechno5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(blogHouse5710), "Search Results list contains title: " + blogHouse5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(newsTechno5710), "Search Results list does not contain title: "
                + newsTechno5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(newsHouse5710), "Search Results list contains title: " + newsHouse5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationTechno5710), "Search Results list does not contain title: "
                + publicationTechno5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationHouse5710), "Search Results list contains title: "
                + publicationHouse5710);

        // ---- Step 6 ----
        // ---- Step action ----
        // Fill in Search field with trance;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText3);

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blog Trance
        // news article Trance
        // publication article Trance;

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(blogTrance5710), "Search Results list does not contain title: "
                + blogTrance5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(blogHouse5710), "Search Results list contains title: " + blogHouse5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(newsTrance5710), "Search Results list does not contain title: "
                + newsTrance5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(newsHouse5710), "Search Results list contains title: " + newsHouse5710);
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationTrance5710), "Search Results list does not contain title: "
                + publicationTrance5710);
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains(publicationHouse5710), "Search Results list contains title: "
                + publicationHouse5710);

    }

    /*
     * AONE-5711 Searching items by content
     */
    @AlfrescoTest(testlink = "AONE-5711")
    @Test(groups = {"WQS", "EnterpriseOnly", "Bug"})
    public void searchItemsByContent() throws Exception
    {
        String searchedText = "House1";
        String searchedText2 = "techno1";
        String searchedText3 = "trance1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with House term
        // ---- Expected results ----
        // Data is entered successfully;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blog1
        // article1
        // publication1

        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5711"), "Search Results list does not contain title: "
                + "Blog H5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Te5711"), "Search Results list contains title: " + "Blog Te5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5711"), "Search Results list does not contain title: "
                + "News H5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News Te5711"), "Search Results list contains title: " + "News Te5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5711"), "Search Results list does not contain title: "
                + "Publ H5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Te5711"), "Search Results list contains title: " + "Publ Te5711");

        // ---- Step 4 ----
        // ---- Step action ----
        // Fill in Search field with techno
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText2);

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blog2
        // article2
        // publication2

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Te5711"), "Search Results list does not contain title: "
                + "Blog Te5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5711"), "Search Results list contains title: " + "Blog H5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News Te5711"), "Search Results list does not contain title: "
                + "News Te5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5711"), "Search Results list contains title: " + "News H5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Te5711"), "Search Results list does not contain title: "
                + "Publ Te5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5711"), "Search Results list contains title: " + "Publ H5711");

        // ---- Step 6 ----
        // ---- Step action ----
        // Fill in Search field with trance;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText3);

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // blog3
        // aritcle3
        // publication3

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Tr5711"), "Search Results list does not contain title: "
                + "Blog Tr5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5711"), "Search Results list contains title: " + "Blog H5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News Tr5711"), "Search Results list does not contain title: "
                + "News Tr5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5711"), "Search Results list contains title: " + "News H5711");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Tr5711"), "Search Results list does not contain title: "
                + "Publ Tr5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5711"), "Search Results list contains title: " + "Publ H5711");

    }

    /*
     * AONE-5713 Pagination on Search page
     */
    @AlfrescoTest(testlink = "AONE-5713")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void paginationSearchPage() throws Exception
    {
        String searchedText = "test";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with "test" data and click Search button near Search field;
        // ---- Expected results ----
        // Items are displayed;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsSearchPage wcmqsSearchPage = wcmqsHomePage.searchText(searchedText).render();
        ArrayList<String> searchResults = wcmqsSearchPage.getTagSearchResults();
        Assert.assertNotEquals(searchResults.size(), 0, "The Search Results list is empty");
        Assert.assertTrue(searchResults.contains("News9test 5713"), "Search Results list does not contain title: "
                + "News9test 5713");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Next page button;
        // ---- Expected results ----
        // Next page is opened;

        wcmqsSearchPage = wcmqsSearchPage.clickNextPage().render();
        Assert.assertTrue(wcmqsSearchPage.isPreviousButtonDisplayed());

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Previous page button;
        // ---- Expected results ----
        // Previous page is opened;

        wcmqsSearchPage = wcmqsSearchPage.clickPrevPage().render();
        Assert.assertTrue(wcmqsSearchPage.isNextButtonDisplayed());

    }

    /*
     * AONE-5712 Searching items by tags
     */
    @AlfrescoTest(testlink = "AONE-5712")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void searchItemsByTags() throws Exception
    {
        String searchedText = "House2";
        String searchedText2 = "techno2";
        String searchedText3 = "trance2";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with House term
        // ---- Expected results ----
        // Data is entered successfully;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // post1
        // news1
        // paper1

        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5712"), "Search Results list does not contain title: "
                + "Blog H5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Te5712"), "Search Results list contains title: " + "Blog Te5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5712"), "Search Results list does not contain title: "
                + "News H5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News Te5712"), "Search Results list contains title: " + "News Te5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5712"), "Search Results list does not contain title: "
                + "Publ H5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Te5712"), "Search Results list contains title: " + "Publ Te5712");

        // ---- Step 4 ----
        // ---- Step action ----
        // Fill in Search field with techno
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText2);

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // post2
        // news2
        // paper2

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Te5712"), "Search Results list does not contain title: "
                + "Blog Te5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5712"), "Search Results list contains title: " + "Blog H5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News Te5712"), "Search Results list does not contain title: "
                + "News Te5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5712"), "Search Results list contains title: " + "News H5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Te5712"), "Search Results list does not contain title: "
                + "Publ Te5711");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5712"), "Search Results list contains title: " + "Publ H5712");

        // ---- Step 6 ----
        // ---- Step action ----
        // Fill in Search field with trance;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsSearchPage.searchText(searchedText3);

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // The following items are displayed:
        // post3
        // news3
        // paper3

        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotNull(wcmqsSearchPage.getTagSearchResults(), "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog Tr5712"), "Search Results list does not contain title: "
                + "Blog Tr5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Blog H5712"), "Search Results list contains title: " + "Blog H5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("News Tr5712"), "Search Results list does not contain title: "
                + "News Tr5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("News H5712"), "Search Results list contains title: " + "News H5712");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ Tr5712"), "Search Results list does not contain title: "
                + "Publ Tr5712");
        Assert.assertFalse(wcmqsSearchPage.getTagSearchResults().toString().contains("Publ H5712"), "Search Results list contains title: " + "Publ H5712");

    }

    /*
     * AONE-5714 Empty search
     */
    @AlfrescoTest(testlink = "AONE-5714")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void emptySearch() throws Exception
    {
        String searchedText = "";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Empty Search field;
        // ---- Expected results ----
        // Data is entered successfully;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // No items are found;

        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertEquals(wcmqsSearchPage.getTagSearchResults().size(), 0, "The Search Results list is not empty");

    }

    /*
     * AONE-5715 Wildcard search
     * Jira issue #MNT-13143
     */
    @AlfrescoTest(testlink = "AONE-5715")
    @Test(groups = {"WQS", "EnterpriseOnly", "ProductBug"})
    public void wildcardSearch() throws Exception
    {
        String searchedText = "*glo?al*";
        // String searchedText = "*global*";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with wildcards;
        // ---- Expected results ----
        // Data is entered successfully;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // Data is proceeded correctly without errors;

        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertNotEquals(wcmqsSearchPage.getTagSearchResults().size(), 0, "The Search Results list is empty");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Global car industry"), "Search Results list does not contain title: "
                + "Global car industry");
    }

    /*
     * AONE-5716 Too long data search
     */
    @AlfrescoTest(testlink = "AONE-5716")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void longDataSearch() throws Exception
    {
        String searchedText;

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill in Search field with more than 1024 symbols;
        // ---- Expected results ----
        // Data is entered successfully;

        searchedText = generateRandomStringOfLength(1024);
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.searchText(searchedText);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Search button near Search field;
        // ---- Expected results ----
        // Data was cut, only 100 symbols can be entered;

        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        String actualSearchedText = wcmqsSearchPage.getTextFromSearchField();
        String expectedSearchedText = searchedText.substring(0, 100);
        Assert.assertEquals(actualSearchedText, expectedSearchedText, "Actual searched text is " + actualSearchedText + ", but expected is "
                + expectedSearchedText);
        Assert.assertEquals(actualSearchedText.length(), 100, "Length of searched text is not 100 characters");
    }


    private void createArticles5710() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // Several articles are created in News, Publications, Blogs components:
        // * blogs: house, techno, trance with any content
        navigateToFolderAndCreateContent("blog", "BlogH5710.html", "content blog h1", blogHouse5710).render();
        navigateToFolderAndCreateContent("blog", "BlogTe5710.html", "content blog te1", blogTechno5710).render();
        drone.refresh();
        navigateToFolderAndCreateContent("blog", "BlogTr5710.html", "content blog tr1", blogTrance5710).render();

        // * news: house, techno, trance
        navigateToFolderAndCreateContent("news", "NewsH5710.html", "content news h1", newsHouse5710).render();
        navigateToFolderAndCreateContent("news", "NewsTr5710.html", "content news te1", newsTechno5710).render();
        navigateToFolderAndCreateContent("news", "NewsTe5710.html", "content news tr1", newsTrance5710).render();

        // * publications (e.g. rename custom files via Share): house, techno, house techno
        navigateToFolderAndCreateContent("publications", "PublicationH5710.html", "content publication h1",
                publicationHouse5710).render();
        navigateToFolderAndCreateContent("publications", "PublicationTe5710.html", "content publication te1",
                publicationTechno5710).render();
        navigateToFolderAndCreateContent("publications", "PublicationTr5710.html", "content publication tr1",
                publicationTrance5710).render();
    }

    private void createArticles5711() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // Several articles are created in News, Publications, Blogs components:
        // * blogs with content: blog1 with content "house", blog2 with content "techno", blog3 with content "trance"
        navigateToFolderAndCreateContent("blog", "BlogH5711.html", blogHouse5711, "Blog H5711").render();
        navigateToFolderAndCreateContent("blog", "BlogTe5711.html", blogTechno5711, "Blog Te5711").render();
        navigateToFolderAndCreateContent("blog", "BlogTr5711.html", blogTrance5711, "Blog Tr5711").render();

        // * news articles with content: article1 with content "house", article2 with content "techno", article3 with content "trance"
        navigateToFolderAndCreateContent("news", "NewsH5711.html", newsHouse5711, "News H5711").render();
        navigateToFolderAndCreateContent("news", "NewsTe5711.html", newsTechno5711, "News Te5711").render();
        navigateToFolderAndCreateContent("news", "NewsTr5711.html", newsTrance5711, "News Tr5711").render();

        // * publication articles with content: publication1 with content "house", publication2 with content "techno", publication3 with content "trance"
        navigateToFolderAndCreateContent("publications", "PublH5711.html", publicationHouse5711, "Publ H5711").render();
        navigateToFolderAndCreateContent("publications", "PublTe5711.html", publicationTechno5711, "Publ Te5711").render();
        navigateToFolderAndCreateContent("publications", "PublTr5711.html", publicationTrance5711, "Publ Tr5711").render();

    }

    private void createArticles5712() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // Several articles are created in News, Publications, Blogs components:
        // * blogs with tags: post1 with tag "house", post2 with tag "techno",post3 with tag "trance"
        navigateToFolderAndCreateContent("blog", "BlogH5712.html", "blog content H5712", "Blog H5712").render();
        navigateToFolderAndCreateContent("blog", "BlogTe5712.html", "blog content Te5712", "Blog Te5712").render();
        DocumentLibraryPage documentLibPage = navigateToFolderAndCreateContent("blog", "BlogTr5712.html", "blog content Tr5712", "Blog Tr5712").render();

        String blog_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "blog";
        documentLibPage = siteActions.navigateToFolder(drone, blog_folder_path).render();
        documentLibPage.getFileDirectoryInfo("BlogH5712.html").addTag(tagHouse);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("BlogTe5712.html").addTag(tagTechno);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("BlogTr5712.html").addTag(tagTrance);
        drone.refresh();

        // * news articles with tags: news1 with tag "house", news2 with tag "techno", news3 with tag "trance"
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        navigateToFolderAndCreateContent("news", "NewsH5712.html", "news content H5712", "News H5712").render();
        navigateToFolderAndCreateContent("news", "NewsTe5712.html", "news content Te5712", "News Te5712").render();
        documentLibPage = navigateToFolderAndCreateContent("news", "NewsTr5712.html", "news content Tr5712", "News Tr5712").render();

        String news_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "news";
        documentLibPage = siteActions.navigateToFolder(drone, news_folder_path).render();
        documentLibPage.getFileDirectoryInfo("NewsH5712.html").addTag(tagHouse);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("NewsTe5712.html").addTag(tagTechno);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("NewsTr5712.html").addTag(tagTrance);
        drone.refresh();

        // * publication articles with tag: paper1 with tag "house", paper2 with tag "techno", paper3 with tag "trance"
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        navigateToFolderAndCreateContent("publications", "PublH5712.html", "publ content H5712", "Publ H5712").render();
        navigateToFolderAndCreateContent("publications", "PublTe5712.html", "publ content Te5712", "Publ Te5712").render();
        documentLibPage = navigateToFolderAndCreateContent("publications", "PublTr5712.html", "publ content Tr5712", "Publ Tr5712").render();

        String publications_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "publications";
        documentLibPage = siteActions.navigateToFolder(drone, publications_folder_path).render();
        documentLibPage.getFileDirectoryInfo("PublH5712.html").addTag(tagHouse);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("PublTe5712.html").addTag(tagTechno);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("PublTr5712.html").addTag(tagTrance);
        drone.refresh();
    }

    private void createArticlesSameContent() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // More than 20 article items with content "test" are created in 'news' and 'blogs' folders;

        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String name;
        // create 10 blogs in "news" folder
        for (int i = 0; i < 10; i++)
        {
            name = "News" + i;
            navigateToFolderAndCreateContent("news", name + "T5713.html", name + " content", name + "test 5713").render();
        }

        // create 10 blogs in "blog" folder
        for (int i = 0; i < 10; i++)
        {
            name = "Blog" + i;
            navigateToFolderAndCreateContent("blog", name + "T5713.html", name + " content", name + "test 5713").render();
        }

    }

}
