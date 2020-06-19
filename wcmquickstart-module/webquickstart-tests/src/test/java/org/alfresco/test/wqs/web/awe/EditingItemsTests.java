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
package org.alfresco.test.wqs.web.awe;

import java.util.List;
import java.util.Map;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.wqs.*;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.*;

@Listeners(FailedTestListener.class)
public class EditingItemsTests extends AbstractWQS
{
    public static final String BLOG_FILE1 = "blog1.html";
    public static final String BLOG_FILE2 = "blog2.html";
    public static final String BLOG_FILE3 = "blog3.html";
    public static final String NEWS_FILE1 = "article1.html";
    public static final String NEWS_FILE2 = "article2.html";
    public static final String NEWS_FILE3 = "article3.html";
    public static final String NEWS_FILE4 = "article4.html";
    public static final String NEWS_FILE5 = "article5.html";
    public static final String NEWS_FILE6 = "article6.html";
    public static final String SLIDE_FILE1 = "slide1.html";
    public static final String SLIDE_FILE2 = "slide2.html";
    public static final String SLIDE_FILE3 = "slide3.html";
    private static final Log logger = LogFactory.getLog(EditingItemsTests.class);
    private String testName;
    private String ipAddress;
    private String siteName;
    private String newTitle = " title edited";
    private String newDescription = " description edited";
    private String newContent = "content edited";
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


        loginActions.loginToShare(drone, loginInfo, shareUrl);


        siteService.create(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName, "", Site.Visibility.PUBLIC);

