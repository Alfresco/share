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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.repository.ModelsPage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Entity that models the list of file or directories as it appears on the {@link DocumentLibraryPage}. The list models the HTML element representing
 * the file or directory.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @author mbhave
 */
public abstract class FileDirectoryInfoImpl extends PageElement implements FileDirectoryInfo
{
    private static Log logger = LogFactory.getLog(FileDirectoryInfoImpl.class);
    protected static final By INLINE_EDIT_LINK = By.cssSelector("div.document-inline-edit>a[title='Inline Edit']>span");
    protected static final By EDIT_OFFLINE_LINK = By.cssSelector("div.document-edit-offline>a[title='Edit Offline']>span");
    protected static final By MORE_ACTIONS_MENU = By.cssSelector("div.more-actions");
    protected static final By FILE_VERSION_IDENTIFIER = By.cssSelector("span.document-version");
    protected static final By VIEW_IN_BROWsER_ICON = By.cssSelector("div.document-view-content>a");
    protected static final By CATEGORY_LINK = By.cssSelector("span.category > a");
    protected static final String INDIRECTLY_SYNCED_ICON = "a[data-action='onCloudIndirectSyncIndicatorAction']";
    protected static final String FAILED_SYNC_ICON = "a[data-action='onCloudSyncFailedIndicatorAction']";
    private static final String EDITED_ICON = "img[alt='editing']";
    private static final String WORKFLOW_ICON = "img[alt='active-workflows']";
    @SuppressWarnings("unused")
    private static final String FILE_EDIT_INFO = "div.yui-dt-liner div:nth-of-type(1)";
    private static final String IMG_FOLDER = ".*/documentlibrary/images/.*folder.*.png";
    private static final String FAVOURITE_CONTENT = "a[class*='favourite-action']";
    private static final String LIKE_CONTENT = "a[class*='like-action']";
    private static final String LIKE_COUNT = "span.likes-count";
    private static final String RULES_ICON = "img[alt='rules']";
    private static final String SELECT_CHECKBOX = "input[id^='checkbox-yui']";
    private static final By INFO_BANNER = By.cssSelector("div.info-banner");
    private static final By LOCK_ICON = By.cssSelector("img[alt='lock-owner']");
    private static final By COMMENT_LINK = By.cssSelector("a.comment");
    private static final By QUICK_SHARE_LINK = By.cssSelector("a.quickshare-action");
    private static final By EDIT_PROP_ICON = By.cssSelector("div.document-edit-properties>a");
    protected static final By CREATE_TASK_WORKFLOW = By.cssSelector("div#onActionAssignWorkflow > a");
    private static final By TAGS_FIELD = By.cssSelector("div.detail span.item span.faded");
    protected static String ACTIONS_MENU = "td:nth-of-type(5)";
    protected final By REQUEST_TO_SYNC = By.cssSelector("div#onActionCloudSyncRequest>a[title='Request Sync']");
    protected final String LINK_MANAGE_PERMISSION = "div[class$='-permissions']>a";
    protected String FILE_DESC_IDENTIFIER = "td.yui-dt-col-fileName div.yui-dt-liner div:nth-of-type(2)";
    protected String TITLE = "span.title";
    protected By TAG_LINK_LOCATOR = By.cssSelector("div.yui-dt-liner>div>span>span>a.tag-link");
    protected String THUMBNAIL = "td.yui-dt-col-thumbnail>div>span>a";
    protected String THUMBNAIL_TYPE = "td.yui-dt-col-thumbnail>div>span";
    protected String THUMBNAIL_LINK_FOLDER = "td.yui-dt-col-thumbnail>div>span.folder>span.link";
    protected String THUMBNAIL_LINK_FILE = "td.yui-dt-col-thumbnail>div>span.thumbnail>span.link";
    protected String LOCATE_LINKED_ITEM = "div#onActionLocate a";
    protected String DELETE_LINK = "div#onActionDelete a";
    protected String COPY_LINK = "div#onActionCopyTo a";
    protected String MOVE_LINK = "div#onActionMoveTo a";
    protected static final String ACTIONS_LIST = "div.action-set>div";
    protected String INPUT_TAG_NAME = "div.inlineTagEdit input";
    protected String INPUT_CONTENT_NAME = "input[name='prop_cm_name']";
    protected String nodeRef;
    protected String INLINE_TAGS = "div.inlineTagEdit>span>span.inlineTagEditTag";
    protected String GOOGLE_DOCS_URL = "googledocsEditor?";
    protected String FILENAME_IDENTIFIER = "h3.filename a";
    protected String DOWNLOAD_DOCUMENT = "div.document-download>a";
    protected String EDIT_CONTENT_NAME_ICON = "span[title='Rename']";
    protected String DOWNLOAD_FOLDER = "div.folder-download>a";
    protected String rowElementXPath = null;
    protected String MORE_ACTIONS = "div#onActionShowMore>a.show-more";
    protected String VIEW_ORIGINAL_DOCUMENT = "div.document-view-original>a";
    protected String DECLARE_AS_RECORD = "div.rm-create-record>a";
    protected String IN_COMPLETE_RECORD = "div.info-banner";
    protected String IS_FOLDER = "img[src*='.png']";
    protected String DESCRIPTION_INFO = "div.detail>span.faded";
    protected String LOCATE_FILE = "div.document-locate>a";
    protected By DETAIL_WINDOW = By.xpath("//div[@class='alf-detail-thumbnail']/../../..");
    protected String DOCUMENT_WEB_ASSET = "div.document-preview-webasset>a";
    protected static final String LINK_CHECKIN_GOOGLE_DOCS = "#onGoogledocsActionCheckin a";
    protected static final String LINK_CANCEL_GOOGLE_DOCS = "#onGoogledocsActionCancel a";
    private static final By MODELINFO_FIELD = By.cssSelector("td.yui-dt-col-fileName div.yui-dt-liner div span");
    private static final String TAG_INFO = "span[title='Tag'] + form + span.item";
	private static final String ENTERPRISE_REMOVE_TAG = "img[src$='delete-tag-off.png']";
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getName()
     */
    @Override
    public String getName()
    {
        String title = "";
        try
        {
            title = findAndWait(By.cssSelector(FILENAME_IDENTIFIER)).getText();

        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getName();
        }
        return title;
    }

