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
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by P3700360 on 06.02.2015.
 */

@Listeners(FailedTestListener.class)
public class WcmqsBlogPostPageTest extends AbstractWQS
{
    private static final Logger logger = Logger.getLogger(WcmqsBlogPostPageTest.class);
    DashBoardPage dashBoard;
    private String siteName;
    private String ipAddress;
    private String[] loginInfo;

    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception
    {
        String testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        loginInfo = new String[] { ADMIN_USERNAME, ADMIN_PASSWORD };
        ipAddress = getIpAddress();
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        int columnNumber = 2;
        String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteService.create(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName, "", Site.Visibility.PUBLIC);

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

    }


    private boolean isLoggedIn()
    {
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        if (wqsPage.isLoggedIn() == false)

            return false;
        else
            return true;

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

    private void accessBlog()
    {
        String blog = "Ethical funds";
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.selectMenu("blog");
        WcmqsBlogPage wqsBlogs = new WcmqsBlogPage(drone);
        wqsBlogs.clickBlogNameFromShare(blog);
    }

    @Test
    public void testCreateArticle()
    {
        String cssTitle = "div[class=\'hd\']";
        String title = "Create Article";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.createArticle();
        String createTitle = drone.find(By.cssSelector(cssTitle)).getText();
        Assert.assertEquals(title, createTitle, "Could not 'Create Article' element on page");
    }

    @Test
    public void testGetTitle()
    {
        String blogName = "Ethical funds";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        String title = wqsBlogPost.getTitle();
        Assert.assertEquals(title, blogName);
    }

    @Test
    public void testGetContent()
    {
        String content = "\n" + "\n"
                + "Ius ei eirmod disputationi, impetus mentitum has id. Quot omnis pertinacia vim in, vel sanctus invenire prodesset eu. Sea an reque saepe expetenda, eos quando everti corrumpit cu.\n"
                + "\n"
                + "Nonumy postea vivendum his at, dicit ornatus eos ut. Mei id tantas eligendi, his vidit tritani ut. In doctus expetendis per. Ex virtute contentiones intellegebat eam, ne vel graecis suscipit tractatos, mel elitr vocibus adolescens in. Feugait efficiendi no eam, ea sea quis nisl eirmod, ut salutandi adolescens incorrupte prin.\n"
                + "\n"
                + "Ne nobis impedit instructior vim. Equidem feugait argumentum eam ea, ne rebum facete blandit vis, admodum repudiandae delicatissimi vis an. Eu ullum possim definitionem eos, ad vix putent dictas moderatius, ut mollis vivendo indoctum sea. Ius ei eirmod disputationi, impetus mentitum has id.\n"
                + "\n"
                + "Et qui oblique utroque omnesque, vix at utinam evertitur inciderint. Sea nominavi referrentur ex, eum an ludus choro. No eirmod efficiendi temporibus vel, at fugit postea recusabo ius, nam fugit dicunt disputando ea.\n";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        String findContent = wqsBlogPost.getContent();
        Assert.assertEquals(findContent, content);
    }

    @Test
    public void testGetVisitorName()
    {
        String visitorName = "Name";
        String cssVisitorName = "input[name='visitorName']";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false)
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorName(visitorName);
        String getName = wqsBlogPost.getVisitorName();
        String name = drone.find(By.cssSelector(cssVisitorName)).getAttribute("value");
        Assert.assertEquals(getName, name);
    }

    @Test
    public void testGetVisitorEmail()
    {
        String visitorEmail = "test@test.com";
        String cssVisitorEmail = "input[name='visitorEmail']";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorEmail(visitorEmail);
        String getEmail = wqsBlogPost.getVisitorName();
        String email = drone.find(By.cssSelector(cssVisitorEmail)).getAttribute("value");
        Assert.assertEquals(getEmail, email);
    }

    @Test
    public void testGetVisitorWebsite()
    {
        String visitorWebsite = "test.com";
        String cssVisitorWebsite = "input[name='visitorWebsite']";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorWebsite(visitorWebsite);
        String getWebsite = wqsBlogPost.getVisitorName();
        String website = drone.find(By.cssSelector(cssVisitorWebsite)).getAttribute("value");
        Assert.assertEquals(website, getWebsite);
    }

