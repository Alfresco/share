
package org.alfresco.test.wqs.web.blog;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.items.VisitorFeedbackRow;
import org.alfresco.po.share.site.datalist.items.VisitorFeedbackRowProperties;
import org.alfresco.po.share.site.datalist.lists.VisitorFeedbackList;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.wqs.FactoryWqsPage;
import org.alfresco.po.wqs.WcmqsBlogPage;
import org.alfresco.po.wqs.WcmqsBlogPostPage;
import org.alfresco.po.wqs.WcmqsComment;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsSearchPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;


/**
 * Created by Lucian Tuca on 12/02/2014. Modified by Sergiu Vidrascu on
 * 01/09/2015
 */

@Listeners(FailedTestListener.class)
public class BlogComponent extends AbstractWQS
{

    private static final Log logger = LogFactory.getLog(BlogComponent.class);
    private String siteName;
    private String ipAddress;
    private String[] loginInfo;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        siteName = this.getClass().getSimpleName() + System.currentTimeMillis();
        ipAddress = getIpAddress();
        loginInfo = new String[]{ADMIN_USERNAME, ADMIN_PASSWORD};
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // User login
        // ---- Step 1 ----
        // ---- Step Action -----
        // WCM Quick Start is installed; - is not required to be executed
        // automatically
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

        siteActions.openSiteDashboard(drone, siteName).render();
        // Data Lists component is added to the site
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        SiteDashboardPage siteDashboardPage1 = customizeSitePage.addPages(addPageTypes).render();

        // Site Dashboard is rendered with Data List link
        siteActions.openSiteDashboard(drone, siteName).render();
        ShareUtil.logout(drone);

