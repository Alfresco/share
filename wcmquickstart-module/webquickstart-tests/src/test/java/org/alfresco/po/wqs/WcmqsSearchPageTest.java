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
package org.alfresco.po.wqs;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by P3700360 on 21.01.2015.
 */
public class WcmqsSearchPageTest extends AbstractTest {
    private static final Logger logger = Logger.getLogger(WcmqsSearchPageTest.class);
    DashBoardPage dashBoard;
    private String wqsURL;
    private String siteName;
    private String ipAddress;

    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception {
        String testName = this.getClass().getSimpleName();
        siteName = testName;

        String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
        try {
            ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
            logger.info("Ip address from Alfresco server was obtained");
        } catch (UnknownHostException | SecurityException e) {
            logger.error("Ip address from Alfresco server could not be obtained");
        }

        wqsURL = siteName + ":8080/wcmqs";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // WCM Quick Start is installed; - is not required to be executed automatically
        //int columnNumber = 2;
        //String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        dashBoard = loginAs(username, password);

   /*     // Site is created in Alfresco Share;
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
    public void tearDown() {
        logout(drone);
    }

    @BeforeMethod
    public void setUp() {
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
    public void testGetTagSearchResults()
    {
        //Positive test
        WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
        String searchTerm = "Ethical funds";
        String cssTagName = ".newslist-wrapper>li>h4>a";
        wqsPage.searchText(searchTerm);
        wqsPage.clickSearchButton();
        wqsPage.getTagSearchResults();
        String tagName = drone.find(By.cssSelector(cssTagName)).getText();
        Assert.assertEquals(searchTerm, tagName);
    }

    @Test
    public void testVerifyNumberOfSearchResultsHeaderPositive()
    {
        //Positive test
        WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
        String searchTerm = "FTSE";
        String cssResults = "p.intheader-paragraph";
        String resultsMsg = "Showing 2 of 2";
        wqsPage.searchText(searchTerm);
        wqsPage.clickSearchButton();
        wqsPage.render();
        Boolean searchResults = wqsPage.verifyNumberOfSearchResultsHeader(2, 2, searchTerm);
        String testResults = drone.find(By.cssSelector(cssResults)).getText();
        Boolean actualResults = testResults.contains(resultsMsg);
        Assert.assertEquals(searchResults, actualResults);
    }

        @Test
        public void testVerifyNumberOfSearchResultsHeaderNegative()
        {
                //Negative test
                String testTerm = "Alfresco";
                String resultsMsg2 = "Showing 10 of 12";
                String cssResults = "p.intheader-paragraph";
                WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
                wqsPage.searchText(testTerm);
                wqsPage.clickSearchButton();
                wqsPage.render();
                Boolean noResults = wqsPage.verifyNumberOfSearchResultsHeader(2, 2, testTerm);
                String testNoResults = drone.find(By.cssSelector(cssResults)).getText();
                Assert.assertNotEquals(noResults, testNoResults);
        }

    @Test
    public void testGetWcmqsSearchPagePaginationPositive()
        {
        //Positive test
        WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
        String xpathPageNo = "//div[@class='pagination']";
        String searchTerm = "FTSE";
        wqsPage.searchText(searchTerm);
        wqsPage.clickSearchButton();
        wqsPage.render();
        String pagesText = drone.find(By.xpath(xpathPageNo)).getText();
        String testPages = wqsPage.getWcmqsSearchPagePagination();
        Assert.assertEquals(pagesText, testPages);
    }

     @Test
     public void testGetWcmqsSearchPagePaginationNegative()
     {
             //Negative test
             WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
             String xpathPageNo = "//div[@class='pagination']";
             String searchTerm = "Alfresco";
             wqsPage.searchText(searchTerm);
             wqsPage.clickSearchButton();
             wqsPage.render();
             String pagesText = drone.find(By.xpath(xpathPageNo)).getText();
             wqsPage.clickNextPage();
             String testPages = wqsPage.getWcmqsSearchPagePagination();
             Assert.assertNotEquals(pagesText, testPages);
     }

    @Test
    public void testClickLatestBlogArticle()
    {
        //Positive test
        WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
        String title = "Ethical funds";
        wqsPage.clickLatestBlogArticle(title);
        if (wqsPage.isLoggedIn() == false);
            {
                    WcmqsSearchPageTest loginSearch = new WcmqsSearchPageTest();
                    loginSearch.LogIn();
            }
            Assert.assertTrue(wqsPage.getTitle().contains(title));
    }


    @Test
    public void testClickNextPagePositive()
    {
        WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
        //Positive test
        String searchTerm = "Alfresco";
        String nextPage = "Page 2 of 2";
        wqsPage.searchText(searchTerm);
        wqsPage.clickSearchButton();
        wqsPage.render();
        wqsPage.clickNextPage();
        String testNext = wqsPage.getWcmqsSearchPagePagination();
        Assert.assertTrue(testNext.contains(nextPage));
    }

    @Test
    public void testClickPrevPagePositive()
    {
            //Positive test
            String prevPage = "Page 1 of 2";
            String searchTerm = "Alfresco";
            WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
            wqsPage.searchText(searchTerm);
            wqsPage.clickSearchButton();
            wqsPage.render();
            wqsPage.clickNextPage();
            wqsPage.clickPrevPage();
            String testPrev = wqsPage.getWcmqsSearchPagePagination();
            Assert.assertTrue(testPrev.contains(prevPage));
    }

    @Test (expectedExceptions = PageOperationException.class)
    public void testClickNextPageNegative()
    {
            String searchTerm = "ftse";
            WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
            wqsPage.searchText(searchTerm);
            wqsPage.clickSearchButton();
            wqsPage.render();
            wqsPage.clickNextPage();
            Assert.assertNull(wqsPage.getWcmqsSearchPagePagination());
    }

    @Test (expectedExceptions = PageOperationException.class)
    public void testClickPrevPageNegative()
    {
                String searchTerm = "ftse";
                WcmqsSearchPage wqsPage = new WcmqsSearchPage(drone);
                wqsPage.searchText(searchTerm);
                wqsPage.clickSearchButton();
                wqsPage.render();
                wqsPage.clickPrevPage();
                Assert.assertNull(wqsPage.getWcmqsSearchPagePagination());
    }
}