    @Test
    public void testSetVisitorComment()
    {
        String visitorComment = "Test Comment";
        String cssVisitorComment = "textarea.bc-textarea";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorComment(visitorComment);
        String comment = drone.find(By.cssSelector(cssVisitorComment)).getAttribute("value");
        Assert.assertEquals(visitorComment, comment);
    }

    @Test
    public void testDeleteArticle()
    {
        String cssTitle = "div[class=\'hd\']";
        String title = "Confirm Delete";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.deleteArticle();
        String confirmDelete = drone.find(By.cssSelector(cssTitle)).getText();
        Assert.assertEquals(title, confirmDelete, "Could not 'Confirm Delete' title on page");
    }

    @Test
    public void testIsDeleteConfirmationWindowDisplayed()
    {

    }

    @Test
    public void testClickPostButton()
    {
        String cssMsg = "div[class='contact-success']>p']";
        String message = "Your comment has been sent!";
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String visitorComment = "Test Comment";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorName(visitorName);
        wqsBlogPost.setVisitorEmail(visitorEmail);
        wqsBlogPost.setVisitorComment(visitorComment);
        wqsBlogPost.clickPostButton();
        String findMsg = drone.find(By.cssSelector(cssMsg)).getText(); //getAttribute("value");
        Assert.assertEquals(findMsg, message);
    }

    @Test
    public void testEditArticle()
    {
        String cssTitle = "div[class=\'hd\']";
        String title = "Edit Ethical funds";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
//                wqsBlogPost.editArticle();
        String editBlog = drone.find(By.cssSelector(cssTitle)).getText();
        Assert.assertEquals(title, editBlog, "Could not 'Edit' title on page");
    }

    @Test
    public void testReportLastCreatedPost()
    {
        String cssReport = "span[class=comments-text]";
        String msg = "*** This comment has been removed. ***";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.reportLastCreatedPost();
        Assert.assertEquals(cssReport, drone.find(By.cssSelector(cssReport)).getText());
    }

    @Test
    public void testIsAddCommentMessageDisplay()
    {
        String cssMsg = "div.contact-success";
        String message = "Your comment has been sent!";
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String visitorComment = "Test Comment";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.setVisitorName(visitorName);
        wqsBlogPost.setVisitorEmail(visitorEmail);
        wqsBlogPost.setVisitorComment(visitorComment);
        wqsBlogPost.clickPostButton();
        Assert.assertEquals(wqsBlogPost.isAddCommentMessageDisplay(), drone.find(By.cssSelector(cssMsg)).isDisplayed());
    }

    @Test
    public void testIsLeaveCommentFormDisplayed()
    {
        String cssForm = ".blog-comment-fieldset";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        Assert.assertEquals(wqsBlogPost.isLeaveCommentFormDisplayed(), drone.find(By.cssSelector(cssForm)).isDisplayed());
    }

    @Test
    public void testIsFormProblemsMessageDisplay()
    {
        String cssMsg = "div.contact-error";
        String cssForm = ".blog-comment-fieldset";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.clickPostButton();
        Assert.assertEquals(wqsBlogPost.isFormProblemsMessageDisplay(), drone.find(By.cssSelector(cssForm)).isDisplayed());
    }

    @Test
    public void testGetFormErrorMessages()
    {
        String cssMsg = "//span[contains(@class,\"contact-error-value\")]";
        String cssForm = ".blog-comment-fieldset";
        WcmqsBlogPostPageTest blogPost = new WcmqsBlogPostPageTest();
        blogPost.accessBlog();
        if (blogPost.isLoggedIn() == false) ;
        {
            blogPost.LogIn();
        }
        WcmqsBlogPostPage wqsBlogPost = new WcmqsBlogPostPage(drone);
        wqsBlogPost.clickPostButton();
        Assert.assertEquals(wqsBlogPost.getFormErrorMessages(), drone.find(By.cssSelector(cssMsg)).getText());
    }

    @Test
    public void testGetCommentSection()
    {

    }

    @Test
    public void testClickToggleEditMarkers()
    {

    }
}
