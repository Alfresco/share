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
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsLoginPage;
import org.alfresco.po.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.po.wqs.*;
import org.apache.log4j.Logger;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
    private static final Logger logger = Logger.getLogger(NewsComponent.class);
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
        dataPrep_AONE_5706(documentLibPage);

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
    @AlfrescoTest(testlink="AONE-5686")
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
     * AONE-5702 News - Markets
     */
    @AlfrescoTest(testlink="AONE-5702")
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
    @AlfrescoTest(testlink="AONE-5703")
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
    @AlfrescoTest(testlink="AONE-5705")
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
    @AlfrescoTest(testlink="AONE-5706")
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
        String expectedTag2 = tag2 + " (1)";
        Assert.assertFalse(newsPage.getTagList().contains("None"), "List of tags link is displayed and it is empty.");
        Assert.assertEquals(newsPage.getTagList().size(), 2, "List of tags does not contain only 2 items");
        Assert.assertEquals(newsPage.getTagList().get(0), expectedTag1, "List of tags does not contain tag: " + expectedTag1);
        Assert.assertEquals(newsPage.getTagList().get(1), expectedTag2, "List of tags does not contain tag: " + expectedTag2);

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

    // TODO: Implement test AONE-5707
    // /*
    // * AONE-5707 News - Markets - Subscribe to RSS
    // */
    // @Test(groups = { "WQS", "EnterpriseOnly" })
    // public void AONE_5707() throws Exception
    // {
    //
    // // ---- Step 1 ----
    // // ---- Step action ----
    // // Navigate to http://host:8080/wcmqs
    // // ---- Expected results ----
    // // Sample site is opened;
    //
    // // TODO 1: Add your code here for step 1.
    //
    // // ---- Step 2 ----
    // // ---- Step action ----
    // // Go to Markets page from News menu;
    // // ---- Expected results ----
    // // Markets page is opened;
    //
    // // TODO 2: Add your code here for step 2.
    //
    // // ---- Step 3 ----
    // // ---- Step action ----
    // // Click Subscribe to RSS icon;
    // // ---- Expected results ----
    // // Subscribe page is opened;
    //
    // // TODO 3: Add your code here for step 3.
    //
    // // ---- Step 4 ----
    // // ---- Step action ----
    // // Select necessary details and subscribe to RSS;
    // // ---- Expected results ----
    // // RSS is successfully selected;
    //
    // // TODO 4: Add your code here for step 4.
    //
    // // ---- Step 5 ----
    // // ---- Step action ----
    // // Update RSS feed;
    // // ---- Expected results ----
    // // All articles are dislpayed in RSS correctly;
    //
    // // TODO 5: Add your code here for step 5.
    //
    // }

    private void dataPrep_AONE_5706(DocumentLibraryPage documentLibPage) throws Exception
    {
        // ---- Step 4 ----
        // ---- Step action ----
        // 4. The following tags are added to appropriate files via Alfresco Share:
        // * test1, test2 to article5.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder("news");
        documentLibPage.selectFolder("markets");
        documentLibPage.getFileDirectoryInfo("article5.html").addTag(tag1);
        documentLibPage.getFileDirectoryInfo("article5.html").addTag(tag2);

        // * test1 to article6.html (Alfresco Quick Start/Quick Start Editorial/root/news/markets)
        documentLibPage.getFileDirectoryInfo("article6.html").addTag(tag1);

        // * test3 to article3.html (Alfresco Quick Start/Quick Start Editorial/root/news/global)
        documentLibPage.getNavigation().clickFolderUp();
        documentLibPage = new DocumentLibraryPage(drone);
        documentLibPage.selectFolder("global");
        documentLibPage.getFileDirectoryInfo("article3.html").addTag(tag3);

        // * test4 to article1.html (Alfresco Quick Start/Quick Start Editorial/root/news/companies)
        documentLibPage.getNavigation().clickFolderUp();
        documentLibPage = new DocumentLibraryPage(drone);
        documentLibPage.selectFolder("companies");
        documentLibPage.getFileDirectoryInfo("article1.html").addTag(tag4);
    }

}
