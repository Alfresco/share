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
package org.alfresco.test.wqs.web.news;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;


/**
 * Created by Cristina Axinte on 01/12/2015.
 */

@Listeners(FailedTestListener.class)
public class NewsComponent extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(NewsComponent.class);
    private String siteName;
    private String ipAddress;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        String testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        ipAddress = getIpAddress();
        tag1 = "test1";
        tag2 = "test2";
        tag3 = "test3";
        tag4 = "test4";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

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
        documentLibPage.selectFolder("Alfresco Quick Start");
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname("localhost");
        documentPropertiesPage.clickSave();

        siteActions.openSiteDashboard(drone, siteName).render();
        // Data Lists component is added to the site
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(addPageTypes).render();

        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        dataPrep_AONE_5706();
        dataPrep_AONE_5694();
        dataPrep_AONE_5700();

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
     * AONE-5686 News
     */
    @AlfrescoTest(testlink = "AONE-5686")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyNews() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify News drop-down list;
        // ---- Expected results ----
        // The following items are displayed:
        // Global Economy
        // Companies
        // Markets

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        List<String> links = new ArrayList<String>();
        List<ShareLink> shareLinks = homePage.getAllFoldersFromMenu("news");
        for (ShareLink sharelink : shareLinks)
        {
            links.add(sharelink.getHref());
        }
        assertThat("Folder list contains correct news folders", links, hasItem(containsString("companies")));
        assertThat("Folder list contains correct news folders", links, hasItem(containsString("markets")));
        assertThat("Folder list contains correct news folders", links, hasItem(containsString("global")));

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Global Economy link;
        // ---- Expected results ----
        // Global Economy (Our round-up of the latest news on the global economy) page is opened;

        homePage.openNewsPageFolder("global").render();
        assertThat("Reached page is global economy", homePage.getTitle(), containsString("Global Economy"));

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Companies link;
        // ---- Expected results ----
        // Company News (Latest company news) page is opened;

        homePage.clickWebQuickStartLogo().render();
        homePage.openNewsPageFolder("companies").render();
        assertThat("Reached page is companies", homePage.getTitle(), containsString("Companies"));

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Markets link;
        // ---- Expected results ----
        // Markets (Latest news from the financial markets) page is opened;

        homePage.clickWebQuickStartLogo().render();
        homePage.openNewsPageFolder("markets").render();
        assertThat("Reached page is markets", homePage.getTitle(), containsString("Markets"));

    }

    /*
    * AONE-5687 News page
    */
    @AlfrescoTest(testlink = "AONE-5687")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyNewsPage() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click News link;
        // ---- Expected results ----
        // News page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage wcmqsNewsPage = homePage.selectMenu("News").render();
        Assert.assertTrue(wcmqsNewsPage instanceof WcmqsNewsPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify News page;
        // ---- Expected results ----
        //  The following items are displayed:
        // * Articles (Article name link, From <component name> link, Created date, 1 paragraph, Article picture preview)
        // * More News (Articles names links)

        Assert.assertTrue(wcmqsNewsPage.isRightTitlesDisplayed());
        Assert.assertTrue(wcmqsNewsPage.isFeatureTitleDisplayed());
    }


    /*
    * AONE-5688 Opening articles from News page
    */
    @AlfrescoTest(testlink = "AONE-5688")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void openArticlesNewsPage() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click News link;
        // ---- Expected results ----
        // News page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage wcmqsNewsPage = homePage.selectMenu("News").render();
        Assert.assertTrue(wcmqsNewsPage instanceof WcmqsNewsPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click "Europe dept concerns ease but bank fears remain" link;
        // ---- Expected results ----
        //   Article is opened successfully;

        // TODO update test from TestLink. Sample articles no longer match

        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsNewsPage.clickLinkByTitle(WcmqsNewsPage.EUROPE_DEPT_CONCERNS).render();
        Assert.assertTrue(wcmqsNewsArticleDetails.isNewsArticleImageDisplayed());
        wcmqsNewsPage = wcmqsNewsArticleDetails.selectMenu("News").render();

        List<ShareLink> articles = wcmqsNewsPage.getHeadlineTitleNews();
        for (ShareLink article : articles)
        {
            wcmqsNewsArticleDetails = article.click().render();
            Assert.assertTrue(wcmqsNewsArticleDetails.isNewsArticleImageDisplayed());
            wcmqsNewsArticleDetails.selectMenu("News").render();
        }

    }


    /*
   * AONE-5689 Opening components from News page (links in articles)
   */
    @AlfrescoTest(testlink = "AONE-5689")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void openArticlesNewsPageLinks() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click News link;
        // ---- Expected results ----
        // News page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage wcmqsNewsPage = homePage.selectMenu("News").render();
        Assert.assertTrue(wcmqsNewsPage instanceof WcmqsNewsPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // For "Europe dept concerns ease but bank fears remain" click "Global economy" link;
        // ---- Expected results ----
        //  Component is opened successfully;

        wcmqsNewsPage = wcmqsNewsPage.clickCategoryLinkByTitle(WcmqsNewsPage.FTSE_1000).render();
        Assert.assertEquals(wcmqsNewsPage.getCategoryTitle(), "Global Economy");
        wcmqsNewsPage.selectMenu("News").render();

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to News page and click "Global economy" link for  "Media Consult new site coming out in September";
        // ---- Expected results ----
        //   Component is opened successfully;

        wcmqsNewsPage = wcmqsNewsPage.clickCategoryLinkByTitle(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY).render();
        Assert.assertEquals(wcmqsNewsPage.getCategoryTitle(), "Company News");
        wcmqsNewsPage.selectMenu("News").render();

        wcmqsNewsPage = wcmqsNewsPage.clickCategoryLinkByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS).render();
        Assert.assertEquals(wcmqsNewsPage.getCategoryTitle(), "Company News");
        wcmqsNewsPage.selectMenu("News").render();

        wcmqsNewsPage = wcmqsNewsPage.clickCategoryLinkByTitle(WcmqsNewsPage.INVESTORS_FEAR).render();
        Assert.assertEquals(wcmqsNewsPage.getCategoryTitle(), "Markets");
        wcmqsNewsPage.selectMenu("News").render();

        wcmqsNewsPage = wcmqsNewsPage.clickCategoryLinkByTitle(WcmqsNewsPage.HOUSE_PRICES).render();
        Assert.assertEquals(wcmqsNewsPage.getCategoryTitle(), "Markets");
        wcmqsNewsPage.selectMenu("News").render();
    }


    /*
    *  AONE-5690:Opening articles from News page (More news section)
    */
    @AlfrescoTest(testlink = "AONE-5690")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void openArticlesNewsPageMore() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click News link;
        // ---- Expected results ----
        // News page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage wcmqsNewsPage = homePage.selectMenu("News").render();
        Assert.assertTrue(wcmqsNewsPage instanceof WcmqsNewsPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click "Europe dept concerns ease but bank fears remain" link from "More news";
        // ---- Expected results ----
        // Article  is opened successfully;

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to News page and click "Media Consult new site coming out in September" link from "More news";
        // ---- Expected results ----
        //   Component is opened successfully;

        List<ShareLink> rightTitleNews = wcmqsNewsPage.getRightHeadlineTitleNews();
        for (ShareLink rightTitle : rightTitleNews)
        {
            try
            {
                rightTitle.openLink();
            }
            catch (StaleElementReferenceException e)
            {
                rightTitle = new ShareLink(wcmqsNewsPage.resolveRightTitleNewsStale(rightTitle.getDescription()), drone);
                rightTitle.openLink();
            }

            WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
            Assert.assertEquals(wcmqsNewsArticleDetails.getTitleOfNewsArticle(), rightTitle.getDescription());
            wcmqsNewsArticleDetails.selectMenu("News").render();

        }
    }

    /*
    *  AONE-5691:Opening articles from News page (More news section)
    */
    @AlfrescoTest(testlink = "AONE-5691")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyGlobalEconomy() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Global Economy page from News menu;
        // ---- Expected results ----
        // The following items are displayed:* Subscribe to RSS link* Articles list (Article name link, Creation date, 1 paragraph, image preview)
        // * Related articles list (list of articles names links) * Section tags (the list of tags links with number of tags specified in brackets)

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL).render();

        Assert.assertTrue(newsPage.isRSSLinkDisplayed());
        Assert.assertEquals(newsPage.getHeadlineTitleNews().get(0).getDescription(), WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
        Assert.assertTrue(newsPage.isDateTimeNewsPresent("article4"));
        Assert.assertTrue(newsPage.isImageLinkForTitleDisplayed(WcmqsNewsPage.EUROPE_DEPT_CONCERNS));
        Assert.assertTrue(newsPage.isRightTitlesDisplayed());
        Assert.assertTrue(newsPage.isSectionTagsDisplayed());

    }

    /*
    *   AONE-5692:News - Global economy articles (v 3.4)
    */
    @AlfrescoTest(testlink = "AONE-5692")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyGlobalEconomyArticles() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Global Economy page from News menu;
        // ---- Expected results ----
        // Global Economy page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click "Europe dept concerns ease but bank fears remain" article name;
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed: * Article name  * From <component name> link
        // * Article picture * Created date  * Tags  * AWE actions (Edit, Create Article, Delete icons)

        newsPage.clickLinkByTitle(WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
        WcmqsNewsArticleDetails newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.EUROPE_DEPT_CONCERNS, "Article title is not " + WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
        Assert.assertEquals(newsDetailsPage.getFromLinkName(), "Global Economy", "The from link is not " + WcmqsNewsPage.GLOBAL);
        Assert.assertTrue(newsDetailsPage.isNewsArticleImageDisplayed(), "Article picture is not displayed.");
        Assert.assertTrue(newsDetailsPage.isTagsSectionDisplayed(), "Tag section is not displayed.");
        Assert.assertTrue(newsDetailsPage.isEditButtonDisplayed(), "Edit button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isCreateButtonDisplayed(), "Create button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isDeleteButtonDisplayed(), "Delete button is not displayed.");

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Global Ecomony link in "From" section;
        // ---- Expected results ----
        // User is returned to Global economy page;

        newsPage = newsDetailsPage.clickComponentLinkFromSection(WcmqsNewsPage.GLOBAL);
        newsPage.render();

        // ---- Step 5 ----
        // ---- Step action ----
        // Click "Media Consult new site coming out in September" article name;
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed: * Article name  * From <component name> link
        // * Article picture * Created date  * Tags  * AWE actions (Edit, Create Article, Delete icons)

        newsPage.clickLinkByTitle(WcmqsNewsPage.FTSE_1000).render();
        newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.FTSE_1000, "Article title is not " + WcmqsNewsPage.FTSE_1000);
        Assert.assertEquals(newsDetailsPage.getFromLinkName().toLowerCase(), "global economy", "The from link is not " + WcmqsNewsPage.GLOBAL);
        Assert.assertTrue(newsDetailsPage.isNewsArticleImageDisplayed(), "Article picture is not displayed.");
        Assert.assertTrue(newsDetailsPage.isTagsSectionDisplayed(), "Tag section is not displayed.");
        Assert.assertTrue(newsDetailsPage.isEditButtonDisplayed(), "Edit button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isCreateButtonDisplayed(), "Create button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isDeleteButtonDisplayed(), "Delete button is not displayed.");

        // ---- Step 6 ----
        // ---- Step action ----
        // Click 'Company Name' link in the signature for quotation;
        // ---- Expected results ----
        // User is redirected to "Minicards are now available" article in "Companies" component;

        //TODO The link from signature is not redirected to Minicards article - old functionality. Test link should be updated
    }

    /*
   *   AONE-5693:News - Global economy - Related Articles
   */
    @AlfrescoTest(testlink = "AONE-5693")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyGlobalEconomyRelatedArticles() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Global Economy page from News menu;
        // ---- Expected results ----
        // Global Economy page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click "Minicards are now available" article name in "Related Articles";
        // The only article available in Related Article is Fresh flight to Swiss franc as Europe's bond strains return
        // ---- Expected results ----
        //  Article is opened successfully and displayed correctly;

        newsPage.getRightHeadlineTitleNews().get(0).click();
        WcmqsNewsArticleDetails newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS, "Article title is not " + WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);

    }

    /*
    *   AONE-5694:News - Global economy - Section Tags
    */
    @AlfrescoTest(testlink = "AONE-5694")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyGlobalEconomySectionTags() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Global Economy page from News menu;
        // ---- Expected results ----
        // Global Economy page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);
        newsPage.render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify Section Tags menu;
        // ---- Expected results ----
        // The list of tags is dislpayed with number of tags (only items for current component are displayed):
        // test 1 (2)
        // test 2 (1)

        String expectedTag1 = tag1 + " (2)";
        String expectedTag2 = tag3 + " (2)";
        String expectedTag3 = tag2 + " (1)";
        Assert.assertFalse(newsPage.getTagList().contains("None"), "List of tags link is displayed and it is empty.");
        Assert.assertEquals(newsPage.getTagList().size(), 3, "List of tags does not contain only 2 items");
        Assert.assertEquals(newsPage.getTagList().get(0), expectedTag1, "List of tags does not contain tag: " + expectedTag1);
        Assert.assertEquals(newsPage.getTagList().get(1), expectedTag2, "List of tags does not contain tag: " + expectedTag2);
        Assert.assertEquals(newsPage.getTagList().get(2), expectedTag3, "List of tags does not contain tag: " + expectedTag3);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click test 1 tag link;
        // ---- Expected results ----
        // Two articles are dislpayed;

        newsPage.getTagLinks().get(0).click();
        WcmqsSearchPage searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 2, "List of search results does not contain only 2 articles");

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to  Global Economy  page and click test 2 tag link;
        // ---- Expected results ----
        // One article is displayed;

        searchPage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);
        newsPage.getTagLinks().get(1).click();
        searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 1, "List of search results does not contain only one article");

    }


    /*
    *    AONE-5695:News - Global economy - Subscribe to RSS
    */
    @AlfrescoTest(testlink = "AONE-5695")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyGlobalEconomyRss() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Global Economy page from News menu;
        // ---- Expected results ----
        // Global Economy page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Subscribe to RSS icon;
        // ---- Expected results ----
        // Subscribe page is opened;

        RssFeedPage rssFeedPage = newsPage.clickRssLink().render();
        Assert.assertTrue(rssFeedPage.isSubscribePanelDisplay());

        // ---- Step 4 ----
        // ---- Step action ----
        // Select necessary details and subscribe to RSS;
        // ---- Expected results ----
        // RSS is successfully selected;


        // ---- Step 5 ----
        // ---- Step action ----
        // Update RSS feed;
        // ---- Expected results ----
        // All articles are dislpayed in RSS correctly;

        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.EUROPE_DEPT_CONCERNS));
        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.FTSE_1000));
    }


    /*
    *  AONE-5696:News - Companies
    */
    @AlfrescoTest(testlink = "AONE-5696")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCompanies() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Companies page from News menu;
        // ---- Expected results ----
        // The following items are displayed:* Subscribe to RSS link* Articles list (Article name link, Creation date, 1 paragraph, image preview)
        // * Related articles list (list of articles names links) * Section tags (the list of tags links with number of tags specified in brackets)

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES).render();

        Assert.assertTrue(newsPage.isRSSLinkDisplayed());
        Assert.assertEquals(newsPage.getHeadlineTitleNews().get(0).getDescription(), WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
        Assert.assertTrue(newsPage.isDateTimeNewsPresent("article2"));
        Assert.assertTrue(newsPage.isImageLinkForTitleDisplayed(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));
        Assert.assertTrue(newsPage.isRightTitlesDisplayed());
        Assert.assertTrue(newsPage.isSectionTagsDisplayed());

    }

    /*
    *   AONE-5697:News - Companies articles (v. 3.4)
    */
    @AlfrescoTest(testlink = "AONE-5697")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCompanyArticles() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Company news page from News menu;
        // ---- Expected results ----
        // Company page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        //  Click "China eyes shake-up of bank holdings" article name; // or Global car industry
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed: * Article name  * From <component name> link
        // * Article picture * Created date  * Tags  * AWE actions (Edit, Create Article, Delete icons)

        newsPage.clickLinkByTitle(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
        WcmqsNewsArticleDetails newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.GLOBAL_CAR_INDUSTRY, "Article title is not " + WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
        Assert.assertEquals(newsDetailsPage.getFromLinkName().toLowerCase(), WcmqsNewsPage.COMPANIES, "The from link is not " + WcmqsNewsPage.COMPANIES);
        Assert.assertTrue(newsDetailsPage.isNewsArticleImageDisplayed(), "Article picture is not displayed.");
        Assert.assertTrue(newsDetailsPage.isTagsSectionDisplayed(), "Tag section is not displayed.");
        Assert.assertTrue(newsDetailsPage.isEditButtonDisplayed(), "Edit button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isCreateButtonDisplayed(), "Create button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isDeleteButtonDisplayed(), "Delete button is not displayed.");

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Companies link in "From" section;
        // ---- Expected results ----
        // User is returned to Company News page;

        newsPage = newsDetailsPage.clickComponentLinkFromSection(WcmqsNewsPage.COMPANIES);
        newsPage.render();

        // ---- Step 5 ----
        // ---- Step action ----
        // Click "Media Consult new site coming out in September" article name;
        // Fresh flight to Swiss franc as Europe's bond strains return
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed: * Article name  * From <component name> link
        // * Article picture * Created date  * Tags  * AWE actions (Edit, Create Article, Delete icons)

        newsPage.clickLinkByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS).render();
        newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS, "Article title is not " + WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);
        Assert.assertEquals(newsDetailsPage.getFromLinkName().toLowerCase(), WcmqsNewsPage.COMPANIES, "The from link is not " + WcmqsNewsPage.COMPANIES);
        Assert.assertTrue(newsDetailsPage.isNewsArticleImageDisplayed(), "Article picture is not displayed.");
        Assert.assertTrue(newsDetailsPage.isTagsSectionDisplayed(), "Tag section is not displayed.");
        Assert.assertTrue(newsDetailsPage.isEditButtonDisplayed(), "Edit button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isCreateButtonDisplayed(), "Create button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isDeleteButtonDisplayed(), "Delete button is not displayed.");

        // ---- Step 6 ----
        // ---- Step action ----
        // Click 'Company Name' link in the signature for quotation;
        // ---- Expected results ----
        // User is redirected to "Minicards are now available" article in "Companies" component;

        //TODO The link from signature is not redirected to Minicards article - old functionality. Test link should be updated
    }


    /*
    *   AONE-5699:News - Companies - Related Articles
    */
    @AlfrescoTest(testlink = "AONE-5699")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCompaniesRelatedArticles() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Companies page from News menu;
        // ---- Expected results ----
        // Companies page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click "Europe dept concerns ease but bank fears remain" article name in Related Articles;
        // ---- Expected results ----
        //  Article is opened successfully and displayed correctly;

        newsPage.getRightHeadlineTitleNews().get(0).click();
        WcmqsNewsArticleDetails newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.EUROPE_DEPT_CONCERNS, "Article title is not " + WcmqsNewsPage.EUROPE_DEPT_CONCERNS);

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to Companies page;
        // ---- Expected results ----
        // User is returned to Companies page;

        newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 5 ----
        // ---- Step action ----
        // Click "Investors fear rising risk of US regional defaults" article name;
        // ---- Expected results ----
        // Article is opened successfully;

        newsPage.getRightHeadlineTitleNews().get(1).click();
        newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), WcmqsNewsPage.INVESTORS_FEAR, "Article title is not " + WcmqsNewsPage.INVESTORS_FEAR);

    }

    /*
    *   AONE-5700:News - Companies - Section Tags
    */
    @AlfrescoTest(testlink = "AONE-5700")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCompaniesSectionTags() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Companies page from News menu;
        // ---- Expected results ----
        // Companies page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES);
        newsPage.render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify Section Tags menu;
        // ---- Expected results ----
        // The list of tags is dislpayed with number of tags (only items for current component are displayed):
        // test 1 (2)
        // test 2 (1)

        String expectedTag1 = tag1 + " (2)";
        String expectedTag2 = tag4 + " (1)";
        String expectedTag3 = tag3 + " (1)";
        String expectedTag4 = tag2 + " (1)";
        Assert.assertFalse(newsPage.getTagList().contains("None"), "List of tags link is displayed and it is empty.");
        Assert.assertEquals(newsPage.getTagList().size(), 4, "List of tags does not contain only 4 items");
        Assert.assertEquals(newsPage.getTagList().get(0), expectedTag1, "List of tags does not contain tag: " + expectedTag1);
        Assert.assertEquals(newsPage.getTagList().get(1), expectedTag2, "List of tags does not contain tag: " + expectedTag2);
        Assert.assertEquals(newsPage.getTagList().get(2), expectedTag3, "List of tags does not contain tag: " + expectedTag3);
        Assert.assertEquals(newsPage.getTagList().get(3), expectedTag4, "List of tags does not contain tag: " + expectedTag4);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click test 1 tag link;
        // ---- Expected results ----
        // Two articles are dislpayed;

        newsPage.getTagLinks().get(0).click();
        WcmqsSearchPage searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 2, "List of search results does not contain only 2 articles");

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to Companies page and click test 2 tag link;
        // ---- Expected results ----
        // One article is displayed;

        searchPage.openNewsPageFolder(WcmqsNewsPage.COMPANIES);
        newsPage.getTagLinks().get(1).click();
        searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 1, "List of search results does not contain only one article");

    }

    /*
    *    AONE-5701:News - Companies - Subscribe to RSS
    */
    @AlfrescoTest(testlink = "AONE-5701")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCompaniesRss() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Companies page from News menu;
        // ---- Expected results ----
        // Companies page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Subscribe to RSS icon;
        // ---- Expected results ----
        // Subscribe page is opened;

        RssFeedPage rssFeedPage = newsPage.clickRssLink().render();
        Assert.assertTrue(rssFeedPage.isSubscribePanelDisplay());

        // ---- Step 4 ----
        // ---- Step action ----
        // Select necessary details and subscribe to RSS;
        // ---- Expected results ----
        // RSS is successfully selected;


        // ---- Step 5 ----
        // ---- Step action ----
        // Update RSS feed;
        // ---- Expected results ----
        // All articles are dislpayed in RSS correctly;

        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));
        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));
    }

    /*
     * AONE-5702 News - Markets
     */
    @AlfrescoTest(testlink = "AONE-5702")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyMarketsNews() throws Exception
    {
        String newsName = "article6";
        String expectedNewsTitle = "Investors fear rising risk of US regional defaults";
        String expectedNewsDesc = "No malorum consulatu eam, quod dicunt adhuc numquam. Lorem labores senserit at ius, cu vel viim te adhuc numquam. Lorem labores senserit at ius, cu vel viim te adhuc idisse recusabo omittantur.";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;
        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Markets page from News menu;
        // ---- Expected results ----
        // The following items are displayed:
        // Subscribe to RSS link
        // Articles list (Article name link, Creation date, 1 paragraph, image preview)
        // Related articles list (list of articles names links)
        // Section tags (the list of tags links with number of tags specified in brackets)
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS);
        newsPage.render();

        Assert.assertTrue(newsPage.isRSSLinkDisplayed(), "Subscribe to RSS link is not displayed.");
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");
        Assert.assertEquals(newsPage.getNewsTitle(newsName), expectedNewsTitle, "News title " + expectedNewsTitle + " is not displayed.");
        Assert.assertTrue(newsPage.isDateTimeNewsPresent(newsName), "Creation date of news " + expectedNewsTitle + " is not displayed.");
        Assert.assertTrue(newsPage.getNewsDescription(newsName).contains(expectedNewsDesc), "Description of news " + expectedNewsTitle + " is not displayed.");
        Assert.assertTrue(newsPage.isImageLinkForTitleDisplayed(expectedNewsTitle), "Image of news " + expectedNewsTitle + " is not displayed.");
        Assert.assertNotEquals(newsPage.getRightHeadlineTitleNews().size(), 0, "List of related articles is empty.");
        Assert.assertNotEquals(newsPage.getTagList().size(), 0, "List of tags link is displayed and it is empty.");

    }

    /*
     * AONE-5703 News - Markets articles(v 3.4)
     */
    @AlfrescoTest(testlink = "AONE-5703")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyMarketsArticles() throws Exception
    {
        String newsTitle1 = "Investors fear rising risk of US regional defaults";

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Markets page from News menu;
        // ---- Expected results ----
        // Markets page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Investors fear rising risk of US regional defaults article name;
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed:
        // Article name
        // From component name link
        // Article picture
        // Created date
        // Tags
        // AWE actions (Edit, Create Article, Delete icons)

        newsPage.clickLinkByTitle(newsTitle1);
        WcmqsNewsArticleDetails newsDetailsPage = new WcmqsNewsArticleDetails(drone);
        newsDetailsPage.render();
        Assert.assertEquals(newsDetailsPage.getTitleOfNewsArticle(), newsTitle1, "Article title is not " + newsTitle1);
        Assert.assertEquals(newsDetailsPage.getFromLinkName().toLowerCase(), WcmqsNewsPage.MARKETS, "The from link is not " + WcmqsNewsPage.MARKETS);
        Assert.assertTrue(newsDetailsPage.isNewsArticleImageDisplayed(), "Article picture is not displayed.");
        Assert.assertTrue(newsDetailsPage.isTagsSectionDisplayed(), "Tag section is not displayed.");
        Assert.assertTrue(newsDetailsPage.isEditButtonDisplayed(), "Edit button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isCreateButtonDisplayed(), "Create button is not displayed.");
        Assert.assertTrue(newsDetailsPage.isDeleteButtonDisplayed(), "Delete button is not displayed.");

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Markets link in From section;
        // ---- Expected results ----
        // User is returned to Markets page;

        newsPage = newsDetailsPage.clickComponentLinkFromSection(WcmqsNewsPage.MARKETS);
        newsPage.render();

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Our new brochure is now available article name;
        // ---- Expected results ----
        // Article is opened successfully, the following items are displayed:
        // Article name
        // From component name link
        // Article picture
        // Created date
        // Tags
        // AWE actions (Edit, Create Article, Delete icons)

        // TODO 5: Add your code here for step 5. !!!! need clarification for the steps 5 and 6

        // ---- Step 6 ----
        // ---- Step action ----
        // Click 'Company Name' link in the signature for quotation;
        // ---- Expected results ----
        // User is redirected to Minicards are now available article in Companies component;

        // TODO 6: Add your code here for step 6.

    }

    /*
     * AONE-5705 News - Markets - Related Articles
     */
    @AlfrescoTest(testlink = "AONE-5705")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyMarketsRelatedArticles() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Markets page from News menu;
        // ---- Expected results ----
        // Markets page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS);
        newsPage.render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Media Consult new site coming out in September article name in Related Articles;
        // ---- Expected results ----
        // Article is opened successfully

        // Replace "Click "Media Consult new site coming out in September" article name in Related Articles;" with "Click"FTSE 100 rallies from seven-week
        // low" article name in Related Articles;" beacause "Media Consult new site coming out in September" is not available in realted Articles
        newsPage.clickLinkByTitle(WcmqsNewsPage.FTSE_1000, WcmqsNewsPage.RELATED_ARTICLES_SECTION);
        WcmqsNewsArticleDetails newsArticleDetails = new WcmqsNewsArticleDetails(drone);
        newsArticleDetails.render();
        Assert.assertEquals(newsArticleDetails.getTitleOfNewsArticle(), WcmqsNewsPage.FTSE_1000, "News article: " + WcmqsNewsPage.FTSE_1000
                + " is not opened successfully.");

    }

    /*
     * AONE-5706 News - Markets - Section Tags
     */
    @AlfrescoTest(testlink = "AONE-5706")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyMarketsSectionTags() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Markets page from News menu;
        // ---- Expected results ----
        // Markets page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS);
        newsPage.render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify Section Tags menu;
        // ---- Expected results ----
        // The list of tags is dislpayed with number of tags (only items for current component are displayed):
        // test 1 (2)
        // test 2 (1)

        String expectedTag1 = tag1 + " (2)";
        String expectedTag2 = tag4 + " (2)";
        String expectedTag3 = tag2 + " (1)";
        Assert.assertFalse(newsPage.getTagList().contains("None"), "List of tags link is displayed and it is empty.");
        Assert.assertEquals(newsPage.getTagList().size(), 3, "List of tags does not contain only 2 items");
        Assert.assertEquals(newsPage.getTagList().get(0), expectedTag1, "List of tags does not contain tag: " + expectedTag1);
        Assert.assertEquals(newsPage.getTagList().get(1), expectedTag2, "List of tags does not contain tag: " + expectedTag2);
        Assert.assertEquals(newsPage.getTagList().get(2), expectedTag3, "List of tags does not contain tag: " + expectedTag3);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click test 1 tag link;
        // ---- Expected results ----
        // Two articles are dislpayed;

        newsPage.getTagLinks().get(0).click();
        WcmqsSearchPage searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 2, "List of search results does not contain only 2 articles");

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to Markets page and click test 2 tag link;
        // ---- Expected results ----
        // One article is displayed;

        searchPage.openNewsPageFolder(WcmqsNewsPage.MARKETS);
        newsPage.getTagLinks().get(1).click();
        searchPage = new WcmqsSearchPage(drone);
        searchPage.render();
        Assert.assertEquals(searchPage.getTagSearchResults().size(), 1, "List of search results does not contain only one article");

    }

    /*
    * AONE-5707 News - Markets - Subscribe to RSS
    */
    @AlfrescoTest(testlink = "AONE-5707")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyMarketsRss() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Markets page from News menu;
        // ---- Expected results ----
        // Markets page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(WcmqsNewsPage.MARKETS).render();
        Assert.assertNotEquals(newsPage.getHeadlineTitleNews().size(), 0, "List of news titles is empty.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Subscribe to RSS icon;
        // ---- Expected results ----
        // Subscribe page is opened;

        RssFeedPage rssFeedPage = newsPage.clickRssLink().render();
        Assert.assertTrue(rssFeedPage.isSubscribePanelDisplay());

        // ---- Step 4 ----
        // ---- Step action ----
        // Select necessary details and subscribe to RSS;
        // ---- Expected results ----
        // RSS is successfully selected;

        // ---- Step 5 ----
        // ---- Step action ----
        // Update RSS feed;
        // ---- Expected results ----
        // All articles are dislpayed in RSS correctly;

        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.INVESTORS_FEAR));
        Assert.assertTrue(rssFeedPage.isDisplayedInFeed(WcmqsNewsPage.HOUSE_PRICES));
    }

    private void dataPrep_AONE_5706() throws Exception
    {
        // 4. The following tags are added to appropriate files via Alfresco Share:
        // *  test1, test2  to article3.html (Alfresco Quick Start/Quick Start Editorial/root/news/global)

        String blog_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "news" + File.separator + "markets";
        DocumentLibraryPage documentLibPage = siteActions.navigateToFolder(drone, blog_folder_path).render();
        documentLibPage.getFileDirectoryInfo("article5.html").addTag(tag1);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("article6.html").addTag(tag1);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("article5.html").addTag(tag2);
        drone.refresh();
        // * test1 to article4.html (Alfresco Quick Start/Quick Start Editorial/root/news/global)

        // *  test3 to article1.html (Alfresco Quick Start/Quick Start Editorial/root/news/companies)
        documentLibPage = documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage.selectFolder("global").render();
        documentLibPage.getFileDirectoryInfo("article3.html").addTag(tag3);
        drone.refresh();
        // * test4 to article6.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)
        documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage = documentLibPage.selectFolder("companies").render();
        documentLibPage.getFileDirectoryInfo("article1.html").addTag(tag4);
    }

    private void dataPrep_AONE_5694() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // 4. The following tags are added to appropriate files via Alfresco Share:
        // * test1, test2 to article5.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)

        String blog_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "news" + File.separator + "global";
        siteActions.navigateToDocuemntLibrary(drone, siteName);
        DocumentLibraryPage documentLibPage = siteActions.navigateToFolder(drone, blog_folder_path).render();
        documentLibPage.getFileDirectoryInfo("article3.html").addTag(tag1);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("article3.html").addTag(tag2);
        drone.refresh();
        // * test1 to article6.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)
        documentLibPage.getFileDirectoryInfo("article4.html").addTag(tag1);
        drone.refresh();

        // * test3 to article3.html (Alfresco Quick Start/Quick Start Editorial/root/news/global)
        documentLibPage = documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage.selectFolder("companies").render();
        documentLibPage.getFileDirectoryInfo("article1.html").addTag(tag3);
        drone.refresh();
        // * test4 to article1.html (Alfresco Quick Start/Quick Start Editorial/root/news/companies)
        documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage = documentLibPage.selectFolder("markets").render();
        documentLibPage.getFileDirectoryInfo("article6.html").addTag(tag4);
    }

    private void dataPrep_AONE_5700() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // 4. The following tags are added to appropriate files via Alfresco Share:
        // * test1, test2 to article5.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)

        String blog_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT + File.separator + "news" + File.separator + "companies";
        siteActions.navigateToDocuemntLibrary(drone, siteName);
        DocumentLibraryPage documentLibPage = siteActions.navigateToFolder(drone, blog_folder_path).render();
        documentLibPage.getFileDirectoryInfo("article1.html").addTag(tag1);
        drone.refresh();
        documentLibPage.getFileDirectoryInfo("article1.html").addTag(tag2);
        drone.refresh();
        // * test1 to article6.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)
        documentLibPage.getFileDirectoryInfo("article2.html").addTag(tag1);
        drone.refresh();

        // * test3 to article3.html (Alfresco Quick Start/Quick Start Editorial/root/news/global)
        documentLibPage = documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage.selectFolder("global").render();
        documentLibPage.getFileDirectoryInfo("article3.html").addTag(tag3);
        drone.refresh();
        // * test4 to article1.html (Alfresco Quick Start/Quick Start Editorial/root/news/companies)
        documentLibPage.getNavigation().clickFolderUp().render();
        documentLibPage = documentLibPage.selectFolder("markets").render();
        documentLibPage.getFileDirectoryInfo("article6.html").addTag(tag4);
    }

}
