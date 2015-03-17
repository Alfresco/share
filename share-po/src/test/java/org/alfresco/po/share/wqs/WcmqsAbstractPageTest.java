package org.alfresco.po.share.wqs;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.test.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by rdorobantu on 12/5/2014.
 */
@Listeners(FailedTestListener.class)
public class WcmqsAbstractPageTest extends AbstractTest
{
    private static final Logger logger = Logger.getLogger(WcmqsAbstractPageTest.class);
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
        wqsURL = siteName + ":8080/wcmqs";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // WCM Quick Start is installed; - is not required to be executed automatically
        int columnNumber = 2;
        String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        dashBoard = loginAs(username, password);

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
        Runtime.getRuntime().exec(setHostAddress);

    }

    @AfterClass
    public void tearDown()
    {
        logout(drone);
    }

    @Test
    public void testIsAlfrescoLogoDisplay() throws Exception
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isAlfrescoLogoDisplay(), "Alfresco Logo is not displayed.");
    }

    @Test
    public void testIsBottomUrlDisplayed() throws Exception
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isBottomUrlDisplayed(), "Bottom URL is not displayed.");
    }

    @Test
    public void testIsSearchFieldWithButtonDisplay() throws Exception
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isSearchFieldWithButtonDisplay(), "Search field with button is not displayed.");
    }

    @Test
    public void testIsContactLinkDisplay() throws Exception
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isContactLinkDisplay(), "Contact link is not displayed.");
    }

    @Test
    public void testClickAlfrescoLink()
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        wqsPage.clickAlfrescoLink();
        String pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Alfresco"));

    }

    @Test
    public void testClickContactLink()
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        wqsPage.clickContactLink();
        String pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Contact"));
    }

    @Test
    public void testClickWebQuickStartLogo()
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        wqsPage.clickWebQuickStartLogo();
        String pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Home"));
    }

    @Test
    public void testInputTextInSearchField() throws Exception
    {
        drone.navigateTo(wqsURL);
        String inputText = "Global car industry";
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        wqsPage.inputTextInSearchField(inputText);
        Assert.assertEquals(wqsPage.getTextFromSearchField(), "Global car industry");
    }

    @Test
    public void testSearchText() throws Exception
    {
        drone.navigateTo(wqsURL);
        String searchedText = "Fresh flight";
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        wqsPage.searchText(searchedText);
        WcmqsSearchPage searchPage = new WcmqsSearchPage(drone);
        Assert.assertTrue(searchPage.verifyNumberOfSearchResultsHeader(1,searchedText));
    }

    @Test
    public void testIsImageLinkForTitleDisplayed() throws Exception
    {
        drone.navigateTo(wqsURL);
        String title = "Global car industry";
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isImageLinkForTitleDisplayed(title), "Image for title is not displayed.");
    }

    @Test
    public void testIsPageMenuDisplay() throws Exception
    {
        drone.navigateTo(wqsURL);
        WcmqsAbstractPage wqsPage = new WcmqsHomePage(drone);
        Assert.assertTrue(wqsPage.isPageMenuDisplay(), "Page menu is not displayed.");
    }

}
