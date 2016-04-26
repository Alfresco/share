
package org.alfresco.test.wqs.web.awe;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.WcmqsBlogPage;
import org.alfresco.po.wqs.WcmqsBlogPostPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by svidrascu on 11/19/2014.
 */

@Listeners(FailedTestListener.class)
public class DeleteItemsViaAWE extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(DeleteItemsViaAWE.class);
    private String testName;
    private String siteName;
    private String ipAddress;
    private String[] loginInfo;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        ipAddress = getIpAddress();
        loginInfo = new String[]{ADMIN_USERNAME, ADMIN_PASSWORD};
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

    /**
     * AONE-5641:Deleting "Ethical funds" blog post
     */

    @AlfrescoTest(testlink = "AONE-5641")
    @Test(groups = "WQS")
    public void deleteEthicalFundsBlogPost() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Ethical funds" blog post;
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        Assert.assertNotNull(wcmqsBlogPostPage, "Blog Post " + WcmqsBlogPage.ETHICAL_FUNDS + " is not opened");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        wcmqsBlogPostPage.cancelArticleDelete();
        Assert.assertTrue(wcmqsBlogPostPage.getTitle().equals(WcmqsBlogPage.ETHICAL_FUNDS), "Canceling deleting Blog Post " + WcmqsBlogPage.ETHICAL_FUNDS
                + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;
        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;
        WcmqsBlogPage blogPage = wcmqsBlogPostPage.confirmArticleDelete().render();
        Assert.assertTrue(blogPage.isBlogDeleted(WcmqsBlogPage.ETHICAL_FUNDS), "Deleting Blog Post " + WcmqsBlogPage.ETHICAL_FUNDS
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(getShareUrl());
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsBlogPage.BLOG);
        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_1), "Ethical funds page hasn't been deleted correctly");
        ShareUtil.logout(drone);

    }

    /**
     * AONE-5642:Deleting "Company organises workshop" blog post
     */

    @AlfrescoTest(testlink = "AONE-5642")
    @Test(groups = "WQS")
    public void deleteCompanyOrgWorkshopBlogPost() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Company organises workshop" blog post;
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        Assert.assertNotNull(wcmqsBlogPostPage, "Blog Post " + WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP + " is not opened");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        wcmqsBlogPostPage.cancelArticleDelete();
        Assert.assertTrue(wcmqsBlogPostPage.getTitle().equals(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP), "Canceling deleting Blog Post "
                + WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        wcmqsBlogPostPage.confirmArticleDelete();
        WcmqsBlogPage blogPage = homePage.selectMenu("blog").render();
        Assert.assertTrue(blogPage.isBlogDeleted(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP), "Deleting Blog Post "
                + WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog2.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsBlogPage.BLOG);
        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_2), "Company organizes workshop page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

    /**
     * AONE-5643:Deleting "Our Analyst's thoughts" blog post
     */
    @AlfrescoTest(testlink = "AONE-5643")
    @Test(groups = "WQS")
    public void deleteOurAnalystBlogPost() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Our top analyst's latest..." blog post;
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();

        Assert.assertNotNull(wcmqsBlogPostPage, "Blog Post " + WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS + " is not opened");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        wcmqsBlogPostPage.cancelArticleDelete();
        Assert.assertTrue(wcmqsBlogPostPage.getTitle().contains(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS), "Canceling deleting Blog Post "
                + WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        wcmqsBlogPostPage.deleteArticle();
        Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        wcmqsBlogPostPage.confirmArticleDelete();
        WcmqsBlogPage blogPage = homePage.selectMenu("blog").render();
        Assert.assertTrue(blogPage.isBlogDeleted(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS), "Deleting Blog Post " + WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog3.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsBlogPage.BLOG);

        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_3), "Our top analyst's latest thoughts page hasn't been deleted correctly");
        ShareUtil.logout(drone);

    }

    /**
     * AONE-5644:Deleting "Europe dept...."article (Global economy)
     */
    @AlfrescoTest(testlink = "AONE-5644")
    @Test(groups = "WQS")
    public void deleteEuropeDeptArticle() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Europe dept concerns ease but bank fears remain" article in Global economy (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.EUROPE_DEPT_CONCERNS).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.EUROPE_DEPT_CONCERNS).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.EUROPE_DEPT_CONCERNS), "Canceling deleting News Article "
                + WcmqsNewsPage.EUROPE_DEPT_CONCERNS + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        newsArticleDetails.confirmArticleDelete();
        WcmqsNewsPage newsPage = homePage.selectMenu("news").render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.EUROPE_DEPT_CONCERNS), "Deleting Blog Post " + WcmqsNewsPage.EUROPE_DEPT_CONCERNS
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.GLOBAL);
        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_4),
                "Europe dept concerns ease but bank fears remain page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

    /**
     * AONE-5645:Deleting "FTSE 100 rallies from seven-week low" (Global economy)
     */
    @AlfrescoTest(testlink = "AONE-5645")
    @Test(groups = "WQS")
    public void deleteFtse100Article() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Europe dept concerns ease but bank fears remain" article in Global economy (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.FTSE_1000).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.FTSE_1000).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.FTSE_1000), "Canceling deleting News Article "
                + WcmqsNewsPage.FTSE_1000 + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        newsArticleDetails.confirmArticleDelete();
        WcmqsNewsPage newsPage = homePage.selectMenu("news").render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.FTSE_1000), "Deleting Blog Post " + WcmqsNewsPage.FTSE_1000
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        drone.navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.GLOBAL);

        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_3), "FTSE 100 rallies from seven-week low page hasn't been deleted correctly");
        ShareUtil.logout(drone);

    }

    /**
     * AONE-5646:Deleting "China eyes shake-up...."article (Companies)
     */
    @AlfrescoTest(testlink = "AONE-5646")
    @Test(groups = "WQS")
    public void deleteChinaEyesArticle() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Global car industry" article in Companies (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY), "Canceling deleting News Article "
                + WcmqsNewsPage.GLOBAL_CAR_INDUSTRY + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;
        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        newsArticleDetails.confirmArticleDelete().render();
        homePage = returnToHomePage().render();
        WcmqsNewsPage newsPage = homePage.selectMenu("news").render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY), "Deleting Blog Post " + WcmqsNewsPage.GLOBAL_CAR_INDUSTRY
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.COMPANIES);
        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_2), "Global car industry page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

    /**
     * AONE-5647:Deleting "Fresh flight to Swiss franc as Europe's bond strains return" (Global economy)
     */
    @AlfrescoTest(testlink = "AONE-5647")
    @Test(groups = "WQS")
    public void deleteFreshFlightArticle() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Fresh flight to Swiss franc as Europe's bond strains return" article in Companies (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS), "Canceling deleting News Article "
                + WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        newsArticleDetails.confirmArticleDelete();
        homePage = returnToHomePage().render();
        WcmqsNewsPage newsPage = homePage.selectMenu("news").render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS), "Deleting Blog Post " + WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.COMPANIES);
        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_1),
                "Fresh flight to Swiss franc as Europe's bond strains return page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

    /**
     * AONE-5648:Deleting Investors fear rising risk of US regional defaults (Global economy)
     */
    @AlfrescoTest(testlink = "AONE-5648")
    @Test(groups = "WQS")
    public void deleteInvestorsFearArticle() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open Investors fear rising risk of US regional defaults article in markets (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.INVESTORS_FEAR).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.INVESTORS_FEAR).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.INVESTORS_FEAR), "Canceling deleting News Article "
                + WcmqsNewsPage.INVESTORS_FEAR + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;
        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        WcmqsNewsPage newsPage = newsArticleDetails.confirmArticleDelete().render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.INVESTORS_FEAR), "Deleting Blog Post " + WcmqsNewsPage.INVESTORS_FEAR
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.MARKETS);

        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_6),
                "Investors fear rising risk of US regional defaults page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

    /**
     * AONE-5649:Deleting House prices face rollercoaster ride (markets)
     */
    @AlfrescoTest(testlink = "AONE-5649")
    @Test(groups = "WQS")
    public void deleteHousePricesArticle() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open House prices face rollercoaster ride article in markets (News);
        // ---- Expected results ----
        // 2. Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsNewsPage.HOUSE_PRICES).render();
        WcmqsNewsArticleDetails newsArticleDetails = wcmqsSearchPage.clickLinkByTitle(WcmqsNewsPage.HOUSE_PRICES).render();
        Assert.assertNotNull(newsArticleDetails);

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Delete button near post;
        // ---- Expected results ----
        // Confirm Delete window is opened;

        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 4 ----
        // ---- Step action ---
        // Click Cancel button;
        // ---- Expected results ----
        // File is not deleted;

        newsArticleDetails.cancelArticleDelete();
        Assert.assertTrue(newsArticleDetails.getTitleOfNewsArticle().equals(WcmqsNewsPage.HOUSE_PRICES), "Canceling deleting News Article "
                + WcmqsNewsPage.HOUSE_PRICES + " failed. The blog is not displayed.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Click Delete button;
        // ---- Expected results ----
        // Confirm Delete window is opened;
        newsArticleDetails.deleteArticle();
        Assert.assertTrue(newsArticleDetails.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // File is deleted and no more dislpayed in the list of articles;

        WcmqsNewsPage newsPage = newsArticleDetails.confirmArticleDelete().render();
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(WcmqsNewsPage.HOUSE_PRICES), "Deleting Blog Post " + WcmqsNewsPage.HOUSE_PRICES
                + " failed. The blog is displayed.");
        waitForDocumentsToIndex();

        // ---- Step 7 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        documentLibPage.selectFolder(QUICK_START_EDITORIAL);
        documentLibPage.selectFolder(ROOT);
        documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
        documentLibPage.selectFolder(WcmqsNewsPage.MARKETS);

        Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_5), "House prices face rollercoaster ride page hasn't been deleted correctly");
        ShareUtil.logout(drone);
    }

}
