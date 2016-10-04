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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.share.site.document.DocumentAction.CHNAGE_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.DocumentAction.DetailsPageType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.owasp.esapi.ESAPI;

/**
 * <li>This is a parent class for all the pages using View Details as asection.</li> <li>Functionality here is to support below details: Title, Modified
 * Details, Like(counter), Favourite, Comment(counter) Comment Navigation, Properties, Permissions, Share Panel, Tag Panel.</li>
 *
 * @author Naved Shah
 * @version 1.7.0
 */
public abstract class DetailsPage extends SitePage
{
    protected static final String CHECKEDOUT_MESSAGE_PLACEHOLDER = "div.node-header>div.status-banner.theme-bg-color-2.theme-border-4";
    protected static final String ACTION_SET_ID = "document.detail.action.set.id";
    public static final String DOCUMENT_VERSION_PLACEHOLDER = "div.node-header>div.node-info>h1.thin.dark>span.document-version";
    private static final String FAVOURITE_ACTION = "a.favourite-action";
    private static final String LIKE_ACTION = "a.like-action";
    private static final By PROP_FROM_LABEL = By.cssSelector(".viewmode-label");
    private static final By PROP_FROM_VALUE = By.cssSelector(".viewmode-value");
    private static final By FORM_VIEW_MODE_FIELD = By.cssSelector(".viewmode-field");
    private static final String ADD_COMMENT_BUTTON = "span.yui-button.yui-push-button.onAddCommentClick>span.first-child>button";
    private static final String COMMENT_PANEL = "div[id$='default-comments-list']";
    private static final String COMMENT_SECTION = "div[class='comments-list']";
    private static final String DIV_COMMENT_CONTENT = "//div[@class='comment-content']";
    private static final String SPAN_LIKE_COMMENT = "span.likes-count";
    private static final String THIN_DARK_TITLE_ELEMENT = "div.node-header>div.node-info>h1.thin.dark";
    // private static final String PAGE_SHARE_PANEL = "div.panel-body>div.link-info>a";
    private static final String PAGE_SHARE_PANEL = "//h2[text()='Share']";
    private static final String COMMENT_LINK = "a[class*='comment']";
    private static final String MANAGE_PERMISSIONS = "div[class$='-permissions'] a";
    private final static By PAGINATION = By.xpath(".//*[@id='template_x002e_comments_x002e_folder-details_x0023_default-paginator-top']");
    private Log logger = LogFactory.getLog(DetailsPage.class);
    private static final By NODE_PATH = By.cssSelector("div.node-path span a");
    private static final By TAGS_PANEL = By.cssSelector("div[class*='tags']");
    private static final By PROPERTIES_PANEL = By.cssSelector("div[class*='metadata-header']");
    private static final By PERMISSIONS_PANEL = By.cssSelector("div[id*='permissions']");
    private static final By COMMENT_COUNT = By.cssSelector("span.comment-count");
    protected String PERMISSION_SETTINGS_PANEL_CSS = ".folder-permissions";
    private final By SYNC_SETTINGS_PANEL_CSS = By.cssSelector(".document-sync");
    private final By COMMENTS_PANEL_CSS = By.cssSelector("div.comments-list");
    private final By COPY_TO_SHARE_LINKS = By.cssSelector("h3.thin.dark");
    private static final String LINK_VIEW_ON_GOOGLE_MAPS = "div[id$='default-actionSet'] div.document-view-googlemaps a";
    private static final String EDIT_TAGS_ICON = ".folder-tags h2 .edit";
    private static final String EDIT_PROPERTIES_ICON = ".folder-metadata-header h2 .edit";
    private static final String COMMENT_FIELD_CSS = "form[id*='add-form']";

    private static final String EDIT_TAGS_ICON_DOC = ".document-tags h2 .edit";
    private static final String EDIT_PROPERTIES_ICON_DOC = ".document-metadata-header h2 .edit";
    @SuppressWarnings("unused")
    private static final String MANAGE_RULES = "div[class$='-permissions'] a";
    public static final String TAKE_OWNERSHIP = "//span[text()='Become Owner']";
    public String deleteAction;
    public enum ShareLinks
    {
        COPY_LINK_TO_SHARE_THIS_PAGE("Copy this link to share the current page"), COPY_LINK_FOR_WEBDEV_URL("copy link for webdav url");

        String linkText;

        private ShareLinks(String linkText)
        {
            this.linkText = linkText;
        }

        public String getLinkText()
        {
            return linkText;
        }
    }

