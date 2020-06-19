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
package org.alfresco.test.wqs.web;

/**
 * Created by P3700473 on 12/2/2014.
 */

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.FactoryWqsPage;
import org.alfresco.po.wqs.WcmqsAbstractPage;
import org.alfresco.po.wqs.WcmqsAllPublicationsPage;
import org.alfresco.po.wqs.WcmqsBlogPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsLoginPage;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.alfresco.po.wqs.WcmqsPublicationPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.po.wqs.*;
import org.alfresco.test.wqs.web.search.SearchTests;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by Lucian Tuca on 12/02/2014.
 */

@Listeners(FailedTestListener.class)
public class MainPage extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(MainPage.class);
    private String testName;
    private String siteName;
    private String ipAddress;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        ipAddress = getIpAddress();
        testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();


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

        //Change property for quick start to sitename
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder("Alfresco Quick Start");
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties()
                .render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        //Change property for quick start live to ip address
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

    /*
    * AONE-5656 Main page
    */
    @AlfrescoTest(testlink = "AONE-5656")
    @Test(groups = {"WQS"})
    public void verifyMainPage() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify Main page;
        // ---- Expected results ----
        // The following items are displayed:

        // Alfresco Web Quick Start logo,
        WcmqsHomePage mainPage = new WcmqsHomePage(drone);
        Assert.assertTrue(mainPage.isAlfrescoLogoDisplay());

        // alfresco.com link in the bottom
        Assert.assertTrue(mainPage.isBottomUrlDisplayed());

        // Navigation links(Home, News, Publications, Blog, Alfresco.com),

        // Search field with Search button;
        Assert.assertTrue(mainPage.isSearchFieldWithButtonDisplay());

        // Contact link
        Assert.assertTrue(mainPage.isContactLinkDisplay());

        // # Slide... banner with Read more button;
        Assert.assertTrue(mainPage.isSlideReadMoreButtonDisplayed());

        Assert.assertTrue(mainPage.isNewsAndAnalysisSectionDisplayed());

        // Featured section with articles names links (Ethical funds, Minicards  are now available, Alfresco  Datasheet - Social Computing)
        Assert.assertTrue(mainPage.isFeaturedSectionDisplayed());

        // Example Feature (Investments and advertising campaigns) section with Read more button;
        Assert.assertTrue(mainPage.isExampleFeatureSectionDisplayed());

        // Latest Blog Articles section with blog posts preview (Ethical  funds,Company  organises workshop,Our top  analyst's latest...)
        Assert.assertTrue(mainPage.isLatestBlogArticlesDisplayed());

        System.out.println("a");
    }

    /*
    * AONE-5657 Verify correct navigation from main page
    */
    @AlfrescoTest(testlink = "AONE-5657")
    @Test(groups = {"WQS"})
    public void navigationFromMainPage() throws Exception
    {

        navigateTo(wqsURL);
        WcmqsHomePage mainPage = FactoryWqsPage.resolveWqsPage(drone).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Click News link;
        // ---- Expected results ----
        // User goes to the News page;

        mainPage.selectMenu(WcmqsNewsPage.NEWS_MENU_STR);
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
        Assert.assertTrue(newsPage.getTitle().contains("News"));

        // ---- Step 2 ----
        // ---- Step action ----
        // Click Home link;
        // ---- Expected results ----
        // User goes to main page;

        newsPage.selectMenu(WcmqsHomePage.HOME_MENU_STR);
        mainPage = FactoryWqsPage.resolveWqsPage(drone).render();
        Assert.assertTrue(mainPage.getTitle().contains("Home"));

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Publications link;
        // ---- Expected results ----
        // User goes to the Publications page;

        mainPage.selectMenu(WcmqsAllPublicationsPage.PUBLICATIONS_MENU_STR);
        WcmqsAllPublicationsPage allPublicationsPage = new WcmqsAllPublicationsPage(drone).render();
        Assert.assertTrue(allPublicationsPage.getTitle().contains("Publications"));

        // ---- Step 4 ----
        // ---- Step action ----
        // Click on Alfresco Web Quick Start logo;
        // ---- Expected results ----
        // User goes to main page;

        mainPage = allPublicationsPage.clickWebQuickStartLogo().render();
        Assert.assertTrue(mainPage.getTitle().contains("Home"));

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Blog link;
        // ---- Expected results ----
        // User goes to the Blog page;

        mainPage.selectMenu(WcmqsAbstractPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone).render();
        Assert.assertTrue(blogPage.getTitle().contains("Blog"));

        // ---- Step 6 ----
        // ---- Step action ----
        // Click Contact link;
        // ---- Expected results ----
        // Contact page is opened;

        blogPage.clickContactLink();
        String pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Contact"));

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Alfresco.com link;
        // ---- Expected results ----
        // User goes to Alfresco main page site(http://www.alfresco.com);

        blogPage.clickAlfrescoLink();
        pageTitle = drone.getTitle();
        Assert.assertTrue(pageTitle.contains("Alfresco"));
    }

    /*
    * AONE-5658 Opening articles from main page
    */
    @AlfrescoTest(testlink = "AONE-5658")
    @Test(groups = {"WQS"})
    public void openArticlesMainPage() throws Exception
    {

        navigateTo(wqsURL);
        WcmqsHomePage mainPage = FactoryWqsPage.resolveWqsPage(drone).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Click Media  Consult new site coming out in September link in News and Analysis section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickLinkByTitle(WcmqsNewsPage.FTSE_1000, WcmqsHomePage.SECTION_NEWSLIST);
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FTSE_1000));

        // ---- Step 2 ----
        // ---- Step action ----
        // Return to Main page and click picture preview for Media  Consult new site coming out in September article;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickImageLink(WcmqsNewsPage.FTSE_1000);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FTSE_1000));

        // ---- Step 3 ----
        // ---- Step action ----
        // Return to Main page and click China  eyes shake-up of bank holdings link in News and Analysis section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY, WcmqsHomePage.SECTION_NEWSLIST);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to Main page and click picture preview for China  eyes shake-up of bank holdings article;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickImageLink(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to Main page and click Minicards  are now available link in News and Analysis section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS, WcmqsHomePage.SECTION_NEWSLIST);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));

        // ---- Step 6 ----
        // ---- Step action ----
        // Return to Main page and click picture preview for Minicards  are now available article;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickImageLink(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));

        // ---- Step 7 ----
        // ---- Step action ----
        // Return to Main page and click Ethical funds link in Featured section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS, WcmqsHomePage.SECTION_SERVICES);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 8 ----
        // ---- Step action ----
        // Return to Main page and click Minicards  are now available link in Featured section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS, WcmqsHomePage.SECTION_SERVICES);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));

        // ---- Step 9 ----
        // ---- Step action ----
        // Return to Main page and click Alfresco  Datasheet - Social Computing link in Featured section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsPublicationPage.PUBLICATION_PAGES.get(2), WcmqsHomePage.SECTION_SERVICES);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString("Datasheet_Social_Computing.pdf"));
        navigateTo(wqsURL);

        // ---- Step 10 ----
        // ---- Step action ----
        // Return to Main page and click Read more button for Example Feature section;
        // ---- Expected results ----
        // Page is reloaded or jumps up;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle("read more", WcmqsHomePage.SECTION_ADDRESSBOX);
        assertThat("Verify that page navigation url now has # suffix ", mainPage.getDrone().getCurrentUrl(),
                containsString("wcmqs/#"));

        // ---- Step 11 ----
        // ---- Step action ----
        // Return to Main page and click Ethical funds link in Latest Blog Articles section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS, WcmqsHomePage.SECTION_LATESTNEWS);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 12 ----
        // ---- Step action ----
        // Return to Main page and click Company  organises workshop link in Latest Blog  Articles section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP, WcmqsHomePage.SECTION_LATESTNEWS);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

        // ---- Step 13 ----
        // ---- Step action ----
        // Return to Main page and click Our top  analyst's latest thoughts link in Latest Blog  Articles section;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS, WcmqsHomePage.SECTION_LATESTNEWS);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

        // ---- Step 14 ----
        // ---- Step action ----
        // Return to Main page and click Read More button on animated banner when First Slide article is displayed;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickOnSlideShowReadme(1);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.FTSE_1000));

        // ---- Step 15 ----
        // ---- Step action ----
        // Return to Main page and click Read More button on animated banner when  Second Slide article is displayed;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickOnSlideShowReadme(2);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.EXPERTS_WEIGHT_STOCKS));

        // ---- Step 16 ----
        // ---- Step action ----
        // Return to Main page and click Read More button on animated banner when  Third Slide article is displayed;
        // ---- Expected results ----
        // Article is opened successfully and displayed correctly;

        mainPage.clickWebQuickStartLogo().render();
        mainPage.clickOnSlideShowReadme(3);
        assertThat("Verify if the correct page opened ", mainPage.getTitle(), containsString(WcmqsNewsPage.CREDIT_CARDS));

    }

    /*
    * AONE-5660 Adding new section in wcmqs site
    */
    @AlfrescoTest(testlink = "AONE-5660")
    @Test(groups = {"WQS"})
    public void addNewSection() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to WCMQS site- Alfresco Quick Start - Quick Start Editorial - root folder;
        // ---- Expected results ----
        // root folder is opened;

        ShareUtil.loginAs(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);

        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        Assert.assertNotNull(documentLibraryPage);

        // ---- Step 2 ----
        // ---- Step action ----
        // Create new folder with name accounting and title Accounting Data;
        // ---- Expected results ----
        // New folder is created under root;

        String root_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT;
        //siteActions.navigateToFolder(drone, root_folder_path).render();
        siteActions.createFolder(drone, ACCOUNTING, ACCOUNTING_DATA, root_folder_path).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ACCOUNTING).render();
        Assert.assertNotNull(documentLibraryPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Go to http://host:port/wcmqs;
        // ---- Expected results ----
        // Wcmqs is opened;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = FactoryWqsPage.resolveWqsPage(drone).render();
        Assert.assertNotNull(homePage);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click on Accounting Data section;
        // ---- Expected results ----
        // No erro is displayed. Corect page with Coming soon... notifications is displayed;

        waitAndOpenNewSection(homePage, ACCOUNTING, 4);
        String pageTitle = drone.getTitle();

    }

}