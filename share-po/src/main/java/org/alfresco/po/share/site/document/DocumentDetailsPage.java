/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.preview.PdfJsPlugin;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.util.FileDownloader;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Site document library page object, holds all element of the HTML page
 * relating to share's site document library page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@SuppressWarnings("unused")
public class DocumentDetailsPage extends DetailsPage
{
    private static final Log logger = LogFactory.getLog(DocumentDetailsPage.class);
    private static final String UPLOADED_DOCUMENT_NEW_VERSION_PLACEHOLDER = "div[class$='document-upload-new-version']";
    private static final String REVISON_HISTORY_PANEL = "document.detail.version.history.panel";
    private static final String DOWNLOAD_FILE_CSS_LOCATOR = "div.node-header>div.node-action>span>span>a";
    private static final String EDIT_OFFLINE_LINK = "div.document-edit-offline>a";
    private static final String EDIT_ONLINE_LINK = "div.document-edit-online>a";
    private static final String INLINE_EDIT_LINK = ".document-inline-edit>a";
    private static final String DOCUMENT_PROPERTIES_DISPLAYED_SIZE = "span[id$='-formContainer_prop_size']";
    private static final String DOCUMENT_PREVIEWER = "div > div.web-preview > div.previewer";
    private static final String DOCUMENT_PREVIEW_WITH_FLASH_PLAYER = "div.web-preview.real";
    private static final String DOCUMENT_PREVIEW_WITHOUT_FLASH_PLAYER = "div[id$='default-previewer-div']>img, div[id$='default-previewer-div'] canvas";
    private static final String NO_DOCUMENT_PREVIEW = "div.message";
    private static final String FILE_ISNT_VISIBLE = "Unfortunately the file can't be viewed in your web browser.";
    private static final String DOCUMENT_CANT_BE_PREVIEWED = "This document can't be previewed.";
    private static final String CLICK_HERE_TO_DOWNLOAD_LINK = "Click here to download it.";
    private static final String THIN_DARK_TITLE_ELEMENT = "div.node-header>div.node-info>h1.thin.dark";
    private static final String SYNC_TO_CLOUD = "a[title='Sync to Cloud'].action-link>span";

    private static final String DELETE_DOCUMENT = "a[title='Delete Document'].action-link>span";

    private static final String UNSYNC_FROM_CLOUD = "a[title='Unsync from Cloud'].action-link>span";
    protected static final String CHECKEDOUT_MESSAGE_PLACEHOLDER = "div.node-header>div.status-banner.theme-bg-color-2.theme-border-4";
    protected static final String ACTION_SET_ID = "document.detail.action.set.id";
    public static final String DOCUMENT_VERSION_PLACEHOLDER = "div.node-header>div.node-info>h1.thin.dark>span.document-version";
    private static final String LINK_EDIT_IN_GOOGLE_DOCS = "div[id$='default-actionSet'] div.google-docs-edit-action-link a";
    private static final String LINK_CHECKIN_GOOGLE_DOCS = "div[id$='default-actionSet'] div.google-docs-checkin-action-link a";
    private static final String LINK_VIEW_ON_GOOGLE_MAPS = "div[id$='default-actionSet'] div.document-view-googlemaps a";
    private static final String LINK_RESUME_EDIT_IN_GOOGLE_DOCS = "div[id$='default-actionSet'] div.google-docs-resume-action-link a";
    private static final String REQUEST_SYNC_ICON = "a.document-requestsync-link[title='Request Sync']";
    private static final String LOCATION_IN_CLOUD = "p.location";
    private static final String SYNC_STATUS = ".cloud-sync-details-info>p:not(.location)";
    private static final String COMMENT_COUNT = "span.comment-count";
    protected String previousVersion;
    private String expectedVersion;
    protected String documentVersion;
    private boolean isGoogleCreate = false;
    private static final String GOOGLE_DOCS_URL = "googledocsEditor?";
    private static final By WORKFLOW_INFO = By.cssSelector("div.document-workflows>div>div.info");
    private static final String COPY_THIS_LINK_TO_SHARE_THE_CURRENT_PAGE = "div.link-info input";
    private static final By QUICK_SHARE_LINK = By.cssSelector("a.quickshare-action");
    private static final By QUICK_SHARE_LINK_ENABLED = By.cssSelector("a[class='quickshare-action enabled']");
    private static final String DOWNLOAD_ACTION_LINK = ".document-download>a";
    private static final String VIEW_IN_BROWSER = ".document-view-content>a";
    private static final By VIEW_WORKING_COPY = By.cssSelector("div.document-view-working-copy>a");
    private static final By VERSION_HISTORY_PANEL = By.cssSelector("div[class*='document-versions']");
    private static final By WORKFLOWS_PANEL = By.cssSelector("div[class*='document-workflows']");
    private static final String START_WORKFLOW_ICON = ".template_x002e_document-workflows_x002e_document-details_x0023_default.edit";
    private static final String UPLOAD_NEW_VERSION_ICON = "span[class*='twister-actions']>a[class*='document-versions']";
    private static final String PUBLISH_CONFIRM = "//div[@class='publishConfirm']/div[@class='success' and contains(text(), '%s is queued for publishing to')]/span/span[contains(text(), '%s')]";
    private static final By SELECT_CHANNEL_BUTTON = By.cssSelector("#alfresco-socialPublishing-instance-channel-select-button-button");
    protected static final By PROMPT_PANEL_ID = By.id("prompt");
    private static final By BUTTON_TAG_NAME = By.tagName("button");
    private static final By HISTORY_VERSIONS = By.cssSelector("div[class*='document-versions'] span[class='document-version']");
    private static final By SYNC_MESSAGE = By.xpath(".//span[contains(text(),'Sync was created')]");