    public void setNodeRef(String nodeRef)
    {
        this.nodeRef = nodeRef;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTitle()
     */
    @Override
    public HtmlPage clickOnTitle()
    {
        try
        {
            WebElement element = findAndWait(By.cssSelector(FILENAME_IDENTIFIER));
            element.click();
            domEventCompleted();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No such element", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find FILENAME_IDENTIFIER", te);
        }

        throw new PageException("Unable to click on content Title.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isTypeFolder()
     */
    @Override
    public boolean isTypeFolder()
    {
        boolean isFolder = false;
        try
        {
            WebElement img = findElement(By.tagName("img"));
            String path = img.getAttribute("src");

            if (path != null && path.matches(IMG_FOLDER))
            {
                isFolder = true;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return isFolder;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        try
        {
            return findAndWait(By.cssSelector(FILE_DESC_IDENTIFIER)).getText();
        }
        catch (TimeoutException te)
        {
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentEditInfo()
     */
    @Override
    public String getContentEditInfo()
    {
        return findAndWait(By.cssSelector("h3.filename+div.detail>span")).getText();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector(".category>a"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(Categories.getCategory(webElement.getText()));
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<String> getCategoryList()
    {
        List<String> categories = new ArrayList<>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector(".category>a"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            WebElement deleteLink = findAndWait(By.cssSelector("div[class$='delete'] a"));
            deleteLink.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the css ", e);
        }
        catch (StaleElementReferenceException st)
        {
            throw new StaleElementReferenceException("Unable to find the css ", st);
        }
        
        return factoryPage.instantiatePage(driver, ConfirmDeletePage.class);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPage selectEditProperties()
    {
        WebElement editProperties = findAndWait(EDIT_PROP_ICON);
        String javaScript = "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                "arguments[0].dispatchEvent(evObj);";
        executeJavaScript(javaScript, editProperties);
        editProperties.click();
        return factoryPage.instantiatePage(driver,EditDocumentPropertiesPage.class).render();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewInBrowser()
     */
    @Override
    public void selectViewInBrowser()
    {
        WebElement viewInBrowser = findElement(VIEW_IN_BROWsER_ICON);
        viewInBrowser.click();

        Set<String> winSet = driver.getWindowHandles();
        List<String> winList = new ArrayList<String>(winSet);
        String newTab = winList.get(winList.size() - 1);
        // // close the original tab

        // DONOT CLOSE THE WINDOW

        //closeWindow();
        // switch to new tab
       driver.switchTo().window(newTab);
    }

    /**
     * Selects the 'Actions' menu link on the select data row on DocumentLibrary
     * Page.
     * 
     * @return {@link WebElement} WebElement that allows access to Actions menu for the selected Content
     */
    public WebElement selectContentActions()
    {
        return findElement(By.cssSelector(ACTIONS_MENU));
    }

    /**
     * Selects the 'Actions' menu link on the select data row on DocumentLibrary Page.
     * 
     * @return List of {@link WebElement} available for the selected Content
     */
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentActions()
     */
    @Override
    public List<String> getContentActions()
    {
        List<String> actions = new ArrayList<>();

        try
        {
            mouseOver(selectContentActions());           
            List<WebElement> actionElements = findElements(By.cssSelector(ACTIONS_LIST));
            for (WebElement webElement : actionElements)
            {
                actions.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new NoSuchElementException("Not able to find actions", e);
        }
        return actions;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
    public void selectFavourite()
    {
        try
        {
            findElement(By.cssSelector(FAVOURITE_CONTENT)).click();
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            selectFavourite();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        findElement(By.cssSelector(LIKE_CONTENT)).click();
        domEventCompleted();
    }

    /**
     * Gets the Like option tool tip on the select data row on
     * DocumentLibrary Page.
     */
    @Override
    public String getLikeOrUnlikeTip()
    {
        return findElement(By.cssSelector(LIKE_CONTENT)).getAttribute("title");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        try
        {
            WebElement likeContent = findElement(By.cssSelector(LIKE_CONTENT));
            String status = likeContent.getAttribute("class");
            if (status != null)
            {
                boolean liked = status.contains("like-action enabled");
                return liked;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isLiked();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
    public boolean isFavourite()
    {
        try
        {
            WebElement favouriteContent = findElement(By.cssSelector(FAVOURITE_CONTENT));
            String status = favouriteContent.getAttribute("class");
            if (status != null)
            {
                return status.contains("favourite-action enabled");
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isFavourite();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
    public String getLikeCount()
    {
        try
        {
            return findElement(By.cssSelector(LIKE_COUNT)).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getLikeCount();
        }
    }


    /**
     * Get NodeRef for the content on the selected data row on DocumentLibrary
     * Page.
     * 
     * @return {String} Node Ref / GUID
     */
    @Override
    public String getContentNodeRef()
    {
        if(nodeRef == null || nodeRef.isEmpty())
        {
            try
            {
                WebElement nodeRefElement = findElement(By.cssSelector("input[type='checked']"));
                nodeRef = nodeRefElement.getAttribute("value");
            }
            catch (NoSuchElementException nse)
            {
                throw new PageOperationException("Unable to find content node ref value", nse);
            }
            throw new PageOperationException("The node ref value was invalid");
        }
        return nodeRef;
    }

    @Override
    public String toString()
    {
        return "FileDirectoryInfo [getName()=" + getName() + "]";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTitle()
     */
    @Override
    public String getTitle()
    {
        try
        {
            return findAndWait(By.cssSelector(TITLE)).getText();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageOperationException("Unable to find content row title");
    }


    public void clickOnAddTag()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime * 2);
        while (true)
        {
            try
            {
                timer.start();
                WebElement tagInfo = findAndWait(By.cssSelector(TAG_INFO));
                //getDrone().mouseOver(tagInfo);
                String javaScript = "var evObj = document.createEvent('MouseEvents');" +
                        "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                        "arguments[0].dispatchEvent(evObj);";
                executeJavaScript(javaScript, tagInfo);
                By addTagButton = By.xpath(String.format("//h3/span/a[text()='%s']/../../../div/span[@title='Tag']", getName()));
                waitUntilElementClickable(addTagButton, SECONDS.convert(3000, MILLISECONDS));
                executeJavaScript("arguments[0].click();", findAndWait(addTagButton));
                if (findElement(By.cssSelector(INPUT_TAG_NAME)).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
                logger.error("Unable to find the add tag icon", e);
            }
            catch (TimeoutException te)
            {
                logger.error("Exceeded time to find the tag info area ", te);
            }
            catch (StaleElementReferenceException stale)
            {
            }
            finally
            {
                timer.end();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#removeTagButtonIsDisplayed(java.lang.String)
     */
    public boolean removeTagButtonIsDisplayed(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("tagName is required.");
        }
        try
        {
            return getRemoveTagButton(tagName).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagRemoveButton(java.lang.String)
     */
    public void clickOnTagRemoveButton(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("tagName is required.");
        }

        try
        {
            getRemoveTagButton(tagName).click();
        }
        catch (Exception e)
        {
            throw new PageException("Unable to find the remove tag button.", e);
        }
    }

    /**
     * This method finds the remove button on tag element and returns button
     * 
     * @param tagName String
     * @return WebElement
     */
    private WebElement getRemoveTagButton(String tagName)
    {
        for (WebElement tag : getInlineTagList())
        {
            String text = tag.getText();
            if (text != null && text.equalsIgnoreCase(tagName))
            {
                try
                {
                    return tag.findElement(By.cssSelector(ENTERPRISE_REMOVE_TAG));
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Unable to find the remove tag button.", e);
                }
            }
        }
        throw new PageException("Unable to find the remove tag button.");
    }
    /**
     * This method gets the list of in line tags after clicking on tag info icon.
     * 
     * @return List<WebElement> collection of tags
     */
    private List<WebElement> getInlineTagList()
    {
        try
        {
            return findAllWithWait(By.cssSelector(INLINE_TAGS));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.");
        }

    }
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagSaveButton()
     */
    public void clickOnTagSaveButton()
    {
        try
        {
            findAndWait(By.xpath("//form[@class='insitu-edit']/a[text()='Save']")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css.", ex);
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }

    public void clickOnTagCancelButton()
    {
        try
        {
            findAndWait(By.xpath("//form[@class='insitu-edit']/a[text()='Cancel']")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css.", ex);
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCheckbox()
     */
    @Override
    public void selectCheckbox()
    {
        findAndWait(By.cssSelector(SELECT_CHECKBOX)).click();
        domEventCompleted();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCheckboxSelected()
     */
    @Override
    public boolean isCheckboxSelected()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isSelected();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        try
        {
            findElement(By.cssSelector(THUMBNAIL)).click();
            return getCurrentPage();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find and click thumbnail icon ", e);
        }

        throw new PageOperationException("Unable to click find and click on Thumbnail icon");
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     * <br/><br/>author hamara
     */
    @Override
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(THUMBNAIL_TYPE));
            return (thumbnailType.getAttribute("class").contains("folder") && (!isLinkToFolder()) );
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * link to a folder.
     *
     * @return {boolean} <tt>true</tt> if the content is of type link to folder.
     */
    @Override
    public boolean isLinkToFolder()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(THUMBNAIL_LINK_FOLDER));
            return (thumbnailType.isDisplayed());
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * link to file.
     *
     * @return {boolean} <tt>true</tt> if the content is of type link to file.
     */
    @Override
    public boolean isLinkToFile()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(THUMBNAIL_LINK_FILE));
            return (thumbnailType.isDisplayed());
        }
        catch (Exception e)
        {
        }
        return false;
    }


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isPartOfWorkflow()
     */
    @Override
    public boolean isPartOfWorkflow()
    {
        try
        {
            WebElement thumbnailType =findElement(By.cssSelector(WORKFLOW_ICON));
            return thumbnailType.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }

    }

    /**
     * Clicks on the download folder as a zip button from the action menu
     */
    public void downloadFolderAsZip()
    {
        if (!isFolder())
        {
            throw new UnsupportedOperationException("Download folder as zip is available for folders only.");
        }
        try
        {
            WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_FOLDER));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to click download folder as a zip", nse);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownload()
     */
    @Override
    public void selectDownload()
    {
        WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_DOCUMENT));
        menuOption.click();
        // Assumes driver capability settings to save file in a specific location when
        // <Download> option is selected via Browser
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getNodeRef()
     */
    @Override
    public String getNodeRef()
    {
        if(nodeRef == null || nodeRef.isEmpty())
        {
            try
            {
                nodeRef = super.findAndWait(By.cssSelector(SELECT_CHECKBOX)).getAttribute("value");
            }
            catch (StaleElementReferenceException e)
            {
                throw new PageException("Unable to obtain nodeRef id required for FileDirectoryInfo", e);
            }
        }
        return nodeRef;
    }

    /**
     * Refresh web element mechanism.
     * As the page changes every id on every action or event
     * that takes place on the page, we refresh the web element
     * we were working with by re-finding it on the page
     * and updating the page object.
     */
    protected void resolveStaleness()
    {
        if (nodeRef == null || nodeRef.isEmpty())
        {
            throw new UnsupportedOperationException(String.format("Content noderef is required: %s", nodeRef));
        }

        WebElement element = driver.findElement(By.cssSelector(String.format("input[value='%s']", nodeRef)));
        WebElement parent = element.findElement(By.xpath("../../.."));
        setWrappedElement(parent);
    }

    /**
     * Performs the find and wait given amount of time
     * with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * 
     * @param cssSelector By
     * @return {@link WebElement}
     */
    @Override
    public WebElement findAndWait(By cssSelector)
    {
        try
        {
            return super.findAndWaitInNestedElement(cssSelector,getDefaultWaitTime());
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findAndWait(cssSelector);
        }
    }

    /*
     * @see org.alfresco.po.HtmlElement#findElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findElement(By cssSelector)
    {
        try
        {
            return findAndWaitInNestedElement(cssSelector, getDefaultWaitTime());
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findElement(cssSelector);
        }
    }

    /**
     * Performs the find with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * 
     * @param cssSelector By
     * @return colelction {@link WebElement}
     */
    public List<WebElement> findAllWithWait(By cssSelector)
    {
        try
        {
            return driver.findElements(cssSelector);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findAllWithWait(cssSelector);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        WebElement menuOption = findAndWait(By.cssSelector("div.folder-view-details>a"));
        menuOption.click();

        return getCurrentPage().render();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSignUpDialogVisible()
     */
    @Override
    public boolean isSignUpDialogVisible()
    {
        RenderTime time = new RenderTime(getDefaultWaitTime());

        time.start();

        try
        {
            while (true)
            {
                try
                {
                    return !driver.findElement(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
                }
                catch (NoSuchElementException e)
                {
                    try
                    {
                        return driver.findElement(By.cssSelector("form.cloud-auth-form")).isDisplayed();
                    }
                    catch (NoSuchElementException nse)
                    {
                        time.end();
                        continue;
                    }
                }
            }
        }
        catch (PageRenderTimeException prte)
        {

        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        WebElement inLineEdit = findAndWait(INLINE_EDIT_LINK);
        inLineEdit.click();
        return factoryPage.instantiatePage(driver, InlineEditPage.class);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentInfo()
     */
    @Override
    public String getContentInfo()
    {
        try
        {
            return findAndWait(INFO_BANNER).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Info banner.", e);
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocked()
     */
    @Override
    public boolean isLocked()
    {
        try
        {
            return findElement(LOCK_ICON).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Lock icon is not displayed", te);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        try
        {
            findElement(MORE_ACTIONS_MENU);
            return driver.findElement(INLINE_EDIT_LINK).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Inline Edit link is not displayed", te);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        try
        {
            findElement(MORE_ACTIONS_MENU);
            return driver.findElement(EDIT_OFFLINE_LINK).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Edit Offline link is not displayed", te);
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        try
        {
            WebElement deleteLink = findElement(By.cssSelector("div[class$='delete'] a"));
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocateLinkedItemDisplayed()
     */
    @Override
    public boolean isLocateLinkedItemDisplayed()
    {
        try
        {
            mouseOver(selectContentActions());
            
            WebElement action = findElement(By.cssSelector(LOCATE_LINKED_ITEM));
            return action.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.trace("Locate Linked Item is not displayed", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeleteLinkDisplayed()
     */
    @Override
    public boolean isDeleteLinkDisplayed() 
    {
        try 
        {
            mouseOver(selectContentActions());
            
            WebElement action = findElement(By.cssSelector(DELETE_LINK));
            return action.isDisplayed();
        } 
        catch (NoSuchElementException e) 
        {
            logger.trace("Delete Link is not displayed", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDeleteLink()
     */
    @Override
    public HtmlPage selectDeleteLink()
    {
        try
        {
            if (isDeleteLinkDisplayed())
            {
                WebElement deleteLink = findAndWait(By.cssSelector(DELETE_LINK));
                deleteLink.click();
            }
            else 
            {
                throw new NoSuchElementException("Delete Link not visible");
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the css ", e);
        }
        catch (StaleElementReferenceException st)
        {
            throw new StaleElementReferenceException("Unable to find the css ", st);
        }

        return factoryPage.getPage(driver);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#deleteLink()
     */
    @Override
    public HtmlPage deleteLink()
    {
        SharePopup confirmDelete = selectDeleteLink().render();
        return confirmDelete.clickActionByName(Action.Delete.toString());
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLocateLinkedItem()
     */
    @Override
    public HtmlPage selectLocateLinkedItem()
    {
        try 
        {
            if (isLocateLinkedItemDisplayed())
            {
                WebElement locateLinkedItem = findElement(By.cssSelector(LOCATE_LINKED_ITEM));
                locateLinkedItem.click();
                return getCurrentPage().render();
            }
            throw new NoSuchElementException("Locate Linked Item not visible");
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Locate Linked Item ", te);
            throw new PageException("Unable to find Locate Linked Item ", te);
        }
    }

    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        try
        {
            findAndWait(By.cssSelector("div.folder-manage-rules > a")).click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Manage Rules link is not displayed for selected data row", te);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isRuleIconPresent(long)
     */
    @Override
    public boolean isRuleIconPresent(long waitTime)
    {
        try
        {
            return findAndWait(By.cssSelector(RULES_ICON), waitTime).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Rule icon is not displayed", e);
            }
        }
        return false;
    }


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        try
        {
            WebElement managePermissionLink = findAndWait(By.cssSelector(LINK_MANAGE_PERMISSION));
            managePermissionLink.click();
            return getCurrentPage().render();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row", exception);
            }
        }
        throw new PageOperationException("Manage permission link is not displayed for selected data row");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCopyTo()
     */
    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        return selectCopyOrMoveTo("Copy to...");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
        return selectCopyOrMoveTo("Move to...");
    }

    private CopyOrMoveContentPage selectCopyOrMoveTo(String linkText)
    {
        try
        {
            WebElement copyToLink = findAndWait(By.linkText(linkText));
            copyToLink.click();
            return getCurrentPage().render();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error(linkText + " link is not displayed for selected data row", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCopyOrMoveTo(linkText);
        }
        throw new PageOperationException(linkText + " link is not displayed for selected data row");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#delete()
     */
    @Override
    public HtmlPage delete()
    {
        return selectDelete().selectAction(Action.Delete);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        try
        {
            // css selector changed to suite MyAlfresco + fix localisation issues due to linkText
            WebElement startWorkFlow = findElement(CREATE_TASK_WORKFLOW);
            startWorkFlow.click();
            //check we left the page
            driver.findElements(CREATE_TASK_WORKFLOW);
            return getCurrentPage().render();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectStartWorkFlow();
        }
        throw new PageException("Unable to find start workflow.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUploadNewVersion()
     */

    @Override
    public UpdateFilePage selectUploadNewVersion()
    {
        try
        {
            WebElement uploadNewVersionLink = findElement(By.cssSelector("div[class$='document-upload-new-version'] a"));
            uploadNewVersionLink.click();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }

        // TODO add version
        return factoryPage.instantiatePage(driver, UpdateFilePage.class).render();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(LINK_MANAGE_PERMISSION)).isDisplayed();

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector("div.document-edit-properties>a")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Edit properties link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(getValue("edit.offline.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("edited");
            return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectEditOffline();
        }
        catch (Exception e)
        {
            throw new PageException("Robot not working");
        }
        throw new PageException("Unable to find Edit Offline link");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(getValue("cancel.editing.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("cancelled.");
            return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCancelEditing();
        }
        throw new PageException("Unable to find Cancel Editing link");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        try
        {
            return driver.findElement(By.cssSelector(EDITED_ICON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        try
        {
            WebElement manageAspectLink = findElement(By.cssSelector("div[class$='document-manage-aspects'] a"));
            manageAspectLink.click();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectManageAspects();
        }
        return factoryPage.instantiatePage(driver, SelectAspectsPage.class);
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     */
    protected void waitUntilMessageAppearAndDisappear(String text)
    {
        long defaultWaitTime = getDefaultWaitTime();
        waitUntilMessageAppearAndDisappear(text, SECONDS.convert(defaultWaitTime, MILLISECONDS));
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        waitUntilVisible(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
        waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isCommentLinkPresent()
     */
    @Override
    public boolean isCommentLinkPresent()
    {
        try
        {
            WebElement commentLink = findElement(COMMENT_LINK);
            return commentLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /**
     * Check if quick share link is present.
     * 
     * @return boolean
     */
    @Override
    public boolean isShareLinkVisible()
    {
        try
        {
            WebElement shareLink = findElement(QUICK_SHARE_LINK);

            return shareLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /**
     * Check if Like is present.
     *
     * @return boolean
     */
    @Override
    public boolean isLikeVisible()
    {
        try
        {
            WebElement likeLink = findElement(By.cssSelector(LIKE_CONTENT));

            return likeLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /**
     * Check if Favorite is present.
     *
     * @return boolean
     */
    @Override
    public boolean isFavoriteVisible()
    {
        try
        {
            WebElement favLink = findElement(By.cssSelector(FAVOURITE_CONTENT));

            return favLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    @Override
    public boolean isViewInBrowserVisible()
    {
        try
        {
            WebElement icon =findElement(VIEW_IN_BROWsER_ICON);

            return icon.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    @Override
    public void contentNameEnableEdit()
    {
        WebElement contentNameLink = findAndWait(By.cssSelector(FILENAME_IDENTIFIER));
        //getDrone().mouseOver(contentNameLink);

        String javaScript = "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                "arguments[0].dispatchEvent(evObj);";
        executeJavaScript(javaScript, contentNameLink);

        resolveStaleness();
        // Wait till pencil icon appears
        WebElement editIcon = findElement(By.cssSelector(EDIT_CONTENT_NAME_ICON));
        // Select to get focus
        editIcon.click();
    }

    @Override
    public void contentNameEnter(String newContentName)
    {
        try
        {
            WebElement inputBox = findElement(By.cssSelector(INPUT_CONTENT_NAME));
            if (inputBox.isDisplayed())
            {
                WebElement inputCOntentName = findAndWait(By.cssSelector(INPUT_CONTENT_NAME));
                inputCOntentName.clear();
                inputCOntentName.sendKeys(newContentName);
                return;
            }
            else
            {
                throw new PageException("Input is not displayed displayed");
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Input should be displayed", e);
            throw new PageOperationException("Input should be displayed");
        }

    }

    @Override
    public void contentNameClickSave()
    {
        clickLinkText(By.cssSelector(INPUT_CONTENT_NAME), "Save");
    }

    private void clickLinkText(By by, String linkText)
    {
        String expectionMessage = "";
        try
        {
            WebElement inputBox = findElement(by);
            if (inputBox.isDisplayed())
            {
                driver.findElement(By.linkText(linkText)).click();
                //Check form has disappeared.
                driver.findElements(by);
                return;
            }
            else
            {
                throw new PageOperationException("Input is not displayed displayed");
            }
        }
        catch (TimeoutException ex)
        {
            expectionMessage = "Exceeded time to find the " + linkText + " button css." + ex;
            logger.error(expectionMessage);
        }
        catch (NoSuchElementException ex)
        {
            expectionMessage = "Not able to find the input css." + ex;
            logger.error(expectionMessage);
        }
        throw new PageOperationException("Exceeded time to find the " + linkText + " button css." + expectionMessage);
    }

    @Override
    public void contentNameClickCancel()
    {
        clickLinkText(By.cssSelector(INPUT_CONTENT_NAME), "Cancel");
    }

    @Override
    public void renameContent(String newContentName)
    {
        if (StringUtils.isEmpty(newContentName))
        {
            throw new IllegalArgumentException("Content name is required");
        }
        try
        {
            contentNameEnableEdit();
            contentNameEnter(newContentName);
            contentNameClickSave();
        }
        catch (TimeoutException e)
        {
            logger.error("Error renaming content: ", e);
            throw new PageException("Error While renaming content: " + newContentName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModified()
    {
        throw new UnsupportedOperationException("Modified is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModifier()
    {
        try
        {
            if (!hasCreator())
            {
                return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span/*[2]")).getText();
            }
            else
            {
                throw new PageOperationException("Content just created.");
            }
        }
        catch (TimeoutException e)
        {
            throw new UnsupportedOperationException("Modifier is not available in current view. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreated()
    {
        throw new UnsupportedOperationException("Created is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlPage selectCreator()
    {
        throw new UnsupportedOperationException("Creator is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreator()
    {
        try
        {
            if (hasCreator())
            {
                return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span/*[2]")).getText();
            }
            else
            {
                throw new PageOperationException("Content modified.");
            }
        }
        catch (TimeoutException e)
        {
            throw new UnsupportedOperationException("Creator is not available in current view.");
        }
    }

    private boolean hasCreator()
    {
        return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span")).getText().contains("Created ");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Share Link is not Supported for the Folder");
        }
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getFileOrFolderHeight()
     */
    public double getFileOrFolderHeight()
    {
        throw new UnsupportedOperationException("File or Folder Height is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoPopUpDisplayed()
     */
    @Override
    public boolean isInfoPopUpDisplayed()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickInfoIcon()
     */
    @Override
    public void clickInfoIcon()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoIconVisible()
     */
    @Override
    public boolean isInfoIconVisible()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    @Override
    public String getContentNameFromInfoMenu()
    {
        throw new UnsupportedOperationException("Info menu is not available in this view type.");
    }

    @Override
    public String getVersionInfo()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Only available for file.");
        }
        String version = "";
        try
        {
            version = findAndWait(By.xpath(".//span[@class='document-version']")).getText();
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getVersionInfo();
        }
        return version;
    }

    @Override
    public boolean isCheckBoxVisible()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    @Override
    public boolean isVersionVisible()
    {
        try
        {
            return driver.findElement(FILE_VERSION_IDENTIFIER).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagNameLink(java.lang.String)
     */
    @Override
    public HtmlPage clickOnCategoryNameLink(String categoryName)
    {
        if (categoryName == null)
        {
            throw new UnsupportedOperationException("Drone and category Name is required.");
        }

        try
        {
            List<WebElement> categoryList = findAllWithWait(CATEGORY_LINK);
            if (categoryList != null)
            {
                for (WebElement tag : categoryList)
                {
                    String tagText = tag.getText();
                    if (categoryName.equalsIgnoreCase(tagText))
                    {
                        tag.click();
                        driver.navigate().refresh();
                        return getCurrentPage();
                    }
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css. ACE-3037");
        }
        throw new PageException("Not able to category name: " + categoryName);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickCommentsLink()
     */
    @Override
    public HtmlPage clickCommentsLink()
    {
        try
        {
            findAndWait(COMMENT_LINK).click();

            return getCurrentPage();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments element", ex);
        }

        throw new PageException("Unable to find the comments Link.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsToolTip()
     */
    @Override
    public String getCommentsToolTip()
    {
        try
        {
            return findAndWait(COMMENT_LINK).getAttribute("title");
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        throw new PageException("Unable to find the comments tooltip.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsCount()
     */
    @Override
    public int getCommentsCount()
    {
        int cnt = 0;
        try
        {
            String count = findAndWait(By.cssSelector("span.comment-count")).getText();
            cnt = Integer.parseInt(count);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("Unable to convert comments count string value into int", nfe);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        return cnt;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickContentNameFromInfoMenu()
     */
    @Override
    public HtmlPage clickContentNameFromInfoMenu()
    {
        throw new UnsupportedOperationException("Info menu is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectModifier()
     */
    @Override
    public HtmlPage selectModifier()
    {
        try
        {
            WebElement creatorLink = findAndWait(By.cssSelector("a[href$='profile']"));
            creatorLink.click();

            return getCurrentPage();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        throw new PageOperationException("Error in finding and clicking on modifier link.");
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isNodeRefColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("NodeRef column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isStatusColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Status column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isThumbnailColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Thumbnail column is not available in current view.");
    // }
    //
    // @Override
    // public boolean isNameColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Name column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isTitleColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Title column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isDescriptionColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Description column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isCreatorColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Creator column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isCreatedColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Created column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isModifierColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Modifier column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isModifiedColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Modified column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isActionsColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Actions column is not available in current view.");
    // }

    /**
     * (non-Javadoc)
     * 
     * @see FileDirectoryInfo#getPreViewUrl()
     */
    @Override
    public String getPreViewUrl()
    {
        try
        {
            return findAndWait(By.cssSelector(THUMBNAIL + ">img")).getAttribute("src");
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the preView img", ex);
        }
        throw new PageOperationException("Error getting URL to preview image");
    }

    /**
     * Gets the Like option tool tip on the select data row on
     * DocumentLibrary Page.
     */
    @Override
    public String getFavouriteOrUnFavouriteTip()
    {
        return findElement(By.cssSelector(FAVOURITE_CONTENT)).getAttribute("title");
    }

    /**
     * Check if the file is shared.
     * 
     * @return boolean
     */
    @Override
    public boolean isFileShared()
    {
        try
        {
            WebElement shareLink = findElement(QUICK_SHARE_LINK);

            String elClass = shareLink.getAttribute("class");
            return elClass.contains("enabled");
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /**
     * Check if the save link is visible.
     * 
     * @return boolean
     */
    @Override
    public boolean isSaveLinkVisible()
    {
        return isLinkVisible("Save");
    }

    /**
     * Check if the save link is visible.
     * 
     * @return boolean
     */
    @Override
    public boolean isCancelLinkVisible()
    {
        return isLinkVisible("Cancel");
    }

    /**
     * Check if the link is visible.
     * 
     * @return boolean
     */
    private boolean isLinkVisible(String linkText)
    {
        try
        {
            return driver.findElement(By.linkText(linkText)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean isViewOriginalLinkPresent()
    {
        try
        {
            return findAndWait(By.cssSelector(VIEW_ORIGINAL_DOCUMENT)).isDisplayed();
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

    @Override
    public HtmlPage selectViewOriginalDocument()
    {
        try
        {
            WebElement link = findAndWait(By.cssSelector(VIEW_ORIGINAL_DOCUMENT));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to select View Original Document ", e);
        }

        return getCurrentPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getThumbnailURL()
    {
        throw new UnsupportedOperationException("Not implemented in current view.");
    }

    @Override
    public boolean isDownloadPresent()
    {
        try
        {
            return driver.findElement(By.cssSelector(DOWNLOAD_DOCUMENT)).isDisplayed();

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    @Override
    public boolean isMoreMenuButtonPresent()
    {
        try
        {
            WebElement moreMenu = driver.findElement(MORE_ACTIONS_MENU); 
            return moreMenu.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("More+ menu is not displayed", te);
            }
        }
        return false;
    }

    @Override
    public boolean isTagsFieldPresent()
    {
        try
        {
            return driver.findElement(TAGS_FIELD).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Tags field is not displayed", te);
            }
        }
        return false;
    }

    @Override
    public List<String> getDescriptionList()
    {
        List<String> descriptionsList = new ArrayList<String>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector("div.detail span.item"));
            for (WebElement webElement : categoryElements)
            {
                descriptionsList.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find description", e);
        }
        return descriptionsList;
    }

    @Override
    public String getDescriptionFromInfo()
    {
        try
        {
            return findAndWait(By.cssSelector(DESCRIPTION_INFO)).getText();
        }
        catch (TimeoutException te)
        {
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLocateFile()
     */
    @Override
    public void selectLocateFile()
    {
        try
        {
            WebElement menuOption = findElement(By.cssSelector(LOCATE_FILE));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not able to find Locate file", nse);
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOfflineAndCloseFileWindow()
     */
    @Override
    public DocumentLibraryPage selectEditOfflineAndCloseFileWindow()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(getValue("edit.offline.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("edited");


            return factoryPage.getPage(driver).render();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectEditOfflineAndCloseFileWindow();
        }
        catch (Exception e)
        {
            throw new PageException("Robot not working");
        }
        throw new PageException("Unable to find Edit Offline link");
    }

    public void declareRecord()
    {

        try
        {
            WebElement declare_record = findAndWait(By.cssSelector(DECLARE_AS_RECORD));
            declare_record.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to find element");
        }

    }

    /**
     * Verifies whether type is record.
     * 
     * <br/><br/>author hamara
     */
    public boolean isTypeRecord()
    {
        boolean isTypeRecord = false;

        try
        {
            WebElement rec = findElement(By.cssSelector(IN_COMPLETE_RECORD));
            String rec_text = rec.getText();
            if (rec_text != null && rec_text.contains("Incomplete Record"))
            {
                isTypeRecord = true;
            }
        }
        catch (Exception e)
        {
            logger.info("Error checking Record: ", e);
        }

        return isTypeRecord;

    }

    /**
     * Verifies whether type is record.
     * 
     * <br/><br/>author hamara
     */
    public boolean isFolderType()
    {
        boolean isTypeFolder = false;

        try
        {
            WebElement rec = findElement(By.cssSelector(IS_FOLDER));
            String rec_text = rec.getAttribute("src");
            if (rec_text != null && rec_text.contains("png"))
            {
                isTypeFolder = true;
            }
        }
        catch (Exception e)
        {
            logger.info("Error checking if it's a folder: ", e);
        }
        return isTypeFolder;
    }

    /*
     * Clicks on Preview Web Asset from the action menu
     */
    @Override
    public void selectPreviewWebAsset()
    {
        WebElement menuOption = findElement(By.cssSelector(DOCUMENT_WEB_ASSET));
        menuOption.click();
    }


    /**
     * Method Returns true if Model Info is presented for the selected model
     */
    @Override
    public boolean isModelInfoPresent()
    {
        try
        {
            List<WebElement> modelInfo = getModelInfoElements();
            if (modelInfo.isEmpty())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Model Info is not displayed", te);
            }
        }
        return false;
    }

    private List<WebElement> getModelInfoElements()
    {
        List<WebElement> allSpans = new ArrayList<WebElement>();
        List<WebElement> modelInfoSpans = new ArrayList<WebElement>();

        try
        {
            allSpans = findElements(MODELINFO_FIELD);

            for (WebElement info : allSpans)
            {
                // Get elements where model Info is displayed
                if (info.isDisplayed())
                {
                    List<WebElement> modelInfoEms = info.findElements(By.tagName("em"));
                    if (!modelInfoEms.isEmpty())
                    {
                        // Span with modelInfo: add to the list
                        modelInfoSpans.add(info);
                    }
                }
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Model Info is not displayed", te);
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Model Info is not displayed", nse);
            }
        }
        return modelInfoSpans;
    }

    public enum ModelInfo
    {
        ISACTIVE, MODELNAME, MODELDESC;
    }

    /**
     * Returns the text value of the parent tag (span) for the selected child tag (em)
     * 
     * @param infoRequired: ISACTIVE, MODELNAME, MODELDESC
     * @return String text value
     */
    private String getModelInfo(ModelInfo infoRequired)
    {
        PageUtils.checkMandatoryParam("Specify Which Model Info is Required", infoRequired);

        if (!(getCurrentPage() instanceof ModelsPage))
        {
            throw new UnsupportedOperationException("Model Info is not displayed");
        }

        String infoReq = getValue("model.is.active");

        if (infoRequired.equals(ModelInfo.MODELNAME))
        {
            infoReq = getValue("model.name");
        }
        else if (infoRequired.equals(ModelInfo.MODELDESC))
        {
            infoReq = getValue("model.description");
        }

        infoReq = infoReq + ": ";

        try
        {
            List<WebElement> modelInfo = getModelInfoElements();

            for (WebElement model : modelInfo)
            {
                String val = model.getText();
                if (val.startsWith(infoReq))
                {
                    return val.replace(infoReq, "");
                }
            }
        }
        catch (Exception te)
        {
            throw new PageOperationException("Model Info is not displayed", te);
        }
        throw new PageOperationException("Model Info is not displayed");
    }

    /**
     * Method Returns true if the selected DataDictionary>model is Active
     */
    public boolean isModelActive()
    {
        try
        {
            if ("true".equalsIgnoreCase(getModelInfo(ModelInfo.ISACTIVE)))
            {
                return true;
            }
        }
        catch (UnsupportedOperationException ue)
        {
            throw new UnsupportedOperationException("This operation is only supported for ModelsPage", ue);
        }
        catch (Exception e)
        {
            logger.info("Exception", e);
            if (logger.isTraceEnabled())
            {
                logger.trace("Model Info is not displayed", e);
            }
        }
        return false;
    }

    /**
     * Method Returns the DataDictionary>model Name for the selected model
     */
    public String getModelName()
    {
        try
        {
            return getModelInfo(ModelInfo.MODELNAME);
        }
        catch (UnsupportedOperationException ue)
        {
            throw new UnsupportedOperationException("This operation is only supported for ModelsPage", ue);
        }
        catch (Exception e)
        {
            throw new PageOperationException("Model Name Can not be found", e);
        }
    }

    /**
     * Method Returns the DataDictionary>model description for the selected model
     */
    public String getModelDesription()
    {
        try
        {
            return getModelInfo(ModelInfo.MODELDESC);
        }
        catch (UnsupportedOperationException ue)
        {
            throw new UnsupportedOperationException("This operation is only supported for ModelsPage", ue);
        }
        catch (Exception e)
        {
            throw new PageOperationException("Model Description Can not be found", e);
        }
    }

    /**
     * Method to select Copy To... button for folder on cloud
     */
    public CopyOrMoveContentPage selectCopyToOnFolderCloud()
    {
        try
        {
            WebElement copyToLink = findAndWait(By.cssSelector("div.document-copy-to>a"));
            copyToLink.click();
            return getCurrentPage().render();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Copy to link is not displayed for selected data row", exception);
        }      
        throw new PageOperationException("Copy to link is not displayed for selected data row");
    }
}
