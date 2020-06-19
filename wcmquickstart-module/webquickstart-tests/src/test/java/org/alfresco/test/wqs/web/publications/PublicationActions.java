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
package org.alfresco.test.wqs.web.publications;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.FactoryWqsPage;
import org.alfresco.po.wqs.WcmqsAllPublicationsPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsPublicationPage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.po.wqs.*;
import org.alfresco.test.wqs.web.news.NewsComponent;
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

/**
 * Created by svidrascu on 11/19/2014.
 */

@Listeners(FailedTestListener.class)
public class PublicationActions extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(PublicationActions.class);
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
        loginInfo = new String[] {ADMIN_USERNAME, ADMIN_PASSWORD};
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
        wqsDashlet.waitForImportMessage();

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

    /**
     * AONE-5661:Publications
     */
    @AlfrescoTest(testlink="AONE-5661")
    @Test(groups = "WQS")
    public void verifyPublications() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        //verify that the publications dropdown list exists and has research reports and white papers within it
        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        wcmqsHomePage.mouseOverMenu("publications");
        Assert.assertTrue(wcmqsHomePage.isResearchReportsDisplayed());
        wcmqsHomePage.mouseOverMenu("publications");
        Assert.assertTrue(wcmqsHomePage.isWhitePapersDisplayed());

        //click on research reports and check if the correct page opened
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = wcmqsHomePage.openPublicationsPageFolder("research reports").render();
        Assert.assertTrue(wcmqsAllPublicationsPage.getTitle().contains("Research Reports"));

        //click on white papers and check if the correct page opened
        wcmqsAllPublicationsPage = wcmqsHomePage.openPublicationsPageFolder("white papers").render();
        Assert.assertTrue(wcmqsAllPublicationsPage.getTitle().contains("White Papers"));
    }

    /**
     * AONE-5662:Publications page
     */
    @AlfrescoTest(testlink="AONE-5662")
    @Test(groups = "WQS")
    public void verifyPublicationsPage() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        //open publications page and check if you reached the correct page, if key publications section is displayed, and that publications are displayed with link and description
        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = wcmqsHomePage.selectMenu("publications").render();

        Assert.assertTrue(wcmqsAllPublicationsPage.getTitle().contains("Publications"));
        Assert.assertTrue(wcmqsAllPublicationsPage.getKeyPublicationsSection().isDisplayed());
        Assert.assertTrue(wcmqsAllPublicationsPage.getAllPublictionsTitles().size() > 3);
    }

    /**
     * AONE-5663:Opening Documents from publications page
     */
    @AlfrescoTest(testlink="AONE-5663")
    @Test(groups = "WQS")
    public void openDocumentsFromPublications() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = wcmqsHomePage.selectMenu("publications").render();

        //open publications page using the publication title and check if you reached the correct page
        for (int i = 0; i < 7; i++)
        {
            wcmqsAllPublicationsPage.getAllPublictionsTitles().get(i).openLink();
            WcmqsPublicationPage pageFound = new WcmqsPublicationPage(drone);
            Boolean check = false;
            for (String PageTitle : WcmqsPublicationPage.PUBLICATION_PAGES)
            {
                if (pageFound.getTitle().contains(PageTitle))
                {
                    check = true;
                    break;
                }
            }
            Assert.assertTrue(check, "Publication page did not open correctly");
            wcmqsAllPublicationsPage = pageFound.selectMenu("publications").render();
        }

        //open publications page using the publication image and check if you reached the correct page
        for (int i = 0; i < 7; i++)
        {
            wcmqsAllPublicationsPage.getAllPublictionsImages().get(i).openLink();
            WcmqsPublicationPage pageFound = new WcmqsPublicationPage(drone);;
            Boolean check = false;
            for (String PageTitle : WcmqsPublicationPage.PUBLICATION_PAGES)
            {
                if (pageFound.getTitle().contains(PageTitle))
                {
                    check = true;
                    break;
                }
            }
            Assert.assertTrue(check, "Publication page did not open correctly");
            wcmqsAllPublicationsPage = pageFound.selectMenu("publications").render();
        }
    }

    /**
     * AONE-5664:Verifying publications page
     */
    @AlfrescoTest(testlink="AONE-5664")
    @Test(groups = "WQS")
    public void verifyAlfrescoWcmPublication() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        //Click publications link
        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = wcmqsHomePage.selectMenu("publications").render();

        //Click Alfresco WCM link
        wcmqsAllPublicationsPage.getAllPublictionsTitles().get(0).openLink();

        WcmqsPublicationPage wcmqsPublicationPage = FactoryWqsPage.resolveWqsPage(drone).render();

        //Verify Publication page contains: Publication name, Publication date, Publication preview, Tags section, Publication details section
        Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());

    }

    /**
     * AONE-5665:Verifying publications details
     */
    @AlfrescoTest(testlink="AONE-5665")
    @Test(groups = "WQS")
    public void verifyAlfrescoWcmDetails() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        //Click publications link
        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = wcmqsHomePage.selectMenu("publications").render();

        //Click Alfresco WCM link
        wcmqsAllPublicationsPage.getAllPublictionsTitles().get(0).openLink();
        WcmqsPublicationPage wcmqsPublicationPage = FactoryWqsPage.resolveWqsPage(drone).render();

        //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

        //Verify Document Can Be Downloaded correctly
        File testFile = wcmqsPublicationPage.downloadFiles();
        Assert.assertTrue(testFile.length() > 0);

    }


    /**
     * AONE-5666:Tags
     */
    @AlfrescoTest(testlink="AONE-5666")
    @Test(groups = {"WQS", "ProductBug"})
    public void verifyTags() throws Exception
    {

        // ---- Data prep ----
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();

        String folderPath = DOCLIB + SLASH + ALFRESCO_QUICK_START + SLASH + QUICK_START_EDITORIAL + SLASH + ROOT + SLASH + "publications";
        documentLibPage = siteActions.navigateToFolder(drone, folderPath).render();

        documentLibPage.getFileDirectoryInfo("WCM.pdf").addTag("tag2");
        documentLibPage.selectFolder("white-papers");
        documentLibPage.getFileDirectoryInfo("Datasheet_OEM.pdf").addTag("tag1");
        documentLibPage.getNavigation().clickFolderUp();
        documentLibPage.selectFolder("research-reports");
        documentLibPage.getFileDirectoryInfo("Enterprise_Network_0410.pdf").addTag("tag2");
        ShareUtil.logout(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        //  Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open White papers component in Publications;
        // ---- Expected results ----
        //  White papers component is opened;

        WcmqsHomePage wcmqsHomePage = FactoryWqsPage.resolveWqsPage(drone).render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");

        // ---- Step 3 ----
        // ---- Step action ----
        // Click tag1 for Microsoft  Word - OEM 0510 v2 publication;
        // ---- Expected results ----
        //  Search page is opened, publication Microsoft  Word - OEM 0510 v2 is displayed;

        WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.clickDocumentTag("tag1");
        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Microsoft Word - OEM 0510 v2"),
                "Tag search did not return Microsoft  Word - OEM 0510 v2");

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to White papers page;
        // ---- Expected results ----
        //  White papers component is opened;

        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");

        // ---- Step 5 ----
        // ---- Step action ----
        // Open Microsoft  Word - OEM 0510 v2 publication;
        // ---- Expected results ----
        //  Microsoft  Word - OEM 0510 v2 details page is opened;

        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
        wcmqsAllPublicationsPage.clickLinkByTitle("Microsoft Word - OEM 0510 v2");

        // ---- Step 6 ----
        // ---- Step action ----
        // Click tag1 link;
        // ---- Expected results ----
        //  Search page is opened, publication Microsoft  Word - OEM 0510 v2 is displayed;

        wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.clickDocumentTag("tag1");
        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("0510"), "Tag search did not return Microsoft  Word - OEM 0510 v2");

        // ---- Step 7 ----
        // ---- Step action ----
        // Open Research reports component in Publications;
        // ---- Expected results ----
        //  Research reports component is opened;

        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");

        // ---- Step 8 ----
        // ---- Step action ----
        // Click tag2 for Enterprise  Network publication;
        // ---- Expected results ----
        //  Search page is opened, publication Enterprise  Network and Alfresco WCM publications are displayed;

        wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.clickDocumentTag("tag2");
        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Enterprise Network"),
                "Tag search did not return Enterprise Network --- SEE MNT-12860");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Alfresco WCM"), "Tag search did not return Alfresco WCM");

        // ---- Step 9 ----
        // ---- Step action ----
        // Return to Research reports page;
        // ---- Expected results ----
        //  Research reports component is opened;

        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");

        // ---- Step 10 ----
        // ---- Step action ----
        // Open Enterprise  Network publication;
        // ---- Expected results ----
        //  Enterprise  Network details page is opened;

        wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
        wcmqsAllPublicationsPage.clickLinkByTitle("Enterprise Network");

        // ---- Step 11 ----
        // ---- Step action ----
        // Click tag2 link;
        // ---- Expected results ----
        //  Search page is opened, publication Enterprise  Network and Alfresco WCM  publications are displayed;

        wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.clickDocumentTag("tag2");
        wcmqsSearchPage = new WcmqsSearchPage(drone);
        wcmqsSearchPage.render();
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Enterprise Network"),
                "Tag search did not return Enterprise Network");
        Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Alfresco WCM"), "Tag search did not return Alfresco WCM");

    }

    /**
     * AONE-5667:Publications page
     */
    @AlfrescoTest(testlink="AONE-5667")
    @Test(groups = "WQS")
    public void verifyResearchReports() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify Research reports page;
        // ---- Expected results ----
        // The following items are displayed: *Research reports section(some text in it)
        // Publications section (Publication's name link, Publication's preview, Publications date and author, Publication's description);
        // Tags section;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
        wcmqsAllPublicationsPage.render();
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDescriptionDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationPreviewDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDateAndAuthorDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTagDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTitleDisplay());
    }

    /**
     * AONE-5668:Publications page
     */
    @AlfrescoTest(testlink="AONE-5668")
    @Test(groups = "WQS")
    public void verifyResearchReportsPubl() throws Exception
    {
        drone.navigateTo(wqsURL);
        // ---- Step 1 ----
        // ---- Step action ----
        // Click Enterprise network publication link;
        // ---- Expected results ----
        // Publication is opened successfully, the following items are displayed: Publication name, Publication date, Publication preview, Tags section, Publication details section;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickLinkByTitle("Enterprise Network");

        WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());
        // ---- Step 2 ----
        // ---- Step action ----
        // Return to Research reports page and click Enterprise network publication preview;
        // ---- Expected results ----
        // Publication is opened successfully;
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");
        wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickDocumentImage("Enterprise Network");
        wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());

    }

    /**
     * AONE-5669:Publications page
     */
    @AlfrescoTest(testlink="AONE-5669")
    @Test(groups = "WQS")
    public void verifyResearchReportsPublDetails() throws Exception
    {
        drone.navigateTo(wqsURL);
        // ---- Step 1 ----
        // ---- Step action ----
        // Click Enterprise network publication link;
        // ---- Expected results ----
        // Publications page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("research reports");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickLinkByTitle("Enterprise Network");
        // ---- Step 2 ----
        // ---- Step action ----
        // Verify publication details;
        // ---- Expected results ----
        // Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download);

        WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

        // ---- Step 3 ----
        // ---- Step action ----
        // Click link in Download field;
        // ---- Expected results ----
        // Publication is downloaded;

        File testFile = wcmqsPublicationPage.downloadFiles();
        Assert.assertTrue(testFile.length() > 0);
    }

    /**
     * AONE-5670:Publications - white papers
     */
    @AlfrescoTest(testlink="AONE-5670")
    @Test(groups = "WQS")
    public void verifyWhitePapers() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        drone.navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify Research reports page;
        // ---- Expected results ----
        // The following items are displayed: *white papers section(some text in it)
        // Publications section (Publication's name link, Publication's preview, Publications date and author, Publication's description);
        // Tags section;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);

        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
        wcmqsAllPublicationsPage.render();
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDescriptionDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationPreviewDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDateAndAuthorDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTagDisplay());
        Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTitleDisplay());
    }

    /**
     * AONE-5671:Publications - white papers publications
     */
    @AlfrescoTest(testlink="AONE-5671")
    @Test(groups = "WQS")
    public void verifyWhitePapersPubl() throws Exception
    {
        drone.navigateTo(wqsURL);
        // ---- Step 1 ----
        // ---- Step action ----
        // Click Enterprise network publication link;
        // ---- Expected results ----
        // Publication is opened successfully, the following items are displayed: Publication name, Publication date, Publication preview, Tags section, Publication details section;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickLinkByTitle("Records Management Datasheet");

        WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());
        // ---- Step 2 ----
        // ---- Step action ----
        // Return to Research reports page and click Enterprise network publication preview;
        // ---- Expected results ----
        // Publication is opened successfully;
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");
        wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickDocumentImage("Records Management Datasheet");
        wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());

    }

    /**
     * AONE-5672:Publications - white papers publications details
     */
    @AlfrescoTest(testlink="AONE-5672")
    @Test(groups = "WQS")
    public void verifyWhitePapersPublDetails() throws Exception
    {
        drone.navigateTo(wqsURL);
        // ---- Step 1 ----
        // ---- Step action ----
        // Click Enterprise network publication link;
        // ---- Expected results ----
        // Publications page is opened;

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.mouseOverMenu("publications");
        wcmqsHomePage.openPublicationsPageFolder("white papers");
        WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

        wcmqsAllPublicationsPage.clickLinkByTitle("Records Management Datasheet");
        // ---- Step 2 ----
        // ---- Step action ----
        // Verify publication details;
        // ---- Expected results ----
        // Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download);

        WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
        wcmqsPublicationPage.render();

        //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
        Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

        // ---- Step 3 ----
        // ---- Step action ----
        // Click link in Download field;
        // ---- Expected results ----
        // Publication is downloaded;

        File testFile = wcmqsPublicationPage.downloadFiles();
        Assert.assertTrue(testFile.length() > 0);
    }
}