    private static final By DOCUMENT_BODY = By.cssSelector("div[id$='document-details_x0023_default-viewer']");

    private static final By VIEW_ORIGINAL_DOCUMENT = By.cssSelector("div.document-view-original>a");
    public static final String UNZIP_TO = "//span[text()='Unzip to...']";
    private static final String ERROR_EDITING_DOCUMENT = ".//*[@id='message']/div/span";
    private static final String LINK_CANCEL_GOOGLE_DOCS = "#onGoogledocsActionCancel a";
    private static final String PERMISSION_SETTINsGS_PANEL_CSS = ".document-permissions";

    public void setPreviousVersion(final String previousVersion)
    {
        this.previousVersion = previousVersion;
    }

    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     * 
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public DocumentDetailsPage render(RenderTime timer)
    {
        String docVersionOnScreen;
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                // If popup is not displayed start render check
                if (!driver.findElement(By.cssSelector("div.bd")).isDisplayed())
                {
                    // upload dialog should not be displayed.
                    if (!driver.findElement(By.cssSelector("div.yui-dt-bd")).isDisplayed())
                    {
                        //If edit offline mode then version will not be displayed
                        if(isEditOfflineDisplayed())
                        {
                            docVersionOnScreen = "";
                            break;
                        }
                        docVersionOnScreen = findAndWait(By.cssSelector(DOCUMENT_VERSION_PLACEHOLDER)).getText().trim();
                        // If the text is not what we expect it to be, then repeat
                        if (this.previousVersion != null && docVersionOnScreen.equals(this.previousVersion))
                        {
                            // We are still seeing the old version number
                            // Go around again
                            continue;
                        }
                        // If we see expected version number, stop and serve.
                        if (expectedVersion != null && !expectedVersion.isEmpty())
                        {
                            if (docVersionOnScreen.equals(this.expectedVersion))
                            {
                                break;
                            }
                        }
                        // Populate the doc version
                        break;
                    }

                }
            }
            catch (TimeoutException te)
            {
                throw new PageException("Document version not rendered in time", te);
            }
            catch (NoSuchElementException te)
            {
                // Expected if the page has not rendered
            }
            catch (StaleElementReferenceException e)
            {
                // This occurs occasionally, as well
            }
            finally
            {
                timer.end();
            }
        }
        this.documentVersion = docVersionOnScreen;
        return this;
    }

    /**
     * Gets document version value from top of the details page
     * 
     * @return String value of document version
     */
    public synchronized String getDocumentVersion()
    {
        // Render must have populated the document version
        return this.documentVersion;
    }

    public boolean isDeleteDocumentLinkDisplayed()
    {
        try
        {
            WebElement el = driver.findElement(By.cssSelector(DELETE_DOCUMENT));
            boolean isDeleteDocument = driver.findElement(By.cssSelector(DELETE_DOCUMENT)).isDisplayed();
            return isDeleteDocument;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if button on version history view is displayed
     * 
     * @return true if visible on the page
     */
    public boolean isUploadNewVersionDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector(UPLOADED_DOCUMENT_NEW_VERSION_PLACEHOLDER)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the action to edit offline is available
     * 
     * @return true if visible on the page
     */
    public boolean isEditOfflineLinkDisplayed()
    {
        try
        {
            boolean isEditOff = driver.findElement(By.cssSelector(EDIT_OFFLINE_LINK)).isDisplayed();
            return isEditOff;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Check for edit offline banner that appears on the top
     * of the page when document is locked by edit off line.
     * 
     * @return returns <tt>false</tt> always
     */
    public boolean isEditOfflineDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector("span.editing")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Check for locked by you banner that appears on the top
     * of the page when document is locked by edit off line and locked by you when viewing original.
     * 
     * @return returns <tt>false</tt> always
     */
    public boolean isLockedByYou()
    {
        try
        {
            return driver.findElement(By.cssSelector("span.lock-owner")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if the page viewed is the document details page.
     */
    public boolean isDocumentDetailsPage()
    {
        return isDetailsPage("document");
    }

    /**
     * Mimics the action of deleting a document detail.
     */
    public DocumentLibraryPage delete()
    {
        By by = By.cssSelector("div.document-delete>a");
        WebElement button = findAndWait(by);
        button.click();
        confirmDeleteAction();
        waitUntilElementDeletedFromDom(by, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return factoryPage.getPage(driver).render();
    }

    /**
     * Gets the document detail title.
     * 
     * @return String document detail page title
     */
    public String getDocumentTitle()
    {
        WebElement element = findAndWait(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
        WebElement header = element.findElement(By.tagName("span"));
        return element.getText().replace(header.getText(), "");
    }

    /**
     * Mimics the action of clicking on the upload new version button.
     * 
     * @return HtmlPage page response object
     */
    public HtmlPage selectUploadNewVersion()
    {
        WebElement link = findAndWait(By.cssSelector("div.document-upload-new-version>a"));
        String version = getDocumentVersion();
        link.click();

        return getFileUpdatePage(driver, version, isEditOfflineLinkDisplayed());
    }

    /**
     * File upload page pop up object.
     * 
     * @param driver {@link WebDriver} browser client
     * @param version the number of the original document
     * @param editOffLine mode status
     * @return {@link UpdateFilePage} page object response
     */
    public HtmlPage getFileUpdatePage(WebDriver driver, final String version, final boolean editOffLine)
    {
        UpdateFilePage page = factoryPage.instantiatePage(driver, UpdateFilePage.class);
        page.setDocumentVersion(version);
        page.setEditOffline(editOffLine);
        return page;
    }

    /**
     * Locates the revision history DIV.
     * 
     * @return {@link WebElement} represent revision history
     */
    public WebElement getRevisionPanel()
    {
        return findByKey(REVISON_HISTORY_PANEL);
    }

    /**
     * Get the comments of the last commit from the revision history panel
     * 
     * @return String comments
     */
    public String getCommentsOfLastCommit()
    {
        /**
         * Use an element as an anchor as it has a unique id and dive into its
         * child elements to obtain the comments as it has not been given an id
         * or CSS that is unique.
         **/
        WebElement commentField = findAndWait(By.cssSelector("div[id$='default-latestVersion'] div.version-details-right"));
        String comments = commentField.getText();
        if (!comments.isEmpty())
        {
            int index = comments.indexOf('\n');
            comments = comments.substring(index + 1);
        }
        return comments;
    }

    /**
     * Verifies if document is locked for off line editing.
     * 
     * @return true if locked for off line editing
     */
    public synchronized boolean isCheckedOut()
    {
        try
        {
            return driver.findElement(By.cssSelector(CHECKEDOUT_MESSAGE_PLACEHOLDER)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Get status banner message (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     * 
     * @return String status banner text message
     */
    public synchronized String getContentInfo()
    {
        try
        {
            return findAndWait(By.cssSelector(CHECKEDOUT_MESSAGE_PLACEHOLDER + ">span")).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Info banner.", e);
        }
        return "";
    }

    /**
     * Select the edit off line link.
     * 
     * @return {@link HtmlPage} edit off line page.
     */
    public HtmlPage selectEditOffLine(File file)
    {
        try
        {
            WebElement link = driver.findElement(By.cssSelector(EDIT_OFFLINE_LINK));
            link.click();
            if (file != null)
            {
                String path = downloadFile(DOWNLOAD_FILE_CSS_LOCATOR, false);
                FileDownloader downloader = new FileDownloader(driver);
                try
                {
                    downloader.download(path, file);
                }
                catch (Exception e)
                {
                    throw new PageException("Edit offline file download error", e);
                }
            }
            return factoryPage.instantiatePage(driver,DocumentEditOfflinePage.class);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to edit offline", nse);
        }
    }

    /**
     * Select the edit off line link.
     * 
     * @return {@link HtmlPage} edit off line page.
     */
    public HtmlPage selectEditOffLine()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(EDIT_OFFLINE_LINK));
            link.click();
            return factoryPage.instantiatePage(driver,DocumentEditOfflinePage.class);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to edit offline", nse);
        }
    }

    /**
     * Mimics the action of selecting In Line Edit.
     * 
     * @return {@link InlineEditPage}
     */
    public EditTextDocumentPage selectInlineEdit()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(INLINE_EDIT_LINK));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
            throw new PageOperationException("Unable to select Inline Edit", exception);
        }

        return factoryPage.instantiatePage(driver, EditTextDocumentPage.class);

    }

    /**
     * Finds and clicks on the file download link.
     * 
     * @param selector css selector descriptor
     * @return filePath String file url
     */
    private String downloadFile(final String selector, boolean click)
    {
        String filePath = "";
        try
        {
            WebElement link = driver.findElement(By.cssSelector(selector));
            if (click)
            {
                link.click();
            }
            String fileUrl = link.getAttribute("href");
            if (fileUrl != null && !fileUrl.isEmpty())
            {
                filePath = fileUrl.replace("?a=true", "");
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return filePath;
    }

    /**
     * Downloads the document shown by the current page, optionally
     * doing it by clicking the link in the browser (no control) or
     * by doing a URL-based download.
     * 
     * @param file optional file to download to. When given
     *            the link <b>will not be clicked</b> but the
     *            file will be downloaded directly from the server.
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectDownload(File file)
    {
        boolean click = (file == null);
        String path = downloadFile(DOWNLOAD_FILE_CSS_LOCATOR, click);

        // A file was provided so stream into it
        if (file != null)
        {
            FileDownloader downloader = new FileDownloader(driver);
            try
            {
                downloader.download(path, file);
            }
            catch (Exception e)
            {
                throw new PageException("Unable to download file", e);
            }
        }
        return getCurrentPage();
    }

    /**
     * Get the file size from the properties
     * section of the document details page.
     * 
     * @return String size
     */
    public String getDocumentSize()
    {
        WebElement fileSize = driver.findElement(By.cssSelector(DOCUMENT_PROPERTIES_DISPLAYED_SIZE));
        return fileSize.getText();
    }

    /**
     * This test case needs flash player installed on linux box and till that it will be disabled.
     * Gets the Preview status on the document page.
     * 
     * @return boolean
     */
    public boolean isFlashPreviewDisplayed()
    {
        try
        {
            return findAndWait(By.cssSelector(DOCUMENT_PREVIEW_WITH_FLASH_PLAYER)).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            try
            {
                return driver.findElement(By.cssSelector(DOCUMENT_PREVIEW_WITHOUT_FLASH_PLAYER)).isDisplayed();
            }
            catch (TimeoutException e)
            {
            }
        }

        return false;
    }

    /**
     * Get the name of the WebPreview plugin which is in use
     * 
     * @return String The name the plugin used
     */
    public String getPreviewerClassName()
    {
        try
        {
            return driver.findElement(By.cssSelector(DOCUMENT_PREVIEWER)).getAttribute("class");
        }
        catch (TimeoutException nse)
        {
            try
            {
                return driver.findElement(By.cssSelector(DOCUMENT_PREVIEWER)).getAttribute("class");
            }
            catch (TimeoutException e)
            {
            }
        }

        return null;
    }

    /**
     * Gets the No Preview Message on the document page.
     * 
     * @return boolean
     */
    public boolean isNoPreviewMessageDisplayed()
    {
        try
        {
            WebElement noPreviewElement = findAndWait(By.cssSelector(NO_DOCUMENT_PREVIEW));

            if (noPreviewElement != null)
            {
                String message = noPreviewElement.getText();

                if (message != null && (message.contains(DOCUMENT_CANT_BE_PREVIEWED) || message.contains(FILE_ISNT_VISIBLE)))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Preview Message is not displayed", nse);
            }
        }
        catch (TimeoutException tme)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Preview Message is not displayed", tme);
            }
        }
        return false;
    }
    PdfJsPlugin pdfJsPlugin;
    /**
     * Return the PdfJs web preview plugin. If this plugin is not in use then a {@link PageRenderTimeException} exception will be thrown.
     * 
     * @return {@link PdfJsPlugin}
     */
    public PdfJsPlugin getPdfJsPreview()
    {
        return pdfJsPlugin.render();
    }

    /**
     * Clicks on download link when no preview message is displayed for
     * unsupported details.
     * 
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage clickOnDownloadLinkForUnsupportedDocument()
    {

        try
        {
            WebElement noPreviewElement = findAndWait(By.cssSelector(NO_DOCUMENT_PREVIEW));
            noPreviewElement.findElement(By.linkText(CLICK_HERE_TO_DOWNLOAD_LINK)).click();
        }
        catch (Exception exception)
        {
            logger.error("Not able to find the web element: details can't be Previewed");
            throw new PageException("Unable to find option for Download Link", exception);
        }

        return getCurrentPage();
    }


    /**
     * Gets the No Preview Message on the document page.
     * 
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        try
        {
            return findAndWait(By.cssSelector("form.cloud-auth-form"), getDefaultWaitTime()).isDisplayed();
        }
        catch (TimeoutException te)
        {
            return false;
        }

    }

    /**
     * Checks whether the cloud destination folder to sync page is displayed.
     * 
     * @return boolean
     */
    public boolean isDestAndAssigneeVisible()
    {
        try
        {
            return findAndWait(By.cssSelector("div[id$='_default-cloud-folder-dialog']")).isDisplayed();
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Mimics the action of selecting the UnSync from Cloud icon on the document
     * page.
     * 
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage selectUnSyncFromCloud()
    {
        try
        {
            findAndWait(By.cssSelector(UNSYNC_FROM_CLOUD)).click();
            waitForElement(By.cssSelector("div#prompt_h"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            findAndWait(By.cssSelector("input#requestDeleteRemote")).click();
            findAndWait(By.cssSelector("span.button-group>span[class$='yui-push-button']")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find element", nse);
        }
        return getCurrentPage();
    }

    /**
     * Method to check if the file synced to the Cloud
     * 
     * @return true if "UnSync From Cloud" icon is displayed
     */
    public boolean isFileSyncSetUp()
    {
        try
        {
            findAndWait(By.cssSelector(UNSYNC_FROM_CLOUD)).isDisplayed();
            return true;
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Mimics the action of clicking on the assign workflow link.
     * 
     * @return StartWorkFlowPage page response object
     */
    public HtmlPage selectStartWorkFlowPage()
    {
        try
        {
            findAndWait(By.cssSelector("div.document-assign-workflow>a")).click();
            return getCurrentPage();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /**
     * Mimics the action of clicking on the Start workflow icon in WorkFlow section.
     * 
     * @return {@link StartWorkFlowPage} page response object
     */
    public HtmlPage selectStartWorkFlowIcon()
    {
        try
        {
            findAndWait(By.cssSelector("a[name='.onAssignWorkflowClick']")).click();
            return getCurrentPage();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /**
     * Checks weather the "Sync to Cloud" option is displayed
     * 
     * @return boolean
     */
    public boolean isSyncToCloudOptionDisplayed()
    {
        try
        {
            driver.findElement(By.cssSelector(SYNC_TO_CLOUD)).isDisplayed();
            return true;
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }


    /**
     * Verify if Link Edit in Google docs is visible.
     * 
     * @return true if displayed
     * <br/><br/>author sprasanna
     */
    public boolean isEditInGoogleDocsLinkVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector(LINK_EDIT_IN_GOOGLE_DOCS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if Link Resume Editing in Google docs is visible.
     * 
     * @return true if displayed
     * <br/><br/>author sprasanna
     */
    public boolean isResumeEditingInGoogleDocsLinkVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector(LINK_RESUME_EDIT_IN_GOOGLE_DOCS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the action to edit offline is available
     * 
     * @return true if visible on the page
     */
    public boolean isInlineEditLinkDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector(INLINE_EDIT_LINK)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the comment count is displayed or not
     * 
     * @param waitTime long
     * @return boolean
     */
    public boolean isCommentCountPresent(long waitTime)
    {
        try
        {
            return findAndWaitWithRefresh(By.cssSelector(COMMENT_COUNT), waitTime).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Comment count is not displayed", e);
            }
        }
        return false;
    }

    /**
     * Checks if hide record link is displayed.
     * This will only be visible under the following
     * condition:
     * <ul>
     * <li>Record management module enabled</li>
     * <li>When the document has been declared as record</li>
     * </ul>
     * 
     * @return true if link is displayed
     */
    public boolean isHideRecordLinkDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector("div#onHideRecordAction.rm-hide-record")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to get Sync Status
     * 
     * @return String
     */
    public String getSyncStatus()
    {
        try
        {
            WebElement location = findAndWait(By.cssSelector(SYNC_STATUS));
            return location.getText();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Sync Status element is not visible", te);
            }
        }
        return "";
    }

    /**
     * Method to get location in cloud
     * 
     * @return String
     */
    public String getLocationInCloud()
    {
        try
        {
            WebElement location = findAndWait(By.cssSelector(LOCATION_IN_CLOUD));
            return location.getText().split("\n")[0];
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Cloud location element is not visible", te);
            }
        }
        return "";
    }

    /**
     * Method to check if "Request To Sync" icon is displayed in DocumentDetails page
     * 
     * @return boolean
     */
    public boolean isRequestSyncIconDisplayed()
    {
        return isElementDisplayed(By.cssSelector(REQUEST_SYNC_ICON));
    }

    /**
     * Method to revert the document to the specified version.
     * 
     * @param versionNumber revision number to revert
     * @return {@link HtmlPage} page response.
     */
    public HtmlPage selectRevertToVersion(String versionNumber)
    {
        try
        {
            Double.parseDouble(versionNumber);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Version number passed is not a number : " + versionNumber, e);
        }
        try
        {
            driver.findElement(By.cssSelector("a[rel='" + versionNumber + "']")).click();
            RevertToVersionPage r = factoryPage.instantiatePage(driver, RevertToVersionPage.class);
            r.setDocumentVersion(versionNumber);
            r.setEditOffline(false);
            return r;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Revert to version button for " + versionNumber + " is not displayed", nse);
            }
        }
        throw new PageException("Revert to version button for " + versionNumber + " is not displayed");
    }

    /**
     * Method to verify if a document is part of workflow or not
     * 
     * @return True if it is part of a Workflow
     */
    public boolean isPartOfWorkflow()
    {
        try
        {
            String workFlowInfo = driver.findElement(WORKFLOW_INFO).getText();
            if (workFlowInfo.equals("This document is part of the following workflow(s):")
                    || workFlowInfo.equals("This document is part of the following task(s):"))
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Opens the "Copy this link to share the current page" in the new tab
     * 
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage openCopyThisLinkInNewTab()
    {

        try
        {
            WebElement copyThisLink = findAndWait(By.cssSelector(COPY_THIS_LINK_TO_SHARE_THE_CURRENT_PAGE));
            createNewTab();
            driver.navigate().to(copyThisLink.getAttribute("value"));
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element: Copy This Link To Share The Current Page ");
            throw new PageException("Unable to find  Copy This Link To Share The Current Page ", exception);
        }

        return getCurrentPage();
    }

    public String getExpectedVersion()
    {
        return expectedVersion;
    }

    public void setExpectedVersion(String expectedVersion)
    {
        this.expectedVersion = expectedVersion;
    }

    /**
     * Selects the Change Type link from Document actions.
     * 
     * @return {@link ChangeTypePage} response
     */
    public ChangeTypePage selectChangeType()
    {
        By changeTypeLink = By.cssSelector("div#onActionChangeType a");
        findAndWait(changeTypeLink).click();
        return factoryPage.instantiatePage(driver, ChangeTypePage.class);
    }

    /**
     * Method to to verify if the download button for a version is present
     * 
     * @param versionNumber revision number
     */
    public boolean isDownloadPreviousVersion(String versionNumber)
    {
        WebElement downloadButton = findAndWait(By.xpath("//span[contains(text(),'" + versionNumber
                + "')]//..//..//span[@class='actions']//a[@title='Download']"));
        return downloadButton.isDisplayed();
    }

    /**
     * Method to download the document to the specified version.
     * 
     * @param versionNumber revision number
     */
    public void selectDownloadPreviousVersion(String versionNumber)
    {
        WebElement downloadButton = findAndWait(By.cssSelector("a[rel='" + versionNumber + "'] + a.download"));
        downloadButton.click();
        // Assumes driver capability settings to save file in a specific location when
        // //span[contains(text(),'1.1')]//..//..//span[@class='actions']//a[@title='Download']
    }

    /**
     * Click on quick share link.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickShareLink()
    {
        /*
         * if (isFolder())
         * {
         * throw new UnsupportedOperationException("Share Link is not Supported for the Folder");
         * }
         */
        try
        {
            findAndWait(QUICK_SHARE_LINK).click();
            return factoryPage.instantiatePage(driver, ShareLinkPage.class);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the share link element", ex);
        }

        throw new PageException("Unable to find the Share Link.");
    }

    /**
     * Check if the file is shared.
     * 
     * @return boolean
     */
    public boolean isFileShared()
    {
        try
        {
            return findAndWait(QUICK_SHARE_LINK_ENABLED).isDisplayed();
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /**
     * Method to return Edit icons on Details page
     * 
     * @return List <WebElement>
     */

    public List<WebElement> getEditControls()
    {
        try
        {
            List<WebElement> elements = findAndWaitForElements(By.cssSelector("a.edit"));
            return elements;
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Verify if Link View on Google Maps is visible.
     * 
     * <br/><br/>author rmanyam
     * @return true if displayed
     */
    public boolean isViewOnGoogleMapsLinkVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector(LINK_VIEW_ON_GOOGLE_MAPS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check whether download button is present
     * 
     * @return true if displayed
     */
    public boolean isDownloadButtonVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector(DOWNLOAD_FILE_CSS_LOCATOR)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Mimics the action of selecting Online Edit.
     * 
     * @return {@link InlineEditPage}
     */
    public EditTextDocumentPage selectOnlineEdit()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(EDIT_ONLINE_LINK));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
            throw new PageOperationException("Unable to select Online Edit", exception);
        }
        return factoryPage.instantiatePage(driver, EditTextDocumentPage.class);
    }

    /**
     * Downloads the document shown by the current page from Actions panel, optionally
     * doing it by clicking the link in the browser (no control) or
     * by doing a URL-based download.
     * 
     * @param file optional file to download to. When given
     *            the link <b>will not be clicked</b> but the
     *            file will be downloaded directly from the server.
     * @return {@link HtmlPage} page response
     */

    public HtmlPage selectDownloadFromActions(File file)
    {
        boolean click = (file == null);
        String path = downloadFile(DOWNLOAD_ACTION_LINK, click);

        // A file was provided so stream into it
        if (file != null)
        {
            FileDownloader downloader = new FileDownloader(driver);
            try
            {
                downloader.download(path, file);
            }
            catch (Exception e)
            {
                throw new PageException("Unable to download file", e);
            }
        }
        return getCurrentPage();
    }

    /**
     * Verify if the action to View Working Copy is available
     * 
     * @return true if visible on the page
     */
    public boolean isViewWorkingCopyDisplayed()
    {
        try
        {
            return findAndWait(VIEW_WORKING_COPY).isDisplayed();
        }
        catch (NoSuchElementException | TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Get Document Action List
     * 
     * @return List<String>
     */
    public List<String> getDocumentActionList()
    {
        List<String> actionNames = new ArrayList<String>();
        String text = null;

        List<WebElement> actions = findAndWaitForElements(By.xpath("//div[contains(@id, 'default-actionSet')]/div/a/span"));

        for (WebElement action : actions)
        {
            text = action.getText();

            if (text != null)
            {
                actionNames.add(text);
            }
        }
        return actionNames;
    }

    /**
     * Verify the Version histiry panel is present in the page.
     * 
     * @return boolean
     */
    public boolean isVersionHistoryPanelPresent()
    {
        try
        {
            return driver.findElement(VERSION_HISTORY_PANEL).isDisplayed();
        }
        catch (NoSuchElementException exce)
        {
        }
        return false;

    }

    public boolean isVersionPresentInVersionHistoryPanel(String version)
    {
        try
        {
            List<WebElement> elems = findAndWaitForElements(HISTORY_VERSIONS);
            for (WebElement e : elems)
            {
                if (e.getText().equals(version))
                    return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element:" + HISTORY_VERSIONS, nse);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + HISTORY_VERSIONS, toe);
        }

        return false;
    }

    public boolean isStartWorkflowIconDisplayed()
    {
        return isEditIconPresent(WORKFLOWS_PANEL);
    }

    public boolean isUploadNewVersionIconDisplayed()
    {
        return isEditIconPresent(VERSION_HISTORY_PANEL);
    }

    /**
     * Click on Start Workflow icon in Workflow panel at Details Page
     * 
     * @return StartWorkFlowPage
     */
    public HtmlPage clickStartWorkflowIcon()
    {
        WebElement icon = findAndWait(By.cssSelector(START_WORKFLOW_ICON));
        icon.click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Mimics the action of clicking on the upload new version icon in the .
     * 
     * @return HtmlPage page response object
     */
    public HtmlPage selectUploadNewVersionIcon()
    {
        WebElement link = findAndWait(By.cssSelector("span[class*='twister-actions']>a[class*='document-versions']"));
        String version = getDocumentVersion();
        link.click();

        return getFileUpdatePage(driver, version, isEditOfflineLinkDisplayed());
    }

    /**
     * Method to open properties to the specified version.
     * 
     * @param versionNumber revision number
     */
    public HtmlPage selectViewProperties(String versionNumber)
    {

        try
        {
            Double.parseDouble(versionNumber);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Version number passed is not a number : " + versionNumber, e);
        }
        try
        {
            driver.findElement(By.cssSelector("a[rel='" + versionNumber + "']~a[class*='historicProperties']")).click();

            HtmlPage page = getCurrentPage();
            if (page instanceof ShareDialogue)
            {
                return getCurrentPage();
            }
            return page;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("View Properties button to version " + versionNumber + " is not displayed", nse);
            }
        }
        throw new PageException("View Properties button to version " + versionNumber + " is not displayed");
    }

    /**
     * Select the inline edit link
     * 
     * @return {@link HtmlPage} edit inline page for HTML documents.
     */
    public HtmlPage selectInlineHtmlEdit()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(INLINE_EDIT_LINK));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
            throw new PageOperationException("Unable to select Inline Edit", exception);
        }

        return factoryPage.instantiatePage(driver,EditHtmlDocumentPage.class);
    }

    /**
     * Method to get Current Version details
     * 
     * @return {@link org.alfresco.po.share.site.document.VersionDetails}
     */
    public VersionDetails getCurrentVersionDetails()
    {
        try
        {
        	WebElement ele = driver.findElement(By.cssSelector("div[id$='_default-latestVersion']"));
            return new VersionDetails(driver, ele, factoryPage);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Latest Version details\"", nse);
        }
    }

    public List<VersionDetails> getOlderVersionDetails()
    {
        List<VersionDetails> versionDetailsList = new ArrayList<>();
        try
        {
            List<WebElement> olderVersions = driver.findElements(By.cssSelector("div[id$='_default-olderVersions']>table>tbody.yui-dt-data>tr>td>div.yui-dt-liner"));
            for (WebElement element : olderVersions)
            {
                versionDetailsList.add(new VersionDetails(driver, element, factoryPage));
            }
            return versionDetailsList;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Older Versions element not found");
            }
            throw new PageOperationException("Unable to find Older Versions", nse);
        }
    }

    /**
     * Clicks on the No button for no upgrade document for google docs.
     * 
     * @return HtmlPage
     */
    public HtmlPage cancelNo()
    {
        try
        {
            WebElement prompt = findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement cancelButton = findButton("No", elements);
            cancelButton.click();
        }
        catch (TimeoutException nse)
        {
            throw new TimeoutException("upgrade prompt was not found", nse);
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Clicks on the submit button to allow upgrade document for google docs.
     */
    public void clickYes()
    {
        try
        {
            WebElement prompt = findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("Yes", elements);
            okButton.click();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("upgrade prompt was not found", te);
        }
        catch (NoSuchElementException te)
        {
            throw new PageOperationException("authorisation prompt was not found", te);
        }
    }

    /**
     * Get the file content from the document details page.
     * 
     * @return theContentOfFile
     * <br/><br/>author Cristina Axinte
     */
    public String getDocumentBody()
    {
        try
        {
            waitForElement(DOCUMENT_BODY, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return driver.findElement(DOCUMENT_BODY).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find the document body. ", e);
        }
    }

    /**
     * Gets the popup message error when trying to edit offline a document
     * 
     * @return boolean popup message
     */
    public boolean isErrorEditOfflineDocument(String fileName)
    {

        String errorMessage = findAndWait(By.xpath(ERROR_EDITING_DOCUMENT)).getText();
        if (errorMessage.equals("You cannot edit '" + fileName + "'."))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * Verify if View Original Document is displayed
     * 
     * @return true if present
     */
    public boolean isViewOriginalLinkPresent()
    {
        try
        {
            return driver.findElement((VIEW_ORIGINAL_DOCUMENT)).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("View Original Document link is not displayed", te);
            }
        }

        return false;
    }

    /**
     * Click on View Original Document
     * 
     * @return new DocumentDetailsPage
     */
    public HtmlPage selectViewOriginalDocument()
    {
        try
        {
            WebElement link = findAndWait((VIEW_ORIGINAL_DOCUMENT));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to select View Original Document ", e);
        }

        return getCurrentPage();
    }

    /**
     * Returns true if Sync message is present
     * 
     * @return boolean
     */
    public boolean isSyncMessagePresent()
    {
        try
        {
            waitForElement(SYNC_MESSAGE, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
            WebElement syncMessage = driver.findElement(SYNC_MESSAGE);
            if (syncMessage != null)
                return true;
        }
        catch (TimeoutException toe)
        {
            logger.error("Message element not found!!", toe);
            return false;
        }
        return false;
    }

    /**
     * Method to click Cancel Editing in Google Docs
     * 
     * @return HtmlPage
     */
    public HtmlPage clickCancelEditingInGoogleDocs()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(LINK_CANCEL_GOOGLE_DOCS));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to find " + LINK_CANCEL_GOOGLE_DOCS, e);
        }
        return getCurrentPage().render();
    }

}