    /**
     * @param type String
     * @return Verify if the page viewed is the @type@ details page.
     */
    public boolean isDetailsPage(String type)
    {
        try
        {
            return findAndWait(By.cssSelector("div[id$='" + type + "-details']")).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Page, contains Element :" + "div[id$='" + type + "-details']" + " does not exist", e);
            }
        }
        return false;
    }

    /**
     * Gets the page detail title.
     *
     * @return String page detail page title
     */
    public String getContentTitle()
    {
        WebElement element = findAndWait(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
        return element.getText();
    }

    /**
     * Get all the comments that are displayed on the page.
     *
     * @return List<String> collection of comments
     */
    public List<String> getComments()
    {
        List<String> comments = new ArrayList<String>();
        try
        {
            WebElement commentInput = driver.findElement(By.cssSelector(COMMENT_PANEL));
            WebElement table = commentInput.findElement(By.tagName("table"));
            List<WebElement> elements = table.findElements(By.xpath(DIV_COMMENT_CONTENT));
            for (WebElement element : elements)
            {
                String elementText = element.getText();
                if (!elementText.isEmpty())
                {
                    WebElement p = element.findElement(By.tagName("p"));
                    comments.add(p.getText());
                }
            }
        }
        catch (StaleElementReferenceException sere)
        {
            logger.error("Element :" + COMMENT_PANEL + " or " + DIV_COMMENT_CONTENT + " does not exist", sere);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element :" + COMMENT_PANEL + " or " + DIV_COMMENT_CONTENT + " does not exist", nse);
        }
        return comments;
    }

    /**
     * Mimics the action of selecting the thumbs up icon on the details page.
     */
    public HtmlPage selectLike()
    {
        findAndWait(By.cssSelector("a[class^=\"like-action\"][title=\"Like this document\"]")).click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the thumbs up icon on the details folder.
     */
    public HtmlPage selectLikeFolder()
    {
        findAndWait(By.cssSelector("a[class^=\"like-action\"][title=\"Like this folder\"]")).click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the thumbs up icon on the details page.
     */
    public HtmlPage selectUnlike()
    {
        findAndWait(By.cssSelector("a[class^=\"like-action\"][title=\"Unlike\"]")).click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Checks for the number of positive votes.
     *
     * @return String number of votes
     */
    public String getLikeCount()
    {
        String count = "";
        try
        {
            count = findAndWait(By.cssSelector(SPAN_LIKE_COMMENT)).getText();
        }
        catch (TimeoutException nsee)
        {
            logger.error("Element :" + SPAN_LIKE_COMMENT + " does not exist", nsee);
        }
        return count;
    }

    /**
     * Get the detail permissions from view.
     *
     * @return {@link Map} of key values.
     */
    public Map<String, String> getPermissionsOfDetailsPage()
    {

        Map<String, String> prop = new HashMap<String, String>();
        try
        {
            WebElement formFieldElements = findAndWait(By.cssSelector("#template_x002e_folder-permissions_x002e_folder-details_x0023_default-body"));
            for (WebElement element : formFieldElements.findElements(FORM_VIEW_MODE_FIELD))
            {
                String key = element.findElement(PROP_FROM_LABEL).getText().trim().replace(":", "").replace(" ", "");
                String value = element.findElement(PROP_FROM_VALUE).getText().trim();
                prop.put(key, value);
            }
            return prop;

        }
        catch (NoSuchElementException elementException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Field Element is not found", elementException);
            }
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out while finding form fields", toe);
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Get the detail properties from view.
     *
     * @return {@link Map} of Key and Values.
     */
    public Map<String, Object> getProperties()
    {
        Map<String, Object> prop = new HashMap<String, Object>();
        try
        {
            for (WebElement webElement : findAndWaitForElements(By.cssSelector(".form-field")))
            {
                String key = webElement.findElement(PROP_FROM_LABEL).getText().trim().replace(":", "").replace(" ", "");

                if (key.equalsIgnoreCase("Categories"))
                {
                    WebElement value = webElement.findElement(PROP_FROM_VALUE);
                    Object categoriesObject;
                    List<Categories> categories = new ArrayList<Categories>();
                    try
                    {
                        List<WebElement> categoryElements = value.findElements(By.cssSelector("div[class='itemtype-cm:category']"));
                        for (WebElement categoryElement : categoryElements)
                        {
                            categories.add(Categories.getCategory(categoryElement.getText()));
                        }
                        if (categories == null || categories.isEmpty())
                        {
                            categoriesObject = value.getText();
                        }
                        else
                        {
                            categoriesObject = categories;
                        }
                    }
                    catch (NoSuchElementException e)
                    {
                        categoriesObject = value.getText();
                    }
                    prop.put(key, categoriesObject);
                }
                else
                {
                    String value = webElement.findElement(PROP_FROM_VALUE).getText().trim();
                    prop.put(key, value);
                }
            }
            return prop;
        }
        catch (TimeoutException exception)
        {
            return Collections.emptyMap();
        }

    }

    /**
     * Return list with categories names added to file;
     *
     * @return List<String>
     */
    public List<String> getCategoriesNames()
    {
        List<WebElement> categoriesElems = findAndWaitForElements(By.cssSelector("div[class='itemtype-cm:category']"));
        List<String> categoriesNames = new ArrayList<>();
        for (WebElement categoryElem : categoriesElems)
        {
            categoriesNames.add(categoryElem.getText());
        }
        return categoriesNames;
    }

    /**
     * Get the tags displayed on the tags section on the details details page.
     *
     * @return Collection of String tag name
     */
    public List<String> getTagList()
    {
        try
        {
            List<WebElement> tagList = findAndWaitForElements(By.cssSelector("span.tag"));
            if (tagList != null && !tagList.isEmpty())
            {
                List<String> tagNames = new ArrayList<String>();
                for (WebElement tagElement : tagList)
                {
                    tagNames.add(tagElement.getText());
                }
                return tagNames;
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.error("Element :span.tag does not exist", te);
            }
        }
        return Collections.<String>emptyList();
    }

    /**
     * Mimics the action of selecting the Favourite icon on the details page.
     *
     * @return {@link DetailsPage}
     */
    public HtmlPage selectFavourite()
    {
        try
        {
            findAndWait(By.cssSelector(FAVOURITE_ACTION)).click();
            waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Element :" + FAVOURITE_ACTION + " does not exist", e);
        }
        throw new PageException("Favourite element not present");
    }

    /**
     * Gets the Favourites status on the document page.
     *
     * @return Boolean
     */
    public boolean isFavourite()
    {
        try
        {
            WebElement favouriteStatus = driver.findElement(By.cssSelector(FAVOURITE_ACTION));
            String status = favouriteStatus.getAttribute("class");
            if (status != null)
            {
                return status.contains("favourite-action-favourite");
            }
            else
            {
                return false;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Gets the Like status on the details page.
     *
     * @return boolean
     */
    public boolean isLiked()
    {
        try
        {
            WebElement likeStatus = driver.findElement(By.cssSelector(LIKE_ACTION));
            String status = likeStatus.getAttribute("class");
            if (status != null)
            {
                return status.contains("like-action-liked");
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Gets the Tool Tip for Favourites action on the details page.
     *
     * @return String
     */
    public String getToolTipForFavourite()
    {
        String toolTip = "";
        try
        {
            WebElement favouriteStatus = driver.findElement(By.cssSelector(FAVOURITE_ACTION));
            toolTip = favouriteStatus.getAttribute("title");
            if (toolTip == null)
            {
                toolTip = "";
            }
        }
        catch (NoSuchElementException e)
        {
            throw new NoSuchElementException("Option to Favourite not found", e);
        }
        return toolTip;
    }

    /**
     * Gets the Tool Tip Like action on the details page.
     *
     * @return String
     */
    public String getToolTipForLike()
    {
        String toolTip = "";
        try
        {
            WebElement likeStatus = findAndWait(By.cssSelector(LIKE_ACTION));
            toolTip = likeStatus.getAttribute("title");
            if (toolTip == null)
            {
                toolTip = "";
            }
        }
        catch (NoSuchElementException e)
        {
            throw new NoSuchElementException("Option to Like not found", e);
        }
        return toolTip;
    }

    /**
     * Mimics the action of selecting edit properties on the details page.
     *
     * @return WebDriver (Drone object which has to be casted at runtime)
     */
    public EditDocumentPropertiesPage selectEditProperties()
    {
        String type = "document";
        try
        {
            findAndWait(By.cssSelector("div[id$='default-actionSet'] div." + type + "-edit-metadata a")).click();
            return getCurrentPage().render();
        }
        catch (TimeoutException e)
        {
            logger.error("Element :" + "div[id$='default-actionSet'] div." + type + "-edit-metadata a" + " does not exist", e);
        }
        throw new PageException("Properties not present in the page");
    }

    /**
     * Adding a comment to a details by selecting add to prompt the input field,
     * as this is based on a rich editor JavaScript was used to enter the
     * comment.
     *
     * @param comment String user comment
     * @param encoder Encoder as html, javascript, no encoder
     * @return {@link HtmlPage} page response
     */
    public HtmlPage addComment(String comment, Encoder encoder)
    {
        String encodedComment = comment;
        if (encoder == null)
        {
            // Assume no encoding
            encoder = Encoder.ENCODER_NOENCODER;
        }

        switch (encoder)
        {
            case ENCODER_HTML:
                encodedComment = ESAPI.encoder().encodeForHTML(comment);
                logger.info("Comment encoded as HTML");
                break;
            case ENCODER_JAVASCRIPT:
                encodedComment = ESAPI.encoder().encodeForJavaScript(comment);
                logger.info("Comment encoded as JavaScript");
                break;
            default:
                logger.info("Comment is not encoded");
        }
        return addComment(encodedComment);
    }

    /**
     * Method for verify present Comment section on the details page or not.
     *
     * @return boolean
     */
    public boolean isCommentSectionPresent()
    {
        try
        {
            return findAndWait(By.cssSelector(COMMENT_SECTION)).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.error("Element :" + COMMENT_SECTION + " does not exist", e);
        }
        return false;
    }

    /**
     * Adding a comment to a details by selecting add to prompt the input field,
     * as this is based on a rich editor JavaScript was used to enter the
     * comment.
     *
     * @param comment String user comment
     * @return {@link HtmlPage} page response
     */
    public HtmlPage addComment(final String comment)
    {
        WebElement addComment = null;
        WebElement tinymceaddCommentButton = null;

        try
        {
            addComment = findAndWait(By.cssSelector(ADD_COMMENT_BUTTON));

            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Add Comment panel isDisplayed : %s ", addComment.isDisplayed()));
            }

        }
        catch (TimeoutException e)
        {
            try
            {
                tinymceaddCommentButton = findAndWait(By.cssSelector("button[id$='default-add-submit-button']"));

                if (logger.isTraceEnabled())
                {
                    logger.trace(String.format("Add Comment panel isDisplayed in tinyMCE editor : %s ", tinymceaddCommentButton.isDisplayed()));
                }

            }
            catch (TimeoutException te)
            {
            }
        }

        if ((addComment != null && addComment.isDisplayed()) || (tinymceaddCommentButton != null && tinymceaddCommentButton.isDisplayed()))
        {

            // String whichPage = getTitle();
            String addCommentButtonId = null;

            /*
             * Adding comment uses the rich editor to ensure it works on all
             * drivers we are using a js script to click on add comment, enter
             * comment to rich editor and double click on adding comment button
             * to submit.
             */
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", comment);
            if (addComment != null)
            {
                addCommentButtonId = addComment.getAttribute("id");
                String addCommentJs = String.format("document.getElementById('%s').click();", addCommentButtonId);
                executeJavaScript(addCommentJs);
            }

            executeJavaScript(setCommentJs);
            /*
             * As of Alfresco v4.2 the add comment button is enclosed in a span
             * that disables/enables the add comment button hence why we need to
             * know of it to enable the button.
             */
            driver.findElement(By.cssSelector("button[id$='_default-add-submit-button']")).click();
            // check to ensure js completed
            if (isErrorBalloonMessageDisplay() || addCommentButtonId != null && findAndWait(By.id(addCommentButtonId)).isDisplayed())
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Adding comment JavaScript executed successfully");
                }
            }
        }
        else
        {
            throw new PageException("Add comment form has not been rendered in time");
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("about to render new page response");
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Removes the comment on detail page.
     *
     * @param comment to remove
     * @return DetailsPage page object response
     */
    public HtmlPage removeComment(final String comment)
    {
        if (StringUtils.isEmpty(comment))
        {
            throw new UnsupportedOperationException("Comment input required");
        }
        WebElement commentWebElement = getCommentWebElement(comment);
        mouseOver(commentWebElement);
        WebElement delete = commentWebElement.findElement(By.name(".onConfirmDeleteCommentClick"));
        delete.click();
        confirmDelete();
        canResume();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Delete the comment on detail page.
     *
     * @param comment to remove
     * @return DetailsPage page object response
     */
    public HtmlPage deleteComment(final String comment)
    {
        if (StringUtils.isEmpty(comment))
        {
            throw new UnsupportedOperationException("Comment input required");
        }
        List<WebElement> comments = findAndWaitForElements(By.xpath("//div[@class='comment-details']//div[@class='comment-content']"));
        WebElement commentEl = null;
        if (logger.isTraceEnabled())
        {
            logger.trace(String.format("Are there comments on the page : %s ", comments.isEmpty()));
        }
        for (WebElement commentElement : comments)
        {
            if (commentElement.getText().equals(comment))
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace(String.format("We have found a match to comment ' %s ' : true", comment));
                }
                commentEl = commentElement;
            }
        }
        if (commentEl != null)
        {
            mouseOver(commentEl);
            WebElement delete = findAndWait(By.xpath("//p[text()='" + comment + "']/../..//a[@title='Delete Comment']"));
            delete.click();
            confirmDelete();
            canResume();
            waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

        }

        return getCurrentPage();

    }

    /**
     * Confirm delete dialog acceptance action.
     */
    protected void confirmDelete()
    {
        try
        {
            WebElement prompt = findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(By.tagName("button"));
            // Find the delete button in the prompt
            WebElement delete = findButton("Delete", elements);
            delete.click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Element :" + PROMPT_PANEL_ID + " does not exist", toe);
        }

    }

    /**
     * Verifies the modified information is present in the page object.
     *
     * @return boolean
     */
    public boolean isModifiedByDetailsPresent()
    {
        String selector =  "span.item-modifier";
        try
        {
            WebElement modified = driver.findElement(By.cssSelector(selector));
            boolean hasLink = modified.findElement(By.tagName("a")).isDisplayed();
            if (!modified.getText().isEmpty() && hasLink)
            {
                return true;
            }
        }
        catch (TimeoutException toe)
        {
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;

    }

    /**
     * Verify the share panel is present or not in the page.
     *
     * @return boolean
     */
    public boolean isSharePanePresent()
    {
        try
        {
            WebElement sharePaneElement = driver.findElement(By.xpath(PAGE_SHARE_PANEL));
            return sharePaneElement.isDisplayed();
        }
        catch (NoSuchElementException exce)
        {
        }
        return false;

    }

    TinyMceEditor tinyMceEditor;
    /**
     * Get Rich Text or to edit the contents of Comments etc.
     *
     * @return TinyMceEditor
     */
    public TinyMceEditor getContentPage()
    {

        WebElement addComment = findAndWait(By.cssSelector(ADD_COMMENT_BUTTON));
        if (logger.isTraceEnabled())
        {
            logger.trace(String.format("Add Comment panel isDisplayed : %s ", addComment.isDisplayed()));
        }
        if (addComment.isDisplayed())
        {
            String addCommentButtonId = addComment.getAttribute("id");
            /*
             * Adding comment uses the rich editor to ensure it works on all
             * drivers we are using a js script to click on add comment, enter
             * comment to rich editor and double click on adding comment button
             * to submit.
             */
            String addCommentJs = String.format("document.getElementById('%s').click();", addCommentButtonId);
            executeJavaScript(addCommentJs);
        }

        return tinyMceEditor;
    }

    /**
     * @return boolean
     */
    public boolean isCommentLinkPresent()
    {
        try
        {
            return findAndWait(By.cssSelector(COMMENT_LINK)).isDisplayed();
        }
        catch (TimeoutException toe)
        {

        }
        throw new PageException("Comment link is not present!");
    }

    /**
     * The number of comments value displayed on the span comment count.
     *
     * @return int total number of comments
     */
    public int getCommentCount()
    {
        try
        {
            WebElement span = driver.findElement(COMMENT_COUNT);
            return Integer.valueOf(span.getText());
        }
        catch (NoSuchElementException e)
        {
        }
        catch (NumberFormatException ne)
        {
        }

        return 0;
    }

    /**
     * Mimics the action of selecting the Manage Permission icon on the document
     * page.
     *
     * @return {@link ManagePermissionsPage}
     */
    public HtmlPage selectManagePermissions()
    {
        logger.trace(" -- Searching for manage permission link --");
        driver.findElement(By.cssSelector(MANAGE_PERMISSIONS)).click();
        logger.trace(" -- Got it --");
        return getCurrentPage();
    }

    /**
     * Clicks on Take Ownership link under folder options on the right handside
     * 
     * @return
     */
    public TakeOwnershipPage selectTakeOwnership()
    {
        try
        {
            findAndWait(By.xpath(TAKE_OWNERSHIP)).click();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Take Ownership link.", toe);
            }
        }
        return factoryPage.instantiatePage(driver, TakeOwnershipPage.class);
    }
    
   
    /**
     * Mimics the action of select the manage aspects.
     *
     * @return {@link SelectAspectsPage}
     */
    public SelectAspectsPage selectManageAspects()
    {
        try
        {
            findAndWait(By.cssSelector(".document-manage-aspects>a")).click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to select manage aspects", exception);
        }
        return factoryPage.instantiatePage(driver, SelectAspectsPage.class);
    }

    /**
     * Mimics the action of return to the repository Page
     *
     * @return RepositoryPage
     */
    public HtmlPage navigateToFolderInRepositoryPage() throws PageOperationException
    {
        WebElement folderLink = null;
        try
        {
            List<WebElement> path = findAndWaitForElements(NODE_PATH);
            if (path.isEmpty())
            {
                throw new PageException("The link elements are not found");
            }
            folderLink = path.get((path.size() - 1));
            folderLink.click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cannot go back to document library", te);
        }
    }

    /**
     * Mimics the action of return to the parent folder from details Page
     *
     * @return RepositoryPage
     */
    public HtmlPage navigateToParentFolder() throws PageOperationException
    {
        WebElement folderLink = null;
        try
        {
            List<WebElement> path = findAndWaitForElements(NODE_PATH);
            if (path.isEmpty())
            {
                throw new PageException("The link elements are not found");
            }
            // Alf-Issue: Workaround
            // if folder-details page, go back one more level to parentFolder
            int parentNodes = path.size();
            if (isDetailsPage("folder") && parentNodes > 1)
            {
                folderLink = path.get(parentNodes - 2);
            }
            else
            {
                folderLink = path.get((parentNodes - 1));
            }

            folderLink.click();
            RepositoryPage repositoryPage = getCurrentPage().render();
            repositoryPage.setViewType(repositoryPage.getNavigation().getViewType());
            return repositoryPage;
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cannot go back to document library", te);
        }
    }

    /**
     * Checks if link is displayed.
     *
     * @return true if visible
     */
    public boolean isDocumentActionPresent(DocumentAction action)
    {
        DetailsPageType type;
        try
        {

            if (this instanceof FolderDetailsPage)
            {
                type = DetailsPageType.FOLDER;
            }
            else if (this instanceof DocumentDetailsPage)
            {
                type = DetailsPageType.DOCUMENT;
            }
            else
            {
                throw new UnsupportedOperationException("This action is not supported.");
            }
            return driver.findElement(By.cssSelector(action.getDocumentAction(type))).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("LinkType is not present Css value is :", nse);
            }
            return false;
        }
    }

    /**
     * Editing a comment to a details by selecting the edit link,
     * as this is based on a rich editor JavaScript was used to enter the
     * comment.
     *
     * @param comment    String user comment
     * @param newComment String user new comment
     * @return {@link HtmlPage} page response
     */
    public HtmlPage editComment(final String comment, final String newComment)
    {
        try
        {
            WebElement editCommentLink = findAndWait(By.xpath(String.format("//div[@class='comment-content']/p[text()='%s']/../..", comment)));
            mouseOver(editCommentLink);
            editCommentLink.findElement(By.cssSelector("span.comment-actions a")).click();
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", newComment);
            executeJavaScript(setCommentJs);
            // check to ensure js completed
            if (isErrorBalloonMessageDisplay() || newComment.equals(new TinyMceEditor().getText()))
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Adding comment JavaScript executed successfully");
                }
            }
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find the edit comment link", te);
        }
        catch (NoSuchElementException te)
        {
            logger.error("`Not able find the edit comment link", te);
        }

        throw new PageException("Edit comment form has not been rendered in time");
    }

    /**
     * Mimics the action of saving the edited comments on details page.
     *
     * @return {@link DetailsPage}
     */
    public HtmlPage saveEditComments()
    {
        try
        {
            WebElement saveButton = getVisibleElement(By.cssSelector("button[id$='submit-button']"));
            saveButton.click();
            if (isErrorBalloonMessageDisplay() || !saveButton.isDisplayed())
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Adding comment JavaScript executed successfully");
                }
            }
            waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find the edit comments save button css.", e);
        }
        throw new PageException("Error in saving the edit comments.");
    }

    /**
     * Mimics the action of cancelling the edited comments on details page.
     *
     */
    public void cancelEditComments()
    {
        try
        {
            getVisibleElement(By.cssSelector("button[id$='cancel-button']")).click();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find the edit comments cancel button css.", e);
            throw new PageException("Error in cancelling the edit comments.");
        }
    }

    public AddCommentForm clickAddCommentButton()
    {
        WebElement addComment = findAndWait(By.cssSelector(ADD_COMMENT_BUTTON));
        addComment.click();
        return new AddCommentForm();
    }

    public EditCommentForm clickEditCommentButton(String comment)
    {
        checkNotNull(comment);
        WebElement commentElement = getCommentWebElement(comment);
        mouseOver(commentElement);
        WebElement edit = commentElement.findElement(By.name(".onEditCommentClick"));
        edit.click();
        return new EditCommentForm();
    }

    /**
     * Check is comment has edit button on details page
     *
     * @param comment String
     * @return boolean
     */
    public boolean isEditCommentButtonPresent(String comment)
    {
        Boolean present = false;
        try
        {
            checkNotNull(comment);
            List<WebElement> comments = findAndWaitForElements(By.xpath("//div[@class='comment-details']//div[@class='comment-content']"));
            WebElement commentEl = null;
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Are there comments on the page : %s ", comments.isEmpty()));
            }
            for (WebElement commentElement : comments)
            {
                if (commentElement.getText().equals(comment))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace(String.format("We have found a match to comment ' %s ' : true", comment));
                    }
                    commentEl = commentElement;
                }
            }
            if (commentEl != null)
            {
                mouseOver(commentEl);
                present = commentEl.findElement(By.name(".onEditCommentClick")).isDisplayed();

            }
            return present;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Edit comment link is not displayed for selected comment", nse);
            }
        }
        return false;

    }

    /**
     * Check is comment has delete button on details page
     *
     * @param comment String
     * @return boolean
     */
    public boolean isDeleteCommentButtonPresent(String comment)
    {
        Boolean present = false;
        try
        {
            checkNotNull(comment);
            List<WebElement> comments = findAndWaitForElements(By.xpath("//div[@class='comment-details']//div[@class='comment-content']"));
            WebElement commentEl = null;
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Are there comments on the page : %s ", comments.isEmpty()));
            }
            for (WebElement commentElement : comments)
            {
                if (commentElement.getText().equals(comment))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace(String.format("We have found a match to comment ' %s ' : true", comment));
                    }
                    commentEl = commentElement;
                }
            }
            if (commentEl != null)
            {
                mouseOver(commentEl);
                present = commentEl.findElement(By.name(".onConfirmDeleteCommentClick")).isDisplayed();

            }
            return present;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Delete comment link is not displayed for selected comment", nse);
            }
        }
        return false;

    }

    /**
     * Check is comment has Avatar, Edit and Remove buttons, Commentator name.
     *
     * @param comment String
     * @return boolean
     */
    public boolean isCommentCorrect(String comment)
    {
        try
        {
            checkNotNull(comment);
            return isCommentButtonsEnableAndDisplay(comment) && isCommentAvatarDisplay(comment) && isCommentatorNameDisplayAndEnable(comment);
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Check is Remove and Edit Buttons display and Enable.
     *
     * @param comment String
     * @return boolean
     */

    private boolean isCommentButtonsEnableAndDisplay(String comment)
    {
        WebElement commentElement = getCommentWebElement(comment);
        mouseOver(commentElement);
        WebElement edit = commentElement.findElement(By.name(".onEditCommentClick"));
        WebElement delete = commentElement.findElement(By.name(".onConfirmDeleteCommentClick"));
        return edit.isEnabled() && delete.isEnabled() && edit.isDisplayed() && delete.isDisplayed();
    }

    private boolean isCommentAvatarDisplay(String comment)
    {
        WebElement commentElement = getCommentWebElement(comment);
        WebElement avatar = commentElement.findElement(By.xpath(".//a[contains(@href,'profile')]"));
        return avatar.isDisplayed();
    }

    private boolean isCommentatorNameDisplayAndEnable(String comment)
    {
        WebElement commentElement = getCommentWebElement(comment);
        WebElement commentatorName = commentElement.findElement(By.xpath(".//img[@alt='Avatar']"));
        return commentatorName.isDisplayed() && commentatorName.isEnabled();
    }

    public String getCommentChangeTime(String comment)
    {
        WebElement commentElement = getCommentWebElement(comment);
        WebElement timeElement = commentElement.findElement(By.xpath(".//span[@class='info']/span"));
        return timeElement.getAttribute("title");
    }

    public boolean isErrorBalloonMessageDisplay()
    {
        try
        {
            return findAndWait(By.cssSelector(".balloon>.text>div"), 1000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    private WebElement getCommentWebElement(String comment)
    {
        try
        {
            List<WebElement> comments = findAndWaitForElements(By.xpath("//div[@class='comment-details']"));
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Are there comments on the page : %s ", comments.isEmpty()));
            }

            for (WebElement commentElement : comments)
            {
                WebElement targetComment = commentElement.findElement(By.xpath(DIV_COMMENT_CONTENT));
                String commentOnPage = targetComment.getText();
                if (comment.equals(commentOnPage))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace(String.format("We have found a match to comment ' %s ' : true", comment));
                    }
                    return commentElement;
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            return getCommentWebElement(comment);
        }
        throw new NoSuchElementException("Required comment didn't found!");
    }

    /**
     * Open DeleteConfirmForm for comment. Verify that all buttons and text correct.
     *
     * @param comment String
     * @return boolean
     */
    public boolean checkConfirmDeleteForm(final String comment)
    {
        WebElement commentWebElement = getCommentWebElement(comment);
        mouseOver(commentWebElement);
        WebElement delete = commentWebElement.findElement(By.name(".onConfirmDeleteCommentClick"));
        delete.click();
        return isDeleteDialogDisplay() && isDeleteDialogTextCorrect() && isDeleteDialogButtonsEnableAndDisplay();
    }

    private boolean isDeleteDialogDisplay()
    {
        WebElement dialogForm = findAndWait(By.cssSelector("#prompt"));
        return dialogForm.isDisplayed();
    }

    private boolean isDeleteDialogTextCorrect()
    {
        WebElement dialogText = findAndWait(By.cssSelector(".yui-simple-dialog .bd"));
        return dialogText.getText().equals("Are you sure you want to delete this comment?");
    }

    private boolean isDeleteDialogButtonsEnableAndDisplay()
    {
        boolean result = false;
        List<WebElement> buttons = findAndWaitForElements(By.cssSelector("#prompt button"));
        for (WebElement button : buttons)
        {
            result = button.isDisplayed() && button.isEnabled();
        }
        return result;
    }

    /**
     * Close Confrim Delete Comment form.
     */
    public void cancelDeleteComment()
    {
        WebElement prompt = findAndWait(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        WebElement cancel = findButton("Cancel", elements);
        cancel.click();
    }

    public PaginationForm getCommentsPagination()
    {
        return new PaginationForm(driver, PAGINATION);
    }

    /**
     * This method finds whether the tags panel is displayed or not.
     *
     * @return boolean
     */
    public boolean isTagsPanelPresent()
    {
        try
        {
            return driver.findElement(TAGS_PANEL).isDisplayed();
        }
        catch (NoSuchElementException exce)
        {
        }
        return false;
    }

    /**
     * This method finds whether the properties panel is displayed or not.
     *
     * @return boolean
     */
    public boolean isPropertiesPanelPresent()
    {
        try
        {
            return driver.findElement(PROPERTIES_PANEL).isDisplayed();
        }
        catch (NoSuchElementException exce)
        {
        }
        return false;
    }

    /**
     * This method finds whether the like link is displayed or not.
     *
     * @return boolean
     */
    public boolean isLikeLinkPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(LIKE_ACTION)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * This method finds whether the favourite link is displayed or not.
     *
     * @return boolean
     */
    public boolean isFavouriteLinkPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(FAVOURITE_ACTION)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find favourite link");
        }
        return false;
    }

    /**
     * This method finds whether the add comment button is displayed or not.
     *
     * @return boolean
     */
    public boolean isAddCommentButtonPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(ADD_COMMENT_BUTTON)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * This method finds whether the comments panel is displayed or not.
     *
     * @return boolean
     */
    public boolean isCommentsPanelPresent()
    {
        try
        {
            return driver.findElement(COMMENTS_PANEL_CSS).isDisplayed();

        }
        catch (NoSuchElementException nse)
        {
        }

        return false;
    }

    /**
     * This method finds whether the add comments button is enabled or not.
     *
     * @return boolean
     */
    public boolean isAddCommentsButtonEnbaled()
    {
        try
        {
            return driver.findElement(By.cssSelector(ADD_COMMENT_BUTTON)).isEnabled();

        }
        catch (NoSuchElementException nse)
        {
        }

        return false;
    }

    /**
     * This method finds whether the CopyToShareLink is present or not.
     *
     * @return boolean
     */
    public boolean isCopyShareLinkPresent()
    {
        try
        {
            for (WebElement element : driver.findElements(COPY_TO_SHARE_LINKS))
            {
                if (element.getText().equalsIgnoreCase(ShareLinks.COPY_LINK_TO_SHARE_THIS_PAGE.getLinkText()))
                {
                    return element.findElement(By.xpath("//following-sibling::div/input")).isDisplayed();
                }
            }
        }
        catch (NoSuchElementException nse)
        {
        }

        return false;
    }

    /**
     * This method finds whether the Sync panel is present or not.
     *
     * @return boolean
     */
    public boolean isSynPanelPresent()
    {
        try
        {
            return driver.findElement(SYNC_SETTINGS_PANEL_CSS).isDisplayed();

        }
        catch (NoSuchElementException nse)
        {
        }

        return false;
    }

    /**
     * This method finds whether the Permission panel is present or not.
     *
     * @return boolean
     */
    public boolean isPermissionsPanelPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(PERMISSION_SETTINGS_PANEL_CSS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Add comment by clicking 'Comment' link
     *
     * @return AddCommentForm
     */

    public AddCommentForm selectAddComment()
    {
        findAndWait(By.xpath("//a[contains(@class,'comment')]")).click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return new AddCommentForm();
    }

    /**
     * Click Add comment button from the comment form after clicking 'Comment' link
     *
     * @return AddCommentForm
     */
    public AddCommentForm clickAddButton()
    {
        findAndWait(By.xpath("//button[contains(@id,'comments')]")).click();
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return new AddCommentForm();
    }

    /**
     * Click on Edit Tags icon from Tags panel at Details page
     *
     * @param folder when true is for folder details page; false - document details page
     * @return EditDocumentPropertiesPage
     */
    public HtmlPage clickEditTagsIcon(boolean folder)
    {
        if (folder)
        {
            findAndWait(By.cssSelector(EDIT_TAGS_ICON)).click();
        }
        else
        {
            findAndWait(By.cssSelector(EDIT_TAGS_ICON_DOC)).click();
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Click on Edit Properties icon from Properties panel at Details page
     *
     * @param folder when true is for folder details page; false - document details page
     * @return EditDocumentPropertiesPage
     */
    public HtmlPage clickEditPropertiesIcon(boolean folder)
    {
        if (folder)
        {
            findAndWait(By.cssSelector(EDIT_PROPERTIES_ICON)).click();
        }
        else
        {
            findAndWait(By.cssSelector(EDIT_PROPERTIES_ICON_DOC)).click();
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * @return boolean
     */
    public boolean isCommentFieldPresent()
    {
        try
        {
            return findAndWait(By.cssSelector(COMMENT_FIELD_CSS)).isDisplayed();
        }
        catch (TimeoutException toe)
        {

        }
        throw new PageException("Comment field isn't present!");
    }

    /**
     * Verify if Link View on Google Maps is visible.
     *
     * @return true if displayed
     * <br/><br/>author rmanyam
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
     * Method to verify whether Add Comment button is present
     *
     * @return true if displayed
     */
    public boolean isAddCommentButtonDisplayed()
    {
        try
        {
            return findAndWait(By.cssSelector(ADD_COMMENT_BUTTON), 2000).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    public boolean isTagIconDisplayed()
    {
        return isEditIconPresent(TAGS_PANEL);
    }

    public boolean isEditPropertiesIconDisplayed()
    {
        return isEditIconPresent(PROPERTIES_PANEL);
    }

    public boolean isEditPermissionsIconDisplayed()
    {
        return isEditIconPresent(PERMISSIONS_PANEL);
    }

    protected boolean isEditIconPresent(By locator)
    {
        try
        {
            WebElement webElement = findAndWait(locator);
            return webElement.findElement(By.cssSelector(".alfresco-twister-actions>a")).isDisplayed();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to locate the panel");
        }
    }

    /**
     * Mimics the action of deleting a document/folder.
     */
    public HtmlPage delete()
    {
        WebElement button = findAndWait(By.cssSelector("div.document-delete>a"));
        button.click();
        confirmDeleteAction();
        return getCurrentPage();
    }

    /**
     * Mimics the action of deleting a document/folder.
     */
    public HtmlPage confirmDeleteAction()
    {
    	deleteAction = factoryPage.getValue("delete.button.label");
	    SharePopup confirmDelete = getCurrentPage().render();
	    return confirmDelete.clickActionByName(deleteAction);
    }
    /**
     * Select link Copy to... from Actions
     *
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectCopyTo()
    {
        return selectCopyOrMoveTo("Copy to...");
    }

    /**
     * Select link Move to... from Actions
     *
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectMoveTo()
    {
        return selectCopyOrMoveTo("Move to...");
    }

    /**
     * Select link Unzip to... from Actions
     *
     * @return
     */
    public CopyOrMoveContentPage selectUnzipTo()
    {
        return selectCopyOrMoveTo("Unzip to...");
    }
    private CopyOrMoveContentPage selectCopyOrMoveTo(String link)
    {
        try
        {
            WebElement toLink = findAndWait(By.linkText(link));
            toLink.click();
            return factoryPage.instantiatePage(driver,CopyOrMoveContentPage.class);
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Action " + link + " is not displayed ", e);
            }
        }
        catch (StaleElementReferenceException st)
        {
        }
        throw new PageOperationException("Unable to find " + link + " Link at a page");
    }

    private HtmlPage selectChangeType()
    {
        try
        {
            WebElement changeTypeLink = findAndWait(By.cssSelector(CHNAGE_TYPE.getCssValue() + " a"));
            changeTypeLink.click();
            return factoryPage.getPage(driver).render();

        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + CHNAGE_TYPE);
        }
    }

    public HtmlPage changeType(String typeValue)
    {        
        ChangeTypePage changeTypePage = selectChangeType().render();
        
        List<String> availableTypes = changeTypePage.getTypes();
        logger.info("Types Found: " + availableTypes.size());
        
        try 
        {
	        int typeIndex = availableTypes.indexOf(typeValue);
	        
	        logger.info("Type Found at index: " + typeIndex);
	        
	        if (typeIndex <= 0)
	        {
	            throw new ShareException(typeValue + " isn't present in the list");
	        }
	        else
	        {
	            changeTypePage.selectChangeTypeByIndex(typeIndex);
	            changeTypePage.selectSave().render();
	            waitUntilAlert();
	        }
        }
        catch(ClassCastException | NullPointerException e)
        {
        	throw new ShareException("Error getting the index of: " + typeValue, e);
        }

        return getCurrentPage();
    }
    
    /**
     * Util to true if the specified type is available in the drop down list
     * @param typeValue
     * @return true if the type is available, otherwise false
     */
    public boolean isTypeAvailable(String typeValue)
    {
        try
        {
        	ChangeTypePage changeTypePage = selectChangeType().render();;
            
            if (changeTypePage.getTypes().contains(typeValue))
            {
                return true;
            }
        }
        catch (ShareException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Change Type dropdown is not displayed ", e);
            }
        }
        return false;
    }

    /**
     * Get HTML for comment
     *
     * @return String HTML
     */
    public String getCommentHTML(String comment)
    {
        try
        {
            List<WebElement> comments = findAndWaitForElements(By.xpath("//div[@class='comment-details']"));
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Are there comments on the page : %s ", comments.isEmpty()));
            }

            for (WebElement commentElement : comments)
            {
                WebElement targetComment = commentElement.findElement(By.xpath(DIV_COMMENT_CONTENT));
                String commentOnPage = targetComment.getText();
                if (comment.equals(commentOnPage))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace(String.format("We have found a match to comment ' %s ' : true", comment));
                    }
                    String html = targetComment.getAttribute("innerHTML");
                    return html;
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            return getCommentHTML(comment);
        }
        throw new NoSuchElementException("Required comment didn't found!");
    }
}
