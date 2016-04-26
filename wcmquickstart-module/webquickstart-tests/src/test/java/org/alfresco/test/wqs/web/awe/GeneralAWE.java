
package org.alfresco.test.wqs.web.awe;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.*;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
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
 * Created by Lucian Tuca on 12/02/2014.
 */

@Listeners(FailedTestListener.class)
public class GeneralAWE extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(GeneralAWE.class);

    private String testName;
    private String siteName;
    private String ipAddress;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        ipAddress = getIpAddress();
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

    /*
    * AONE-5650 Toggle Edit Markers
    */
    @AlfrescoTest(testlink = "AONE-5650")
    @Test(groups = {"WQS"})
    public void toogleEditMarkers() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any blog post;
        // ---- Expected results ----
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        Assert.assertNotNull(blogPostPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Toggle Edit Markers button on the AWE pannel;
        // ---- Expected results ----
        // Edit Markers are not displayed;

        blogPostPage.clickToggleEditMarkers();
        Assert.assertFalse(blogPostPage.isEditMarkersDisplayed());

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Toggle Edit Markers button on the AWE pannel once more;
        // ---- Expected results ----
        // Edit Markers are displayed;

        blogPostPage.clickToggleEditMarkers();
        Assert.assertTrue(blogPostPage.isEditMarkersDisplayed());
    }

    /*
     * AONE-5651 Orientation
     */
    @AlfrescoTest(testlink = "AONE-5651")
    @Test(groups = {"WQS", "ProductBug"})
    public void orientation() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any blog post;
        // ---- Expected results ----
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        Assert.assertNotNull(blogPostPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // In Orientation menu select Left;
        // ---- Expected results ----
        // AWE pannel is displayed on the left side;

        blogPostPage.changeOrientationLeft();
        Assert.assertTrue(blogPostPage.isAWEOrientedLeft());

        // ---- Step 4 ----
        // ---- Step action ----
        // In Orientation menu select Right;
        // ---- Expected results ----
        // AWE pannel is displayed on the right side;

        blogPostPage.changeOrientationRight();
        Assert.assertTrue(blogPostPage.isAWEOrientedRight());

        // ---- Step 5 ----
        // ---- Step action ----
        // In Orientation menu select Top;
        // ---- Expected results ----
        // AWE pannel is displayed on the top;

        blogPostPage.changeOrientationTop();
        Assert.assertTrue(blogPostPage.isAWEOrientedTop());
    }

    /*
     * AONE-5652 Creating any article via AWE
     */
    @AlfrescoTest(testlink = "AONE-5652")
    @Test(groups = {"WQS"})
    public void createArticleViaAwe() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any article;
        // ---- Expected results ----
        // Article is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage blogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Specify Username/password (e.g. admin/admin) and log in; -- user is already logged in
        // ---- Expected results ----
        // User credentials are specified successfully, user is logged in;

        Assert.assertNotNull(blogPostPage);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Create Article button on the AWE pannel;
        // ---- Expected results ----
        // Create article window is opened;

        WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
        Assert.assertNotNull(editPage);

        // ---- Step 5 ----
        // ---- Step action ----
        // Fill in Name field with test_article.html and content with Hello, world! and save;
        // ---- Expected results ----
        // Information is added successfully, data is saved and displayed correctly, new item is displayed in the list of articles for selected component;

        String articleName = testName + "name.html";
        String articleTitle = testName + "title";
        String articleDescription = testName + "description";
        String articleContent = testName + "content";

        editPage.editName(articleName);
        editPage.editTitle(articleTitle);
        editPage.editDescription(articleDescription);
        editPage.insertTextInContent(articleContent);
        WcmqsBlogPage blogPage = editPage.clickSubmitButton().render();

        WcmqsBlogPostPage newBlogPostPage = waitAndOpenBlogPost(blogPage, articleTitle);

        String actualTitle = newBlogPostPage.getTitle();
        String actualContent = newBlogPostPage.getContent();

        Assert.assertEquals(actualTitle, articleTitle);
        Assert.assertEquals(actualContent, articleContent);
    }

    /*
     * AONE-5653 Creating any article via AWE - Cancel
     */
    @AlfrescoTest(testlink = "AONE-5653")
    @Test(groups = {"WQS"})
    public void createAndCancelArticleViaAwe() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any article;
        // ---- Expected results ----
        // Article is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage blogPostPage = blogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Specify Username/password (e.g. admin/admin) and log in; -- user is already logged in
        // ---- Expected results ----
        // User credentials are specified successfully, user is logged in;

        Assert.assertNotNull(blogPostPage);

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Create Article button on the AWE pannel;
        // ---- Expected results ----
        // Create article window is opened;

        WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
        Assert.assertNotNull(editPage);

        // ---- Step 5 ----
        // ---- Step action ----
        // Fill in Name field with test_article.html and content with Hello, world!;
        // ---- Expected results ----
        // Information is added successfully;

        String articleName = testName + "name.html";
        String articleTitle = testName + "title";
        String articleDescription = testName + "description";
        String articleContent = testName + "content";

        editPage.editName(articleName);
        editPage.editTitle(articleTitle);
        editPage.editDescription(articleDescription);
        editPage.insertTextInContent(articleContent);

        // ---- Step 6 ----
        // ---- Step action ----
        // Click Cancel button;
        // ---- Expected results ----
        // New item creation is canceled, item is not created and not displayed in the list of items;

        WcmqsBlogPostPage newBlogPostPage = editPage.clickCancelButton().render();
        String pageTitle = newBlogPostPage.getTitle();
        Assert.assertTrue(pageTitle.contains(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));
    }

    /*
     * AONE-5654 Editing any article via AWE
     */
    @AlfrescoTest(testlink = "AONE-5654")
    @Test(groups = {"WQS"})
    public void editArticleViaAwe() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any article;
        // ---- Expected results ----
        // Article is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage blogPostPage = blogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();
        Assert.assertNotNull(blogPostPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Edit button on the AWE pannel;
        // ---- Expected results ----
        // Edit window is opened;

        WcmqsEditPage editPage = blogPostPage.clickAWEEditArticle();
        Assert.assertNotNull(editPage);

        // ---- Step 4 ----
        // ---- Step action ----
        // Change information in all fields and save it;
        // ---- Expected results ----
        // Information is changed successfully, data is saved and displayed correctly;
        String beforeEditTitle = editPage.getArticleDetails().getTitle();

        String articleName = testName + System.currentTimeMillis()+ "name.html";
        String articleTitle = testName + System.currentTimeMillis() + "title";
        String articleDescription = testName + "description";
        String articleContent = testName + "content";

        editPage.editName(articleName);
        editPage.editTitle(articleTitle);
        editPage.editDescription(articleDescription);
        editPage.insertTextInContent(articleContent);
        blogPage = editPage.clickSubmitButton().render();

        String editedTitle = beforeEditTitle + articleTitle;
        blogPostPage = waitAndOpenBlogPost(blogPage, articleTitle);

        String actualTitle = blogPostPage.getTitle();
        String actualContent = blogPostPage.getContent();

        Assert.assertEquals(actualTitle, editedTitle);
        Assert.assertEquals(actualContent, articleContent);

    }

    /*
     * AONE-5655 Editing any article via AWE - Cancel
     */
    @AlfrescoTest(testlink = "AONE-5655")
    @Test(groups = {"WQS"})
    public void editAndCancelArticleViaAwe() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any article;
        // ---- Expected results ----
        // Article is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage blogPostPage = blogPage.openBlogPost(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();
        Assert.assertNotNull(blogPostPage);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Edit button on the AWE pannel;
        // ---- Expected results ----
        // Edit window is opened;

        WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
        Assert.assertNotNull(editPage);

        // ---- Step 4 ----
        // ---- Step action ----
        // Change information in all fields;
        // ---- Expected results ----
        // Information is changed successfully;

        String articleName = testName + "name.html";
        String articleTitle = testName + "title";
        String articleDescription = testName + "description";
        String articleContent = testName + "content";

        editPage.editName(articleName);
        editPage.editTitle(articleTitle);
        editPage.editDescription(articleDescription);
        editPage.insertTextInContent(articleContent);

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Cancel button;
        // ---- Expected results ----
        // Article editing is canceled, changes are not saved;

        WcmqsBlogPostPage newBlogPostPage = editPage.clickCancelButton().render();
        String pageTitle = newBlogPostPage.getTitle();
        Assert.assertTrue(pageTitle.contains(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));
    }

}