        waitForWcmqsToLoad();
        loginToWqsFromHomePage();

    }

    @AfterClass(alwaysRun = true)
    public void tearDownAfterClass()
    {
        logger.info("Delete the site after tests run.");

        siteService.delete(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName);
        super.tearDown();

    }

    /*
     * AONE-5673 Blogs page
     */
    @AlfrescoTest(testlink = "AONE-5673")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void blogsPage() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify Blog page;
        // ---- Expected results ----

        // Pagination
        WcmqsHomePage homePage = FactoryWqsPage.resolveWqsPage(drone).render();
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

        WcmqsBlogPage blogPage = FactoryWqsPage.resolveWqsPage(drone).render();
        Assert.assertEquals(blogPage.getBlogPosts(), 3);

        // The following items are displayed:
        // Sample blog posts (Post title link, Creation date, Creator name,
        // #comments link, Read more button)
        // Section Tags (list of tags with number of tags in brackets)

        Assert.assertTrue(blogPage.isBlogDisplayed(WcmqsBlogPage.ETHICAL_FUNDS));
        Assert.assertTrue(blogPage.isBlogPostDateDisplayed());
        Assert.assertTrue(blogPage.isBlogPostCreatorDisplayed());
        Assert.assertTrue(blogPage.isBlogPostCommentsLinkDisplayed());
    }

    /*
     * AONE-5674 Opening blog post
     */
    @AlfrescoTest(testlink = "AONE-5674")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void openBlogPost() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open Blogs page;
        // ---- Expected results ----
        // Blogs page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click 'Ethical funds' blog name link;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 4 ----
        // ---- Step action ----
        // Return to Blogs page and click Company organises workshop blog name
        // link;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage.render();
        blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

        // ---- Step 5 ----
        // ---- Step action ----
        // Return to Blogs page and click Our top analyst's latest... blog name
        // link;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage.render();
        blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

        // ---- Step 6 ----
        // ---- Step action ----
        // Return to Blogs page and click Read more button for 'Ethical funds'
        // for blog;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage.render();
        blogPage.clickReadMoreByBlog(WcmqsBlogPage.ETHICAL_FUNDS);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 7 ----
        // ---- Step action ----
        // Return to Blogs page and click Read more button for 'Company
        // organises workshop for blog;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage.render();
        blogPage.clickReadMoreByBlog(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

        // ---- Step 8 ----
        // ---- Step action ----
        // Return to Blogs page and click Read more button for Our top analyst's
        // latest... for blog;
        // ---- Expected results ----
        // Blog post is opened successfully and displayed correctly;

        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage.render();
        blogPage.clickReadMoreByBlog(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

    }

    /*
     * AONE-5675 Pagination
     */
    @AlfrescoTest(testlink = "AONE-5675")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void pagination() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Blogs page;
        // ---- Expected results ----
        // Blogs page is opened, created blogs are dislpayed correctly;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Check the number of displayed items;
        // ---- Expected results ----
        // Only three latest items are displayed, Sectionpage2 field is limited
        // to 3 latest items;

        assertThat("Verify if the correct number of blog pages is displayed ", blogPage.getBlogPosts(), is(equalTo(3)));

    }

    /*
     * AONE-5676 Commenting a blog post
     */
    @AlfrescoTest(testlink = "AONE-5676")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void commentBlogPost() throws Exception
    {

        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open any blog post (e.g. blog1.html);
        // ---- Expected results ----
        // Blog post is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        assertThat("Verify if the correct page opened ", wcmqsBlogPostPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 3 ----
        // ---- Step action ----
        // Specify mandatory information on Comment form and save comment;
        // ---- Expected results ----
        // Information is saved successfully and displayed correctly;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();
        wcmqsBlogPostPage.render();

        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        homePage = wcmqsBlogPostPage.clickWebQuickStartLogo().render();
        homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsComment wcmqsComment = new WcmqsComment(drone);
        assertThat(wcmqsComment.getNameFromContent(), is(equalTo(visitorName)));
        assertThat(wcmqsComment.getCommentFromContent(), is(equalTo(visitorComment)));
        waitForDocumentsToIndex();

        // ---- Step 4 ----
        // ---- Step action ----
        // Open My Web Site via Alfresco Share;
        // ---- Expected results ----
        // Site is opened successfully;

        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage docLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();

        // ---- Step 5 ----
        // ---- Step action ----
        // Go to Data Lists component;
        // ---- Expected results ----
        // Data lists component is opened, Visitor Feedback (Alfresco WCM Quick
        // Start) data list is displayed by default;

        DataListPage dataListPage = docLibPage.getSiteNav().selectDataListPage().render();

        // ---- Step 6 ----
        // ---- Step action ----
        // Open Visitor Feedback (Alfresco WCM Quick Start) data list;
        // ---- Expected results ----
        // New feedback item is displayed, it consists information entered on
        // step 4.

        dataListPage.selectDataList("Visitor Feedback (Quick Start Editorial)");
        VisitorFeedbackList feedbackList = new VisitorFeedbackList(drone);
        feedbackList.render();
        VisitorFeedbackRow newFeedback = feedbackList.getRowForSpecificValues(visitorEmail, visitorComment, visitorName, visitorWebsite);
        assertThat(newFeedback.getVisitorName(), is(equalTo(visitorName)));
        assertThat(newFeedback.getVisitorEmail(), is(equalTo(visitorEmail)));
        assertThat(newFeedback.getVisitorComment(), is(equalTo(visitorComment)));
        assertThat(newFeedback.getVisitorWebsite(), is(equalTo(visitorWebsite)));
        ShareUtil.logout(drone);

    }

    /*
     * AONE-5677 Verify correct work of comments number value
     */
    @AlfrescoTest(testlink = "AONE-5677")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyCommentsNumberValue() throws Exception
    {
        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        navigateTo(wqsURL);

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();
        waitForDocumentsToIndex();
        // ---- Step 1 ----
        // ---- Step action ----
        // Open My Web Site via Alfresco Share;
        // ---- Expected results ----
        // Site is opened successfully;
        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        SiteDashboardPage shareSite = siteActions.openSiteDashboard(drone, siteName).render();

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to Data Lists component;
        // ---- Expected results ----
        // Data lists component is opened, Visitor Feedback (Alfresco WCM Quick
        // Start) data list is displayed by default;

        DataListPage dataListPage = shareSite.getSiteNav().selectDataListPage().render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Open Visitor Feedback (Alfresco WCM Quick Start) data list;
        // ---- Expected results ----
        // Visitor Feedback data list is opened;

        dataListPage.selectDataList("Visitor Feedback (Quick Start Editorial)");
        VisitorFeedbackList feedbackList = new VisitorFeedbackList(drone);
        feedbackList.render();

        // ---- Step 4 ----
        // ---- Step action ----
        // Click Duplicate button for prevoiusly created(in WCMQS-12) comment;
        // ---- Expected results ----
        // Item duplicated message is shown;

        VisitorFeedbackRow testrow = feedbackList.getRowForVisitorEmail(visitorEmail);
        testrow.clickDuplicateOnRow();
        assertThat("Check if the duplicate message appears!", testrow.isDuplicateMessageDisplayed());
        waitForDocumentsToIndex();
        ShareUtil.logout(drone);

        // ---- Step 5 ----
        // ---- Step action ----
        // Navigate to blog1 article page;
        // ---- Expected results ----
        // Blog1 is opened;

        navigateTo(wqsURL);
        waitForWcmqsToLoad();
        homePage = new WcmqsHomePage(drone);
        blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 6 ----
        // ---- Step action ----
        // Verify number of comments;
        // ---- Expected results ----
        // Number of comments increased;

        WcmqsComment wcmqsComment = new WcmqsComment(drone).render();
        assertThat(wcmqsComment.getNumberOfCommentsOnPage(), is(greaterThan(2)));

    }

    /*
     * AONE-5678 Creating comment with wildcards in blog
     */
    @AlfrescoTest(testlink = "AONE-5678")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void createCommentWithWildcards() throws Exception
    {
        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter some data, containing wildcards in the Name field;
        // ---- Expected results ----
        // Data entered successfully;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        wcmqsBlogPostPage.setVisitorName(visitorName + "!@#$%^&*");

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Please fix the problems indicated below. Thank you. the name you
        // entered contains invalid characters message is shown;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();

        assertThat("Main form error message is displayed! ", wcmqsBlogPostPage.isFormProblemsMessageDisplay());
        assertThat("Verify name field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(),
                hasItem(equalTo("the name you entered contains invalid characters")));

        // ---- Step 3 ----
        // ---- Step action ----
        // Fill name field with correct data and click Post button again;
        // ---- Expected results ----
        // Comment is saved successfully;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        // ---- Step 4 ----
        // ---- Step action ----
        // Open any blog post again;
        // ---- Expected results ----
        // Blog post is opened;

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

        // ---- Step 5 ----
        // ---- Step action ----
        // Enter some data, containing wildcards in the Email field;
        // ---- Expected results ----
        // Data entered successfully

        wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail + "!@#$%^&*");

        // ---- Step 6 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Please fix the problems indicated below. Thank you.the email address
        // is not valid message is shown;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();

        assertThat("Main form error message is displayed! ", wcmqsBlogPostPage.isFormProblemsMessageDisplay());
        assertThat("Verify name field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("the email address is not valid")));

        // ---- Step 7 ----
        // ---- Step action ----
        // Fill mail field with correct data and click Post button again;
        // ---- Expected results ----
        // Comment is saved successfully;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        // ---- Step 8 ----
        // ---- Step action ----
        // Open any blog post again;
        // ---- Expected results ----
        // Blog post is opened;

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);

        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 9 ----
        // ---- Step action ----
        // Enter some data, containing wildcards in the Website field;
        // ---- Expected results ----
        // Data entered successfully;

        wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite + "!@#$%^&*");

        // ---- Step 10 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Comment is saved successfully;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

    }

    /*
     * AONE-5680 Verifying correct work of name field on comment form
     * Jira issue #ACE-3714
     */
    @AlfrescoTest(testlink = "AONE-5680")
    @Test(groups = {"WQS", "EnterpriseOnly", "ProductBug"})
    public void verifyNameFieldFromComment() throws Exception
    {
        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

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
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Enter data in Name field ended with space;
        // ---- Expected results ----
        // Data entered successfully;

        wcmqsBlogPostPage.setVisitorName(visitorName + "     ");

        // ---- Step 4 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Data processed correctly, Your comment has been sent message is
        // displayed;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();
        assertThat("Check if posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

    }

    /*
     * AONE-5681 Commenting blog post with empty mandatory fields
     */
    @AlfrescoTest(testlink = "AONE-5681")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void emptyFieldsInComment() throws Exception
    {
        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Do not enter any data and click Post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        wcmqsBlogPostPage.clickPostButton();
        assertThat("Main form error message is displayed! ", wcmqsBlogPostPage.isFormProblemsMessageDisplay());

        // ---- Step 2 ----
        // ---- Step action ----
        // Leave Name(mandatory) field empty, fill other fields with correct
        // data;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify name field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("please enter a name")));

        // ---- Step 4 ----
        // ---- Step action ----
        // Leave Email(mandatory) field empty, fill other fields with correct
        // data;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail("");

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify email field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("please enter an email address")));

        // ---- Step 6 ----
        // ---- Step action ----
        // Leave Website field empty, fill other fields with correct data;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite("");

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Post is saved successfully;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        // ---- Step 8 ----
        // ---- Step action ----
        // Open Blog post again;
        // ---- Expected results ----
        // Blog post is opened;

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);

        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

        // ---- Step 9 ----
        // ---- Step action ----
        // Leave Comment field empty, fill other fields with correct data;
        // ---- Expected results ----
        // Data is entered successfully;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);

        // ---- Step 10 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify comment textfield error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("please enter a comment")));
    }

    /*
     * AONE-5682 Checking correct work of Email field
     */
    @AlfrescoTest(testlink = "AONE-5682")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyEmailField() throws Exception
    {
        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter some email address without @ in Email field(e.g.
        // mail_this.com);
        // ---- Expected results ----
        // Data is entered sucessfully;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

        wcmqsBlogPostPage.setVisitorEmail("mail_this.com");

        // ---- Step 2 ----
        // ---- Step action ----
        // Fill other fields with correct data;
        // ---- Expected results ----
        // Data is entered sucessfully;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify invalid email error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("the email address is not valid")));

        // ---- Step 4 ----
        // ---- Step action ----
        // Enter some incorrect data in Email field(e.g. !#$@^*%.$)#);
        // ---- Expected results ----
        // Data is entered sucessfully;

        wcmqsBlogPostPage.setVisitorEmail("!#$@^*%.$");

        // ---- Step 5 ----
        // ---- Step action ----
        // Click post button;
        // ---- Expected results ----
        // Friendly notification is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify invalid email error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("the email address is not valid")));

        // ---- Step 6 ----
        // ---- Step action ----
        // Enter some incorrect data woithout . in Email field(e.g.
        // mail1@comcom);
        // ---- Expected results ----
        // Data is entered sucessfully;

        wcmqsBlogPostPage.setVisitorEmail("mail@comcom");

        // ---- Step 7 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Friendly notificetion is displayed;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Verify invalid email error is displayed", wcmqsBlogPostPage.getFormErrorMessages(), hasItem(equalTo("the email address is not valid")));

        // ---- Step 8 ----
        // ---- Step action ----
        // Enter some valid email address in Email field(e.g. mail1@tt.com);
        // ---- Expected results ----
        // Data is entered sucessfully;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);

        // ---- Step 9 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Comment is saved and displayed correctly;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

    }

    /*
     * AONE-5683 Reporting post
     */
    @AlfrescoTest(testlink = "AONE-5683")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void reportPost() throws Exception
    {

        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;
        String feedBack = getTestName() + " Test feedback Subject";

        // ---- Step 1 ----
        // ---- Step action ----
        // Click Report this function;
        // ---- Expected results ----
        // This comment has been removed message is shown;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
        wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        wcmqsBlogPostPage.reportLastCreatedPost();
        waitForDocumentsToIndex();

        // ---- Step 2 ----
        // ---- Step action ----
        // Navigate to Visitor Feedback (Alfresco Share - Site' Name - Data
        // Lists);
        // ---- Expected results ----
        // Visitor Feedback data list is opened;

        navigateTo(shareUrl);
        loginActions.loginToShare(drone, loginInfo, shareUrl);

        SiteDashboardPage siteDashboardPage2 = siteActions.openSiteDashboard(drone, siteName).render();
        DataListPage dataListPage = siteDashboardPage2.getSiteNav().selectDataListPage().render();
        dataListPage.selectDataList("Visitor Feedback (Quick Start Editorial)");
        VisitorFeedbackList feedbackList = new VisitorFeedbackList(drone);
        feedbackList.render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the presense of recently added comment;
        // ---- Expected results ----
        // Recently added comment is present in Visitor Feedback data list;

        VisitorFeedbackRow newFeedback = feedbackList.getRowForSpecificValues(visitorEmail, visitorComment, visitorName, visitorWebsite);
        assertThat(newFeedback.getVisitorName(), is(equalTo(visitorName)));
        assertThat(newFeedback.getVisitorEmail(), is(equalTo(visitorEmail)));
        assertThat(newFeedback.getVisitorComment(), is(equalTo(visitorComment)));
        assertThat(newFeedback.getVisitorWebsite(), is(equalTo(visitorWebsite)));

        // ---- Step 4 ----
        // ---- Step action ----
        // Verify Comment has been flagged field;
        // ---- Expected results ----
        // Comment has been flagged is checked;

        assertThat(newFeedback.getCommnetFlag(), is(equalTo("true")));

        // ---- Step 5 ----
        // ---- Step action ----
        // Click Edit button;
        // ---- Expected results ----
        // Edit data item form is shown;
        feedbackList.getDrone().maximize();
        newFeedback.clickEditOnRow();

        // ---- Step 6 ----
        // ---- Step action ----
        // Uncheck Comment has been flagged checkbox;
        // ---- Expected results ----
        // Comment has been flagged checkbox is unchecked;

        VisitorFeedbackRowProperties visitorFeedbackRowProperties = new VisitorFeedbackRowProperties(drone).render();
        visitorFeedbackRowProperties.clickCommentFlag();

        // ---- Step 7 ----
        // ---- Step action ----
        // Fill Feedback Subject field with any valid data;
        // ---- Expected results ----
        // Data is entered in Feedback Subject field;

        visitorFeedbackRowProperties.setFeedbackSubject(feedBack);
        visitorFeedbackRowProperties.clickSave();
        feedbackList.render();
        ShareUtil.logout(drone);

        // ---- Step 8 ----
        // ---- Step action ----
        // Go back to Blog post page;
        // ---- Expected results ----
        // Blog post page is opened;

        navigateTo(wqsURL);
        waitForWcmqsToLoad();
        homePage = new WcmqsHomePage(drone);
        WcmqsSearchPage wcmqsSearchPage = homePage.searchText(WcmqsBlogPage.ETHICAL_FUNDS).render();
        wcmqsBlogPostPage = wcmqsSearchPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

        // ---- Step 9 ----
        // ---- Step action ----
        // Verify the presence of edited comment;
        // ---- Expected results ----
        // Recently edited comment is present on Blog post page;

        assertThat("Verify if the new feedback comment has re-appeared", wcmqsBlogPostPage.getFeedBackComments(), hasItem(visitorComment));

    }

    /*
     * AONE-5684 Verify correct work of Leave Comment form
     */
    @AlfrescoTest(testlink = "AONE-5684")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyLeaveCommentForm() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step action ----
        // Fill in Leave Comment fields with correct data;
        // ---- Expected results ----
        // Data is entered successfully;

        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
        WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Your comment has been sent! message is shown;

        wcmqsBlogPostPage.clickPostButton();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        // ---- Step 3 ----
        // ---- Step action ----
        // Refresh browser page and verify the presence of Leave Comment form;
        // ---- Expected results ----
        // Page is refreshed, Leave Comment form is shown again;

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        blogPage = new WcmqsBlogPage(drone);
        blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
        wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
        assertThat("Leave comment form is displayed ", wcmqsBlogPostPage.isLeaveCommentFormDisplayed());
    }

    /*
     * AONE-5685 Section tags
     */
    @AlfrescoTest(testlink = "AONE-5685")
    @Test(groups = {"WQS", "ProductBug"})
    public void verifySectionTags() throws Exception
    {

        // ---- Data prep ----
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();

        documentLibPage.selectFolder("Alfresco Quick Start");
        documentLibPage.selectFolder("Quick Start Editorial");
        documentLibPage.selectFolder("root");
        documentLibPage.selectFolder("blog");
        documentLibPage.getFileDirectoryInfo("blog1.html").addTag("testtag 1");
        documentLibPage.getFileDirectoryInfo("blog2.html").addTag("testtag 1");
        documentLibPage.getFileDirectoryInfo("blog3.html").addTag("testtag 2");

        documentLibPage.getFileDirectoryInfo("index.html");
        ShareUtil.logout(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened;

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ----
        // Open Blogs page;
        // ---- Expected results ----
        // Blogs page is opened;

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify Section Tags;
        // ---- Expected results ----
        // Tags links with number of tags is displayed:
        // testtag 1 (2)
        // testtag 2 (1)

        WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
        assertThat("Verify if tag list contains testtag 1 (2) and testtag 2 (1)", wcmqsBlogPage.getTagList(), hasItems("testtag 1 (2)", "testtag 2 (1)"));

        // ---- Step 4 ----
        // ---- Step action ----
        // Click testtag 1 link;
        // ---- Expected results ----
        // Two blog posts are dislayed;

        wcmqsBlogPage.clickTag("testtag 1 (2)");
        WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
        assertThat("Check if the number of results is correct --> See defect MNT-12860", wcmqsSearchPage.getTagSearchResults().size(), is(equalTo(2)));

        // ---- Step 5 ----
        // ---- Step action ----
        // Click testtag 2 link;
        // ---- Expected results ----
        // One blog post is displayed;

        homePage = new WcmqsHomePage(drone);
        homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
        wcmqsBlogPage.clickTag("testtag 2 (1)");
        wcmqsSearchPage = new WcmqsSearchPage(drone);
        assertThat("Check if the number of results is correct", wcmqsSearchPage.getTagSearchResults().size(), is(equalTo(1)));
    }

    /*
     * AONE-5679 Adding blog post comment with too long data
     */
    @AlfrescoTest(testlink = "AONE-5679")
    @Test(groups = {"WQS"})
    public void addLongDataInComment() throws Exception
    {

        String visitorName = "name" + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorWebsite = "website " + getTestName();
        String visitorComment = "Comment by " + visitorName;

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
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        WcmqsBlogPostPage wcmqsBlogPostPage = blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP).render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Enter too long data(more 1024 characters) in Name field;
        // ---- Expected results ----
        // Data successfully entered;

        // TODO : Test CASE Steps needs to be updated since the data you enter
        // is automatically truncated to 70 chars
        wcmqsBlogPostPage.setVisitorName(visitorName + StringUtils.leftPad("test", 1100, 'a'));
        assertThat("Check if the number of entered chars is 70", wcmqsBlogPostPage.getVisitorName().length(), is(equalTo(70)));

        // ---- Step 4 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // You should be able to post succesfully;

        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton();
        wcmqsBlogPostPage.render();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        // ---- Step 5 ----
        // ---- Step action ----
        // Open any blog post again;
        // ---- Expected results ----
        // Blog post is opened;


        // TODO : Test CASE Steps needs to be updated since the data you enter
        // is automatically truncated to 100 chars

        // ---- Step 7 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Comment is displayed, Email's field data is restricted to 101
        // symbols;


        // ---- Step 8 ----
        // ---- Step action ----
        // Open any blog post again;
        // ---- Expected results ----
        // Blog post is opened;

        homePage = new WcmqsHomePage(drone);
        blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS).render();

        assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

        // ---- Step 9 ----
        // ---- Step action ----
        // Enter too long data(more 1024 characters) in Website field;
        // ---- Expected results ----
        // Data successfully entered;

        // TODO : Test CASE Steps needs to be updated since the data you enter
        // is automatically truncated to 101 chars
        wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite + StringUtils.leftPad("test", 1100, 'a'));
        assertThat("Check if the number of entered chars is 100", wcmqsBlogPostPage.getVisitorWebsite().length(), is(equalTo(100)));

        // ---- Step 10 ----
        // ---- Step action ----
        // Fill other fields with correct data and click Post button;
        // ---- Expected results ----
        // Comment is displayed, Website's field data is restricted to 101
        // symbols;

        wcmqsBlogPostPage.setVisitorName(visitorName);
        wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
        wcmqsBlogPostPage.setVisitorComment(visitorComment);
        wcmqsBlogPostPage.clickPostButton().render();
        assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

    }


}
