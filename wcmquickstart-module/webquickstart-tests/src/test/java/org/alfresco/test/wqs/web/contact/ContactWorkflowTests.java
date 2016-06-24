
package org.alfresco.test.wqs.web.contact;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.test.wqs.web.blog.BlogComponent;
import org.alfresco.test.wqs.web.search.SearchTests;
import org.alfresco.po.wqs.FactoryWqsPage;
import org.alfresco.po.wqs.WcmqsContactPage;
import org.alfresco.po.wqs.WcmqsHomePage;
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
 * Created by Cristina Axinte on 1/7/2015.
 */

@Listeners(FailedTestListener.class)
public class ContactWorkflowTests extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(ContactWorkflowTests.class);

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
        loginInfo = new String[] { ADMIN_USERNAME, ADMIN_PASSWORD };
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // User login
        // ---- Step 1 ----
        // ---- Step Action -----
        // WCM Quick Start is installed; is not required to be executed automatically
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
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo(QUICK_START_EDITORIAL).selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname("localhost");
        documentPropertiesPage.clickSave();
        documentLibPage.render();
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
     * AONE-5717 Verify the presence of Contact request on My Tasks dashboard
     */
    @AlfrescoTest(testlink="AONE-5717")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyContactRequestOnMyTasks() throws Exception
    {
        String visitorName = "name " + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Fill all mandatory fields with valid data;
        // ---- Expected results ----
        // Data is entered successfully;

        navigateTo(wqsURL);
        WcmqsHomePage homePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsContactPage contactPage = homePage.clickContactLink().render();
        contactPage.render();
        contactPage.setVisitorName(visitorName);
        contactPage.setVisitorEmail(visitorEmail);
        contactPage.setVisitorComment(visitorComment);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Your message has been sent! message is shown;

        contactPage.clickPostButton().render();
        Assert.assertTrue(contactPage.isAddCommentMessageDisplay(), "Comment was not posted.");
        String expectedMessage = "Your message has been sent!";
        Assert.assertTrue(contactPage.getAddCommentSuccessfulMessage().contains(expectedMessage), "Message: " + expectedMessage + " is not present.");
        waitForDocumentsToIndex();
        // ---- Step 3 ----
        // ---- Step action ----
        // Log in Alfresco Share as admin;
        // ---- Expected results ----
        // Admin is logged in Alfresco Share;

        loginActions.loginToShare(drone, loginInfo, shareUrl);

        // ---- Step 4 ----
        // ---- Step action ----
        // Verify the presence of Contact request on My Tasks dashboard;
        // ---- Expected results ----
        // Contact request from %Name% task is present on MyTasks dashboard;

        String taskName = "Contact request from " + visitorName;
        MyTasksPage myTasksPage = siteActions.getSharePage(drone).getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(taskName), "Task: " + taskName + " is not present.");

        ShareUtil.logout(drone);
    }

    /*
     * AONE-5718 Verify the available actions for Contact request(v 3.4)
     */
    @AlfrescoTest(testlink="AONE-5718")
    @Test(groups = {"WQS", "EnterpriseOnly"})
    public void verifyActionsForContactRequest() throws Exception
    {
        String visitorName = "name " + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 4 ----
        // ---- Step action ---
        // Contact request with valid data is sent;
        navigateTo(wqsURL);
        WcmqsHomePage homePage = FactoryWqsPage.resolveWqsPage(drone).render();

        WcmqsContactPage contactPage = homePage.clickContactLink().render();
        contactPage.setVisitorName(visitorName);
        contactPage.setVisitorEmail(visitorEmail);
        contactPage.setVisitorComment(visitorComment);
        contactPage.clickPostButton().render();
        Assert.assertTrue(contactPage.isAddCommentMessageDisplay(), "Comment was not posted.");
        waitForDocumentsToIndex();


        // ---- Step 1 ----
        // ---- Step action ----
        // Login as admin in Alfresco Share;
        // ---- Expected results ----
        // Admin is logged in Alfresco Share;

        loginActions.loginToShare(drone, loginInfo, shareUrl);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify actions for Contact request on MyTasks dashboard;
        // ---- Expected results ----
        // The are Edit task and View task buttons, Task link, Priority icon;

        String taskName = "Contact request from " + visitorName;

        MyTasksPage myTasksPage = siteActions.getSharePage(drone).getNav().selectMyTasks().render();
        waitForCommentPresent(myTasksPage, taskName);
        Assert.assertTrue(myTasksPage.isTaskPresent(taskName), "Task: " + taskName + " is not present.");
        Assert.assertTrue(myTasksPage.isTaskEditButtonEnabled(taskName), "Edit task button is not present for task: " + taskName);
        Assert.assertTrue(myTasksPage.isTaskViewButtonEnabled(taskName), "View task button is not present for task: " + taskName);
        Assert.assertTrue(myTasksPage.isTaskPriorityIconEnabled(taskName), "Priority icon button is not present for task: " + taskName);

        ShareUtil.logout(drone);
    }

}
