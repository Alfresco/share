
package org.alfresco.test.wqs.web.awe;

import java.util.List;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.wqs.*;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.test.wqs.share.WqsShareTests;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by Lucian Tuca on 11/17/2014.
 */
@Listeners(FailedTestListener.class)
public class CreatingItemsViaAWE extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(CreatingItemsViaAWE.class);
    private String ipAddress;
    private String testName;
    private String siteName;
    private String[] loginInfo;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        ipAddress = getIpAddress();
        loginInfo = new String[] { ADMIN_USERNAME, ADMIN_PASSWORD };
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // User login
        // ---- Step 1 ----
        // ---- Step Action -----
        // WCM Quick Start is installed; - is not required to be executed automatically
        loginActions.loginToShare(drone, loginInfo, shareUrl);

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
        wqsDashlet.waitForImportMessage();

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

    @AlfrescoTest(testlink="AONE-5631")
    @Test(groups = "WQS")
    public void createBlogPostEthicalFunds() throws Exception
    {
        String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsEditPage editPage = blogPostPage.createArticle().render();

        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsBlogPage blogPage = fillWqsCreateForm(blogPostName, blogPostTitle, blogPostContent).render();
        blogPostPage = waitAndOpenBlogPost(blogPage, blogPostTitle);
        Assert.assertEquals(blogPostTitle, blogPostPage.getTitle());
        Assert.assertEquals(blogPostContent, blogPostPage.getContent());

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + WcmqsBlogPage.BLOG;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        waitForDocumentsToIndex();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(blogPostName).render();

        Assert.assertEquals(blogPostName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(blogPostContent));
        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5632")
    @Test(groups = "WQS")
    public void createBlogPostCompanyWorkshop() throws Exception
    {
        String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        WcmqsEditPage editPage = blogPostPage.createArticle().render();

        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsBlogPage blogPage = fillWqsCreateForm(blogPostName, blogPostTitle, blogPostContent).render();
        blogPostPage = waitAndOpenBlogPost(blogPage, blogPostTitle);
        Assert.assertEquals(blogPostTitle, blogPostPage.getTitle());
        Assert.assertEquals(blogPostContent, blogPostPage.getContent());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + WcmqsBlogPage.BLOG;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(blogPostName).render();

        Assert.assertEquals(blogPostName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(blogPostContent));
        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5633")
    @Test(groups = "WQS")
    public void createBlogPostOurTopAnalysts() throws Exception
    {
        String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();
        WcmqsEditPage editPage = blogPostPage.createArticle().render();

        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsBlogPage blogPage = fillWqsCreateForm(blogPostName, blogPostTitle, blogPostContent).render();
        blogPostPage = waitAndOpenBlogPost(blogPage, blogPostTitle);
        Assert.assertEquals(blogPostTitle, blogPostPage.getTitle());
        Assert.assertEquals(blogPostContent, blogPostPage.getContent());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + WcmqsBlogPage.BLOG;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(blogPostName).render();

        Assert.assertEquals(blogPostName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(blogPostContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5633")
    @Test(groups = "WQS")
    public void createArticleEuropeDept() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.GLOBAL, WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.GLOBAL;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5635")
    @Test(groups = "WQS")
    public void createArticleFTSE100() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.GLOBAL, WcmqsNewsPage.FTSE_1000);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.GLOBAL;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5636")
    @Test(groups = "WQS")
    public void createArticleGlobalCarIndustry() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.COMPANIES, WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.COMPANIES;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5637")
    @Test(groups = "WQS")
    public void createArticleFreshFlightToSwiss() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.COMPANIES, WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.COMPANIES;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage postDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, postDetailsPage.getDocumentTitle());
        Assert.assertTrue(postDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5638")
    @Test(groups = "WQS")
    public void createArticleInventorsFearRisingRisk() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.MARKETS, WcmqsNewsPage.INVESTORS_FEAR);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.MARKETS;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5639")
    @Test(groups = "WQS")
    public void createArticleHousesPrices() throws Exception
    {
        String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
        String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
        String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";

        navigateTo(wqsURL);
        WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.MARKETS, WcmqsNewsPage.HOUSE_PRICES);
        WcmqsEditPage editPage = newsArticleDetails.clickCreateButton().render();
        Assert.assertNotNull(editPage.getArticleDetails());

        WcmqsNewsPage newNewsPage = fillWqsCreateForm(newsArticleName, newsArticleTitle, newsArticleContent).render();
        newsArticleDetails = waitAndOpenNewsArticle(newNewsPage, newsArticleTitle);
        Assert.assertEquals(newsArticleTitle, newsArticleDetails.getTitleOfNewsArticle());
        Assert.assertEquals(newsArticleContent, newsArticleDetails.getBodyOfNewsArticle());
        waitForDocumentsToIndex();

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + NEWS + SLASH + WcmqsNewsPage.MARKETS;
        DocumentLibraryPage documentLibraryPage = siteActions.navigateToFolder(drone, folderPath).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        Assert.assertEquals(newsArticleName, blogPostDetailsPage.getDocumentTitle());
        Assert.assertTrue(blogPostDetailsPage.getDocumentBody().contains(newsArticleContent));

        ShareUtil.logout(drone);
    }

    @AlfrescoTest(testlink="AONE-5640")
    @Test(groups = "WQS")
    public void createHtmlArticleViaShare() throws Exception
    {
        String articleName = testName + System.currentTimeMillis() + "_name.html";
        String articleTitle = testName + System.currentTimeMillis() + "_title";
        String articleDescription = testName + System.currentTimeMillis() + "_description";
        String articleContent = testName + System.currentTimeMillis() + "_content";

        loginActions.loginToShare(drone, loginInfo, shareUrl);

        // ---- Step 1 ----
        // ---- Step Actions ----
        // Create an HTML article in Quick Start Editorial > root > news > global(e.g. article10.html).
        // ---- Expected results ----
        // HTML article is successfully created;
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
        documentLibraryPage = documentLibraryPage.selectFolder(WcmqsNewsPage.GLOBAL).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(articleName);
        contentDetails.setTitle(articleTitle);
        contentDetails.setDescription(articleDescription);
        contentDetails.setContent(articleContent);
        DocumentLibraryPage articlePage = siteActions.createContent(drone, contentDetails, ContentType.HTML);
        Assert.assertNotNull(articlePage);

        // ---- Step 2 ----
        // ---- Step actions ----
        // Navigate to Quick Start Editorial > root > news > global > collections > section.articles.
        // ---- Expected results ----
        // Section.articles folder is opened;
        SiteDashboardPage siteDashboard = siteActions.openSiteDashboard(drone, siteName).render();
        documentLibraryPage = siteDashboard.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
        documentLibraryPage = documentLibraryPage.selectFolder(WcmqsNewsPage.GLOBAL).render();
        documentLibraryPage = documentLibraryPage.selectFolder(WcmqsNewsPage.COLLECTIONS).render();
        Assert.assertNotNull(documentLibraryPage);

        // Wait 2 minutes to allow refresh query to execute
        waitForDocumentsToIndex();
        drone.refresh();

        // ---- Step 3 ----
        // ---- Step actions ----
        // Click Edit Metadata button;
        // ---- Expected results
        // Edit metadata form is opened;
        FileDirectoryInfo folderInfo = documentLibraryPage.getFileDirectoryInfo(WcmqsNewsPage.SECTION_ARTICLES);
        EditDocumentPropertiesPage editDocumentPropertiesPage = folderInfo.selectEditProperties().render();
        Assert.assertNotNull(editDocumentPropertiesPage);

        // ---- Step 4 ----
        // ---- Step actions ----
        // Verify the presense of arcticle10.html in Web Assets section;
        // ---- Expected results ----
        // Article10.html file is present in Web Assets section;
        List<String> foundAssets = editDocumentPropertiesPage.getWebAssets();
        Assert.assertTrue(foundAssets.contains(articleName));
    }
}