        SiteDashboardPage siteDashBoard = (SiteDashboardPage) siteActions.openSiteDashboard(drone, siteName);
        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        siteDashBoard = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        SiteWebQuickStartDashlet wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
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
     * AONE-5619:Editing "Ethical funds" blog post
     */
    @AlfrescoTest(testlink="AONE-5619")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingEthicalFundBlogPost() throws Exception
    {
        String blogName = "Ethical funds";
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
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        Assert.assertTrue(blogPostPage.getTitle().contains(blogName), "Blog :" + blogName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near blog post;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsBlogPage blogsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE1);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE1 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE1 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE1).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE1 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), BLOG_FILE1, "Name Property is not " + BLOG_FILE1);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE1 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE1 + " was not updated.");
        // Assert.assertTrue(properties.get("Template Name").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE1+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5620:Editing "Company organises workshop" blog post
     */
    @AlfrescoTest(testlink="AONE-5620")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingCompanyOrgWorkshopBlogPost() throws Exception
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
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage blogPostPage = blogPage.openBlogPost(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        Assert.assertTrue(blogPostPage.getTitle().contains(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP), "Blog :" + WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP
                + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near blog post;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsBlogPage blogsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog2.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE2);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE2 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE2 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE2).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE2 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), BLOG_FILE2, "Name Property is not " + BLOG_FILE2);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE2 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE2 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE2+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5621:Editing "Our top analyst's latest..." blog post
     */
    @AlfrescoTest(testlink="AONE-5621")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingOurTopAnalystBlogPost() throws Exception
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
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage blogPostPage = blogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();
        Assert.assertTrue(blogPostPage.getTitle().contains(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS), "Blog :" + WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS
                + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near blog post;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsBlogPage blogsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog3.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE3);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE3 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE3 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE3).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE3 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), BLOG_FILE3, "Name Property is not " + BLOG_FILE3);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE3 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE3 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE3+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5622:Editing "Europe dept...."article (Global economy)
     */
    @AlfrescoTest(testlink="AONE-5622")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingEuropeDeptArticle() throws Exception
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
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.GLOBAL, WcmqsNewsPage.EUROPE_DEPT_CONCERNS).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(WcmqsNewsPage.EUROPE_DEPT_CONCERNS), "News article: " + WcmqsNewsPage.EUROPE_DEPT_CONCERNS
                + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/global) and verify article4.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("global").render();

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE4);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE4 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE4 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE4).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE4 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE4, "Name Property is not " + NEWS_FILE4);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE4 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE4 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE4+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5623:Editing "Media Consult new site...."article (Global economy)
     */
    @AlfrescoTest(testlink="AONE-5623")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingMediaConsultArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.FTSE_1000;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Media Consult new site coming out in September" article in Global economy (News);
        // ---- Expected results ----
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.GLOBAL, newsName).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/global) and verify article3.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("global").render();

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE3);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE3 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE3 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE3).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE3 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE3, "Name Property is not " + NEWS_FILE3);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE3 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE3 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE3+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5624:Editing "China eyes shake-up...."article (Companies)
     */
    @AlfrescoTest(testlink="AONE-5624")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingChinaEyesArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.GLOBAL_CAR_INDUSTRY;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "China eyes shake-up of bank holding" article in Company News (News);
        // ---- Expected results ----
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.COMPANIES, newsName).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/companies) and verify article2.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("companies").render();
        documentLibraryPage.getNavigation().selectDetailedView().render();


        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE2);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE2 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE2 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE2).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE2 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE2, "Name Property is not " + NEWS_FILE2);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE2 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE2 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE2+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5625:Editing "Minicards are now available" article (Companies)
     */
    @AlfrescoTest(testlink="AONE-5625")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingMinicardsAvailableArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Minicards are now available" article in Company News (News);
        // ---- Expected results ----
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.COMPANIES, newsName).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        wcmqsEditPage.clickSubmitButton();

        WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
        newsPage2.render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/companies) and verify article1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("companies").render();

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE1);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE1 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE1 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE1).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE1 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE1, "Name Property is not " + NEWS_FILE1);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE1 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE1 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE1+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5626:Editing "Investors fear rising risk..." article (Markets)
     */
    @AlfrescoTest(testlink="AONE-5626")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingInventorsFearRiskArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.INVESTORS_FEAR;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Investors fear rising risk of US regional defaults" article in Markets (News);
        // ---- Expected results ----
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.MARKETS, newsName).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/markets) and verify article6.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("markets").render();

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE6);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE6 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE6 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE6).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE6 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE6, "Name Property is not " + NEWS_FILE6);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE6 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE6 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE6+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5627:Editing "Our new brochure is now available" article (Markets)
     */
    @AlfrescoTest(testlink="AONE-5627")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingOurNewBrochureArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.HOUSE_PRICES;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open "Our new brochure is now available" article in Markets (News);
        // ---- Expected results ----
        // Article is opened;
        WcmqsNewsArticleDetails newsArticlePage = openNewsFromCategory(WcmqsNewsPage.MARKETS, newsName).render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/markets) and verify article5.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("markets").render();

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE5);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE5 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE5 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE5).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE5 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), NEWS_FILE5, "Name Property is not " + NEWS_FILE5);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE5 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE5 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE5+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5628:Editing "First slide" article
     */
    @AlfrescoTest(testlink="AONE-5628")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingFirstSlideArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.FTSE_1000;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // On main page click "Read more" on the animated banner when "First slide" is displayed;;
        // ---- Expected results ----
        // Article is opened;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE1);
        WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
        newsArticlePage.render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
        String titles = rightTitles.toString();
        Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide1.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE1);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE1 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE1 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE1).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE1 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), SLIDE_FILE1, "Name Property is not " + SLIDE_FILE1);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE1 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + SLIDE_FILE1 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5629:Editing "Second slide" article
     */
    @AlfrescoTest(testlink="AONE-5629")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingSecondSlideArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.EXPERTS_WEIGHT_STOCKS;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // On main page click "Read more" on the animated banner when "Second slide" is displayed;
        // ---- Expected results ----
        // Article is opened;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE2);
        WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
        newsArticlePage.render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
        String titles = rightTitles.toString();
        Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide2.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE2);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE2 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE2 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE2).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE2 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), SLIDE_FILE2, "Name Property is not " + SLIDE_FILE2);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE2 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + SLIDE_FILE2 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        ShareUtil.logout(drone);
    }

    /**
     * AONE-5630:Editing "Third slide" article
     */
    @AlfrescoTest(testlink="AONE-5630")
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void editingThirdSlideArticle() throws Exception
    {
        String newsName = WcmqsNewsPage.CREDIT_CARDS;

        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // On main page click "Read more" on the animated banner when "Third slide" is displayed;
        // ---- Expected results ----
        // Article is opened;
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.render();
        wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE3);
        WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
        newsArticlePage.render();
        Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near article;
        // ---- Expected results ----
        // Edit window is opened;
        WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that all fields are displayed correctly incl. Content field;
        // ---- Expected results ----
        // All fields are displayed correctly, article content is displayed in Content field;
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
        Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
        Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;

        // String newTemplateName="template";
        wcmqsEditPage.editTitle(newTitle);
        wcmqsEditPage.editDescription(newDescription);
        wcmqsEditPage.insertTextInContent(newContent);
        // wcmqsEditPage.editTemplateName(newTemplateName);
        WcmqsNewsPage newsPage2 = wcmqsEditPage.clickSubmitButton().render();
        List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
        String titles = rightTitles.toString();
        Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");
        waitForDocumentsToIndex();

        // ---- Step 6 ----
        // ---- Step action ---
        // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide3.html file;
        // ---- Expected results ----
        // Changes made via AWE are dislpayed correctly
        ShareUtil.loginAs(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE3);
        Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE3 + " was not updated.");
        Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE3 + " was not updated.");

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE3).render();
        Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE3 + " was not updated.");
        Map<String, Object> properties = docDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), SLIDE_FILE3, "Name Property is not " + SLIDE_FILE3);
        Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE3 + " was not updated.");
        Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + SLIDE_FILE3 + " was not updated.");
        // Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        ShareUtil.logout(drone);
    }

}
