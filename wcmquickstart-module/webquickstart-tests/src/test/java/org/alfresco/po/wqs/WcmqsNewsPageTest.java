package org.alfresco.po.wqs;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.test.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by rdorobantu on 12/8/2014.
 */
@Listeners(FailedTestListener.class)
public class WcmqsNewsPageTest extends AbstractTest
{
    private static final Logger logger = Logger.getLogger(WcmqsNewsPageTest.class);
    DashBoardPage dashBoard;
    private String wqsURL;
    private String siteName;
    private String ipAddress;

    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception
    {
        String testName = this.getClass().getSimpleName();
        siteName = testName;

        String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
        try
        {
            ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
            logger.info("Ip address from Alfresco server was obtained");
        }
        catch (UnknownHostException | SecurityException e)
        {
            logger.error("Ip address from Alfresco server could not be obtained");
        }

        ;
        wqsURL = siteName + ":8080/wcmqs/news/";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // WCM Quick Start is installed; - is not required to be executed automatically
        int columnNumber = 2;
        String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        dashBoard = loginAs(username, password);
/*
        // Site is created in Alfresco Share;
        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();

        // WCM Quick Start Site Data is imported;
        CustomiseSiteDashboardPage customiseSiteDashboardPage = site.getSiteNav().selectCustomizeDashboard().render();
        SiteDashboardPage siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, columnNumber);
        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();
        wqsDashlet.waitForImportMessage();

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage.selectFolder("Alfresco Quick Start");
        EditDocumentPropertiesPage documentPropertiesPage = documentLibraryPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties()
            .render();
        documentPropertiesPage.setSiteHostname(siteName);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibraryPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // setup new entry in hosts to be able to access the new wcmqs site
        String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
            + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
        Runtime.getRuntime().exec(setHostAddress);*/

    }

    @AfterClass
    public void tearDown()
    {
        logout(drone);
    }

    @BeforeMethod
    public void setUp()
    {
        drone.navigateTo(wqsURL);
    }

    private void LogIn()
    {
        String userName = "admin";
        String password = "admin";
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.inputUserName(userName);
        loginPage.inputPassword(password);
        loginPage.clickLoginButton();
    }

    @Test
    public void testIsDateTimeNewsPresent()
    {
       // drone.navigateTo(wqsURL);
        String newsName = "global";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        Assert.assertTrue(newsPage.isDateTimeNewsPresent(newsName));

    }

    @Test
    public void testGetDateTimeNews()
    {
        //drone.navigateTo(wqsURL);
        String newsName = "global";
        String dateTime = "29 July 2010 07:24 PM";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        Assert.assertTrue(newsPage.getDateTimeNews(newsName).contains(dateTime));
    }

    @Test
    public void testGetNewsDescription()
    {
        //drone.navigateTo(wqsURL);
        String newsName = "article2";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
//        Assert.assertEquals(newsPage.getNewsDescription(newsName),
//            "Ubique ancillae appellantur cu per, possit perpe tua repudiare vix cu, eius inciderint scribentur ut eos. Nam an deleniti placerat petentium.");
    }

    @Test
    public void testGetNewsTitle()
    {
       // drone.navigateTo(wqsURL);
       // String newsName = "global";
        String newsName = "article3.html";
        String title = "FTSE 100 rallies from seven-week low";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        //Assert.assertEquals(newsPage.getNewsTitle(newsName), "FTSE 100 rallies from seven-week low");
        Assert.assertEquals(newsPage.getNewsTitle(newsName), title);
    }

    @Test
    public void testGetHeadlineTitleNews()
    {
        //drone.navigateTo(wqsURL);
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
//        Assert.assertEquals(newsPage.getHeadlineTitleNews().size(), 6,
//            "The number of headline titles in the news Page is " + newsPage.getHeadlineTitleNews().size() + ". Expected 6 but was " + newsPage
//                     .getHeadlineTitleNews().size() + ".");
        int noTitles = newsPage.getHeadlineTitleNews().size();
        Assert.assertEquals(noTitles, 5);
    }

    @Test
    public void testClickNewsByName()
    {
        //drone.navigateTo(wqsURL);
        String newsName = "article2";
        String newsTitle = "Global car industry";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.clickNewsByName(newsName);
        WcmqsNewsArticleDetails newsArticleDetails = new WcmqsNewsArticleDetails(drone);
        Assert.assertEquals(newsArticleDetails.getTitleOfNewsArticle(), newsTitle);
    }

    @Test
    public void testOpenNewsPageFolder()
    {
        //drone.navigateTo(wqsURL);
        String folderName = "markets";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.openNewsPageFolder(folderName);
        String pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Markets"));

    }

    @Test
    public void testCheckIfBlogIsDeleted()
    {
        //drone.navigateTo(wqsURL);
        String title = "blog3";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.selectMenu(WcmqsAbstractPage.BLOG_MENU_STR);
        Assert.assertTrue(newsPage.checkIfBlogIsDeleted(title), "Blog is deleted.");

    }

    @Test
    public void testIsDeleteConfirmationWindowDisplayed()
    {
        //drone.navigateTo(wqsURL);
        String newsName = "article3";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.clickNewsByName(newsName);
//        newsPage.deleteArticle();
//        Assert.assertTrue(newsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window is not displayed.");
    }

    @Test
    public void testCancelArticleDelete()
    {
        //drone.navigateTo(wqsURL);
        String newsName = "article2";
        String newsTitle = "Global car industry";;
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.clickNewsByName(newsName);
        if (newsPage.isLoggedIn() == false);
        {
            WcmqsNewsPageTest loginNews = new WcmqsNewsPageTest();
            loginNews.LogIn();
        }
//        newsPage.deleteArticle();
//        newsPage.cancelArticleDelete();
        Assert.assertTrue(newsPage.checkIfNewsExists(newsTitle), "News does not exist.");
    }

    @Test
    public void testCheckIfNewsExists()
    {
       // drone.navigateTo(wqsURL);
        String newsName = "article2";
        String newsTitle = "Global car industry";
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        newsPage.clickNewsByName(newsName);
        Assert.assertTrue(newsPage.checkIfNewsExists(newsTitle), "News does not exist.");
    }

}
