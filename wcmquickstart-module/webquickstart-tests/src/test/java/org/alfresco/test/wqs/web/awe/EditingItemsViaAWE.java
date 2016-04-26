
package org.alfresco.test.wqs.web.awe;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BOLD;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BULLET;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.ITALIC;
import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.alfresco.po.wqs.WcmqsArticleDetails;
import org.alfresco.po.wqs.WcmqsEditPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openqa.selenium.Keys;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditingItemsViaAWE extends AbstractWQS
{
    private static final Log logger = LogFactory.getLog(EditingItemsViaAWE.class);
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

    /**
     * AONE-5607:Verify correct displaying of Edit blog post/article page
     */
    @AlfrescoTest(testlink="AONE-5607")
    @Test(groups = "WQS")
    public void verifyDisplayEditBlogPost() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened

        navigateTo(wqsURL);

        // ---- Step 2 ----
        // ---- Step action ---
        // Open any blog post/article
        // ---- Expected results ----
        // Blog post/article is opened

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near blog post/article(login as admin, if required)
        // ---- Expected results ----
        // Edit window is opened

        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify the presence of all fields on the form
        // ---- Expected results ----
        // The form contains of fields: Name(mandatory), Title, Description,
        // Content, Template Name. It also contains Submit and Cancel buttons
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isNameFieldMandatory(), "Name field is not mandatory");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed");
        Assert.assertTrue(wcmqsEditPage.isCancelButtonDisplayed(), "Cancel button is not displayed");
    }

    /**
     * AONE-5608:Editing blog post/article, Name(negative test with spaces)
     */
    @AlfrescoTest(testlink="AONE-5608")
    @Test(groups = "WQS")
    public void editingBlogPostArticleNameSpaces() throws Exception
    {
        navigateTo(wqsURL);
        drone.maximize();

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Fill Name field with spaces;
        // ---- Expected results ----
        // Data is entered successfully;
        String newName = "      ";
        wcmqsEditPage.editName(newName);
        wcmqsEditPage.moveFocusTo("title");
        wcmqsEditPage.moveFocusTo("name");
        Assert.assertTrue(wcmqsEditPage.getArticleDetails().getName().contains(newName));

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed;
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "The value cannot be empty.");

    }

    /**
     * AONE-5609:Editing blog post/article, Name(negative test, empty field)
     */
    @AlfrescoTest(testlink="AONE-5609")
    @Test(groups = "WQS")
    public void editingBlogPostArticleNameEmpty() throws Exception
    {
        navigateTo(wqsURL);
        drone.maximize();

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Leave Name field empty
        // ---- Expected results ----
        // Name field is empty
        wcmqsEditPage.sendKeyOnName(Keys.RETURN);

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "The value cannot be empty.");

    }

    /**
     * AONE-5610:Editing blog post/article, Name(negative test with wildcards)
     */
    @AlfrescoTest(testlink="AONE-5610")
    @Test(groups = "WQS", enabled = true)
    public void editingBlogPostArticleNameWildcards() throws Exception
    {
        navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Fill Name field with wildcards
        // ---- Expected results ----
        // Data is entered successfully
        String name2 = "a *";
        wcmqsEditPage.editName(name2);

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "Value contains illegal characters.");

    }

    /**
     * AONE-5611:Editing blog post/article, Name(positive test)
     */
    @AlfrescoTest(testlink="AONE-5611")
    @Test(groups = "WQS", enabled = true)
    public void editingBlogPostArticleNamePositive() throws Exception
    {
        navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Enter some new name in Name field;
        // ---- Expected results ----
        // Data is entered successfully
        String newName = "article3.html";
        wcmqsEditPage.editName(newName);
        Assert.assertTrue(wcmqsEditPage.getArticleDetails().getName().contains(newName));

        // ---- Step 2 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, data is changed successfully
        WcmqsNewsPage newsPage = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertNotNull(newsPage);

    }

    /**
     * AONE-5612:Editing Content field
     */
    @AlfrescoTest(testlink="AONE-5612")
    @Test(groups = {"WQS", "ProductBug"}, enabled = true)
    public void editingContentField() throws Exception
    {
        String textBold = "Bold Text";
        String textItalic = "Italic Text";
        String textUnderlined = "Underlined Text";
        String textBullet = "Bullet Text";
        String colorText = "Color Text";

        navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Leave Content field empty
        // ---- Expected results ----
        // Content field is empty

        String content = "";
        wcmqsEditPage.insertTextInContent(content);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button status
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Enter some valid data and apply Bold, Italic and Underline styles for it
        // ---- Expected results ----
        // Styles are applied successfully
        String content2 = "test";
        wcmqsEditPage.insertTextInContent(content2);

        TinyMceEditor tinyMceEditor = wcmqsEditPage.getContentTinyMCEEditor();
        tinyMceEditor.setText(textBold);
        tinyMceEditor.clickTextFormatter(BOLD);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><strong>%s</strong></p>", textBold), "The text didn't mark as bold.");

        tinyMceEditor.setText(textItalic);
        tinyMceEditor.clickTextFormatter(ITALIC);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><em>%s</em></p>", textItalic), "The text didn't italic.");

        tinyMceEditor.setText(textUnderlined);
        tinyMceEditor.clickFormat();
        tinyMceEditor.clickTextFormatter(FormatType.UNDERLINED);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"text-decoration: underline;\">%s</span></p>", textUnderlined),
                "The text didn't underlined. BUG ACE-3512");

        // ---- Step 4 ----
        // ---- Step action ---
        // Try to paste any Unodered and Odered list
        // ---- Expected results ----
        // Lists are pasted successfully

        tinyMceEditor.setText(textBullet);
        tinyMceEditor.clickTextFormatter(BULLET);
        assertEquals(tinyMceEditor.getContent(), String.format("<ul style=\"\"><li>%s</li></ul>", textBullet), "List didn't display.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Change color for some text
        // ---- Expected results ----
        // Color is changed successfully

        tinyMceEditor.setText(colorText);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"color: rgb(0, 0, 255);\">%s</span></p>", colorText),
                "Text didn't get colored.");

        // ---- Step 6 ----
        // ---- Step action ---
        // Click Undo button (In order to click Undo button, Edit button should be clicked first)
        // ---- Expected results ----
        // Last formatting action is canceled
        String editedText = "text to undo";
        tinyMceEditor.setText(editedText);
        tinyMceEditor.clickEdit();
        tinyMceEditor.clickUndo();
        Assert.assertFalse(tinyMceEditor.getContent().contains(editedText));

        // ---- Step 7 ----
        // ---- Step action ---
        // Click Redo button (In order to click Undo button, Edit button should be clicked first)
        // ---- Expected results ----
        // Last formatting action is redone
        tinyMceEditor.clickEdit();
        tinyMceEditor.clickRedo();
        Assert.assertTrue(tinyMceEditor.getContent().contains(editedText));

        // ---- Step 8 ----
        // ---- Step action ---
        // Highlight some text and click Remove formatting button (Edit menu is already expanded, Format menu should be clicked twice)
        // ---- Expected results ----
        // Formatting is removed
        tinyMceEditor.selectTextFromEditor();
        tinyMceEditor.clickFormat();
        tinyMceEditor.clickFormat();
        tinyMceEditor.removeFormatting();
        Assert.assertNotEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"color: rgb(0, 0, 255);\">%s</span></p>", colorText),
                "Text didn't get colored.");

        // ---- Step 9 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are saved
        WcmqsNewsPage newsPage = wcmqsEditPage.clickSubmitButton().render();
        Assert.assertNotNull(newsPage);

    }

    /**
     * AONE-5613:Editing Content field(negative test)
     */
    @AlfrescoTest(testlink="AONE-5613")
    @Test(groups = { "WQS" })
    public void editingContentFieldNegative() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Content field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newContent = "content " + getTestName();
        editPage.insertTextInContent(newContent);
        Assert.assertTrue(editPage.getContentTinyMCEEditor().getText().contains(newContent));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getBodyOfNewsArticle().contains(newContent));
    }

    /**
     * AONE-5614:Editing Title field(negative test)
     */
    @AlfrescoTest(testlink="AONE-5614")
    @Test(groups = { "WQS" })
    public void editingTitleFieldNegative() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newTitle = "title " + getTestName();
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getTitleOfNewsArticle().contains(newTitle));
    }

    /**
     * AONE-5615:Editing Title field(positive test)
     */
    @AlfrescoTest(testlink="AONE-5615")
    @Test(groups = { "WQS" })
    public void editingTitleFieldPositive() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newTitle = "title " + getTestName();
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Sumbit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are saved;
        WcmqsNewsPage newsPage = editPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage.getNewsTitle(shareName).contains(newTitle), "New title is " + newsPage.getNewsTitle(shareName) + ". Expected: " + newTitle);

    }

    /**
     * AONE-5616:Description field(cancel editing)
     */
    @AlfrescoTest(testlink="AONE-5616")
    @Test(groups = { "WQS" })
    public void cancelEditingDescriptionField() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);
        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description " + getTestName();
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data isn't changed;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        // the user is returned in the article page. To check if the changed are not present, edit again the blog post/article
        editPage = article.clickEditButton().render();
        Assert.assertFalse(editPage.getArticleDetails().getDescription().contains(newDescription));

    }

    /**
     * AONE-5617:Description field(wildcards)
     */
    @AlfrescoTest(testlink="AONE-5617")
    @Test(groups = { "WQS" })
    public void descriptionFieldsWildcards() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description~!@#$%^&*(a)";
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data is changed;
        WcmqsNewsPage newsPage = editPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage.getNewsDescription(shareName).contains(newDescription));

    }

    /**
     * AONE-5618:Description field(submit editing)
     */
    @AlfrescoTest(testlink="AONE-5618")
    @Test(groups = { "WQS" })
    public void descriptionFieldSubmit() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description " + getTestName();
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data is changed;
        WcmqsNewsPage newsPage = editPage.clickSubmitButton().render();
        Assert.assertTrue(newsPage.getNewsDescription(shareName).contains(newDescription));

    }
}
